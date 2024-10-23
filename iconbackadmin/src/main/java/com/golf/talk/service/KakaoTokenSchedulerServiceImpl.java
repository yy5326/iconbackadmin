package com.golf.talk.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import javax.annotation.Resource;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.BaseServiceImpl;
import com.golf.talk.vo.KakaoInfoVO;

@Service("tokenSchedule")
public class KakaoTokenSchedulerServiceImpl extends BaseServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(KakaoTokenSchedulerServiceImpl.class);

	@Resource
	private GeneralDAOImpl _gDao;
	
	/**
	 * 경동 토큰 갱신
	 */
	@SuppressWarnings("unchecked")
	public JSONObject kdUpdateToken() {
		logger.info("[OAuth] token method is started...");
		//System.out.println("[OAuth] token method is started...");
		
		List<KakaoInfoVO> kakaoInfoList = (List<KakaoInfoVO>) _gDao.selectList("token.selectByKaKaoInfo", "");
		KakaoInfoVO infoVo = null;
        HttpURLConnection conn;
        JSONObject responseJson = null;
        String authorization = "";
        String contentType = "application/x-www-form-urlencoded";
        URL url = null;
        String tokenUrl = "";	//개발
        
        try {
        	
        	if(kakaoInfoList.size()>0) {
        		for(int i = 0; i<kakaoInfoList.size(); i++) {
                    // URL 설정
			        tokenUrl = kakaoInfoList.get(i).getTokenUrl();
			        
        			url = new URL(tokenUrl);
        			conn = (HttpURLConnection) url.openConnection();
        			
		            // HTTP POST 메소드 설정
		            conn.setRequestMethod("POST");
		            authorization = "Basic ".concat(encodingByBase64(kakaoInfoList.get(i).getClientId().concat(":").concat(kakaoInfoList.get(i).getClientId())));
		            
		            // HTTP Request Header 설정
		            conn.setRequestProperty("Authorization", authorization);
		            conn.setRequestProperty("Content-Type", contentType);
		
		            // POST 파라미터 전달을 위한 설정
		            conn.setDoInput(true);
		            conn.setDoOutput(true);
		
		            // HTTP Request Body 설정
		            DataOutputStream stream = new DataOutputStream(conn.getOutputStream());
		            String parameters = "grant_type=".concat("password")
		                        .concat("&password=").concat(kakaoInfoList.get(i).getPassword())
		                        .concat("&username=").concat(kakaoInfoList.get(i).getUserName())
		                        .concat("&version=").concat("v1");
		            stream.write(parameters.getBytes("UTF-8"));
		            stream.flush();
		            stream.close();

		            // HTTP 응답코드 받기
		            int responseCode = conn.getResponseCode();
		            // HTTP 응답코드에 따른 분기처리
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

		                infoVo = new KakaoInfoVO();
		                infoVo.setKakaoInfoSq(kakaoInfoList.get(i).getKakaoInfoSq());
		                infoVo.setToken(String.valueOf(responseJson.get("access_token")));
		                infoVo.setMode(super.getConfig().getWebMode());
		                _gDao.update("token.updateForToken", infoVo); 
		            } else {
		            	logger.info("An error occurred.");
		            	logger.info("response code: " + responseCode);
		            	logger.info("response message: " + (responseJson == null ? "nothing" : responseJson.toJSONString()));
		                //System.out.println("An error occurred.");
		                //System.out.println("response code: " + responseCode);
		                //System.out.println("response message: " + (responseJson == null ? "nothing" : responseJson.toJSONString()));
		            }
        		}
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        logger.info("[OAuth] token method is ended...");
        //System.out.println("[OAuth] token method is ended...");

        return responseJson;
    }
	
	private String encodingByBase64(String inputString) {
		
		logger.info("[OAuthVO] encodingByBase64 method is started...");
        //System.out.println("[OAuthVO] encodingByBase64 method is started...");

        byte[] bytes = new byte[0];

        try {
            bytes = inputString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Base64.Encoder encoder = Base64.getEncoder();
        String outputString = encoder.encodeToString(bytes);

        logger.info("[OAuthVO] encodingByBase64 method is ended...");
        //System.out.println("[OAuthVO] encodingByBase64 method is ended...");

        return outputString;
    }
}
