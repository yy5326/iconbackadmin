package com.golf.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class HttpURLConnectionUtil {
	
	private static DocumentBuilder docBuilder;
	private static String xmlData;
	private static Document doc;
	
	public static Document HttpURLConnectionUtil(String url) throws Exception {
		
		System.out.println("==============================");
		System.out.println("HttpURLConnectionParser()");
		System.out.println("==============================");
		
		docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		URL conUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) conUrl.openConnection();
		
		byte[] bytes = new byte[4096];
		InputStream in = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (true) {
			int red = in.read(bytes);
			if (red < 0)
				break;
			baos.write(bytes, 0, red);
		}
		xmlData = baos.toString("utf-8");
		doc = docBuilder.parse(new InputSource(new StringReader(xmlData)));
		
		return doc;
	}
	
	public JSONObject HttpURLConnectionForJson(String uri, String method) throws Exception {
		
		URL url = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setDoOutput(true);
		con.setDoOutput(true);
		con.connect();
		
		// 리턴된 페이지를 파싱
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer sb = new StringBuffer();
		
		String strData = "";
		while ((strData = br.readLine()) != null) {
			sb.append(strData + "\n");
		}
		System.out.println("sb===" + sb.toString());
		JSONObject jsonObj;
		
		jsonObj = (JSONObject) JSONValue.parse(sb.toString());
		
		br.close();
		
		return jsonObj;
	}
	
	public String HttpURLConnectionForHtml(String uri, String method) throws Exception {
		
		URL url = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setDoOutput(true);
		con.setDoOutput(true);
		con.connect();
		
		// 리턴된 페이지를 파싱
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuffer sb = new StringBuffer();
		
		String strData = "";
		while ((strData = br.readLine()) != null) {
			sb.append(strData + "\n");
		}
		
		br.close();
		
		return sb.toString();
	}
}
