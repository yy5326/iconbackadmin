package com.golf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 메일 전송 클래스
 * @author wimy
 *
 */
public class MailUtil
{
	private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);
	
    private String smtp;
    private String port;
    private String charset = "UTF-8";
    private String fromName;
    private String fromAddress;
    private String subject;
    private String body;
    private InternetAddress from;
    private Properties prop;
    private javax.mail.Session mailSession;

    
    
    /**
	 * @return the smtp
	 */
	public String getSmtp()
	{
		return smtp;
	}

	/**
	 * @param smtp the smtp to set
	 */
	public void setSmtp(String smtp)
	{
		this.smtp = smtp;
	}

	
	/**
	 * @return the port
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port)
	{
		this.port = port;
	}
	
	/**
	 * @return the charset
	 */
	public String getCharset()
	{
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	/**
	 * @return the fromName
	 */
	public String getFromName()
	{
		return fromName;
	}

	/**
	 * @param fromName the fromName to set
	 */
	public void setFromName(String fromName)
	{
		this.fromName = fromName;
	}

	/**
	 * @return the fromAddress
	 */
	public String getFromAddress()
	{
		return fromAddress;
	}

	/**
	 * @param fromAddress the fromAddress to set
	 */
	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	}
	
	/**
	 * @return the from
	 */
	public InternetAddress getFrom()
	{
		return from;
	}

	/**
	 * @param from the from to set
	 */
	public void setFrom(InternetAddress from)
	{
		this.from = from;
	}

	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body)
	{
		this.body = body;
	}

	

	/**
     * 생성자.
     *
     * @param smtp String(SMTP)
     * @param senderName String(보내는사람명)
     * @param senderAddress String(보내는사람주소)
     * @param subject String(제목)
     * @param body String(내용)
     * @throws Exception 
     */
    public MailUtil()
    {
    }

    /**
     * 메일 발송 : 한 명
     * 
     * @param receiver 
     * @return int
     */
    public int send(String receiver, String subject, String body) throws Exception
    {
        int iCount;
		
        try
		{
			iCount = this.send(new String[]{receiver}, subject, body);
		}
		catch (Exception ex)
		{
			throw ex;
		}
        
        return iCount;
    }

    /**
     * 메일 발송 : 여러 명
     *
     * @param receivers 받는 사람 주소 배열
     * @return int
     * @throws Exception 
     */
    public int send(String[] receivers, String subject, String body) throws Exception
    {
    	if(this.mailSession == null)
    	{
    		if(this.prop == null)
    		{
	    		this.prop = new Properties();
	            this.prop.put("mail.smtp.host", this.getSmtp());
	            this.prop.put("mail.smtp.port", this.getPort());
	            //this.prop.put("mail.smtp.auth", "true");
    		}
    		
    		this.mailSession = javax.mail.Session.getDefaultInstance(prop, null);
            this.mailSession.setDebug(false);
    	}
    	
    	if(this.from == null)
        {
        	try
			{
				this.from = new InternetAddress(this.getFromAddress());
				this.from.setPersonal(this.getFromName(), this.getCharset());
			}
			catch (AddressException ex)
			{
				throw ex;
			}
        	catch (UnsupportedEncodingException ex)
        	{
        		throw ex;
        	}
        }
        
    	this.setSubject(subject);
    	this.setBody(body);
    	
        int iSendCount = 0;

        try
        {
        	if(receivers == null || receivers.length == 0)
        		throw new Exception("받는 사람이 없습니다.");
        	
        	MimeMessage msg = new MimeMessage(mailSession);
            
        	msg.setFrom(from);
            
            InternetAddress[] ias = new InternetAddress[receivers.length];
            for(int i = 0; i < receivers.length; i++)
            	ias[i] = new InternetAddress(receivers[i]);
            
            msg.setRecipients(Message.RecipientType.TO, ias);
            msg.setSubject(subject, this.getCharset());
            
            //본문내용
            Multipart multiPart = new MimeMultipart();
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(this.getBody(), "text/html;charset=" + this.getCharset());
            multiPart.addBodyPart(mimeBodyPart);
            //본문내용 추가
            msg.setContent(multiPart);	
            
            msg.setSentDate(new Date());
            
            Transport.send(msg);
            
            iSendCount++;
        }
        catch(Exception e)
        {
           throw e;
        }

        return iSendCount;
    }
    
    /**
     * 웹 서버에 있는 HTML파일을 읽어 문자열로 가져옵니다. 
     * @param absoluteFileUrl HTML템플릿 파일 - 절대위치입력
     * @return
     */
    public String readMailTemplateFile(String absoluteFileUrl) throws Exception
	{
		FileReader fr = null;
		BufferedReader br = null;
		
		StringBuffer sb = new StringBuffer(16384); 
		try
		{
			fr = new FileReader(absoluteFileUrl);
			br = new BufferedReader(fr);
			
			int iChar = 0;
			while( (iChar = br.read()) > 0 ) 
				sb.append( (char)iChar );
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (IOException e) 
			{
				throw e;
			}
			
			try
			{
				fr.close();
			}
			catch (IOException e) 
			{
				throw e;
			}
			
		}
		return sb.toString();
	}
    
    /**
     * 텍스트 파일의 내용을 전부 읽어서 반환
     * @param pathName
     * @return
     */
	public String readTextFile(String pathName) throws IOException 
	{
        File file = new File(pathName);
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        
		try 
		{
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), this.charset));
            String text = null;

            while ((text = reader.readLine()) != null) 
            {
                contents.append(text);
            }            
        } 
		catch (FileNotFoundException e) 
		{
            throw e;
        } 
		catch (IOException e) 
		{
        	throw e;
        } 
		finally 
		{
            try 
            {
                if (reader != null)
                	reader.close();
            } 
            catch (IOException e) 
            {
            	throw e;
            }
        }
        
        return contents.toString();
	}	
}
