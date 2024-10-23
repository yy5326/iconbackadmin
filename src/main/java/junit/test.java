package junit;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations={"file:src/main/resources/egovframework/spring/com/context-datasource.xml"})
public class test {
	private String DRIVER=
			"oracle.jdbc.driver.OracleDriver";
			//"com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private String URL=
			"jdbc:oracle:thin:@localhost:1521:orcl";	
			//"jdbc:sqlserver://59.17.158.226:1433;databaseName=ezHrmaster";
	
	private String User="";
	private String PW="";
	
//	@Test
	public void testConnection() throws Exception{
		System.out.println("dd");
	}
	@Test
	public void TestRSA() {
	        try {
	        	
	            //공개키 및 개인키 생성
	            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
	            keyPairGenerator.initialize(512);
//	            
//	            
	           /* BigInteger modulus = new BigInteger("19927804726893694655564636122312903410318899883620438681131347750839482091140999934466022668324641173840375580246548842304505369011075091148804788159220854474083812750602392649694098464280511499803050275375869416698448244476009519713122385422547747619934330132296889449165416348701784862207407726742747312256143223920147626034189038711881881476733592423323231761721927411868871438584006798261202048488894750320352394374553184723822906021397069262502613500007828907133835388617548711588337404520424994621603103376039083120741814010536511121180587464326486769550913103992964701712072568204898189082928929902429249019009");
	            BigInteger d_val = new BigInteger("65537");
	            
	            KeyFactory factory = KeyFactory.getInstance("RSA");
	            RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(modulus, d_val);
	            PublicKey publicKey = factory.generatePublic(publicSpec);*/
	            
	            KeyPair keyPair = keyPairGenerator.genKeyPair();
	            Key publicKey = keyPair.getPublic(); // 공개키
	            Key privateKey = keyPair.getPrivate(); // 개인키
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//	            
	            RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
	            RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
//	 
	            System.out.println("public key modulus(" + publicKeySpec.getModulus() + ") exponent(" + publicKeySpec.getPublicExponent() + ")");
	            System.out.println("private key modulus(" + privateKeySpec.getModulus() + ") exponent(" + privateKeySpec.getPrivateExponent() + ")");
	 
//	            System.out.println("public::::"+publicKey);
//	            System.out.println("----------------");
	            //System.out.println("private::::("+privateKey+")");
	            
	            // 암호화(Encryption) 예제
	            String inputStr = "세이프123"; // "세이프123"을 암호화한다!
	 
	            Cipher cipher = Cipher.getInstance("RSA");
	            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	            byte[] arrCipherData = cipher.doFinal(inputStr.getBytes()); // 암호화된 데이터(byte 배열)
	            String strCipher = new String(arrCipherData);
	            
	            System.out.println(strCipher); // 암호화 결과물 출력(눈으로 보이기에는 깨질 수 있음)
	 
	            // 복호화(Decryption) 예제
	            
	            BigInteger modulus2 = new BigInteger("19927804726893694655564636122312903410318899883620438681131347750839482091140999934466022668324641173840375580246548842304505369011075091148804788159220854474083812750602392649694098464280511499803050275375869416698448244476009519713122385422547747619934330132296889449165416348701784862207407726742747312256143223920147626034189038711881881476733592423323231761721927411868871438584006798261202048488894750320352394374553184723822906021397069262502613500007828907133835388617548711588337404520424994621603103376039083120741814010536511121180587464326486769550913103992964701712072568204898189082928929902429249019009");
	            BigInteger d_val2 = new BigInteger("15801581217365528330038412581720778362208252899157778612727966622943915129321213110066005462636779649373068614350251059524211216738613901486640194489681098378850321150045844924647957869497617240799321202686387933191453071100062357308571204102316840877140048125258593679978930854219534826652613902614406359400038386604240343057223945213519109692458998381649007110599415992271938861449803283971836427693080930575641691298987683069564868930072125443382587440043808859268894963182182297880059375014606425234744106655699901964189899231484280272476984184101688555652774110167500772430349335895671022863523659401115706276013");
	            
	            KeyFactory factory2 = KeyFactory.getInstance("RSA");
	            RSAPrivateKeySpec privSpec2 = new RSAPrivateKeySpec(modulus2, d_val2);
	            PrivateKey privKey2 = factory2.generatePrivate(privSpec2);
	            
	            cipher.init(Cipher.DECRYPT_MODE, privKey2);
	            byte[] arrData = cipher.doFinal(arrCipherData);
	            String strResult = new String(arrData);
	 
	            System.out.println(strResult);
	            // 복호화 결과물 출력(다시 "세이프123"이 출력됨)
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }


}
