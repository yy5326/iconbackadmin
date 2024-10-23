package com.golf.controller;

import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.golf.model.common.GeneralUserVO;
import com.golf.service.ConfigBean;

/**
 * 기본 super controller : 컨트롤러 상속용 부모 컨트롤러 <br>
 * 
 * @author wimy
 */
@Deprecated
@Controller
public class ParentController {
	
	private static final Logger logger = LoggerFactory.getLogger(ParentController.class);
	
	@Resource(name="configBean")
	ConfigBean configBean;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	HttpSession session;
	
	public ConfigBean getConfig()
	{
		return this.configBean;
	}
	
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	/**
	 * 관리자 세션 - 관리자 모델로 형변환하여 사용
	 * 			- 관리자 모델이 정의되면 해당 모델 클래스로 변환해서 사용하는 것을 추천
	 * @return
	 */
	public GeneralUserVO getAdminSession() {
		return (GeneralUserVO)this.session.getAttribute(this.getConfig().getSessionNameForAdmin());
	}
	
	/**
	 * 사용자 세션 - 사용자 모델로 형변환하여 사용
	 * 			- 사용자 모델이 정의되면 해당 모델 클래스로 변환해서 사용하는 것을 추천
	 * @return
	 */
	public GeneralUserVO getUserSession() {
		 return (GeneralUserVO)this.session.getAttribute(this.getConfig().getSessionNameForUser());
	}
	
	/**
	 * 페이지 서브 타이틀 추가  
	 *  - 웹 접근성 요소 중 하나 : 페이지별 부 제목 표시 처리용
	 *  - jsp html 선언부 title 엘리먼트에 ${pageTitle } 추가 후 사용 (globals.properties : site.pageTitle 참조)
	 * @param title
	 * @param model
	 */
	public void setPageSubTitle(String title, Model model) {
		model.addAttribute(this.getConfig().getPageTitle(), title);
	}
	
	/**
	 * BindingResult객체를 global Exception으로 변경하여 공통 Error페이지로 출력할 수 있도록 합니다.
	 * 
	 * @param result
	 * @param model
	 * @return
	 */
	public String setBindingResult(BindingResult result, Model model) {
		if (result.getAllErrors().size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (Object e : result.getAllErrors()) {
				sb.append(((ObjectError)e).getObjectName() + " - " + ((ObjectError)e).getDefaultMessage() + "<br />");
			}
			
			Exception ex = new Exception("[BindingResult Exception] 올바르지 않은 파라미터 입력 : " + sb.toString());
			//Exception ex = new Exception("[BindingResult Exception] 올바르지 않은 파라미터 입력");
			
			logger.error(ex.getMessage(), ex);
			
			model.addAttribute("exception", ex);
		}
		
		return getConfig().getViewError();
	}
	
	
	
	/**
	 * 협의 및 수정 필요 
	 * message-common 키에 대한 값을 가져옵니다.
	 * @param key
	 * @return
	 */
	public String getMessage(String key) {
		return this.getApplicationContext().getMessage(key, new Object[0], Locale.getDefault());
	}
}
