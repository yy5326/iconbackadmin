package com.golf.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http, https 프로토콜 변환 - URL을 기준으로 필터에 명시된 페이지일 경우 https로 변환
 * 
 * @author wimy
 *
 */
public class ProtocolFilter implements Filter {
	
	// private static final Logger logger = LoggerFactory.getLogger(ProtocolFilter.class);
	
	HttpServletRequest httpRequest;
	String[] httpUri;
	String using;
	
	String httpsPort;
	String httpPort;
	String httpsUrlPre = "https://";
	String httpUrlPre = "http://";
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (httpUri == null)
			httpUri = filterConfig.getInitParameter("https_uri").split(",");
		
		if (using == null)
			using = filterConfig.getInitParameter("using");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		httpRequest = (HttpServletRequest) request;
		
		// //jboss ServletRequest.getServletContext() 메서드를 지원하지 않음. 컨텍스트 파라미터 사용 불가
		// LoginController.login() 메서드 참조. (포트 변경 하드코딩)
		// if (httpsPort == null)
		// httpsPort = request.getServletContext().getInitParameter("sslPort").equals("443") ? "" : ":" + request.getServletContext().getInitParameter("sslPort");
		// if (httpPort == null)
		// httpPort = request.getServletContext().getInitParameter("non-sslPort").equals("80") ? "" : ":" + request.getServletContext().getInitParameter("non-sslPort");
		
		// 하드 코딩시 사용
		if (httpsPort == null)
			httpsPort = ":8443";
		
		if (httpPort == null)
			httpPort = ":8080";
		
		if (using.toLowerCase().equals("false")) {
			chain.doFilter(request, response);
		}
		else {
			// 백그라운드 ajax 처리 액션, 로그인 성공 후 리디렉션 처리 페이지일 경우 필터 작동 스킵 (호출 페이지와 동일한 scheme 사용)
			if (httpRequest.getRequestURL().indexOf("/ajax/") > -1 || httpRequest.getRequestURL().indexOf("/login/login_success.do") > -1) {
				chain.doFilter(request, response);
			}
			else {
				String queryString = (httpRequest.getQueryString() != null) ? "?" + httpRequest.getQueryString() : "";
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				
				// change to https
				if (httpRequest.getRequestURL().indexOf(httpsUrlPre) < 0 && isSecure(httpRequest.getRequestURI())) {
					String newUrl = httpsUrlPre + httpRequest.getServerName() + httpsPort + httpRequest.getServletPath() + queryString;
					httpResponse.sendRedirect(newUrl);
				}
				// change to http
				else if (httpRequest.getRequestURL().indexOf(httpUrlPre) < 0 && !isSecure(httpRequest.getRequestURI())) {
					String newUrl = httpUrlPre + httpRequest.getServerName() + httpPort + httpRequest.getServletPath() + queryString;
					httpResponse.sendRedirect(newUrl);
				}
				else {
					chain.doFilter(request, response);
				}
			}
		}
	}
	
	public boolean isSecure(String uri) {
		boolean isHttpsPage = false;
		for (String tmpUri : httpUri) {
			if (uri.indexOf(tmpUri.trim()) > -1) {
				isHttpsPage = true;
				break;
			}
		}
		return isHttpsPage;
	}
	
	@Override
	public void destroy() {
		httpUri = null;
		httpsUrlPre = null;
		httpUrlPre = null;
	}
}
