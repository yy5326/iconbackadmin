package com.golf.talk.vo;

import lombok.Data;

public @Data class KakaoInfoVO {
	
	private Integer kakaoInfoSq;	// primary key
	
	// ----------카카오 고정값 start------------
	private String userName;		//관리자ID
	private String clientId;		//clientId
	private String password;		//패스워드
  	
  	private String tokenUrl;		//토큰url
  	private String sendUrl;			//카카오 발송 URL
  	private String senderKey;		//sender_key
  	private String version;
  	// ----------카카오 고정값 end--------------
  	
  	private String token;			//토큰
  	
  	private String mode;			//서버구분
  	private String regDate;
}
