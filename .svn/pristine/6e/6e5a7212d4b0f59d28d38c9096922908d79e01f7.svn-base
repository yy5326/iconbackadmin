package com.golf.talk.message;

import com.golf.talk.vo.SendRequestVO;

public class PayExpireMessage {
	
	public static String getOneWayMessage(SendRequestVO sendRequestVO) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 결제 요청\n\n");

		sb.append("안녕하세요. "+sendRequestVO.getReceiver()+" 고객님 골프택배 "+sendRequestVO.getRcpNumber()+" 에 대한 결제가 아직 이뤄지지 않았습니다.\n\n");

		sb.append("픽업예정일 "+sendRequestVO.getPicReqDt()+"이 다가오고 있으니 챗봇 접수내역에서 결제 진행해주시기 바랍니다.\n\n");

		sb.append("추가 문의사항이 있으시면 고객센터 1544-3044로 문의부탁드립니다.\n");
		return sb.toString();
	}
	
	public static String getRoundMessage(SendRequestVO startRequestVO, SendRequestVO roundRequestVO) {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 결제 요청\n\n");
		if ( roundRequestVO != null ) {
			sb.append("안녕하세요. "+startRequestVO.getReceiver()+" 고객님 골프택배 "+startRequestVO.getRcpNumber()+","+roundRequestVO.getRcpNumber()+" 에 대한 결제가 아직 이뤄지지 않았습니다.\n\n");
		} else {
			sb.append("안녕하세요. "+startRequestVO.getReceiver()+" 고객님 골프택배 "+startRequestVO.getRcpNumber()+" 에 대한 결제가 아직 이뤄지지 않았습니다.\n\n");
		}

		sb.append("픽업예정일 "+startRequestVO.getPicReqDt()+"이 다가오고 있으니 챗봇 접수내역에서 결제 진행해주시기 바랍니다.\n\n");

		sb.append("추가 문의사항이 있으시면 고객센터 1544-3044로 문의부탁드립니다.\n");
		return sb.toString();
	}

}
