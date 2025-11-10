package com.golf.talk.message;

import java.text.ParseException;
import java.util.List;

import com.golf.talk.vo.SendRequestVO;

public class PayRequestMessage {
	static String serviceName = "";
	static String itemName ="";
	/**
	 * 결제요청알림톡(편도) - 스테이징 (테스트) 코드 hdexp_t0001, user 페이지 (운영) 코드 hdexp_0001
	 * @param sendVO
	 * @return
	 * @throws ParseException
	 */
	public static String getOneWayMessage(SendRequestVO sendRequestVO) throws ParseException {

		if ( "0".equals(sendRequestVO.getRcpKind()) ) {
			// 관리자 접수, 편도
			return makeOneWayMessageByIcon(sendRequestVO, sendRequestVO.getPrdType());
		} else if ( "1".equals(sendRequestVO.getRcpKind()) ) {
			// 챗봇 접수, 편도
			return makeOneWayMessageByChat(sendRequestVO);
		} else if ( "2".equals(sendRequestVO.getRcpKind()) ) {
			// 어플 접수, 편도
			return makeOneWayMessageByIcon(sendRequestVO, sendRequestVO.getPrdType());
		} else if ( "3".equals(sendRequestVO.getRcpKind()) ) {

			return hdCardMessageByIcon(sendRequestVO);
		} else {
			// 구분값 없을시
			return makeOneWayMessageByIcon(sendRequestVO, sendRequestVO.getPrdType());
		}
	}


	/**
	 * 현대카드 제휴
	 * 접수 안내 알람
	 * 비즈메시지 템플릿코드 - hdexp_t0001_hdcard
	 * */
	public static String hdCardMessageByIcon(SendRequestVO sendRequestVO) throws ParseException {

		String gameDt = "미정";
		if ( sendRequestVO.getGameDt() != null ) {
			gameDt = sendRequestVO.getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[현대카드x아이콘] 골프택배 접수 내역 및 결제 안내\n\n");

		//sb.append("안녕하세요. "+sendRequestVO.getReceiver()+" 고객님 골프택배 편도 접수가 승인되었습니다. \n");
		sb.append("안녕하세요. "+sendRequestVO.getCusReNm()+" 고객님 골프택배 편도 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n");
		sb.append(KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n");

		sb.append("접수번호 : "+sendRequestVO.getRcpNumber()+"\n");
		sb.append("성함 : "+sendRequestVO.getReceiver()+"\n");
		sb.append("전화번호 : "+sendRequestVO.getReceiverNum()+"\n");
		sb.append("골프백수량 : "+sendRequestVO.getPdCnt()+"\n");
		sb.append("픽업지 : "+sendRequestVO.getCusSendAddr()+"\n");
		sb.append("도착지 : "+sendRequestVO.getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+sendRequestVO.getPicReqDt()+"\n");

		sb.append("총 결제 금액 : "+sendRequestVO.getPdPay()+"원 \n");
		sb.append("현대카드 결제시 결제금액 : "+sendRequestVO.getPdDiscount()+"원 \n");
		sb.append("(물품가액에 따른 추가할증 "+sendRequestVO.getPdEtcPay()+"원 포함금액입니다)\n\n");

		sb.append("내용 확인 후 아래 번호로 카드번호 전달 시 예약 완료 됩니다. \n");
		sb.append("*카드번호 16자리와 유효기간 010-4763-0983로 문자보내주시면 됩니다. \n\n");
		sb.append("*계좌이체 필요시 1544-3044로 연락주셔야 하며, 할인적용은 불가합니다. \n");

		return sb.toString();
	}




	public static String makeOneWayMessageByIcon(SendRequestVO sendRequestVO, int prdType) throws ParseException {
		StringBuffer sb = new StringBuffer();


		if(prdType == 3) {
			serviceName = "아이콘 트래블 ";
			itemName = "캐리어";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+sendRequestVO.getCusReNm()+" 고객님 캐리어  편도 접수가 승인되었습니다. \n");
		}else if(prdType == 5) {
			serviceName = "인천공항 배송";
			itemName = "골프백";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+sendRequestVO.getCusReNm()+" 고객님 인천공항 배송 편도 접수가 승인되었습니다. \n");
		}else {
			serviceName = "골프택배 ";
			itemName = "골프백";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+sendRequestVO.getCusReNm()+" 고객님 골프택배 편도 접수가 승인되었습니다. \n");
		}

		String gameDt = "미정";
		if ( sendRequestVO.getGameDt() != null ) {
			gameDt = sendRequestVO.getGameDt();
		}



		//sb.append("안녕하세요. "+sendRequestVO.getReceiver()+" 고객님 골프택배 편도 접수가 승인되었습니다. \n");

		// 2025.04.24 골프택배 알림톡 메시지 수정으로 캐리어, 인천공항 따로 분리.
		if(prdType == 3 || prdType == 5) {

			sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n");
			sb.append(KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n\n");

			if(prdType == 5 ) {
				sb.append("접수번호 : "+sendRequestVO.getRcpNumbers()+"\n");
			}else {
				sb.append("접수번호 : "+sendRequestVO.getRcpNumber()+"\n");
			}
			sb.append("성함 : "+sendRequestVO.getReceiver()+"\n");
			sb.append("전화번호 : "+sendRequestVO.getReceiverNum()+"\n");
//			sb.append(itemName+"수량 : "+sendRequestVO.getPdCnt()+"\n"); // 캐리어 배송일경우 캐리어와 수량사이에 공백이 있어서 주석처리
            if(prdType == 5 ) {
                sb.append(itemName+"수량 : "+sendRequestVO.getBoxCnt()+"\n");
            }else if(prdType == 3 ) {
                sb.append(itemName+" 수량 : "+sendRequestVO.getPdCnt()+"\n");
            }else if(prdType != 3 ) {
                sb.append("보스턴백수량 : " + sendRequestVO.getBoston() + "\n");
            }
			sb.append("픽업지 : "+sendRequestVO.getCusSendAddr()+"\n");
			sb.append("도착지 : "+sendRequestVO.getCusReAddr()+"\n");

			if(prdType != 3 ) {
				if(prdType == 5 ) {
					sb.append("여행일정 : "+gameDt+"\n");
				}else {
					sb.append("라운딩일자 : "+gameDt+"\n");
				}
				sb.append("픽업예정일 : "+sendRequestVO.getPicReqDt()+"\n\n");

				if(sendRequestVO.getBoston() > 0 && sendRequestVO.getPdDiscount() > 0) { // 241127 보스턴수량
					sb.append("총 결제 금액 : "+sendRequestVO.getPdDiscount()+"원 \n");
				}else {
					sb.append("총 결제 금액 : "+sendRequestVO.getPdPay()+"원 \n");
				}
				sb.append("(물품가액에 따른 추가할증 "+sendRequestVO.getPdEtcPay()+"원 포함금액입니다)\n");
			}else {
				sb.append("여행일자 : "+gameDt+"\n");
				sb.append("픽업예정일 : "+sendRequestVO.getPicReqDt()+"\n\n");

				sb.append("총 결제 금액 : "+sendRequestVO.getPdPay()+"원 \n");
			}


		}else {
			// 2025.04.24 골프택배 알림톡 메시지 수정추가
			sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");
			sb.append("라운딩일자 : "+gameDt+"\n");
			sb.append("픽업예정일 : "+sendRequestVO.getPicReqDt()+"\n\n");
			sb.append("픽업지 : "+sendRequestVO.getCusSendAddr()+"\n");
			sb.append("도착지 : "+sendRequestVO.getCusReAddr()+"\n");
			sb.append("전화번호 : "+sendRequestVO.getReceiverNum()+"\n");
			sb.append("골프백수량 : "+sendRequestVO.getPdCnt()+"\n"); //
			sb.append("보스턴백수량 : "+sendRequestVO.getBoston()+"\n\n");

			if(sendRequestVO.getBoston() > 0 && sendRequestVO.getPdDiscount() > 0) { // 241127 보스턴수량
				sb.append("총 결제 금액 : "+sendRequestVO.getPdDiscount()+"원 \n");
			}else {
				sb.append("총 결제 금액 : "+sendRequestVO.getPdPay()+"원 \n");
			}
			sb.append("(물품가액에 따른 추가할증 "+sendRequestVO.getPdEtcPay()+"원 포함금액입니다)\n\n");
		}


		sb.append("\n결제계좌 : 농협 3010266187691 아이콘(주)\n");
		sb.append("내용 확인 후 위의 결제 계좌로 총 결제 금액 입금 시 예약 완료 됩니다. \n");
		sb.append("*카드결제를 원하시면 카드번호 16자리와 유효기간 \n");
		sb.append("010-4763-0961로 문자보내주시면 됩니다.\n");

		return sb.toString();
	}

	public static String makeOneWayMessageByChat(SendRequestVO sendRequestVO) throws ParseException {
		String gameDt = "미정";
		if ( sendRequestVO.getGameDt() != null ) {
			gameDt = sendRequestVO.getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 골프택배 접수 내역 및 결제 안내\n\n");

		sb.append("안녕하세요. "+sendRequestVO.getReceiver()+" 고객님 골프택배 편도 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(sendRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");

		sb.append("접수번호 : "+sendRequestVO.getRcpNumber()+"\n");
		sb.append("성함 : "+sendRequestVO.getReceiver()+"\n");
		sb.append("전화번호 : "+sendRequestVO.getReceiverNum()+"\n");
		sb.append("골프백수량 : "+sendRequestVO.getPdCnt()+"\n");
		sb.append("픽업지 : "+sendRequestVO.getCusSendAddr()+"\n");
		sb.append("도착지 : "+sendRequestVO.getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+sendRequestVO.getPicReqDt()+"\n");

		sb.append("총 결제 금액 : "+sendRequestVO.getPdPay()+"원 \n");
		sb.append("(물품가액에 따른 추가할증 "+sendRequestVO.getPdEtcPay()+"원 포함금액입니다)\n\n");

		sb.append("★ 여러 접수 건에 대한 일괄결제를 원하시는 경우 1544-3044 고객센터로 문의 주시기 바랍니다.\n");

		return sb.toString();
	}

	public static String getRoundMessage(List<SendRequestVO> list) throws ParseException {
		// TODO Auto-generated method stub
		if ( "0".equals(list.get(0).getRcpKind()) ) {
			// 관리자 접수, 왕복
			return makeRoundMessageByIcon(list, list.get(0).getPrdType());
		} else if ( "1".equals(list.get(0).getRcpKind()) ) {
			// 챗봇 접수, 왕복
			return makeRoundMessageByChat(list);
		} else if ( "2".equals(list.get(0).getRcpKind()) ) {
			// 어플 접수, 왕복
			return makeRoundMessageByIcon(list, list.get(0).getPrdType());
		} else if ( "3".equals(list.get(0).getRcpKind()) ) {

			return hdCardMessageByIconRound(list);
		} else {
			// 구분값 없을시
			return makeRoundMessageByIcon(list, list.get(0).getPrdType());
		}
	}

	/**
	 * 현대카드 제휴
	 * 접수 안내 알람
	 * 비즈메시지 템플릿코드 - hdexp_t0002_hdcard
	 * */
	public static String hdCardMessageByIconRound(List<SendRequestVO> userlist) throws ParseException {

		String gameDt = "미정";
		if ( userlist.get(0).getGameDt() != null ) {
			gameDt = userlist.get(0).getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[현대카드x아이콘] 골프택배 접수 내역 및 결제 안내\n");

		//sb.append("안녕하세요. "+userlist.get(0).getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("안녕하세요. "+userlist.get(0).getCusReNm()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(userlist.get(0).getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");
		sb.append(KaKaoUtil.getPayLimitDate(userlist.get(0).getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n");

		sb.append("접수번호 : "+userlist.get(0).getRcpNumber()+","+userlist.get(1).getRcpNumber()+"\n");
		sb.append("성함 : "+userlist.get(0).getReceiver()+"\n");
		sb.append("전화번호 : "+userlist.get(0).getReceiverNum()+"\n");
		sb.append("골프백수량 : "+userlist.get(0).getPdCnt()+"\n");
		sb.append("픽업지 : "+userlist.get(0).getCusSendAddr()+"\n");
		sb.append("도착지 : "+userlist.get(0).getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+userlist.get(0).getPicReqDt()+"\n");
		sb.append("회수예정일 : "+userlist.get(1).getPicReqDt()+"\n");
		sb.append("회수도착지 : "+userlist.get(1).getCusReAddr()+"\n\n");
		int sumPay = userlist.get(0).getPdPay()+userlist.get(1).getPdPay();
		sb.append("총 결제 금액 : "+sumPay+"원 \n");
		int sumDiscount = userlist.get(0).getPdDiscount() + userlist.get(1).getPdDiscount();
		sb.append("현대카드 결제시 결제금액 : "+sumDiscount+"원 \n");
		int sumEtcPay = userlist.get(0).getPdEtcPay()+userlist.get(1).getPdEtcPay();
		sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");

		sb.append("내용 확인 후 아래 번호로 카드번호 전달 시 예약 완료 됩니다. \n");
		sb.append("*카드번호 16자리와 유효기간 010-4763-0983로 문자보내주시면 됩니다. \n\n");
		sb.append("*계좌이체 필요시 1544-3044로 연락주셔야 하며, 할인적용은 불가합니다. \n");

		return sb.toString();
	}

	public static String makeRoundMessageByIcon(List<SendRequestVO> userlist, int prdType) throws ParseException {
		StringBuffer sb = new StringBuffer();
		String gameDt = "미정";

		if(prdType == 3) {
			serviceName = "아이콘 트래블 ";
			itemName = "캐리어";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n");
			sb.append("안녕하세요. "+userlist.get(0).getCusReNm()+" 고객님 캐리어 왕복 접수가 승인되었습니다. \n");
		}else {
			serviceName = "골프택배 ";
			itemName = "골프백";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n");
			sb.append("안녕하세요. "+userlist.get(0).getCusReNm()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		}

		if ( userlist.get(0).getGameDt() != null ) {
			gameDt = userlist.get(0).getGameDt();
		}

		//sb.append("안녕하세요. "+userlist.get(0).getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");

		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(userlist.get(0).getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");
		sb.append(KaKaoUtil.getPayLimitDate(userlist.get(0).getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n");

		sb.append("접수번호 : "+userlist.get(0).getRcpNumber()+","+userlist.get(1).getRcpNumber()+"\n");
		sb.append("성함 : "+userlist.get(0).getReceiver()+"\n");
		sb.append("전화번호 : "+userlist.get(0).getReceiverNum()+"\n");
		sb.append(itemName+"수량 : "+userlist.get(0).getPdCnt()+"\n");
		if(prdType != 3 ) {
			sb.append("보스턴백수량 : "+userlist.get(0).getBoston()+"\n");
		}

		if(prdType != 3 ) {
			sb.append("픽업지 : "+userlist.get(0).getCusSendAddr()+"\n");
			sb.append("도착지 : "+userlist.get(0).getCusReAddr()+"\n");
			sb.append("라운딩일자 : "+gameDt+"\n");
			sb.append("픽업예정일 : "+userlist.get(0).getPicReqDt()+"\n");
			sb.append("회수예정일 : "+userlist.get(1).getPicReqDt()+"\n");
			sb.append("회수도착지 : "+userlist.get(1).getCusReAddr()+"\n\n");
			int sumPay = userlist.get(0).getPdPay()+userlist.get(1).getPdPay();
			sb.append("총 결제 금액 : "+sumPay+"원 \n");
			int sumEtcPay = userlist.get(0).getPdEtcPay()+userlist.get(1).getPdEtcPay();
			sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");
		}else {
			sb.append("픽업지 : "+userlist.get(0).getCusSendAddr()+"\n");
			sb.append("픽업예정일 : "+userlist.get(0).getPicReqDt()+"\n");
			sb.append("도착지 : "+userlist.get(0).getCusReAddr()+"\n");
			sb.append("여행일자 : "+gameDt+"\n");
			sb.append("회수지 : "+ userlist.get(0).getCusReAddr() +"\n");
			sb.append("회수예정일 : "+userlist.get(1).getPicReqDt()+"\n");
			sb.append("회수도착지 : "+userlist.get(1).getCusReAddr()+"\n\n");

			int sumPay = userlist.get(0).getPdPay()+userlist.get(1).getPdPay();
			sb.append("총 결제 금액 : "+sumPay+"원 \n");

		}


		sb.append("결제계좌 : 농협 3010266187691 아이콘(주)\n");
		sb.append("내용 확인 후 위의 결제 계좌로 총 결제 금액 입금 시 예약 완료 됩니다. \n");
		sb.append("*카드결제를 원하시면 카드번호 16자리와 유효기간 \n");
		sb.append("010-4763-0983로 문자보내주시면 됩니다.\n");

		return sb.toString();
	}

	public static String makeRoundMessageByChat(List<SendRequestVO> userlist) throws ParseException {
		String gameDt = "미정";
		if ( userlist.get(0).getGameDt() != null ) {
			gameDt = userlist.get(0).getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 골프택배 접수 내역 및 결제 안내\n");

		sb.append("안녕하세요. "+userlist.get(0).getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(userlist.get(0).getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");

		sb.append("접수번호 : "+userlist.get(0).getRcpNumber()+","+userlist.get(1).getRcpNumber()+"\n");
		sb.append("성함 : "+userlist.get(0).getReceiver()+"\n");
		sb.append("전화번호 : "+userlist.get(0).getReceiverNum()+"\n");
		sb.append("골프백수량 : "+userlist.get(0).getPdCnt()+"\n");
		sb.append("픽업지 : "+userlist.get(0).getCusSendAddr()+"\n");
		sb.append("도착지 : "+userlist.get(0).getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+userlist.get(0).getPicReqDt()+"\n");
		sb.append("회수예정일 : "+userlist.get(1).getPicReqDt()+"\n");
		sb.append("회수도착지 : "+userlist.get(1).getCusReAddr()+"\n\n");
		int sumPay = userlist.get(0).getPdPay()+userlist.get(1).getPdPay();
		sb.append("총 결제 금액 : "+sumPay+"원 \n");
		int sumEtcPay = userlist.get(0).getPdEtcPay()+userlist.get(1).getPdEtcPay();
		sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");

		sb.append("★ 여러 접수 건에 대한 일괄결제를 원하시는 경우 1544-3044 고객센터로 문의 주시기 바랍니다.\n");

		return sb.toString();
	}

	// hdexp_t0002
	public static String getRoundMessage(SendRequestVO startRequestVO, SendRequestVO roundRequestVO) throws ParseException {
		// TODO Auto-generated method stub
		if ( "0".equals(startRequestVO.getRcpKind()) ) {
			// 관리자 접수, 편도
			return makeRoundMessageByIcon(startRequestVO, roundRequestVO, startRequestVO.getPrdType());
		} else if ( "1".equals(startRequestVO.getRcpKind()) ) {
			// 챗봇 접수, 왕복
			return makeRoundMessageByChat(startRequestVO, roundRequestVO);
		} else if ( "2".equals(startRequestVO.getRcpKind()) ) {
			// 어플 접수, 왕복
			return makeRoundMessageByIcon(startRequestVO, roundRequestVO, startRequestVO.getPrdType());
		} else if ( "3".equals(startRequestVO.getRcpKind()) ) {
			// 현대카드 접수, 왕복
			return makeHdCardMessageByIconRound(startRequestVO, roundRequestVO);
		} else {
			// 구분값 없을시
			return makeRoundMessageByIcon(startRequestVO, roundRequestVO, startRequestVO.getPrdType());
		}
	}

	/**
	 * 현대카드 제휴
	 * 접수 안내 알람
	 * 비즈메시지 템플릿코드 - hdexp_t0002_hdcard
	 * */
	public static String makeHdCardMessageByIconRound(SendRequestVO startRequestVO, SendRequestVO roundRequestVO) throws ParseException {

		String gameDt = "미정";
		if ( startRequestVO.getGameDt() != null ) {
			gameDt = startRequestVO.getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[현대카드x아이콘] 골프택배 접수 내역 및 결제 안내\n");

		//sb.append("안녕하세요. "+userlist.get(0).getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("안녕하세요. "+startRequestVO.getCusReNm()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");
		sb.append(KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n");

		sb.append("접수번호 : "+startRequestVO.getRcpNumber()+","+roundRequestVO.getRcpNumber()+"\n");
		sb.append("성함 : "+startRequestVO.getReceiver()+"\n");
		sb.append("전화번호 : "+startRequestVO.getReceiverNum()+"\n");
		sb.append("골프백수량 : "+startRequestVO.getPdCnt()+"\n");
		sb.append("픽업지 : "+startRequestVO.getCusSendAddr()+"\n");
		sb.append("도착지 : "+startRequestVO.getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+startRequestVO.getPicReqDt()+"\n");
		sb.append("회수예정일 : "+roundRequestVO.getPicReqDt()+"\n");
		sb.append("회수도착지 : "+roundRequestVO.getCusReAddr()+"\n\n");
		int sumPay = startRequestVO.getPdPay()+roundRequestVO.getPdPay();
		sb.append("총 결제 금액 : "+sumPay+"원 \n");
		int sumDiscount = (startRequestVO.getPdDiscount() *2);
		sb.append("현대카드 결제시 결제금액 : "+sumDiscount+"원 \n");
		int sumEtcPay = startRequestVO.getPdEtcPay()+roundRequestVO.getPdEtcPay();
		sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");

		sb.append("내용 확인 후 아래 번호로 카드번호 전달 시 예약 완료 됩니다. \n");
		sb.append("*카드번호 16자리와 유효기간 010-4763-0983로 문자보내주시면 됩니다. \n\n");
		sb.append("*계좌이체 필요시 1544-3044로 연락주셔야 하며, 할인적용은 불가합니다. \n");

		return sb.toString();
	}

	// hdexp_t0002
	public static String makeRoundMessageByIcon(SendRequestVO startRequestVO, SendRequestVO roundRequestVO, int prdType) throws ParseException {
		String gameDt = "미정";

		StringBuffer sb = new StringBuffer();

		if(prdType == 3) {
			serviceName = "아이콘 트래블 ";
			itemName = "캐리어";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+startRequestVO.getCusReNm()+" 고객님 캐리어 왕복 접수가 승인되었습니다. \n");

		}else if(prdType == 5) {
			serviceName = "인천공항 배송";
			itemName = "골프백";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+startRequestVO.getCusReNm()+" 고객님 인천공항 배송 편도 접수가 승인되었습니다. \n");
		}else {

			serviceName = "골프택배 ";
			itemName = "골프백";

			sb.append("[아이콘] "+ serviceName +"접수 내역 및 결제 안내\n\n");
			sb.append("안녕하세요. "+startRequestVO.getCusReNm()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		}
		if ( startRequestVO.getGameDt() != null ) {
			gameDt = startRequestVO.getGameDt();
		}


		//sb.append("안녕하세요. "+startRequestVO.getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");

		// 2025.04.24 골프택배 알림톡 메시지 수정으로 캐리어, 인천공항 따로 분리.
		if(prdType == 3 || prdType == 5) {

			sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");
			sb.append(KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+" 까지 결제가 안되는 경우 자동으로 접수가 취소됩니다.\n");

			sb.append("접수번호 : "+startRequestVO.getRcpNumber()+","+roundRequestVO.getRcpNumber()+"\n");
			sb.append("성함 : "+startRequestVO.getReceiver()+"\n");
			sb.append("전화번호 : "+startRequestVO.getReceiverNum()+"\n");
			sb.append(itemName+"수량 : "+startRequestVO.getPdCnt()+"\n");
			if(prdType != 3 ) {
				sb.append("보스턴백수량 : "+startRequestVO.getBoston()+"\n");
			}

			if(prdType != 3 ) {
				sb.append("픽업지 : "+startRequestVO.getCusSendAddr()+"\n");
				sb.append("도착지 : "+startRequestVO.getCusReAddr()+"\n");
				sb.append("라운딩일자 : "+gameDt+"\n");
				sb.append("픽업예정일 : "+startRequestVO.getPicReqDt()+"\n");
				sb.append("회수예정일 : "+roundRequestVO.getPicReqDt()+"\n");
				sb.append("회수도착지 : "+roundRequestVO.getCusReAddr()+"\n\n");

				if(startRequestVO.getBoston() > 0) { // 241127 보스턴수량
					sb.append("총 결제 금액 : "+startRequestVO.getPdDiscount()+"원 \n");
				}else {
					int sumPay = startRequestVO.getPdPay()+roundRequestVO.getPdPay();
					sb.append("총 결제 금액 : "+sumPay+"원 \n");
				}

				int sumEtcPay = startRequestVO.getPdEtcPay()+roundRequestVO.getPdEtcPay();
				sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n");
			}else {

				sb.append("픽업지 : "+startRequestVO.getCusSendAddr()+"\n");
				sb.append("픽업예정일 : "+startRequestVO.getPicReqDt()+"\n");
				sb.append("도착지 : "+startRequestVO.getCusReAddr()+"\n");
				sb.append("여행일자 : "+gameDt+"\n");
				// 2025.04.25 회수지 데이터 수정
				// startRequestVO.getCusReAddr() => roundRequestVO.getCusSendAddr()
				sb.append("회수지 : "+ roundRequestVO.getCusSendAddr() +"\n");
				sb.append("회수예정일 : "+roundRequestVO.getPicReqDt()+"\n");
				sb.append("회수도착지 : "+roundRequestVO.getCusReAddr()+"\n\n");

				int sumPay = startRequestVO.getPdPay()+roundRequestVO.getPdPay();
				sb.append("총 결제 금액 : "+sumPay+"원 \n");
			}
		}else {
			// 2025.04.24 골프택배 알림톡 메시지 수정추가
			sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");

			sb.append("라운딩일자 : "+gameDt+"\n");
			sb.append("픽업예정일 : "+startRequestVO.getPicReqDt()+"\n");
			sb.append("픽업지 : "+startRequestVO.getCusSendAddr()+"\n");
			sb.append("도착지 : "+startRequestVO.getCusReAddr()+"\n");
			sb.append("회수예정일 : "+roundRequestVO.getPicReqDt()+"\n");
			sb.append("회수지 : "+ roundRequestVO.getCusSendAddr() +"\n");
			sb.append("회수도착지 : "+roundRequestVO.getCusReAddr()+"\n");
			sb.append("전화번호 : "+startRequestVO.getReceiverNum()+"\n");
			sb.append("골프백수량 : "+startRequestVO.getPdCnt()+"\n");
			sb.append("보스턴백수량 : "+startRequestVO.getBoston()+"\n\n");

			if(startRequestVO.getBoston() > 0) { // 241127 보스턴수량
				sb.append("총 결제 금액 : "+startRequestVO.getPdDiscount()+"원 \n");
			}else {
				int sumPay = startRequestVO.getPdPay()+roundRequestVO.getPdPay();
				sb.append("총 결제 금액 : "+sumPay+"원 \n");
			}
			int sumEtcPay = startRequestVO.getPdEtcPay()+roundRequestVO.getPdEtcPay();
			sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n");
		}
		sb.append("\n결제계좌 : 농협 3010266187691 아이콘(주)\n");
		sb.append("내용 확인 후 위의 결제 계좌로 총 결제 금액 입금 시 예약 완료 됩니다. \n");
		sb.append("*카드결제를 원하시면 카드번호 16자리와 유효기간 \n");
		sb.append("010-4763-0983로 문자보내주시면 됩니다.\n");

		return sb.toString();
	}

	public static String makeRoundMessageByChat(SendRequestVO startRequestVO, SendRequestVO roundRequestVO) throws ParseException {
		String gameDt = "미정";
		if ( startRequestVO.getGameDt() != null ) {
			gameDt = startRequestVO.getGameDt();
		}

		StringBuffer sb = new StringBuffer();
		sb.append("[아이콘] 골프택배 접수 내역 및 결제 안내\n");

		sb.append("안녕하세요. "+startRequestVO.getReceiver()+" 고객님 골프택배 왕복 접수가 승인되었습니다. \n");
		sb.append("접수 내역 및 결제 금액 확인 후 "+KaKaoUtil.getPayLimitDate(startRequestVO.getPicReqDt())+"까지 결제해주시면 접수가 완료됩니다.\n\n");

		if ( roundRequestVO != null ) {
			sb.append("접수번호 : "+startRequestVO.getRcpNumber()+","+roundRequestVO.getRcpNumber()+"\n");
		} else {
			sb.append("접수번호 : "+startRequestVO.getRcpNumber()+"\n");
		}

		sb.append("성함 : "+startRequestVO.getReceiver()+"\n");
		sb.append("전화번호 : "+startRequestVO.getReceiverNum()+"\n");
		sb.append("골프백수량 : "+startRequestVO.getPdCnt()+"\n");
		sb.append("픽업지 : "+startRequestVO.getCusSendAddr()+"\n");
		sb.append("도착지 : "+startRequestVO.getCusReAddr()+"\n");
		sb.append("라운딩일자 : "+gameDt+"\n");
		sb.append("픽업예정일 : "+startRequestVO.getPicReqDt()+"\n");
		if ( roundRequestVO != null ) {
			sb.append("회수예정일 : "+roundRequestVO.getPicReqDt()+"\n");
			sb.append("회수도착지 : "+roundRequestVO.getCusReAddr()+"\n\n");
			int sumPay = startRequestVO.getPdPay()+roundRequestVO.getPdPay();
			sb.append("총 결제 금액 : "+sumPay+"원 \n");
			int sumEtcPay = startRequestVO.getPdEtcPay()+roundRequestVO.getPdEtcPay();
			sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");
		} else {
			sb.append("회수예정일 : "+"미정"+"\n");
			sb.append("회수도착지 : "+"미정"+"\n\n");
			int sumPay = startRequestVO.getPdPay();
			sb.append("총 결제 금액 : "+sumPay+"원 \n");
			int sumEtcPay = startRequestVO.getPdEtcPay();
			sb.append("(물품가액에 따른 추가할증 "+sumEtcPay+"원 포함금액입니다)\n\n");
		}

		sb.append("★ 여러 접수 건에 대한 일괄결제를 원하시는 경우 1544-3044 고객센터로 문의 주시기 바랍니다.\n");

		return sb.toString();
	}
}
