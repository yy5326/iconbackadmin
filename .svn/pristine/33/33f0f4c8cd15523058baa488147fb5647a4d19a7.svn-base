package com.golf.model.common;

import java.io.Serializable;

import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

/**
 * 페이징 모델<br> 
 * egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo 상속
 * 
 * @author wimy
 *
 */
public class PagingVO extends PaginationInfo implements Serializable {

	private static final long serialVersionUID = -6375901820271055337L;

	public PagingVO() {
		setTotalCount(0);
		setRecordCount(10);
		setBlockCount(10);
		setPageIndex(1);
		
		//골프 - 관리자 페이징 클래스
		setPagerClassName("pagenum");		
	}
	
	/**
	 * 가상번호
	 */
	private int virtualNumber = -1;

	/**
	 * 페이저 유형
	 */
	private String pageType = "front";

	/**
	 * 페이저 클래스 이름
	 */
	private String pagerClassName = "";

	// private String firstButton; // 처음 버튼
	// private String prevBlockButton; // 이전 * pageIndex 버튼
	// private String lastButton; // 마지막 버튼
	// private String nextBlockButton; // 다음 * pageIndex 버튼
	// private String onIndexClass; // 현재 페이지인 경우
	// private String offIndexClass;// 현재 페이지가 아닌 경우

	/**
	 * 페이저 클래스 이름
	 * 
	 * @return
	 */
	public String getPagerClassName() {
		return this.pagerClassName;
	}

	public void setPagerClassName(String className) {
		this.pagerClassName = className;
	}

	/**
	 * 전체 레코드 수 
	 * alias of PaginationInfo.totalRecordCount
	 * @return 
	 */
	public int getTotalCount() {
		return super.getTotalRecordCount();
	}

	/**
	 * 전체 레코드 수 
	 * alias of PaginationInfo.totalRecordCount
	 * @param totalCount
	 */
	public void setTotalCount(int totalCount) {
		super.setTotalRecordCount(totalCount);
	}

	/**
	 * 한 페이지에 보이는 레코드 수
	 * alias of PaginationInfo.recordCountPerPage
	 * @return 
	 */
	public int getRecordCount() {
		return super.getRecordCountPerPage();
	}

	/**
	 * 한 페이지에 보이는 레코드 수
	 * alias of PaginationInfo.recordCountPerPage
	 * 
	 * @param recordCount
	 */
	public void setRecordCount(int recordCount) {
		super.setRecordCountPerPage(recordCount);
	}

	
	/**
	 * 페이저 표시 갯수
	 * alias of PaginationInfo.pageSize
	 * @return 
	 */
	public int getBlockCount() {
		return super.getPageSize();
	}

	/**
	 * 페이저 표시 갯수
	 * alias of PaginationInfo.pageSize
	 * @param blockCount
	 */
	public void setBlockCount(int blockCount) {
		super.setPageSize(blockCount);
	}

	/**
	 * 현재 페이지 인덱스
	 * alias of PaginationInfo.currentPageNo
	 * @return
	 */
	public int getPageIndex() {
		return super.getCurrentPageNo();
	}

	
	/**
	 * 현재 페이지 인덱스
	 * alias of PaginationInfo.currentPageNo
	 * @param pageIndex
	 */
	public void setPageIndex(int pageIndex) {
		super.setCurrentPageNo(pageIndex);
	}

	/**
	 * 현재 페이지의 첫 레코드 번호
	 * @return
	 */
	public int getStartRow() {
		return this.getTotalCount()
				- (this.getRecordCount() * (this.getPageIndex() - 1));
	}

	/**
	 * 현재 페이지의 끝 레코드 번호
	 * @return
	 */
	public int getEndRow() {

		int iRowNum = this.getTotalCount()
				- (this.getRecordCount() * (this.getPageIndex() - 1))
				- this.getRecordCount() + 1;

		if (iRowNum < 1)
			iRowNum = 1;

		return iRowNum;
	}

	/**
	 * 가상번호 : getStartRow() 값을 외부에서 변형해가며 사용할 수 있도록 하기 위한 변수
	 *        - 사용처 : 게시물 목록 내 번호 표시용으로 사용
	 * @return
	 */
	public int getVirtualNumber() {
		if (this.virtualNumber < 0)
			this.virtualNumber = this.getStartRow();
		return this.virtualNumber;
	}

	/**
	 * 가상번호 : getStartRow() 값을 외부에서 변형해가며 사용할 수 있도록 하기 위한 변수
	 *        - 사용처 : 게시물 목록 내 번호 표시용으로 사용
	 * @return
	 */
	public void setVirtualNumber(int num) {
		this.virtualNumber = num;
	}

	/**
	 * 총 페이지 수
	 * alias of PaginationInfo.totalPageCount
	 * @return
	 */
	public int getTotalPageCount() {

//		if (this.totalPageCount < 0) {
//			setTotalPageCount();
//		}
//
//		return this.totalPageCount;
		return super.getTotalPageCount();
	}

//	/**
//	 * 총 페이지 수
//	 */
//	public void setTotalPageCount() {
//
//		this.totalPageCount = this.getTotalCount() / this.getRecordCount();
//		if ((this.getTotalCount() % this.getRecordCount()) > 0)
//			this.totalPageCount++;
//		
//	}

	/**
	 * 현재 페이지 인덱스에 대한 페이저 블럭 인덱스 값
	 * 
	 * @return
	 */
	public int getCurrentBlockIndex() {
		int currBlock = 1;
		for (int i = 1; i <= this.getTotalPageCount(); i++) {
			if (this.getPageIndex() <= (this.getBlockCount() * i)) {
				currBlock = i;
				break;
			}
		}

		return currBlock;
	}

	/**
	 * 현재 페이지 블럭 중 마지막 페이지 인덱스 값
	 * 
	 * @return
	 */
	public int getEndPage() {
		int endPage = this.getBlockCount() * this.getCurrentBlockIndex();

		if (this.getTotalPageCount() < endPage)
			endPage = this.getTotalPageCount();

		return endPage;
	}

	/**
	 * 현재 페이지 블럭 중 시작 페이지 인덱스 값
	 * 
	 * @return
	 */
	public int getStartPage() {
		int startPage = 1;
		startPage = this.getBlockCount() * this.getCurrentBlockIndex()
				- this.getBlockCount() + 1;
		if (startPage < 1)
			startPage = 1;

		return startPage;
	}

	/**
	 * 페이저 타입
	 * 
	 * @return
	 */
	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}	
}
