package com.golf.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.golf.model.common.StringPair;

/**
 * globals.properties 모음 ParentController에서 로드하여 객체 접근 방법으로 코딩 가능 globals.properties를 추가하면 이 클래스에도 getter/setter를 추가해야 함.
 * 
 * @author wimy
 *
 */
@Component
public class ConfigBean {
	@Value("#{globals['globals.web.mode']}")
	String _sWebMode;
	
	@Value("#{globals['site.baseTitle']}")
	String _sHTMLTitle;
	
	@Value("#{globals['site.pageTitle']}")
	String _sPageTitle;
	
	@Value("#{globals['session.name.admin']}")
	String _sAdminSessionName;
	
	@Value("#{globals['session.name.user']}")
	String _sUserSessionName;
	
	@Value("#{globals['cookie.popup.prefix']}")
	String _sCookiePopupPrefix;
	
	@Value("#{globals['cannot.using.id']}")
	String _sCannotUsingID;
	
	@Value("#{globals['url.host.local']}")
	String _sHostLocal;
	
	@Value("#{globals['url.host.dev']}")
	String _sHostDev;
	
	@Value("#{globals['url.host.op']}")
	String _sHostOp;
	
	@Value("#{globals['url.cdn.local']}")
	String _sCdnLocal;
	
	@Value("#{globals['url.cdn.dev']}")
	String _sCdnDev;
	
	@Value("#{globals['url.cdn.op']}")
	String _sCdnOp;
	
	@Value("#{globals['url.admin.root']}")
	String _sAdminRoot;
	
	@Value("#{globals['url.admin.login']}")
	String _sAdminLoginURL;
	
	@Value("#{globals['url.admin.main']}")
	String _sAdminMainURL;
	
	@Value("#{globals['url.front.login']}")
	String _sFrontLoginURL;
	
	@Value("#{globals['url.front.main']}")
	String _sFrontMainURL;
	
	@Value("#{globals['view.alert']}")
	String _sViewAlert;
	
	@Value("#{globals['view.error']}")
	String _sViewError;
	
	@Value("#{globals['upload.path.physicallocal']}")
	String _sPathFileLocal;
	
	@Value("#{globals['upload.path.physicaldev']}")
	String _sPathFileDev;
	
	@Value("#{globals['upload.path.physicalop']}")
	String _sPathFileOp;
	
	@Value("#{globals['upload.path.virtual']}")
	String _sPathVirtual;
	
	@Value("#{globals['upload.path.common']}")
	String _sPathCommon;
	
	@Value("#{globals['upload.path.test']}")
	String _sPathTest;
	
	@Value("#{globals['upload.path.editor']}")
	String _sPathEditor;
	
	@Value("#{globals['upload.path.image']}")
	String _sPathImage;
	
	@Value("#{globals['upload.ext']}")
	String _sUploadExtensions;
	
	@Value("#{globals['admin.email']}")
	String _sAdminEmail;
	
	@Value("#{globals['admin.name']}")
	String _sAdminName;
	
	@Value("#{globals['admin.keynumber']}")
	String _sAdminKeyNumber;
	
	@Value("#{globals['admin.address']}")
	String _sAdminAddress;
	
	@Value("#{globals['mail.host']}")
	String _sMailHost;
	
	@Value("#{globals['mail.port']}")
	String _sMailPort;
	
	@Value("#{globals['sns.fb.api']}")
	String _sSNSFbAPIKey;
	
	@Value("#{globals['sns.kakao.api']}")
	String _sSNSKakaoAPIKey;
	
	@Value("#{globals['sns.naver.api']}")
	String _sSNSNaverAPIKey;
	
	@Value("#{globals['common.date.type']}")
	String _sDateType;
	
	@Value("#{globals['common.time.type']}")
	String _sTimeType;
	
	@Value("#{globals['common.emails']}")
	String _sCommonEmails;
	
	@Value("#{globals['email.auth']}")
	String _sEmailAuth;
	
	@Value("#{globals['email.join']}")
	String _sEmailJoin;
	
	@Value("#{globals['email.general']}")
	String _sEmailGeneral;
	
	@Value("#{globals['email.password']}")
	String _sEmailPassword;
	
	@Value("#{globals['email.withdrawal']}")
	String _sEmailWithdrawal;
	
	@Value("#{globals['email.question']}")
	String _sEmailQuestion;
	
	// @Value("#{globals['email.qna']}")
	// String _sEmailQna;
	
	/**
	 * 웹 모드 : local, dev, op
	 * 
	 * @return
	 */
	public String getWebMode() {
		return _sWebMode;
	}
	
	/**
	 * 
	 * @return 기본 타이틀
	 */
	public String getHTMLTitle() {
		return _sHTMLTitle;
	}
	
	/**
	 * @return 페이지 타이틀 속성 이름. model.addAttribute()에 사용되는 타이틀 바인딩용 attribute 이름
	 */
	public String getPageTitle() {
		return _sPageTitle;
	}
	
	/**
	 * @return 관리자 세션 이름
	 */
	public String getSessionNameForAdmin() {
		return _sAdminSessionName;
	}
	
	/**
	 * @return 사용자 세션 이름
	 */
	public String getSessionNameForUser() {
		return _sUserSessionName;
	}
	
	/**
	 * @return 팝업용 쿠키 이름 접두사
	 */
	public String getCookiePopupPrefix() {
		return _sCookiePopupPrefix;
	}
	
	/**
	 * 
	 * @return 사용할 수 없는 아이디 문자열
	 */
	public String[] getCannotUseIDs() {
		return _sCannotUsingID.split(",");
	}
	
	/**
	 * 서버 호스트 : globals.web.mode 값에 따라 값이 달라짐. 기본값 local
	 * 
	 * @return
	 */
	public String getHost() {
		if (this.getWebMode().equals("dev"))
			return this._sHostDev;
		else if (this.getWebMode().equals("op"))
			return this._sHostOp;
		else
			return this._sHostLocal;
	}
	
	/**
	 * CDN : globals.web.mode 값에 따라 값이 달라짐. 기본값 local
	 * 
	 * @return
	 */
	public String getCdn() {
		if (this.getWebMode().equals("dev"))
			return this._sCdnDev;
		else if (this.getWebMode().equals("op"))
			return this._sCdnOp;
		else
			return this._sCdnLocal;
	}
	
	/**
	 * 관리자 루트
	 * 
	 * @return
	 */
	public String getAdminRoot() {
		return _sAdminRoot;
	}
	
	/**
	 * @return 관리자 로그인 URL
	 */
	public String getAdminLoginURL() {
		return _sAdminLoginURL;
	}
	
	/**
	 * @return 관리자 메인 URL
	 */
	public String getAdminMainURL() {
		return _sAdminMainURL;
	}
	
	/**
	 * @return 사용자 로그인 URL
	 */
	public String getFrontLoginURL() {
		return _sFrontLoginURL;
	}
	
	/**
	 * @return 사용자 메인 URL
	 */
	public String getFrontMain() {
		return _sFrontMainURL;
	}
	
	/**
	 * 
	 * @return 스프링 컨트롤러 alert 출력용 URL
	 */
	public String getViewAlert() {
		return _sViewAlert;
	}
	
	/**
	 * 
	 * @return 스프링 컨트롤러 error 출력용 URL
	 */
	public String getViewError() {
		return _sViewError;
	}
	
	/**
	 * @return 이메일 서버 호스트
	 */
	public String getMailHost() {
		return _sMailHost;
	}
	
	/**
	 * @return 메일 서버 포트 : 기본값 25
	 */
	public int getMailPort() {
		if (StringUtils.isEmpty(_sMailPort))
			return 25;
		else
			return Integer.parseInt(_sMailPort);
	}
	
	/**
	 * 파일 업로드 물리 경로 globals.properties - upload.path.file 참조 : globals.web.mode 값에 따라 값이 달라짐. 기본값 local
	 * 
	 * @return
	 */
	public String getUploadPathFile() {
		if (this.getWebMode().equals("dev"))
			return this._sPathFileDev;
		else if (this.getWebMode().equals("op"))
			return this._sPathFileOp;
		else
			return this._sPathFileLocal;
	}
	
	/**
	 * 업로드 루트 가상디렉토리
	 * 
	 * @return
	 */
	public String getUploadVirtualPath() {
		return this._sPathVirtual;
	}
	
	/***
	 * 업로드 일반 파일 경로 (서브 디렉토리)
	 * 
	 * @return
	 */
	public String getUploadCommonPath() {
		return this._sPathCommon;
	}
	
	/**
	 * 업로드 테스트 경로 (서브 디렉토리)
	 * 
	 * @return
	 */
	public String getUploadTestPath() {
		return this._sPathTest;
	}
	
	/**
	 * 업로드 에디터 경로 (서브 디렉토리)
	 * 
	 * @return
	 */
	public String getUploadPathEditor() {
		return _sPathEditor;
	}
	
	/**
	 * 업로드 이미지 경로 (서브 디렉토리)
	 * 
	 * @return
	 */
	public String getUploadPathImage() {
		return _sPathImage;
	}
	
	/**
	 * 업로드 가능 확장자 배열 : globals.properties = upload.ext 참조
	 * 
	 * @return
	 */
	public String[] getUploadExtensions() {
		String[] arr = _sUploadExtensions.split(",");
		return arr;
	}
	
	/**
	 * 관리자 대표 이메일
	 * 
	 * @return
	 */
	public String getAdminEmail() {
		return _sAdminEmail;
	}
	
	/**
	 * 관리자 대표 이메일 표시용 이름
	 * 
	 * @return
	 */
	public String getAdminName() {
		return _sAdminName;
	}
	
	/**
	 * 대표 전화번호
	 * 
	 * @return
	 */
	public String getAdminKeyNumber() {
		return _sAdminKeyNumber;
	}
	
	/**
	 * 회사 주소
	 * 
	 * @return
	 */
	public String getAdminAddress() {
		return _sAdminAddress;
	}
	
	/**
	 * 페이스북 API 키
	 * 
	 * @return
	 */
	public String getSNSFbAPIKey() {
		return _sSNSFbAPIKey;
	}
	
	/**
	 * 카카오 API 키
	 * 
	 * @return
	 */
	public String getSNSKakaoAPIKey() {
		return _sSNSKakaoAPIKey;
	}
	
	/**
	 * 네이버 API 키
	 * 
	 * @return
	 */
	public String getSNSNaverAPIKey() {
		return _sSNSNaverAPIKey;
	}
	
	/**
	 * 날짜 표시 유형 : SearchVO 참조
	 * 
	 * @return
	 */
	public String getDateType() {
		return _sDateType;
	}
	
	/**
	 * 시각 표시 유형
	 * 
	 * @return
	 */
	public String getTimeType() {
		return _sTimeType;
	}
	
	List<StringPair> _listEmails = null;
	
	/**
	 * 공용 이메일 목록 globals.properties : common.emails 참조
	 * 
	 * @return
	 */
	public List<StringPair> getEmails() {
		if (!StringUtils.isEmpty(this._sCommonEmails)) {
			if (_listEmails == null) {
				_listEmails = new ArrayList<StringPair>();
				
				String[] arrEmails = this._sCommonEmails.split(",");
				for (String s : arrEmails)
					_listEmails.add(new StringPair(s.trim(), s.trim()));
			}
		}
		
		return _listEmails;
	}
	
	/**
	 * 이메일 템플릿 URL - 인증
	 * 
	 * @return the _sEmailAuth
	 */
	public String getEmailAuth() {
		return _sEmailAuth;
	}
	
	/**
	 * 이메일 템플릿 URL - 회원가입
	 * 
	 * @return the _sEmailMember
	 */
	public String getEmailJoin() {
		return _sEmailJoin;
	}
	
	/**
	 * 이메일 템플릿 URL - 일반
	 * 
	 * @return the _sEmailGeneral
	 */
	public String getEmailGeneral() {
		return _sEmailGeneral;
	}
	
	/**
	 * 이메일 템플릿 URL - 비밀번호 변경
	 * 
	 * @return
	 */
	public String getEmailPassword() {
		return _sEmailPassword;
	}
	
	/**
	 * 이메일 템플릿 URL - 회원 탈퇴
	 * 
	 * @return
	 */
	public String getEmailWithdrawal() {
		return _sEmailWithdrawal;
	}
	
	/**
	 * 이메일 템플릿 URL - 문의 형식
	 * 
	 * @return
	 */
	public String getEmailQuestion() {
		return _sEmailQuestion;
	}
	
	/**
	 * 이메일 템플릿 URL - QNA
	 * 
	 * @return
	 */
	public String getEmailQna() {
		return _sEmailQuestion;
	}
}
