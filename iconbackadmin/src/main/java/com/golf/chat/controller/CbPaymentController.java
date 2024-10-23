package com.golf.chat.controller;

import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbCommonResultVO;
import com.golf.service.model.GolfReceiptPayVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.chat.service.CbPaymentServiceImpl;
import com.golf.chat.service.CbSignUpServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/kakao/cbPayment")
@Api(value = "/kakao/cbPayment", description = "결제")
public class CbPaymentController {

	@Resource
	CbPaymentServiceImpl _kakaoPayMentService;
	
	@Resource
	SendTalkServiceImpl _sendTalkService;
	
	@Autowired
	private GeneralDAOImpl _gDao;
	
	@PostMapping("/paySearch")
	@ApiOperation(value = "아이콘 결제상태 조회", notes = "아이콘 결제상태 조회 (DR-6-60, DR-6-62-620, DR-6-63, TT-3-34-340-3400)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "성공", response = GolfReceiptPayVO.class)
	})
	public JSONObject PaySearch(@RequestBody CbCommonVO cbCommon) throws Exception{
		cbCommon.setRcpNumber("");

		JSONObject obj = new JSONObject();
		JSONArray kakaoArray = new JSONArray();
		obj.put("httpStatus","OK");	

		try {			
			GolfReceiptPayVO vo = (GolfReceiptPayVO)_gDao.selectOne("cbPayment.selectBySearch", cbCommon);
			//접수기본정보
			JSONObject data = new JSONObject();
			data.put("orderId", vo.getOrderId());
			data.put("pdName", vo.getPdName());
			data.put("payStat", vo.getPayStat());
			data.put("payAmt", vo.getPayAmt());
			data.put("taxFree", vo.getTaxFree());
			data.put("vat", vo.getVat());
			data.put("pdCnt", vo.getPdCnt());	
			data.put("bostonCnt", vo.getBostonCnt());			
			data.put("cid", vo.getCid());		
			
			//골프접수인 경우 카카오에는 결제상태를 전달
			if(vo.getRcpInfStat().equals("골프접수")) {
				data.put("rcpInfStat", vo.getPayStat());
			}else {
				data.put("rcpInfStat", vo.getRcpInfStat());
			}					
			
			data.put("userId", vo.getUserId());
			data.put("tid", vo.getTid());
			data.put("kakaoPayStat", vo.getKakaoPayStat());
			data.put("kakaoPayMessage", vo.getKakaoPayMessage());
			data.put("payCanAmt", vo.getPayCanAmt());
			data.put("canTaxFree", vo.getCanTaxFree());
			data.put("canVat", vo.getCanVat());
			
			kakaoArray.add(data);
		}catch(Exception e) {
			System.out.println("PaySearch Exception  : " + e);	
		}finally {
			obj.put("result",kakaoArray);
			return obj;				
		}
	}
	
	@PostMapping("/payTid")
	@ApiOperation(value = "TID 저장", notes = "페이에서 발급받은 TID 업데이트 (DR-6-61)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class)
		, @ApiResponse(code = 311, message = "중복등록")
	})
	public JSONObject payTid(@RequestBody CbCommonVO cbCommon) throws Exception{
		int status = 500;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");	

		try {			
			status = _kakaoPayMentService.updateKakaoCbPayMent(cbCommon);
		}catch(Exception e) {
			System.out.println("payTid Exception  : " + e);
		}finally {
			data.put("status", status);	
			obj.put("result", data);
			return obj;		
		}	
	}
		
	@PostMapping("/payKakao")
	@ApiOperation(value = "카카오페이 결제성공, 취소결과 저장", notes = "카카오페이 결제성공, 결제 취소 결과 저장 (DR-6-62-621, TT-3-34-340-3401)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class)
		, @ApiResponse(code = 311, message = "중복등록")
	})
	public JSONObject payProc(@RequestBody GolfReceiptPayVO golfReceiptPayVO) throws Exception{
		int status = 500;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");	

		try {
			golfReceiptPayVO.setPayGubun("1");
			status = _kakaoPayMentService.insertKakaoCbPayMent(golfReceiptPayVO);
			
			if(golfReceiptPayVO.getPayStat().equals("1")) {
				
				if(status==200) {
					// 접수 완료 알림톡
					_sendTalkService.sendReceiptComplete(golfReceiptPayVO);
				}
			} else if(golfReceiptPayVO.getPayStat().equals("0")) {	
				// 취소전 입금대기 상태로 되돌리는 것으로 파악됨
			} else {
				if(status==200) {
					// 접수 취소 알림톡 - 일단 보내지 않음, 챗봇에서 결제대기 상태로 바꾸는 것에 추가하여 결제취소시 자동 접수취소가 되는 기능이 있는지 파악되지 않음
					// 위의 기능을 빠르게 구현시 결제 취소시 자동 접수취소할대 접수취소 api를 호출하면 된다.
					//_sendTalkService.sendReceiptComplete(golfReceiptPayVO);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("payProc Exception  : " + e);
			status = 500;
		}finally {
			data.put("status", status);	
			obj.put("result", data);
			return obj;		
		}	
	}
		
	@PostMapping("/payKakaoFail")
	@ApiOperation(value = "카카오페이 결제실패", notes = "카카오페이 결제실패 (DR-6-62-622)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class)
	})
	public JSONObject payFailProc(@RequestBody GolfReceiptPayVO golfReceiptPayVO) throws Exception{
		int status = 500;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");	

		try {			
			golfReceiptPayVO.setPayGubun("1");
			status = _kakaoPayMentService.insertFailKakaoCbPayMent(golfReceiptPayVO);
		}catch(Exception e) {
			System.out.println("payFailProc Exception  : " + e);
		}finally {
			data.put("status", status);	
			obj.put("result", data);
			return obj;		
		}		
	}
	
}
