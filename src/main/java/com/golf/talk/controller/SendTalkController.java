package com.golf.talk.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.golf.enums.PrdType;
import com.golf.talk.message.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.golf.service.GlobalProperty;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.talk.vo.SendRequestVO;

@RestController
@RequestMapping("/talk/send")
public class SendTalkController {
	
	private static final Logger logger = LoggerFactory.getLogger(SendTalkController.class);
	
	@Resource(name="globalProperty")
	GlobalProperty configBean;
	
	@Resource
	SendTalkServiceImpl _sendTalkService;
	
	@PostMapping("/sendPayRequest")
	public String sendPayRequest(@RequestBody List<SendRequestVO> list) {
		
		try {
			if ( list.size() == 1 && "1".equals(list.get(0).getDelKind()) ) {
				// 편도
				logger.info("결제요청알림톡(편도)");
				String message = PayRequestMessage.getOneWayMessage(list.get(0));				
				//_sendTalkService.sendPayRequest(list, message);
			} else if ( list.size() == 2 ) {
				// 왕복
				logger.info("결제요청알림톡(왕복)");
				String message = PayRequestMessage.getRoundMessage(list);
				//_sendTalkService.sendPayRequest(list, message);
			} else {
				// 데이터 오류
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		
		return "jsonView";
	}

	@PostMapping("/sendReceiptUnable")
	public String sendReceiptUnable(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수불가알림톡");
			String message = ReceiptUnableMessage.getMessage(list.get(0));
			_sendTalkService.sendReceiptUnable(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendReceiptCompleteForUser")
	public String sendReceiptCompleteForUser(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수완료알림톡(고객)");
			String message = ReceiptCompleteMessage.getForUser(list.get(0));
			//_sendTalkService.sendReceiptComplete(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendReceiptCompleteForUserInfo")
	public String sendReceiptCompleteForUserInfo(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수완료알림톡(고객)_아이콘전용");
			String message = ReceiptCompleteMessage.getForIconInfo(list.get(0));
			_sendTalkService.sendTalk(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "fail";
		}
		return "request Success";
	}
	
	@PostMapping("/sendReceiptCompleteForIcon")
	public String sendReceiptCompleteForIcon(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수완료알림톡(아이콘)");
			String message = ReceiptCompleteMessage.getForIcon(list.get(0),list.get(0).getPrdType());
			//_sendTalkService.sendReceiptComplete(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendReceiptCancelForUser")
	public String sendReceiptCancelForUser(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수취소알림톡(고객)");
			String message = ReceiptCancelMessage.getForUser(list.get(0));
			//_sendTalkService.sendReceiptCancel(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendReceipCancelForIcon")
	public String sendReceipCancelForIcon(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("접수취소알림톡(아이콘)");
			String message = ReceiptCancelMessage.getForIcon(list.get(0), list.get(0).getPrdType());
			//_sendTalkService.sendReceiptCancel(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendPayExpire")
	public String sendPayExpire(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("결제만료알림톡(고객)");
			String message = PayExpireMessage.getOneWayMessage(list.get(0));
			//_sendTalkService.sendReceiptCancel(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@PostMapping("/sendFileUpload")
	public String sendFileUpload(@RequestBody List<SendRequestVO> list) {		
		try {
			logger.info("사진업로드알림톡(기사)");
			String message = FileUploadMessage.getMessage(list.get(0));
			_sendTalkService.sendFileUpload(list, message);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}

	/**
	 * 접수 안내 (receipt)
	 * @param list
	 * @return
	 */
	@PostMapping("/sendVbankMessageForUser")
	public String sendVbankMessageForUser(@RequestBody List<SendRequestVO> list) {

		try {
			logger.info("[공통] 가상계좌안내");
			for (SendRequestVO sendVO : list) {
				String message = CommonMessage.makeVBankForUserByIcon(sendVO);
				_sendTalkService.sendTalk(list, message);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return "jsonView";
	}

	/**
	 *
	 * @param list
	 * @return
	 */
	@PostMapping("/sendReceiptNoticeMessageForUser")
	public String sendReceiptNoticeMessageForUser(@RequestBody List<SendRequestVO> list) {

		try {
			logger.info("접수 안내");
			for (SendRequestVO sendVO : list) {
				String message = ReceiptNoticeMessage.makeForUserByIcon(sendVO, PrdType.fromValue(sendVO.getPrdType()));
				_sendTalkService.sendTalk(list, message);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return "jsonView";
	}

	@PostMapping("/sendPayCompleteMessageForUser")
	public String sendPayCompleteMessageForUser(@RequestBody Map<String, SendRequestVO> sendRequestMap) {

		try {
			logger.info("결제 완료 알림");
			final SendRequestVO startRequestVO = sendRequestMap.get("startRequestVO");
			final SendRequestVO roundRequestVO = sendRequestMap.get("roundRequestVO");
			String message = PayCompleteMessage.makeForUserByIcon(startRequestVO, roundRequestVO, PrdType.fromValue(startRequestVO.getPrdType()));
			List<SendRequestVO> list = new ArrayList<>();
			list.add(startRequestVO);
			_sendTalkService.sendTalk(list, message);
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return "jsonView";
	}

	@PostMapping("/sendPayCancelMessageForUser")
	public String sendPayCancelMessageForUser(@RequestBody List<SendRequestVO> list) {

		try {
			logger.info("취소 완료 알림");

			for (SendRequestVO sendVO : list) {
				String message = PayCancelMessage.makeForUserByIcon(sendVO, PrdType.fromValue(sendVO.getPrdType()));
				_sendTalkService.sendTalk(list, message);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return "jsonView";
	}
}
