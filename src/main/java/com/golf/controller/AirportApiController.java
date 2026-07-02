package com.golf.controller;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.golf.chat.service.CbReceiveServiceImpl;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.service.dao.GeneralDAOImpl;


import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.util.StringUtil;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.golf.service.impl.CarrierServiceImpl;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;



/**
 * 인천공항 관련 API 컨트롤러
 *
 */
@RestController
@RequestMapping("/airport")
public class AirportApiController extends PrtController  {

	@Resource
	GeneralDAOImpl _gDao;
	@Resource
	CbReceiveServiceImpl _receiptService;
	@Resource
	CarrierServiceImpl _carrierReceiptService;
	@Resource
	SendTalkServiceImpl _sendTalkService;

	@Resource
	private CommonController common;

	private static final Logger logger = LoggerFactory.getLogger(AirportApiController.class);

	/**
	 * 인천공항 정산 요청
	 * TODO 입국/출국 값에 따른 주소변경.
	 * @param cbReceiptInfoVO
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/commission")
	@ApiOperation(value = "수수료")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공"),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject airportCommission(@RequestBody CbReceiptInfoVO cbReceiptInfoVO) throws Exception{

		logger.debug("##########################################");
		logger.debug("###########  인천공항 정산 요청  ##############");
		logger.debug("##########################################");

		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();

		GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();

		int addPay = 5000; // 인천공항 추가수수료(내륙이지만 도서산간비용으로)

		data.put("status", "500");
		try {
			//1 편도, 2:왕복
			receiptInfVO.setDelKind(cbReceiptInfoVO.getDelKind());

			// 골프장
			receiptInfVO.setSendGolfSeq(cbReceiptInfoVO.getSendGolfSeq());
			receiptInfVO.setRecvGolfSeq(cbReceiptInfoVO.getRecvGolfSeq());

			// 픽업지
			receiptInfVO.setCusSendAddr1(cbReceiptInfoVO.getCusSendAddr1());
			receiptInfVO.setCusSendAddr2(cbReceiptInfoVO.getCusSendAddr2());
			// 도착지
			receiptInfVO.setCusReAddr1(cbReceiptInfoVO.getCusReAddr1());
			receiptInfVO.setCusReAddr2(cbReceiptInfoVO.getCusReAddr2());

			logger.debug("log... {}", receiptInfVO);
			// 캐리어
			if(cbReceiptInfoVO.getCrCnt() > 0) { //TODO CRCNT OR BOXCNT..
				receiptInfVO.setPdCnt(cbReceiptInfoVO.getCrCnt());
				_carrierReceiptService.preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVO);
				logger.debug("캐리어 정산 이후 log... {}", receiptInfVO);
				int carrAddPay = addPay * cbReceiptInfoVO.getCrCnt();
				data.put("status", "200");
				data.put("crDosunPay", receiptInfVO.getPdDosunPay());
				data.put("crDeliPay", receiptInfVO.getPdDeliPay() + receiptInfVO.getIconFee() + carrAddPay);
				data.put("crEtcPay", receiptInfVO.getPdEtcPay());
				data.put("crPay", receiptInfVO.getPdPay() + carrAddPay);
				data.put("crRoundTripDiscountAmt", receiptInfVO.getRoundTripDiscountAmt()); // 편도-왕복 차액
			}
			// 골프백
			if(cbReceiptInfoVO.getPdCnt() > 0) {
				receiptInfVO.setPdCnt(cbReceiptInfoVO.getPdCnt());
				_receiptService.preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVO);
				logger.debug("골프백 정산 이후 log... {}", receiptInfVO);
				int golfAddPay = addPay * cbReceiptInfoVO.getPdCnt();
				data.put("status", "200");
				data.put("pdDosunPay", receiptInfVO.getPdDosunPay());
				data.put("pdDeliPay", receiptInfVO.getPdDeliPay() + receiptInfVO.getIconFee() + golfAddPay);
				data.put("pdEtcPay", receiptInfVO.getPdEtcPay());
				data.put("pdPay", receiptInfVO.getPdPay() + golfAddPay);
				data.put("pdRoundTripDiscountAmt", receiptInfVO.getRoundTripDiscountAmt()); // 편도-왕복 차액
			}

			logger.debug("골프백+캐리어 정산 이후 log... {}", data);
		} catch ( Exception e ) {
			data.put("status", "500");
			logger.error(e.toString());
			e.printStackTrace();
		}
		obj.put("httpStatus","OK");
		obj.put("result", data);
		return obj;

	}

	/**
	 * 인천공항 배송접수
	 * /airport/receiptProcConfirm
	 *
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/receiptProcConfirm")
	public JSONObject airportReceipt(@RequestBody List<CbReceiptInfoVO> list){

		logger.debug("###########  인천공항 정산등록  ##############");

		JSONObject obj = new JSONObject();
		obj.put("httpStatus","OK");
		JSONArray kakaoArray = new JSONArray();
		try {

			//그룹코드 인천공항접수에만 추가. 고정값
			if(!list.isEmpty()) {
				int reqAirSeq = (int) _gDao.selectOne("golfReceipt.getRcpAirSeq", null);
				list.get(0).setRcpSeqNo(reqAirSeq);
				list.get(0).setRcpType("A");
			}

			int cbPay = 0;
			int crPay = 0;
			String orderId = ""; // 결제용 묶음코드
			JSONObject data = new JSONObject();
			data.put("rcpType","A");
			data.put("rcpNumberGolf","");
			data.put("rcpNumberCarrier","");

			List<GolfReceiptInfVO> receiptGolfList  = new ArrayList<>();
			List<GolfReceiptInfVO> receiptCarrierList  = new ArrayList<>();
			List<String> rcpNumbers = new ArrayList<>();

			if(list.get(0).getPdCnt()  > 0) {
				data.put("golfCnt", list.get(0).getPdCnt());
				receiptGolfList = _receiptService.receiptProcConfirm(list);
				if(!receiptGolfList.isEmpty()) {
					// 한줄로 결과 요청시
	//				data.put("rcpNm", "골프백");
					if (list.isEmpty()) {
						// 요청한 데이터 없음
						logger.info("요청한 데이터 없음");
						data.put("cbPay", cbPay);
						data.put("cbReceiptStatus", "-1"); // 0이 접수대기
						data.put("errorMsg", "요청한 데이터 없음");
					} else if ( "2".equals(list.get(0).getDelKind()) && list.size() != 2 ) { // 인천공항 배송은 편도 고정
						logger.info("왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)");
						data.put("cbPay", cbPay);
						data.put("cbReceiptStatus", "-1"); // 0이 접수대기
						data.put("errorMsg", "왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)");
					} else if ( !"1".equals(list.get(0).getDelKind()) && !"2".equals(list.get(0).getDelKind()) ) {
						logger.info("왕복 유형 코드 오류");
						data.put("cbPay", cbPay);
						data.put("cbReceiptStatus", "-1"); // 0이 접수대기
						data.put("errorMsg", "delKind는 1(편도)이거나 2(왕복)이어야 합니다.");
					} else {
						String regDate = _receiptService.selectDetailByRcpSeq(receiptGolfList.get(0).getRcpSeq(), "S").getRegDate();
						// 인천공항 배송은 편도 고정
						if ( "2".equals(list.get(0).getDelKind()) ) {
							cbPay = receiptGolfList.get(0).getPdPay()+receiptGolfList.get(1).getPdPay();
							data.put("cbPayGolf", cbPay);
						} else {
							cbPay = receiptGolfList.get(0).getPdPay();

							data.put("cbDosunPay",receiptGolfList.get(0).getPdDosunPay());
							data.put("cbEtcPay",receiptGolfList.get(0).getPdEtcPay());
							data.put("cbDeliPay",receiptGolfList.get(0).getPdDeliPay());
							data.put("cbFeePay",receiptGolfList.get(0).getIconFee());

							data.put("cbPayGolf", cbPay);
						}

						if (list.get(0).getOrderNumber() != null) {
							orderId = list.get(0).getOrderNumber();
							if ( "2".equals(list.get(0).getDelKind()) ) {
								data.put("sRoundTripDiscountAmt", receiptGolfList.get(0).getRoundTripDiscountAmt()); // 왕복 1차 편도-왕복 차액
								data.put("rRoundTripDiscountAmt", receiptGolfList.get(1).getRoundTripDiscountAmt()); // 왕복 2차 편도-왕복 차액
							}
						} else {
							if ( "2".equals(list.get(0).getDelKind()) ) {
								orderId = "GRP"+receiptGolfList.get(0).getRcpSeq(); // 챗봇접수번호 - 왕복시 그룹번호 참조 create
							} else {
								orderId = receiptGolfList.get(0).getRcpNumber(); // 챗봇접수번호 - 편도시 rcpNumber
							}
						}
						data.put("orderId",orderId);

						String rcpNumber = "";
						for(GolfReceiptInfVO vo : receiptGolfList) {
							rcpNumber += vo.getRcpNumber()+"|";
						}
						rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);

						data.put("rcpNumberGolf",rcpNumber);
						data.put("regDate", regDate);

						rcpNumbers.add(rcpNumber);
						logger.info("챗봇접수번호 : "+rcpNumber);
						logger.info("결제용 묶음코드 : "+orderId);
						logger.info("결제 금액 : "+receiptGolfList.get(0).getPdPay());
						logger.info("접수 상태 : "+receiptGolfList.get(0).getRcpInfStat());
						logger.info("접수 일시 : "+regDate);
						logger.info("할인: {}", receiptGolfList.get(0).getDiscount() );

					}
	//				kakaoArray.add(data);
				}
			}

			if(list.get(0).getBoxCnt()  > 0) {
				data.put("carrCnt", list.get(0).getBoxCnt());
				receiptCarrierList = _carrierReceiptService.receiptProcConfirm(list);
				if(!receiptCarrierList.isEmpty()) {

					String regDate = _receiptService.selectDetailByRcpSeq(receiptCarrierList.get(0).getRcpSeq(), "S").getRegDate();
	//				data.put("rcpNm", "캐리어");
					orderId = StringUtil.isEmpty(orderId) ? receiptCarrierList.get(0).getRcpNumber() : orderId;
					data.put("orderId",orderId);

					String rcpNumber = "";
					for(GolfReceiptInfVO vo : receiptCarrierList) {
						rcpNumber += vo.getRcpNumber()+"|";
					}
					rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);
					data.put("rcpNumberCarrier",rcpNumber);
					rcpNumbers.add(rcpNumber);
					if("2".equals(list.get(0).getDelKind())) {
						crPay = receiptCarrierList.get(0).getPdPay()*2;
						data.put("cbPayCarrier", crPay);
					}else {
						crPay = receiptCarrierList.get(0).getPdPay();
						data.put("cbPayCarrier", crPay);

						data.put("crDosunPay",receiptCarrierList.get(0).getPdDosunPay());
						data.put("crEtcPay",receiptCarrierList.get(0).getPdEtcPay());
						data.put("crDeliPay",receiptCarrierList.get(0).getPdDeliPay());
						data.put("crFeePay",receiptCarrierList.get(0).getIconFee());

					}
					data.put("regDate", regDate);
				}
			}

			String golfNum = String.valueOf(data.get("rcpNumberGolf"));
			String carrNum = String.valueOf(data.get("rcpNumberCarrier"));

			String rcpNumber = StringUtil.isEmpty(golfNum) ? carrNum : golfNum + (StringUtil.isEmpty(carrNum) ? "" : "|" + carrNum);
			data.put("cbPay", cbPay + crPay);
			data.put("rcpNumber", rcpNumber);
			kakaoArray.add(data); //단건
			obj.put("result", kakaoArray);


			// 카톡 결제요청 알림톡
			if(!list.isEmpty() && !kakaoArray.isEmpty()) {
				if("0".equals(list.get(0).getRcpKind()) || "3".equals(list.get(0).getRcpKind())){
					List<GolfReceiptInfVO> receiptList = new ArrayList<GolfReceiptInfVO>();
					receiptList = !receiptGolfList.isEmpty() ? receiptGolfList : receiptCarrierList;
					logger.debug("인천공항 골프백 log... {}", new Gson().toJson(receiptGolfList));
					logger.debug("인천공항 캐리어 log... {}", new Gson().toJson(receiptCarrierList));

					logger.debug("인천공항 이전 log... {}", new Gson().toJson(receiptList));

					if(!receiptList.isEmpty()){
						for (GolfReceiptInfVO receipt : receiptList) {
							receipt.setRcpType("A"); //무조건 A
							receipt.setBoxCnt(list.get(0).getBoxCnt()); // 캐리어수
							if(list.get(0).getBoxCnt() > 0 && list.get(0).getPdCnt() < 1){
								receipt.setPdCnt(0); //골프백수
							}
							receipt.setRcpNumberArray(rcpNumbers);
							receipt.setPdPay(cbPay + crPay);
						}
						logger.debug("인천공항 최종 log... {}", new Gson().toJson(receiptList));
						GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
						mstVO.setGameDt(list.get(0).getGameDt()); // 라운딩 시작일
	//					 아이콘 프로젝트 접수시 결제정보 전송하려면 사용
						_sendTalkService.sendPayRequest(receiptList, mstVO);
					}
				}
			}

		}catch ( Exception e ) {
			e.printStackTrace();
			logger.info("영업소 프로그램 및 기타 에러"+e.getMessage());
			JSONObject data = new JSONObject();
			data.put("cbPay", 0);
			data.put("cbReceiptStatus", "-1"); // 0이 접수대기
			data.put("errorMsg", e.getMessage());
			kakaoArray.add(data);
			obj.put("result", kakaoArray);
		}
		return obj;

	}

	/**
	 * 인천공항 배송접수 수정
	 * /airport/updateReceipt
	 *
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/updateReceipt")
	public JSONObject airportUpdate(@RequestBody  List<CbReceiptInfoVO> list) throws Exception{

		logger.debug(">>>>>>>>>> 인천공항 수정 요청" );

		JSONObject obj = new JSONObject();

		try {
			List<GolfReceiptInfVO> carrierResult = _carrierReceiptService.updateReceipt(list);
			List<GolfReceiptInfVO> golfResult = _receiptService.updateReceipt(list);


			logger.debug("업데이트 골프백 결과 {}", new Gson().toJson(golfResult));
			logger.debug("업데이트 캐리어 결과 {}", new Gson().toJson(carrierResult));
			obj.put("httpStatus","OK");

			JSONObject data = new JSONObject();
			String rcpNumber = "";
			int cbPay = 0;
			for(GolfReceiptInfVO vo : golfResult) {
				rcpNumber += vo.getRcpNumber()+"|";
				data.put("cbReceiptStatus", vo.getRcpInfStat());
				cbPay += vo.getPdPay();
				data.put("resultStatus", "Y");
			}
			data.put("cbPay", cbPay);
			data.put("rcpNumber", rcpNumber);
			data.put("orderId", golfResult.get(0).getOrderId());
			obj.put("result", data);

		} catch ( Exception e ) {
			e.printStackTrace();
			JSONObject data = new JSONObject();
			data.put("resultStatus", "N");
			obj.put("result", data);
		}

		obj.put("httpStatus","OK");

		return obj;

	}
}
