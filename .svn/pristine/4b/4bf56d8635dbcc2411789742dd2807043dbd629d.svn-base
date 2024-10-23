<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_header_html.jsp"%>

<div class="pop">
	<div class="pop_tit">영업소 찾기</div>

	<form id="formPopup">
	<div class="cons clearfix" style="width: 745px;">

			<div class="right" style="float: right;">
				<div class="pop_ser_box">
					<p>
						<input type="hidden" name="type2" id="type2" value="${search.type2}"/>
						<tt:select list="${type1}" id="type1" class="office_name" name="type1" listKey="value" listValue="text" indexKey="" selected="${search.type1}" />
						<span>
							<input id="searchWord" name="searchWord" type="text"  maxlength="20" class="search_word" value="${search.searchWord}" onkeypress="if(event.keyCode=='13'){event.preventDefault(); doSearch();}"><a href="javascript:doSearch();" class="search_btn btn_ser">검색</a>
						</span>
					</p>
				</div>
			</div>
			<iframe id="tabIframe" style="width: 745px; height: 300px;"></iframe>
		</div>
	</form>
  </div>
</html>

<script>
	//$(document).ready(function(){
	$(function(){
		loadIframe("${search.type1}", "${search.type2}", "${search.searchWord}");
	});

	function loadIframe(type1, type2, searchWord){
		var sHref = "../common/officePopupFrame.do";
		$.Nav('iframe', '#tabIframe', sHref, {type1: type1, type2: type2, searchWord: searchWord});
	}

	function doSearch(){
		loadIframe($("#type1").val(), $("#type2").val(), $("#searchWord").val());
	}

	function office(id, picOfficeNm){
		var picOfficeCode = "#officeCode"+$("#type2").val();
		var picOffice = "#office"+$("#type2").val();
		$(picOfficeCode, opener.document).val(id);
		$(picOffice, opener.document).val(picOfficeNm);
		window.close();
	}
</script>