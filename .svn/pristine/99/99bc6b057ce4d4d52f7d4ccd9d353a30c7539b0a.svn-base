package com.golf.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.golf.model.common.AlertModel;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.util.StringUtil;

/**
 * 파일 다운로드 컨트롤러
 * 
 * @author wimy
 *
 */
@Controller
public class DownloadController extends PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);
	
	@Resource
	GeneralDAOImpl _gDao;
	
	
	/**
	 * 브라우저 이름 가져오기
	 * @param request
	 * @return
	 */
	private String getBrowser(HttpServletRequest request) {
		String header = request.getHeader("User-Agent");
		String sRtn = "FireFox";
		
		if (header.contains("MSIE"))
			sRtn = "MSIE";
		else if (header.contains("Chrome"))
			sRtn = "Chrome";
		else if (header.contains("Opera"))
			sRtn = "Opera";
		
		return sRtn;
	}
	
	/**
	 * 스트림 파일 다운로드
	 * 
	 * @param filename : 하위 경로(DB 컬럼값)를 포함한 다운로드할 파일 URL (서버측 파일)
	 * @param originalName : 원본 파일명 (클라이언트가 다운로드 받을 파일명)
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/common/download.do")
	public String fileDownload(@RequestParam(required = true) String filename, String originalName, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String uploadPath = super.getConfig().getUploadPathPhysical();
		//String sPath = filename.substring(0, filename.lastIndexOf("/"));
		String sFilename = filename.substring(filename.lastIndexOf("/") + 1); 
		
		//원본 파일명 : 다운로드 요청 브라우저에 보낼 파일명
		if (StringUtils.isEmpty(originalName))
			originalName = sFilename;
		
		//주의 : 브라우저 별 파일명 인코딩이 다름
		String sBrowser = this.getBrowser(request);
		if (sBrowser.equals("MSIE"))
			originalName = java.net.URLEncoder.encode(originalName, "UTF-8").replaceAll("\\+", "%20");
		else
			originalName = new String(originalName.getBytes("UTF-8"), "ISO-8859-1");
		
		//logger.debug(">>>>> originalName : {}", originalName);
		
		BufferedInputStream bis;
		OutputStream output;
		
		try {
			File file = new File(uploadPath + filename);
			int iFileSize = (int) file.length();
			if (iFileSize > 0) {
				response.setContentType("application/octet-stream");
				response.setContentLength(iFileSize);
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + originalName + "\"");
				
				int read;
				byte[] bytes = new byte[1024];
				bis = new BufferedInputStream(new FileInputStream(file));
				output = response.getOutputStream();
				
				while ((read = bis.read(bytes, 0, bytes.length)) != -1) {
					output.write(bytes, 0, read);
				}
				
				output.flush();
				output.close();
				bis.close();
				
				return null;
			} else {
				logger.warn("file not exist. filename : {}", filename);
				AlertModel am = new AlertModel();
				am.setMessage(super.getMessage("common.nofile"));
				am.setScript("history.back();");
				model.addAttribute("alert", am);
				return getConfig().getViewAlert();
			}
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} 
		finally {
			output = null;
			bis = null;
		}
	}
	
	/**
	 * 일괄 파일 다운로드 - zip 압축 다운로드
	 * @param files (구분자 |) 업로드 루트 경로를 제외한 서버 저장 파일 경로 + 파일명 (배열형 문자열)
	 * @param names (구분자 |) 압축 파일 내 저장될 파일명 (배열형 문자열)
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/common/downloadAll.do")
	public String fileDownloadZip(@RequestParam(required = true) String files, @RequestParam(required = true) String names, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		int bufferSize = 1024 * 2;
		String ouputName = StringUtil.getDateToString("yyyy-MM-dd_HHmm") + ".zip";
		ZipOutputStream zos = null;
		try {
			response.setContentType("application/zip; charset=utf-8");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + ouputName + "\"");
			
			OutputStream os = response.getOutputStream();
			zos = new ZipOutputStream(os);
			zos.setLevel(8); // default 8
			//BufferedInputStream bis = null;
			
			String[] originFiles = URLDecoder.decode(files, Charset.defaultCharset().displayName()).split("\\|");
			String[] saveNames = URLDecoder.decode(names, Charset.defaultCharset().displayName()).split("\\|");
			int i = 0;
			for (String physicalName : originFiles) {
				String sRealPath = super.getServletContext().getRealPath(super.getConfig().getUploadPathVirtual() + physicalName);
				//logger.debug("sRealPath + file : {}" , sRealPath + file);
				File sourceFile = new File(sRealPath);
				if (!sourceFile.exists()) {
					logger.error("[압축일괄다운로드] 파일 없음. {}, {}", sourceFile.getAbsolutePath(), saveNames[i]);
					continue;
				}

//				//old coding
//				bis = new BufferedInputStream(new FileInputStream(sourceFile));
//				ZipEntry zentry = new ZipEntry(saveNames[i]);
//				zentry.setTime(sourceFile.lastModified());
//				zos.putNextEntry(zentry);
//				
//				byte[] buffer = new byte[bufferSize];
//				int cnt = 0;
//				while ((cnt = bis.read(buffer, 0, bufferSize)) != -1) {
//					zos.write(buffer, 0, cnt);
//				}
//				zos.closeEntry();
				
				
				boolean isEntry = false;
				int j = 0;
				while (!isEntry) {
					try {
						String sFileName = saveNames[i];
						if (j > 0)
							sFileName = FilenameUtils.getBaseName(sFileName) + " (" + j + ")." + FilenameUtils.getExtension(sFileName);
						
						ZipEntry zentry = new ZipEntry(sFileName);
						zentry.setTime(sourceFile.lastModified());
						zos.putNextEntry(zentry);
						zos.write(FileUtils.readFileToByteArray(sourceFile));
						zos.closeEntry();
						isEntry = true;
					}
					catch (ZipException e) {
						j++;
					}
					catch (Exception e) {
						logger.error("[압축일괄다운로드 오류]");
						logger.error(e.getMessage(), e);
						break;
					}
				}
				i++;
			}
			
			zos.flush();
			zos.close();
			//bis.close();
			
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			AlertModel am = new AlertModel();
			am.setMessage(super.getMessage("common.nofile"));
			am.setScript("history.back();");
			model.addAttribute("alert", am);
			return getConfig().getViewAlert();
		}
		finally {
			if (zos != null) {
				zos.close();
				zos = null;
			}
		}
		
		return null;
	}
}
