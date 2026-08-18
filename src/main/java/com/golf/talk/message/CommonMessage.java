package com.golf.talk.message;

import java.util.ArrayList;
import java.util.List;

import com.golf.payment.constant.GlobalConstant;
import org.json.simple.JSONObject;
import com.golf.talk.vo.SendRequestVO;
import org.springframework.stereotype.Component;

/**
 * [공통] (common)
 */
@Component
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
	 * [공통] 가상계좌 안내 (v2)
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

	/**
	 * [공통] 결제요청안내 (v1)
	 */
	public static String makeRequestPayForUserByIcon(SendRequestVO sendVO) {
		String template = getRequestPayTemplate();

		// #{고객명}
		final String cusSendNm = sendVO.getReceiver();
		// #{주문번호}
		final String orderNumber = sendVO.getOrderNumber();
		// #{총결제금액}
		final String payAmt = sendVO.getVbankPayAmt();

		final String replaceTemplate = template
				.replace("#{고객명}", cusSendNm)
				.replace("#{주문번호}", orderNumber)
				.replace("#{총결제금액}", payAmt);
		return replaceTemplate;
	}

	/**
	 * [공통] 결제요청안내 버튼 (v1)
	 */
	@SuppressWarnings("unchecked")
	public static List<JSONObject> makeRequestPayBtnForUserByIcon(SendRequestVO sendVO) {
		final String orderSeq = sendVO.getOrderNumber().replace(GlobalConstant.ORDER_NUMBER_PREFIX, "");
		final String btnUrl = "https://1con.co.kr/receipt/order_list.do?orderSeq=" + orderSeq;
		List<JSONObject> buttonList = new ArrayList<>();
		JSONObject requestSendMessage = new JSONObject();
		requestSendMessage.put("name","결제하기");
		requestSendMessage.put("type","WL");
		requestSendMessage.put("url_mobile", btnUrl);
		requestSendMessage.put("url_pc", btnUrl);

		buttonList.add(requestSendMessage);

		return buttonList;
	}

	/**
	 * [공통] 결제요청안내 (v2)
	 */
	private static String getRequestPayTemplate() {
		String template =
				"[아이콘] 결제요청안내\n" +
				"\n" +
				"안녕하세요. 아이콘 #{고객명} 고객님\n" +
				"접수하신 상품의 결제 요청드립니다.\n" +
				"\n" +
				"주문번호 : #{주문번호}\n" +
				"총합계 : #{총결제금액}원\n" +
				"\n" +
				"아래 [결제하기] 버튼을 클릭하시어, \n" +
				"로그인 후 [접수현황 > 주문내역] 에서\n" +
				"결제 부탁 드립니다.";
		return template;
	}
}
