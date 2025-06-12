package com.golf.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.poi.util.SystemOutLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.golf.service.model.GolfReceiptInfVO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;

import com.golf.chat.vo.CbDeliveryVO;
import com.golf.service.model.GolfHistoryVO;
import com.golf.util.EgovMapDeserializer;

/**
 * 골프택배 외부 API
 * @author kokuma84
 *
 */
@Service("golfApi")
public class GolfApi extends BaseServiceImpl {

	@Resource
	GolfReceiptServiceImpl _receiptService;

	private static final Logger logger = LoggerFactory.getLogger(GolfApi.class);

	/**
	 * 외부업체 update API
	 * @param mstVO
	 * @param list
	 * @throws Exception
	 */
	@Async
	public void updateApi(GolfReceiptInfVO infVO, String url) throws Exception {
		System.out.println("실행순서..");
		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumber(infVO.getRcpNumber());
		golfHistoryVO.setRegUser("API");
		golfHistoryVO.setHistoryMemo("API 전송오류");

		String chk = "0";

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			if(null == infVO.getRcpKind()) {
				GolfReceiptInfVO api =  (GolfReceiptInfVO)getDAO().selectOne("golfReceiptInf.selectDetailByRcpNumber", infVO.getRcpNumber());
				infVO.setRcpKind(api.getRcpKind());
			}
			if("2".equals(infVO.getRcpKind()) || "0".equals(infVO.getRcpKind()) || "5".equals(infVO.getRcpKind())) { // 교참추가...

				chk = "1";

				paramMap.put("rcpNumber", infVO.getRcpNumber()); //아이콘접수번호

				chk = "2";
				//아이콘 상태변경 이라 되어있는데, 스마트스코어가 아닌가.. ?
				if("update_cbag_status".equals(url)) {
					paramMap.put("rcpInfStat", infVO.getRcpInfStat()); //아이콘접수상태변경
					paramMap.put("rtNumber", infVO.getRtNumber()); //영업소 회수번호
					paramMap.put("pdBarcode", infVO.getPdBarcode()); //운송장번호
					paramMap.put("canReason", infVO.getCanReason()); //취소사유

					System.out.println("파람맵" + new Gson().toJson(paramMap));
				}else if("regi_pickup_picture".equals(url)) {
					//골프백 사진 정보 전송
					paramMap.put("pdBarcode", infVO.getPdBarcode()); //운송장번호

					List<CbDeliveryVO> golfPhotoList = (List<CbDeliveryVO>)getDAO().selectList("cbDelivery.selectByGolfPhoto", infVO);
					String golfPhoto = "";

					if(golfPhotoList.size()>0) {
						for(CbDeliveryVO vo : golfPhotoList) {
							golfPhoto += super.getConfig().getHost()+"/upload/files/pickup/"+Integer.toString(infVO.getRcpInfSeq())+"/"+vo.getGolfPhoto()+"|";
						}
						golfPhoto = golfPhoto.substring(0, golfPhoto.length()-1);
					}

					paramMap.put("golfPhoto", golfPhoto); //사진 URL
				}else if("update_receipt".equals(url)) {
					//아이콘 수정
					paramMap.put("cusSendNm", infVO.getCusSendNm()); //보내는 사람 이름
					paramMap.put("cusSendTelNum", infVO.getCusSendTelnum()); //보내는 사람 전화번호
					paramMap.put("cusSendAddr1", infVO.getCusSendAddr1()); //보내는곳 주소
					paramMap.put("cusSendAddr2", infVO.getCusSendAddr2()); //보내는곳 상세 주소
					paramMap.put("cusSendZip", infVO.getCusSendZip()); //보내는곳 우편번호

					//smartscore 측 api에서 필요없는데이터 .
					//paramMap.put("brStrNm", infVO.getBrStrNm()); //발송영업소명

					paramMap.put("cusReNm", infVO.getCusReNm()); //받는 사람 이름
					paramMap.put("cusReTelNum", infVO.getCusReTelnum2()); //받는 사람 전화번호
					paramMap.put("cusReAddr1", infVO.getCusReAddr1()); //받는곳 주소
					paramMap.put("cusReAddr2", infVO.getCusReAddr2()); //받는곳 상세 주소
					paramMap.put("cusReZip", infVO.getCusReZip()); //받는곳 우편번호
					//smartscore 측 api에서 필요없는데이터 .
					//paramMap.put("brEndNm", infVO.getBrEndNm()); //도착영업소명

					paramMap.put("extComp", infVO.getExtComp()); //외부업체
					paramMap.put("extCompTelNum", infVO.getExtCompTelnum()); //외부업체 연락처
					paramMap.put("gameDt", infVO.getGameDt()); //경기일
					paramMap.put("picReqDt", infVO.getPicReqDt()); //픽업희망일
					paramMap.put("pdMemo", infVO.getPdMemo()); //메모

					System.out.println("파람맵 " + new Gson().toJson(paramMap));

				}
				chk = "3";
				//스마트스코어 API URL
				if("2".equals(infVO.getRcpKind())) {
					// local
					infVO.setOutApi("https://td29.smartscore.kr/ss/cbag/external/icon/");

					// op
					if(super.getConfig().getWebMode().equals("op")) {
						infVO.setOutApi("https://psn.smartscore.kr/ss/cbag/external/icon/");
					}
					// dev
					if(super.getConfig().getWebMode().equals("dev")) {
						infVO.setOutApi("https://td29.smartscore.kr/ss/cbag/external/icon/");
					}
				}

				// 2025.05.21 교참추가 RCP_KIND = 5
				// 골프전산 -> 아이콘택배로 상태 변경값을 전달하는 API
				// api/update/receiptState/update_cbag_status
				if("0".equals(infVO.getRcpKind()) || "5".equals(infVO.getRcpKind())) {
//					logger.debug("WebMode 모드 : {}" , super.getConfig().getWebMode());
					if(super.getConfig().getWebMode().equals("op")) {
						logger.info("골프전산 ->> 접수 [상태변경 API]");
						infVO.setOutApi("http://127.0.0.1:18080/api/update/receiptState/");
					}else {
						infVO.setOutApi("https://test.icexp.co.kr/api/update/receiptState/");
//							infVO.setOutApi("http://localhost:7777/api/update/receiptState/");
					}

					//infVO.setOutApi("http://localhost:8095/api/update/receiptState/");
					// 아이콘 접수 사이트는 .do 사용
					url = url+".do";
				}

				System.out.println("paramMap : " + paramMap);

				chk = "4";


				logger.info("발송전 url확인" + infVO.getOutApi()+url);
				int apiMap;

				//왕복2차 고정.
				if(infVO.getRcpNumberArray().size() == 2){
					paramMap.put("rcpNumber", infVO.getRcpNumberArray().get(0));
					apiMap = callUrl(infVO.getOutApi()+url, gson.toJson(paramMap));
					logger.info("통신 통과 {}", apiMap);
					 GolfReceiptInfVO rcpStatChk =  (GolfReceiptInfVO)getDAO().selectOne("golfReceiptInf.selectDetailByRcpNumber", infVO.getRcpNumberArray().get(0));
					 //왕복1차가 배송완료인 상태에서 왕복2차를 접수취소하면 1차만 데이터 던져준다
					 if(!rcpStatChk.getRcpInfStat().equals('3')){
						 paramMap.put("rcpNumber", infVO.getRcpNumberArray().get(1));
						 apiMap = callUrl(infVO.getOutApi()+url, gson.toJson(paramMap));
						 System.out.println("왕복1차 배송완료테스트" + apiMap);
					 }
				}else {
					//스마트스코어에서 골프취소 하면 api 요청안함
					if("7".equals(infVO.getRcpInfStat()) && "2".equals(infVO.getRcpKind()))
						{
							//취소 왜 나눠놨는지 의문
							apiMap = callUrl(infVO.getOutApi()+url, gson.toJson(paramMap));
							//
						}else{
							apiMap = callUrl(infVO.getOutApi()+url, gson.toJson(paramMap));
							//apiMap =202;
						}
					logger.info("통신 통과 {}", apiMap);
					logger.debug("요청 url확인" + infVO.getOutApi()+url);
					logger.debug("요청 데이터확인" + paramMap);

				}
				System.out.println("apiMap : " + apiMap);

				chk = "5";
				//성공 데이터
				if(200==apiMap){

	//					infVO.setRtNumber(apiMap.get("rtNumber").toString());
	//					infVO.setPdRecDt(apiMap.get("pdRecDt").toString());

					//_gDao.update("golfReceipt.updateGolfInf", infVO);
					golfHistoryVO.setHistoryMemo("SS 취소상태로 변경요청 ");

				}
				else if(202==apiMap){
					golfHistoryVO.setHistoryMemo("SS측 취소는 API 재요청 안함 ");
				}
				else{
					golfHistoryVO.setHistoryMemo("API 전송오류");
					//getDAO().insert("golfHistory.insertHistory", golfHistoryVO);
				}
			}
		}catch (Exception e) {
			golfHistoryVO.setHistoryMemo("아이콘 전송오류("+chk+")");
			getDAO().insert("golfHistory.insertHistory", golfHistoryVO);

			System.out.println("Exception : " + e);
			logger.info("전송오류 확인 : {}", e);
		}
	}

	public int callUrl(String urlAddr, String param) throws Exception{

		HttpURLConnection conn;
		URL url = new URL(urlAddr);			 //URL;
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("charset", "utf-8");
        conn.setDoInput(true);
        conn.setDoOutput(true);


		try(OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());){
			os.write(param);
			os.flush();
		}

		StringBuffer sb = new StringBuffer();

		int responseCode = conn.getResponseCode();
		System.out.println(responseCode);
		try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}

		conn.disconnect();

		return responseCode;
	}
}
