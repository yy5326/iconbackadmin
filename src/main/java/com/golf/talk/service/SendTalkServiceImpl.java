package com.golf.talk.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.golf.enums.*;
import com.golf.talk.message.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.service.model.GolfReceiptPayVO;
import com.golf.talk.vo.KakaoInfoVO;
import com.golf.talk.vo.KakaoVO;
import com.golf.talk.vo.SendRequestVO;

@Service("SendTalkService")
public class SendTalkServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(SendTalkServiceImpl.class);

	@Resource(name="globalProperty")
	GlobalProperty configBean;

	@Autowired
	private GeneralDAOImpl _gDao;

	public List<SendRequestVO> createPayRequestList(List<GolfReceiptInfVO> receiptList) {
		List<SendRequestVO> sendRequestList = new ArrayList<SendRequestVO>();
		for ( GolfReceiptInfVO receiptInfoVO : receiptList ) {
			boolean duplicateRound = false;

			SendRequestVO sendVo = new SendRequestVO();
			sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명

			sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			sendVo.setCusReNm(receiptInfoVO.getCusReNm());
			sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setRcpKind(receiptInfoVO.getRcpKind());
			sendVo.setPdDiscount(receiptInfoVO.getDiscount());
			sendVo.setPrdType(receiptInfoVO.getPrdType());
			sendVo.setBostonCnt(receiptInfoVO.getBostonCnt());

			String cusSendAddr2 = "";
			if ( receiptInfoVO.getCusSendAddr2() != null ) {
				cusSendAddr2 = receiptInfoVO.getCusSendAddr2();
			}

			String cusReAddr2 = "";
			if ( receiptInfoVO.getCusReAddr2() != null ) {
				cusReAddr2 = receiptInfoVO.getCusReAddr2();
			}
			sendVo.setCusSendAddr(receiptInfoVO.getCusSendAddr1()+" "+cusSendAddr2);
			sendVo.setCusReAddr(receiptInfoVO.getCusReAddr1()+" "+cusReAddr2);
			sendVo.setRcpSeq(receiptInfoVO.getRcpSeq());

			//인천공항용
			if("A".equals(receiptInfoVO.getRcpType())) {
				sendVo.setBoxCnt(receiptList.get(0).getBoxCnt());
				sendVo.setPrdType(5); //인천공항은 1/3 모두라서 알림톡일때만 5
				if(!receiptInfoVO.getRcpNumberArray().isEmpty()) {
					// 골프/캐리어 접수각각
					sendVo.setRcpNumbers(String.valueOf(receiptInfoVO.getRcpNumberArray()).replaceAll("\\[","").replaceAll("\\]",""));
				}

			}


			for ( int i = 0 ; i < sendRequestList.size() ; i++ ) {
				if ( sendRequestList.get(i).getRcpSeq() == receiptInfoVO.getRcpSeq() ) {
					// 기존 add 한 list element 의 왕복건으로 처리
					duplicateRound = true;
					//sendRequestList.get(i).setEtc1("hdexp_0002");
					if (RcpKind.SMARTSCORE.getValue().equals(receiptList.get(i).getRcpKind())) {
						KaKaoUtil.getTemplateForEtc("hdexp_0004", sendRequestList.get(i));
					}else {
						KaKaoUtil.getTemplateForEtc("hdexp_0002", sendRequestList.get(i));
					}

					try {
						if (RcpKind.SMARTSCORE.getValue().equals(receiptList.get(i).getRcpKind())) {
							sendRequestList.get(i).setTalkMessage(ReceiptCompleteMessage.getForUserRound(sendRequestList.get(i), sendVo));
						}else {
							sendRequestList.get(i).setTalkMessage(PayRequestMessage.getRoundMessage(sendRequestList.get(i), sendVo));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPayRequestList - 기존 add 한 list element 의 왕복건으로 처리 오류");
						e.printStackTrace();
					}
					break;
				}
			}

			if ( !duplicateRound ) {
				// 기존과 겹치지 않는 건으로 처리
				if ( "1".equals(receiptInfoVO.getDelKind()) ) {
					//sendVo.setEtc1("hdexp_0001");

					try {
						if("2".equals(receiptInfoVO.getRcpKind())) {
							KaKaoUtil.getTemplateForEtc("hdexp_0004", sendVo);
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForUserRound(sendVo, sendVo));
						}else {
							KaKaoUtil.getTemplateForEtc("hdexp_0001", sendVo);
							sendVo.setTalkMessage(PayRequestMessage.getOneWayMessage(sendVo));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPayRequestList - 기존과 겹치지 않는 건으로 처리 편도 오류");
						e.printStackTrace();
					}
				} else {
					//sendVo.setEtc1("hdexp_0002");
					try {
						if("2".equals(receiptInfoVO.getRcpKind())) {
							KaKaoUtil.getTemplateForEtc("hdexp_0004", sendVo);
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForUserRound(sendVo, sendVo));
						}else {
							KaKaoUtil.getTemplateForEtc("hdexp_0002", sendVo);
							sendVo.setTalkMessage(PayRequestMessage.getRoundMessage(sendVo, sendVo));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPayRequestList - 기존과 겹치지 않는 건으로 처리 왕복 오류");
						e.printStackTrace();
					}
				}

				sendRequestList.add(sendVo);
			}
		}
		return sendRequestList;
	}

	public List<SendRequestVO> createPaymentArrayList(List<GolfReceiptInfVO> receiptList, String etc, String sendType) {
		List<SendRequestVO> sendRequestList = new ArrayList<SendRequestVO>();
		for ( GolfReceiptInfVO receiptInfoVO : receiptList ) {
			boolean duplicateRound = false;

			SendRequestVO sendVo = new SendRequestVO();

			logger.debug("여기 : " + receiptInfoVO.getCusSendNm() );
			sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			sendVo.setCusReNm(receiptInfoVO.getCusReNm());
			sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setRcpSeq(receiptInfoVO.getRcpSeq());
			sendVo.setRcpKind(receiptInfoVO.getRcpKind());
			sendVo.setPdDiscount(receiptInfoVO.getDiscount());
			sendVo.setPrdType(receiptInfoVO.getPrdType());
			sendVo.setBostonCnt(receiptInfoVO.getBostonCnt());
			//sendVo.setEtc1(etc);
			if ( "user".equals(sendType) ) {
				sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
				sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명


				KaKaoUtil.getTemplateForEtc(etc, sendVo);
			} else {
				sendVo.setReceiverNum(KakaoTalkRecipientType.MANAGER.getPhoneNumber());
				sendVo.setReceiver(KakaoTalkRecipientType.MANAGER.getRecipentName());
				KaKaoUtil.getIconTemplateForEtc(etc, sendVo);
			}

			for ( int i = 0 ; i < sendRequestList.size() ; i++ ) {
				if ( sendRequestList.get(i).getRcpSeq() == receiptInfoVO.getRcpSeq() ) {
					// 기존 add 한 list element 의 왕복건으로 처리
					duplicateRound = true;

					try {
						if ( etc.equals("hdexp_0004")) {
							// 결제 완료, 알림톡 유저, 왕복
							sendRequestList.get(i).setTalkMessage(ReceiptCompleteMessage.getForUserRound(sendRequestList.get(i), sendVo));
						} else if ( etc.equals("hdexp_0005")) {
							// 결제 완료, 알림톡 관리자, 왕복
							sendRequestList.get(i).setTalkMessage(ReceiptCompleteMessage.getForIconRound(sendRequestList.get(i), sendVo, sendRequestList.get(i).getPrdType()));
						} else if ( etc.equals("hdexp_0006")) {
							// 결제 취소, 알림톡 유저, 왕복
							sendRequestList.get(i).setTalkMessage(ReceiptCancelMessage.getForUserRound(sendRequestList.get(i), sendVo));
						} else if ( etc.equals("hdexp_0007")) {
							// 결제 취소, 알림톡 관리자, 왕복
							sendRequestList.get(i).setTalkMessage(ReceiptCancelMessage.getForIconRound(sendRequestList.get(i), sendVo, sendRequestList.get(i).getPrdType()));
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPaymentArrayUserList - 기존 add 한 list element 의 왕복건으로 처리 오류");
						e.printStackTrace();
					}
					break;
				}
			}

			if ( !duplicateRound ) {
				// 기존과 겹치지 않는 건으로 처리
				if ( "1".equals(receiptInfoVO.getDelKind()) ) {
					try {
						if ( etc.equals("hdexp_0004")) {
							// 결제 완료, 알림톡 유저, 편도
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForUser(sendVo));
						} else if ( etc.equals("hdexp_0005")) {
							// 결제 완료, 알림톡 관리자, 편도
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForIcon(sendVo, sendVo.getPrdType()));
						} else if ( etc.equals("hdexp_0006")) {
							// 결제 취소, 알림톡 유저, 편도
							sendVo.setTalkMessage(ReceiptCancelMessage.getForUser(sendVo));
						} else if ( etc.equals("hdexp_0007")) {
							// 결제 취소, 알림톡 관리자, 편도
							sendVo.setTalkMessage(ReceiptCancelMessage.getForIcon(sendVo, sendVo.getPrdType()));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPaymentArrayUserList - 기존과 겹치지 않는 건으로 처리 편도 오류");
						e.printStackTrace();
					}
				} else {
					try {
						if ( etc.equals("hdexp_0004")) {
							// 결제 완료, 알림톡 유저, 왕복
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForUserRound(sendVo, sendVo));
						} else if ( etc.equals("hdexp_0005")) {
							// 결제 완료, 알림톡 관리자, 왕복
							sendVo.setTalkMessage(ReceiptCompleteMessage.getForIconRound(sendVo, sendVo, sendVo.getPrdType()));
						} else if ( etc.equals("hdexp_0006")) {
							// 결제 취소, 알림톡 유저, 왕복
							sendVo.setTalkMessage(ReceiptCancelMessage.getForUserRound(sendVo, sendVo));
						} else if ( etc.equals("hdexp_0007")) {
							// 결제 취소, 알림톡 관리자, 왕복
							sendVo.setTalkMessage(ReceiptCancelMessage.getForIconRound(sendVo, sendVo, sendVo.getPrdType()));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						logger.info("createPaymentArrayUserList - 기존과 겹치지 않는 건으로 처리 왕복 오류");
						e.printStackTrace();
					}
				}

				sendRequestList.add(sendVo);
			}
		}
		return sendRequestList;
	}

	public List<SendRequestVO> createSendRequestArrayList(List<GolfReceiptInfVO> receiptList, KakaoTalkEtcType etcType, KakaoTalkRecipientType recipientType) {
		// 발송 목록
		List<SendRequestVO> sendRequestList = new ArrayList<>();

		// 접수 건 별 그룹핑
		Map<Integer, List<GolfReceiptInfVO>> groupedByRcpSeq = receiptList.stream()
				.collect(Collectors.groupingBy(GolfReceiptInfVO::getRcpSeq));
		for (Integer rcpSeq : groupedByRcpSeq.keySet()) {
			List<GolfReceiptInfVO> srList = groupedByRcpSeq.get(rcpSeq);
			// 주문 건 재그룹핑
			Map<String, List<GolfReceiptInfVO>> groupedByOrderIds = srList.stream()
					.collect(Collectors.groupingBy(GolfReceiptInfVO::getOrderId));
			for (String orderId : groupedByOrderIds.keySet()) {
				List<GolfReceiptInfVO> receiptGroup = groupedByOrderIds.get(orderId);
				// S(출발)가 R(도착)보다 먼저 오도록 srType 기준 정렬
				receiptGroup.sort(Comparator.comparing(GolfReceiptInfVO::getSrType));

				// 편도 or 왕복 1차
				final GolfReceiptInfVO svo = receiptGroup.get(0);
				final TripType tripType = TripType.fromCode(svo.getDelKind());
				final PrdType prdType = PrdType.fromValue(svo.getPrdType());
				final SendRequestVO startRequestVO = getSendRequestVO(etcType, recipientType, svo);

				switch (etcType) {
					case ONEWAY_PAY:
						startRequestVO.setTalkMessage(PayCompleteMessage.makeForUserByIcon(startRequestVO, null, prdType));
						break;
					case ROUND_PAY:
						if (TripType.ROUND_TRIP.equals(tripType)) {
							// 왕복 2차
							SendRequestVO roundRequestVO = getSendRequestVO(etcType, recipientType, receiptGroup.get(1));
							startRequestVO.setTalkMessage(PayCompleteMessage.makeForUserByIcon(startRequestVO, roundRequestVO, prdType));
						}
						break;
					case CANCEL:
						startRequestVO.setTalkMessage(PayCancelMessage.makeForUserByIcon(startRequestVO, prdType));
						break;
				}

				final SendRequestVO sendVO = startRequestVO;
				sendRequestList.add(sendVO);
			}
		}
		return sendRequestList;
	}

	/**
	 * 발송요청
	 *
	 * @param etcType 템플릿 유형
	 * @param recipientType 수신자 유형
	 * @param svo 접수 정보
	 */
	private static SendRequestVO getSendRequestVO(KakaoTalkEtcType etcType, KakaoTalkRecipientType recipientType, GolfReceiptInfVO svo) {
		SendRequestVO sendVo = new SendRequestVO();
		sendVo.setDelKind(svo.getDelKind());		// 배송구분(1-편도/2-왕복)
		sendVo.setRcpNumber(svo.getRcpNumber()); 	// 접수번호
		sendVo.setCusSendNm(svo.getCusSendNm()); 	// 예약자
		sendVo.setCusReNm(svo.getCusReNm());		// 받는분
		sendVo.setPdCnt(svo.getPdCnt());			// 골프백수량
		sendVo.setGameDt(svo.getGameDt());			// 라운딩일정 or 여행일정
		sendVo.setPicReqDt(svo.getPicReqDt());		// 픽업희망일
		sendVo.setPdPay(svo.getPdPay());			// 총합계
		sendVo.setPdEtcPay(svo.getPdEtcPay());		// 할증운임
		sendVo.setRcpSeq(svo.getRcpSeq());			// 그룹번호
		sendVo.setRcpKind(svo.getRcpKind());		// 접수구분
		sendVo.setPdDiscount(svo.getDiscount());	// 할인 적용
		sendVo.setPrdType(svo.getPrdType());		// 상품유형
		sendVo.setBostonCnt(svo.getBostonCnt());	// 보스턴백 수량

		if (KakaoTalkRecipientType.USER.equals(recipientType) ) {
			// 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(svo));
			// 수신자명
			sendVo.setReceiver(KaKaoUtil.getReceiver(svo));
			// 템플릿 그룹 ID
			sendVo.setEtc1(KaKaoUtil.getTemplateGrpId(PrdType.fromValue(sendVo.getPrdType()), etcType));
		} else {
			// ICON 관리자 수신
			sendVo.setReceiverNum(KakaoTalkRecipientType.MANAGER.getPhoneNumber());
			sendVo.setReceiver(KakaoTalkRecipientType.MANAGER.getRecipentName());
			KaKaoUtil.getIconTemplateForEtc("icon", sendVo);
		}

		return sendVo;
	}

	// 아이콘 관리자 골프대기->골프접수시 결제요청 알림톡 전송 - 파라미터 mstVO 있는 경우, 라운딩 일자가 리스트에 없는 로직에서 호출(화면에서 바로 접수완료)
	public void sendPayRequest(List<GolfReceiptInfVO> receiptList, GolfReceiptMstVO mstVO) {
		// TODO Auto-generated method stub
		for ( int i = 0 ; i < receiptList.size() ; i++ ) {
			receiptList.get(i).setGameDt(mstVO.getGameDt());
		}
		List<SendRequestVO> sendRequestList = createPayRequestList(receiptList);
		try {
			sendTalk(sendRequestList);

			logger.debug("전송 내용" + sendRequestList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("결제요청 알림톡 오류");
			e.printStackTrace();
		}
	}

	// 아이콘 관리자 골프대기->골프접수시 결제요청 알림톡 전송
	public void sendPayRequest(List<GolfReceiptInfVO> receiptList) {
		// TODO Auto-generated method stub
		List<SendRequestVO> sendRequestList = createPayRequestList(receiptList);
		try {
			sendTalk(sendRequestList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("결제요청 알림톡 오류");
			e.printStackTrace();
		}
	}

	// 아이콘 관리자 접수취소시 알림톡 ( 고객, 아이콘 ) - 관리자 접수취소는 한건만 가능
	public void deleteGolfDeliveryForCancel(GolfReceiptInfVO vo) {
		// TODO Auto-generated method stub
		GolfReceiptPayVO payVO = new GolfReceiptPayVO();
		payVO.setRcpInfSeqArray(vo.getRcpInfSeqArray());
		payVO.setPayStat(StringUtils.isEmpty(vo.getGolfUpdate()) ? PayStat.COMPLETE.code() : PayStat.CANCEL.code());

		List<GolfReceiptInfVO> rcpNumberList = new ArrayList<GolfReceiptInfVO>();
		GolfReceiptInfVO sendVo = new GolfReceiptInfVO();
		sendVo.setRcpNumber(vo.getRcpNumber());
		sendVo.setRcpKind(vo.getRcpKind());
		sendVo.setRcpNumberArray(vo.getRcpNumberArray());
		sendVo.setPrdType(vo.getPrdType());
		rcpNumberList.add(sendVo);

		// 골프취소
		String delType = "";
		// 이미 데이터가 취소처리된 다음이므로 원 데이터가 편도취소인지 왕복취소인지에 대한 구분이 필요하다. - 단건처리만
		/*if ( rcpNumberList.size() > 1 ) {
			delType = "왕복";
		}*/
		sendReceiptCancel(rcpNumberList,delType);

	}

	// 챗봇 결제 완료시 접수완료 알림톡(고객, 아이콘)
	@SuppressWarnings("unchecked")
	public void sendReceiptComplete(GolfReceiptPayVO golfReceiptPayVO) {
		// TODO Auto-generated method stub
		List<GolfReceiptInfVO> rcpNumberList = (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectByKakaoHistory", golfReceiptPayVO.getOrderId());
		List<SendRequestVO> userlist = new ArrayList<SendRequestVO>();
		List<SendRequestVO> iconList = new ArrayList<SendRequestVO>();

		for ( GolfReceiptInfVO receiptInfoVO : rcpNumberList ) {

			receiptInfoVO = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpNumber", receiptInfoVO);

			SendRequestVO sendVo = new SendRequestVO();
			sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명
			sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			sendVo.setCusReNm(receiptInfoVO.getCusReNm());
			sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setRcpKind(receiptInfoVO.getRcpKind());
			sendVo.setPdDiscount(receiptInfoVO.getDiscount());
			sendVo.setPrdType(receiptInfoVO.getPrdType());
			sendVo.setBostonCnt(receiptInfoVO.getBostonCnt());

			//sendVo.setEtc1("hdexp_0004");
			KaKaoUtil.getTemplateForEtc("hdexp_0004", sendVo);

			userlist.add(sendVo);

			SendRequestVO iconSendVo = new SendRequestVO();
			iconSendVo.setReceiverNum(KakaoTalkRecipientType.MANAGER.getPhoneNumber());
			iconSendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			iconSendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			iconSendVo.setCusReNm(receiptInfoVO.getCusReNm());
			iconSendVo.setPdCnt(receiptInfoVO.getPdCnt());
			iconSendVo.setGameDt(receiptInfoVO.getGameDt());
			iconSendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			iconSendVo.setPdPay(receiptInfoVO.getPdPay());
			iconSendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			iconSendVo.setDelKind(receiptInfoVO.getDelKind());
			iconSendVo.setRcpKind(receiptInfoVO.getRcpKind());
			iconSendVo.setPdDiscount(receiptInfoVO.getDiscount());
			iconSendVo.setEtc1("hdexp_0005");
			KaKaoUtil.getIconTemplateForEtc("hdexp_0005", iconSendVo);

			iconList.add(iconSendVo);
		}

		try {
			if ( "1".equals(userlist.get(0).getDelKind()) ) {
				// 편도
				String message = ReceiptCompleteMessage.getForUser(userlist.get(0));
				sendTalk(userlist, message);
				String iconMessage = ReceiptCompleteMessage.getForIcon(userlist.get(0), userlist.get(0).getPrdType());
				sendTalk(iconList, iconMessage);
			} else {
				// 왕복
				String message = ReceiptCompleteMessage.getForUserRound(userlist);
				String iconMessage = ReceiptCompleteMessage.getForIconRound(userlist, userlist.get(0).getPrdType());
				userlist.remove(1);
				iconList.remove(1);
				sendTalk(userlist, message);
				sendTalk(iconList, iconMessage);
			}
		} catch ( Exception e ) {
			logger.info("결제 완료시 접수완료 알림톡 전송 오류");
			e.printStackTrace();
		}

	}

	// 챗봇 접수취소시 접수취소 알림톡(고객, 아이콘)
	public void sendReceiptCancel(List<GolfReceiptInfVO> resultList, String orderId) {
		// TODO Auto-generated method stub

		List<SendRequestVO> userlist = new ArrayList<SendRequestVO>();
		List<SendRequestVO> iconList = new ArrayList<SendRequestVO>();

		for ( GolfReceiptInfVO receiptInfoVO : resultList ) {


			receiptInfoVO = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpNumber", receiptInfoVO);

			SendRequestVO sendVo = new SendRequestVO();
			sendVo.setReceiverNum(KaKaoUtil.getReceiverTelNum(receiptInfoVO)); // 수신자 연락처 - 접수자 연락처, 접수자 연락처가 없을 경우 보내는 분 연락처
			sendVo.setReceiver(KaKaoUtil.getReceiver(receiptInfoVO)); // 수신자명

			if("2".equals(receiptInfoVO.getRcpKind())) {
				//스마트 스코어 왕복 취소건인 경우 알림톡에 2개 접수번호 표시, 편도는 자동으로 1개 표시
				sendVo.setRcpNumber(String.join(" | ", resultList.get(0).getRcpNumberArray()));
			}else {
				sendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			}

			sendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			sendVo.setCusReNm(receiptInfoVO.getCusReNm());
			sendVo.setPdCnt(receiptInfoVO.getPdCnt());
			sendVo.setGameDt(receiptInfoVO.getGameDt());
			sendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			sendVo.setPdPay(receiptInfoVO.getPdPay());
			sendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			sendVo.setDelKind(receiptInfoVO.getDelKind());
			sendVo.setRcpKind(receiptInfoVO.getRcpKind());
			sendVo.setPdDiscount(receiptInfoVO.getDiscount());
			sendVo.setPrdType(receiptInfoVO.getPrdType());
			sendVo.setBostonCnt(receiptInfoVO.getBostonCnt());
			//sendVo.setEtc1("hdexp_0006");
			KaKaoUtil.getTemplateForEtc("hdexp_0006", sendVo);
			userlist.add(sendVo);

			SendRequestVO iconSendVo = new SendRequestVO();
			iconSendVo.setReceiverNum(KakaoTalkRecipientType.MANAGER.getPhoneNumber());

			iconSendVo.setRcpNumber(receiptInfoVO.getRcpNumber());
			iconSendVo.setCusSendNm(receiptInfoVO.getCusSendNm());
			iconSendVo.setCusReNm(receiptInfoVO.getCusReNm());
			iconSendVo.setPdCnt(receiptInfoVO.getPdCnt());
			iconSendVo.setGameDt(receiptInfoVO.getGameDt());
			iconSendVo.setPicReqDt(receiptInfoVO.getPicReqDt());
			iconSendVo.setPdPay(receiptInfoVO.getPdPay());
			iconSendVo.setPdEtcPay(receiptInfoVO.getPdEtcPay());
			iconSendVo.setDelKind(receiptInfoVO.getDelKind());
			iconSendVo.setRcpKind(receiptInfoVO.getRcpKind());
			iconSendVo.setPdDiscount(receiptInfoVO.getDiscount());
			iconSendVo.setPrdType(receiptInfoVO.getPrdType());
			iconSendVo.setBostonCnt(receiptInfoVO.getBostonCnt());
			//iconSendVo.setEtc1("hdexp_0007");
			KaKaoUtil.getIconTemplateForEtc("hdexp_0007", iconSendVo);
			iconList.add(iconSendVo);
		}
		try {
			if ( orderId == null || "".equals(orderId) ) {
				// 편도
				String message = ReceiptCancelMessage.getForUser(userlist.get(0));
				sendTalk(userlist, message);
				String iconMessage = ReceiptCancelMessage.getForIcon(userlist.get(0), userlist.get(0).getPrdType());
				sendTalk(iconList, iconMessage);
			} else {
				// 왕복
				String message = ReceiptCancelMessage.getForUserRound(userlist);
				String iconMessage = ReceiptCancelMessage.getForIconRound(userlist, userlist.get(0).getPrdType() );
				userlist.remove(1);
				iconList.remove(1);
				sendTalk(userlist, message);
				sendTalk(iconList, iconMessage);
			}
		} catch ( Exception e ) {
			logger.info("결제 완료시 접수완료 알림톡 전송 오류");
			e.printStackTrace();
		}

	}

	public void sendPayExpire(List<SendRequestVO> list, String message) throws Exception {
		// TODO Auto-generated method stub
		sendTalk(list, message);
	}

	public void sendFileUpload(List<SendRequestVO> list, String message) throws Exception {
		// TODO Auto-generated method stub
		sendTalk(list, message);
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void sendTalk(List<SendRequestVO> list, String message) throws Exception {
		String server = configBean.getWebMode();
		JSONObject requestSend = new JSONObject();
		HttpURLConnection conn;
        JSONObject responseJson = null;
        KakaoVO templCodeList = null;
        List<KakaoVO> result = new ArrayList<KakaoVO>();

        for ( SendRequestVO sendRequestVO : list ) {
			// 렌탈이면 패스
			logger.debug("1 카카오 알림톡 타입: {}", sendRequestVO.getRcpKind());

			boolean rentalFlag = Arrays.asList("6", "7", "8").contains(sendRequestVO.getRcpKind());
			if(rentalFlag) continue;

        	KakaoVO kakaoVO = new KakaoVO();
        	kakaoVO.setKakaoInfoVO(new KakaoInfoVO());
    		kakaoVO.getKakaoInfoVO().setMode(server);
    		kakaoVO.setEtc1(sendRequestVO.getEtc1()); // 템플릿 그룹 ID
    		kakaoVO.setEtc2(sendRequestVO.getRcpNumber()); // 골프접수번호

    		// find template
    		logger.info(server+" server data search");
    		if ( server.equals("op") ) {
            	templCodeList = (KakaoVO) _gDao.selectOne("kakao.selectByNewTemplate", kakaoVO);
            }else {
            	templCodeList = (KakaoVO) _gDao.selectOne("kakao.selectByTemplateTest", kakaoVO);
            }
    		templCodeList.setPhoneNumber(sendRequestVO.getReceiverNum());


    		// ready send & param set
    		String sendUrl = templCodeList.getKakaoInfoVO().getSendUrl();
    		requestSend.put("message_type","AT"); // 메세지타입
    		requestSend.put("sender_key",templCodeList.getKakaoInfoVO().getSenderKey()); // 발신 프로필키
    		requestSend.put("phone_number", templCodeList.getPhoneNumber()); // 수신번호
    		requestSend.put("template_code", templCodeList.getTemplateCode()); // 템플릿 코드
    		requestSend.put("sender_no", KakaoTalkRecipientType.MANAGER.getPhoneNumber());	 	 //보내는 번호	- 아이콘 담당자 전번 hard coding
    		requestSend.put("message", message);


    		logger.info("####################################");
    		logger.info("send message: " + message);
    		logger.info("####################################");

    		if(list.get(0).getButtonList()!=null) {
    			requestSend.put("button", list.get(0).getButtonList());
    		}

    		// send start
    		URL url = new URL(sendUrl);
    		conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer ".concat(templCodeList.getKakaoInfoVO().getToken()));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
            String parameters = requestSend.toJSONString();
            stream.write(parameters.getBytes("UTF-8"));
            stream.flush();
            stream.close();

            // HTTP 응답코드에 따른 분기처리
            int responseCode = conn.getResponseCode();
            if (200 <= responseCode && responseCode <= 299) {
                // 응답스트림을 한줄씩 스트링버퍼에 담는다.
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                // 응답받은 json 데이터를 파싱
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(sb.toString());

                responseJson = (JSONObject) obj;
                logger.info("발송성공");
            } else {
            	logger.info("발송실패");
            }
            logger.info("send response code: " +responseCode);
            logger.info("send URL: " +sendUrl);
            logger.info("sender_key: " +templCodeList.getKakaoInfoVO().getSenderKey());
            logger.info("Authorization: " +"Bearer ".concat(templCodeList.getKakaoInfoVO().getToken()));
            logger.info("send parameters : " + parameters);
            logger.info("response message: " + responseJson);
            logger.info("response code: " + responseJson.get("code"));

            kakaoVO.setResponseCode(responseCode);
            kakaoVO.setSendParameters(parameters);
            kakaoVO.setTemplateCode(templCodeList.getTemplateCode());
            kakaoVO.setPhoneNumber(templCodeList.getPhoneNumber());
            kakaoVO.setApiCode(responseJson.get("code").toString());
            result.add(kakaoVO);

            // 템플릿 그룹 ID
            _gDao.insert("kakao.insert", kakaoVO);
        }
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public void sendTalk(List<SendRequestVO> list) throws Exception {
		String server = configBean.getWebMode();
		JSONObject requestSend = new JSONObject();
		HttpURLConnection conn;
        JSONObject responseJson = null;
        KakaoVO templCodeList = null;
        List<KakaoVO> result = new ArrayList<KakaoVO>();

        for ( SendRequestVO sendRequestVO : list ) {
			// 렌탈이면 패스
			logger.debug("2 카카오 알림톡 타입: {}", sendRequestVO.getRcpKind());
			boolean rentalFlag = Arrays.asList("6", "7", "8").contains(sendRequestVO.getRcpKind());
			if(rentalFlag) continue;

        	KakaoVO kakaoVO = new KakaoVO();
        	kakaoVO.setKakaoInfoVO(new KakaoInfoVO());
    		kakaoVO.getKakaoInfoVO().setMode(server);
    		kakaoVO.setEtc1(sendRequestVO.getEtc1()); // 템플릿 그룹 ID
			if (sendRequestVO.getRcpNumber() != null) {
    			kakaoVO.setEtc2(sendRequestVO.getRcpNumber()); // 골프접수번호
			}

    		// find template
    		logger.info(server+" server data search");
    		if ( server.equals("op") ) {
            	templCodeList = (KakaoVO) _gDao.selectOne("kakao.selectByNewTemplate", kakaoVO);
            }else {
            	templCodeList = (KakaoVO) _gDao.selectOne("kakao.selectByTemplateTest", kakaoVO);
            }
    		templCodeList.setPhoneNumber(sendRequestVO.getReceiverNum().replace("-", ""));


    		// ready send & param set
    		String sendUrl = templCodeList.getKakaoInfoVO().getSendUrl();
    		requestSend.put("message_type","AT"); // 메세지타입
    		requestSend.put("sender_key",templCodeList.getKakaoInfoVO().getSenderKey()); // 발신 프로필키
    		requestSend.put("phone_number", templCodeList.getPhoneNumber()); // 수신번호
    		requestSend.put("template_code", templCodeList.getTemplateCode()); // 템플릿 코드
    		requestSend.put("sender_no", KakaoTalkRecipientType.MANAGER.getPhoneNumber());	 	 //보내는 번호	- 아이콘 담당자 전번 hard coding
    		requestSend.put("message", sendRequestVO.getTalkMessage());

    		// send start
    		URL url = new URL(sendUrl);
    		conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer ".concat(templCodeList.getKakaoInfoVO().getToken()));
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
            String parameters = requestSend.toJSONString();
            stream.write(parameters.getBytes("UTF-8"));
            stream.flush();
            stream.close();

            // HTTP 응답코드에 따른 분기처리
            int responseCode = conn.getResponseCode();
            logger.info("응답코드 : {}", responseCode);

            if (200 <= responseCode && responseCode <= 299) {
                // 응답스트림을 한줄씩 스트링버퍼에 담는다.
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                // 응답받은 json 데이터를 파싱
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(sb.toString());

                responseJson = (JSONObject) obj;
                logger.info("발송성공");

                logger.info("send response code: " +responseCode);
                logger.info("send URL: " +sendUrl);
                logger.info("sender_key: " +templCodeList.getKakaoInfoVO().getSenderKey());
                logger.info("Authorization: " +"Bearer ".concat(templCodeList.getKakaoInfoVO().getToken()));
                logger.info("send parameters : " + parameters);
                logger.info("response message: " + responseJson);
                logger.info("response code: " + responseJson.get("code"));


                kakaoVO.setResponseCode(responseCode);
                kakaoVO.setSendParameters(parameters);
                kakaoVO.setTemplateCode(templCodeList.getTemplateCode());
                kakaoVO.setPhoneNumber(templCodeList.getPhoneNumber());
                kakaoVO.setApiCode(responseJson.get("code").toString());
                result.add(kakaoVO);

                // 템플릿 그룹 ID
                _gDao.insert("kakao.insert", kakaoVO);
            } else {
            	logger.info("발송실패");
            }

        }
	}

	// 접수불가 알림톡 - 현재 어느 시점에 쓰이는지 정의되어 있지 않아 생성만 하고 메세지 전송 테스트 x
	public void sendReceiptUnable(List<SendRequestVO> list, String message) throws Exception {
		// TODO Auto-generated method stub
		sendTalk(list, message);
	}
}
