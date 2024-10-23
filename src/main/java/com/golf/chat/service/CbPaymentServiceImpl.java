package com.golf.chat.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbDeliveryVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptPayVO;

import com.golf.service.impl.BaseServiceImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;

@Service("KakaoPaymentService")
public class CbPaymentServiceImpl extends BaseServiceImpl {
	@Resource
	GolfReceiptServiceImpl _golfReceiptService;
	
	public int updateKakaoCbPayMent(CbCommonVO cbCommon) {
		GolfReceiptPayVO golfReceiptPayVO = new GolfReceiptPayVO();
		golfReceiptPayVO.setUserId(cbCommon.getUserId());
		golfReceiptPayVO.setOrderId(cbCommon.getOrderId());
		golfReceiptPayVO.setTid(cbCommon.getTid());
		
		int status = (int)getDAO().selectOne("cbPayment.selectByCnt", golfReceiptPayVO);
		String historyMemo = "TID, 사용자ID UPDATE";
		
		try {
			if(status==0) {
				//TID, 사용자ID 부여
				getDAO().update("cbPayment.updateKakaoCbPayMent", golfReceiptPayVO);
				
				//정상등록
				status = 200;
			}else {
				//TID 있음
				historyMemo = "기존 TID, 사용자ID 있음";
				status = 311;
			}
			
			//히스토리 등록 골프접수번호 찾기
			List<GolfReceiptInfVO> rcpNumberList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByKakaoHistory", golfReceiptPayVO.getOrderId());
			
			//히스토리 등록
			for(GolfReceiptInfVO vo : rcpNumberList) {
				_golfReceiptService.insertGolfHistory(String.valueOf(vo.getRcpNumber()), historyMemo);
			}

			return status;
		}catch(Exception e) {			
			//카카오페이 결제 오류
			System.out.println("updateKakaoCbPayMent Exception  : " + e);
			return 500;
		}
	}
	
	public int insertKakaoCbPayMent(GolfReceiptPayVO golfReceiptPayVO) {
		String historyMemo = "카카오페이 결제완료";
		if(golfReceiptPayVO.getPayStat().equals("1")) {	
			//처리결과 메세지 빈칸처리
			golfReceiptPayVO.setKakaoPayMessage("");
		}else if(golfReceiptPayVO.getPayStat().equals("0")) {	
			golfReceiptPayVO.setKakaoPayMessage("READY");
		}else {
			//처리결과 메세지 실패상세메세지로 변경
			golfReceiptPayVO.setKakaoPayMessage(golfReceiptPayVO.getFailDetailMessage());
			historyMemo = "카카오페이 결제취소";
		}

		int status = (int)getDAO().selectOne("cbPayment.selectByCnt", golfReceiptPayVO);
		
		try {
			if(status==0) {
				//결제로그 저장
				golfReceiptPayVO.setCreatedDate(convertToDate(golfReceiptPayVO.getCreatedDate()));
				golfReceiptPayVO.setApprovedDate(convertToDate(golfReceiptPayVO.getApprovedDate()));
				golfReceiptPayVO.setCancelDate(convertToDate(golfReceiptPayVO.getCancelDate()));
				
				getDAO().insert("cbPayment.insertKakaoCbPayMentLog", golfReceiptPayVO);
				
				//결제완료 or 취소 변경
				getDAO().update("cbPayment.updateKakaoCbPayMent", golfReceiptPayVO);
				
				//정상등록
				status = 200;
			}else {
				if(golfReceiptPayVO.getPayStat().equals("1")) {	
					//TID 있음
					historyMemo = "기존 결제완료건 있음";
				}else if(golfReceiptPayVO.getPayStat().equals("0")) {	
					//READY로 변경
					getDAO().update("cbPayment.updateKakaoCbPayMent", golfReceiptPayVO);
					historyMemo = "카카오페이 " + golfReceiptPayVO.getFailDetailMessage();
				}else {
					historyMemo = "기존 결제취소건 있음";
				}
				
				if(golfReceiptPayVO.getPayStat().equals("0")) {	
					status = 200;
				}else{
					status = 311;
				}
			}
			
			//히스토리 등록 골프접수번호 찾기
			List<GolfReceiptInfVO> rcpNumberList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByKakaoHistory", golfReceiptPayVO.getOrderId());
			
			//히스토리 등록
			for(GolfReceiptInfVO vo : rcpNumberList) {
				_golfReceiptService.insertGolfHistory(String.valueOf(vo.getRcpNumber()), historyMemo);
			}

			return status;
		}catch(Exception e) {			
			//카카오페이 결제 오류
			System.out.println("insertKakaoCbPayMent Exception  : " + e);
			return 500;
		}
	}

	
	public int insertFailKakaoCbPayMent(GolfReceiptPayVO golfReceiptPayVO) {
		int status = 200;
		String historyMemo = "카카오페이 결제실패";
		
		//처리결과 메세지 실패상세결과메세지 넣기
		golfReceiptPayVO.setKakaoPayMessage(golfReceiptPayVO.getFailDetailMessage());
		
		try {			
			//결제실패로그 저장
			getDAO().insert("cbPayment.insertKakaoCbPayMentLog", golfReceiptPayVO);

			//카카오상태변경
			getDAO().update("cbPayment.updateKakaoCbPayMent", golfReceiptPayVO);
			
			//히스토리 등록 골프접수번호 찾기
			List<GolfReceiptInfVO> rcpNumberList = (List<GolfReceiptInfVO>)getDAO().selectList("golfReceiptInf.selectByKakaoHistory", golfReceiptPayVO.getOrderId());
			
			//히스토리 등록
			for(GolfReceiptInfVO vo : rcpNumberList) {
				_golfReceiptService.insertGolfHistory(String.valueOf(vo.getRcpNumber()), historyMemo);
			}

			return status;
		}catch(Exception e) {			
			//카카오페이 결제실패 오류
			System.out.println("insertFailKakaoCbPayMent Exception  : " + e);
			return 500;
		}
	}
}
