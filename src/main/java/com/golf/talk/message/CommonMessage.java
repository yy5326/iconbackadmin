package com.golf.talk.message;

import com.golf.talk.vo.SendRequestVO;

/**
 * [공통] (common)
 */
public class CommonMessage {

	/**
	 * [공통] 가상계좌안내 (v1)
	 */
	public static String makeVBankForUserByIcon(SendRequestVO sendVO) {
		String template = getVBankTemplate();

		// #{고객명}
		final String cusSendNm = sendVO.getReceiver();
		// #{가상계좌}
		final String vbankInfo = sendVO.getVbankInfo();
		// #{총결제금액}
		final String payAmt = sendVO.getVbankPayAmt();
		// #{입금만료일시}
		final String vbankExpDtm = sendVO.getVbankExpDtm();

		final String replaceTemplate = template
				.replace("#{고객명}", cusSendNm)
				.replace("#{가상계좌}", vbankInfo)
				.replace("#{총결제금액}", payAmt)
				.replace("#{입금만료일시}", vbankExpDtm);
		return replaceTemplate;
	}

	/**
	 * [골프택배] 가상계좌 안내 (v2)
	 */
	private static String getVBankTemplate() {
		String template =
				"[아이콘] 가상계좌 안내\n" +
				"\n" +
				"안녕하세요. 아이콘 #{고객명} 고객님 가상계좌 안내드립니다.\n" +
				"안내드린 계좌번호로 입금 부탁드리겠습니다.\n" +
				"\n" +
				"가상계좌 : #{가상계좌}\n" +
				"\n" +
				"총합계 : #{총결제금액}원\n" +
				"\n" +
				"당일 #{입금만료일시}까지 결제되지 않을 경우 자동 취소됩니다.";
		return template;
	}
}
