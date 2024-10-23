<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>


		<form action="./golfInfList.do" id ="form1" style="overflow:hidden !important;">
		<div class="contents inqueryCont">
			<div class="top clearfix">
			
				<div class="left">
					<a href="#" id="golfDeleteData">선택삭제</a>
					<a href="#" id="golfDelete">선택불가능처리</a>
				</div>
				<div class="right">
					<tt:select list="${type1 }" id="type1" name="type1" listKey="value" listValue="text" selected="${search.type1 }" />
					<input type="text" class="searchWord" name="searchWord" placeholder="검색어" value="${search.searchWord}" />
					<a onclick="javascript:doSearch();" id="btnSearch" class="search_btn">검색</a>
										
					<select name="recordCount" id="recordCount">
						<option value="10" <c:if test="${search.recordCount eq 10 }">selected</c:if>>10줄씩 보기</option>
						<option value="20" <c:if test="${search.recordCount eq 20 }">selected</c:if>>20줄씩 보기</option>
					</select>
					<a href="#" id="golfWrite" class="search_btn">등록</a>
					<a class="search_btn" href="javascript:$.Nav('excel','./to_excel.do');">엑셀저장</a>
					<!-- <a href="#" class="excel_upload">엑셀 업로드</a> -->
				</div>
			</div>
			
				<table class="inquiry">
					<colgroup>
						<col width="2%">
						<col width="11%">
						<col width="7%">
						<col width="7%">
						<col width="12%">
						<col width="*%">
						<col width="7%">
						<col width="7%">
						<col width="7%">
						<col width="7%">
						<col width="7%">
						<col width="7%">
					</colgroup>
					<thead>
						<tr>
							<th class="chk03"><input type="checkbox" id="allChk" /><label for="allChk"></label></th>
							<th>골프장명</th>
							<th>연락처</th>
							<th>기타연락처</th>
							<th>지정영업소</th>
							<th>주소</th>
							<th>기본(편도)</th>
							<th>스코어(편도)</th>
							<th>특판(편도)</th>
							<th>기본(왕복)</th>
							<th>스코어(왕복)</th>
							<th>특판(왕복)</th>
							<th>지정여부</th>
						</tr>
					</thead>
					<tbody>
<c:if test="${!empty golfInfList}">
	<c:forEach var="m" items="${golfInfList}" varStatus="ss">
						<tr class="golfInfView"  style="cursor:pointer" id="${m.golfSeq}">
							<td class="chk03" onclick="event.cancelBubble=true"><input type="checkbox" id="chk01[${ss.index }]" name="seqs" value="${m.golfSeq}"/><label for="chk01[${ss.index }]"></label></td>
							<td>${m.golfName }</td>
							<td>${fn:split(m.golfTelnum,'-')[0]}-${fn:split(m.golfTelnum,'-')[1]}-${fn:split(m.golfTelnum,'-')[2]}</td>
							<td>${fn:split(m.golfTelnum2,'-')[0]}-${fn:split(m.golfTelnum2,'-')[1]}</td>
							<td>${m.picOffice1 }</td>
							<%-- <td>${m.picOffice2 }</td>
							<td>${m.picOffice3 }</td> --%>
							<td>${m.golfAddr1}&nbsp;${m.golfAddr2}</td>
							<td><fmt:formatNumber value="${m.fee1}" pattern="#,###" /></td>
							<td><fmt:formatNumber value="${m.fee2}" pattern="#,###" /></td>
							<td><fmt:formatNumber value="${m.fee3}" pattern="#,###" /></td>
							<td><fmt:formatNumber value="${m.fee4}" pattern="#,###" /></td>
							<td><fmt:formatNumber value="${m.fee5}" pattern="#,###" /></td>
							<td><fmt:formatNumber value="${m.fee6}" pattern="#,###" /></td>
							<td>${m.useYnNm}</td>
						</tr>
	</c:forEach>	
</c:if>		
<c:if test="${empty golfInfList}">
						<tr>
							<td colspan="12"><s:message code="common.noData" /></td>
						</tr>
</c:if>			
					</tbody>
				</table>
				<%@ include file="/WEB-INF/jsp/common/inc-paging-admin.jsp"%>
		</div>
		<input type="hidden" name = "golfSeq" id = "golfSeq" value="0"/>
		</form>
</body>		
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#golfDelete").click(function(){
			var chk = "N";
			$("input[name=seqs]").each(function() {
				if ($(this).is(':checked')) {
					chk = "Y";
				}
			});

			if(chk=="N"){
				alert("불가능처리 할 골프장을 선택해주세요.");
				return;
			}else{
				if(confirm("선택한 골프장을 불가능처리 하시겠습니까?")){
					$("#form1").attr("action", "golfInfDelete.do");
					$("#form1").submit();
				}				
			}
		});
		
		$("#golfDeleteData").click(function(){
			var chk = "N";
			$("input[name=seqs]").each(function() {
				if ($(this).is(':checked')) {
					chk = "Y";
				}
			});

			if(chk=="N"){
				alert("삭제 할 골프장을 선택해주세요.");
				return;
			}else{
				if(confirm("선택한 골프장을 삭제 하시겠습니까?")){
					$("#form1").attr("action", "golfInfDeleteData.do");
					$("#form1").submit();
				}				
			}
		});
		
		$("#allChk").click(function(){
			if($("#allChk").prop("checked")) { 
				$("input[type=checkbox]").prop("checked",true); 
			} else { 
				$("input[type=checkbox]").prop("checked",false); 
			}

		});
				
		$('select[name=recordCount]').change(function() {
			$("#form1").submit();		
		});
		
		//영업소 상세정보(골프장관리 레이어01)
    	$(".golfInfView").click(function(e){	
			var golfSeq = $(this).attr('id');
			$("#golfSeq").val(golfSeq);
    		var sHref = './golfInfDetail.do?golfSeq='+golfSeq;
    		parent.$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1200,
    			height: 595,
    		});
    	});
		
		//영업소 상세정보(골프장관리 레이어01)
    	$("#golfWrite").click(function(e){
    		var sHref = './golfInfWriteLayer.do';
    		parent.$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1200,
    			height: 780,
    		});
    	});
		
		/* 골프장 파일 업로드 삭제예정*/
    	$(".excel_upload").click(function(e){	
    		var sHref = './golfInfExcelLayer.do';
    		parent.$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 500,
    			height: 250
    		});
    	});
	});
	function doSearch(){
		$("#form1").submit();		
	}
</script>
</html>