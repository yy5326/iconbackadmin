package com.golf.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum IconGolfItem {
	// 짐캐리/인천공항 라인업 (RCP_KIND = "4")
	// RCP_KIND, 품목명, 가로, 세로, 높이, 무게, 단가(택배운임+아이콘수수료)
	ZC_GOLF_BAG("4", "인천공항골프백", 40, 40, 160, 15, 25500), // 21000+4500
	ZC_CARRIER("4", "인천공항캐리어", 40, 40, 80, 10, 22500), // 18000+4500
	ZC_BOSTON_BAG("4", "인천공항보스턴백", 40, 40, 80, 10, 10500); // 6000+4500

	private final String rcpKind;
	private final String itemName;
	private final int width;
	private final int vertical;
	private final int height;
	private final int weight;
	private final int price;

	public static IconGolfItem findItem(String rcpKind, String pdNm) {
		return Arrays.stream(values())
				.filter(item -> item.getRcpKind().equals(rcpKind) && item.getItemName().equals(pdNm))
				.findFirst()
				.orElse(ZC_GOLF_BAG); // 기본값
	}

}
