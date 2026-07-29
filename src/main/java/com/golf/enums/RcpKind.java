package com.golf.enums;

import lombok.Getter;

@Getter
public enum RcpKind {

	ICON("0", "아이콘"),
	KAKAO("1", "카카오"),
	SMARTSCORE("2", "스마트스코어"),
	HDCARD("3", "현대카드"),
	ZIMCARRY("4", "짐캐리"),
	KORCHAM("5", "대한상의"),
	ICON_RENTAL_WEB("6", "아이콘렌탈"),
	ICON_RENTAL_MOBILE("7", "아이콘렌탈"),
	ICON_RENTAL_ETC("8", "아이콘렌탈"),
	PANSTAR("9", "팬스타크루즈");

	private final String value;
	private final String description;

    RcpKind(String value, String description){
        this.value = value;
        this.description = description;
    }
}
