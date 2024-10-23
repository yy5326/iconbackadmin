package com.golf.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
import org.json.JSONObject;

import org.json.JSONArray;
//import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.json.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KdngeAPI extends BaseServiceImpl{
	
	
	private final GeneralDAOImpl _gDao;
	private final GolfReceiptServiceImpl _receiptService;
	


	// SetGolfDelivery
	/**
	 * 픽업요청 및 물품정보 전달
	 * @param infVO
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public @ResponseBody String setGolfDeliveryArr(GolfReceiptInfVO infVO) throws Exception {
		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVO);
		String returnVal = "success";
		
		for(GolfReceiptInfVO vo: receiptList) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("domeOverType", "D"); //골프택배 고정
			paramMap.put("prodType", "P");
			paramMap.put("isDealItem", "Y");
			paramMap.put("prodCode", null);
			paramMap.put("prodRegNo", vo.getRcpNumber()); // 접수번호를 줘야한다.
			paramMap.put("prodName", "골프택배");
			paramMap.put("prodMemo", vo.getPdMemo());
			paramMap.put("wrapStatus", "골프백");
			paramMap.put("price", vo.getPdIndivPrice());
			paramMap.put("branSndnCode",vo.getBrStrCode());
			paramMap.put("branArvlCode",vo.getBrEndCode());
			
//			작업중.....
			
			paramMap.put("RT_NUMBER", vo.getRtNumber());
			paramMap.put("CUS_SEND_NM", vo.getCusSendNm());
			paramMap.put("CUS_SEND_TELNUM", vo.getCusSendTelnum());
			paramMap.put("CUS_SEND_TELNUM2", vo.getCusSendTelnum2());
			paramMap.put("CUS_SEND_ZIP", vo.getCusSendZip());
			paramMap.put("CUS_SEND_ADDR1", vo.getCusSendAddr1());
			paramMap.put("CUS_SEND_ADDR2", vo.getCusSendAddr2());
			paramMap.put("CUS_RE_NM", vo.getCusReNm());
			paramMap.put("CUS_RE_TELNUM", vo.getCusReTelnum());
			paramMap.put("CUS_RE_TELNUM2", vo.getCusReTelnum2());
			paramMap.put("CUS_RE_ZIP", vo.getCusReZip());
			paramMap.put("CUS_RE_ADDR1", vo.getCusReAddr1());
			paramMap.put("CUS_RE_ADDR2", vo.getCusReAddr2());
			paramMap.put("BR_STR_CODE", vo.getBrStrCode());
			paramMap.put("BR_END_CODE", vo.getBrEndCode());
			paramMap.put("PD_DELI_EXPT_DT", vo.getPicReqDt().concat(" 12:00:00"));
			paramMap.put("PD_NM", vo.getPdNm());
			paramMap.put("PD_PACK", vo.getPdPack());
			paramMap.put("PD_CNT", vo.getPdCnt()+vo.getBostonCnt()); //골프백 + 보스턴백

			//2021-02-22 PD_BOHUM_PAY로 변경 paramMap.put("PD_ETC_PAY", vo.getPdEtcPay());		
			paramMap.put("PD_BOHUM_PAY", vo.getPdEtcPay());
			
			paramMap.put("PD_DOSUN_PAY", vo.getPdDosunPay());
			paramMap.put("PD_DELI_PAY", vo.getPdDeliPay());
			paramMap.put("PD_MEMO", vo.getPdMemo());
			paramMap.put("PD_DELI_GUBUN", vo.getPayType());
			paramMap.put("PD_INDIV_PRICE", vo.getPdIndivPrice());

			EgovMap apiMap = callUrl(getApiUrl("SetGolfDelivery"), gson.toJson(paramMap), "PUT");
			
			log.debug("api 요청(픽업) :" + gson.toJson(paramMap));
			System.out.println("api 요청(픽업) :" + gson.toJson(paramMap));
			
			log.debug("api 리턴 :" + apiMap);
			if(!apiMap.isEmpty()){
				//response code check
				//성공 데이터
				if("200".equals(apiMap.get("code").toString())){
					infVO.setRtNumber(apiMap.get("rtNumber").toString());
					infVO.setPdRecDt(apiMap.get("pdRecDt").toString());
					infVO.setRcpNumber(vo.getRcpNumber());
					infVO.setRcpInfSeq(vo.getRcpInfSeq());
					//픽업요청 단계로 변경
					infVO.setRcpInfStat(StatusVO.Pickup.code());
					infVO.setRcpKind(vo.getRcpKind());
					infVO.setRcpSeq(vo.getRcpSeq());

					_receiptService.updateReceiptInfSel(infVO, false);
										
					//_gDao.update("golfReceipt.updateGolfInf", infVO);
				}else{
					returnVal = vo.getRcpNumber() + "(" + apiMap.get("code").toString() + ")";
				}
			}else{
				returnVal = vo.getRcpNumber() + "(" + apiMap.get("code").toString() + ")";
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
			result.put("brNm", apiMapList.get(0).get("brandName").toString());
		}
		return result;
	}
	
	
	// GetExtraCharge
	
	// DeleteGolfDelivery

	// GetSCTime
	
	
	
	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */
	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(super.getConfig().getWebMode().equals("op") ? "http://211.62.97.193:8050/" : "http://211.62.97.193:8050/").append(url);
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
		JSONObject obj = new JSONObject(param);
		String searchType  = (String) obj.get("SEARCH_TYPE");
		String searchValue = (String) obj.get("SEARCH_WORD");
		
		HttpURLConnection con;
		URL url = new URL(urlAddr+searchType+"/"+URLEncoder.encode(searchValue, "UTF-8"));
//		log.debug("인코딩 UTL {}", url);
		con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("X-AUTH-SECRET-KEY", super.getConfig().getSecretKey());
        con.setRequestProperty("Content-Type", "application/json");	//content-Type 설정설정
        con.setRequestProperty("charset", "utf-8");
        con.setDoInput(true);
        con.setDoOutput(true);	// 서버에서 온 데이터를 출력할 수 있는 상태인지
        con.setRequestMethod(method);	// GET / POST / PUT /DELETE
    	
//      log.debug("# 요청 url 원본 : {}" , url);        
//		log.debug("# 요청 메소드 : {}" , con.getRequestMethod());
		
		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					sb.append(responseLine.trim());
					log.debug("## {}", responseLine);
				}
		}catch(Exception e) {
			e.getStackTrace();
		}
		log.debug("response : {}", sb.toString());
		con.disconnect();
		
		if(sb.toString() == "") {
			return null;
		}
		
		EgovMap map = new EgovMap();
		
		
		try {
			map.put("list", new JSONObject(sb.toString()).optJSONObject("data").optJSONArray("list"));
		}catch(Exception e) {
			e.getStackTrace();
			map.put("list", null);
		}
		
		log.debug("리스트 {}",map);
		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(map.get("list").toString(), EgovMap.class);
		
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
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setRequestProperty("X-AUTH-SECRET-KEY", super.getConfig().getSecretKey());
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("charset", "utf-8");
		con.setDoInput(true);
		con.setDoOutput(true);

		try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
			os.write(param);
			os.flush();
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}
		con.disconnect();

		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(sb.toString(), EgovMap.class);

		return  rtnMap;
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
	
}
