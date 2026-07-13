package com.golf.talk.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.talk.message.KaKaoUtil;
import com.golf.talk.message.PayExpireMessage;
import com.golf.talk.vo.SendRequestVO;
import com.golf.util.HolidayUtil;

@Service("payExpireSchedule")
public class KakaoPayExpireSchedulerServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(KakaoTokenSchedulerServiceImpl.class);
	
	@Resource(name="globalProperty")
	GlobalProperty configBean;

	@Resource
	private GeneralDAOImpl _gDao;
	
	@Resource
	SendTalkServiceImpl _sendTalkService;
	
	public String getPid() throws IOException { byte[] bo = new byte[100]; String[] cmd = {"bash", "-c", "echo $PPID"}; Process p = Runtime.getRuntime().exec(cmd); while (p.getInputStream().read(bo) == 0); return new String(bo).trim(); }

	
	public void payExpireExecute() {
		
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat holidayFormat = new SimpleDateFormat("yyyyMMdd");
			
			//cal.set(Calendar.YEAR, 2021);
			//cal.set(Calendar.MONTH, 4);
			//cal.set(Calendar.DATE, 18);
			logger.info("configBean.getWebMode() : " + configBean.getWebMode());
			logger.info("pid :: " + getPid());
			if ( configBean.getWebMode().equals("op") ) {
				logger.info("pid :: " + getPid());
				// 공휴일일 경우 실행하지 않는다.
				if ( !HolidayUtil.isHoliday(holidayFormat.format(cal.getTime())) ) {
					logger.info("결제만료 리스트 start");
					List<GolfReceiptInfVO> payExpireList = getPayExpireList(cal, holidayFormat);
					logger.info("결제만료 리스트 end"+payExpireList);
					
					logger.info("결제만료 메세지 전송 start");
					if ( payExpireList != null) sendPayExpireMessage(payExpireList);
					logger.info("결제만료 메세지 전송 end");
				} else {
					logger.info("공휴일 스케줄러 실행하지 않습니다.");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("결제만료 알림톡 에러 : "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	// 결제만료 데이터 리스트
	@SuppressWarnings("unchecked")
	public List<GolfReceiptInfVO> getPayExpireList(Calendar cal, SimpleDateFormat holidayFormat) throws Exception {
		
		List<String> rcpSeqArr = null;
		/*String intervalValue = "-1"; // 오늘이 공휴일 하루전날(내일이 공휴일)이 아닐 경우 픽업일-1일이 오늘인 데이터
		List<String> rcpSeqArr = null;
		if ( HolidayUtil.isHoliday(holidayFormat.format(cal.getTime())) ) {
			// 오늘이 공휴일 하루전날(내일이 공휴일)일 경우 픽업일-2일이 오늘인 데이터
			intervalValue = "-2";
		}*/
		Map<String, Object> searchMap = new HashMap<String, Object>();
		searchMap.put("now", holidayFormat.format(cal.getTime()));
		
		String intervalValue = Integer.toString(HolidayUtil.diffNowAndHoilday(cal, holidayFormat));
		searchMap.put("intervalValue", intervalValue);
		
		rcpSeqArr = (List<String>) _gDao.selectList("golfReceiptInf.selectPayExpireRcpSeqList", searchMap);
		if ( rcpSeqArr != null && rcpSeqArr.size() > 0 ) {
			searchMap.put("rcpSeqArr", rcpSeqArr);
			return (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectPayExpireList", searchMap);
		} else {
			return null;
		}
	}
	
	// GolfReceiptInfVO 데이터로 결제만료 알림톡 전송
	public void sendPayExpireMessage(List<GolfReceiptInfVO> payExpireList) throws Exception {
		
		/*for ( GolfReceiptInfVO receiptInfoVO : payExpireList ) {
			List<SendRequestVO> list = new ArrayList<SendRequestVO>();
			SendRequestVO sendVo = new SendRequestVO();
			
			//sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			//sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명
			sendVo.setReceiverNum("01047630148"); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)+KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자명
			
			sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			//sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			//sendVo.setCusSendTelNum2(receiptInfoVO.getCusSendTelnum2());
			//sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			//sendVo.setCusSendAddr(receiptInfoVO.getCusSendAddr1()+receiptInfoVO.getCusSendAddr2());
			//sendVo.setCusReAddr(receiptInfoVO.getCusReAddr1()+receiptInfoVO.getCusReAddr2());
			//sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			//sendVo.setRPicReqDt(null);
			//sendVo.setRCusReAddr(null);
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setEtc1("hdexp_0008");
			
			list.add(sendVo);
			String message = PayExpireMessage.getMessage(list.get(0));
			_sendTalkService.sendPayExpire(list, message);
		}*/
		
		List<SendRequestVO> sendRequestList = new ArrayList<SendRequestVO>();
		
		for ( GolfReceiptInfVO receiptInfoVO : payExpireList ) {
			
			boolean duplicateRound = false;
			
			List<SendRequestVO> list = new ArrayList<SendRequestVO>();
			SendRequestVO sendVo = new SendRequestVO();
			
			sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명
			//sendVo.setReceiverNum("01047630148"); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			//sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)+KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자명
			
			sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			//sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			//sendVo.setCusSendTelNum2(receiptInfoVO.getCusSendTelnum2());
			//sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			//sendVo.setCusSendAddr(receiptInfoVO.getCusSendAddr1()+receiptInfoVO.getCusSendAddr2());
			//sendVo.setCusReAddr(receiptInfoVO.getCusReAddr1()+receiptInfoVO.getCusReAddr2());
			//sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			//sendVo.setRPicReqDt(null);
			//sendVo.setRCusReAddr(null);
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setRcpSeq(receiptInfoVO.getRcpSeq());
			sendVo.setEtc1("hdexp_0008");
			
			for ( int i = 0 ; i < sendRequestList.size() ; i++ ) {
				
				if ( sendRequestList.get(i).getRcpSeq() == receiptInfoVO.getRcpSeq() ) {
					// 기존 add 한 list element 의 왕복건으로 처리
					duplicateRound = true;
					try {
						sendRequestList.get(i).setTalkMessage(PayExpireMessage.getRoundMessage(sendRequestList.get(i), sendVo));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("sendPayExpireMessage - 기존 add 한 list element 의 왕복건으로 처리 오류");
						e.printStackTrace();
					}
					break;
				}
			}
			
			if ( !duplicateRound ) {
				// 기존과 겹치지 않는 건으로 처리				
				if ( "1".equals(receiptInfoVO.getDelKind()) ) {
					try {
						sendVo.setTalkMessage(PayExpireMessage.getOneWayMessage(sendVo));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("sendPayExpireMessage - 기존과 겹치지 않는 건으로 처리 편도 오류");
						e.printStackTrace();
					}
				} else {
					try {
						sendVo.setTalkMessage(PayExpireMessage.getRoundMessage(sendVo, null));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("sendPayExpireMessage - 기존과 겹치지 않는 건으로 처리 왕복 오류");
						e.printStackTrace();
					}
				}
				
				sendRequestList.add(sendVo);
			}
		}
		
		_sendTalkService.sendTalk(sendRequestList);
		
	}

}
