package com.golf.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.golf.model.FileMeta;
import com.golf.model.common.BaseFileVO;
import com.golf.util.StringUtil;
import com.golf.util.UploadFile;

/**
 * 업로드 컨트롤러
 * @author wimy
 *
 */
@Controller
public class UploadController extends PrtController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
	UploadFile _upload;
	
	
	/**
	 * drag & drop 업로드 처리
	 * - 주의 : submit 방식으로 업로드 불가능 
	 * - ServletFilter 사용중
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/common/dnd_uploadBasic.do", method = RequestMethod.POST)
	@ResponseBody
	public LinkedList<FileMeta> dndUploadBasic(MultipartHttpServletRequest request, HttpServletResponse response) {
		
		LinkedList<FileMeta> files = new LinkedList<FileMeta>();
		Iterator<String> itr = request.getFileNames();
		List<MultipartFile> inputFiles = new ArrayList<MultipartFile>();
		while (itr.hasNext()) {
			MultipartFile mpf = request.getFile(itr.next());
			inputFiles.add(mpf);
			//logger.debug("{} : {}", mpf.getOriginalFilename(), mpf.getSize());// System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());
		}
		
		try {
			List<BaseFileVO> bfiles = _upload.saveFiles(inputFiles, null, super.getConfig().getUploadPathTemp());
			for (int i = 0; i < bfiles.size(); i++) {
				FileMeta fm = new FileMeta();
				fm.setBytes(inputFiles.get(i).getBytes());
				fm.setPhysicalName(bfiles.get(i).getFullPathName());
				fm.setFileSize(StringUtil.getFileSize(bfiles.get(i).getSize()));
				fm.setContentType(bfiles.get(i).getContentType());
				fm.setFileName(bfiles.get(i).getFileName());
				
				files.add(fm);
			}
		}
		catch (Exception e) {
			//허용되지 않는 파일
			FileMeta fm = new FileMeta();
			fm.setPhysicalName(e.getMessage());
			fm.setFileName(e.getMessage());
			fm.setFileSeq(-1);
			fm.setContentType(e.getMessage());
			fm.setFileSize("0");
			
			files.add(fm);
		}
		
		return files;
	}
	

}
