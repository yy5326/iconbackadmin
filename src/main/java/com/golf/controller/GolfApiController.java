package com.golf.controller;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.JSONParser;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.golf.model.common.PhoneVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;
import com.golf.service.impl.KdngeAPI;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.service.model.GolfReceiptPayVO;
import com.golf.talk.vo.SendRequestVO;
import com.golf.service.model.StatusVO;
import com.golf.util.EgovMapDeserializer;
import com.golf.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Data;

import com.golf.talk.controller.SendTalkController;
import com.golf.talk.vo.SendRequestVO;
import org.apache.commons.codec.binary.Base64;

import com.golf.service.impl.GolfApi;
import org.springframework.beans.factory.annotation.Value;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import com.golf.service.impl.GolfReceiptServiceImpl;

/**
 * API 컨트롤러
 * @author inferno
 *
 */
@RestController
public class GolfApiController extends PrtController  {

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	GolfReceiptServiceImpl _receiptService;

	@Resource
	SendTalkController _sendTalk;

	@Autowired
	private GolfApi golfApi;
	
	@Autowired
	private KdngeAPI kdngeApi;

	@Resource
	private CommonController common;

	@Value("#{globals['api.key']}")
	private String apiKey;

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	/** 영업소 물품 정보 수정
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/update/delivery", method = RequestMethod.POST, produces = "application/json; charset=utf8")
    public @ResponseBody ResponseEntity<JSONObject> updateDelivery(@RequestBody Map<String, Object> param) {
		String code = "200";
		Gson gson = new Gson();

		//logger.debug(param.toString());
		try {

			//보내주는 파라미터명이 상이하여 camelcase로 치환(EgovMap 내부에서 처리)
			Iterator<String> iter = param.keySet().iterator();
			EgovMap gm = new EgovMap();
			while(iter.hasNext()){
				String key = iter.next();
				if(!StringUtil.isEmpty(key)){
					gm.put("pd_deli_gubun".equals(StringUtils.lowerCase(key)) ? "payType" : key, param.get(key));
				}
			}

			GolfReceiptInfVO vo = gson.fromJson(gson.toJson(gm), GolfReceiptInfVO.class);
			logger.debug("convertParam =====> "+ gson.toJson(gm));
			logger.debug("convertParam =====> "+ vo.toString());

			if(!StringUtil.isEmpty(vo.getGolfUpdate())){
				GolfReceiptInfVO temp = _receiptService.selectReceiptKey(vo);
				if(temp.getRcpInfSeq() > 0){
					vo.setRcpInfSeq(temp.getRcpInfSeq());
				}else{
					return setJson("300");
				}

				List<GolfReceiptInfVO> golfReceiptList = (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectByKakao", vo);

				if(golfReceiptList.size()>0) {
					vo.setRcpSeq(golfReceiptList.get(0).getRcpSeq());
					vo.setRcpNumber(golfReceiptList.get(0).getRcpNumber());
					vo.setRcpInfSeq(golfReceiptList.get(0).getRcpInfSeq());
				}

				//상태변경(최초 운송장번호 생성 및 택배접수일 전달, 택배접수)
				if("U".equals(vo.getGolfUpdate())){
					vo.setRcpInfStat(StatusVO.Delivery.code());

					//골프백 사진 카톡 발송
					List<SendRequestVO> sendTalkList =  new ArrayList<SendRequestVO>();
					SendRequestVO sendTalk = new SendRequestVO();
					JSONObject requestSendMessage = new JSONObject();
			        ArrayList buttonList = new ArrayList();

					if(golfReceiptList.size()>0) {
						
						vo.setPdBarcode(vo.getRtNumber());
						System.out.println("업데이트 요청");

						// 도서산간운임비 초기화 되는 문제 해결
						GolfReceiptInfVO origin = (GolfReceiptInfVO)_gDao.selectOne("golfReceiptInf.selectByPrimaryKey", vo.getRcpInfSeq());

						if(origin != null) {
							// 기사님 앱에서 금액을 안 보내서 0으로 들어온 경우, DB에 있던 원래 금액으로 복구
							if(vo.getPdDosunPay() == 0) vo.setPdDosunPay(origin.getPdDosunPay());
							if(vo.getPdDeliPay() == 0)  vo.setPdDeliPay(origin.getPdDeliPay());
							if(vo.getPdPay() == 0)      vo.setPdPay(origin.getPdPay());
							if(vo.getPdEtcPay() == 0)   vo.setPdEtcPay(origin.getPdEtcPay());
							if(vo.getIconFee() == 0)    vo.setIconFee(origin.getIconFee());
						}
						_receiptService.updateReceipt(vo);

						//api전송
						vo.setRcpKind(golfReceiptList.get(0).getRcpKind());
						golfApi.updateApi(vo, "update_cbag_status");

						// 교참추가
						if(golfReceiptList.get(0).getRcpKind().equals("0") || golfReceiptList.get(0).getRcpKind().equals("5")) {
							vo.setCusSendNm(golfReceiptList.get(0).getCusReNm());//관리자에서 접수 한 경우 보내는 사람이름이 아이콘이여서 받는사람으로 변경
						}else {
							vo.setCusSendNm(golfReceiptList.get(0).getCusSendNm());
						}

						//수신고객명
						sendTalk.setCusSendNm(vo.getCusSendNm());
						//운송장번호
						sendTalk.setPdBarcode(vo.getRtNumber());
						//수신 연락처
						sendTalk.setReceiverNum("01047630961");
						//템플릿코드
						sendTalk.setEtc1("hdexp_0011");
						//예약자
						sendTalk.setReceiver(golfReceiptList.get(0).getCusReceiptNm());

						//수신번호 파라미터 암호화
						Base64 b = new Base64();
						String url = super.getConfig().getWebMode().equals("op") ? "https://kdhd.icexp.co.kr/golfCam/golfCamWrite.do" : "https://test.icexp.co.kr/golfCam/golfCamWrite.do";

						requestSendMessage.put("name","골프백 사진 등록");
						requestSendMessage.put("type","WL");
						requestSendMessage.put("url_mobile", url + "?rcpInfSeq="+vo.getRcpInfSeq()+"&codePhoto="+b.encodeBase64URLSafeString(rsaTranse(sendTalk.getReceiverNum())));

						buttonList.add(requestSendMessage);

						sendTalk.setButtonList(buttonList);

						sendTalkList.add(sendTalk);
						_sendTalk.sendFileUpload(sendTalkList);
					}
					//api 변경 요청 요망
					//_gDao.update("golfReceiptMst.updateSelective", vo);
				//상태변경(픽업취소)
				}else if("C".equals(vo.getGolfUpdate())){
					 //영업소 취소 요청 => 픽업취소 단계로 변경
					 vo.setRcpInfStat(StatusVO.PickupCancel.code());
					 vo.setCanReason("영업소 취소");
					 _receiptService.updateReceiptInfSel(vo, false);

				//상태변경(택배취소)
				}else if("D".equals(vo.getGolfUpdate())){
					 //영업소 삭제 요청 => 택배취소 단계로 변경
					 vo.setRcpInfStat(StatusVO.DeliveryCancel.code());
					 vo.setCanReason("영업소 발송자료 삭제");
					 _receiptService.updateReceiptInfSel(vo, false);

				}else{
					return setJson("100", new JSONObject(gm));
				}
			}else{
				return setJson("100");
			}
			return setJson(code, new JSONObject(gm));

		} catch (NullPointerException e) {
			e.printStackTrace();
			return setJson("100");
		} catch (Exception ee) {
			ee.printStackTrace();
			return setJson("905");
		}
    }

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
			
			
			EgovMap apiMap = kdngeApi.callUrl(kdngeApi.getApiUrl("api/v2/express/find/detail/"), vo.getPdBarcode(), "GET");

//			String url = super.getConfig().getWebMode().equals("op") ? "http://kdtracking.kditlab.com/Tracking" : "http://kdtracking.kditlab.com:81/tracking";
//			EgovMap apiMap = gsonCallHttp(url, vo.getRtNumber(), apiKey);
			System.out.println("에이피아이맵" + apiMap);
			if(!(apiMap == null)){
				
				JSONObject jsonObject = new JSONObject(apiMap);
				Object listData = jsonObject.get("trackingList");
				logger.debug("오브젝트1 "+ listData);
				
				org.json.JSONObject root = new org.json.JSONObject(new Gson().toJson(listData));
				org.json.JSONArray jarray = root.getJSONArray("listData");
			
				
				Map<String, Object> map = new HashMap<String , Object>();
				Map<String, Object> info = new HashMap<String , Object>();
				Map<String, Object> list = new HashMap<String, Object>();
				ArrayList<JSONObject> arrayJson = new ArrayList<JSONObject>();
				
				for(int j = 0 ; j < jarray.length(); j++) {
					
					JSONObject jObj = new JSONObject();
					jObj.put("regDate", jarray.getJSONObject(j).get("scanDt"));
					jObj.put("location", jarray.getJSONObject(j).get("strtPointNm"));
					jObj.put("scStat", "정상");
					jObj.put("scNm", null); // null
					jObj.put("tel", jarray.getJSONObject(j).get("strtPointTelno"));
					jObj.put("stat", getScanStatusName(jarray.getJSONObject(j).get("scanType").toString()));
					
					arrayJson.add(jObj);
				}
				list.put("listData", arrayJson);
				
				map.put("items", list);
				
		        
		        logger.debug("변환값 "+ map);
				
				JSONObject jsonObject2 = new JSONObject((Map) listData);
				Object listData2 = jsonObject2.get("listData");
				
				logger.debug("오브젝트2 "+ listData2);
			
				info.put("barcode", apiMap.get("prodCode"));
				info.put("branchStart", apiMap.get("sndnNm"));
				info.put("branchEnd", apiMap.get("arvlNm"));
				info.put("cnt", apiMap.get("count"));
				info.put("pdDt", apiMap.get("prodRegDt"));
				info.put("prod", apiMap.get("prodName"));
				info.put("reName", apiMap.get("rvCustNm"));
				info.put("sendName", apiMap.get("snCustNm"));
				info.put("recDt", null);
				info.put("relation", null);
				
				
//				map.put("items", apiMap.get("trackingList"));
				map.put("info", info);

				return toJson(map);
			}else{
				ArrayList<Map<String, Object>> listMap = new ArrayList<>();

				ArrayList<String> list = new ArrayList<>();

				Map<String, Object> map = new HashMap<String , Object>();

				map.put("list", list);

				Map<String, Object> map2 = new HashMap<String , Object>();

				logger.debug("list 확인 : " + map);
				
//				map2.put("items", map);


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
     * 스캔 타입 코드에 해당하는 설명 문자열을 반환
     * @param code 스캔 타입 코드 (예: "0001")
     * @return 상태 설명 문자열 (예: "예약접수")
     */
	public String getScanStatusName(String code) {
        if (code == null) return "알 수 없음";

        switch (code) {
            case "0001":
                return "예약접수";
            case "0002":
                return "발송";
            case "0003":
                return "터미널";
            case "0004":
                return "노선상차";
            case "0005":
                return "차량사고";
            case "0006":
                return "도착";
            case "0007":
                return "인수확인";
            case "0008":
                return "배송중";
            case "0009":
                return "회수";
            case "0010":
                return "발송취소";
            default:
                return "알 수 없음";
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

			// 2025.05.21 상공(코참) Type2 == korcham 일 경우 경주신라CC / 강동디아너스CC 만 조회
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
