package com.golf.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

public class PagingTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5232900811603482866L;

	private Logger logger = Logger.getLogger(PagingTag.class);

	private int currentPage;
	private int totalCount;
	private int pageSize;
	private String skin;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().println(getPage());
		} catch (IOException e) {
			logger.error(e.toString(), e);
		}

		return SKIP_BODY;
	}

	private StringBuffer getPage() {

		StringBuffer page = new StringBuffer();

		if (totalCount > 0) {
			// 관리자 일때 페이징 이미지
			String pagePrev = "/images/admin/button/btn_page_prev.gif";
			String prev = "/images/admin/button/btn_prev.gif";

			String next = "/images/admin/button/btn_next.gif";
			String pageNext = "/images/admin/button/btn_page_next.gif";

			// 프론트 일때 사용자 이미지
			if ("front".equals(skin)) {
				pagePrev = "/images/front/button/btn_page_prev.gif";
				prev = "/images/front/button/btn_prev.gif";

				next = "/images/front/button/btn_next.gif";
				pageNext = "/images/front/button/btn_page_next.gif";
			}

			int blockPage = ((currentPage - 1) / pageSize) * pageSize + 1;
			int i = 0;
			int pageCount = (totalCount - 1) / pageSize + 1;

			page.append("<table cellpadding=\"0\" cellspacing=\"0\">\n")
					.append("<tbody>\n").append("	<tr>\n");

			if (currentPage == 1) {
				page.append("<td class=\"img_wd\"><img src=\"" + pagePrev
						+ "\" alt=\"첫번재 페이지 가기\" /></td>\n");
			} else {
				page.append("<td class=\"img_wd\"><a href=\"javascript:paging(1);\" class=\"img\"><img src=\""
						+ pagePrev + "\" alt=\"첫번재 페이지 가기\" /></a></td>\n");
			}

			/********** 이전 10 개 구문 시작 **********/
			if (blockPage == 1) {
				page.append("<td class=\"img_wd\"><img src=\"" + prev
						+ "\" alt=\"이전 10개 이동\" /></td>\n");
			} else {
				page.append("<td class=\"img_wd\"><a href=\"javascript:paging("
						+ (blockPage - 10) + ");\"><img src=\"" + prev
						+ "\" alt=\"이전 10개 이동\" /></a></td>\n");
			}

			if (currentPage != 0) {
				i = 1;
				while (i <= 10 && blockPage <= pageCount) {
					if (blockPage == currentPage) {
						page.append("<td><span><a href=\"#\" class=\"on\">"
								+ blockPage + "</a></span></td>\n");
					} else {
						page.append("<td><span><a href=\"javascript:paging("
								+ blockPage + ");\">" + blockPage
								+ "</a></span></td>\n");
					}
					blockPage = blockPage + 1;
					i = i + 1;
				}
			}

			/********** 다음 10 개 구문 시작 **********/
			if (currentPage != 0) {
				if (blockPage > pageCount) {
					page.append("<td class=\"img_wd\"><img src=\"" + next
							+ "\" alt=\"다음 10개 이동\" /></td>\n");
				} else {
					page.append("<td class=\"img_wd\"><a href=\"javascript:paging("
							+ (blockPage - 10)
							+ ");\" class=\"img\"><img src=\""
							+ next
							+ "\" alt=\"다음 10개 이동\" /></a></td>\n");
				}
			}

			/********** 마지막 페이지로 이동 **********/
			if (currentPage == pageCount) {
				page.append("<td class=\"img_wd\"><img src=\"" + pageNext
						+ "\" alt=\"마지막 페이지 가기\" /></td>\n");
			} else {
				page.append("<td class=\"img_wd\"><a href=\"javascript:paging("
						+ pageCount + ");\" class=\"img\"><img src=\""
						+ pageNext + "\" alt=\"마지막 페이지 가기\" /></a></td>\n");
			}

			page.append("</tr>\n").append("</tbody>\n").append("</table>\n");
		}

		return page;
	}
}
