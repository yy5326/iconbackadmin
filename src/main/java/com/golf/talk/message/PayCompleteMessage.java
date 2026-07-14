package com.golf.talk.message;

import com.golf.enums.PrdType;
import com.golf.enums.TripType;
import com.golf.talk.vo.SendRequestVO;

/**
 * 결제 완료 알림 (oneway_pay, round_pay)
 */
public class PayCompleteMessage {
	/**
	 * 템플릿 생성
	 *
	 * @param startRequestVO
	 * @param roundRequestVO, 편도일 때 null
	 * @param prdType
	 */
	public static String makeForUserByIcon(SendRequestVO startRequestVO, SendRequestVO roundRequestVO, PrdType prdType) {
		final String delKind = startRequestVO.getDelKind();
		final TripType tripType = TripType.fromCode(delKind);
		String template = "";
		switch (prdType) {
			case GOLF:
				template = getGolfTemplate(tripType);
				break;
			case CARRIER:
				template = getCarrierTemplate(tripType);
				break;
			case AIRPORT:
				template = getAirportTemplate(tripType);
				break;
		}

		// #{예약자명}
		final String cusSendNm = startRequestVO.getCusReNm();
		// #{연락처}
		final String receiverNum = startRequestVO.getReceiverNum();
		// #{라운딩일자}
		final String gameDt = startRequestVO.getGameDt();
		// #{픽업예정일}
		final String picReqDt = startRequestVO.getPicReqDt() != null ? startRequestVO.getPicReqDt() : "미정";
		// #{픽업지주소}
		final String cusSendAddr = startRequestVO.getCusSendAddr();
		// #{도착지주소}
		final String cusReAddr = startRequestVO.getCusReAddr();

		String replaceTemplate = template
				.replace("#{예약자명}", cusSendNm)
				.replace("#{연락처}", receiverNum)
				.replace("#{라운딩일자}", gameDt)
				.replace("#{픽업예정일}", picReqDt)
				.replace("#{픽업지주소}", cusSendAddr)
				.replace("#{도착지주소}", cusReAddr);

		if (TripType.ROUND_TRIP.equals(tripType)) {
			// #{회수지주소}
			final String roundCusSendAddr = roundRequestVO.getCusSendAddr();
			// #{회수도착지주소}
			final String roundCusReAddr = roundRequestVO.getCusReAddr();
			// #{총결제금액}
			String payAmt = startRequestVO.getPayAmt();
			replaceTemplate = replaceTemplate
				.replace("#{회수지주소}", roundCusSendAddr)
				.replace("#{회수도착지주소}", roundCusReAddr)
				.replace("#{총결제금액}", payAmt);
		} else { // TripType.ONE_WAY
			// #{총결제금액}
			String payAmt = startRequestVO.getPayAmt();
			replaceTemplate = replaceTemplate
				.replace("#{총결제금액}", payAmt);
		}

		switch (prdType) {
			case GOLF:
				// #{추가할증금액}
				String etcPayAmt = startRequestVO.getEtcPayAmt();
				// #{골프백수량}
				final Integer golfCnt = startRequestVO.getPdCnt();
				// #{보스턴백수량}
				final Integer bostonCnt = startRequestVO.getBostonCnt();
				replaceTemplate = replaceTemplate.replace("#{추가할증금액}", etcPayAmt);
				replaceTemplate = replaceTemplate.replace("#{골프백수량}", golfCnt.toString());
				replaceTemplate = replaceTemplate.replace("#{보스턴백수량}", bostonCnt.toString());
				break;
			case CARRIER:
			case AIRPORT:
				// #{캐리어수량}
				final Integer carrierCnt = startRequestVO.getBoxCnt();
				replaceTemplate = replaceTemplate.replace("#{캐리어수량}", carrierCnt.toString());
				break;
		}
		return replaceTemplate;
	}

	/**
	 * [골프택배] 편도 결제 완료 알림 (v1)
	 * [골프택배] 왕복 결제 완료 알림 (v1)
	 */
	private static String getGolfTemplate(TripType tripType) {
		String template;
		if (TripType.ROUND_TRIP.equals(tripType)) {
			template =
					"[아이콘] 골프택배 결제 완료\n" +
					"\n" +
					"[접수정보]\n" +
					"성함 : #{예약자명}\n" +
					"연락처 : #{연락처}\n" +
					"라운딩일정 : #{라운딩일자}\n" +
					"픽업희망일 : #{픽업예정일}\n" +
					"\n" +
					"[주소지]\n" +
					"픽업지 : #{픽업지주소}\n" +
					"도착지 : #{도착지주소}\n" +
					"회수지 : #{회수지주소}\n" +
					"회수도착지 : #{회수도착지주소}\n" +
					"\n" +
					"[수량]\n" +
					"골프백수량 : #{골프백수량}\n" +
					"보스턴백수량 : #{보스턴백수량}\n" +
					"\n" +
					"총합계 : #{총결제금액}원\n" +
					"(추가할증 : #{추가할증금액}원)";
		} else { // TripType.ONE_WAY
			template =
					"[아이콘] 골프택배 결제 완료\n" +
					"\n" +
					"[접수정보]\n" +
					"성함 : #{예약자명}\n" +
					"연락처 : #{연락처}\n" +
					"라운딩일정 : #{라운딩일자}\n" +
					"픽업희망일 : #{픽업예정일}\n" +
					"\n" +
					"[주소지]\n" +
					"픽업지 : #{픽업지주소}\n" +
					"도착지 : #{도착지주소}\n" +
					"\n" +
					"[수량]\n" +
					"골프백수량 : #{골프백수량}\n" +
					"보스턴백수량 : #{보스턴백수량}\n" +
					"\n" +
					"총합계 : #{총결제금액}원\n" +
					"(추가할증 : #{추가할증금액}원)";
		}
		return template;
	}

	/**
	 * [캐리어] 편도 결제 완료 알림 (v1)
	 * [캐리어] 왕복 결제 완료 알림 (v1)
	 */
	private static String getCarrierTemplate(TripType tripType) {
		String template;
		if (TripType.ROUND_TRIP.equals(tripType)) {
			template =
					"[아이콘] 아이콘 트래블 결제 완료\n" +
					"\n" +
					"[접수정보]\n" +
					"성함 : #{예약자명}\n" +
					"연락처 : #{연락처}\n" +
					"여행일정 : #{여행일자}\n" +
					"픽업희망일 : #{픽업예정일}\n" +
					"\n" +
					"[주소지]\n" +
					"픽업지 : #{픽업지주소}\n" +
					"도착지 : #{도착지주소}\n" +
					"회수지 : #{회수지주소}\n" +
					"회수도착지 : #{회수도착지주소}\n" +
					"\n" +
					"캐리어수량 : #{캐리어수량}\n" +
					"총합계 : #{총결제금액}원";
		} else { // TripType.ONE_WAY
			template =
					"[아이콘] 아이콘 트래블 결제 완료\n" +
					"\n" +
					"[접수정보]\n" +
					"성함 : #{예약자명}\n" +
					"연락처 : #{연락처}\n" +
					"여행일정 : #{여행일자}\n" +
					"픽업희망일 : #{픽업예정일}\n" +
					"\n" +
					"[주소지]\n" +
					"픽업지 : #{픽업지주소}\n" +
					"도착지 : #{도착지주소}\n" +
					"\n" +
					"캐리어수량 : #{캐리어수량}\n" +
					"총합계 : #{총결제금액}원";
		}
		return template;
	}

	/**
	 * [인천공항배송] 편도 결제 완료 알림 (v1)
	 */
	private static String getAirportTemplate(TripType tripType) {
		String template;
//		if (TripType.ROUND_TRIP.equals(tripType)) {
//			template =
//					""; // 왕복 템플릿 없음
//		} else { // TripType.ONE_WAY
			template =
					"[아이콘] 아이콘 트래블 결제 완료\n" +
					"\n" +
					"[접수정보]\n" +
					"성함 : #{예약자명}\n" +
					"연락처 : #{연락처}\n" +
					"여행일정 : #{여행일자}\n" +
					"픽업희망일 : #{픽업예정일}\n" +
					"\n" +
					"[주소지]\n" +
					"픽업지 : #{픽업지주소}\n" +
					"도착지 : #{도착지주소}\n" +
					"\n" +
					"캐리어수량 : #{캐리어수량}\n" +
					"총합계 : #{총결제금액}원";
//		}
		return template;
	}
}
