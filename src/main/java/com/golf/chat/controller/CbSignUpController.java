package com.golf.chat.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.golf.chat.vo.CbCommonResultVO;
import com.golf.chat.vo.CbSignUpVO;

import com.golf.chat.service.CbSignUpServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/kakao/cbSignUp")
@Api(value = "/kakao/cbSignUp", description = "회원가입")

public class CbSignUpController {
	
	@Resource
	CbSignUpServiceImpl _kakoSignUpService;
	
	@PostMapping("/registerUserInfo")
	@ApiOperation(value = "회원가입 및 동의 로그 저장", notes = "카카오싱크 완료 후 사용자id, 전화번호, 싱크일시 정보 받아서 회원DB, 서비스 약관 동의로그 저장, 중복등록 체크(SU-1-10, SU-1-11, SU-1-12)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class),
	      @ApiResponse(code = 311, message = "중복등록")
	})
	public JSONObject registerUserInfo(@RequestBody CbSignUpVO cbUserInfoVo) throws Exception{
		int status = 500;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");

		try {	
			status = _kakoSignUpService.insertUser(cbUserInfoVo);
		}catch(Exception e) {
			System.out.println("registerUserInfo Exception  : " + e);				
		}finally {
			data.put("status", status);	
			obj.put("result", data);
			return obj;	
		}
	}
	

	
	@PostMapping("/registerUserUpdate")
	@ApiOperation(value = "회원정보 수정")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "저장 성공", response = CbCommonResultVO.class)
	})
	public JSONObject insertUserUpdate(@RequestBody CbSignUpVO cbUserInfoVo) throws Exception{
		int status = 500;
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");

		try {	
			status = _kakoSignUpService.updatetUser(cbUserInfoVo);
		}catch(Exception e) {
			System.out.println("insertUserUpdate Exception  : " + e);				
		}finally {
			data.put("status", status);	
			obj.put("result", data);
			return obj;	
		}
	}
}
