<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
/**
	server attribute에 search 객체를 추가해야 함.
	하단 goPage() 메서드 있음 : jquery.locator.js 필요
	test : <div>startPage : ${search.paging.startPage }, endPage : ${search.paging.endPage }, totalCount : ${search.paging.totalCount }</div>

	<div class="pagenum">
		<span>
			<a href="" class="btn_prev2"></a>
			<a href="" class="btn_prev1"></a>
		</span>
		<span class="mL3">
			<a href="" >1</a>
			<a href="">2</a>
			<a href="" class="on">3</a>
			<a href="">4</a>
			<a href="">5</a>
			<a href="">6</a>
			<a href="">7</a>
			<a href="">8</a>
			<a href="">9</a>
			<a href="">10</a>
		</span>
		<span class="mL3">
			<a href="" class="btn_next1"></a>
			<a href="" class="btn_next2"></a>
		</span>
	</div>
*/
--%>
<c:if test="${search.paging.totalCount > 0}">
	<div class="${empty search.paging.pagerClassName ? 'page_navi_set' : search.paging.pagerClassName}">

		<span>
	<c:if test="${search.paging.pageIndex > 10 }">
			<a href="javascript:goPage(1);" class="btn_prev2"></a>
	</c:if>
	<c:if test="${search.paging.currentBlockIndex > 1}">
			<a href="javascript:goPage(${search.paging.blockCount * (search.paging.currentBlockIndex - 1) });" class="btn_prev1"></a>
	</c:if>		</span>

		<span class="mL3">
	<c:forEach begin="${search.paging.startPage}" end="${search.paging.endPage}" var="page" varStatus="s">
		<c:choose>
			<c:when test="${page eq search.paging.pageIndex }">
						<a href="javascript:;" class="on">${page }</a>
			</c:when>
			<c:otherwise>
						<a href="javascript:goPage(${page });">${page }</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>
		</span>

		<span class="mL3">

	<c:if test="${search.paging.totalPageCount gt search.paging.endPage }">
			<a href="javascript:goPage(${search.paging.endPage + 1});" class="btn_next1"></a>
	</c:if>
	<c:if test="${search.paging.pageIndex lt search.paging.totalPageCount && search.paging.totalPageCount > 10 }">
			<a href="javascript:goPage(${search.paging.totalPageCount });" class="btn_next2"></a>
	</c:if>
		</span>

	</div>
	<script type="text/javascript">
		function goPage(index) {
			$.Nav('go', location.pathname, {'cpage': index});
		}
	</script>
</c:if>