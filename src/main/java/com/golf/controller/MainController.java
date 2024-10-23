package com.golf.controller;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.search.MainSearchVO;
import com.google.gson.Gson;

/**
 * 메인 컨트롤러
 *
 */
@Controller
public class MainController extends PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Resource
	GeneralDAOImpl _gDao;
	
	/**
	 * 메인 - 통합 메인
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/main.do")
	public String main(MainSearchVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		
		
		//logger.debug("result1:{}", search.getTest()); 
		
		String test = "!";
		
//		byte[] test = hexToByteArray(search.getTest());
		
		//logger.debug("result:{}", new Gson().toJson(test)); 
		
//		String a = new String(Base64.getDecoder().decode(search.getTest()));
//		System.out.println(a);
//		List<MainVO> mainList = (List<MainVO>) _gDao.selectList("main.selectList", "");
//		
//		
		
		   // 복호화(Decryption) 예제
		  //공개키 및 개인키 생성
//		String deStr = URLDecoder.decode(test, "UTF-8"); 

        /*BigInteger modulus2 = new BigInteger("7321419137057019679893723533735696892477377475745328650319633688190161888982484240992427215315826124858018364755852119701658608630992499282349520067225403");
        BigInteger d_val2 = new BigInteger("1557520570194683436487454315994676077252843382621135725510113872785559867802789100499999403922279864273050467912255895446948044273951207388331390456480273");
        
        KeyFactory factory2 = KeyFactory.getInstance("RSA");
        
        RSAPrivateKeySpec privSpec2 = new RSAPrivateKeySpec(modulus2, d_val2);
        PrivateKey privKey2 = factory2.generatePrivate(privSpec2);
//        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("RSA");
        
        cipher.init(Cipher.DECRYPT_MODE, privKey2);
        
        
        String newstr = new String(search.getTest());
        byte[] arrData = cipher.doFinal(java.util.Base64.getDecoder().decode("KdA/we2tpyw/i7ZAe8sezgE2Wi/XaQmT/KCFU53wIk2+QCDhwHWYZ8o0TiDaY6ULXR/wQ8RHqvliyw2ObJnYpQ=="));
        String strResult = new String(arrData);*/


        
        // 복호화 결과물 출력(다시 "세이프123"이 출력됨)
		
		//model.addAttribute("list", test);
//		
		return "main";
		
		
	} //end main.do 
	
	
	
	//바이트 to 헥스 
	 /* hexToByteArray */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return new byte[]{};
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;

    }
	
	
} //end class 
