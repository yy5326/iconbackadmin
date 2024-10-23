package com.golf.talk.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.StatusVO;
import com.golf.talk.message.KaKaoUtil;
import com.golf.talk.message.PayExpireMessage;
import com.golf.talk.message.PayRequestMessage;
import com.golf.talk.vo.SendRequestVO;
import com.golf.util.EgovMapDeserializer;
import com.golf.util.HolidayUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;

import com.golf.service.impl.GolfApi;

@Service("scTimeSchedule")
public class ScTimeSchedulerServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(KakaoTokenSchedulerServiceImpl.class);
	
	@Resource(name="globalProperty")
	GlobalProperty configBean;

	@Resource
	private GeneralDAOImpl _gDao;
	
	@Resource
	GolfReceiptServiceImpl _receiptService;

	@Autowired
	private GolfApi golfApi;
	
	public void scTime() {
		String rcpNumber = "";
		
		try {
			List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectscTimeList", "");
			
			JSONArray apiArray = new JSONArray();
			for(int i=0; i<receiptList.size();i++) {
				JSONObject requestSearch = new JSONObject();
				requestSearch.put("RCP_INF_SEQ", receiptList.get(i).getRcpInfSeq());
				requestSearch.put("PD_BARCODE", receiptList.get(i).getPdBarcode());
				apiArray.add(requestSearch);
			}
	
			if(apiArray.size()>0) {
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
				EgovMap apiMap = callUrl(getApiUrl("GetSCTime"), gson.toJson(apiArray));
				List<EgovMap> apiMapList = new ArrayList<EgovMap>();
				if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
					apiMapList = (List<EgovMap>)apiMap.get("listData");
				}
	
				for(int i = 0; i<apiMapList.size(); i++) {
					for(int j=0; j<receiptList.size();j++) {
						if(receiptList.get(j).getRcpInfSeq()==Integer.parseInt(apiMapList.get(i).get("rcpInfSeq").toString())) {
							if(apiMapList.get(i).get("scTime")!=null) {
								//update
								receiptList.get(j).setScTime(apiMapList.get(i).get("scTime").toString());
								receiptList.get(j).setRcpInfStat(StatusVO.Complete.code());
								_gDao.update("golfReceipt.updateGolfInfScTime", receiptList.get(j));
								
								receiptList.get(j).setCanReason("");
								golfApi.updateApi(receiptList.get(j), "update_cbag_status");
								
								//스케줄로 배송완료 체크 시
								_receiptService.insertGolfShceduleHistory(receiptList.get(j).getRcpNumber(), "배송완료(스케줄 업데이트)");							
							}
						}
					}
				}
			}
		}catch(Exception e) {
			System.out.println("scTimeSchedule Exception : " + e);
		}
	}
	
	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */

	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(configBean.getWebMode().equals("op") ? "https://kdapi.kditlab.com:447/RndGolf/" : "https://kdapi.kditlab.com:444/RndGolf/").append(url);
		return sb.toString();
	}
	
	/**
	 * api주소 분기 처리
	 * @param urlAddr, param
	 * @throws Exception
	 */

	public EgovMap callUrl(String urlAddr, String param) throws Exception{
		if(configBean.getWebMode().equals("local")){
			return gsonCallHttp(urlAddr, param);
		}else{
			return gsonCallHttps(urlAddr, param);
		}

	}
	
	public EgovMap gsonCallHttp(String urlAddr, String param) throws Exception{

		HttpURLConnection con;
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
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

		return rtnMap;
	}
	

	public EgovMap gsonCallHttps(String urlAddr, String param) throws Exception{

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
		con.setRequestMethod("POST");
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

		return rtnMap;
	}
}
