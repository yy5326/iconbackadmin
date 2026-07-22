package com.golf.chat.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.golf.enums.*;
import com.golf.service.model.*;
import com.golf.talk.vo.SendRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbGolfInfoVo;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.BaseServiceImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;
import com.golf.service.impl.KdngeAPI;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.util.EgovMapDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service("CbReceiveService")
public class CbReceiveServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(CbReceiveServiceImpl.class);

	private int bostonAmt = 6000;		//보스턴백 편도 운임
	private int bostonAmtR = 6000;		//보스턴백 왕복 운임
	private int bostonDosunAmt = 6000;	//보스턴백 편도 도선
	private int bostonDosunAmtR = 6000;	//보스턴백 왕복 도선

	// 제주,여수경도 특수구간 운임
	private int specialRouteGolfDosunAmt = 10000;  // 5000 + 5000
	private int specialRouteBostonDosunAmt = 12000;  // 6000 + 6000

	@Autowired
	private GeneralDAOImpl _gDao;

	@Resource
	GolfReceiptServiceImpl _receiptService;

	@Resource
	SendTalkServiceImpl _sendTalkService;

	@Resource
	KdngeAPI _kdngeApi;

	public int golfCnt(CbCommonVO cbCommon) throws Exception {
		// TODO Auto-generated method stub
		return (int)_gDao.selectByKey("cbGolfInfo.selectByCnt", cbCommon);
	}

	@SuppressWarnings("unchecked")
	public List<CbGolfInfoVo> selectReceiptGolfList(CbCommonVO cbCommon) {
		// TODO Auto-generated method stub
		return (List<CbGolfInfoVo>)_gDao.selectList("cbGolfInfo.selectBySearch", cbCommon);
	}

	/**
	 *
	 * @param list
	 * 챗봇 접수 시 운임체계(기본운임), 품목명(국제골프택배기본), 포장상태(편도/왕복 따라감), 배송구분(현불택배) 로 기본값 설정
	 * 골프접수번호는 insert시 생성
	 * @return
	 * @throws Exception
	 */
	public List<GolfReceiptInfVO> receiptProcConfirm(List<CbReceiptInfoVO> list) throws Exception {
		List<GolfReceiptInfVO> receiptList = new ArrayList<GolfReceiptInfVO>();

		String rcpInfStat = RcpInfStat.WAIT.code();
		String payStat = PayStat.COMPLETE.code();
		String company = "스마트스코어";
		String kakaoPayStat = "";
		String kakaoPayMessage = "";
		int bostonCnt = 0;

		if ( list != null ) {

			//접수 등록
			GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
			mstVO.setGolfSeq(list.get(0).getRecvGolfSeq()); // 받는분(왕복시 첫번째 데이터의 받는분) 골프장 순번 - 필수값 RECV_GOLF_SEQ
			mstVO.setDelKind(list.get(0).getDelKind());
			mstVO.setFareType("1"); // 운임체계
			//mstVO.setGameDt(list.get(0));
			mstVO.setRegUser(list.get(0).getUserId()); // 등록유저
			mstVO.setUseYn("Y");
			mstVO.setGameDt(list.get(0).getGameDt()); // 라운딩 시작일
			mstVO.setGameEndDt(list.get(0).getGameEndDt()); // 라운딩 종료일

			_gDao.insert("golfReceipt.insertReceiptMst", mstVO);
			//int rcpSeq = _gDao3.insert("golfReceipt.insertReceiptMst", mstVO);

			//int rcpSeq = ((GolfReceiptMstVO)_gDao3.selectByKey("golfReceipt.selectMaxReceiptKey", mstVO)).getRcpSeq();
			int rcpSeq = mstVO.getRcpSeq();
			logger.info("insertReceiptMst 실행 이후"+mstVO.getRcpSeq());
			logger.info("insertReceiptMst 실행 이후 max select rcpSeq"+rcpSeq);
			for ( CbReceiptInfoVO cbReceiptInfoVo : list ) {
				GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();

				receiptInfVO.setRcpSeq(rcpSeq);

				if("1".equals(cbReceiptInfoVo.getRcpKind())) { //결제상태 - 카카오 : 입금대기, 스마트스코어 : 결제완료
					company = "카카오";
					payStat = PayStat.STAY.code();
					kakaoPayStat = "READY";
					kakaoPayMessage = "결제요청";
				}else {
					bostonCnt = cbReceiptInfoVo.getBostonCnt();
				}

				if("0".equals(cbReceiptInfoVo.getRcpKind())) { //결제상태 - 카카오 : 입금대기, 스마트스코어 : 결제완료
					company = "아이콘";
					payStat = cbReceiptInfoVo.getPayStat();
					kakaoPayStat = "";
					kakaoPayMessage = "";
				}

				// 2025.05.21 코참 추가
				if("5".equals(cbReceiptInfoVo.getRcpKind())) {
					company = "코참";
					payStat = cbReceiptInfoVo.getPayStat();
					kakaoPayStat = "";
					kakaoPayMessage = "";
				}

				if("9".equals(cbReceiptInfoVo.getRcpKind())) {
					company = "팬스타크루즈";
					payStat = cbReceiptInfoVo.getPayStat();
					kakaoPayStat = "";
					kakaoPayMessage = "";
				}

				//스마트 스코어 일때 접수상태 골프접수,20260326 팬스타크루즈 추가
				if ("2".equals(cbReceiptInfoVo.getRcpKind()) || "0".equals(cbReceiptInfoVo.getRcpKind())
						|| "3".equals(cbReceiptInfoVo.getRcpKind()) || "5".equals(cbReceiptInfoVo.getRcpKind())
						|| "9".equals(cbReceiptInfoVo.getRcpKind())) {
					if (PayStat.UNPAID.code().equals(payStat)) {
						rcpInfStat = RcpInfStat.WAIT.code();
					} else {
						rcpInfStat = RcpInfStat.RECEIPT.code();
					}
					receiptInfVO.setRcpInfStat(rcpInfStat);
				} else {
					receiptInfVO.setRcpInfStat(rcpInfStat);
				}
				//receiptInfVO.setRcpInfStat(rcpInfStat); // 접수상태 - 카카오 , 스마트스코어: 접수대기
				receiptInfVO.setSrType(cbReceiptInfoVo.getSrType());
				receiptInfVO.setPayType("0"); // 배송지불타입(현불)
				receiptInfVO.setPdNm("국제골프택배기본");
				// 241120 보스턴백수량 1개이상일시 국제골프택배세트로 고정
				// 20250521 코참 추가,20260326 팬스타크루즈 추가
				if(("0".equals(cbReceiptInfoVo.getRcpKind()) || "5".equals(cbReceiptInfoVo.getRcpKind()) || "9".equals(cbReceiptInfoVo.getRcpKind()))
						&& cbReceiptInfoVo.getBostonCnt() > 0) {
					receiptInfVO.setPdNm("국제골프택배세트");
				}


				receiptInfVO.setPdPack("1".equals(cbReceiptInfoVo.getDelKind()) ? "편도" : "왕복");

				receiptInfVO.setDelKind(cbReceiptInfoVo.getDelKind());
				receiptInfVO.setSendGolfSeq(cbReceiptInfoVo.getSendGolfSeq());
				receiptInfVO.setCusSendNm(cbReceiptInfoVo.getCusSendNm());
				receiptInfVO.setCusSendTelnum(cbReceiptInfoVo.getCusSendTelnum());
				receiptInfVO.setCusSendZip(cbReceiptInfoVo.getCusSendZip());
				receiptInfVO.setCusSendAddr1(cbReceiptInfoVo.getCusSendAddr1());
				receiptInfVO.setCusSendAddr2(cbReceiptInfoVo.getCusSendAddr2());

				receiptInfVO.setRecvGolfSeq(cbReceiptInfoVo.getRecvGolfSeq());
				receiptInfVO.setCusReNm(cbReceiptInfoVo.getCusReNm());
				receiptInfVO.setCusReTelnum2(cbReceiptInfoVo.getCusReTelNum());
				receiptInfVO.setCusReZip(cbReceiptInfoVo.getCusReZip());
				receiptInfVO.setCusReAddr1(cbReceiptInfoVo.getCusReAddr1());
				receiptInfVO.setCusReAddr2(cbReceiptInfoVo.getCusReAddr2());

				receiptInfVO.setPdCnt(cbReceiptInfoVo.getPdCnt()); // 골프백 수량
				receiptInfVO.setBostonCnt(bostonCnt); // 보스턴백 수량
				receiptInfVO.setPdIndivPrice(cbReceiptInfoVo.getPdIndivPrice()); // 개별단가
				receiptInfVO.setPdGolfCnt(cbReceiptInfoVo.getPdGolfCnt()); // 골프채 수량

				receiptInfVO.setPdMemo(cbReceiptInfoVo.getPdMemo());
				//receiptInfVO.setPicReqDt(null); // 배송예정일이라 함 - 접수시에는 입력하지 않음
				receiptInfVO.setPicReqDt(cbReceiptInfoVo.getPicReqDt()); // 픽업희망일

				//receiptInfVO.setRcpKind("1");
				receiptInfVO.setRcpKind(cbReceiptInfoVo.getRcpKind()); // 접수구분 - 현재 챗봇에서 넘겨줌
				receiptInfVO.setUserId(cbReceiptInfoVo.getUserId()); // 카카오챗봇ID
				// 접수자이름
				receiptInfVO.setCusReceiptNm(cbReceiptInfoVo.getCusReceiptNm());
				// 접수자전화번호
 				receiptInfVO.setCusReceiptTelNum(cbReceiptInfoVo.getCusReceiptTelNum());
				receiptInfVO.setPrdType(1);

				//인천공항 구분용 preInsertReceiptInfo 함수 호출전.
				if("A".equals(list.get(0).getRcpType())) {
					receiptInfVO.setRcpType(list.get(0).getRcpType());
					receiptInfVO.setRcpAirSeq(list.get(0).getRcpSeqNo());
				}


				preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVo);

				// 공통 할인 계산
				applyDiscountAndFees(receiptInfVO, cbReceiptInfoVo);

				_gDao.insert("golfReceipt.insertReceiptInfChat", receiptInfVO);
				receiptList.add(receiptInfVO);
//
//				if("0".equals(cbReceiptInfoVo.getRcpKind())){
//
//					// 아이콘 프로젝트 접수시 결제정보 전송하려면 사용
//					logger.debug("test point 0" + new Gson().toJson(receiptList));
//					logger.debug("test point 0" + new Gson().toJson(mstVO));
//					_sendTalkService.sendPayRequest(receiptList, mstVO);
//				}

				//결제금액
				GolfReceiptPayVO payVO = new GolfReceiptPayVO();
				payVO.setPayAmt(receiptInfVO.getPdPay());
				payVO.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
				payVO.setUserId(cbReceiptInfoVo.getUserId());
				payVO.setKakaoPayStat(kakaoPayStat);
				payVO.setKakaoPayMessage(kakaoPayMessage);
				payVO.setPayStat(payStat); // 결제상태 - 입금대기

				// PAY TABLE ORDER ID 추가
				if (cbReceiptInfoVo.getOrderNumber() != null) {
					// 주문번호
					payVO.setOrderId(cbReceiptInfoVo.getOrderNumber());
				} else {
					if ( "1".equals(list.get(0).getDelKind()) ) {
						// 편도
						payVO.setOrderId(receiptInfVO.getRcpNumber());
					} else {
						// 왕복
						payVO.setOrderId("GRP"+mstVO.getRcpSeq());
					}
				}

				_gDao.insert("golfReceiptPay.insertReceiptPay", payVO);

				//히스토리 등록
				String historyMemo = company + " 택배접수 편도";

				if( !"1".equals(list.get(0).getDelKind()) && receiptInfVO.getSrType()!=null ) {
					if(receiptInfVO.getSrType().equals("S")) {
						historyMemo = company + " 택배접수 왕복1차";
					}else if(receiptInfVO.getSrType().equals("R")) {
						historyMemo = company + " 택배접수 왕복2차";
					}
				}
				insertGolfHistory(String.valueOf(receiptInfVO.getRcpNumber()), historyMemo);
			}

			/* 결제모듈 추가 → 결제정보 전송 X */
//			// 코참 추가
//			if("0".equals(list.get(0).getRcpKind()) || "3".equals(list.get(0).getRcpKind())
//					|| "5".equals(list.get(0).getRcpKind())|| "9".equals(list.get(0).getRcpKind())){
//
//				if(!"A".equals(list.get(0).getRcpType())){
//					// 아이콘 프로젝트 접수시 결제정보 전송하려면 사용
//					_sendTalkService.sendPayRequest(receiptList, mstVO);
//				}
//			}
		}

		return receiptList;
	}

	/**
	 * GOLF_RECEIPT_MST, GOLF_RECEIPT_INF INSERT 전 처리
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도서산간구분 set
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도착영업소명, 도착영업소코드 set, 골프장일 경우 골프장 테이블에 있는 도착영업소명, 도착영업소코드 set
	 * 각종 운임 set(총금액까지)
	 * @param cbReceiptInfoVo
	 * @param receiptInfVO
	 */
	public void preInsertReceiptInfo(GolfReceiptInfVO receiptInfVO, CbReceiptInfoVO cbReceiptInfoVo)  throws Exception{

		GolfInfVO search = new GolfInfVO();
		GolfInfVO sendGolfInfVo = null;
		GolfInfVO recvGolfInfVo = null;

		// 보내는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
		if (  cbReceiptInfoVo.getSendGolfSeq() != 0 ) {
			search.setGolfSeq(cbReceiptInfoVo.getSendGolfSeq());
			sendGolfInfVo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", search.getGolfSeq());
			logger.info(receiptInfVO.getPdIndivPrice());
			logger.info("보내는분 도착영업소 : "+sendGolfInfVo.getPicOfficeCode1()+","+sendGolfInfVo.getGolfName()+sendGolfInfVo.getIslYn());
			receiptInfVO.setCusSendZip(sendGolfInfVo.getZipCode());
			receiptInfVO.setCusSendAddr1(sendGolfInfVo.getGolfAddr1());
			receiptInfVO.setBrStrCode(sendGolfInfVo.getPicOfficeCode1());
			receiptInfVO.setBrStrNm(sendGolfInfVo.getPicOffice1());
		} else {
			GolfInfVO infVO = new GolfInfVO();
			infVO.setType1("zipcode");
			//infVO.setType1("A");
			infVO.setKeyword(cbReceiptInfoVo.getCusSendZip());
			Map<String, String> branchMap = _kdngeApi.getBranchInfo(infVO);
			logger.info("보내는분 도착영업소 : "+branchMap.get("brCode")+","+branchMap.get("brNm"));
			receiptInfVO.setBrStrCode(branchMap.get("brCode"));
			receiptInfVO.setBrStrNm(branchMap.get("brNm"));
		}

		// 받는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
		if (  cbReceiptInfoVo.getRecvGolfSeq() != 0 ) {
			search.setGolfSeq(cbReceiptInfoVo.getRecvGolfSeq());
			recvGolfInfVo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", search.getGolfSeq());
			logger.info("보내는분 도착영업소 : "+recvGolfInfVo.getPicOfficeCode1()+","+recvGolfInfVo.getGolfName());
			receiptInfVO.setCusReZip(recvGolfInfVo.getZipCode());
			receiptInfVO.setCusReAddr1(recvGolfInfVo.getGolfAddr1());
			receiptInfVO.setBrEndCode(recvGolfInfVo.getPicOfficeCode1());
			receiptInfVO.setBrEndNm(recvGolfInfVo.getPicOffice1());
		} else {
			GolfInfVO infVO = new GolfInfVO();
			infVO.setType1("zipcode");
			//infVO.setType1("A");
			infVO.setKeyword(cbReceiptInfoVo.getCusReZip());
			Map<String, String> branchMap = _kdngeApi.getBranchInfo(infVO);
			receiptInfVO.setBrEndCode(branchMap.get("brCode"));
			receiptInfVO.setBrEndNm(branchMap.get("brNm"));
		}

		// 도서산간구분, 운임계산
		searchIsl(receiptInfVO, cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getBranchInfo(GolfInfVO infVO) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("SEARCH_TYPE", infVO.getType1());
		paramMap.put("SEARCH_WORD", infVO.getSearchWord());

		logger.debug("여기가 중요 {}", infVO.getType1());
		EgovMap apiMap = callUrl(getApiUrl("GetBranchInfo"), gson.toJson(paramMap));

		if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
			List<EgovMap> apiMapList = (List<EgovMap>)apiMap.get("listData");
			result.put("brCode", apiMapList.get(0).get("brCode").toString());
			result.put("brNm", apiMapList.get(0).get("brNm").toString());
		}
		return result;
	}

	public void searchIsl(GolfReceiptInfVO receiptInfVO, CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {
		// 0 = 기본운임, 1 = 스코어운임, 2 = 특판가

		int amtArray[] = {5000, 21000, 20000};
		int dosun1 = 0; int dosun2 = 0;

		receiptInfVO.setSendIslYn("N");
		receiptInfVO.setRecvIslYn("N");

		receiptInfVO.setDiscountTotal(0);

		int pdDosunPay = 0;
		int pdDeliPay = 0;
		int iconPay = 0;
		int extraPrice = 0;
		int pdPay = 0;
		int bostonCnt = 0;
		int hdCardDiscount_OneWay = 4000;
		int hdCardDiscount_Round = 1000;
        //팬스타크루즈 할인금액
        int panstarDiscount_GolfBag = 3000;
        int panstarDiscount_BostonBag = 2000;

		//스마트스코어,20260326 팬스타크루즈 추가
		if("2".equals(cbReceiptInfoVo.getRcpKind()) || "0".equals(cbReceiptInfoVo.getRcpKind()) ||  "3".equals(cbReceiptInfoVo.getRcpKind())  ||  "4".equals(cbReceiptInfoVo.getRcpKind()) || "5".equals(cbReceiptInfoVo.getRcpKind()) || "9".equals(cbReceiptInfoVo.getRcpKind())) {
			bostonCnt = cbReceiptInfoVo.getBostonCnt();
		}

		if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
			if ( (receiptInfVO.getCusSendAddr1() != null && receiptInfVO.getCusSendAddr1().contains("제주"))
					|| (receiptInfVO.getCusSendAddr2() != null && receiptInfVO.getCusSendAddr2().contains("제주")) ) {
				dosun1 += 1;
				receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
			}

			if ( (receiptInfVO.getCusReAddr1() != null && receiptInfVO.getCusReAddr1().contains("제주"))
					|| (receiptInfVO.getCusReAddr2() != null && receiptInfVO.getCusReAddr2().contains("제주")) ) {
				dosun1 += 1; // 보내는분 출발지, 도착지 모두 도서산간이면 도서운임 안받음, 둘중 하나가 도서산간일 경우 도서운임
				receiptInfVO.setSendIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
			}

			if(dosun1==0) {
				if(receiptInfVO.getSendGolfSeq()==211) {
					dosun1 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
				}else if(receiptInfVO.getRecvGolfSeq()==211) {
					dosun1 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
				}
			}

			// 어딘가 도서산간 Y로 입력하는 부분이 한번씩 빠져서 방어로직 하나더 추가.
			if(receiptInfVO.getSendIslYn().equals("N") || receiptInfVO.getRecvIslYn().equals("N")) {
				if(receiptInfVO.getSendGolfSeq()==211) {
					dosun1 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
				}else if(receiptInfVO.getRecvGolfSeq()==211) {
					dosun1 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
				}
			}

			// 편도일 경우
			// 도서운임
			//pdDosunPay = dosun1 >= 1 ? (cbReceiptInfoVo.getPdCnt()*amtArray[0]) + (bostonCnt * bostonDosunAmt) : 0;
			// 편도 도선료 계산
			pdDosunPay = calculateDosunPay(cbReceiptInfoVo, receiptInfVO, bostonDosunAmt, cbReceiptInfoVo.getPdCnt());
			int discount =  hdCardDiscount_OneWay*cbReceiptInfoVo.getPdCnt();


			// 배송운임
			pdDeliPay = (amtArray[1]*cbReceiptInfoVo.getPdCnt()) + (bostonAmt * bostonCnt);



			// 아이콘 수수료
			iconPay = calculateIconFee(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);

			// 할증운임
//			extraPrice = getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());
			extraPrice = _kdngeApi.getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());

			// 총운임
			if (dosun1 >= 1) {
				// 도서 산간일 경우 0+1을 수량에 곱함
				//pdPay = ((amtArray[0] + amtArray[1]) * cbReceiptInfoVo.getPdCnt()) + ((bostonAmt + bostonDosunAmt) * bostonCnt);
				pdPay = pdDosunPay + (amtArray[1] * cbReceiptInfoVo.getPdCnt()) + (bostonAmt * bostonCnt);
			} else {
				pdPay = (amtArray[1] * cbReceiptInfoVo.getPdCnt()) + (bostonDosunAmt * bostonCnt);
			}
			// 편도
			pdPay += extraPrice + iconPay;

			if("3".equals(cbReceiptInfoVo.getRcpKind())) {
				receiptInfVO.setDiscount(pdPay - discount);
			}

            // 편도일경우 discout값을 0으로해서 할인금액 표시안되도록
            if("9".equals(cbReceiptInfoVo.getRcpKind())) {
                receiptInfVO.setDiscountTotal(0);
            }
		} else {
			// 왕복일 경우
			if ( "S".equals(cbReceiptInfoVo.getSrType()) ) {

				// 왕복 1차
				if ( (receiptInfVO.getCusSendAddr1() != null && receiptInfVO.getCusSendAddr1().contains("제주"))
						|| (receiptInfVO.getCusSendAddr2() != null && receiptInfVO.getCusSendAddr2().contains("제주")) ) {
					dosun1 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
				}

				if ( (receiptInfVO.getCusReAddr1() != null && receiptInfVO.getCusReAddr1().contains("제주"))
						|| (receiptInfVO.getCusReAddr2() != null && receiptInfVO.getCusReAddr2().contains("제주")) ) {
					dosun1 += 1; // 보내는분 출발지, 도착지 모두 도서산간이면 도서운임 안받음, 둘중 하나가 도서산간일 경우 도서운임
					receiptInfVO.setRecvIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
				}

				if(dosun1==0) {
					if(receiptInfVO.getSendGolfSeq()==211) {
						dosun1 += 1;
						receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
					}else if(receiptInfVO.getRecvGolfSeq()==211) {
						dosun1 += 1;
						receiptInfVO.setRecvIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
					}
				}


				// 어딘가 도서산간 Y로 입력하는 부분이 한번씩 빠져서 방어로직 하나더 추가.
				if(receiptInfVO.getSendIslYn().equals("N") || receiptInfVO.getRecvIslYn().equals("N")) {
					if(receiptInfVO.getSendGolfSeq()==211) {
						dosun1 += 1;
						receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
					}else if(receiptInfVO.getRecvGolfSeq()==211) {
						dosun1 += 1;
						receiptInfVO.setRecvIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
					}
				}

				// 도서운임
				//pdDosunPay = (dosun1 >= 1 ? (cbReceiptInfoVo.getPdCnt()*amtArray[0]) + (bostonCnt * bostonDosunAmtR) : 0);
				// 왕복 1차 도선료 계산
				pdDosunPay = calculateDosunPay(cbReceiptInfoVo, receiptInfVO, bostonDosunAmtR, cbReceiptInfoVo.getPdCnt());
				logger.info("RCP:{} - 1차 도선료 : {} ", cbReceiptInfoVo.getRcpNumber(), pdDosunPay);


				// 211 골프장일경우 위의 도선 로직이 빠지는 경우가 있어서 방어 로직 하나 더 추가
				/*if(receiptInfVO.getSendGolfSeq() == 211 ||  receiptInfVO.getRecvGolfSeq() == 211) {
					pdDosunPay = (cbReceiptInfoVo.getPdCnt()*amtArray[0]) + (bostonCnt * bostonDosunAmtR);
				}
				logger.info("RCP:{} - 1차 도선료 : {} ", cbReceiptInfoVo.getRcpNumber(), pdDosunPay);*/

				// 배송운임
				pdDeliPay = (amtArray[2]*cbReceiptInfoVo.getPdCnt()) + (bostonAmtR * bostonCnt);
								System.out.println("amtArray[2] : " + amtArray[2]);
								System.out.println("cbReceiptInfoVo.getPdCnt() : " + cbReceiptInfoVo.getPdCnt());
								System.out.println("bostonAmtR : " + bostonAmtR);
								System.out.println("bostonCnt : " + bostonCnt);
				// 아이콘 수수료
				iconPay = calculateIconFee(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);

				System.out.println("아이콘페이" + iconPay);
				// 할증운임
//				extraPrice = getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());
				extraPrice = _kdngeApi.getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());
				// 총운임
				if (dosun1 >= 1) {
					// 도서 산간일 경우 0+1을 수량에 곱함
					//pdPay = ((amtArray[0] + amtArray[2]) * cbReceiptInfoVo.getPdCnt()) + ((bostonAmtR + bostonDosunAmtR) * bostonCnt);
					pdPay = pdDosunPay + (amtArray[2] * cbReceiptInfoVo.getPdCnt()) + (bostonAmt * bostonCnt);
				} else {
					pdPay = (amtArray[2] * cbReceiptInfoVo.getPdCnt()) + (bostonDosunAmtR * bostonCnt);
				}

				// 편도 기준 정가 계산
				final int onewayPdPay = getOnewayPdPay(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo, dosun1, amtArray, bostonCnt, extraPrice);
				// 왕복 1차
				pdPay += extraPrice + iconPay;
				// 편도-왕복 차액
				receiptInfVO.setRoundTripDiscountAmt(onewayPdPay - pdPay);


//				if("3".equals(cbReceiptInfoVo.getRcpKind())) {
//					receiptInfVO.setDiscount(pdPay - (hdCardDiscount_Round * cbReceiptInfoVo.getPdCnt()));
//				}
			} else {

				// 왕복 2차
				if ( (receiptInfVO.getCusSendAddr1() != null && receiptInfVO.getCusSendAddr1().contains("제주"))
						|| (receiptInfVO.getCusSendAddr2() != null && receiptInfVO.getCusSendAddr2().contains("제주")) ) {
					dosun2 += 1;
					receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
				}

				if ( (receiptInfVO.getCusReAddr1() != null && receiptInfVO.getCusReAddr1().contains("제주"))
						|| (receiptInfVO.getCusReAddr2() != null && receiptInfVO.getCusReAddr2().contains("제주")) ) {
					dosun2 += 1; // 보내는분 출발지, 도착지 모두 도서산간이면 도서운임 안받음, 둘중 하나가 도서산간일 경우 도서운임
					receiptInfVO.setRecvIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
				}

				if(dosun2==0) {
					if(receiptInfVO.getSendGolfSeq()==211) {
						dosun2 += 1;
						receiptInfVO.setSendIslYn("Y"); // 보내는분 출발지가 도서산간일 경우 도서산간구분
					}else if(receiptInfVO.getRecvGolfSeq()==211) {
						dosun2 += 1;
						receiptInfVO.setRecvIslYn("Y"); // 보내는분 도착지가 도서산간일 경우 도서산간구분
					}
				}

				// 도서운임
				//pdDosunPay = (dosun2 >= 1 ? (cbReceiptInfoVo.getPdCnt()*amtArray[0]) + (bostonCnt * bostonDosunAmtR) : 0);
				// 왕복 2차 도선료 계산
				pdDosunPay = calculateDosunPay(cbReceiptInfoVo, receiptInfVO, bostonDosunAmtR, cbReceiptInfoVo.getPdCnt());
				logger.info("RCP:{} - 2차 도선료 : {} ", cbReceiptInfoVo.getRcpNumber(), pdDosunPay);

				// 211 골프장일경우 위의 도선 로직이 빠지는 경우가 있어서 방어 로직 하나 더 추가
				/*if(receiptInfVO.getSendGolfSeq() == 211 ||  receiptInfVO.getRecvGolfSeq() == 211) {
					pdDosunPay = (cbReceiptInfoVo.getPdCnt()*amtArray[0]) + (bostonCnt * bostonDosunAmtR);
				}
				logger.info("RCP:{} - 2차 도선료 : {} ", cbReceiptInfoVo.getRcpNumber(), pdDosunPay);*/

				// 배송운임
				pdDeliPay = (amtArray[2]*cbReceiptInfoVo.getPdCnt()) + (bostonAmtR * bostonCnt);
				// 아이콘 수수료
				iconPay = calculateIconFee(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);
				// 할증운임
//				extraPrice = getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());
				extraPrice = _kdngeApi.getExtraPrice(cbReceiptInfoVo.getPdIndivPrice());
				// 총운임
				if (dosun2 >= 1) {
					// 도서 산간일 경우 0+1을 수량에 곱함
					//pdPay = ((amtArray[0] + amtArray[2]) * cbReceiptInfoVo.getPdCnt()) + ((bostonAmtR + bostonDosunAmtR) * bostonCnt);
					pdPay = pdDosunPay + (amtArray[2] * cbReceiptInfoVo.getPdCnt()) + (bostonAmt * bostonCnt);
				} else {
					pdPay = (amtArray[2] * cbReceiptInfoVo.getPdCnt()) + (bostonDosunAmtR * bostonCnt);
				}
				// 편도 기준 정가 계산
				final int onewayPdPay = getOnewayPdPay(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo, dosun1, amtArray, bostonCnt, extraPrice);
				// 왕복 2차
				pdPay += extraPrice + iconPay;
				// 편도-왕복 차액
				receiptInfVO.setRoundTripDiscountAmt(onewayPdPay - pdPay);

//				if("3".equals(cbReceiptInfoVo.getRcpKind())) {
//					receiptInfVO.setDiscount(pdPay - (hdCardDiscount_Round * cbReceiptInfoVo.getPdCnt()));
//				}
			}

			if("3".equals(cbReceiptInfoVo.getRcpKind())) {
				receiptInfVO.setDiscount(pdPay - (hdCardDiscount_Round * cbReceiptInfoVo.getPdCnt()));
			}
            //20260326 팬스타크루즈 추가(할인금액계산)
            if("9".equals(cbReceiptInfoVo.getRcpKind())){

                //보스턴백
                int bostonBagDiscount = cbReceiptInfoVo.getBostonCnt()>=1? panstarDiscount_BostonBag*cbReceiptInfoVo.getBostonCnt() : 0;
                // 골프백
                int golfBagDiscount = cbReceiptInfoVo.getPdCnt() >= 1? panstarDiscount_GolfBag*cbReceiptInfoVo.getPdCnt() : 0;

                receiptInfVO.setDiscountTotal(bostonBagDiscount+golfBagDiscount);
            }
		}

		// 쿠폰 사용 여부 판단
		boolean isCouponApplied = (cbReceiptInfoVo.getCouponCode() != null
				&& !cbReceiptInfoVo.getCouponCode().isEmpty()
				&& !"0".equals(cbReceiptInfoVo.getCouponCode()))
				|| (cbReceiptInfoVo.getCouponDiscount() > 0);

		// 왕복 합산 이후에 적용하도록 receiptProcConfirm 으로 이동 (정책 변경(편도마다적용) 대비하여 주석)
		/*if ("0".equals(cbReceiptInfoVo.getRcpKind()) || "5".equals(cbReceiptInfoVo.getRcpKind())) {
			if (cbReceiptInfoVo.getPdCnt() > 0 && cbReceiptInfoVo.getBostonCnt() > 0) {

				// 쿠폰이 사용되지 않았을 때만 할인 적용
				if (!isCouponApplied && cbReceiptInfoVo.getBostonCnt() > 0) {
					int bostonDiscount = 3000 * cbReceiptInfoVo.getBostonCnt();
					// 세트 할인
					pdPay -= bostonDiscount;
					receiptInfVO.setPdPay(pdPay);
					receiptInfVO.setDiscountTotal(bostonDiscount);
					receiptInfVO.setDiscount(pdPay); // 할인된 최종 금액
				} else {
					logger.info("### 쿠폰 사용으로 인해 보스턴백 세트 할인 제외");
					receiptInfVO.setDiscount(pdPay);
					receiptInfVO.setDiscountTotal(0);
				}
			}
		}*/

		// 골프장 제휴사 수수료 보정 로직
		if (isPartnerGolf(cbReceiptInfoVo.getSendGolfSeq()) || isPartnerGolf(cbReceiptInfoVo.getRecvGolfSeq())) {
			// 쿠폰 적용 시 수수료 정책: 골프백 8,000원 + 보스턴백 4,000원 = 12,000원
			if (isCouponApplied) {
				// 개별 단위 수수료 정의 (수량에 맞춰 계산)
				int targetGolfBagFee = 8000 * cbReceiptInfoVo.getPdCnt();
				int targetBostonFee = 4000 * cbReceiptInfoVo.getBostonCnt();
				int newTotalIconPay = targetGolfBagFee + targetBostonFee;
				// pdPay에서 기존 iconPay를 빼고, 새 수수료를 더함
				pdPay = pdPay - iconPay + newTotalIconPay;
				// iconPay 갱신
				iconPay = newTotalIconPay;
				logger.info("### 제휴사 쿠폰 적용(12,000원 정책): 골프백={}, 보스턴백={}, 최종 pdPay={}", targetGolfBagFee, targetBostonFee, pdPay);
			}
		}

		int couponDiscount = cbReceiptInfoVo.getCouponDiscount();
		receiptInfVO.setDiscountAmt(couponDiscount);

		receiptInfVO.setPdDosunPay(pdDosunPay);
		receiptInfVO.setPdDeliPay(pdDeliPay);
		receiptInfVO.setIconFee(iconPay);
		receiptInfVO.setPdEtcPay(extraPrice);
		receiptInfVO.setPdPay(pdPay);

		/**
		 * 241120 요청사항
		 * 골프택배 접수시, 보스턴백 수량에 따른 각 3000원 할인적용.
		 * 편도 /왕복 3000
		 * DelKind : 1 편도, 2 왕복
		 * RcpKind : 0 (모바일, 웹)
		 * + 20250521 코참 추가 RcpKind 5
		 */
		/*if ("0".equals(cbReceiptInfoVo.getRcpKind()) || "5".equals(cbReceiptInfoVo.getRcpKind())) {
			if (cbReceiptInfoVo.getPdCnt() > 0) {
				if (cbReceiptInfoVo.getBostonCnt() > 0) {
					int bostonDiscount = 3000;
					int discount = bostonDiscount * cbReceiptInfoVo.getBostonCnt();
					if ("1".equals(cbReceiptInfoVo.getDelKind())) { // 편도
					} else if ("2".equals(cbReceiptInfoVo.getDelKind())) { // 왕복
						pdPay = pdPay*2;
					}
					receiptInfVO.setDiscount(pdPay-discount);
					receiptInfVO.setDiscountTotal(discount);
				}
			}
		}*/

	}

	/**
	 * 왕복(1차/2차) 분할결제 건의 "편도 기준 정가"를 역산
	 *
	 * delKind를 일시적으로 "1"(편도)로 바꿔 calculateIconFee를 호출:
	 * iconPay(아이콘 수수료)는 편도/왕복에 따라 금액이 다르게 계산 됨(8,000원 vs 4,500원 등),
	 * round_trip_discount_amt(편도-왕복 차액) 산출 시 운임(amtArray)뿐 아니라 수수료도
	 * 편도 기준으로 재계산해야 정확한 정가 차이가 나옴
	 * (extraPrice는 delKind와 무관하게 동일하므로 그대로 사용)
	 *
	 * @param dosun1      도서산간 여부 플래그 (1차: dosun1, 2차: dosun2 — 호출 시 해당 분기 값 전달)
	 * @param amtArray    {기본운임, 스코어운임(편도), 특판가(왕복)}
	 * @param bostonCnt   보스턴백 수량
	 * @param extraPrice  할증운임 (편도/왕복 공통, 그대로 합산)
	 * @return 편도였다면 부과됐을 정가(운임+할증+수수료 합계)
	 */
	private int getOnewayPdPay(CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo, int dosun1, int[] amtArray, int bostonCnt, int extraPrice) {
		String origDelKind = cbReceiptInfoVo.getDelKind();
		cbReceiptInfoVo.setDelKind("1"); // delKind를 편도("1")로 변경해서 조회
		int onewayIconFee = calculateIconFee(cbReceiptInfoVo, sendGolfInfVo, recvGolfInfVo);
		cbReceiptInfoVo.setDelKind(origDelKind); // 원복

		int onewayBasePay;
		if ( dosun1 >= 1 ) {
			onewayBasePay = ((amtArray[0]+ amtArray[1])* cbReceiptInfoVo.getPdCnt()) + ((bostonAmt + bostonDosunAmt) * bostonCnt);
		} else {
			onewayBasePay = (amtArray[1]* cbReceiptInfoVo.getPdCnt()) + (bostonDosunAmt * bostonCnt);
		}
		int onewayTotal = onewayBasePay + extraPrice + onewayIconFee;
		return onewayTotal;
	}

	public int getExtraPrice(String pdIndivPrice) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("PD_DELI_GUBUN", "1"); // payType은 고정 현불택배
		paramMap.put("PD_INDIV_PRICE", pdIndivPrice);
		try {
			logger.info(gson.toJson(paramMap));
			EgovMap apiMap = callUrl(getApiUrl("GetExtraCharge"), gson.toJson(paramMap));

			logger.info(apiMap.toString());

			return Integer.parseInt(apiMap.get("pdExtraCharge").toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	// 아이콘 수수료 수수료*수량으로 수정 20210517
	public int calculateIconFee(CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {
		int sumIconFee = 0;
		int sendGolfFee = 0;
		int recevGolfFee = 0;
		int pdCnt = cbReceiptInfoVo.getPdCnt();
		int bostonCnt = 0;
		int sendApiGolfFee = 0;
		int sendApiBostonFee = 0;
		int recvApiGolfFee = 0;
		int recvApiBostonFee = 0;

        //20260326 팬스타크루즈 추가
		if("2".equals(cbReceiptInfoVo.getRcpKind()) || "0".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind()) || "4".equals(cbReceiptInfoVo.getRcpKind())
				|| "5".equals(cbReceiptInfoVo.getRcpKind()) || "9".equals(cbReceiptInfoVo.getRcpKind())) { //스마트스코어 + 코참추가
			bostonCnt = cbReceiptInfoVo.getBostonCnt();
		}

		if ( cbReceiptInfoVo.getSendGolfSeq() > 0) {
			//스마트스코어 접수
			if("2".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind())|| "4".equals(cbReceiptInfoVo.getRcpKind())) {
				sendApiGolfFee = apiFee(cbReceiptInfoVo.getDelKind(), "G");
				sendApiBostonFee = apiFee(cbReceiptInfoVo.getDelKind(), "B");
			}else {
				if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
					sendApiGolfFee = sendGolfInfVo.getFee1();
					sendApiBostonFee = sendGolfInfVo.getFee7();
				}else {
					sendApiGolfFee = sendGolfInfVo.getFee4();
					sendApiBostonFee = sendGolfInfVo.getFee10();
				}
			}

			sendGolfFee = (sendApiGolfFee * pdCnt) + (sendApiBostonFee * bostonCnt);
			System.out.println("샌드" +sendGolfFee);
		}

		if ( cbReceiptInfoVo.getRecvGolfSeq() > 0) {
			//스마트스코어 접수
			if("2".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind()) || "4".equals(cbReceiptInfoVo.getRcpKind())) {
				recvApiGolfFee = apiFee(cbReceiptInfoVo.getDelKind(), "G");
				System.out.println("리시브에이피아이" + recvApiGolfFee);
				recvApiBostonFee = apiFee(cbReceiptInfoVo.getDelKind(), "B");
				System.out.println("리시브에이피아이보스톤" + recvApiBostonFee);
			}else {
				if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
					recvApiGolfFee = recvGolfInfVo.getFee1();
					recvApiBostonFee = recvGolfInfVo.getFee7();
				}else {
					recvApiGolfFee = recvGolfInfVo.getFee4();
					recvApiBostonFee = recvGolfInfVo.getFee10();
				}

			}
			recevGolfFee = (recvApiGolfFee * pdCnt) + (recvApiBostonFee * bostonCnt);
			System.out.println("리시브" +recevGolfFee);
		}

		if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
			if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				sumIconFee = (8000 * pdCnt) + (bostonAmt * bostonCnt) ;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() > 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				// 출발지가 골프장일 경우
				sumIconFee = sendGolfFee;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() > 0 ) {
				sumIconFee = recevGolfFee;

			} else {
				sumIconFee = sendGolfFee < recevGolfFee ? sendGolfFee : recevGolfFee;
			}
		} else {
			if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				sumIconFee = (4500 * pdCnt) + (3000 * bostonCnt) ;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() > 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				// 출발지가 골프장일 경우
				sumIconFee = sendGolfFee;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() > 0 ) {
				sumIconFee = recevGolfFee;

			} else {
				sumIconFee = sendGolfFee < recevGolfFee ? sendGolfFee : recevGolfFee;
			}
		}
		return sumIconFee;
	}

	public int apiFee(String delKind, String gubun) {
		int apiFee = 8000;

		//보스턴백
		if("B".equals(gubun)) {
			apiFee = 6000;
			if("2".equals(delKind)) {
				apiFee = 3000;
			}
		}else {
			if("2".equals(delKind)) {
				apiFee = 4500;
			}
		}
		return apiFee;
	}
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

		_gDao.insert("golfHistory.insertHistory", golfHistoryVO);
	}

	public EgovMap gsonCallHttp(String urlAddr, String param) throws Exception{

		HttpURLConnection con;
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("charset", "utf-8");
        con.setDoInput(true);
        con.setDoOutput(true);

        try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
		    os.write(param);
		    os.flush();
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}
		con.disconnect();

		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(sb.toString(), EgovMap.class);

		return rtnMap;
	}

	public EgovMap gsonCallHttps(String urlAddr, String param) throws Exception{

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HttpURLConnection con;
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("charset", "utf-8");
		con.setDoInput(true);
		con.setDoOutput(true);

		try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
			os.write(param);
			os.flush();
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}
		con.disconnect();

		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(sb.toString(), EgovMap.class);

		return rtnMap;
	}

	/**
	 * api주소 분기 처리
	 * @param urlAddr, param
	 * @throws Exception
	 */

	public EgovMap callUrl(String urlAddr, String param) throws Exception{
		if(super.getConfig().getWebMode().equals("local")){
			return gsonCallHttp(urlAddr, param);
		}else{
			return gsonCallHttps(urlAddr, param);
		}

	}

	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */

	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(super.getConfig().getWebMode().equals("op") ? "https://kdapi.kditlab.com:447/RndGolf/" : "https://kdapi.kditlab.com:444/RndGolf/").append(url);
		return sb.toString();
	}

	/**
	 * 골프접수 수정
	 * @param url
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GolfReceiptInfVO> updateReceipt(List<CbReceiptInfoVO> list) throws Exception {
		IconPayStatus iconPayStatus = null;
		GolfReceiptInfVO infVo = new GolfReceiptInfVO();
		List<GolfReceiptInfVO> result = new ArrayList<>();

		// 리스트로 들어온 각각의 데이터에 관해 수정작업
		for ( CbReceiptInfoVO cbReceiptInfoVO : list ) {
			infVo.setRcpNumber(cbReceiptInfoVO.getRcpNumber());
			infVo = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpNumber", infVo);
			if (cbReceiptInfoVO.getDiTotalPay() != null) {
				infVo.setPayAmt(cbReceiptInfoVO.getDiTotalPay());
			}
			// 골프접수 상태는 모든 정보 수정 가능(주소가 바뀌면 금액도 업데이트 됨)
			// 결제가 완료되어서 픽업요청 상태인 경우 일부정보만 수정 가능
			// 택배 접수가 되면 모든 정보 수정 불가

			if ( infVo == null ) {

				logger.info("접수번호를 찾을 수 없습니다.");

			} else if ( RcpInfStat.RECEIPT_CANCEL.code().equals(infVo.getRcpInfStat()) || RcpInfStat.PICKUP_CANCEL.code().equals(infVo.getRcpInfStat()) || RcpInfStat.DELIVERY_CANCEL.code().equals(infVo.getRcpInfStat()) ){
				// 골프취소, 픽업취소, 택배취소
				logger.info("이미 삭제된 접수번호 입니다. 확인 부탁드립니다.");

			} else if ( RcpInfStat.DELIVERY.code().equals(infVo.getRcpInfStat()) || RcpInfStat.COMPLETE.code().equals(infVo.getRcpInfStat()) || RcpInfStat.PICKUP.code().equals(infVo.getRcpInfStat()) ){
				// 택배접수, 배송완료, 픽업요청 - 수정불가
				logger.info("운송장번호가 발급되어 수정이 불가합니다. 영업소 프로그램으로 확인 부탁드립니다.");

			} else if ( RcpInfStat.RECEIPT.code().equals(infVo.getRcpInfStat()) || RcpInfStat.WAIT.code().equals(infVo.getRcpInfStat()) ){

				// 결제가 완료된 골프접수 건은 일부정보만 수정 가능
				// CUS_SEND_NM, CUS_SEND_TELNUM, CUS_RE_NM, CUS_RE_TELNUM, PD_MEMO
				if (IconPayStatus.PAID.getPayStat().equals(infVo.getPayStat()) && infVo.getDelKind().equals(list.get(0).getDelKind())) {
					// 골프접수 이고 결제 상태가 결제
					if ( cbReceiptInfoVO.getCusReceiptNm() != null && !"".equals(cbReceiptInfoVO.getCusReceiptNm()) ) infVo.setCusReceiptNm(cbReceiptInfoVO.getCusReceiptNm());
					if ( cbReceiptInfoVO.getCusSendNm() != null && !"".equals(cbReceiptInfoVO.getCusSendNm()) ) infVo.setCusSendNm(cbReceiptInfoVO.getCusSendNm());
					if ( cbReceiptInfoVO.getCusSendTelnum() != null && !"".equals(cbReceiptInfoVO.getCusSendTelnum()) ) infVo.setCusSendTelnum(cbReceiptInfoVO.getCusSendTelnum());
					if ( cbReceiptInfoVO.getCusReNm() != null && !"".equals(cbReceiptInfoVO.getCusReNm()) ) infVo.setCusReNm(cbReceiptInfoVO.getCusReNm());
					if ( cbReceiptInfoVO.getCusReTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReTelNum()) ) infVo.setCusReTelnum2(cbReceiptInfoVO.getCusReTelNum());
					if ( cbReceiptInfoVO.getPdMemo() != null && !"".equals(cbReceiptInfoVO.getPdMemo()) ) infVo.setPdMemo(cbReceiptInfoVO.getPdMemo());

					// 아이콘택배 → 골프전산 결제 상태 수정
					String payStat = ""; // IconPayStatus 의 payStat 참조
					if (cbReceiptInfoVO.getPayStatus() != null && !cbReceiptInfoVO.getPayStatus().isEmpty()) {
						iconPayStatus = IconPayStatus.valueOf(cbReceiptInfoVO.getPayStatus());
						if (IconPayStatus.isPaidOrCancelled(iconPayStatus)) {
							payStat = iconPayStatus.getPayStat();
							final String rcpInfStat = iconPayStatus.getRcpInfStat();
							infVo.setRcpInfStat(rcpInfStat);

							if (IconPayStatus.CANCELLED.equals(iconPayStatus)
									&& cbReceiptInfoVO.getCanReason() != null) {
								infVo.setCanReason(cbReceiptInfoVO.getCanReason());
							}
						}
					}

					// 공통 할인 계산
					applyDiscountAndFees(infVo, cbReceiptInfoVO);
					_gDao.update("golfReceiptInf.updateSelective", infVo);

					if (!"".equals(payStat)) { // cbReceiptInfoVO.getPayStatus() 가 없으면 비어있음
						if (IconPayStatus.CANCELLED.equals(iconPayStatus)) {
							// 기존 편도 데이터 payAmt 변경
							GolfReceiptPayVO payVO = new GolfReceiptPayVO();
							payVO.setPayAmt(infVo.getPayAmt());
							payVO.setRcpInfSeq(infVo.getRcpInfSeq());
							payVO.setPayStat(payStat);
							_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", payVO);
						}
					}

					String company = "카카오";
					if(cbReceiptInfoVO.getRcpKind().equals("2")) {
						company = "스마트 스코어";
					}else if(cbReceiptInfoVO.getRcpKind().equals("0")){
						company = "아이콘";
					}else if(cbReceiptInfoVO.getRcpKind().equals("5")){
						company = "코참";
					}
					//히스토리 등록
					String historyMemo = company + " 골프 수정";
					insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
					result.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
				} else if (IconPayStatus.UNPAID.getPayStat().equals(infVo.getPayStat()) || IconPayStatus.WAIT.getPayStat().equals(infVo.getPayStat())) {
					String historyMemo = "스마트 스코어 골프 수정";

					if(infVo.getRcpKind().equals("1")) { //카카오
						historyMemo = "카카오 골프 수정";
					}

					logger.info("receive 수정정보 ", new Gson().toJson(cbReceiptInfoVO));
					logger.info("결제 대기 카카오 골프 수정 ");
					if ( cbReceiptInfoVO.getSendGolfSeq() != 0 ) infVo.setSendGolfSeq(cbReceiptInfoVO.getSendGolfSeq());
					if ( cbReceiptInfoVO.getCusSendNm() != null && !"".equals(cbReceiptInfoVO.getCusSendNm()) ) infVo.setCusSendNm(cbReceiptInfoVO.getCusSendNm());
					if ( cbReceiptInfoVO.getCusSendTelnum() != null && !"".equals(cbReceiptInfoVO.getCusSendTelnum()) ) infVo.setCusSendTelnum(cbReceiptInfoVO.getCusSendTelnum());
					if ( cbReceiptInfoVO.getCusSendZip() != null && !"".equals(cbReceiptInfoVO.getCusSendZip()) ) infVo.setCusSendZip(cbReceiptInfoVO.getCusSendZip());
					if ( cbReceiptInfoVO.getCusSendAddr1() != null && !"".equals(cbReceiptInfoVO.getCusSendAddr1()) ) infVo.setCusSendAddr1(cbReceiptInfoVO.getCusSendAddr1());
					if ( cbReceiptInfoVO.getCusSendAddr2() != null && !"".equals(cbReceiptInfoVO.getCusSendAddr2()) ) infVo.setCusSendAddr2(cbReceiptInfoVO.getCusSendAddr2());

					if ( cbReceiptInfoVO.getCusReNm() != null && !"".equals(cbReceiptInfoVO.getCusReNm()) ) infVo.setCusReNm(cbReceiptInfoVO.getCusReNm());
					if ( cbReceiptInfoVO.getCusReTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReTelNum()) ) infVo.setCusReTelnum2(cbReceiptInfoVO.getCusReTelNum());
					if ( cbReceiptInfoVO.getCusReZip() != null && !"".equals(cbReceiptInfoVO.getCusReZip()) ) infVo.setCusReZip(cbReceiptInfoVO.getCusReZip());
					if ( cbReceiptInfoVO.getCusReAddr1() != null && !"".equals(cbReceiptInfoVO.getCusReAddr1()) ) infVo.setCusReAddr1(cbReceiptInfoVO.getCusReAddr1());
					if ( cbReceiptInfoVO.getCusReAddr2() != null && !"".equals(cbReceiptInfoVO.getCusReAddr2()) ) infVo.setCusReAddr2(cbReceiptInfoVO.getCusReAddr2());

					if ( cbReceiptInfoVO.getPdCnt() != 0 ) infVo.setPdCnt(cbReceiptInfoVO.getPdCnt()); // 골프백 수량
					//if ( cbReceiptInfoVO.getBostonCnt() != 0 ) infVo.setBostonCnt(cbReceiptInfoVO.getBostonCnt()); // 보스턴백 수량
					if ( cbReceiptInfoVO.getPdIndivPrice() != null && !"".equals(cbReceiptInfoVO.getPdIndivPrice()) ) infVo.setPdIndivPrice(cbReceiptInfoVO.getPdIndivPrice()); // 개별단가
					if ( cbReceiptInfoVO.getPdGolfCnt() != null && !"".equals(cbReceiptInfoVO.getPdGolfCnt()) ) infVo.setPdGolfCnt(cbReceiptInfoVO.getPdGolfCnt()); // 골프채 수량

					if ( cbReceiptInfoVO.getPdMemo() != null && !"".equals(cbReceiptInfoVO.getPdMemo()) ) infVo.setPdMemo(cbReceiptInfoVO.getPdMemo());
					//receiptInfVO.setPicReqDt(null); // 배송예정일이라 함 - 접수시에는 입력하지 않음
					if ( cbReceiptInfoVO.getPicReqDt() != null && !"".equals(cbReceiptInfoVO.getPicReqDt()) ) infVo.setPicReqDt(cbReceiptInfoVO.getPicReqDt()); // 픽업희망일

					// 접수자이름
					if ( cbReceiptInfoVO.getCusReceiptNm() != null && !"".equals(cbReceiptInfoVO.getCusReceiptNm()) ) infVo.setCusReceiptNm(cbReceiptInfoVO.getCusReceiptNm());
					// 접수자전화번호
					if ( cbReceiptInfoVO.getCusReceiptTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReceiptTelNum()) ) infVo.setCusReceiptTelNum(cbReceiptInfoVO.getCusReceiptTelNum());
					preInsertReceiptInfo(infVo, cbReceiptInfoVO);

					// 공통 할인 계산
					applyDiscountAndFees(infVo, cbReceiptInfoVO);

					// 아이콘택배 → 골프전산 결제 상태 수정
					String payStat = ""; // IconPayStatus 의 payStat 참조
					if (cbReceiptInfoVO.getPayStatus() != null && !cbReceiptInfoVO.getPayStatus().isEmpty()) {
						iconPayStatus = IconPayStatus.valueOf(cbReceiptInfoVO.getPayStatus());
						if (IconPayStatus.isPaidOrCancelled(iconPayStatus)) {
							payStat = iconPayStatus.getPayStat();
							final String rcpInfStat = iconPayStatus.getRcpInfStat();
							infVo.setRcpInfStat(rcpInfStat);
						}
					}
					_gDao.update("golfReceiptInf.updateSelective", infVo);

					// 기존 편도 데이터 payAmt 변경
					GolfReceiptPayVO payVO = new GolfReceiptPayVO();
					payVO.setPayAmt(infVo.getPdPay());
					payVO.setRcpInfSeq(infVo.getRcpInfSeq());
					if (!"".equals(payStat)) {
						payVO.setPayStat(payStat);
					}
					_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", payVO);

					//히스토리 등록
					insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);

					if ( "2".equals(infVo.getDelKind()) && "1".equals(list.get(0).getDelKind()) ) {
						// 추가 챗봇에서는 왕복->편도로 수정이 가능함

						// 해당 RCP_NUMBER에 해당하는 ROW의 RCP_SEQ와 일치하는 GOLF_RECEIPT_MST의 DEL_KIND = 1
						GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
						mstVO.setDelKind("1");
						mstVO.setRcpSeq(infVo.getRcpSeq());
						_gDao.update("golfReceiptMst.updateSelective", mstVO);

						// 변경해야 할 타겟이 아닌 ROW 골프 취소
						if ( "S".equals(infVo.getSrType()) ) {
							infVo.setSrType("R");
						} else {
							infVo.setSrType("S");
						}
						GolfReceiptInfVO cancelInfVo = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo);
						cancelInfVo.setRcpInfStat(RcpInfStat.RECEIPT_CANCEL.code());
						cancelInfVo.setPrdType(infVo.getPrdType());
						_receiptService.updateReceiptInfSel(cancelInfVo, false);

						// 리턴을 위해 원래대로 변경
						if ( "S".equals(infVo.getSrType()) ) {
							infVo.setSrType("R");
						} else {
							infVo.setSrType("S");
						}

						// 취소한 건에 대한 pay table 변경 - USE_YN='N'
						GolfReceiptPayVO cancelPayVO = new GolfReceiptPayVO();
						cancelPayVO.setRcpInfSeq(cancelInfVo.getRcpInfSeq());
						cancelPayVO.setUseYn("N");
						_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", cancelPayVO);

						// 기존 편도 데이터 order id 변경 - ORDER_ID= GRP로 시작하는 그룹코드가 아닌 rcpNumber
						// prefix "GRP" → "ORD" 로 변경 ex) ORD26060911050003
						GolfReceiptPayVO modifyPayVO = new GolfReceiptPayVO();
						if (!infVo.getOrderId().startsWith("ORD")) {
							modifyPayVO.setOrderId(infVo.getRcpNumber());
						}
						modifyPayVO.setRcpInfSeq(infVo.getRcpInfSeq());
						_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", modifyPayVO);

						// 모두 처리 이후 편도<->왕복 변경시 CHANGE_DELTYPE_YN = 'Y'
						infVo.setChangeDeltypeYn("Y");
						_gDao.update("golfReceiptInf.updateSelective", infVo);

						return (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectDetailByRcpSeq", infVo);
					} else if ( "1".equals(infVo.getDelKind()) && "2".equals(list.get(0).getDelKind()) ) {
						// 추가 챗봇에서는 편도->왕복으로 수정이 가능함
						return changeRoundReceipt(list, infVo);
					}
					result.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
				} else {
					logger.info("결제 완료 건중 수정할 수 없는 데이터가 포함되어 있거나 결제 취소건이라 수정이 불가능합니다. ");
					logger.info("또는 골프 접수 상태에서 편도/왕복은 수정이 불가능합니다. ");
				}
			} else {
				logger.info("기타 사유로 수정 처리 불가");
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private List<GolfReceiptInfVO> changeRoundReceipt(List<CbReceiptInfoVO> list, GolfReceiptInfVO infVo) throws Exception {
		// TODO Auto-generated method stub
		// 해당 RCP_NUMBER에 해당하는 ROW의 RCP_SEQ와 일치하는 GOLF_RECEIPT_MST의 DEL_KIND = 2
		GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
		mstVO.setDelKind("2");
		mstVO.setRcpSeq(infVo.getRcpSeq());
		_gDao.update("golfReceiptMst.updateSelective", mstVO);

		String company = "스마트스코어";

		if(infVo.getRcpKind().equals("1")) { //카카오
			company = "카카오";
		}

		// 새로운 ROW 추가 ( 단, 새로운 ROW에 대한 데이터는 챗봇에서 요청하는 것으로 판단 )
		GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();
		receiptInfVO.setRcpSeq(infVo.getRcpSeq());
		receiptInfVO.setRcpInfStat(infVo.getRcpInfStat()); // 접수상태 - 접수대기
		receiptInfVO.setSrType("R");
		receiptInfVO.setPayType("0"); // 배송지불타입(현불)
		receiptInfVO.setPdNm("국제골프택배기본");
		receiptInfVO.setPdPack("왕복");

		receiptInfVO.setSendGolfSeq(list.get(1).getSendGolfSeq());
		receiptInfVO.setCusSendNm(list.get(1).getCusSendNm());
		receiptInfVO.setCusSendTelnum(list.get(1).getCusSendTelnum());
		receiptInfVO.setCusSendZip(list.get(1).getCusSendZip());
		receiptInfVO.setCusSendAddr1(list.get(1).getCusSendAddr1());
		receiptInfVO.setCusSendAddr2(list.get(1).getCusSendAddr2());

		receiptInfVO.setRecvGolfSeq(list.get(1).getRecvGolfSeq());
		receiptInfVO.setCusReNm(list.get(1).getCusReNm());
		receiptInfVO.setCusReTelnum2(list.get(1).getCusReTelNum());
		receiptInfVO.setCusReZip(list.get(1).getCusReZip());
		receiptInfVO.setCusReAddr1(list.get(1).getCusReAddr1());
		receiptInfVO.setCusReAddr2(list.get(1).getCusReAddr2());

		receiptInfVO.setPdCnt(list.get(1).getPdCnt()); // 골프백 수량
		if("2".equals(infVo.getRcpKind()) || "0".equals(infVo.getRcpKind()) || "5".equals(infVo.getRcpKind())) { //카카오 + 코참
			receiptInfVO.setBostonCnt(list.get(1).getBostonCnt()); // 보스턴백 수량
		}

		receiptInfVO.setPdIndivPrice(list.get(1).getPdIndivPrice()); // 개별단가
		receiptInfVO.setPdGolfCnt(list.get(1).getPdGolfCnt()); // 골프채 수량

		receiptInfVO.setPdMemo(list.get(1).getPdMemo());
		//receiptInfVO.setPicReqDt(null); // 배송예정일이라 함 - 접수시에는 입력하지 않음
		receiptInfVO.setPicReqDt(list.get(1).getPicReqDt()); // 픽업희망일

		receiptInfVO.setRcpKind("1"); // 접수구분 - 카카오
		receiptInfVO.setUserId(list.get(1).getUserId()); // 카카오챗봇ID
		// 접수자이름
		receiptInfVO.setCusReceiptNm(list.get(1).getCusReceiptNm());
		// 접수자전화번호
		receiptInfVO.setCusReceiptTelNum(list.get(1).getCusReceiptTelNum());

		// 편도<->왕복 변환여부 'Y'
		receiptInfVO.setChangeDeltypeYn("Y");

		preInsertReceiptInfo(receiptInfVO, list.get(1));

		_gDao.insert("golfReceiptInf.chatInsertReceiptInf", receiptInfVO);

		//결제금액
		GolfReceiptPayVO payVO = new GolfReceiptPayVO();
		payVO.setPayAmt(receiptInfVO.getPdPay());
		payVO.setRcpInfSeq(receiptInfVO.getRcpInfSeq());
		payVO.setUserId(list.get(1).getUserId());
		payVO.setKakaoPayStat("READY");
		payVO.setKakaoPayMessage("결제요청");
		// PAY TABLE ORDER ID 추가
		payVO.setOrderId("GRP"+mstVO.getRcpSeq());
		_gDao.insert("golfReceiptPay.insertReceiptPay", payVO);

		// 기존 편도 데이터 order id 변경
		GolfReceiptPayVO modifyPayVO = new GolfReceiptPayVO();
		modifyPayVO.setOrderId("GRP"+mstVO.getRcpSeq());
		modifyPayVO.setRcpInfSeq(infVo.getRcpInfSeq());
		_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", modifyPayVO);

		//히스토리 등록
		String historyMemo = "카카오 택배접수 왕복";
		if( !"1".equals(list.get(0).getDelKind()) && receiptInfVO.getSrType()!=null ) {
			if(receiptInfVO.getSrType().equals("S")) {
				historyMemo = company + " 택배접수 왕복1차";
			}else if(receiptInfVO.getSrType().equals("R")) {
				historyMemo = company + " 택배접수 왕복2차";
			}
		}
		insertGolfHistory(String.valueOf(receiptInfVO.getRcpNumber()), historyMemo);

		// 모두 처리 이후 편도<->왕복 변경시 CHANGE_DELTYPE_YN = 'Y'
		infVo.setChangeDeltypeYn("Y");
		_gDao.update("golfReceiptInf.updateSelective", infVo);

		// return
		infVo.setSrType(null);
		return (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectDetailByRcpSeq", infVo);
	}

	// 골프 접수 & 결제 완료 건에 관해서 수정가능한 항목만 값이 있는지 체크
	public boolean isUpdateReceipt(CbReceiptInfoVO cbReceiptInfoVo) {
		/*if ( (cbReceiptInfoVo.getSendGolfSeq() != 0)
				|| (cbReceiptInfoVo.getCusSendZip() != null && !"".equals(cbReceiptInfoVo.getCusSendZip()))
				|| (cbReceiptInfoVo.getCusSendAddr1() != null && !"".equals(cbReceiptInfoVo.getCusSendAddr1()))
				|| (cbReceiptInfoVo.getCusSendAddr2() != null && !"".equals(cbReceiptInfoVo.getCusSendAddr2()))
				|| (cbReceiptInfoVo.getSendIslYn() != null && !"".equals(cbReceiptInfoVo.getSendIslYn()))
				|| (cbReceiptInfoVo.getRecvGolfSeq() != 0)
				|| (cbReceiptInfoVo.getCusReZip() != null && !"".equals(cbReceiptInfoVo.getCusReZip()))
				|| (cbReceiptInfoVo.getCusReAddr1() != null && !"".equals(cbReceiptInfoVo.getCusReAddr1()))
				|| (cbReceiptInfoVo.getCusReAddr2() != null && !"".equals(cbReceiptInfoVo.getCusReAddr2()))
				|| (cbReceiptInfoVo.getRecvIslYn() != null && !"".equals(cbReceiptInfoVo.getRecvIslYn()))
				|| (cbReceiptInfoVo.getPdGolfCnt() != null && !"".equals(cbReceiptInfoVo.getPdGolfCnt()))
				|| (cbReceiptInfoVo.getPdIndivPrice() != null && !"".equals(cbReceiptInfoVo.getPdIndivPrice()))
				|| (cbReceiptInfoVo.getPdCnt() != 0)
				|| (cbReceiptInfoVo.getPicReqDt() != null && !"".equals(cbReceiptInfoVo.getPicReqDt()))
				|| (cbReceiptInfoVo.getGameDt() != null && !"".equals(cbReceiptInfoVo.getGameDt()))
				|| (cbReceiptInfoVo.getGameEndDt() != null && !"".equals(cbReceiptInfoVo.getGameEndDt())) ) {*/
		if ( (cbReceiptInfoVo.getSendGolfSeq() == 0) ) {
			logger.info("getSendGolfSeq null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusSendZip() == null || "".equals(cbReceiptInfoVo.getCusSendZip())) {
			logger.info("getCusSendZip null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusSendAddr1() == null || "".equals(cbReceiptInfoVo.getCusSendAddr1())) {
			logger.info("getCusSendAddr1 null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusSendAddr2() == null || "".equals(cbReceiptInfoVo.getCusSendAddr2())) {
			logger.info("getCusSendAddr2 null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getSendIslYn() == null || "".equals(cbReceiptInfoVo.getSendIslYn())) {
			logger.info("getSendIslYn null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getRecvGolfSeq() == 0) {
			logger.info("getRecvGolfSeq null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusReZip() == null || "".equals(cbReceiptInfoVo.getCusReZip())) {
			logger.info("getCusReZip null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusReAddr1() == null || "".equals(cbReceiptInfoVo.getCusReAddr1())) {
			logger.info("getCusReAddr1 null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getCusReAddr2() == null || "".equals(cbReceiptInfoVo.getCusReAddr2())) {
			logger.info("getCusReAddr2 null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getRecvIslYn() == null || "".equals(cbReceiptInfoVo.getRecvIslYn())) {
			logger.info("getRecvIslYn null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getPdGolfCnt() == null || "".equals(cbReceiptInfoVo.getPdGolfCnt())) {
			logger.info("getPdGolfCnt null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getPdIndivPrice() == null || "".equals(cbReceiptInfoVo.getPdIndivPrice())) {
			logger.info("getPdIndivPrice null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}

//		if (cbReceiptInfoVo.getPdCnt() + cbReceiptInfoVo.getBostonCnt() == 0) {
//			logger.info("getPdCnt null");
//			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
//			return false;
//		}

		if (cbReceiptInfoVo.getPicReqDt() == null || "".equals(cbReceiptInfoVo.getPicReqDt())) {
			logger.info("getPicReqDt null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getGameDt() == null || "".equals(cbReceiptInfoVo.getGameDt())) {
			logger.info("getGameDt null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		if (cbReceiptInfoVo.getGameEndDt() == null || "".equals(cbReceiptInfoVo.getGameEndDt())) {
			logger.info("getGameEndDt null");
			logger.info("필요한 수정 데이터가 충분하지 않습니다.");
			return false;
		}
		return true;
	}

	/**
	 * 카카오 골프 취소
	 * @param url
	 * @throws Exception
	 */
	public String cancelReceipt(CbCommonVO params) throws Exception {
		// TODO Auto-generated method stub

		String result = "N";
		GolfReceiptInfVO infVo = new GolfReceiptInfVO();

		if ( (params.getOrderId() == null || "".equals(params.getOrderId())) && (params.getRcpNumber() == null || "".equals(params.getRcpNumber())) ) {
			logger.info("요청 데이터가 비었습니다.");
			return result;
		}

		if ( params.getOrderId() == null || "".equals(params.getOrderId()) || !params.getOrderId().contains("GRP") ) {
			// 편도만 수정
			System.out.println("여기들어오면안되는거잖아");
			infVo = (GolfReceiptInfVO)_gDao.selectOne("golfReceiptInf.selectDetailByRcpNumber", params.getRcpNumber());
			result = cancelReceiptProc(infVo);
		} else if ( params.getOrderId() != null && !"".equals(params.getOrderId()) && params.getOrderId().contains("GRP") ) {

			// order id로 보내고 rcp number를 보내지 않았을 경우 왕복으로 가정 둘다 취소
			List<GolfReceiptInfVO> list = new ArrayList<GolfReceiptInfVO>();
			infVo.setRcpSeq(Integer.parseInt(params.getOrderId().replace("GRP", "")));
			infVo.setSrType("S");
			list.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
			infVo.setSrType("R");
			list.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
			for ( GolfReceiptInfVO infoVO : list ) {
				result = cancelReceiptProc(infoVO);
			}
		}


		/*if ( "S".equals(delKind) ) {
			// 편도
			GolfReceiptInfVO infVo = new GolfReceiptInfVO();
			infVo = (GolfReceiptInfVO)_gDao3.selectOne("golfReceiptInf.selectDetailByRcpNumber", rcpNumber);
			result = cancelReceiptProc(infVo);
		} else {
			// 왕복
			GolfReceiptInfVO infVo = new GolfReceiptInfVO();
			List<GolfReceiptInfVO> list = new ArrayList<GolfReceiptInfVO>();
			infVo.setRcpSeq(Integer.parseInt(rcpNumber));
			infVo.setSrType("S");
			list.add( (GolfReceiptInfVO)_gDao3.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
			infVo.setSrType("R");
			list.add( (GolfReceiptInfVO)_gDao3.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
			for ( GolfReceiptInfVO infoVO : list ) {
				result = cancelReceiptProc(infoVO);
			}
		}*/
		return result;


	}

	// 각 건당 취소처리
	public String cancelReceiptProc(GolfReceiptInfVO infVo) throws Exception {

		if ( infVo == null ) {

			logger.info("접수번호를 찾을 수 없습니다.");
			return "N";
		} else if ( RcpInfStat.RECEIPT_CANCEL.code().equals(infVo.getRcpInfStat()) || RcpInfStat.PICKUP_CANCEL.code().equals(infVo.getRcpInfStat()) || RcpInfStat.DELIVERY_CANCEL.code().equals(infVo.getRcpInfStat()) ){
			// 골프취소, 픽업취소, 택배취소
			logger.info("이미 삭제된 접수번호 입니다. 확인 부탁드립니다.");
			return "N";
		} else if ( RcpInfStat.DELIVERY.code().equals(infVo.getRcpInfStat()) || RcpInfStat.COMPLETE.code().equals(infVo.getRcpInfStat()) ){
			// 택배접수, 배송완료
			logger.info("운송장번호가 발급되어 취소가 불가합니다. 영업소 프로그램으로 확인 부탁드립니다.");
			return "N";
		} else if ( RcpInfStat.PICKUP.code().equals(infVo.getRcpInfStat()) ){
			// 픽업요청 -> 픽업취소
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("RT_NUMBER", infVo.getRtNumber());
			paramMap.put("PD_DELI_EXPT_DT", infVo.getPicReqDt());
			EgovMap apiMap = callUrl(getApiUrl("DeleteGolfDelivery"), gson.toJson(paramMap));

			if(!apiMap.isEmpty()){
				//response code check
				if("200".equals(apiMap.get("code").toString())){
					infVo.setRtNumber(apiMap.get("rtNumber").toString());
					//픽업취소 단계로 변경 및 취소사유 등록
					infVo.setRcpInfStat(RcpInfStat.PICKUP_CANCEL.code());
					//픽업취소 시 입금대기 상태면 결제 상태값은 변경 안함
					_receiptService.updateReceiptInfSel(infVo, !PayStat.STAY.code().equals(infVo.getPayStat()) ? true : false);
					//return toJson(returnVal);
					return "Y";
				}else if("100".equals(apiMap.get("code").toString())){
					//return toJson("error1");
					return "N";
				}else if("300".equals(apiMap.get("code").toString())){
					//return toJson("error3");
					return "N";
				}else{
					//return toJson("error2");
					return "N";
				}
			}else{
				//return toJson("error2");
				return "N";
			}
		} else if ( RcpInfStat.RECEIPT.code().equals(infVo.getRcpInfStat()) || RcpInfStat.WAIT.code().equals(infVo.getRcpInfStat()) ){
			// 골프접수, 접수대기 -> 골프취소
			///골프취소 단계로 변경
			infVo.setRcpInfStat(RcpInfStat.RECEIPT_CANCEL.code());
			_receiptService.updateReceiptInfSel(infVo, false);
			return "Y";
		} else {
			logger.info("기타 사유로 수정 처리 불가");
			return "N";
		}
	}

	// RCP_INF_SEQ 로 검색
	public List<GolfReceiptInfVO> cancelResultList(CbCommonVO params) throws Exception {
		// 처리 이후(수정 혹은 취소) 접수상태 체크를 위한 구문
		List<GolfReceiptInfVO> list = new ArrayList<GolfReceiptInfVO>();
		if ( params.getOrderId() == null || "".equals(params.getOrderId()) || !params.getOrderId().contains("GRP") ) {
			// 편도만
			list.add( (GolfReceiptInfVO)_gDao.selectOne("golfReceiptInf.selectDetailByRcpNumber", params.getRcpNumber()) );
		} else if ( params.getOrderId() != null && !"".equals(params.getOrderId()) && params.getOrderId().contains("GRP") ) {

			// order id로 보내고 rcp number를 보내지 않았을 경우 왕복으로 가정 둘다 취소
			GolfReceiptInfVO infVo = new GolfReceiptInfVO();
			infVo.setRcpSeq(Integer.parseInt(params.getOrderId().replace("GRP", "")));
			infVo.setSrType("S");
			list.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
			infVo.setSrType("R");
			list.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );

		}
		return list;
	}

	public GolfReceiptInfVO selectDetailByRcpSeq(int rcpSeq, String delKind) throws Exception {
		// 접수 이후 리턴값을 위한 구문
		GolfReceiptInfVO infVo = new GolfReceiptInfVO();
		infVo.setRcpSeq(rcpSeq);
		infVo.setSrType("S");
		return (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo);
	}

	public boolean isPartnerGolf(int golfSeq) {
		if (golfSeq <= 0) return false;
		GolfInfVO golfInfo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", (long)golfSeq);
		if (golfInfo == null) return false;
		return golfInfo.getFee1() < 8000;
	}

	private int calculateDosunPay(CbReceiptInfoVO cbVo, GolfReceiptInfVO rcpVo, int bostonDosunAmt, int pdCnt) {
		// 방어 로직 추가
		if (!"Y".equals(rcpVo.getSendIslYn()) && !"Y".equals(rcpVo.getRecvIslYn())) {
			return 0;
		}
		// 기본 계산
		int basePay = (pdCnt * 5000) + (cbVo.getBostonCnt() * bostonDosunAmt);
		// 특수 구간 판별
		boolean isSendJeju = (rcpVo.getCusSendAddr1() != null && rcpVo.getCusSendAddr1().contains("제주"))
				|| (rcpVo.getCusSendAddr2() != null && rcpVo.getCusSendAddr2().contains("제주"));
		boolean isRecvJeju = (rcpVo.getCusReAddr1() != null && rcpVo.getCusReAddr1().contains("제주"))
				|| (rcpVo.getCusReAddr2() != null && rcpVo.getCusReAddr2().contains("제주"));
		boolean isSpecialRoute = (isSendJeju && rcpVo.getRecvGolfSeq() == 211)
				|| (isRecvJeju && rcpVo.getSendGolfSeq() == 211);
		// 특수 구간일 경우 재계산
		if (isSpecialRoute) {
			return (pdCnt * specialRouteGolfDosunAmt) + (cbVo.getBostonCnt() * specialRouteBostonDosunAmt);
		}
		return basePay;
	}

	/**
	 * 할인 및 수수료 계산 공통 로직
	 */
	private void applyDiscountAndFees(GolfReceiptInfVO vo, CbReceiptInfoVO info) {
		// 보스턴백 할인 (아이콘, 코참)
		if (("0".equals(info.getRcpKind()) || "5".equals(info.getRcpKind()))
				&& info.getPdCnt() > 0 && info.getBostonCnt() > 0) {

			if (info.getCouponDiscount() <= 0) {
				int bostonDiscount = 3000;
				int discount = bostonDiscount * info.getBostonCnt();
				int iconPay = vo.getIconFee();

				if ("1".equals(info.getDelKind())) {
					iconPay = iconPay - discount;
				} else if ("2".equals(info.getDelKind())) {
					discount = discount / 2;
					iconPay = iconPay - discount;
				}
				vo.setIconFee(Math.max(0, iconPay));
				vo.setPdPay(vo.getPdPay() - discount);
				vo.setDiscountTotal(vo.getDiscountTotal() + discount);
				logger.info("### [보스턴백 할인] 자동 적용: {}원", discount);
			}
		}

		// 팬스타크루즈 할인
		if ("9".equals(info.getRcpKind())) {
			int bostonDiscount = 2000 * info.getBostonCnt();
			int golfBagDiscount = 3000 * info.getPdCnt();
			int totalDiscount = (bostonDiscount + golfBagDiscount) / 2;
			if ("2".equals(info.getDelKind())) {
				vo.setPdPay(vo.getPdPay() - totalDiscount);
			}
		}

		// 인천공항 추가 수수료
		if ("A".equals(info.getRcpType())) {
			int addPay = 5000;
			int golfAddPay = addPay * info.getPdCnt();
			int iconPay = vo.getIconFee();
			if ("1".equals(info.getDelKind())) {
				iconPay = iconPay + golfAddPay;
			}
			vo.setIconFee(Math.max(0, iconPay));
			vo.setPdPay(vo.getPdPay() + golfAddPay);
		}

		// 쿠폰 할인
		int couponVal = info.getCouponDiscount();
		if (couponVal > 0) {
			vo.setPdPay(Math.max(0, vo.getPdPay() - couponVal));
			vo.setDiscountTotal(vo.getDiscountTotal() + couponVal);
			logger.info("### 쿠폰 할인 적용 완료. 최종금액: {}원, 총할인액: {}", vo.getPdPay(), vo.getDiscountTotal());
		}
	}
}
