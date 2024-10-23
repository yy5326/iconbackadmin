package com.golf.chat.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.golf.chat.service.CbLogServiceImpl;
import com.golf.chat.vo.CbLogVO;
import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbCommonResultVO;
import com.golf.chat.vo.CbDeliveryVO;
import com.golf.service.impl.BaseServiceImpl;
import com.golf.service.model.GolfReceiptPayVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


@RestController
@RequestMapping("/kakao/cbLog")
@Api(value = "/kakao/cbLog", description = "카카오 챗봇 로그")
public class CbLogController extends BaseServiceImpl {
	@Resource
	CbLogServiceImpl _kakaoLogService;
		
	@PostMapping("/log")
	@ApiOperation(value = "챗봇 대화로그 적재", notes = "사용자의 발화 적재 (CM-1)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class)
	})
	public JSONObject logProc(@RequestBody CbLogVO cbLogVO) throws Exception{
		int status = 200;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");	

		try {			
			status = _kakaoLogService.insertKakaoCb(cbLogVO);
		}catch(Exception e) {
			System.out.println("payProc Exception  : " + e);
		}finally{
			data.put("status", status);	
			obj.put("result", data);
			return obj;	
		}

					
	}
	
	//삭제예정
	@PostMapping("/stat")
	@ApiOperation(value = "임시 접수상태 업데이트", notes = "접수상태 업데이트 (TP) 삭제예정")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbDeliveryVO.class)
	})
	public JSONObject Stat(@RequestBody CbCommonVO cbCommon) throws Exception{

		String status = "";
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");	

		try {			
			status = _kakaoLogService.updateKakaoStat(cbCommon);			
		}catch(Exception e) {
			System.out.println("Stat Exception  : " + e);
		}finally{
			if(status.equals("접수대기")) {
				data.put("rcpInfStat", "업데이트 완료");
			}else if(status.equals("")) {
				data.put("rcpInfStat", "업데이트 오류");
			}else if(status.equals("접수번호가 없습니다.")) {
				data.put("rcpInfStat", cbCommon.getRcpNumber() + " 접수번호가 없습니다.");
			}else {
				data.put("rcpInfStat", status + " 상태는 변경할 수 없습니다.");
			}			
			
			obj.put("result", data);
			return obj;		
		}
	}
}
