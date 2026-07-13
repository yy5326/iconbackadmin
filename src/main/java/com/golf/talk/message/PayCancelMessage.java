package com.golf.talk.message;

import com.golf.enums.PrdType;
import com.golf.talk.vo.SendRequestVO;

/**
 * 취소 완료 알림 (oneway_pay, round_pay)
 */
public class PayCancelMessage {
	public static String makeForUserByIcon(SendRequestVO sendRequestVO, PrdType prdType) {
		String template = "";
		switch (prdType) {
			case GOLF:
				template = getGolfTemplate();
			case CARRIER:
				template = getCarrierTemplate();
			case AIRPORT:
				template = getAirportTemplate();
		}

		// #{고객명}
		final String cusSendNm = sendRequestVO.getCusSendNm();
		// #{예약자명}
		final String receiverNm = sendRequestVO.getReceiver();
		// #{연락처}
		final String receiverNum = sendRequestVO.getReceiverNum();
		final String replaceTemplate = template
				.replace("#{고객명}", cusSendNm)
				.replace("#{예약자명}", receiverNm)
				.replace("#{연락처}", receiverNum);

		// #{라운딩일자}, #{여행일자}
		String gameDt = sendRequestVO.getGameDt();
		switch (prdType) {
			case GOLF:
				replaceTemplate.replace("#{라운딩일자}", gameDt);
			case CARRIER:
				// #{캐리어수량}
				final Integer carrierCnt = sendRequestVO.getPdCnt();
				replaceTemplate.replace("#{캐리어수량}", carrierCnt.toString());
				replaceTemplate.replace("#{라운딩일자}", gameDt);
			case AIRPORT:
				replaceTemplate.replace("#{여행일자}", gameDt);
		}
		return replaceTemplate;
	}

	/**
	 * [골프택배] 취소 완료 알림 (v1)
	 */
	private static String getGolfTemplate() {
		String template =
				"[아이콘] 접수 취소 완료\n" +
				"\n" +
				"안녕하세요. 아이콘 #{고객명} 고객님 골프택배 접수가 정상 취소되었습니다.\n" +
				"\n" +
				"예약자 : #{예약자명}\n" +
				"연락처 : #{연락처}\n" +
				"라운딩일정 : #{라운딩일자}\n" +
				"\n" +
				"다음에 다시 이용 부탁드립니다.\n" +
				"고객님께 만족을 드릴 수 있는 아이콘이 되겠습니다. 감사합니다.\n" +
				"환불은 영업일 기준 최대 2~3일 정도 소요됩니다.\n" +
				"\n" +
				"추가 문의사항이 있으시면 고객센터 1544-3044로 문의 부탁드립니다.";
		return template;
	}

	/**
	 * [캐리어] 취소 완료 알림 (v1)
	 */
	private static String getCarrierTemplate() {
		String template =
				"[아이콘] 접수 취소 완료\n" +
				"\n" +
				"안녕하세요. 아이콘 #{고객명} 고객님 캐리어 접수가 정상 취소되었습니다.\n" +
				"\n" +
				"성함 : #{예약자명}\n" +
				"연락처 : #{연락처}\n" +
				"캐리어수량 : #{캐리어수량}\n" +
				"라운딩일정 : #{라운딩일자}\n" +
				"\n" +
				"다음에 다시 이용 부탁드립니다.\n" +
				"고객님께 만족을 드릴 수 있는 아이콘이 되겠습니다. 감사합니다.\n" +
				"환불은 영업일 기준 최대 2~3일 정도 소요됩니다.\n" +
				"\n" +
				"추가 문의사항이 있으시면 고객센터 1544-3044로 문의부탁드립니다.";
		return template;
	}

	/**
	 * [인천공항배송] 취소 완료 알림 (v1)
	 */
	private static String getAirportTemplate() {
		String template =
				"[아이콘] 접수 취소 완료\n" +
				"\n" +
				"안녕하세요. 아이콘 #{고객명} 고객님 인천공항 접수가 정상 취소되었습니다.\n" +
				"\n" +
				"성함 : #{예약자명}\n" +
				"연락처 : #{연락처}\n" +
				"여행일정 : #{여행일자}\n" +
				"\n" +
				"다음에 다시 이용 부탁드립니다.\n" +
				"고객님께 만족을 드릴 수 있는 아이콘이 되겠습니다. 감사합니다.\n" +
				"환불은 영업일 기준 최대 2~3일 정도 소요됩니다.\n" +
				"\n" +
				"추가 문의사항이 있으시면 고객센터 1544-3044로 문의부탁드립니다.";
		return template;
	}
}