package com.golf.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.golf.model.common.BaseFileVO;
import com.golf.service.model.GolfFileVO;

/**
 * 업로드 파일 처리<br><br>
 * 
 *  bean 설정 : context-common.xml 참조<br>
 * 		- 업로드 허용 확장자를 변경하려면 fileExtensions 변경<br>
 * @author wimy
 *
 */
public class UploadFile {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadFile.class);
	
	private String uploadPath;
	
	private String subDirectory;
	
	private String fileExtensions;
	
	public UploadFile() {
	}
	
	/**
	 * @return the uploadPath
	 */
	public String getUploadPath() {
		return uploadPath;
	}
	
	/**
	 * @param uploadPath
	 *            the uploadPath to set
	 */
	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}
	
	/**
	 * @return the fileExtensions
	 */
	public String getFileExtensions() {
		return fileExtensions;
	}
	
	/**
	 * @param fileExtensions
	 *            the fileExtensions to set
	 */
	public void setFileExtensions(String fileExtensions) {
		this.fileExtensions = fileExtensions;
	}
	
	/**
	 * @return the subDirectory : (기본값 : "")
	 */
	public String getSubDirectory() {
		return subDirectory;
	}
	
	/**
	 * @param subDirectory
	 */
	public void setSubDirectory(String subDirectory) {
		this.subDirectory = subDirectory;
	}
	
	/**
	 * 업로드 경로와 파일명을 확인하여 부족하거나 남는 슬래시를 정리하여  연결합니다.
	 * 
	 * @param path
	 * @param filename
	 * @return
	 */
	public static String concatUploadPathName(String path, String filename) {
		
		String sPathName = path.trim();
		boolean hasSlash = Pattern.matches(".+/$", sPathName);
		
		if (hasSlash) {
			if (Pattern.matches("^/.+", filename)) {
				sPathName += filename.trim().replaceFirst("^/", "");
			} else {
				sPathName += filename.trim();
			}
		} else {
			if (Pattern.matches("^/.+", filename)) {
				sPathName += filename.trim();
			} else {
				sPathName += "/" + filename.trim();
			}
		}
		
		sPathName = sPathName.replaceAll("/{2,}", "/");
		
		return sPathName;
	}
	
	/**
	 * 업로드 파일을 처리한 후 List<BaseFile> 객체를 반환합니다. 업로드 파일이 없어도 폼의 업로드 폼 갯수 만큼의 List객체가 반환됩니다.
	 * 
	 * @param files
	 * @param deleteFlag
	 *			삭제 플래그가 없을 경우 null 입력. 삭제 플래그가 포함된 업로드 배열은 업로드 파일이 존재해도 업로드하지 않습니다.
	 * @param subDir
	 * 			추가할 서브 디렉토리
	 * @return
	 * @throws Exception
	 */
	public List<BaseFileVO> saveFiles(List<MultipartFile> files, List<String> deleteFlag, String subDir) throws Exception {
		List<BaseFileVO> list = new ArrayList<BaseFileVO>();
		
		//InputStream inputStream;
		//OutputStream outputStream;
		try {
			boolean hasAllowedFile = true;
			String sFileExt = "";
			if (files == null)
				return null;
			
			// 확장자 검사
			for (MultipartFile file : files) {
				if (file.getSize() == 0)
					continue;
				hasAllowedFile = false;
				for (String fe : this.getFileExtensions().toLowerCase().split(",")) {
					sFileExt = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();
					if (sFileExt.equals(fe)) {
						hasAllowedFile = true;
						break;
					}
				}
				if (!hasAllowedFile)
					break;
			}
			
			if (!hasAllowedFile)
				throw new Exception("허용되지 않는 파일(*." + sFileExt + ")");
			
			int idx = 0;
			// 업로드 처리
			for (MultipartFile file : files) {
				//캐스팅 이슈
				//BaseFileVO bf = new BaseFileVO();
				BaseFileVO bf = new GolfFileVO();
				
				// 삭제 체크가 있을 경우(파일 경로 문자열)
				if (deleteFlag != null && !StringUtils.isEmpty(deleteFlag.get(idx))) {
					File f = new File(concatUploadPathName(this.getUploadPath(), deleteFlag.get(idx)));
					if (f.exists()) {
						f.delete();
						bf.setDeleted(true);
						
						bf.setFileName("");
						bf.setContentType("");
						bf.setSize(0);
						bf.setServerSubPath("");
						bf.setPhysicalName("");
					}
				} 
				else {
					if (file.getSize() > 0) {
						bf.setFileName(file.getOriginalFilename());
						bf.setContentType(file.getContentType());
						bf.setSize(file.getSize());
						bf.setExt(org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase());

						// 주의 : 업로드 가상 디렉토리 루트 이후의 경로만 세팅
						if(!StringUtils.isEmpty(subDir))
							this.setSubDirectory(subDir);
						bf.setServerSubPath(this.getSubDirectory());
						
						// 디렉토리 생성
						File dir = new File(this.getUploadPath() + bf.getServerSubPath());
						if (!dir.exists()) {
							dir.mkdirs();
						}

						String sSaveFilename = StringUtil.getUniqueNumber() + "." + org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();
						bf.setPhysicalName(sSaveFilename);
						file.transferTo(new File(this.getUploadPath() + bf.getFullPathName()));
						
					}
					list.add(bf);
				}
				idx++;
			}
		} catch (Exception ex) {
			// logger.error(ex.getMessage(), ex);
			throw ex;
		} finally {
			//outputStream = null;
			//inputStream = null;
		}
		
		return list;
	}
	
	/**
	 * 삭제 및 업로드 후 List<BaseFile> 객체 반환. 
	 * <ul>
	 *  <li> ** saveFiles() 와 다르게 삭제와 업로드가 따로 처리됨. **</li>
	 * 	<li> 업로드 파일이 없어도 폼의 업로드 폼 갯수 만큼의 List객체 반환.</li>
	 *  <li> 삭제 파일이 없을 경우 null 입력 필요.</li>
	 *  <li> 삭제 파일이 있는 경우 따로 만들어서 와야함.</li>
	 *  <li> 업로드 디렉토리는 고정.</li>
	 * </ul>
	 * 
	 * @param uploadFiles
	 * 			업로드할 파일
	 * @param deleteFiles
	 * 			삭제 플래그 - 삭제할 파일의 경로
	 * @param subDir
	 * 			업로드 디렉토리
	 * @return
	 * @throws Exception
	 */
	public List<BaseFileVO> deleteFilesUploadFiles(List<MultipartFile> uploadFiles, List<String> deleteFiles, String subDir) throws Exception {
		List<BaseFileVO> list = new ArrayList<BaseFileVO>();
		
		//InputStream inputStream;
		//OutputStream outputStream;
		
		try {
			boolean hasAllowedFile = true;
			String sFileExt = "";
			if (uploadFiles == null)
				return null;
			
			// 확장자 검사
			for (MultipartFile file : uploadFiles) {
				if (file.getSize() == 0)
					continue;
				hasAllowedFile = false;
				for (String fe : this.getFileExtensions().toLowerCase().split(",")) {
					sFileExt = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();
					if (sFileExt.equals(fe)) {
						hasAllowedFile = true;
						break;
					}
				}
				if (!hasAllowedFile)
					break;
			}
			
			if (!hasAllowedFile)
				throw new Exception("허용되지 않는 파일(*." + sFileExt + ")");

			
			// 업로드 
			int idx = 0;
			for (MultipartFile file : uploadFiles) {
				BaseFileVO bf = new GolfFileVO();
				
				if (file.getSize() > 0) {
					bf.setFileName(file.getOriginalFilename());
					bf.setContentType(file.getContentType());
					bf.setSize(file.getSize());
					bf.setExt(org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase());
					
					// 주의 : 업로드 가상 디렉토리 루트 이후의 경로만 세팅
					if(!StringUtils.isEmpty(subDir))
						this.setSubDirectory(subDir);
					bf.setServerSubPath(this.getSubDirectory());
					
					// 디렉토리 생성
					File dir = new File(this.getUploadPath() + bf.getServerSubPath());
					if (!dir.exists()) {
						dir.mkdirs();
					}

					String sSaveFilename = StringUtil.getUniqueNumber() + "." + org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();
					bf.setPhysicalName(sSaveFilename);
					file.transferTo(new File(this.getUploadPath() + bf.getFullPathName()));
					
				}
				list.add(bf);
				
				idx++;
			}
			
			// 삭제
			for (int i = 0; i < deleteFiles.size(); i++) {
				BaseFileVO bf = new GolfFileVO();
				
				if (deleteFiles != null && !StringUtils.isEmpty(deleteFiles.get(i))) {
					File f = new File(concatUploadPathName(this.getUploadPath(), deleteFiles.get(i)));
					if (f.exists()) {
						f.delete();
						bf.setDeleted(true);
						
						bf.setFileName("");
						bf.setContentType("");
						bf.setSize(0);
						bf.setServerSubPath("");
						bf.setPhysicalName("");
					}
				} 
			}
	
		} catch (Exception e) {
			 //logger.error(e.getMessage(), e);
			throw e;
		} finally {
			//outputStream = null;
			//inputStream = null;
		}
		
		return list;
	}
	
}
