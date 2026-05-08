package com.golf.service.impl;

import java.beans.Encoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.golf.service.model.StatusPayVO;
import com.golf.talk.service.SendTalkServiceImpl;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.StatusVO;
import com.golf.util.EgovMapDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mysql.cj.xdevapi.JsonArray;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.json.JSONObject;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
@RequiredArgsConstructor
@Slf4j
public class KdngeAPI extends BaseServiceImpl{

	private final GeneralDAOImpl _gDao;
	private final GolfReceiptServiceImpl _receiptService;
	private final SendTalkServiceImpl _sendTalkService;

	// SetGolfDelivery
	/**
	 * 픽업요청 및 물품정보 전달
	 * @param infVO
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public @ResponseBody String setGolfDeliveryArr(GolfReceiptInfVO infVO) throws Exception {

//		List<Integer> list = new ArrayList<>();
//		list.add(10228);
//		list.add(10229);
//		infVO.setRcpInfSeqArray(list);

		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVO);
		String returnVal = "success";
//		log.debug("priceArr {}", priceList);

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		JSONObject paramMap = new JSONObject();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

//			log.debug("반복 : {}", i);

		for (GolfReceiptInfVO vo : receiptList) {

			String totalIndivPrice = vo.getPdIndivPrice();
			int pdCount = vo.getPdCnt();
			int bstnCount = vo.getBostonCnt();

			String[] priceArr = totalIndivPrice.split("[|]");
			List<String> priceList = new ArrayList<>(Arrays.asList(priceArr));

			if ("".equals(priceArr[0])) {
				priceList.remove(0);
			}

			for (int i = 0; i < priceList.size(); i++) {
				paramMap = new JSONObject();

				if (pdCount == 0 && bstnCount == 0)
					break;

				paramMap.put("prodName", "국제골프택배기본(" + vo.getPdPack() + ")");
				paramMap.put("wrapStatus", vo.getPdPack());
				paramMap.put("isDealItem", "Y");

				paramMap.put("width", 40);
				paramMap.put("vertical", 40);
				paramMap.put("height", 160);
				paramMap.put("weight", 15);

				if ("100".equals(priceList.get(i))) {
					bstnCount--;
					paramMap.put("prodName", "보스턴백");
//					paramMap.put("otherPay", vo.getIconFee());
					paramMap.put("height", 80);
					paramMap.put("weight", 10);

				} else {
					if (pdCount == 0) {
						bstnCount--;
						paramMap.put("prodName", "보스턴백");
//						paramMap.put("otherPay", vo.getIconFee());
						paramMap.put("height", 80);
						paramMap.put("weight", 10);
					} else {
						pdCount--;
//						paramMap.put("otherPay", vo.getPdDeliPay());
					}
				}

				// 캐리어 관련 로직
				if (vo.getPrdType() == 3) {
					paramMap.put("price", "200");
					paramMap.put("prodName", "아이콘캐리어(중형내륙)");
				} else {
					paramMap.put("price", priceList.get(i));
				}

				paramMap.put("domeOverType", "D"); // 골프택배 고정
				paramMap.put("prodCode", null);
				paramMap.put("prodRegNo", vo.getRcpNumber()); // 접수번호를 줘야한다.
				paramMap.put("prodMemo", "".equals(vo.getPdMemo()) ? null : vo.getPdMemo());

				paramMap.put("calcType", "0001");

				// paramMap.put("price", vo.getPdIndivPrice());
				paramMap.put("branSndnCode", vo.getBrStrCode());
				paramMap.put("branArvlCode", vo.getBrEndCode());

				paramMap.put("custSndrName", vo.getCusSendNm());
				paramMap.put("custSndrTelno", vo.getCusSendTelnum());
				paramMap.put("custSndrEtcTelno", vo.getCusSendTelnum2());
				paramMap.put("custSndrZip", vo.getCusSendZip());
				paramMap.put("custSndrAddr", vo.getCusSendAddr1());
				paramMap.put("custSndrDaddr", vo.getCusSendAddr2());

				paramMap.put("custRcvrName", vo.getCusReNm());
				paramMap.put("custRcvrTelno", vo.getCusReTelnum());
				paramMap.put("custRcvrEtcTelno", vo.getCusReTelnum2());
				paramMap.put("custRcvrZip", vo.getCusReZip());
				paramMap.put("custRcvrAddr", vo.getCusReAddr1());
				paramMap.put("custRcvrDaddr", vo.getCusReAddr2());

				paramMap.put("payType", "0001");
				paramMap.put("payMethod", "0001");

//				paramMap.put("etcPay", vo.getPdEtcPay());
//				paramMap.put("otherPay", vo.getPdDeliPay());
				paramMap.put("etcPay", "0");
				paramMap.put("otherPay", "0");

				jsonArray.add(jsonParser.parse(paramMap.toString()));
			}
		}

		jsonObject.put("list", jsonParser.parse(jsonArray.toString()));
		log.debug("put 요청 바디 Object {}", gson.toJson(jsonObject));

		EgovMap apiMap = callUrl(getApiUrl("api/v1/express/returns"), gson.toJson(jsonObject), "PUT");

		log.debug("api 리턴 :" + gson.toJson(apiMap));


		if(apiMap != null ) {

			if (apiMap.get("listData") != null && ((List<EgovMap>) apiMap.get("listData")).size() > 0) {

				List<EgovMap> apiMapList = (List<EgovMap>) apiMap.get("listData");

				if(receiptList != null && apiMapList != null) {

					for (int i = 0; i < apiMapList.size(); i++) {

						log.debug("test #### {}", i);

						//GolfReceiptInfVO vo = receiptList.get(i);
						EgovMap egovMap = apiMapList.get(i);

						if ("Y".equals(egovMap.get("result"))) {

							JSONObject rObj = new JSONObject();
							rObj = MapToJson((Map<String, Object>) egovMap.get("content"));

							log.debug("업데이트 내용 {}", new Gson().toJson(rObj));
							log.debug("업데이트 내용2 {}", rObj.get("prodRegNo").toString());
							infVO.setRtNumber(rObj.get("prodCode").toString());
							infVO.setPdBarcode(rObj.get("prodCode").toString());
							infVO.setPdRecDt(rObj.get("rsvtRegDt").toString());
							infVO.setRcpNumber(rObj.get("prodRegNo").toString());
							infVO.setRcpInfStat(StatusVO.Pickup.code());

							GolfReceiptInfVO receipt =  (GolfReceiptInfVO) _gDao.selectOne("golfReceiptInf.selectDetailByKeyForKdnge", infVO.getRcpNumber());
							//String rcpToInfSeq = rObj.get("prodRegNo").toString().substring(10);
//
							infVO.setRcpInfSeq(receipt.getRcpInfSeq());
							infVO.setRcpKind(receipt.getRcpKind());
							infVO.setRcpSeq(receipt.getRcpSeq());

							_receiptService.updateReceiptInfSel(infVO, false);
						} else {
							returnVal = infVO.getRcpNumber() + "(" + String.valueOf(apiMap.get("code")) + ")";
						}
					}
				}
			}
		}

		return toJson(returnVal);
	}


	/**
	 * GetBranchInfo
	 * 영업소 조회
	 * method - get
	 * @param  -
	 * 		{corpType}    : KD:경동 / HD:합동
	 * 		{searchType}  : code:영업소 코드 / name:영업소 이름 / zipcode:우편번호
	 *		{searchValue} : 검색어
	 * URL    - api/v1/branch/find/{corpType}/{searchType}/{searchValue}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getBranchInfo(GolfInfVO infVO) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("SEARCH_TYPE", infVO.getType1());
		paramMap.put("SEARCH_WORD", infVO.getSearchWord());

		log.info("타입확인, {} ", infVO.getType1());
		EgovMap apiMap = callUrl(getApiUrl("api/v1/branch/find/kd/"), gson.toJson(paramMap), "GET");


		log.info("## 전송 메시지 : {}", apiMap);


		if(!"S".equals(apiMap.get("result"))) {
			log.info("### 영업소 조회 실패");
			log.info("### 에러 코드  : {}", apiMap.get("code"));
			log.info("### 에러메시지  : {}", apiMap.get("message"));
			log.info("### 골프장seq  : {}", infVO.getGolfSeq());
		};

		if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
			List<EgovMap> apiMapList = (List<EgovMap>)apiMap.get("listData");
			result.put("brCode", apiMapList.get(0).get("branCode").toString());
			result.put("brNm", apiMapList.get(0).get("branName").toString());paramMap.put("SEARCH_TYPE", infVO.getType1());
		}
		return result;
	}


	/**
	 * GetExtraCharge
	 * 할증 운임 조회
	 * method - get
	 * @param  -
	 * URL    - api/v1/fare/calculator/disc
	 */
	@SuppressWarnings("unchecked")
	public @ResponseBody String getExtraPrice(GolfReceiptInfVO vo) throws Exception {

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

		String totalIndivPrice = vo.getPdIndivPrice();
		String[] priceArr = totalIndivPrice.split("[|]");
		List<String> priceList = new ArrayList<>(Arrays.asList(priceArr));
		if("".equals(priceArr[0])){
			priceList.remove(0);
		}

		JSONObject paramMap = new JSONObject();

		long TotalPrice = 0;
		String rspCode ="200";
		for (String s : priceList) {
			paramMap.put("kdhdType", "KD");
			paramMap.put("payType", null);
			paramMap.put("branSndnSido", null);
			paramMap.put("branArvlSido", null);

			// 고정
			paramMap.put("width", "1");
			paramMap.put("vertical", "1");
			paramMap.put("height", "1");
			paramMap.put("weight", 1.0);

			// 반복처리 응답오면 금액 합쳐야함
			paramMap.put("price", s);
			try {
				EgovMap apiMap = callUrl(getApiUrl("api/v1/fare/calculator/disc"), gson.toJson(paramMap), "POST");
				TotalPrice += (long) apiMap.get("discPay");
			} catch (Exception e) {
				e.printStackTrace();
				rspCode = "API Service Error";
			}

		}

		JSONObject returnObj = new JSONObject();
		returnObj.put("code", rspCode);
		returnObj.put("pdExtraCharge", TotalPrice);

		if(!returnObj.isEmpty()){
			return toJson(returnObj);
		}else{
			return "false";
		}
	}
	// 2025.04.03 접수시 할증가 추가.
	public int getExtraPrice(String pdIndivPrice) {

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

		String totalIndivPrice = pdIndivPrice;
		String[] priceArr = totalIndivPrice.split("[|]");
		List<String> priceList = new ArrayList<>(Arrays.asList(priceArr));
		if("".equals(priceArr[0])){
			priceList.remove(0);
		}

		JSONObject paramMap = new JSONObject();

		long TotalPrice = 0;
		String rspCode ="200";
		for (String s : priceList) {
			paramMap.put("kdhdType", "KD");
			paramMap.put("payType", null);
			paramMap.put("branSndnSido", null);
			paramMap.put("branArvlSido", null);

			// 고정
			paramMap.put("width", "1");
			paramMap.put("vertical", "1");
			paramMap.put("height", "1");
			paramMap.put("weight", 1.0);

			// 반복처리 응답오면 금액 합쳐야함
			paramMap.put("price", s);
			try {
				EgovMap apiMap = callUrl(getApiUrl("api/v1/fare/calculator/disc"), gson.toJson(paramMap), "POST");
				TotalPrice += (long) apiMap.get("discPay");
			} catch (Exception e) {
				e.printStackTrace();
				rspCode = "API Service Error";
			}

		}

		JSONObject returnObj = new JSONObject();
		returnObj.put("code", rspCode);
		returnObj.put("pdExtraCharge", TotalPrice);

		if(!returnObj.isEmpty()){
			return Integer.parseInt(returnObj.get("pdExtraCharge").toString());
		}else{
			return 0;
		}
	}

	// DeleteGolfDelivery
	@SuppressWarnings("unchecked")
	public @ResponseBody String deleteGolfDelivery(GolfReceiptInfVO infVO) throws Exception {
		GolfReceiptInfVO vo = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", infVO.getRcpInfSeq());

		//성공 데이터
		String returnVal = "success";

		//골프접수 -> 골프취소
		if(StatusVO.Receipt.code().equals(vo.getRcpInfStat()) || StatusVO.Wait.code().equals(vo.getRcpInfStat())){

			//골프취소 단계로 변경
			infVO.setRcpInfStat(StatusVO.ReceiptCancel.code());
			infVO.setPrdType(vo.getPrdType());

			_receiptService.updateReceiptInfSel(infVO, false);

			// 골프접수 상태변경시 알림톡 전송
			/*if ( !super.getConfig().getWebMode().equals("op") ) {
				_sendTalkService.deleteGolfDeliveryForCancel(vo);
			}*/

			//스마트스코어인 경우 알림톡 발송 안함
			if(!"2".equals(infVO.getRcpKind())) {
				_sendTalkService.deleteGolfDeliveryForCancel(vo);
			}

			return toJson(returnVal);
			//픽업요청 -> 픽업취소
		}else{
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
			JSONObject paramMap = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONParser jsonParser = new JSONParser();

			log.debug("RCP NUM !! : {}", vo.getRcpNumber());
			jsonArray.add(vo.getRcpNumber());

			//paramMap.put("prodCodes", vo.getRcpNumber());
			paramMap.put("prodRegNos", jsonArray);

			EgovMap apiMap = callUrl(getApiUrl("api/v1/express/regno/cancels"), gson.toJson(paramMap), "DELETE");
			log.debug("api 리턴 :" + gson.toJson(apiMap));

			if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
				List<EgovMap> apiMapList = (List<EgovMap>) apiMap.get("listData");

				if("API_PRC_S000".equals(apiMapList.get(0).get("code"))) {
					infVO.setRtNumber(apiMapList.get(0).get("prodCode").toString());
					//픽업취소 단계로 변경 및 취소사유 등록
					infVO.setRcpInfStat(StatusVO.PickupCancel.code());
					//픽업취소 시 입금대기 상태면 결제 상태값은 변경 안함
					_receiptService.updateReceiptInfSel(infVO, !StatusPayVO.stay.code().equals(vo.getPayStat()) ? true : false);

				}else if("N".equals(apiMapList.get(0).get("result"))){
					if("API_PRC_E009".equals(apiMapList.get(0).get("code"))){
						returnVal = "error1";
					}else{
						returnVal = apiMapList.get(0).get("message").toString();
					}


				}else {
					returnVal = "API Service Error";
				}

			}


		}
		return toJson(returnVal);
	}



	// GetSCTime


	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */
	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(super.getConfig().getWebMode().equals("op") ? "https://ong-api.kdexp.com/" : "https://dev-ong-api.kdexp.com/").append(url);
		return sb.toString();
	}



	/**
	 * api주소 분기 처리
	 * @param urlAddr, param, method
	 * @throws Exception
	 */

	public EgovMap callUrl(String urlAddr, String param, String method) throws Exception{
		if(super.getConfig().getWebMode().equals("local")){

			log.debug("전송 파람 {} ",param);
			return gsonCallHttp(urlAddr, param, method);
		}else{
			return gsonCallHttps(urlAddr, param, method);
		}
	}

	public EgovMap gsonCallHttp(String urlAddr, String param, String method) throws Exception{
		HttpURLConnection con;
		URL url;

		// 영업소조회시 인코딩이 필요함(GET 요청)
		if("GET".equals(method)){
			if(urlAddr.contains("/v2/express/find/detail/")) {
			
				url = new URL(urlAddr+param);
			}else {
				org.json.JSONObject obj = new org.json.JSONObject(param);
				String searchType  = (String) obj.get("SEARCH_TYPE");
				String searchValue = (String) obj.get("SEARCH_WORD");
				url = new URL(urlAddr+searchType+"/"+URLEncoder.encode(searchValue, "UTF-8"));
			}
			
			
		}else {
			url = new URL(urlAddr);
		}



//		log.debug("인코딩 UTL {}", url);
		con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("X-AUTH-SECRET-KEY", super.getConfig().getSecretKey());
        con.setRequestProperty("Content-Type", "application/json");	//content-Type 설정설정
		con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("charset", "utf-8");
        con.setDoInput(true);
        con.setDoOutput(true);	// 서버에서 온 데이터를 출력할 수 있는 상태인지
        con.setRequestMethod(method);	// GET / POST / PUT /DELETE

      	log.debug("# 요청 url 원본 : {}" , url);
		log.debug("# 요청 메소드 : {}" , con.getRequestMethod());
		if(!"GET".equals(method)){
			try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
				//os.write(URLEncoder.encode(param, "UTF-8"));
				//os.write(String.valueOf(param.getBytes(StandardCharsets.UTF_8)));
				os.write(param);
				os.flush();
			}
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

				String responseLine = null;

				while ((responseLine = br.readLine()) != null) {
					sb.append(responseLine.trim());
					log.debug("## {}", responseLine);
				}
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("응답 Exp");
		}
		log.debug("response : {}", sb.toString());
		con.disconnect();

		if(sb.toString() == "") {
			return null;
		}

		EgovMap map = new EgovMap();



		try {
			map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data").optJSONArray("list"));

		}catch (Exception e) {
			e.getStackTrace();
			try{
				map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data"));
			}catch (Exception ie){
				ie.getStackTrace();
				map.put("list", null);
			}
		}

		if(map.get("list") == null){
			try {
			map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data"));
			}catch(Exception e) {
				e.getStackTrace();
				map.put("list", null);
			}
		}

		if(map.get("list") == null){
			try {
				map.put("list", new org.json.JSONObject(sb.toString()).optJSONArray("data"));
			}catch(Exception e) {
				e.getStackTrace();
				map.put("list", null);
			}
		}
		log.debug("리스트 {}",map);
		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap =null;
		if (map.get("list") != null) {
			 rtnMap = gson.fromJson(map.get("list").toString(), EgovMap.class);
		}
		

		return rtnMap;
	}

	public EgovMap gsonCallHttps(String urlAddr, String param, String method) throws Exception{

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HttpURLConnection con;
		URL url;

		// 영업소조회시 인코딩이 필요함(GET 요청)
		
		
		if("GET".equals(method)){
			if(urlAddr.contains("/v2/express/find/detail/")) {
			
				url = new URL(urlAddr+param);
			}else {
				org.json.JSONObject obj = new org.json.JSONObject(param);
				String searchType  = (String) obj.get("SEARCH_TYPE");
				String searchValue = (String) obj.get("SEARCH_WORD");
				url = new URL(urlAddr+searchType+"/"+URLEncoder.encode(searchValue, "UTF-8"));
			}
		}else {
			url = new URL(urlAddr);
		}

//		log.debug("인코딩 UTL {}", url);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("X-AUTH-SECRET-KEY", super.getConfig().getSecretKey());
		con.setRequestProperty("Content-Type", "application/json");	//content-Type 설정설정
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("charset", "utf-8");
		con.setDoInput(true);
		con.setDoOutput(true);	// 서버에서 온 데이터를 출력할 수 있는 상태인지
		con.setRequestMethod(method);	// GET / POST / PUT /DELETE

		log.debug("# 요청 url 원본 : {}" , url);
		log.debug("# 요청 메소드 : {}" , con.getRequestMethod());
		if(!"GET".equals(method)){
			try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
				//os.write(URLEncoder.encode(param, "UTF-8"));
				//os.write(String.valueOf(param.getBytes(StandardCharsets.UTF_8)));
				os.write(param);
				os.flush();
			}
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {

			String responseLine = null;

			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
				log.debug("## {}", responseLine);
			}
		}catch(Exception e) {
			e.printStackTrace();
			log.debug("응답 Exp");
		}
		log.debug("response : {}", sb.toString());
		con.disconnect();

		if(sb.toString() == "") {
			return null;
		}

		EgovMap map = new EgovMap();

		try {
			map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data").optJSONArray("list"));

		}catch (Exception e) {
			e.getStackTrace();
			try{
				map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data"));
			}catch (Exception ie){
				ie.getStackTrace();
				map.put("list", null);
			}
		}

		if(map.get("list") == null){
			try {
				map.put("list", new org.json.JSONObject(sb.toString()).optJSONObject("data"));
			}catch(Exception e) {
				e.getStackTrace();
				map.put("list", null);
			}
		}

		if(map.get("list") == null){
			try {
				map.put("list", new org.json.JSONObject(sb.toString()).optJSONArray("data"));
			}catch(Exception e) {
				e.getStackTrace();
				map.put("list", null);
			}
		}
		log.debug("리스트 {}",map);
		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(map.get("list").toString(), EgovMap.class);

		return rtnMap;
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

	public JSONObject MapToJson(Map<String, Object> map) {

		JSONObject json = new JSONObject();
		String key = "";
		Object value = null;
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			json.put(key, value);
		}
		return json;
	}

	/**
	 * 짐캐리 송장(라벨) 출력 명령어 조회
	 * @param prodCode 운송장번호 (RT_NUMBER)
	 * @return 프린터 제어 명령어 (Raw Text)
	 * @throws Exception
	 */
	public String getZimcarryLabelData(String prodCode) throws Exception {
		String labelContent = "";
		try {
			// 1. 파라미터 세팅
			String endPoint = "api/v1/label/print/LMTP0003/0002";
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			jsonArray.add(prodCode);
			jsonObject.put("prodCodes", jsonArray);

			Gson gson = new GsonBuilder().serializeNulls().create();
			Object apiResponse = callUrl(getApiUrl(endPoint), gson.toJson(jsonObject), "POST");

			if (apiResponse == null) return "ERROR: API 응답 없음";

			Map<String, Object> dataMap = (Map<String, Object>) apiResponse;
			Object contentsObj = dataMap.get("contents");
			Map<String, Object> targetItem = null;

			if (contentsObj instanceof Map) {
				Map<String, Object> contentsMap = (Map<String, Object>) contentsObj;
				// 로그에서 확인된 'listData' 키 내부의 리스트를 추출
				if (contentsMap.containsKey("listData")) {
					Object listDataObj = contentsMap.get("listData");
					if (listDataObj instanceof List && !((List) listDataObj).isEmpty()) {
						targetItem = (Map<String, Object>) ((List) listDataObj).get(0);
					}
				} else {
					targetItem = contentsMap; // listData가 없는 일반 Map인 경우

				}
			} else if (contentsObj instanceof List) {
				List<Map<String, Object>> contentsList = (List<Map<String, Object>>) contentsObj;
				if (!contentsList.isEmpty()) targetItem = contentsList.get(0);
			}

			// 3. 데이터 추출
			if (targetItem != null) {
				String resCode = String.valueOf(targetItem.get("resultCode"));
				if ("200".equals(resCode)) {
					labelContent = (String) targetItem.get("content");
				} else {
					String resMsg = (String) targetItem.get("resultMessage");
					return "ERROR: 경동 API 에러(" + resCode + ") - " + resMsg;
				}
			} else {
				return "ERROR: 송장 데이터 구조 분석 실패 (listData를 찾을 수 없음)";
			}

		} catch (Exception e) {
			log.error("라벨 출력 프로세스 에러", e);
			return "ERROR: " + e.getMessage();
		}

		if (labelContent == null || labelContent.trim().isEmpty() || "null".equals(labelContent)) {
			return "ERROR: 송장 HTML 내용이 비어있습니다.";
		}
		return labelContent.trim();
	}

}
