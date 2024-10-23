package com.golf.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import com.golf.model.common.AlertModel;
import com.golf.service.dao.GeneralDAOImpl2;
import com.golf.service.model.GroupVO;

/**
 * 발송 컨트롤러
 *
 */
@Controller
public class LoginController extends PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
			
	@Resource
	GeneralDAOImpl2 _gDao2;
	
	/**
	 * 그룹웨어 로그인 페이지
	 * @param site
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/golfLogin.do")
	public String GolfLogin(GroupVO search, BindingResult result, Model model, HttpServletRequest request) {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		super.getSession().invalidate();
		String url = "./main.do";

		try {
			//그룹웨어 db접근권한 및 db서버 방화벽 해제 요청도 해야함
			GroupVO templCodeList = (GroupVO)_gDao2.selectByKey("group.selectByToken", search);
			_gDao2.update("group.deleteToken", search);
			
			if(templCodeList!=null) {
				request.getSession().setAttribute(this.getConfig().getSessionNameForAdmin(), templCodeList);
				request.getSession().setAttribute("historyEmpCode", templCodeList.getEmpCode());
				url = super.getConfig().getAdminMainURL(); //월별조회화면
				return "redirect:" + url;
			}else {
				AlertModel am = new AlertModel("허용되지 않은 사용자입니다.", "./main.do", null);
				model.addAttribute("alert", am);
				return super.getConfig().getViewAlert();
			}
		}catch (Exception e) {
			logger.error(">>>>> Error : {}", e.getMessage(), e);
			AlertModel am = new AlertModel("허용되지 않은 사용자입니다.", "./main.do", null);
			model.addAttribute("alert", am);
			return super.getConfig().getViewAlert();
		}
	}
}  
