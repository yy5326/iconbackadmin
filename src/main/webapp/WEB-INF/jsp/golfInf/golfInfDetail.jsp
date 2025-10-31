<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>

<body class="body_bg">
	<div id="wrap">
		<div class="contents cont_layer">
			<form name="form2" id="form2" action="golfInfWrite.do" method="post">
				<div id="original">
					<div class="receiver">
						<p class="type">기본정보</p>
						<div class="tableBox">
							<table id="golf">
								<tr>
									<th>골프장 상호명</th>
									<td>${vo.golfName}</td>
								</tr>
								<tr>
									<th>연락처</th>
									<td>${fn:split(vo.golfTelnum,'-')[0]}-${fn:split(vo.golfTelnum,'-')[1]}-${fn:split(vo.golfTelnum,'-')[2]}</td>
								</tr>
								<tr>
									<th>기타 연락처</th>
									<td>${fn:split(vo.golfTelnum2,'-')[0]}-${fn:split(vo.golfTelnum2,'-')[1]}</td>
								</tr>
								<tr>
									<th>주소</th>
									<td>(${vo.zipCode})&nbsp;&nbsp;${vo.golfAddr1} &nbsp;${vo.golfAddr2}</td>
								</tr>
								<tr>
									<th>지정영업소</th>
									<td>${vo.picOffice1}</td>
								</tr>
<c:if test="${vo.picOffice2 != '' && vo.picOffice2 != null}">								
								<tr>
									<th>픽업영업소2</th>
									<td>${vo.picOffice2}</td>
								</tr>
</c:if>								

<c:if test="${vo.picOffice3 != '' && vo.picOffice3 != null}">								
								<tr>
									<th>픽업영업소3</th>
									<td>${vo.picOffice3}</td>
								</tr>
</c:if>								
							</table>
						</div>
					</div>
				</div>		
		 <input type="hidden" name = "golfSeq" id="golfSeq" value="${vo.golfSeq}"/>
		 </form>		
		<div class="receipt_layer">
			<a href="javascript:;" id="update" class="btn acen">수정</a>
			<a href="javascript:$.colorbox.close();" id="" class="btn acen">닫기</a>
		</div>
	</div>

<script type="text/javascript">
	/* 골프장관리_레이어02 */
	$(document).ready(function(){
		_initPageLoad();

		
		$("#update").click(function(){
			var golfSeq = $("#golfSeq").val();
    		var sHref = './golfInfWriteLayer.do?golfSeq='+golfSeq;
    		parent.$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1230,
    			height: 810
    		});
    	});
	});	
	
</script>
	</div>
	</body>
</html>
