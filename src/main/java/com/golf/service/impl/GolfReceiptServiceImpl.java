package com.golf.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.service.model.GolfReceiptPayVO;
import com.golf.service.model.StatusPayVO;
import com.golf.service.model.StatusVO;
import com.google.gson.Gson;

import com.golf.service.model.GolfHistoryVO;

/**
 * 골프택배 접수 서비스
 * @author inferno
 *
 */
@Service("ReceiptService")
public class GolfReceiptServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(GolfReceiptServiceImpl.class);

	@Autowired
	private GolfApi golfApi;

	@Autowired
	private RentalServiceImpl rentalServiceImpl;

	/**
	 * 골프택배 접수
	 * @param mstVO
	 * @param list
	 * @throws Exception
	 */
	public void insertReceipt(GolfReceiptMstVO mstVO, List<GolfReceiptInfVO> list) throws Exception {

		// 1. 접수 마스터 등록
		getDAO().insert("golfReceipt.insertReceiptMst", mstVO);

		if(list != null){
			String rcpKind = list.get(0).getRcpKind();

			for(GolfReceiptInfVO receiptInfVO : list){

				// [보정] 짐캐리가 아닐 때만 기존의 할인 로직 수행
				if (!"zimcarry_111".equals(mstVO.getPartner())) {
					// 중복 if문 하나로 정리
					if (("0".equals(rcpKind) || "5".equals(rcpKind)) && "1".equals(mstVO.getFareType())) {
						if (receiptInfVO.getBostonCnt() > 0 && receiptInfVO.getPdCnt() > 0) {
							int bostonDiscount = 3000;
							int discount = bostonDiscount * receiptInfVO.getBostonCnt();
							int pdPay = receiptInfVO.getPdPay();
							int iconPay = receiptInfVO.getIconFee();

							if(receiptInfVO.getBostonCnt() > receiptInfVO.getPdCnt()) {
								discount = bostonDiscount * receiptInfVO.getPdCnt();
							}

							if ("1".equals(mstVO.getDelKind())) { // 편도
								iconPay = iconPay - discount;
								receiptInfVO.setDiscount(pdPay - discount);
							} else if ("2".equals(mstVO.getDelKind())) { // 왕복
								receiptInfVO.setDiscount((pdPay * 2) - discount);
								discount = discount / 2;
								iconPay = iconPay - discount;
							}
							receiptInfVO.setIconFee(iconPay < 0 ? 0 : iconPay);
							receiptInfVO.setPdPay(receiptInfVO.getPdPay() - discount);
						}
					}
				}

				// 2. 물품 상세 정보 등록
				getDAO().insert("golfReceipt.insertReceiptInf", receiptInfVO);

				// 3. 결제 및 히스토리 정보 세팅
				GolfReceiptPayVO payVO = new GolfReceiptPayVO();
				payVO.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
				payVO.setPayAmt(receiptInfVO.getPdPay());

				// 짐캐리 제휴사는 바로 결제완료(1) 처리, 나머지는 결제대기(0)
				if ("zimcarry_111".equals(mstVO.getPartner())) {
					payVO.setPayStat("1");  // 결제완료
					payVO.setPayGubun("0"); // 일반결제(아이콘)
				} else {
					payVO.setPayStat("0");  // 결제대기
					payVO.setPayGubun("0");
				}

				String historyMemo = "아이콘 택배접수 편도";
				String orderId = receiptInfVO.getRcpNumber(); // 기본값: 접수번호

				// [분기 처리] 짐캐리 vs 일반
				if ("zimcarry_111".equals(mstVO.getPartner())) {
					historyMemo = "짐캐리 제휴사 접수";
					orderId = "GRP" + mstVO.getRcpSeq(); // 짐캐리도 GRP 형태를 사용한다면 유지
				} else {
					// 일반 접수 로직
					if(receiptInfVO.getSrType() != null) {
						if(receiptInfVO.getSrType().equals("S")) {
							historyMemo = "아이콘 택배접수 왕복1차";
						} else if(receiptInfVO.getSrType().equals("R")) {
							historyMemo = "아이콘 택배접수 왕복2차";
						}
						orderId = "GRP" + mstVO.getRcpSeq();
					}
				}

				payVO.setOrderId(orderId);

				// 4. 결제 정보 및 히스토리 저장
				getDAO().insert("golfReceiptPay.insertReceiptPay", payVO);
				insertGolfHistory(String.valueOf(receiptInfVO.getRcpNumber()), historyMemo);
			}
		}
	}

	/**
	 * 골프장 리스트 검색
	 * @param search
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GolfInfVO> selectReceiptGolfList(GolfInfVO search) throws Exception {

		//골프장 리스트
		return (List<GolfInfVO>) getDAO().selectBySearch("golfReceipt.selectGolfInf", search, "selectGolfInfCount");
	}

	/**
	 * 택배접수 수정(택배조회>수정)
	 * @param mstVo
	 * @param infVo
	 * @throws Exception
	 */
	public String updateReceipt(GolfReceiptMstVO mstVo, GolfReceiptInfVO infVo) throws Exception {
		String historyMemo = "아이콘 택배접수 수정";
		if (mstVo != null) {
			//접수 수정
			getDAO().update("golfReceiptMst.updateSelective", mstVo);

			//물품 접수 등록
			if(infVo != null){
				getDAO().update("golfReceiptInf.updateSelective", infVo);

				//결제금액
				GolfReceiptPayVO payVO = new GolfReceiptPayVO();
				payVO.setPayAmt(infVo.getPdPay());
				payVO.setRcpInfSeq(infVo.getRcpInfSeq());

				getDAO().update("golfReceiptPay.updateSelective", payVO);
				//api 전송
				golfApi.updateApi(infVo, "update_receipt");

				// 아이콘 렌탈 GolfReceiptInfVO
				rentalServiceImpl.updateApi(infVo, "update");

				//히스토리 등록
				insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
			}
			return "yes";
		}

		else
			return "no";
	}

	/**
	 * 택배접수 수정(택배조회>수정)
	 * @param mstVo
	 * @param infVo
	 * @throws Exception
	 */
	public void updateReceipt(GolfReceiptInfVO infVo) throws Exception {
		String historyMemo = "영업소 택배접수";

		//영업소에서 오는 금액 업데이트 금지
		infVo.setPdPay(0);
		getDAO().update("golfReceiptInf.updateSelective", infVo);

		//결제금액
//		GolfReceiptPayVO payVO = new GolfReceiptPayVO();
//		payVO.setRcpKind(infVo.getRcpKind());
//		payVO.setRcpInfSeq(infVo.getRcpInfSeq());
//
//		getDAO().update("golfReceiptPay.updateSelectiveByRcpInfSeqArray", payVO);

		//히스토리 등록
		insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
	}


	/**
	 * 선택취소 처리
	 * @param infVo
	 * @throws Exception
	 */
	public void updateReceiptInfSel(GolfReceiptInfVO infVo, boolean flag) throws Exception {
		System.out.println("취소처리");
		String historyMemo = "픽업요청";
		String rcpKind ="";
		List<String> rcpNumberArr = new ArrayList<String>();
		List<Integer> rcpInfSeqArr = new ArrayList<Integer>();

		GolfReceiptPayVO payVO = new GolfReceiptPayVO();

		//접수구분
		List<GolfReceiptInfVO> golfRcptList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByRcpKindChk", infVo);

		if(golfRcptList.size() > 0) {
			for(GolfReceiptInfVO golfRcp : golfRcptList) {
				rcpKind = golfRcp.getRcpKind();
				rcpNumberArr.add(golfRcp.getRcpNumber());
			}
			//스마트스코어 취소알림톡 표시 및 히스토리 등록용 (왕복인 경우 2건 표시, 2건 히스토리 등록)
			infVo.setRcpNumberArray(rcpNumberArr);
		}


		if(infVo.getRcpInfStat().equals(StatusVO.Delivery.code())) {
			historyMemo = "택배접수";


		}else if(infVo.getRcpInfStat().equals(StatusVO.ReceiptCancel.code())) {
			historyMemo = "골프취소";
			GolfReceiptMstVO mstVo = new GolfReceiptMstVO();
			GolfReceiptInfVO receiptVo = new GolfReceiptInfVO();
			mstVo.setRcpSeq(infVo.getRcpSeq());

			//스마트스코어 취소 경우 (전체취소, 결제취소)
			if("2".equals(rcpKind)) {
				historyMemo = "스마트스코어 골프, 결제 취소";
				receiptVo.setRcpInfStat(StatusVO.ReceiptCancel.code());
				receiptVo.setRcpSeq(mstVo.getRcpSeq());
				receiptVo.setRcpKind(rcpKind);


				//왕복1차가 배송완료가 아닐때는 접수취소하면 둘다 취소된다
				System.out.println("테스트해본니다ㅓ" + golfRcptList.get(0).getRcpInfStat());
				if(!golfRcptList.get(0).getRcpInfStat().equals("3")){
					getDAO().update("golfReceipt.cancelSmartGolfInf", receiptVo);
				}else{
				//왕복1차가 배송완료일시에는 왕복2차만 접수취소한다.
					receiptVo.setRcpNumber(golfRcptList.get(1).getRcpNumber());
					getDAO().update("golfReceipt.cancelSmartGolfInfComplete", receiptVo);
				}

				payVO.setPayStat(StatusPayVO.Cancel.code());
				//왕복1차이고  모두 취소

				logger.debug("사이즈"+ golfRcptList.size());
				logger.debug("델카인드"+ infVo.getDelKind());
				if(golfRcptList.size() > 1 && "1".equals(infVo.getDelKind())) {
					payVO.setOrderId("GRP"+Integer.toString(mstVo.getRcpSeq()));
				}else {
					//왕복2차 및 편도인 경우 한건만 취소
					payVO.setOrderId(infVo.getRcpNumber());
				}

				getDAO().update("golfReceiptPay.cancelSmartGolfPay", payVO);
			}else {
				//편도처리
				mstVo.setDelKind("1");
				getDAO().update("golfReceiptMst.updateSelective", mstVo);

				//왕복인 경우 그룹번호에서 골프접수번호로 변경
				if("2".equals(infVo.getDelKind())) {

					receiptVo.setOrderId("GRP"+infVo.getRcpSeq());

					List<GolfReceiptInfVO> golfReceiptList;
					if(infVo.getPrdType() == 3) {
						 golfReceiptList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectForCarrier", receiptVo);
					}else {
						 golfReceiptList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByKakaoHistory", receiptVo);
					}


					if(golfReceiptList.size()>0) {
						for(GolfReceiptInfVO receiptInfVO : golfReceiptList){
							payVO = new GolfReceiptPayVO();

							payVO.setOrderId(receiptInfVO.getRcpNumber());
							payVO.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
							payVO.setPayAmt(receiptInfVO.getPdPay());
							rcpInfSeqArr.add(receiptInfVO.getRcpInfSeq());
							payVO.setRcpInfSeqArray(rcpInfSeqArr);

							getDAO().update("golfReceiptPay.updateSelectiveByRcpInfSeqArray", payVO);

							//20241129 왕복 선택취소시 할인율 저장.
							if(receiptInfVO.getBostonCnt() > 0
									&& !infVo.getRcpNumber().equals(receiptInfVO.getRcpNumber())) {
								int discountTotal = 3000*receiptInfVO.getBostonCnt();
								receiptVo.setIconFee(receiptInfVO.getIconFee()-discountTotal);
								receiptVo.setPdPay(receiptInfVO.getPdPay()-discountTotal);
							}else {
								receiptVo.setIconFee(receiptInfVO.getIconFee());
								receiptVo.setPdPay(receiptInfVO.getPdPay());
							}
							receiptVo.setPdDeliPay(receiptInfVO.getPdDeliPay());
							receiptVo.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
							getDAO().update("golfReceipt.updateGolfInf", receiptVo);

							logger.debug("point 1");
						}
					}
				}
			}

		}else if(infVo.getRcpInfStat().equals(StatusVO.PickupCancel.code())) {
			historyMemo = "픽업취소";
		}else if(infVo.getRcpInfStat().equals(StatusVO.DeliveryCancel.code())) {
			historyMemo = "택배취소";
		}else if(infVo.getRcpInfStat().equals(StatusVO.Receipt.code())) {
			historyMemo = "골프접수 신청";
		}

		if(infVo != null){
			getDAO().update("golfReceipt.updateGolfInf", infVo);
			logger.debug("point 2");
		}

		//영업소에서 요청또는 취소시에는 결제상태값 변경 처리 안함
		if(flag){
			//픽업취소일때 결제취소
			if(StatusVO.PickupCancel.code().equals(infVo.getRcpInfStat())){
				payVO = new GolfReceiptPayVO();
				payVO.setRcpInfSeq(infVo.getRcpInfSeq());
				payVO.setPayStat(StatusPayVO.Cancel.code());

				rcpInfSeqArr.add(infVo.getRcpInfSeq());
				payVO.setRcpInfSeqArray(rcpInfSeqArr);

				getDAO().update("golfReceiptPay.updateSelectiveByRcpInfSeqArray", payVO);

			}
		}

		//영업소에서 데이터가 오는 경우 골프 접수번호를 주지 않아서 회수번호로 골프 접수번호를 조회 (단 접수대기상태에서 골프접수로 변환 시 제외)
		if(infVo.getRcpNumber()==null) {
			List<GolfReceiptInfVO> golfReceiptList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByKakao", infVo);
			if(golfReceiptList.size()>0) {
				infVo.setRcpNumber(golfReceiptList.get(0).getRcpNumber());
			}
		}

		boolean rentalFlag = Arrays.asList("6", "7", "8").contains(rcpKind); // 렌탈 웹 모바일 기타

		//히스토리 등록 및 API 전송
		if("2".equals(rcpKind) || "0".equals(rcpKind) || "5".equals(rcpKind) ||  rentalFlag ) { // 교참추가..

			GolfReceiptInfVO api =  (GolfReceiptInfVO)getDAO().selectOne("golfReceiptInf.selectDetailByRcpNumber", infVo.getRcpNumber());

			System.out.println("에이피아이첫" + api);

			if(!infVo.getRcpInfStat().equals(StatusVO.ReceiptCancel.code())) {
				rcpNumberArr = new ArrayList<String>();
				rcpNumberArr.add(infVo.getRcpNumber());
				//히스토리 등록용
				infVo.setRcpNumberArray(rcpNumberArr);

				//알림톡 발송용 (스마트스코어인 경우 알림톡 발송 안함)
				infVo.setRcpNumber(rcpNumberArr.get(0));
			}else if("2".equals(rcpKind)){
				//스마스스코어 취소 시 왕복등록건은 1차, 2차 모두 히스토리에 등록되고 알림톡 메세지에 rcmNumber 두개 표시 및 스마트스코어에 rcpNumber 2개 전송
				api.setRcpNumber(String.join(" | ", rcpNumberArr));
				api.setRcpNumberArray(rcpNumberArr);

				System.out.println("에이피아이" + new Gson().toJson(api));
			}

			//api 전송

			System.out.println("크기" + rcpNumberArr.size());

//			if(rcpNumberArr.size() == 2){
//				//요청을 두번 해줘야한다고 한다 ..
//				api.setRcpNumber(rcpNumberArr.get(0));
//				System.out.println("first" + api.getRcpNumber());
//				golfApi.updateApi(api, "update_cbag_status");
//
//				api.setRcpNumber(rcpNumberArr.get(1));
//				System.out.println("second" + api.getRcpNumber());
//				golfApi.updateApi(api, "update_cbag_status");
//
//			}else{
//				golfApi.updateApi(api, "update_cbag_status");
//			}

			// 2025.04.23 스마스스토어 취소제외
			String rcpInfStat = infVo.getRcpInfStat(); // 접수 상태코드
			// 취소 상태인지 여부 (7: 골프취소, 8: 픽업취소, 9: 택배취소)
			boolean isCancelStatus =
					StatusVO.ReceiptCancel.code().equals(rcpInfStat)  // 7
					|| StatusVO.PickupCancel.code().equals(rcpInfStat)   // 8
					|| StatusVO.DeliveryCancel.code().equals(rcpInfStat); // 9

			// 스마트스코어(rcpKind: 2)이면서 취소 상태 코드면 외부 API 호출 금지
			boolean callExternalApi = !("2".equals(rcpKind) && isCancelStatus);

			if (callExternalApi) {
				// 외주 업체 상태 변경 API
				golfApi.updateApi(api, "update_cbag_status");

				// 아이콘 렌탈 접수상태변경
				api.setRcpInfStat(rcpInfStat); //안담길래 별도로 추가함.
				rentalServiceImpl.updateApi(api, "status");
			}

			insertGolfHistoryArr(infVo.getRcpNumberArray(), historyMemo);
			System.out.println("히스토리메모" + historyMemo);

		}else {
			insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
		}
	}

	/**
	 * 결제취소 및 완료 일괄처리
	 * @param infVo
	 * @throws Exception
	 */
	public String updateReceiptPaymentArray(GolfReceiptInfVO infVo) throws Exception {
		System.out.println("결제 취소처리");
		String result = "success";


		String historyMemo = "아이콘 결제완료";
		if("5".equals(infVo.getRcpKind())) {
			historyMemo = "교참 결제완료";
		}

		GolfReceiptPayVO payVO = new GolfReceiptPayVO();
		payVO.setRcpInfSeqArray(infVo.getRcpInfSeqArray());
		payVO.setPayStat(StringUtils.isEmpty(infVo.getGolfUpdate()) ? StatusPayVO.Complete.code() : StatusPayVO.Cancel.code());

		int payChk = (int)getDAO().selectOne("golfReceiptPay.selectByPayChk", payVO);

		if(payChk==0) {
			if(!payVO.getPayStat().equals(StatusPayVO.Cancel.code())){
				payVO.setPayGubun("0");
				payVO.setKakaoPayMessage(""); //결제 완료인 경우 빈칸처리 (카카오에서 접수 된 건은 결제요청이라는 메세지가 기본적으로 들어가있음)
			}
			getDAO().update("golfReceiptPay.updateSelectiveByRcpInfSeqArray", payVO);

			if(payVO.getPayStat().equals(StatusPayVO.Cancel.code())){
				historyMemo = "아이콘 결제취소";
				if("5".equals(infVo.getRcpKind())) {
					historyMemo = "교참 결제취소";
				}
			}
			insertGolfHistoryArr(infVo.getRcpNumberArray(), historyMemo);
		}else {
			result = "fail";
		}

		return result;
	}

	/**
	 * 영업소 회수번호로 물품접수번호,마스터 접수번호 조회
	 * @param infVo
	 * @return
	 * @throws Exception
	 */
	public GolfReceiptInfVO selectReceiptKey(GolfReceiptInfVO infVo) throws Exception {
		return (GolfReceiptInfVO)getDAO().selectByKey("golfReceipt.selectReceiptKey", infVo);
	}

	/**
	 * 스케줄 히스토리 등록
	 * @param url
	 * @throws Exception
	 */
	public void insertGolfShceduleHistory(String rcpNumber, String historyMemo){
		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumber(rcpNumber);
		golfHistoryVO.setHistoryMemo(historyMemo);

		golfHistoryVO.setRegUser("영업소 API");

		getDAO().insert("golfHistory.insertHistory", golfHistoryVO);
	}

	/**
	 * 히스토리 등록
	 * @param url
	 * @throws Exception
	 */
	public void insertGolfHistory(String rcpNumber, String historyMemo){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);

		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumber(rcpNumber);
		golfHistoryVO.setHistoryMemo(historyMemo);

		if(super.getConfig().getWebMode().equals("op")) {
			if(session.getAttribute("historyEmpCode")!=null) {
				golfHistoryVO.setRegUser(session.getAttribute("historyEmpCode").toString());
			}else {
				golfHistoryVO.setRegUser("영업소 API");
			}
		}else {
			golfHistoryVO.setRegUser("local");
		}

		getDAO().insert("golfHistory.insertHistory", golfHistoryVO);
	}


	/**
	 * 히스토리 등록
	 * @param url
	 * @throws Exception
	 */
	public void insertGolfHistoryArr(List<String>insertGolfHistoryArr, String historyMemo){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);

		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumberArray(insertGolfHistoryArr);
		golfHistoryVO.setHistoryMemo(historyMemo);

		if(super.getConfig().getWebMode().equals("op")) {
			if(session.getAttribute("historyEmpCode")!=null) {
				golfHistoryVO.setRegUser(session.getAttribute("historyEmpCode").toString());
			}else {
				golfHistoryVO.setRegUser("영업소 API");
			}
		}else {
			golfHistoryVO.setRegUser("local");
		}

		getDAO().insert("golfHistory.insertHistoryArr", golfHistoryVO);
	}

}
