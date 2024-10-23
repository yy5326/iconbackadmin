package com.golf.util;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 암호화 유틸리티 <br>
 * user define Encryption 	<br>
 * 		AES256 Encrypt / AES256 Decrypt <br>
 *		SHA256 Encoding / SHA512 Encoding<br><br>
 * 
 * java.security.InvalidKeyException: Illegal key size 오류 해결 방법<br>
 * jre 버전별 패치 필요 (local_policy.jar, US_export_policy.jar)<br><br>
 * 
 * jdk8 (Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 8)<br>
 * 		http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html<br><br>
 *
 * jdk7 (Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7)<br>
 * 		http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html<br><br>
 *
 * jdk6 (Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6)<br>
 * 		http://www.oracle.com/technetwork/java/javase/downloads/jce-6-download-429243.html<br><br>
 * 
 * @author wimy
 *
 */
public class Encrypt {
	
	private static final Logger logger = LoggerFactory.getLogger(Encrypt.class);
	
	/**
	 * 중요 : 16bytes 문자열 주의 : 프로젝트 시작 시 최초 한 번만 정의할 것.
	 */
	//private static final String KEY = "abcdefghijklmnop";
	private static final String KEY = "kdexp678901234567890abcdefghijkl"; 
	private static SecretKey _secret = null;
	private static String _iv = "";
	
	private static void prepareAESEncrypt() {
		if (_secret == null) {
			_secret = new SecretKeySpec(KEY.getBytes(), "AES");
		}
		
		_iv = KEY.substring(0, 16);
	}
	
	
	/**
	 * AES 256 암호화 Hex Encoding<br>
	 * use only for UTF-8 Encoding<br>
	 * @param input normal string
	 * @return hex encoded string
	 */
	public static String encryptAES(String input) {
		prepareAESEncrypt();
		
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, _secret, new IvParameterSpec(_iv.getBytes()));
			
			byte[] arrBytes = cipher.doFinal(input.getBytes("UTF-8"));
			// return (new BASE64Encoder()).encode(arrBytes);
			return new String(Hex.encodeHex(arrBytes));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * AES 256 복호화 Hex Encoding<br>
	 * use only for UTF-8 Encoding<br>
	 * @param encodedString hex encoded string
	 * @return
	 */
	public static String decryptAES(String encodedString) {
		prepareAESEncrypt();
		
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, _secret, new IvParameterSpec(_iv.getBytes("UTF-8")));
			byte[] arrBytes = cipher.doFinal(Hex.decodeHex(encodedString.toCharArray()));
			
			return new String(arrBytes, "UTF-8");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * SHA256 인코딩 <br>
	 * 입력 문자열을 SHA256 바이트 배열로 만든 후, base64 encoding 문자열로 변환하여 반환
	 * 
	 * @param input
	 * @return 44bytes base64 encoded string
	 * @throws Exception 
	 */
	public static String SHA256Hash(String input) throws Exception {
		return SHAHash("SHA-256", input, "base64");
	}
	
	/**
	 * SHA256 인코딩 (Hex)
	 * @param input
	 * @return 64bytes hex encoded string
	 * @throws Exception 
	 */
	public static String SHA256HashHex(String input) throws Exception {
		return SHAHash("SHA-256", input, "hex");
	}
	
	/**
	 * SHA512 인코딩 - base64
	 * @param input 
	 * @return 88bytes base64 encoded string
	 * @throws Exception 
	 */
	public static String SHA512Hash(String input) throws Exception {
		return SHAHash("SHA-512", input, "base64");
	}
	
	
	
	
	/**
	 * SHA
	 * @param shaType : SHA-128, SHA-256, SHA-512
	 * @param input 
	 * @param Encoding "base64", "hex" 
	 * @return
	 * @throws Exception 
	 */
	private static String SHAHash(String shaType, String input, String encoding) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance(shaType);
			md.update(input.getBytes(), 0, input.length());
			
			if (encoding.toLowerCase().equals("base64"))
				return new String(Base64.encodeBase64URLSafeString(md.digest()));
			else if (encoding.toLowerCase().equals("hex"))
				return new String(Hex.encodeHex(md.digest()));
			else
				throw new Exception("정의되지 않은 인코딩 - base64 또는 hex만 적용 가능");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
