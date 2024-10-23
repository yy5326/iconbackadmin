package com.golf.controller;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Locale;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.golf.service.GlobalProperty;
import com.golf.service.model.GroupVO;

import org.apache.commons.codec.binary.Base64;
import java.security.spec.RSAPrivateKeySpec;
import java.security.PrivateKey;

/**
 * 기본 super controller : 컨트롤러 상속용 부모 컨트롤러
 * 
 * @author wimy
 *
 */
@Controller
public class PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(PrtController.class);
	
	@Resource(name="globalProperty")
	GlobalProperty configBean;
	
	@Autowired
	ServletContext servletContext;
	
	@Autowired
	ApplicationContext applicationContext;
	
	@Autowired
	HttpSession session;
	
	public GlobalProperty getConfig()
	{
		return this.configBean;
	}
	
	public ServletContext getServletContext() {
		return this.servletContext;
	}
	
	public ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	public HttpSession getSession() {
		return this.session;
	}
	
	/**
	 * 관리자 세션 - 관리자 모델로 형변환하여 사용
	 * 			- 관리자 모델이 정의되면 해당 모델 클래스로 변환해서 사용하는 것을 추천
	 * @return
	 */
	/*public MemberVO getAdminSession() {
		return (MemberVO) this.session.getAttribute(this.getConfig().getSessionNameForAdmin());
	}*/
	
	/**
	 * 사용자 세션 - 사용자 모델로 형변환하여 사용
	 * 			- 사용자 모델이 정의되면 해당 모델 클래스로 변환해서 사용하는 것을 추천
	 * @return
	 */
	/*
	 * public MemberInfoVO getUserSession() {
		 return (MemberInfoVO)this.session.getAttribute(this.getConfig().getSessionNameForUser());
	}
	*/
	
	/**
	 * 세션에 저장된 사이트 정보를 가져옵니다.
	 * 	- 사이트 정보 저장 루틴 : AuthencticInterceptor 참조  
	 * @return
	 */
	/*
	public SiteVO getSiteSession() {
		return (SiteVO)this.session.getAttribute(this.getConfig().getSessionNameForSite());
	}
	*/
	
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
	 * 모델에 reqURI 속성값으로  request.getRequestURI 값을 매핑합니다.
	 * 	- 서브 페이지에서 액션 URL을 필요로 할 때 사용할 수 있습니다.
	 * @param model
	 * @param request
	 * @return
	 */
	public void setRequestURItoModel(Model model, HttpServletRequest request) {
		model.addAttribute("reqURI", request.getRequestURI());
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
	
	public GroupVO getAdminSession() {
		return (GroupVO) this.session.getAttribute(this.getConfig().getSessionNameForAdmin());
	}
	
	

	/**
	 * 파라미터 암호화
	 * @param rsa
	 * @return
	 * @throws Exception
	 */
	public byte[] rsaTranse(String rsa) throws Exception{
		
		BigInteger modulus = new BigInteger("10345918865608327074462728945090846516362250753812647068843026846167108013234608429839352569733455038751909226790475320814198878515405268903520475489246739");
		/*BigInteger exponent = new BigInteger(1, exponentBytes);*/
		BigInteger d_val = new BigInteger("65537");

		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec privSpec = new RSAPublicKeySpec(modulus, d_val);
		PublicKey publickey = fact.generatePublic(privSpec);
		Cipher clsCipher = Cipher.getInstance("RSA");
		clsCipher.init( Cipher.ENCRYPT_MODE, publickey );
		byte[] arrCipherData = clsCipher.doFinal( rsa.getBytes("UTF-8") );
		String strCipher = new String( arrCipherData );
		//System.out.println( "cipher(" + strCipher + ")" );

		return arrCipherData;
	}

	/**
	 * 파라미터 복호화
	 * @param rsa
	 * @return
	 * @throws Exception
	 */
	public String rsaDecoding(byte[] rsaKey) throws Exception{
		
		try {
			
			Base64 b = new Base64();
			rsaKey=b.decodeBase64(rsaKey);
			
			// 복호화 한다.
			BigInteger modulus = new BigInteger("10345918865608327074462728945090846516362250753812647068843026846167108013234608429839352569733455038751909226790475320814198878515405268903520475489246739");
			/* BigInteger exponent = new BigInteger(1, exponentBytes); */
			BigInteger d_val = new BigInteger("979544784794843668416943605967448962174462760385850360287638762538213607917962091893178918412283132638247200664784557077318379225593353629533868558191513");
			
			KeyFactory factory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec privSpec2 = new RSAPrivateKeySpec(modulus, d_val);
			PrivateKey privKey = factory.generatePrivate(privSpec2);
			
			Cipher clsCipher2 = Cipher.getInstance("RSA");
			clsCipher2.init(Cipher.DECRYPT_MODE, privKey);
			byte[] arrCipherData = clsCipher2.doFinal(rsaKey);
			
			String strResult = new String(arrCipherData);
			return strResult;
			
		} catch (Exception e) {
			return "error";
		}

	}
}
