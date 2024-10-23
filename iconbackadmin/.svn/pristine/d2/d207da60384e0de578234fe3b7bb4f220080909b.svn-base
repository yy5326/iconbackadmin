package com.golf.talk.message;

import com.golf.talk.vo.SendRequestVO;

public class FileUploadMessage {
	
	public static String getMessage(SendRequestVO sendRequestVO) {

		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 사진업로드 요청\n\n");
		
		sb.append("예약자 : " + sendRequestVO.getCusSendNm() + "\n\n");

		sb.append("운송장번호 : " + sendRequestVO.getPdBarcode() + "\n\n");

		sb.append("안녕하세요. "+sendRequestVO.getCusSendNm()+" 고객님 접수 건 으로 픽업 진행한 골프백에 대해서 골프채 수량이 파악될 수 있도록 내부 1장, 외부 1장 사진을 촬영하셔서 해당 정보에 업로드 요청드립니다.\n\n");

		sb.append("업로드 해주시는 사진은 운송 중 파손이나 분실 발생 시 비교자료로 사용되기 때문에 꼭 업로드 부탁드립니다.");

		return sb.toString();
	}

}