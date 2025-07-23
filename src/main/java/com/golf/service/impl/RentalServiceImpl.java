package com.golf.service.impl;

import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.model.RentalHistoryVO;
import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.*;
import com.google.gson.Gson;
import egovframework.rte.psl.dataaccess.util.EgovMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.SystemOutLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("RentalService")
@RequiredArgsConstructor
public class RentalServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

	private final GeneralDAOImpl _gDao;

	private final KdngeAPI _kdngeApi;

	@Resource(name="globalProperty")
	GlobalProperty configBean;


	/**
	 * 챗봇 접수 시 운임체계(기본운임), 품목명(국제골프택배기본), 포장상태(편도/왕복 따라감), 배송구분(현불택배) 로 기본값 설정
	 * 골프접수번호는 insert시 생성
	 */
	public List<GolfReceiptInfVO> receiptProcConfirm(List<CbReceiptInfoVO> list) throws Exception {
		List<GolfReceiptInfVO> receiptList = new ArrayList<>();
		String company = "아이콘렌탈";
		// 접수상태(0:골프접수, 1:픽업요청, 2:택배접수, 3:배송완료, 4:접수대기(카카오접수), 7:골프취소, 8:픽업취소, 9:택배취소)
		String rcpInfStat = "0";
		String payStat = "0";

		if (list != null || !list.isEmpty()) {
			GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
			mstVO.setGolfSeq(list.get(0).getRecvGolfSeq()); // 받는분(왕복시 첫번째 데이터의 받는분) 골프장 순번 - 필수값 RECV_GOLF_SEQ
			mstVO.setDelKind(list.get(0).getDelKind());
			mstVO.setFareType("1"); // 운임체계
			mstVO.setRegUser(list.get(0).getUserId()); // 등록유저
			mstVO.setUseYn("Y");
			mstVO.setGameDt(list.get(0).getGameDt()); // 라운딩 시작일
			mstVO.setGameEndDt(list.get(0).getGameEndDt()); // 라운딩 종료일


			// 골프 접수 등록 GOLF_RECEIPT_MST
			_gDao.insert("golfReceipt.insertReceiptMst", mstVO);


			int rcpSeq = mstVO.getRcpSeq();
			logger.info("insertReceiptMst 실행 이후 rcpSeq - {}",mstVO.getRcpSeq());


			// RcpKind : 0: 아이콘, 1: 카카오, 5: 교참, 6 아이콘렌탈로하자
			for ( CbReceiptInfoVO cbReceiptInfoVo : list ){

				GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();

				receiptInfVO.setRcpInfStat(rcpInfStat);
				receiptInfVO.setRcpSeq(rcpSeq);
				receiptInfVO.setSrType(cbReceiptInfoVo.getSrType());
				receiptInfVO.setPayType("0"); // 배송지불타입(현불)
				receiptInfVO.setPdNm("국제골프택배기본");
				receiptInfVO.setPdPack("왕복"); // DEL_KIND - 배송구분(1:편도, 2:왕복)

				receiptInfVO.setDelKind("2"); // DEL_KIND - 배송구분(1:편도, 2:왕복)
				receiptInfVO.setSendGolfSeq(cbReceiptInfoVo.getSendGolfSeq()); // SEND_GOLF_SEQ - 보내는분 골프장 순번
				receiptInfVO.setCusSendNm(cbReceiptInfoVo.getCusSendNm()); // CUS_SEND_NM - 보내는분 이름
				receiptInfVO.setCusSendTelnum(cbReceiptInfoVo.getCusSendTelnum()); // CUS_SEND_TELNUM - 보내는분 연락처
				receiptInfVO.setCusSendZip(cbReceiptInfoVo.getCusSendZip());  // CUS_SEND_ZIP - 보내는분우편번호(골프장이 아닌 경우)
				receiptInfVO.setCusSendAddr1(cbReceiptInfoVo.getCusSendAddr1()); // CUS_SEND_ADDR1 - 보내는분주소(골프장이 아닌 경우)
				receiptInfVO.setCusSendAddr2(cbReceiptInfoVo.getCusSendAddr2()); // CUS_SEND_ADDR2 - 보내는분상세주소(골프장이 아닌 경우)

				receiptInfVO.setRecvGolfSeq(cbReceiptInfoVo.getRecvGolfSeq()); // RECV_GOLF_SEQ - 받는분골프장순번, GOLF_SEQ - 골프장순번
				receiptInfVO.setCusReNm(cbReceiptInfoVo.getCusReNm()); // CUS_RE_NM - 받는분 이름
				receiptInfVO.setCusReTelnum2(cbReceiptInfoVo.getCusReTelNum()); // CUS_RE_TELNUM - 받는분 연락처
				receiptInfVO.setCusReZip(cbReceiptInfoVo.getCusReZip()); // CUS_RE_ZIP - 받는분우편번호
				receiptInfVO.setCusReAddr1(cbReceiptInfoVo.getCusReAddr1()); // CUS_RE_ADDR1 - 받는분 주소
				receiptInfVO.setCusReAddr2(cbReceiptInfoVo.getCusReAddr2()); // CUS_RE_ADDR2 - 받는분 상세주소

				receiptInfVO.setPdCnt(cbReceiptInfoVo.getPdCnt()); // 골프백 수량
				receiptInfVO.setBostonCnt(0); // 보스턴백 수량
				receiptInfVO.setPdIndivPrice(cbReceiptInfoVo.getPdIndivPrice()); // 개별단가
				receiptInfVO.setPdGolfCnt(cbReceiptInfoVo.getPdGolfCnt()); // 골프채 수량

				receiptInfVO.setPdMemo(cbReceiptInfoVo.getPdMemo()); // 메모
				receiptInfVO.setPicReqDt(cbReceiptInfoVo.getPicReqDt()); // 픽업희망일
				receiptInfVO.setRcpKind(cbReceiptInfoVo.getRcpKind()); // 접수구분 - 현재 챗봇에서 넘겨줌
				receiptInfVO.setUserId(cbReceiptInfoVo.getUserId()); // 카카오챗봇ID
				receiptInfVO.setCusReceiptNm(cbReceiptInfoVo.getCusReceiptNm()); // 접수자이름
				receiptInfVO.setCusReceiptTelNum(cbReceiptInfoVo.getCusReceiptTelNum()); // 접수자전화번호
				receiptInfVO.setPrdType(1); //상품타입(1:골프택배, 2:중고채, 3:캐리어)


				// 보내는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
				GolfInfVO sendGolfInfVo = setGolfInfo(cbReceiptInfoVo.getSendGolfSeq(), cbReceiptInfoVo.getCusSendZip(), receiptInfVO, true);
				// 받는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
				GolfInfVO recvGolfInfVo = setGolfInfo(cbReceiptInfoVo.getRecvGolfSeq(), cbReceiptInfoVo.getCusReZip(), receiptInfVO, false);

				// 도서산간구분, 운임계산
				setCalculateShipping(receiptInfVO, cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);

				logger.info("챗봇 골프접수 데이터");
				logger.info("-->"+new Gson().toJson(receiptInfVO));
				_gDao.insert("golfReceipt.insertReceiptInfChat", receiptInfVO);
				receiptList.add(receiptInfVO);

				// 결제완료
				if("2".equals(cbReceiptInfoVo.getPayStat())){
					payStat = "1";
				}

				//결제금액
				GolfReceiptPayVO payVO = new GolfReceiptPayVO();
				payVO.setPayAmt(receiptInfVO.getPdPay());
				payVO.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
				payVO.setPayStat(payStat); // 결제상태 - 입금대기


				// PAY TABLE ORDER ID 추가
				if ( "2".equals(list.get(0).getDelKind()) ) {
					// 왕복
					payVO.setOrderId("GRP"+mstVO.getRcpSeq());
				}

				// 결제정보 등록
				logger.info("골프접수 결제정보 데이터");
				logger.info("-->"+new Gson().toJson(payVO));
				_gDao.insert("golfReceiptPay.insertReceiptPay", payVO);

				String historyMemo = company + " 택배접수 편도";
				if( !"1".equals(list.get(0).getDelKind()) && receiptInfVO.getSrType()!=null ) {
					if(receiptInfVO.getSrType().equals("S")) {
						historyMemo = company + " 택배접수 왕복1차";
					}else if(receiptInfVO.getSrType().equals("R")) {
						historyMemo = company + " 택배접수 왕복2차";
					}
				}
				// 히스토리 등록
				logger.info("골프접수 히스토리 데이터");
				insertGolfHistory(String.valueOf(receiptInfVO.getRcpNumber()), historyMemo);
			}
		}

		return receiptList;
	}

	/**
	 * GOLF_RECEIPT_MST, GOLF_RECEIPT_INF INSERT 전 처리
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도서산간구분 set
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도착영업소명, 도착영업소코드 set, 골프장일 경우 골프장 테이블에 있는 도착영업소명, 도착영업소코드 set
	 * @param golfSeq 골프장번호
	 * @param zipCode 우편번호
	 * @param receiptInfVo 접수정보
	 * @param isSend 보내는건지 여부
	 */
	private GolfInfVO setGolfInfo(int golfSeq, String zipCode, GolfReceiptInfVO receiptInfVo, boolean isSend){
		GolfInfVO golfInfVo = null;
		Map<String, String> branchMap = null;

		logger.info("개별 단가: {}", receiptInfVo.getPdIndivPrice());

		// 골프장 정보가 있을 경우 DB에서 조회
		if (golfSeq != 0) {
			GolfInfVO search = new GolfInfVO();
			search.setGolfSeq(golfSeq); // 조건 : 골프장번호
			golfInfVo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", search.getGolfSeq());

			if (golfInfVo != null) {
				logger.info("도착 영업소 정보: {}, {}, {}", golfInfVo.getPicOfficeCode1(), golfInfVo.getGolfName(), golfInfVo.getIslYn());
				if (isSend) { // 보내는 분
					receiptInfVo.setCusSendZip(golfInfVo.getZipCode());
					receiptInfVo.setCusSendAddr1(golfInfVo.getGolfAddr1());
					receiptInfVo.setBrStrCode(golfInfVo.getPicOfficeCode1());
					receiptInfVo.setBrStrNm(golfInfVo.getPicOffice1());
				} else { // 받는 분
					receiptInfVo.setCusReZip(golfInfVo.getZipCode());
					receiptInfVo.setCusReAddr1(golfInfVo.getGolfAddr1());
					receiptInfVo.setBrEndCode(golfInfVo.getPicOfficeCode1());
					receiptInfVo.setBrEndNm(golfInfVo.getPicOffice1());
				}
			} else {
				logger.info("골프장 정보가 없습니다: {}", golfSeq);
			}
		}

		// DB 골프장 정보가 없으면 우편번호로 영업소 정보 조회
		if (golfInfVo == null || golfInfVo.getGolfSeq() == 0) {
			GolfInfVO infVO = new GolfInfVO();
			infVO.setType1("zipcode");
			infVO.setKeyword(zipCode);

			try {
				branchMap = _kdngeApi.getBranchInfo(infVO); // 차세대 API
				if (branchMap != null) {
					logger.info("도착 영업소 정보: {}, {}", branchMap.get("brCode"), branchMap.get("brNm"));
					if (isSend) { // 보내는 분
						receiptInfVo.setBrStrCode(branchMap.get("brCode"));
						receiptInfVo.setBrStrNm(branchMap.get("brNm"));
					} else { // 받는 분
						receiptInfVo.setBrEndCode(branchMap.get("brCode"));
						receiptInfVo.setBrEndNm(branchMap.get("brNm"));
					}
				} else {
					logger.info("우편번호로 영업소 정보를 조회할 수 없습니다: {}", zipCode);
				}
			} catch (Exception e) {
				logger.info("영업소 정보를 조회하는 중 오류 발생: {}", e.getMessage());
			}
		}

		return golfInfVo;
	}

	// 골프장 정보 조회
	public GolfInfVO getGolfInfo(Long golfSeq) {
		return (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", golfSeq);
	}

	/**
	 * 도서산간구분, 운임계산
	 * 0 = 기본운임, 1 = 스코어운임, 2 = 특판가
	 * 챗봇 한정 - 운임체계(기본운임)
	 * @param receiptInfVO 등록할 접수 정보
	 * @param cbReceiptInfoVo 전달받은 접수 정보
	 * @param sendGolfInfVo 보내는분 정보
	 * @param recvGolfInfVo 받는분 정보
	 */
	public void setCalculateShipping(GolfReceiptInfVO receiptInfVO, CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {

		// 도서운임, 배송비, 기본 배송비
		int amtArray[] = {5000, 21000, 20000};
		int pdDosunPay = 0;  // 도서운임
		int pdDeliPay = 0; // 배송비
		int iconPay = 0;  // 아이콘 수수료
		int extraPrice = 0; // 할증 운임
		int pdPay = 0;  // 총 운임

		// 보내는 사람과 받는 사람 도서산간 여부 초기화
		receiptInfVO.setSendIslYn("N");
		receiptInfVO.setRecvIslYn("N");

		// ---- 왕복 ----
		if ("2".equals(cbReceiptInfoVo.getDelKind())) {
			// 운임 계산
			pdDosunPay = calculateDosunPay(receiptInfVO, amtArray[0]);
			
			// 배송비 계산
			pdDeliPay = calculateDeliveryPay(amtArray[2], cbReceiptInfoVo.getPdCnt());

			// 아이콘 수수료 계산
			iconPay = calculateIconFee(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);

			// 할증 운임
			extraPrice = getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());

			// 총 운임 계산
			pdPay = calculateTotalPay(pdDosunPay, pdDeliPay, iconPay, extraPrice, cbReceiptInfoVo.getPdCnt());
		}else{
			// 아이콘 렌탈은 왕복만 운행.
			// ---- 편도 ----
		}

		// 계산된 운임 값을 receiptInfVO에 반영
		receiptInfVO.setPdDosunPay(pdDosunPay);    // 도서운임
		receiptInfVO.setPdDeliPay(pdDeliPay);      // 배송비
		receiptInfVO.setIconFee(iconPay);          // 아이콘 수수료
		receiptInfVO.setPdEtcPay(extraPrice);      // 할증 운임
		receiptInfVO.setPdPay(pdPay);              // 총 운임

		logger.debug("receiptInfVO {}", receiptInfVO.toString());

		// 보스턴백 사용 x 할인가 x
	}

	// 총 운임 계산
	public int calculateTotalPay(int pdDosunPay, int pdDeliPay, int iconPay, int extraPrice, int pdCnt) {
		int totalPay = 0;

		// 도서산간이 있을 경우 도서운임과 배송비를 합산하여 계산
		totalPay = pdDosunPay + pdDeliPay + iconPay + extraPrice;

		logger.debug("pdDosunPay {}", pdDosunPay);
		logger.debug("pdDeliPay {}", pdDeliPay);
		logger.debug("iconPay {}", iconPay);
		logger.debug("extraPrice {}", extraPrice);
		logger.debug("pdCnt {}", pdCnt);
		logger.debug("totalPay {}", totalPay);

		return totalPay;
	}

	// 도서산간 금액
	public int calculateDosunPay(GolfReceiptInfVO receiptInfVO, int dosunAmt) {
		int dosunCount = 0;

		// 보내는 사람 주소가 도서산간인지 확인
		if (isIsl(receiptInfVO.getCusSendAddr1()) || isIsl(receiptInfVO.getCusSendAddr2())) {
			dosunCount += 1;
			receiptInfVO.setSendIslYn("Y");  // 보내는 사람 도서산간
		}

		// 받는 사람 주소가 도서산간인지 확인
		if (isIsl(receiptInfVO.getCusReAddr1()) || isIsl(receiptInfVO.getCusReAddr2())) {
			dosunCount += 1;
			receiptInfVO.setRecvIslYn("Y");  // 받는 사람 도서산간
		}

		// 도서산간 여부 판단 후, 도선료 계산
		if (dosunCount == 0 && (receiptInfVO.getSendGolfSeq() == 211 || receiptInfVO.getRecvGolfSeq() == 211)) {
			dosunCount += 1;
		}

		return (dosunCount >= 1 ? receiptInfVO.getPdCnt() * dosunAmt : 0);
	}

	// 제주포함여부
	private boolean isIsl(String address) {
		return address != null && address.contains("제주");
	}

	// 할증 운임가
	public int getExtraPrice(String pdIndivPrice){
		return _kdngeApi.getExtraPrice(pdIndivPrice);
	}

	// 배달비 계산 ( 골프백 수 * 금액 )
	public int calculateDeliveryPay(int deliveryAmt, int pdCnt) {
		return deliveryAmt * pdCnt;
	}

	/**
	 * 아이콘 수수료 * 수량
	 * @param cbReceiptInfoVo
	 * @param sendGolfInfVo
	 * @param recvGolfInfVo
	 * @return
	 */
	private int calculateIconFee(CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {
		int sumIconFee = 0;
		int sendGolfFee = 0;
		int recvGolfFee = 0;
		int pdCnt = cbReceiptInfoVo.getPdCnt(); // 골프백 수

		// 보내는 곳이 골프장일 경우
		if (cbReceiptInfoVo.getSendGolfSeq() > 0) {
			sendGolfFee = sendGolfInfVo.getFee4();
		}

		// 받는 곳이 골프장일 경우
		if (cbReceiptInfoVo.getRecvGolfSeq() > 0) {
			recvGolfFee = recvGolfInfVo.getFee4();
		}

		// 왕복금액
		sumIconFee = calculateIconFeeByKind(sendGolfFee, recvGolfFee, pdCnt, 4500);

		return sumIconFee;
	}

	// 수수료 계산 편도 8000, 왕복 1차 4500, 왕복 2차 4500
	public int calculateIconFeeByKind(int sendGolfFee, int recvGolfFee, int pdCnt, int defaultFee) {
		int sumIconFee = 0;
		if (sendGolfFee == 0 && recvGolfFee == 0) {
			sumIconFee = defaultFee * pdCnt;
		} else if (sendGolfFee > 0 && recvGolfFee == 0) {
			sumIconFee = sendGolfFee;
		} else if (sendGolfFee == 0 && recvGolfFee > 0) {
			sumIconFee = recvGolfFee;
		} else {
			sumIconFee = Math.min(sendGolfFee, recvGolfFee);
		}
		return sumIconFee;
	}

	// 골프 히스토리 이력
	public void insertGolfHistory(String rcpNumber, String historyMemo){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);

		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumber(rcpNumber);
		golfHistoryVO.setHistoryMemo(historyMemo);

		if(!super.getConfig().getWebMode().equals("local")) {
			if(session.getAttribute("historyEmpCode")!=null) {
				golfHistoryVO.setRegUser(session.getAttribute("historyEmpCode").toString());
			}else {
				golfHistoryVO.setRegUser("영업소 API");
			}
		}else {
			golfHistoryVO.setRegUser("local");
		}

		logger.info("-->"+new Gson().toJson(golfHistoryVO));
		_gDao.insert("golfHistory.insertHistory", golfHistoryVO);
	}


	// 아이콘 렌탈 상태전달 API 호출
	public void updateApi(GolfReceiptInfVO infVO, String type) {
		logger.debug("########### 아이콘 렌탈 API 호출 ###########");

		if (!"6".equals(infVO.getRcpKind())) {
			logger.debug("##### rcpKind: {} - type: {}", infVO.getRcpKind(), type);
			return;
		}

		logger.info("#### IR RCP_KIND: {}, URL_TYPE: {}", infVO.getRcpKind(), type);
		logger.info("#### IR JSON: {}", new Gson().toJson(infVO));

		String url = getHost() + "/api/icon-golf/response/" + type;

		try {
			EgovMap apiMap = _kdngeApi.callUrl(url, new Gson().toJson(infVO), "POST");
			logger.info("#### IR apiMap: {}", new Gson().toJson(apiMap));

			String statusStr = String.valueOf(apiMap.get("code"));
			String message = String.valueOf(apiMap.get("message"));

			if (!"200".equals(statusStr)) {
				EgovMap resultMap = (EgovMap) apiMap.get("result");

				String rcpInfSeq = String.valueOf(resultMap.get("rcpInfSeq"));
				String rcpSeq = String.valueOf(resultMap.get("rcpSeq"));
				String errorType = String.valueOf(resultMap.get("type"));

				saveFailLog(infVO, errorType, message, rcpInfSeq, rcpSeq);
			}

		} catch (Exception e) {
			logger.error("### IR API 호출 예외 발생: {}", e.getMessage(), e);
			saveFailLog(
					infVO,
					type,
					"EXCEPTION",
					String.valueOf(infVO.getRcpInfSeq()),
					String.valueOf(infVO.getRcpSeq())
			);
		}

		logger.debug("########### 아이콘 렌탈 API 호출 끝 ###########");
	}


	public String getHost() {
		if (configBean.getWebMode().equals("dev"))
			return "http://211.34.229.132:18087";
		if (configBean.getWebMode().equals("local"))
			return "http://localhost:2222";
		else if (configBean.getWebMode().equals("op"))
			return "";
		else
			return "";
	}
	private void saveFailLog(GolfReceiptInfVO infVO, String type, Object message, String rcpInfSeq, String rcpSeq) {
		RentalHistoryVO  rentalHistoryVO = new RentalHistoryVO ();
		rentalHistoryVO.setRcpInfSeq(Integer.parseInt(rcpInfSeq));
		rentalHistoryVO.setRcpSeq(Integer.parseInt(rcpSeq));
		rentalHistoryVO.setType(type);
		rentalHistoryVO.setOrderStatCd(infVO.getRcpInfStat());
		rentalHistoryVO.setMessage(String.valueOf(message));
		logger.debug("########### 접속불가 ###########", rentalHistoryVO);
		_gDao.insert("golfHistory.insertFailApiLog", rentalHistoryVO);

	}
}
