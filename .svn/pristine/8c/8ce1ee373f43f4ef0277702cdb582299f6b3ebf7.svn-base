package com.golf.model.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.golf.model.GeneralModel;

/**
 * 공용 검색 모델 <br> 
 * 검색 조건이 더 생길 경우 상속 클래스를 별도로 만들 것
 * 
 * @author wimy
 * 
 */
public class SearchVO implements GeneralModel {
	
	private static final long serialVersionUID = 6937599700079366836L;

	/**
	 * <p>
	 * search word는 영문
	 * </p>
	 */
	public final static String REGEX_PATTERN_WHITE_SPACE = "[\\s]+";

	/**
	 * <p>
	 * search word 분리자 검색 문법 토큰을 포함한 파싱전 검색어
	 * </p>
	 */
	private static final String TOKEN_SEPARATOR = " ";

	/**
	 * <p>
	 * 공용 날짜 표시 형식 
	 * </p>
	 */
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 공용 시각 표시 형식
	 */
	public static final SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm");
		
	/**
	 * 공용 날짜 시각 표시 형식
	 */
	public static final SimpleDateFormat SIMPLE_DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static final int MILLIS_IN_A_DAY = 24 * 60 * 60 * 1000;
	
	/**
	 * <p>
	 * 페이징 사용 여부
	 * </p>
	 */
	private Boolean paginated;

	/**
	 * 관리자용 쿼리 여부
	 */
	private boolean isAdmin;

	/**
	 * 페이징 객체
	 */
	private PagingVO paging;

	/**
	 * 검색어
	 */
	private String searchWord = "";

	private String type1 = "";

	private String type2 = "";
	
	private String type3 = "";

	private String type4 = "";
	
	private String type5 = "";
	
	private String type6 = "";
	
	private String type7 = "";
	
	/**
	 * 공통 코드 마스터 그룹코드
	 */
	private String groupCode = "";

	/**
	 * 정렬
	 */
	private String orderBy = "";
	
	/**
	 * 정렬
	 * 2017-06-12 : 골프택배 - 테이블 헤더 클릭 정렬 처리용  
	 * 
	 */
	private String ho = "";
	
	/**
	 * 날짜 검색 시작일 YYYY-MM-DD
	 */
	private String startDate = "";

	/**
	 * 날짜 검색 종료일 YYYY-MM-DD
	 */
	private String endDate = "";

	/**
	 * 날짜 검색 오늘 YYYY-MM-DD
	 */
	private String today = "";

	private String pageType = "";
	
	private String redirectURL = "";
	
	private String status = "";
	
	private String statusName = "";
	
	private Date searchDate = null;
/*
 *	공통 업로드 처리용 파라미터 
 */
	private String tableName = "";
	private String refSeq = "";
	private String pkName = "";
	private String pkSeq = "";
	private String fileType = "";
	private String addOnly = "N";
	private List<String> fileTypes;
	
	private String tab = "";
	
	private String pdt = "";
	
	private String ocd;
	
	private String bcd;
	
	//Iframe 여부확인 [리턴 페이지 경로설정]
	private String parentYn;
	
	/**
	 * @return the paginated
	 */
	// public Boolean isPaginated()
	// {
	// return (paginated != null && paginated) || (paginated == null && paging
	// != null);
	// }

	/**
	 * @param paginated
	 *            the paginated to set
	 */
	public void setPaginated(Boolean paginated) {
		this.paginated = paginated;
	}

	public Boolean getPaginated() {
		return paginated;
	}

	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * @param isAdmin
	 *            the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo를 상속한 PagingVO
	 * @return the paging
	 */
	public PagingVO getPaging() {

		if (this.paging == null)
			this.paging = new PagingVO();

		return paging;
	}

	/**
	 * @param paginationInfo
	 *            the paging to set
	 */
	public void setPaging(PagingVO paging) {
		this.paging = paging;
	}
	
	/**
	 * 페이지 인덱스 파라미터
	 * @return
	 */
	public int getCpage() {
		return this.getPaging().getPageIndex();
	}
	
	/**
	 * 페이지 인덱스 파라미터
	 * @param currentPageIndex
	 */
	public void setCpage(int currentPageIndex) {
		this.getPaging().setPageIndex(currentPageIndex);
	}
	
	/**
	 * 페이지 인덱스
	 * 
	 * @return
	 */
	public int getPageIndex() {
		return getPaging().getPageIndex();
	}

	/**
	 * 페이지 인덱스
	 */
	public void setPageIndex(int pageIndex) {
		getPaging().setPageIndex(pageIndex);
	}

	/**
	 * 한 페이지에 보이는 레코드 수
	 * @return
	 */
	public int getRecordCount() {
		return getPaging().getRecordCount();
	}
	
	public void setRecordCount(int recordCount) {
		getPaging().setRecordCount(recordCount);
	}
	
	
	/**
	 * 페이징 개수
	 * @return
	 */
	
	public int getBlockCount() {
		return getPaging().getBlockCount();
	}
	
	public void setBlockCount(int blockCount) {
		getPaging().setBlockCount(blockCount);
	}
	
	
	
	/**
	 * 검색된 전체 레코드 수
	 * 
	 * @return
	 */
	public int getTotalCount() {
		return getPaging().getTotalCount();
	}

	/**
	 * 검색된 전체 레코드 수
	 * 
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		getPaging().setTotalCount(totalCount);
	}

	/**
	 * 검색어 - 기본값 = ""
	 * 
	 * @return
	 */
	public String getSearchWord() {
		if (this.searchWord == null)
			this.searchWord = "";

		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = (searchWord == null || searchWord.length() == 0) ? ""
				: searchWord.trim();
	}

	/**
	 * alias of searchWord
	 * 
	 * @return
	 */
	public String getKeyword() {
		return this.getSearchWord();
	}

	/**
	 * alias of searchWord
	 * 
	 * @param keyword
	 */
	public void setKeyword(String keyword) {
		this.setSearchWord(keyword);
	}
	
	
	/**
	 * 리스트 헤더 클릭 정렬용 파라미터
	 * 2017-06-12 : 골프택배 - 테이블 헤더 클릭 정렬 처리용  
	 * @return
	 */
	public String getHo() {
		return ho;
	}

	/**
	 * 리스트 헤더 클릭 정렬용 파라미터
	 * 2017-06-12 : 골프택배 - 테이블 헤더 클릭 정렬 처리용  
	 * @param ho
	 */
	public void setHo(String ho) {
		this.ho = ho;
	}
	
	
	/**
	 * order by 문자열 - 기본값 = ""
	 * 주의 : searchVO 혼용으로 인해, 원하지 않는 정렬 결과가 나올 수 있음.
	 * @return
	 */
	public String getOrderBy() {
		if (this.orderBy == null)
			this.orderBy = "";
		return orderBy;
	}

	/**
	 * order by 문자열
	 * 주의 : searchVO 혼용으로 인해, 원하지 않는 정렬 결과가 나올 수 있음.
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * [읽기전용] orderBy 문자열을 배열로 변환 - Mybatis iterator용으로 사용 문자열이 없을 경우 null 반환
	 * @return
	 */
	public String[] getOrderBys() {
		if (StringUtils.isEmpty(this.orderBy))
			return null;

		String[] arr = this.orderBy.split(",");
		return arr;
	}

	/**
	 * 공통 코드 마스터 그룹 코드
	 * 
	 * @return
	 */
	public String getGroupCode() {
		if (this.groupCode == null)
			this.groupCode = "";

		return this.groupCode;
	}

	/**
	 * 공통 코드 마스터 그룹 코드
	 * 
	 * @return
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	
	/**
	 * 검색 날짜 시작일 yyyy-mm-dd - 기본값 = ""
	 * 
	 * @return
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * 검색 날짜 시작일 yyyy-mm-dd
	 * 
	 * @return
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * 검색 날짜 종료일 yyyy-mm-dd
	 * 
	 * @return
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * 검색 날짜 종료일 yyyy-mm-dd
	 * 
	 * @return
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 *
	 * @return
	 */
	public String getToday() {
		if (this.today == null)
			this.today = "";
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

	/**
	 * 검색조건1 - 기본값 = ""
	 * 
	 * @return
	 */
	public String getType1() {
		return type1;
	}

	/**
	 * 검색조건1
	 * 
	 * @return
	 */
	public void setType1(String type1) {
		this.type1 = type1;
	}

	/**
	 * 검색조건2 - 기본값 = ""
	 * 
	 * @return
	 */
	public String getType2() {
		return type2;
	}

	/**
	 * 검색조건2
	 * 
	 * @return
	 */
	public void setType2(String type2) {
		this.type2 = type2;
	}
	
	public String getType3() {
		return type3;
	}

	public void setType3(String type3) {
		this.type3 = type3;
	}

	public String getType4() {
		return type4;
	}

	public void setType4(String type4) {
		this.type4 = type4;
	}
	
	public String getType5() {
		return type5;
	}

	public void setType5(String type5) {
		this.type5 = type5;
	}

	public String getPageType() {
		if (this.pageType == null)
			this.pageType = "";
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

		
	/**
	 * 로그인시 리다이렉트URL 파라미터
	 * @return
	 */
	public String getRedirectURL() {
		return redirectURL;
	}

	/**
	 * 로그인시 리다이렉트URL 파라미터
	 * @param redirectURL
	 */
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	/**
	 * 날짜 표시 형식
	 * @return
	 */
	public String getDateFormat() {
		return SIMPLE_DATE_FORMAT.toPattern();
	}
	
	/**
	 * 시각 표시 형식
	 * @return
	 */
	public String getTimeFormat() {
		return SIMPLE_TIME_FORMAT.toPattern();
	}
	
	/**
	 * 날짜 시각 표시 형식
	 * @return
	 */
	public String getDateTimeFormat() {
		return SIMPLE_DATETIME_FORMAT.toPattern();
	}

	/**
	 * 등록/수정 상태 : i/u/d ~
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 등록/수정 상태 : i/u/d ~
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * 등록/수정 상태 이름
	 * @return
	 */
	public String getStatusName() {
		return statusName;
	}

	/**
	 * 등록/수정 상태 이름
	 * @param statusName
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	/**
	 * Date 검색용
	 * @return
	 */
	public Date getSearchDate() {
		return searchDate;
	}

	/**
	 * Date 검색용
	 * @param searchDate
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 테이블 명 - 관련 첨부파일, 관련 메모 셀렉트용
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 테이블 명 - 관련 첨부파일, 관련 메모 셀렉트용
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 참조 순번 - 관련 첨부파일, 관련 메모 셀렉트용
	 * @return
	 */
	public String getRefSeq() {
		return refSeq;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 참조 순번 - 관련 첨부파일, 관련 메모 셀렉트용
	 * @param refSeq
	 */
	public void setRefSeq(String refSeq) {
		this.refSeq = refSeq;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 PK이름
	 * @return
	 */
	public String getPkName() {
		return pkName;
	}

	/**
	 * 파일 업로드 모듈용 - 참조용 PK이름
	 * @param pkName
	 */
	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	/**
	 * 파일 업로드 모듈용 - KDE_FILE.FILE_TYPE 필드
	 * @return
	 */
	public String getFileType() {
		return fileType;
	}

	/**
	 * 파일 업로드 모듈용 - KDE_FILE.FILE_TYPE 필드
	 * @param fileType
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	
	/**
	 * 파일 업로드 모듈용 - 필수 업로드, 선택 업로드 파일 추가 구분자
	 *  - Y : 누적 업로드 (신규 등록)
	 *  - N : 기본값 : 업데이트 (기존 파일 덮어쓰기)
	 * @return
	 */
	public String getAddOnly() {
		return addOnly;
	}

	/**
	 * * 파일 업로드 모듈용 - 필수 업로드, 선택 업로드 파일 추가 구분자
	 *  - Y : 누적 업로드 (신규 등록)
	 *  - N : 기본값 : 업데이트 (기존 파일 덮어쓰기)
	 * @param addOnly
	 */
	public void setAddOnly(String addOnly) {
		this.addOnly = addOnly;
	}
	
	/**
	 * 파일 업로드 모듈용 - 업로드 UI 구성용 PK 
	 * @return
	 */
	public String getPkSeq() {
		return pkSeq;
	}

	/**
	 * 파일 업로드 모듈용 - 업로드 UI 구성용 PK 
	 * @param pkSeq
	 */
	public void setPkSeq(String pkSeq) {
		this.pkSeq = pkSeq;
	}
	
	/**
	 * 쿼리 in 구문용 파라미터
	 * @return
	 */
	public List<String> getFileTypes() {
		if (this.fileTypes == null)
			this.fileTypes = new ArrayList<String>();
		return fileTypes;
	}

	/**
	 * 쿼리 in 구문용 파라미터
	 * @param fileTypes
	 */
	public void setFileTypes(List<String> fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	/**
	 * 탭 유지용 파라미터
	 * @return
	 */
	public String getTab() {
		return tab;
	}

	/**
	 * 탭 유지용 파라미터
	 * @param tab
	 */
	public void setTab(String tab) {
		this.tab = tab;
	}

	/**
	 * PDF 다운로드 타입 - download: 다운로드, "": 저장
	 * @return
	 */
	public String getPdt() {
		return pdt;
	}

	/**
	 * PDF 다운로드 타입 - download: 다운로드, "": 저장
	 * @param pdt
	 */
	public void setPdt(String pdt) {
		this.pdt = pdt;
	}
	
	/**
	 * 영업소코드 검색용 파라미터
	 * @return
	 */
	public String getOcd() {
		return ocd;
	}

	/**
	 * 영업소코드 검색용 파라미터
	 * @param ocd
	 */
	public void setOcd(String ocd) {
		this.ocd = ocd;
	}

	/**
	 * 지점코드 검색용 파라미터
	 * @return
	 */
	public String getBcd() {
		return bcd;
	}

	/**
	 * 지점코드 검색용 파라미터
	 * @param bcd
	 */
	public void setBcd(String bcd) {
		this.bcd = bcd;
	}

	public String getType6() {
		return type6;
	}

	public void setType6(String type6) {
		this.type6 = type6;
	}

	public String getType7() {
		return type7;
	}

	public void setType7(String type7) {
		this.type7 = type7;
	}
	
	/**
	 * iframe 여부 [리턴페이지 경로 설정]
	 * @return
	 */
	public String getParentYn() {
		return parentYn;
	}

	/**
	 * iframe 여부 [리턴페이지 경로 설정]
	 * @param parentYn
	 */
	public void setParentYn(String parentYn) {
		this.parentYn = parentYn;
	}
	
}
