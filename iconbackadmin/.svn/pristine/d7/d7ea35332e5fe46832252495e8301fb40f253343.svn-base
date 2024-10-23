package com.golf.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;

@WebServlet("/FileUploadServlet") 
public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		// 응답콘텐츠타입을 JSON형태로 지정
		// TODO: Iframe을 통한 JSON형태의 결과를 받을때 다운로드 창이 뜨는 문제.
		// response.setContentType("application/json; charset=UTF-8");
		// response.setContentType("text/plain; charset=UTF-8");
		
		// Iframe 기반으로된 업로드방식은 응답 콘테츠 타입이 application/json일 경우
		// 원하지 않는 다운로드 대화상자를 볼수있습니다.
		// 그래서 Content-type 이 text/plain or text/html 형식이어야합니다.
		// 참고: http://javaking75.blog.me/220073311435
		
		// System.out.println(request.getHeader("accept"));
		if (request.getHeader("accept").indexOf("application/json") != -1) {
			response.setContentType("application/json; charset=UTF-8");
		}
		else {
			// IE workaround
			response.setContentType("text/plain; charset=UTF-8");
		}
		
		PrintWriter out = response.getWriter();
		
		RequestModel model = new RequestModel();
		
		try {
			// 디스크상의 실제 경로 얻기
			String contextRootPath = this.getServletContext().getRealPath("/");
			
			// 1. 메모리나 파일로 업로드 파일 보관하는 FileItem의 Factory설정
			DiskFileItemFactory diskFactory = new DiskFileItemFactory();
			// 임시 저장폴더
			diskFactory.setRepository(new File(contextRootPath + "/WEB-INF/temp"));
			
			// 2. 업로드 요청을 처리하는 ServletFileUpload 생성
			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			upload.setSizeMax(10 * 1024 * 1024); // 10MB : 전체 최대 업로드 파일 크기
			// upload.setFileSizeMax(10 * 1024 * 1024); //10MB : 파일하나당 최대 업로드 파일 크기
			
			// 3. 업로드 요청 파싱해서 FileItem 목록 구함
			List<FileItem> items = upload.parseRequest(request);
			
			for (FileItem item : items) {
				
				// 5. FileItem이 폼입력 항목인지 여부에 따라 알맞은 처리
				if (item.isFormField()) { // 파일이 아닌경우
					processFormField(model, item);
				}
				else { // 파일인 경우
					if (item.getSize() != 0) { // 업로드된 파일이 있을경우에만 처리
						processUploadFile(model, item, contextRootPath);
					}
				}
			}
			
			// 값 객체(VO,DTO)를 JSON형태로 문자열로 변환하기 위핸 Gson객체 생성.
			Gson gson = new Gson();
			String outString = gson.toJson(model);
			// System.out.println(outString);
			out.print(outString);
			
		}
		catch (Exception e) {
			
			e.printStackTrace();
			
			out.print("{\"result\":\"500\"");
			out.print(",\"msg\":\"" + e.getMessage());
			out.print("\"}");
		}
	}
	
	// 파일 양식 처리
	private void processUploadFile(RequestModel model, FileItem item, String contextRootPath) throws Exception {
		
		String name = item.getFieldName(); // 필드 이름 얻기
		String fileName = item.getName(); // 파일명 얻기
		String contentType = item.getContentType(); // 컨텐츠 타입 얻기
		long fileSize = item.getSize(); // 파일의 크기 얻기
		
		// 업로드 파일명을 현재시간으로 변경후 저장
		// String uploadedFileName = System.currentTimeMillis()
		// +fileName.substring(fileName.lastIndexOf("."));
		String uploadedFileName = System.currentTimeMillis() + UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));;
		// 저장할 절대경로로 파일 객체 생성
		File uploadedFile = new File(contextRootPath + "/upload/" + uploadedFileName);
		item.write(uploadedFile); // 파일 저장
		
		FileInfoModel fileInfoModel = new FileInfoModel(name, fileName, uploadedFileName, fileSize, contentType);
		
		if ("photo".equals(name)) {
			List<FileInfoModel> photoList = model.getPhoto();
			if (photoList == null)
				photoList = new ArrayList<FileInfoModel>();
			photoList.add(fileInfoModel);
			model.setPhoto(photoList);
		}
		else if ("file".equals(name)) {
			model.setFile(fileInfoModel);
		}
		item.delete(); // 파일과 관련된 자원을 제거한다.
	}
	
	// 일반 양식 처리
	private void processFormField(RequestModel model, FileItem item) throws Exception {
		String name = item.getFieldName();
		String value = item.getString("UTF-8");
		
		// 요청파라미터의 이름에 따라 값객체(RequestModel)에 값 할당.
		if ("title".equals(name)) {
			model.setTitle(value);
		}
		else if ("description".equals(name)) {
			model.setDescription(value);
		}
	}
	
}
