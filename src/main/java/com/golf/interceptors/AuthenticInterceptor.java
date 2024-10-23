package com.golf.interceptors;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.golf.service.GlobalProperty;
import com.golf.util.RSAUtil;
import com.golf.util.StringUtil;
import com.golf.service.model.GroupVO;


/**
 * 골프 인터셉터
 * @author wimy
 * 1. preHandle (controller 전에 유입됨)
 * 2. postHandle (controller 후에 유입됨)
 * 3. afterCompletion (controller 후에 유입됨)
 *
 */
public class AuthenticInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticInterceptor.class);

	@Autowired
	GlobalProperty _globalProperty;
	

	private GlobalProperty getConfig() {
		return this._globalProperty;
	}

	private List<String> _urlPrefixes;

	private int _menuCount;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String sUrl = request.getRequestURI();

		
		try {
			//모바일 제한
			/*if (StringUtil.isMobile(request)){

				response.sendRedirect("/common/accessRestriction.do?type1=1");
				return false;
			}	*/
			

	        if(_globalProperty.getWebMode().equals("op") && !sUrl.contains("/golfApi/")&& !sUrl.contains("/golfCam/")&& !sUrl.contains("/api/")&& !sUrl.contains("/upload/")) {
	        	//그룹웨어 로그인
				if(sUrl.contains("/golfLogin.do")) {
					return true;
				}

				//발송 및 토큰갱신, 토큰값얻기, 위탁장소 지정관련 페이지가 아닌경우
				if(!sUrl.equals("/send.do")) {
					GroupVO vo = (GroupVO) request.getSession().getAttribute(getConfig().getSessionNameForAdmin());

					if (vo != null) {
						return true;
					}else {
						return false;
					}

				}else {
					return true;
				}


	        }else {
	        	return true;
	        }
		}
		catch (Exception e) {
			// e.printStackTrace();
			logger.error(">>> 인터셉터 오류 : {}", e.getMessage(), e);
			return false;
		}
	}
}
