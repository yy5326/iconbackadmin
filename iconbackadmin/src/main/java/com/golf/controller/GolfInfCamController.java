package com.golf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.golf.model.common.AlertModel;
import com.golf.model.common.StringPair;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfFileServiceImpl;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfFileVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.search.GolfReceiptSearchVO;
import com.golf.util.ExcelDownloadUtil;
import com.google.gson.Gson;
import com.golf.util.StringUtil;

//API관련
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;

import net.sf.json.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//맵관련
import lombok.Data;
import java.net.URLEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONValue;
import java.util.Collections;
import java.util.Comparator;

import com.golf.service.impl.GolfApi;
/**
 * 픽업 업로드 컨트롤러
 * @author khs
 *
 */
@Controller
@RequestMapping(value = "/golfCam")
public class GolfInfCamController extends PrtController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Resource
	GeneralDAOImpl _gDao;
	
	@Resource
	GolfFileServiceImpl _golfFileService;

	@Autowired
	private GolfApi golfApi;
	
	/**
	 * 픽업 업로드 화면
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfCamWrite.do")
	public String GolfInfList(GolfReceiptInfVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		AlertModel am = new AlertModel();
		search.setRegUser(new String(rsaDecoding(search.getCodePhoto())));
		model.addAttribute("search", search);
		
		return "/golfCam/golfCamWrite";
	}
	
	/**
	 * 골프 픽업사진 등록
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfCamProc.do")
	public String GolfCamProc(GolfFileVO vo, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		AlertModel am = new AlertModel();
				
		//등록
		try {
			vo.setFileCode("pickup");
			_golfFileService.insertFile(vo);
			
			GolfReceiptInfVO api =  (GolfReceiptInfVO)_gDao.selectOne("golfReceiptInf.selectByPrimaryKey", Integer.parseInt(vo.getRcpInfSeq()));
						
			golfApi.updateApi(api, "regi_pickup_picture");
			
			am = new AlertModel("사진이 등록되었습니다.", "./golfCamComplate.do", null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			am = new AlertModel("처리 중 오류가 발생했습니다.", "./golfCamWrite.do", null);
		}
		model.addAttribute("alert", am);

		return super.getConfig().getViewAlert();
	}
	
	/**
	 * 픽업 사진등록 완료
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfCamComplate.do")
	public String GolfCamComplate(GolfFileVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
	
		return "/golfCam/golfCamComplate";
	}
}
