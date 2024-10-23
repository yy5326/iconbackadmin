package com.golf.chat.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.golf.chat.vo.CbLogVO;
import com.golf.chat.vo.CbCommonVO;

import com.golf.service.impl.BaseServiceImpl;

@Service("KakaoLogService")
public class CbLogServiceImpl extends BaseServiceImpl {
	
	public int insertKakaoCb(CbLogVO cbLogVO) {		
		try {
			//챗봇로그 저장
			getDAO().insert("cbLog.insertKakaoCbLog", cbLogVO); //getDAO로 변환

			return 200;
		}catch(Exception e) {			
			//챗봇로그 저장 오류
			System.out.println("insertKakaoCb Exception  : " + e);
			return 500;
		}
	}
	
	//삭제예정
	public String updateKakaoStat(CbCommonVO cbCommon)throws Exception {
		String status = "";

		status = (String)getDAO().selectOne("cbDelivery.selectByStat", cbCommon); //getDAO로 변환
		try {
			if(status!=null) {
				if(status.equals("접수대기")) {
					//골프접수 업데이트
					getDAO().update("cbDelivery.updateStat", cbCommon); //getDAO로 변환
				}
			}else {
				return "접수번호가 없습니다.";
			}

			return status;
		}catch(Exception e) {			
			//골프접수 업데이트 저장 오류
			System.out.println("updateKakaoStat Exception  : " + e);
			return "";
		}
	}
}
