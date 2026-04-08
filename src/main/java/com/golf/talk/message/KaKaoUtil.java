package com.golf.talk.message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.golf.service.model.GolfReceiptInfVO;
import com.golf.talk.vo.SendRequestVO;
import com.golf.util.HolidayUtil;

public class KaKaoUtil {

	private static final Logger logger = LoggerFactory.getLogger(KaKaoUtil.class);

	public static String getPayLimitDate(String picReqDt) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		cal.setTime(format.parse(picReqDt));
		return HolidayUtil.getBeforeNotHoliday(cal, format);
	}

	public static String getReceiverTelNum(GolfReceiptInfVO receiptInfoVO) {
		if ( receiptInfoVO.getCusReceiptTelNum() == null || "".equals(receiptInfoVO.getCusReceiptTelNum()) ) {
			logger.info("신청자 연락처 없음");
			return receiptInfoVO.getCusSendTelnum();
		}
		// 2023-12-06 요청으로 아이콘에서는 발송자에게 알림톡 + 교참
		if( "0".equals(receiptInfoVO.getRcpKind()) || "5".equals(receiptInfoVO.getRcpKind())) {
			return receiptInfoVO.getCusSendTelnum();
		}
		return receiptInfoVO.getCusReceiptTelNum();
	}

	public static String getReceiver(GolfReceiptInfVO receiptInfoVO) {
		if ( receiptInfoVO.getCusReceiptNm() == null || "".equals(receiptInfoVO.getCusReceiptNm()) ) {
			logger.info("신청자 성함 없음");
			return receiptInfoVO.getCusSendNm();
		}
		// 2023-12-06 요청으로 아이콘에서는 발송자에게 알림톡 + 교참
		if( "0".equals(receiptInfoVO.getRcpKind()) || "5".equals(receiptInfoVO.getRcpKind())) {
			logger.info("요기 : " + receiptInfoVO.getCusSendNm());

			return receiptInfoVO.getCusSendNm();
		}
		return receiptInfoVO.getCusReceiptNm();
	}

	public static void getTempleteForEtc(String etc, SendRequestVO sendVo) {
		// 결제 완료, 알림톡 유저, 편도 + 교참
		if ( "0".equals(sendVo.getRcpKind()) || "5".equals(sendVo.getRcpKind()) || "9".equals(sendVo.getRcpKind() )) {
			// 아이콘 접수 템플릿
			sendVo.setEtc1(etc+"_icon");

			// 3(캐리어)면 별도 메시지 탬플릿
			if(sendVo.getPrdType() == 3) {
				sendVo.setEtc1(etc+"_Carrier");
			}

			// 5 인천공항 별도 메시지 탬플릿
			if(sendVo.getPrdType() == 5) {
				sendVo.setEtc1(etc+"_Airport");
			}
		} else if ( "1".equals(sendVo.getRcpKind()) ) {
			// 챗봇 접수 템플릿
			sendVo.setEtc1(etc);
		} else if ( "2".equals(sendVo.getRcpKind()) ) {
			// 어플 접수 템플릿
			sendVo.setEtc1(etc+"_icon");
		} else if ( "3".equals(sendVo.getRcpKind()) ) {
			// 현대카드 접수 템플릿
			sendVo.setEtc1(etc+"_hdcard");
		} else {
			sendVo.setEtc1(etc);
		}
	}

	public static void getIconTempleteForEtc(String etc, SendRequestVO sendVo) {
		if(sendVo.getPrdType() == 3) {
			sendVo.setEtc1(etc + "_Carrier");
		}else if(sendVo.getPrdType() == 5) {
			sendVo.setEtc1(etc + "_Airport");
		}else {
			sendVo.setEtc1(etc);
		}
	}
}
