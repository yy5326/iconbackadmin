package com.golf.chat.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import com.golf.chat.vo.CbCommonResultVO;
import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbGolfInfoVo;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.chat.vo.CbReceiveResultVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/kakao/cbReceive")
@Api(value = "/kakao/cbReceive", description = "골프 택배 접수")

public class CbReceiveController {

	private static final Logger logger = LoggerFactory.getLogger(CbReceiveController.class);

	@Resource
	CbReceiveServiceImpl _receiptService;

	@Resource
	SendTalkServiceImpl _sendTalkService;

	@SuppressWarnings("unchecked")
	@PostMapping("/golfCnt")
    @ApiOperation(value = "골프장 목록 갯수 조회", notes = "골프장 개수 조회")
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "성공", response = CbCommonResultVO.class)
	})
	public JSONObject golfCnt(@RequestBody CbCommonVO cbCommon) throws Exception{
		int cnt = 0;
		int status = 200;

		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");

		try {
			cnt = _receiptService.golfCnt(cbCommon);//(int)_gDao3.selectByKey("cbDelivery.selectByCnt", cbCommon.getUserId());
		}catch(Exception e) {
			status = 500;
			System.out.println("DeliveryCnt Exception  : " + e);
		}finally {
			data.put("status", status);
			data.put("cnt", cnt);
			obj.put("result", data);
			return obj;
		}
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/golfList")
	@ApiOperation(value = "골프장 협력 여부 확인", notes = "<DR-1> 1. 골프장 이름으로 검색, 택배접수 가능한 골프장인지 여부 확인 (가능 여부를 세가지 값으로 구분하여 리턴)\r\n"
			+ "2. DR-2에서 내륙/제주로 일자에 따른 접수 가능 여부 확인 필요하므로 응답값에 포함 필요")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공", response = CbGolfInfoVo.class),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject golfList(@RequestBody CbCommonVO cbCommon) throws Exception{

		List<CbGolfInfoVo> golfList = _receiptService.selectReceiptGolfList(cbCommon);

		JSONObject obj = new JSONObject();
		obj.put("httpStatus","OK");
		JSONArray kakaoArray = new JSONArray();

		for ( CbGolfInfoVo vo : golfList ) {
			JSONObject data = new JSONObject();
			data.put("golfSeq", vo.getGolfSeq());
			data.put("golfName", vo.getGolfName());
			data.put("golfTelnum", vo.getGolfTelnum());
			data.put("golfAddr", vo.getGolfAddr());
			data.put("golfAddr1", vo.getGolfAddr1());
			data.put("golfAddr2", vo.getGolfAddr2());
			data.put("useYn", vo.getUseYn());
			data.put("islYn", vo.getIslYn());
			data.put("zipCode", vo.getZipCode());
			kakaoArray.add(data);
		}

		logger.info("검색 및 데이터 create 성공");
		obj.put("result",kakaoArray);

		return obj;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/receiptProcConfirm")
	@ApiOperation(value = "골프 접수/수정/예상운임비 확인", notes = "<DR-3>")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject receiptProcConfirm(@RequestBody List<CbReceiptInfoVO> list){
		JSONObject obj = new JSONObject();
		obj.put("httpStatus","OK");
		JSONArray kakaoArray = new JSONArray();
		try {

			List<GolfReceiptInfVO> receiptList = _receiptService.receiptProcConfirm(list);
			logger.info("접수요청 확인");
			// 리스트로 결과 요청시
			/*for ( GolfReceiptInfVO vo : receiptList ) {
				JSONObject data = new JSONObject();
				if ( list != null && "2".equals(list.get(0).getDelKind()) ) {
					data.put("cbReceiptNum", vo.getRcpSeq()); // 접수순번 - 왕복시 그룹번호
				} else {
					data.put("cbReceiptNum", vo.getRcpInfSeq()); // 접수순번 - 편도시 발송순번
				}

				data.put("cbPay", vo.getPdPay());
				data.put("cbReceiptStatus", vo.getRcpInfStat()); // 0이 접수대기
				logger.info("챗봇접수번호 : "+vo.getRcpSeq());
				logger.info("결제 금액 : "+vo.getPdPay());
				kakaoArray.add(data);
			}*/

			// 한줄로 결과 요청시
			JSONObject data = new JSONObject();
			if ( list == null ) {
				// 요청한 데이터 없음
				logger.info("요청한 데이터 없음");
				data.put("cbPay", 0);
				data.put("cbReceiptStatus", "-1"); // 0이 접수대기
				data.put("errorMsg", "요청한 데이터 없음");
			} else if ( "2".equals(list.get(0).getDelKind()) && list.size() != 2 ) {
				logger.info("왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)");
				data.put("cbPay", 0);
				data.put("cbReceiptStatus", "-1"); // 0이 접수대기
				data.put("errorMsg", "왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)");
			} else if ( !"1".equals(list.get(0).getDelKind()) && !"2".equals(list.get(0).getDelKind()) ) {
				logger.info("왕복 유형 코드 오류");
				data.put("cbPay", 0);
				data.put("cbReceiptStatus", "-1"); // 0이 접수대기
				data.put("errorMsg", "delKind는 1(편도)이거나 2(왕복)이어야 합니다.");
			} else {
				String regDate = _receiptService.selectDetailByRcpSeq(receiptList.get(0).getRcpSeq(), "S").getRegDate();

				String orderId = ""; // 결제용 묶음코드
				if (list.get(0).getOrderNumber() != null) {
					if ( "2".equals(list.get(0).getDelKind()) ) {
						orderId = "GRP"+receiptList.get(0).getRcpSeq(); // 챗봇접수번호 - 왕복시 그룹번호 참조 create
						data.put("cbPay", receiptList.get(0).getPdPay()+receiptList.get(1).getPdPay());
					} else {
						orderId = receiptList.get(0).getRcpNumber(); // 챗봇접수번호 - 편도시 rcpNumber
						data.put("cbPay", receiptList.get(0).getPdPay());
					}
				}
				data.put("orderId", orderId);

				String rcpNumber = "";
				for(GolfReceiptInfVO vo : receiptList) {
					rcpNumber += vo.getRcpNumber()+"|";
				}

				rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);
				data.put("rcpNumber",rcpNumber);
				//data.put("discount", receiptList.get(0).getDiscount() + receiptList.get(1).getDiscount());

				//data.put("cbReceiptStatus", receiptList.get(0).getRcpInfStat()); // 0이 접수대기
				data.put("regDate", regDate);
				logger.info("챗봇접수번호 : "+rcpNumber);
				logger.info("결제용 묶음코드 : "+orderId);
				logger.info("결제 금액 : "+receiptList.get(0).getPdPay());
				logger.info("접수 상태 : "+receiptList.get(0).getRcpInfStat());
				logger.info("접수 일시 : "+regDate);
				logger.info("할인: {}", receiptList.get(0).getDiscount() );


			}

			kakaoArray.add(data);
			obj.put("result",kakaoArray);

		} catch ( Exception e ) {
			// 영업소 프로그램 및 기타 에러
			e.printStackTrace();
			logger.info("영업소 프로그램 및 기타 에러"+e.getMessage());
			JSONObject data = new JSONObject();
			data.put("cbPay", 0);
			data.put("cbReceiptStatus", "-1"); // 0이 접수대기
			data.put("errorMsg", e.getMessage());
			kakaoArray.add(data);
			obj.put("result",kakaoArray);
		}

		return obj;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/updateReceipt")
	@ApiOperation(value = "접수 수정", notes = "<TT-3-33-331> 접수 상태 체크 후 골프 접수 상태까지 변경 가능, 각 항목 업데이트, 편도<->왕복은 오프라인 연결")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
	      @ApiResponse(code = 311, message = "접수번호 오류")
	      , @ApiResponse(code = 312, message = "접수상태 오류")
	})
	public JSONObject updateReceipt(@RequestBody List<CbReceiptInfoVO> list) throws Exception{

		JSONObject obj = new JSONObject();

		try {
			List<GolfReceiptInfVO> result = _receiptService.updateReceipt(list);

			obj.put("httpStatus","OK");
			/*JSONArray kakaoArray = new JSONArray();

			for ( GolfReceiptInfVO info : result ) {
				JSONObject data = new JSONObject();
				data.put("resultStatus", "Y");
				data.put("cbReceiptStatus", info.getRcpInfStat());
				data.put("rcpNumber", info.getRcpNumber());
				data.put("cbPay", info.getPdPay());
				kakaoArray.add(data);
			}*/

			JSONObject data = new JSONObject();
			String rcpNumber = "";
			int cbPay = 0;
			for(GolfReceiptInfVO vo : result) {
				rcpNumber += vo.getRcpNumber()+"|";
				data.put("cbReceiptStatus", vo.getRcpInfStat());
				cbPay += vo.getPdPay();
				data.put("resultStatus", "Y");
			}
			data.put("cbPay", cbPay);
			rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);
			data.put("rcpNumber", rcpNumber);
			data.put("orderId", result.get(0).getOrderId());
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

	@SuppressWarnings("unchecked")
	@PostMapping("/cancelReceipt")
	@ApiOperation(value = "접수 취소", notes = "<TT-3-34-341> 접수 상태 체크 후 ‘골프 접수’ 상태 + 픽업예정일 이틀 전 24시까지만 취소 가능")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject cancelReceipt(@RequestBody CbCommonVO cbCommon) throws Exception{

		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();

		try {
			String cancelResult = _receiptService.cancelReceipt(cbCommon);
			if ( "Y".equals(cancelResult) ) {
				data.put("resultStatus", cancelResult);
				List<GolfReceiptInfVO> resultList = _receiptService.cancelResultList(cbCommon);
				if ( resultList.size() > 1 ) {
					data.put( "cbReceiptStatus", resultList.get(0).getRcpInfStat()+"|"+resultList.get(0).getRcpInfStat() );
				} else {
					data.put("cbReceiptStatus",  resultList.get(0).getRcpInfStat());
				}
				// 알림톡 전송
				if(!resultList.get(0).getRcpKind().equals("2")) {
					_sendTalkService.sendReceiptCancel(resultList, cbCommon.getOrderId());
				}

			} else {
				data.put("resultStatus", cancelResult);
				data.put("cbReceiptStatus", "error");
			}

		} catch ( Exception e ) {
			data.put("resultStatus", "N");
		}
		obj.put("httpStatus","OK");
		obj.put("result", data);

		return obj;

	}

	@SuppressWarnings("unchecked")
	@PostMapping("/commission")
	@ApiOperation(value = "수수료")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공"),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject iconCommission(@RequestBody CbReceiptInfoVO cbReceiptInfoVo) throws Exception{

		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();

		GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();

		try {
			receiptInfVO.setSendGolfSeq(cbReceiptInfoVo.getSendGolfSeq());
			receiptInfVO.setRecvGolfSeq(cbReceiptInfoVo.getRecvGolfSeq());

			receiptInfVO.setCusSendAddr1(cbReceiptInfoVo.getCusSendAddr1());
			receiptInfVO.setCusSendAddr2(cbReceiptInfoVo.getCusSendAddr2());
			receiptInfVO.setCusReAddr1(cbReceiptInfoVo.getCusReAddr1());
			receiptInfVO.setCusReAddr2(cbReceiptInfoVo.getCusReAddr2());

			_receiptService.preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVo);

			data.put("status", "200");
			data.put("pdDosunPay", receiptInfVO.getPdDosunPay());
			data.put("pdDeliPay", receiptInfVO.getPdDeliPay() + receiptInfVO.getIconFee());
			data.put("pdEtcPay", receiptInfVO.getPdEtcPay());
			data.put("discount", receiptInfVO.getDiscount());
			data.put("pdPay", receiptInfVO.getPdPay());
			data.put("discountTotal", receiptInfVO.getDiscountTotal()); //241127 SG 할인총값 추가.


		} catch ( Exception e ) {
			data.put("status", "500");
			logger.error(e.toString());
		}
		obj.put("httpStatus","OK");
		obj.put("result", data);
		return obj;

	}

}