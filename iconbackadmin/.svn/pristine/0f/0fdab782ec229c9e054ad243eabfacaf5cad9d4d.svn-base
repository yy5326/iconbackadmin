package com.golf.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES 256 암호화<br><br>
 *
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
 */
public class AES256 {
	
	private static volatile AES256 INSTANCE;
	
	/**
	 * 32bytes 비밀 키 : 프로젝트 최초 시작시 한 번만 변경할 것. 
	 */
	final static String secretKey = "KDExp!@#901234567890123456789012";
	static String IV = ""; // 16bytes
	
	public static AES256 getInstance() {
		if (INSTANCE == null) {
			synchronized (AES256.class) {
				if (INSTANCE == null)
					INSTANCE = new AES256();
			}
		}
		return INSTANCE;
	}
	
	private AES256() {
		IV = secretKey.substring(0, 16);
	}
	
	/**
	 * AES256 암호화 - base64 인코딩 - UTF-8
	 * @param input
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String Encrypt(String input) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = secretKey.getBytes();
		
		SecretKey secureKey = new SecretKeySpec(keyData, "AES");
		
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.ENCRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes()));
		
		byte[] encrypted = c.doFinal(input.getBytes("UTF-8"));
		String enStr = new String(Base64.encodeBase64(encrypted));
		
		return enStr;
	}
	
	/**
	 * AES256 복호화
	 * @param input AES256 암호화 후 base64인코딩된  문자열 (UTF-8)
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public static String Decrypt(String input) throws java.io.UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyData = secretKey.getBytes();
		SecretKey secureKey = new SecretKeySpec(keyData, "AES");
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		c.init(Cipher.DECRYPT_MODE, secureKey, new IvParameterSpec(IV.getBytes("UTF-8")));
		
		byte[] byteStr = Base64.decodeBase64(input.getBytes());
		
		return new String(c.doFinal(byteStr), "UTF-8");
	}
}
