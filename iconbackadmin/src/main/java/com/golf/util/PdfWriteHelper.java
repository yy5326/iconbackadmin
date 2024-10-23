package com.golf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.golf.model.common.BaseFileVO;
import com.golf.service.GlobalProperty;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

/**
 * PDF 생성 helper
 * @author wimy
 *
 */
public class PdfWriteHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(PdfWriteHelper.class);
	
	private PdfVO _pdfVO;
	
	/**
	 * Ctor1
	 * @param templateFile html 템플릿 파일. 웹 절대 경로
	 * @param fontFile 폰트 파일. 웹 절대 경로
	 * @param fontAlias 폰트 별칭. 필수
	 */
	public PdfWriteHelper(String templateFile, String fontFile, String fontAlias) {
		this.getPdfVO().setTemplateFile(templateFile);
		this.getPdfVO().setFontFile(fontFile);
		this.getPdfVO().setFontAlias(fontAlias);
	}
	
	/**
	 * Ctor2
	 * @param templateFile 템플릿 파일. 웹 절대 경로
	 */
	public PdfWriteHelper(String templateFile) {
		this(templateFile, "/common/font/malgun.ttf", "MalgunGothic");
	}
	
	/**
	 * PdfVO 모델
	 * @return
	 */
	public PdfVO getPdfVO() {
		if (this._pdfVO == null)
			this._pdfVO = new PdfVO();
		return this._pdfVO;
	}
	
	
	/**
	 * PDF 저장 / 다운로드<br>
	 *  - 다운로드 : 컨트롤러에서 view return을 null로 설정해야 함<br>
	 * @param prop : GlobalProperty
	 * @param type : 형식 - "": PDF 저장, download: PDF 다운로드
	 * @param request : HttpServletRequest
	 * @param response : HttpServletResponse
	 * @return BaseFileVO  PDF저장: BaseFileVO, PDF다운로드: null
	 * @throws Exception
	 */
	public BaseFileVO savePdf(GlobalProperty prop, String type, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PdfVO vo = this.getPdfVO();
		
		String sPath = prop.getUploadPathVirtual() + prop.getUploadPathFiles();
		String sFileName = StringUtil.getUniqueNumber() + ".pdf";
		
		File f = new File(request.getSession().getServletContext().getRealPath(sPath), vo.getSubDir());
		if (!f.exists())
			f.mkdir();
		
		sPath = sPath + vo.getSubDir();
		f = new File(request.getSession().getServletContext().getRealPath(sPath), sFileName);
		
		if (StringUtil.isEmpty(vo.getFontFile()))
			throw new Exception("PDF를 생성할 수 없습니다.\n글꼴 파일이 설정되지 않았습니다.");
		
		Document document = new Document(PageSize.A4, 10f, 10f, 10f, 10f);
		PdfWriter writer = null;
				
		if (type.toLowerCase().equals("download")) {
			response.setContentType("application/download; charset=utf-8");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + sFileName + "\"");
			writer =  PdfWriter.getInstance(document, response.getOutputStream());
		}
		else
			writer = PdfWriter.getInstance(document, new FileOutputStream(f));
		
		try {
			
			writer.setInitialLeading(12.5f);
			
			// Document 오픈
			document.open();
			XMLWorkerHelper helper = XMLWorkerHelper.getInstance();
			
			// CSS
			CSSResolver cssResolver = new StyleAttrCSSResolver();
			if (!StringUtil.isEmpty(vo.getCssFile())) {
		    	CssFile cssFile = helper.getCSS(new FileInputStream(request.getSession().getServletContext().getRealPath(vo.getCssFile())));
			    cssResolver.addCss(cssFile);
		    }

			// HTML, 폰트 설정
		    XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
			fontProvider.register(request.getSession().getServletContext().getRealPath(vo.getFontFile()), vo.getFontAlias());
			
			CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
			 
		    HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		    htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		 
		    // Pipelines
		    PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
		    HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
		    CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
		 
		    XMLWorker worker = new XMLWorker(css, true);
		    //XMLParser xmlParser = new XMLParser(worker, Charset.forName("UTF-8"));
		    XMLParser xmlParser = new XMLParser(worker, Charset.defaultCharset());
		 
		    String sHtml = this.readTextFile(request.getSession().getServletContext().getRealPath(vo.getTemplateFile()));
		    vo.getData().put("||#font||", vo.getFontAlias());		//글꼴 별칭으로 PDF 글꼴 적용
		    for (String key : vo.getData().keySet()) {
		    	String val = vo.getData().get(key) != null ? String.valueOf(vo.getData().get(key)) : "";
		    	sHtml = sHtml.replace(key, val);
		    }
		    
		    xmlParser.parse(new StringReader(sHtml));
		    
		    if (!type.toLowerCase().equals("download")) { 
		    	document.close();
			    writer.close();
			    
			  //logger.debug(">>>>>> length : {}", f.length());
				BaseFileVO bf = new BaseFileVO();
				bf.setContentType("application/pdf");
				bf.setSize(f.length());
				String sPhysicalName = prop.getUploadPathFiles() + vo.getSubDir() + "/" + sFileName;
				bf.setPhysicalName(sPhysicalName);

				return bf;
		    }
		    else {
		    	writer.flush();
		    	writer.close();
		    	return null;
		    }
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * 텍스트 파일 읽어오기<br>
	 * @param pathName
	 * @return
	 * @throws IOException
	 */
	public String readTextFile(String pathName) throws IOException {
		File file = new File(pathName);
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.defaultCharset().displayName()));
			String text = null;
			while ((text = reader.readLine()) != null) {
				sb.append(text);
			}
		}
		catch (IOException e) {
			throw e;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (IOException e) {
				throw e;
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * PdfWriteHelper용 내부 모델
	 * @author wimy
	 *
	 */
	public class PdfVO {
		
		public PdfVO() {}
		
		private String templateFile;
		private String fontFile;
		private String fontAlias;
		private String cssFile;
		private String subDir;
		private String physicalName;
		private Map<String, Object> data;
		
		
		/**
		 * PDF 변환용 템플릿 HTML 파일 : 웹 절대경로
		 * @return
		 */
		public String getTemplateFile() {
			return templateFile;
		}
		
		/**
		 * PDF 변환용 템플릿 HTML 파일 : 웹 절대경로
		 * @param templateFile
		 */
		public void setTemplateFile(String templateFile) {
			this.templateFile = templateFile;
		}
		
		/**
		 * 폰트 파일 : 웹 절대경로
		 * @return
		 */
		public String getFontFile() {
			return fontFile;
		}

		/**
		 * 폰트 파일 : 웹 절대경로
		 * @param fontFile
		 */
		public void setFontFile(String fontFile) {
			this.fontFile = fontFile;
		}
		
		/**
		 * 폰트 별칭 : 필수
		 * @return
		 */
		public String getFontAlias() {
			return fontAlias;
		}

		/**
		 * 폰트 별칭 : 필수
		 * @param fontAlias
		 */
		public void setFontAlias(String fontAlias) {
			this.fontAlias = fontAlias;
		}

		/**
		 * PDF 변환용 적용 CSS 파일 : 웹 절대경로
		 * @return
		 */
		public String getCssFile() {
			return cssFile;
		}
		
		/**
		 * PDF 변환용 적용 CSS 파일 : 웹 절대경로
		 * @param cssFile
		 */
		public void setCssFile(String cssFile) {
			this.cssFile = cssFile;
		}
		
		/**
		 * PDF가 저장될 업로드 서브 디렉토리
		 * @return
		 */
		public String getSubDir() {
			return subDir;
		}
		
		/**
		 * PDF가 저장될 업로드 서브 디렉토리
		 * @param subDir
		 */
		public void setSubDir(String subDir) {
			this.subDir = subDir;
		}
		
		/**
		 * [사용안함]기존 PDF 파일 경로 (KDE_FILE.PHYSICAL_NAME) : 기존 파일 덮어쓰기. 빈 값이거나 파일이 존재하지 않으면 신규 생성
		 * @return
		 */
		@Deprecated
		public String getPhysicalName() {
			return physicalName;
		}
		
		/**
		 * [사용안함]기존 PDF 파일 경로 (KDE_FILE.PHYSICAL_NAME) : 기존 파일 덮어쓰기. 빈 값이거나 파일이 존재하지 않으면 신규 생성
		 * @param physicalName
		 */
		@Deprecated
		public void setPhysicalName(String physicalName) {
			this.physicalName = physicalName;
		}

		/**
		 * 템플릿 내 치환 문자열 맵 - 치환 문자열 형식.  ex)||#font||
		 * @return
		 */
		public Map<String, Object> getData() {
			if (this.data == null)
				this.data = new HashMap<String, Object>();
			return data;
		}

		/**
		 * 템플릿 내 치환 문자열 맵
		 * @param data
		 */
		public void setData(Map<String, Object> data) {
			this.data = data;
		}	
		
	}
}
