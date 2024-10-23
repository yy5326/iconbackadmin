package com.golf.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * jxl 다운로드 유틸리티
 * @author wimy
 *
 */
public class ExcelDownloadUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ExcelDownloadUtil.class);
	
	
	/**
	 * 엑셀 다운로드<br>
	 *   - jxl 사용 : 템플릿 xlsx 필요<br>
	 *   - TestController 참조<br>
	 * @param request
	 * @param response
	 * @param mapData : 바인딩 데이터 맵
	 * @param saveFilename : 빈 값 또는 null일 경우 yyyy-MM-dd_HHmm.xlsx
	 * @param templateFileName : /excels 디렉토리 내 엑셀 템플릿 파일명  
	 */
	public static void downloadExcel(HttpServletRequest request, HttpServletResponse response, Map<String, Object> mapData, String saveFilename, String templateFileName) {
		
		String sTemplatePath = request.getSession().getServletContext().getRealPath("/excels");
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(sTemplatePath + "/" + templateFileName));
			
			XLSTransformer transformer = new XLSTransformer();
			Workbook resultWorkbook = transformer.transformXLS(is, mapData);
			
			if (StringUtils.isEmpty(saveFilename)) {
				if (!StringUtils.isEmpty(mapData.get("subject1"))) 
					saveFilename = URLEncoder.encode(mapData.get("subject1").toString(), "UTF-8") + "_" + StringUtil.getDateToString("yyyyMMdd_HHmm");
				else 
					saveFilename = StringUtil.getDateToString("yyyyMMdd_HHmm");
			}
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + saveFilename + ".xlsx\"");
			
			os = response.getOutputStream();
			resultWorkbook.write(os);
			
			os.flush();
			os.close();
			is.close();
		}
		catch (ParsePropertyException | IOException | org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
			logger.error("MakeExcel Exception : {}", e.getMessage(), e);
		}
		finally {
			if (os != null)
				os = null;
			if (is != null)
				is = null;
		}
	}
}
