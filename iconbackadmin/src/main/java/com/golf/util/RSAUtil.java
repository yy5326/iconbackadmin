package com.golf.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

/**
 * RSA 암호화 유틸리티<br>
 * /common/js/rsa 디렉토리 내 rsa 암호화 처리용 자바스크립트와 함께 사용
 * @author wimy
 *
 */
public class RSAUtil {
	
	public void generateRSA(HttpServletRequest request, Model model, String sessionName, int keySize) throws Exception {
		KeyPairGenerator generator;		
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(keySize);
			
			KeyPair keyPair = generator.genKeyPair();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			if (request.getSession().getAttribute(sessionName) != null)
				request.getSession().removeAttribute(sessionName);
			request.getSession().setAttribute(sessionName, privateKey);
			RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			
			String publicKeyModulus = publicSpec.getModulus().toString(16);
			String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
			
			model.addAttribute("publicKeyModulus", publicKeyModulus);
			model.addAttribute("publicKeyExponent", publicKeyExponent);
		}
		catch (NoSuchAlgorithmException e) {
			throw e;
		}
		catch (InvalidKeySpecException e) {
			throw e;
		}
	}
	
	public void generateRSA(HttpServletRequest request, Model model, String sessionName) throws Exception {
		generateRSA(request, model, sessionName, 1024);
	}
	
	public String decryptRsa(PrivateKey privateKey, String securedValue) throws Exception {
		//System.out.println("will decrypt : " + securedValue);
		Cipher cipher = Cipher.getInstance("RSA");
		byte[] encryptedBytes = StringUtil.hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes); // 문자 인코딩 주의.
		return decryptedValue;
	}
}
