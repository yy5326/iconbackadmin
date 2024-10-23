package com.golf.chat.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.golf.chat.vo.CbSignUpVO;
import com.golf.service.impl.BaseServiceImpl;

@Service("cbSignUpService")
public class CbSignUpServiceImpl extends BaseServiceImpl {

	public int insertUser(CbSignUpVO cbUserInfoVo) {
		int status = (int)getDAO().selectOne("cbSignUp.selectByCnt", cbUserInfoVo.getUserId());
		CbSignUpVO vo = new CbSignUpVO();
		
		try {	
			vo.setUserId(cbUserInfoVo.getUserId());
			vo.setUserTelNum(cbUserInfoVo.getUserTelNum());
			vo.setUserSyncedDate(convertToDate(cbUserInfoVo.getUserSyncedDate()));
			
			if(status==0) {			
				getDAO().insert("cbSignUp.insertCbUserInfo", vo);

				for(int i=0; i<cbUserInfoVo.getUserAgreedDate().length; i++) {					
					vo.setUserTagArray(cbUserInfoVo.getUserTag()[i]);
					vo.setUserAgreedDateArray(convertToDate(cbUserInfoVo.getUserAgreedDate()[i]));
					getDAO().insert("cbSignUp.insertCbUserInfoLog", vo);
				}
				
				//정상등록
				status = 200;
			}else {
				getDAO().update("cbSignUp.updateCbUserInfo", vo);
				
				//기존 사용자 있음
				status = 311;
				status = 200;
			}
			
			return status;
		}catch(Exception e) {			
			//회원가입 오류
			if(!super.getConfig().getWebMode().equals("op")) {
				System.out.println("CbUserInfo Exception  : " + e);	
			}
			return 500;
		}
	}


	public int updatetUser(CbSignUpVO cbUserInfoVo) {
		int status = 500;
		CbSignUpVO vo = new CbSignUpVO();
		
		try {
			vo.setUserId(cbUserInfoVo.getUserId());
			vo.setUserTelNum(cbUserInfoVo.getUserTelNum());
			getDAO().update("cbSignUp.updateCbUserInfo", vo);
				
			//정상수정
			status = 200;
			
			return status;
		}catch(Exception e) {			
			//회원가입 오류
			if(!super.getConfig().getWebMode().equals("op")) {
				System.out.println("CbUserInfo Exception  : " + e);	
			}
			return 500;
		}
	}
}
