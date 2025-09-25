package com.golf.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//API관련
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.golf.service.impl.RentalServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.golf.model.common.StringPair;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;
import com.golf.service.impl.KdngeAPI;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.StatusPayVO;
import com.golf.service.model.StatusVO;
import com.golf.service.model.search.GolfReceiptSearchVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.util.EgovMapDeserializer;
import com.golf.util.StringUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;
import com.golf.service.impl.GolfApi;

/**
 * 공통 컨트롤러
 * @author khs
 *
 */
@Controller
@EnableScheduling
@RequestMapping(value = "/common")
public class CommonController extends PrtController {

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	GolfReceiptServiceImpl _receiptService;

	@Resource
	SendTalkServiceImpl _sendTalkService;

	@Autowired
	private GolfApi golfApi;

	@Autowired
	private RentalServiceImpl rentalServiceImpl;

	@Autowired
	private KdngeAPI kdngeApi;


	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	/**
	 * 영업소 찾기 팝업
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/officePopup.do")
	public String OfficePopup(GolfInfVO infVO, BindingResult result, Model model) throws Exception {
      	// 검색조건
 		List<StringPair> type1 = new ArrayList<StringPair>();
// 		type1.add(new StringPair("우편번호", "A"));
// 		type1.add(new StringPair("영업소명", "N"));

 		//차세대 코드
		type1.add(new StringPair("우편번호", "zipcode"));
 		type1.add(new StringPair("영업소명", "name"));

		model.addAttribute("type1", type1);
		model.addAttribute("search", infVO);
		return "/common/officePopup";
	}

	/**
	 * 영업소 팝업 리스트
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/officePopupFrame.do")
	public String OfficePopupFrame(GolfInfVO infVO, BindingResult result, Model model) throws Exception {
		//조회내용
      	List<Map<String, Object>> searchMapList = new ArrayList<Map<String, Object>>();
        StringUtil str = new StringUtil();

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("SEARCH_TYPE", infVO.getType1());
//		paramMap.put("SEARCH_TYPE", "A");
		paramMap.put("SEARCH_SE", infVO.getType2());
		paramMap.put("SEARCH_WORD", infVO.getSearchWord());
		EgovMap apiMap = kdngeApi.callUrl(kdngeApi.getApiUrl("api/v1/branch/find/kd/"), gson.toJson(paramMap),"GET");

		List<EgovMap> apiMapList = new ArrayList<EgovMap>();

		if(apiMap.get("listData") != null ) {
			apiMapList = (List<EgovMap>)apiMap.get("listData");
		}


		EgovMap map = new EgovMap();
		for(int i = 0; i<apiMapList.size(); i++) {
			map = new EgovMap();
			String addr = (String) apiMapList.get(i).get("address") + apiMapList.get(i).get("addressDetail");
			map.put("picOfficeCode", apiMapList.get(i).get("branCode"));
			map.put("picOffice", apiMapList.get(i).get("branName"));
			map.put("picOfficeZipCode", apiMapList.get(i).get("zipCode"));
			map.put("picOfficeAddr", addr);
			searchMapList.add(map);
		}

		model.addAttribute("searchMapList", searchMapList);
		model.addAttribute("search", infVO);
		return "/common/officePopupFrame";
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
	@RequestMapping(value = "/getExtraPrice.do")
	public @ResponseBody String getExtraPrice(GolfReceiptInfVO vo) throws Exception {

		return kdngeApi.getExtraPrice(vo);
	}

	/**
	 * 영업소 코드 업데이트
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/officeCode.do")
	public String OfficeCode(GolfInfVO infVO, BindingResult result, Model model) throws Exception {
		List<GolfInfVO> list = (List<GolfInfVO>) _gDao.selectBySearch("golfInf.selectBySearchOffice", infVO, null);

		for(int i = 0; i<list.size(); i++){
//			JSONObject requestSearch = new JSONObject();
//			requestSearch.put("SEARCH_TYPE", "N");
//			requestSearch.put("SEARCH_WORD", list.get(i).getPicOffice1());

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("SEARCH_TYPE", infVO.getType1());
			paramMap.put("SEARCH_WORD", infVO.getSearchWord());
			EgovMap apiMap = kdngeApi.callUrl((kdngeApi.getApiUrl("api/v1/branch/find/kd/" +infVO.getType1() +"/" + infVO.getSearchWord())), gson.toJson(paramMap),"GET");
//			EgovMap apiMap = callUrl(getApiUrl("GetBranchInfo"), gson.toJson(paramMap));

			List<EgovMap> apiMapList = new ArrayList<EgovMap>();

			if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
				apiMapList = (List<EgovMap>)apiMap.get("listData");
			}

			EgovMap map = new EgovMap();
			for(int j = 0; i<apiMapList.size(); j++) {
				GolfInfVO golfVO = new GolfInfVO();
                golfVO.setPicOfficeCode1(apiMapList.get(j).get("branCode").toString());
                golfVO.setPicOffice1(apiMapList.get(j).get("branName").toString());
                golfVO.setGolfSeq(list.get(i).getGolfSeq());
                _gDao.insert("golfInf.updateOfficeCode", golfVO);
			}
		}
		return "";
	}

	/**
	 * 픽업요청 및 물품정보 전달
	 * @param infVO
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/setGolfDeliveryArr.do")
	public @ResponseBody String setGolfDeliveryArr(GolfReceiptInfVO infVO) throws Exception {
		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVO);
		String returnVal = "success";

			kdngeApi.setGolfDeliveryArr(infVO);


		return toJson(returnVal);
	}

	/**
	 * 선택취소(골프접수 -> 골프취소, 픽업요청 -> 픽업취소)
	 * @param infVO
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/deleteGolfDelivery.do")
	public @ResponseBody String deleteGolfDelivery(GolfReceiptInfVO infVO) throws Exception {
		GolfReceiptInfVO vo = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", infVO.getRcpInfSeq());

		return kdngeApi.deleteGolfDelivery(infVO);
	}

	/**
	 * 픽업요청 및 선택취소 시 상태 체크
	 * @param String
	 * @param String
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getState.do")
	public @ResponseBody String getState(Integer rcpInfSeq, String action) throws Exception {
		GolfReceiptInfVO vo = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", rcpInfSeq);

		//성공 데이터
		String returnVal = "success";

		//픽업요청
		if("P".equals(action)){
			//결제 체크(픽업요청 시 결제완료 상태 체크)
			if(!StatusPayVO.Complete.code().equals(vo.getPayStat())){
				returnVal = "error5";
			}
			//골프접수 단계 체크
			if(!StatusVO.Receipt.code().equals(vo.getRcpInfStat())){
				returnVal = "error1";
			}
		//결제완료처리
		}else if("M".equals(action)){
			//골프접수 단계 체크
			if(!StatusVO.Receipt.code().equals(vo.getRcpInfStat())){
				returnVal = "error3";
			}
		//골프접수처리
		}else if("W".equals(action)){
			//접수대기 단계 체크
			if(!StatusVO.Wait.code().equals(vo.getRcpInfStat())){
				returnVal = "error6";
			}
		//결제취소처리
		}else if("C".equals(action)){
			//골프접수 단계 체크
			if(!StatusVO.Receipt.code().equals(vo.getRcpInfStat()) && !StatusVO.ReceiptCancel.code().equals(vo.getRcpInfStat()) && !StatusVO.PickupCancel.code().equals(vo.getRcpInfStat()) &&
			   !StatusVO.DeliveryCancel.code().equals(vo.getRcpInfStat())){
				returnVal = "error4";
			}
		//선택취소
		}else{
			//골프접수, 픽업요청 단계 체크
			if(!StatusVO.Receipt.code().equals(vo.getRcpInfStat()) && !StatusVO.Pickup.code().equals(vo.getRcpInfStat())&& !StatusVO.Wait.code().equals(vo.getRcpInfStat())){
				returnVal = "error2";
			}
		}
		return toJson(returnVal);
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


	/**
	 * 골프 리스트 조회 시 인수완료시간 받기 및 업데이트
	 * @param vo
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List scTimeApi(List<GolfReceiptInfVO> receiptList, String history) throws Exception {
      	JSONArray apiArray = new JSONArray();
		JSONObject requestSearch = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		for(int i=0; i<receiptList.size();i++) {
			if(receiptList.get(i).getPdBarcode()!=null && receiptList.get(i).getScTime()==null && receiptList.get(i).getRcpInfStat().equals(StatusVO.Delivery.code())) {
				apiArray.add(receiptList.get(i).getPdBarcode());
			}
		}
		requestSearch.put("prodCodes", jsonParser.parse(apiArray.toString()));

		if(!"".equals(requestSearch.get("prodCodes"))) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
			EgovMap apiMap = kdngeApi.callUrl(kdngeApi.getApiUrl("api/v1/express/find/delivered"), gson.toJson(requestSearch), "POST");

			if (apiMap.get("listData") != null && ((List<EgovMap>) apiMap.get("listData")).size() > 0) {
				List<EgovMap> apiMapList = (List<EgovMap>) apiMap.get("listData");

				if (!apiMapList.isEmpty()) {

					for (int i = 0; i < apiMapList.size(); i++) {

						if (apiMapList.get(i).get("prodCode").equals(receiptList.get(i).getPdBarcode())) {

							if (apiMapList.get(i).get("deliDate") != null) {
								receiptList.get(i).setScTime(apiMapList.get(i).get("deliDate").toString());
								receiptList.get(i).setRcpInfStat(StatusVO.Complete.code());
								logger.debug("중간확인 {}", receiptList);
								_gDao.update("golfReceipt.updateGolfInfScTime", receiptList.get(i));

								receiptList.get(i).setCanReason("");
								golfApi.updateApi(receiptList.get(i), "update_cbag_status");


								// 아이콘 렌탈 GolfReceiptInfVO
								rentalServiceImpl.updateApi(receiptList.get(i), "status");

								//히스토리 등록
								if (history.equals("list")) {
									_receiptService.insertGolfHistory(receiptList.get(i).getRcpNumber(), "배송완료");
								} else {
									//스케줄로 배송완료 체크 시
									_receiptService.insertGolfShceduleHistory(receiptList.get(i).getRcpNumber(), "배송완료");
								}

							}
						}

					}

				}


			}
		}
//			for(int i = 0; i<apiMapList.size(); i++) {
//				for(int j=0; j<receiptList.size();j++) {
//					if(receiptList.get(j).getRcpInfSeq()==Integer.parseInt(apiMapList.get(i).get("rcpInfSeq").toString())) {
//						if(apiMapList.get(i).get("scTime")!=null) {
//							//update
//							receiptList.get(j).setScTime(apiMapList.get(i).get("scTime").toString());
//							receiptList.get(j).setRcpInfStat(StatusVO.Complete.code());
//							_gDao.update("golfReceipt.updateGolfInfScTime", receiptList.get(j));
//
//							receiptList.get(j).setCanReason("");
//							golfApi.updateApi(receiptList.get(j), "update_cbag_status");
//
//							//히스토리 등록
//							if(history.equals("list")) {
//								_receiptService.insertGolfHistory(receiptList.get(j).getRcpNumber(), "배송완료");
//							}else {
//								//스케줄로 배송완료 체크 시
//								_receiptService.insertGolfShceduleHistory(receiptList.get(j).getRcpNumber(), "배송완료");
//							}
//
//							receiptList.get(j).setScTime(apiMapList.get(i).get("scTime").toString());
//						}
//					}
//				}
//			}
//		}
		return receiptList;
	}

	/**
	 * api주소 분기 처리
	 * @param urlAddr, param
	 * @throws Exception
	 */

	// KDngeAPI 랑 연결해야한다.
	public EgovMap callUrl(String urlAddr, String param) throws Exception{
		if(super.getConfig().getWebMode().equals("local")){
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


	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */

	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(super.getConfig().getWebMode().equals("op") ? "https://kdapi.kditlab.com:447/RndGolf/" : "https://kdapi.kditlab.com:444/RndGolf/").append(url);
		return sb.toString();
	}

	//발송완료 체크 스케줄 1시간마다 실행 (fixedDelay=6000000)
	@Scheduled(fixedDelay=6000000)
	public void doSchedule() throws Exception {
		GolfReceiptSearchVO search = new GolfReceiptSearchVO();
		List<GolfReceiptInfVO> apiList = scTimeApi((List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectByScTime", search), "schedule");
	}
}
