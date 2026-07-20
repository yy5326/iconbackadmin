package com.golf.controller;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.golf.chat.service.CbReceiveServiceImpl;
import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.util.EgovMapDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import com.golf.talk.controller.SendTalkController;

import com.golf.service.impl.CarrierServiceImpl;
import com.golf.service.impl.GolfApi;
import org.springframework.beans.factory.annotation.Value;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * 캐리어 관련 API 컨트롤러
 * @author inferno
 *
 */
@RestController
@RequestMapping("/icon")
public class CarrierApiController extends PrtController  {

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	CarrierServiceImpl _carrierReceiptService;
	
	@Resource
	CbReceiveServiceImpl _receiptService;
	
	@Resource
	SendTalkController _sendTalk;

	@Autowired
	private GolfApi golfApi;
	
	@Resource
	private CommonController common;

	@Value("#{globals['api.key']}")
	private String apiKey;
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	/**
	 * 캐리어접수
	 * /icon/carrier/receipt
	 *
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/carrier/receipt")
	public JSONObject carrierReceipt(@RequestBody List<CbReceiptInfoVO> list){
		
		logger.debug(">>>>>>>>>> 캐리어 접수 요청" );
		
		JSONObject obj = new JSONObject();
		obj.put("httpStatus","OK");
		JSONArray kakaoArray = new JSONArray();
		try {
			
			List<GolfReceiptInfVO> receiptList = _carrierReceiptService.receiptProcConfirm(list);
			logger.info("접수요청 확인");
			
			JSONObject data = new JSONObject();
			
			String rcpNumber = "";
			String regDate = _receiptService.selectDetailByRcpSeq(receiptList.get(0).getRcpSeq(), "S").getRegDate();

			if("2".equals(list.get(0).getDelKind())) {
				data.put("cbPay", receiptList.get(0).getPdPay()*2);
			}else {
				data.put("cbPay", receiptList.get(0).getPdPay());
			}

			String orderId = ""; // 결제용 묶음코드
			if (list.get(0).getOrderNumber() != null) {
				orderId = list.get(0).getOrderNumber();
				if ( "2".equals(list.get(0).getDelKind()) ) {
					data.put("sRoundTripDiscountAmt", receiptList.get(0).getRoundTripDiscountAmt()); // 왕복 1차 편도-왕복 차액
					data.put("rRoundTripDiscountAmt", receiptList.get(1).getRoundTripDiscountAmt()); // 왕복 2차 편도-왕복 차액
				}
			} else {
				orderId = receiptList.get(0).getRcpNumber();
			}
			data.put("orderId",orderId);

			for(GolfReceiptInfVO vo : receiptList) {
				rcpNumber += vo.getRcpNumber()+"|";
			}
			rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);
			data.put("rcpNumber",rcpNumber);

			data.put("regDate", regDate);
			kakaoArray.add(data);			
			obj.put("result",kakaoArray);
		}catch ( Exception e ) {
			e.printStackTrace();
			logger.info("영업소 프로그램 및 기타 에러"+e.getMessage());
			JSONObject data = new JSONObject();
			data.put("cbPay", 0);
			data.put("cbReceiptStatus", "-1"); // 0이 접수대기
			data.put("errorMsg", e.getMessage());
			kakaoArray.add(data);			
			obj.put("result",kakaoArray);
		}
		
		
		return obj;
		
	}
	

	/**
	 * 캐리어 수수료조회
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@PostMapping("/carrier/commission")
	public JSONObject carrierCommission(@RequestBody CbReceiptInfoVO cbReceiptInfoVO) throws Exception{	
		
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		
		GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();
		
		try {
			receiptInfVO.setDelKind(cbReceiptInfoVO.getDelKind());
			receiptInfVO.setPdCnt(cbReceiptInfoVO.getBoxCnt());
			
			receiptInfVO.setSendGolfSeq(cbReceiptInfoVO.getSendGolfSeq());
			receiptInfVO.setRecvGolfSeq(cbReceiptInfoVO.getRecvGolfSeq());
			
			receiptInfVO.setCusSendAddr1(cbReceiptInfoVO.getCusSendAddr1());
			receiptInfVO.setCusSendAddr2(cbReceiptInfoVO.getCusSendAddr2());
			receiptInfVO.setCusReAddr1(cbReceiptInfoVO.getCusReAddr1());
			receiptInfVO.setCusReAddr2(cbReceiptInfoVO.getCusReAddr2());
			
			
			logger.debug("log... {}", receiptInfVO);
			_carrierReceiptService.preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVO);
			
			data.put("status", "200");	
			data.put("discount", receiptInfVO.getDiscount());
			data.put("discount",receiptInfVO.getDiscount());
			data.put("discountTotal",receiptInfVO.getDiscountTotal());
			data.put("pdDosunPay", receiptInfVO.getPdDosunPay());
			data.put("pdDeliPay", receiptInfVO.getPdDeliPay() + receiptInfVO.getIconFee());
			data.put("pdEtcPay", receiptInfVO.getPdEtcPay());
			data.put("pdPay", receiptInfVO.getPdPay());
			data.put("roundTripDiscountAmt", receiptInfVO.getRoundTripDiscountAmt()); // 편도-왕복 차액
			data.put("iconFee", receiptInfVO.getIconFee());
			
		} catch ( Exception e ) {
			data.put("status", "500");	
			logger.error(e.toString());
		}
		obj.put("httpStatus","OK");
		obj.put("result", data);
		return obj;
		
		
	}

	/**
	 * 캐리어접수 수정
	 * /icon/carrier/updateReceipt
	 *
	 * @param list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/carrier/updateReceipt")
	public JSONObject carrierUpdate(@RequestBody  List<CbReceiptInfoVO> list) throws Exception{
		
		logger.debug(">>>>>>>>>> 캐리어 수정 요청" );
		

		JSONObject obj = new JSONObject();		
		
		try {
			List<GolfReceiptInfVO> result = _carrierReceiptService.updateReceipt(list);

			logger.debug("업데이트 결과 {}", new Gson().toJson(result));
			obj.put("httpStatus","OK");
			/*JSONArray kakaoArray = new JSONArray();
			
			for ( GolfReceiptInfVO info : result ) {
				JSONObject data = new JSONObject();
				data.put("resultStatus", "Y");	
				data.put("cbReceiptStatus", info.getRcpInfStat());
				data.put("rcpNumber", info.getRcpNumber());
				data.put("cbPay", info.getPdPay());
				kakaoArray.add(data);
			}*/
			
			JSONObject data = new JSONObject();
			String rcpNumber = "";
			int cbPay = 0;
			for(GolfReceiptInfVO vo : result) {
				rcpNumber += vo.getRcpNumber()+"|";
				data.put("cbReceiptStatus", vo.getRcpInfStat());
				cbPay += vo.getPdPay();
				data.put("resultStatus", "Y");				
			}
			data.put("cbPay", cbPay);
			data.put("rcpNumber", rcpNumber);
			data.put("orderId", result.get(0).getOrderId());
			obj.put("result", data);			
			
		} catch ( Exception e ) {
			e.printStackTrace();
			JSONObject data = new JSONObject();
			data.put("resultStatus", "N");
			obj.put("result", data);
		}
		
		obj.put("httpStatus","OK");
		
		return obj;
		
	}
	
	
	/**
	 * 캐리어 취소
	 * 
	 * */
	@SuppressWarnings("unchecked")
	@PostMapping("/carrier/cancelReceipt")
	public JSONObject cancelReceipt(@RequestBody CbCommonVO cbCommon) throws Exception{
			
		logger.info(">>>>>>>>>> 캐리어 취소 요청" );
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		
		try {
			String cancelResult = _carrierReceiptService.cancelReceipt(cbCommon);
			if ( "Y".equals(cancelResult) ) {
				data.put("resultStatus", cancelResult);
				List<GolfReceiptInfVO> resultList = _carrierReceiptService.cancelResultList(cbCommon);
				if ( resultList.size() > 1 ) {
					data.put( "cbReceiptStatus", resultList.get(0).getRcpInfStat()+"|"+resultList.get(0).getRcpInfStat() );
				} else {
					data.put("cbReceiptStatus",  resultList.get(0).getRcpInfStat());
				}
				
			} else {
				data.put("resultStatus", cancelResult);
				data.put("cbReceiptStatus", "error");
			}
			
		} catch ( Exception e ) {
			data.put("resultStatus", "N");	
		}
		obj.put("httpStatus","OK");
		obj.put("result", data);
		
		return obj;
		
	}
	
	
	
//	
//	
//
//	/** 영업소 물품 정보 수정
//	 *
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping(value = "/update/delivery", method = RequestMethod.POST, produces = "application/json; charset=utf8")
//    public @ResponseBody ResponseEntity<JSONObject> updateDelivery(@RequestBody Map<String, Object> param) {
//		String code = "200";
//		Gson gson = new Gson();
//
//		//logger.debug(param.toString());
//		try {
//
//			//보내주는 파라미터명이 상이하여 camelcase로 치환(EgovMap 내부에서 처리)
//			Iterator<String> iter = param.keySet().iterator();
//			EgovMap gm = new EgovMap();
//			while(iter.hasNext()){
//				String key = iter.next();
//				if(!StringUtil.isEmpty(key)){
//					gm.put("pd_deli_gubun".equals(StringUtils.lowerCase(key)) ? "payType" : key, param.get(key));
//				}
//			}
//
//			GolfReceiptInfVO vo = gson.fromJson(gson.toJson(gm), GolfReceiptInfVO.class);
//			logger.debug("convertParam =====> "+ gson.toJson(gm));
//			logger.debug("convertParam =====> "+ vo.toString());
//			
//			if(!StringUtil.isEmpty(vo.getGolfUpdate())){
//				GolfReceiptInfVO temp = _receiptService.selectReceiptKey(vo);
//				if(temp.getRcpInfSeq() > 0){
//					vo.setRcpInfSeq(temp.getRcpInfSeq());
//				}else{
//					return setJson("300");
//				}
//
//				List<GolfReceiptInfVO> golfReceiptList = (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectByKakao", vo);
//				
//				if(golfReceiptList.size()>0) {
//					vo.setRcpSeq(golfReceiptList.get(0).getRcpSeq());
//					vo.setRcpNumber(golfReceiptList.get(0).getRcpNumber());
//					vo.setRcpInfSeq(golfReceiptList.get(0).getRcpInfSeq());
//				}
//				
//				//상태변경(최초 운송장번호 생성 및 택배접수일 전달, 택배접수)
//				if("U".equals(vo.getGolfUpdate())){
//					vo.setRcpInfStat(RcpInfStat.Delivery.code());
//					
//					//골프백 사진 카톡 발송
//					List<SendRequestVO> sendTalkList =  new ArrayList<SendRequestVO>();
//					SendRequestVO sendTalk = new SendRequestVO();
//					JSONObject requestSendMessage = new JSONObject();
//			        ArrayList buttonList = new ArrayList();
//					
//					if(golfReceiptList.size()>0) {
//						System.out.println("업데이트 요청");
//						_receiptService.updateReceipt(vo);
//
//						//api전송
//						vo.setRcpKind(golfReceiptList.get(0).getRcpKind());
//						golfApi.updateApi(vo, "update_cbag_status");
//
//						if(golfReceiptList.get(0).getRcpKind().equals("0")) {
//							vo.setCusSendNm(golfReceiptList.get(0).getCusReNm());//관리자에서 접수 한 경우 보내는 사람이름이 아이콘이여서 받는사람으로 변경	
//						}else {
//							vo.setCusSendNm(golfReceiptList.get(0).getCusSendNm());
//						}
//						
//						//수신고객명
//						sendTalk.setCusSendNm(vo.getCusSendNm());
//						//운송장번호
//						sendTalk.setPdBarcode(vo.getPdBarcode());
//						//수신 연락처
//						sendTalk.setReceiverNum(KakaoTalkRecipientType.MANAGER.getPhoneNumber());
//						//템플릿코드
//						sendTalk.setEtc1("hdexp_0011");
//						//예약자
//						sendTalk.setReceiver(golfReceiptList.get(0).getCusReceiptNm());
//						
//						//수신번호 파라미터 암호화
//						Base64 b = new Base64();
//						String url = super.getConfig().getWebMode().equals("op") ? "https://kdhd.icexp.co.kr/golfCam/golfCamWrite.do" : "https://test.icexp.co.kr/golfCam/golfCamWrite.do";
//						
//						requestSendMessage.put("name","골프백 사진 등록");
//						requestSendMessage.put("type","WL");
//						requestSendMessage.put("url_mobile", url + "?rcpInfSeq="+vo.getRcpInfSeq()+"&codePhoto="+b.encodeBase64URLSafeString(rsaTranse(sendTalk.getReceiverNum())));	
//						
//						buttonList.add(requestSendMessage);
//						
//						sendTalk.setButtonList(buttonList);
//						
//						sendTalkList.add(sendTalk);
//						_sendTalk.sendFileUpload(sendTalkList);
//					}
//					//api 변경 요청 요망
//					//_gDao.update("golfReceiptMst.updateSelective", vo);
//				//상태변경(픽업취소)
//				}else if("C".equals(vo.getGolfUpdate())){
//					 //영업소 취소 요청 => 픽업취소 단계로 변경
//					 vo.setRcpInfStat(RcpInfStat.PickupCancel.code());
//					 vo.setCanReason("영업소 취소");					 
//					 _receiptService.updateReceiptInfSel(vo, false);
//					 
//				//상태변경(택배취소)
//				}else if("D".equals(vo.getGolfUpdate())){
//					 //영업소 삭제 요청 => 택배취소 단계로 변경
//					 vo.setRcpInfStat(RcpInfStat.DeliveryCancel.code());
//					 vo.setCanReason("영업소 발송자료 삭제");
//					 _receiptService.updateReceiptInfSel(vo, false);
//					
//				}else{
//					return setJson("100", new JSONObject(gm));
//				}
//			}else{
//				return setJson("100");
//			}
//			return setJson(code, new JSONObject(gm));
//
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			return setJson("100");
//		} catch (Exception ee) {
//			ee.printStackTrace();
//			return setJson("905");
//		}
//    }

	private ResponseEntity<JSONObject> setJson(String code){
		return setJson(code, new JSONObject());
	}

	@SuppressWarnings("unchecked")
	private ResponseEntity<JSONObject> setJson(String code, JSONObject result){
		String msg = "";
		JSONObject obj = new JSONObject();
		HttpStatus eHttpStatus = HttpStatus.BAD_REQUEST;

		switch (code) {
		case "200":
			msg = "성공";
			eHttpStatus = HttpStatus.OK;
			break;
		case "300":
			msg = "유효하지 않은 회수번호입니다.";
			eHttpStatus = HttpStatus.NOT_ACCEPTABLE;
			break;
		case "100":
			msg = "정상적이지 않은 입력변수";
			break;
		case "905":
			msg = "예외 발생";
			break;

		default:
			msg = "예외 발생";
			break;
		}

		obj.put("code", code);
		obj.put("message", msg);
		obj.put("result", result);

		return new ResponseEntity<JSONObject>(obj, eHttpStatus);
	}
	
	/**
	 * 할증운임료 조회
	 * @param vo
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update/traking", method = RequestMethod.POST)
	public Object traking(@RequestBody GolfReceiptInfVO vo) throws Exception {
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
        
		try {	
			String url = super.getConfig().getWebMode().equals("op") ? "http://kdtracking.kditlab.com/Tracking" : "http://kdtracking.kditlab.com:81/tracking";
			EgovMap apiMap = gsonCallHttp(url, vo.getPdBarcode(), apiKey);
			System.out.println("에이피아이맵" + apiMap);
			if(!(apiMap == null)){
				return toJson(apiMap);
			}else{
				ArrayList<Map<String, Object>> listMap = new ArrayList<>();
				
				ArrayList<String> list = new ArrayList<>();
				
				Map<String, Object> map = new HashMap<String , Object>();
				
				map.put("listData", list);
				
				Map<String, Object> map2 = new HashMap<String , Object>();

				map2.put("items", map);
				
				
//				
//				String[] arr = {};
//				
//				Map<String, Object> map2 = new HashMap<String, Object>();
//				
//				map2.put("listData", list);
//				
//				Map<String, Object> map3 = new HashMap<String, Object>();
//				
//				map3.put("items" , map2);
//				
//				listMap.add(map3);
//				
				return map2;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return "falsess";
		}
		
	}

	/**
	 * 골프장 조회
	 * @param vo
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/select/golf_list")
	@ApiOperation(value = "골프장 조회")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공"),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject golfList(@RequestBody GolfInfVO search, Model model) throws Exception {
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		String searchText = "";
		
		logger.debug("파라미터 확인용 : " + search.getType1());
	
		if(search.getType1().equals("2")) {
			//주소로 검색
			searchText = search.getGolfAddr1();
		}else {
			//골프장 이름 검색
			searchText = search.getGolfName();
		}
		search.setSearchWord(searchText);
		
			if (search.getRecordCount() < 20)
				search.setRecordCount(5);
				search.setBlockCount(5);
			
			List<GolfInfVO> golfInfList = (List<GolfInfVO>)_gDao.selectBySearch("golfInf.selectBySearch", search, "selectListCount");
			
			JSONObject obj = new JSONObject();
			JSONArray dataArray = new JSONArray();
			
			try {	
				
			for(GolfInfVO vo : golfInfList) {
				JSONObject data = new JSONObject();
				data.put("golfSeq", vo.getGolfSeq());
				data.put("golfName", vo.getGolfName());
				data.put("golfTelnum", vo.getGolfTelnum());
				data.put("golfAddr1", vo.getGolfAddr1());
				data.put("golfAddr2", vo.getGolfAddr2());
				data.put("useYn", vo.getUseYnNm());
				data.put("islYn", vo.getIslYn());
				data.put("zipCode", vo.getZipCode());
				dataArray.add(data);
			}
			
			obj.put("list",dataArray);
			obj.put("search", search);
			return  obj;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			obj.put("result", "fail");
			return obj;
		}
		
	}
	
	
	/**
	 * 골프장 조회 (개선)
	 * @param vo
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/select/golfList")
	@ApiOperation(value = "골프장 조회")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "확인 성공"),
	      @ApiResponse(code = 311, message = "잘못된 파라메터")
	})
	public JSONObject golfList2(@RequestBody GolfInfVO search, Model model) throws Exception {
		
		
		logger.debug("파라미터 확인용 : " + search.getType1());
	
		
			
			List<GolfInfVO> golfInfList = (List<GolfInfVO>)_gDao.selectBySearch("golfInf.selectBySearch", search, "selectListCount");
			
			JSONObject obj = new JSONObject();
			JSONArray dataArray = new JSONArray();
			
			try {	
				
				for(GolfInfVO vo : golfInfList) {
					JSONObject data = new JSONObject();
					data.put("golfSeq", vo.getGolfSeq());
					data.put("golfName", vo.getGolfName());
					data.put("golfTelnum", vo.getGolfTelnum());
					data.put("golfAddr1", vo.getGolfAddr1());
					data.put("golfAddr2", vo.getGolfAddr2());
					data.put("useYn", vo.getUseYnNm());
					data.put("islYn", vo.getIslYn());
					data.put("zipCode", vo.getZipCode());
					dataArray.add(data);
				}
			
			obj.put("list",dataArray);
			//obj.put("search", search);
			return  obj;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			obj.put("result", "fail");
			return obj;
		}
		
	}
	
	/**
	 * Object to Gson String 변환
	 * @param String
	 * @return
	 * @return
	 * @throws Exception
	 */
	private String toJson(Object obj){
		Gson gson = new Gson();
		return gson.toJson(obj);
	}
	
	public EgovMap gsonCallHttp(String urlAddr, String param, String apiKey) throws Exception{

		HttpGet httpGet = new HttpGet(urlAddr+"?barcode="+param);
		httpGet.addHeader("accept", "application/json");
		httpGet.addHeader("kdexp-secret", apiKey);
		httpGet.addHeader("charset", "utf-8");

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

		BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
	    String inputLine;
	    
        StringBuffer response = new StringBuffer();
 
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        
        reader.close();

        httpClient.close();
        
		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		
		EgovMap rtnMap = gson.fromJson(response.toString(), EgovMap.class);
		
		return rtnMap;
		
	}
}
