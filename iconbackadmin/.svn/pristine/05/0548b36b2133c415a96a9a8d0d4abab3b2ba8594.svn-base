package com.golf.talk.message;

import com.golf.talk.vo.SendRequestVO;

public class ReceiptUnableMessage {

	public static String getMessage(SendRequestVO sendRequestVO) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 골프택배 접수 불가 안내\n\n");

		sb.append("안녕하세요. "+sendRequestVO.getReceiver()+" 고객님 "+sendRequestVO.getRcpNumber()+" 접수가 불가함을 안내드립니다.\n\n");

		sb.append("접수 불가에 대한 사유에 대해서는 고객센터(1544-3044)로 문의주시기 바랍니다.\n");
		return sb.toString();
	}

}
