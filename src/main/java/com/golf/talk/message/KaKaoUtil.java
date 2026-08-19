package com.golf.talk.message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.golf.enums.KakaoTalkEtcType;
import com.golf.enums.PrdType;
import com.golf.enums.RcpKind;
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
		// 2023-12-06 요청으로 아이콘에서는 발송자에게 알림톡 + 코참
		if (RcpKind.ICON.getValue().equals(receiptInfoVO.getRcpKind()) || RcpKind.KORCHAM.getValue().equals(receiptInfoVO.getRcpKind())) {
			return receiptInfoVO.getCusSendTelnum();
		}
		return receiptInfoVO.getCusReceiptTelNum();
	}

	public static String getReceiver(GolfReceiptInfVO receiptInfoVO) {
		if ( receiptInfoVO.getCusReceiptNm() == null || "".equals(receiptInfoVO.getCusReceiptNm()) ) {
			logger.info("신청자 성함 없음");
			return receiptInfoVO.getCusSendNm();
		}
		// 2023-12-06 요청으로 아이콘에서는 발송자에게 알림톡 + 코참
		if (RcpKind.ICON.getValue().equals(receiptInfoVO.getRcpKind()) || RcpKind.KORCHAM.getValue().equals(receiptInfoVO.getRcpKind())) {
			logger.info("요기 : " + receiptInfoVO.getCusSendNm());

			return receiptInfoVO.getCusSendNm();
		}
		return receiptInfoVO.getCusReceiptNm();
	}

	public static void getTemplateForEtc(String etc, SendRequestVO sendVo) {
		// 결제 완료, 알림톡 유저, 편도 + 코참
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

	public static void getIconTemplateForEtc(String etc, SendRequestVO sendVo) {
		if(sendVo.getPrdType() == 3) {
			sendVo.setEtc1(etc + "_Carrier");
		}else if(sendVo.getPrdType() == 5) {
			sendVo.setEtc1(etc + "_Airport");
		}else {
			sendVo.setEtc1(etc);
		}
	}

	/**
	 * KakaoTalkEtcType으로 템플릿 불러오기
	 *
	 * @param prdType, 상품 유형
	 * @param etcType, 수신자 유형
	 */
	public static String getTemplateGrpId(PrdType prdType, KakaoTalkEtcType etcType) {
		final String PREFIX = "icexp";
		final String PRODUCT = KakaoTalkEtcType.VBANK_PAY.equals(etcType) ? "common" : prdType.name().toLowerCase();
		final String PROCESS = etcType.name().toLowerCase();

		// 템플릿 그룹 ID 형식 : icexp_{유형}_{업무}_v{버전}
		String templateGrpId = String.format("%s_%s_%s", PREFIX, PRODUCT, PROCESS);
		return templateGrpId;
	}
}
