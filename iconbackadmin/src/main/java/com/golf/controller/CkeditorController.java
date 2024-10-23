package com.golf.controller;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.golf.model.common.BaseFileVO;
import com.golf.util.UploadFile;

/**
 * CKEditor 이미지 업로드 컨트롤러
 * 
 * @author wimy
 *
 */
@Controller
public class CkeditorController extends PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(CkeditorController.class);
	
	@Autowired
	UploadFile uploadFile;
	
	/**
	 * CK editor 업로드 처리 - UTF-8
	 * @param request
	 * @param response
	 * @param upload 업로드 폼 이름
	 */
	@RequestMapping(value = "/common/ckeditor_upload.do", method = RequestMethod.POST)
	public void ckeditorImageUpload(HttpServletRequest request, HttpServletResponse response, @RequestParam MultipartFile upload) {
		
		OutputStream out = null;
		PrintWriter printWriter = null;
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		try {
			List<MultipartFile> list = new ArrayList<MultipartFile>();
			list.add(upload);
			List<BaseFileVO> bfiles = uploadFile.saveFiles(list, null, super.getConfig().getUploadPathEditor());
			
			String callback = request.getParameter("CKEditorFuncNum");
			String fileUrl = super.getConfig().getUploadPathVirtual() + bfiles.get(0).getFullPathName();
			
			printWriter = response.getWriter();
			printWriter.println("<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + fileUrl + "','이미지를 업로드 하였습니다.'" + ")</script>");
			printWriter.flush();
			
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			try {
				if (out != null)
					out.close();
				
				if (printWriter != null)
					printWriter.close();
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}
