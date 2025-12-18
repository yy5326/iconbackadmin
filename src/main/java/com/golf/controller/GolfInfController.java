package com.golf.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfInfServiceImpl;
import com.golf.service.model.GolfInfVO;
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
/**
 * 골프관리자 컨트롤러
 * @author khs
 *
 */
@Controller
@RequestMapping(value = "/golfInf")
public class GolfInfController extends PrtController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	GolfInfServiceImpl _golfService;
	
	@Autowired
	GlobalProperty _globalProperty;
	
	/**
	 * 골프장 조회
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfList.do")
	public String GolfInfList(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

//		if(!(_globalProperty.getWebMode().equals("op")) && !("superUser_092_02".equals(session.getAttribute("partner")))) {
		if(!("superUser_092_02".equals(session.getAttribute("partner")))) {
			return "redirect:/receipt/" + session.getAttribute("partner") +"/receiptList.do";
		} else {
			if (search.getRecordCount() < 20)
				search.setRecordCount(10);
	
			List<GolfInfVO> golfInfList = (List<GolfInfVO>)_gDao.selectBySearch("golfInf.selectBySearch", search, "selectListCount");
			
			//검색조건
			List<StringPair> type1 = new ArrayList<StringPair>();
			type1.add(new StringPair("골프장명", "0")); 
			type1.add(new StringPair("지정영업소", "1"));
			type1.add(new StringPair("주소", "2"));
			
			model.addAttribute("type1", type1);
			model.addAttribute("search", search);
			model.addAttribute("golfInfList", golfInfList);
			
			logger.debug("확인2 : " + model);
			return "/golfInf/golfInfList";
		}
	}

	/**
	 * 골프장관리 등록화면(삭제예정)
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfWrite.do")
	public String GolfInfWrite(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		//번호
		List<StringPair> type1 = new ArrayList<StringPair>();	
		//국번 폰번호 불러오기
		List<String> list = StringUtil.getMobilePhoneList();		
		for(int i=0; i<list.size(); i++) {
			type1.add(new StringPair(list.get(i), list.get(i)));
		}
		model.addAttribute("type1", type1);
		
		return "/golfInf/golfInfWrite";
	}

	/**
	 * 골프장관리 등록, 수정화면
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfWriteLayer.do")
	public String GolfInfWriteLayer(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		//번호
		List<StringPair> type1 = new ArrayList<StringPair>();	
		//국번 폰번호 불러오기
		List<String> list = StringUtil.getMobilePhoneList();		
		for(int i=0; i<list.size(); i++) {
			type1.add(new StringPair(list.get(i), list.get(i)));
		}
		model.addAttribute("type1", type1);
		

		List<StringPair> useYn = new ArrayList<StringPair>();	
		useYn.add(new StringPair("가능", "Y"));
		useYn.add(new StringPair("불가능","N"));
		useYn.add(new StringPair("확인필요", "C"));
		model.addAttribute("useYn", useYn);
		
		GolfInfVO vo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", search.getGolfSeq());
		if(vo!=null) {
			model.addAttribute("type2", "U");	
			model.addAttribute("golfSeq", vo.getGolfSeq());
		}else {
			model.addAttribute("type2", "I");
			model.addAttribute("golfSeq", "0");
		}
		model.addAttribute("vo", vo);
		return "/golfInf/golfInfWriteLayer";
	}
	/**
	 * 골프장 등록, 수정
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfProc.do", method=RequestMethod.POST)
	public String GolfInfProc(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		AlertModel am = new AlertModel();
		//등록
		try {
			if(search.getType2().equals("I")) {
				_golfService.insertGolf(search);
				am = new AlertModel("등록되었습니다.", "./golfInfList.do", null);
			}else {
				_golfService.updateGolf(search);
				am = new AlertModel("수정되었습니다.", "./golfInfList.do", null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			am = new AlertModel("처리 중 오류가 발생했습니다.", "./golfInfList.do", null);
		}
		model.addAttribute("alert", am);

		return super.getConfig().getViewAlert();
	}

	/**
	 * 골프장관리 Detail화면
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfDetail.do")
	public String GolfInfBasic(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		
		GolfInfVO vo = (GolfInfVO) _gDao.selectByKey("golfInf.selectByKey", search.getGolfSeq());
		model.addAttribute("vo", vo);
		
		return "/golfInf/golfInfDetail";
	}
	
	/**
	 * 골프장 불가능 처리
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfDelete.do")
	public String GolfInfDelete(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		AlertModel am = new AlertModel();
		//삭제
		try {
			for (int i = 0; i < search.getSeqs().size(); i++) {
				search.setGolfSeq(Integer.parseInt(search.getSeqs().get(i)));
				_golfService.deleteGolf(search);
			}
			am = new AlertModel("처리되었습니다.", "./golfInfList.do", null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			am = new AlertModel("처리 중 오류가 발생했습니다.", "./golfInfList.do", null);
		}
		model.addAttribute("alert", am);

		return super.getConfig().getViewAlert();
	}
	

	/**
	 * 골프장 삭제
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfInfDeleteData.do")
	public String GolfInfDeleteData(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		AlertModel am = new AlertModel();
		//삭제
		try {
			for (int i = 0; i < search.getSeqs().size(); i++) {
				search.setGolfSeq(Integer.parseInt(search.getSeqs().get(i)));
				_golfService.deleteGolfData(search);
			}
			am = new AlertModel("삭제되었습니다.", "./golfInfList.do", null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			am = new AlertModel("처리 중 오류가 발생했습니다.", "./golfInfList.do", null);
		}
		model.addAttribute("alert", am);

		return super.getConfig().getViewAlert();
	}
	
	/**
	 * 골프장관리 엑셀다운로드
	 * @param search
	 * @param result
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/to_excel.do")
	public String toExcel(GolfInfVO search, BindingResult result, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		if (result.hasErrors()) 
			return super.setBindingResult(result, model);
		
		search.setCpage(1);
		search.setRecordCount(Integer.MAX_VALUE);
		
		List<GolfInfVO> list = (List<GolfInfVO>) _gDao.selectBySearch("golfInf.selectBySearch", search, null);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", list);
		map.put("search", search);
		map.put("subject1", "골프장_목록");
		
		ExcelDownloadUtil.downloadExcel(request, response, map, "", "golf_list.xlsx");
		
		return null;
	}	
}
