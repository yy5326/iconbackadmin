<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>

	<form action="./receiptList.do" id="form71" style="overflow:hidden !important;">
		<input type="hidden" name="chkNum" id="chkNum" value=""/>
		<div class="contents inqueryCont">

			<div class="top_btn clearfix">
				<div class="left">
					<!-- <a href="#" class="search_btn" style="margin-left: 0;">입금대기</a> -->
					<!-- <a href="#" class="search_btn" style="margin-left: 0;">결제취소</a> -->
					<a href="javascript:;" onclick="javascript:setGolfDeliveryCancel();" class="search_btn" class="chk" style="margin-left: 0;">선택취소</a>
					<a href="javascript:setGolfDeliveryPayment('완료');" class="search_btn">결제완료</a>
					<a href="javascript:setGolfDeliveryPayment('취소');" class="search_btn">결제취소</a>
					<a href="javascript:setGolfDelivery();" class="search_btn">픽업요청</a>
					<a href="javascript:setGolfWait();" class="search_btn">골프접수</a>
				</div>
				<div class="right">					
					<a class="search_btn" href="javascript:$.Nav('excel','./receiptListExcel.do');">엑셀저장</a>
				</div>
			</div>

			<div class="top clearfix">
				<div class="left">
					<tt:select style="margin-left:0px;" list="${type6 }" id="type6" name="type6" listKey="value" listValue="text" indexKey="" indexValue="등록구분" selected="${search.type6 }" />
					<tt:select style="margin-left:0px;" list="${type1 }" id="type1" name="type1" listKey="value" listValue="text" indexKey="" indexValue="접수상태" selected="${search.type1 }" />
					<tt:select style="margin-left:0px;" list="${type4 }" id="type4" name="type4" listKey="value" listValue="text" indexKey="" indexValue="구분" selected="${search.type4 }" />
					<tt:select list="${type5 }" id="type5" name="type5" listKey="value" listValue="text" indexKey="" indexValue="결제상태" selected="${search.type5 }" />
					<tt:select list="${type2 }" id="type2" name="type2" listKey="value" listValue="text" selected="${search.type2 }" />

					<input type="text" name="startDate" value="${search.startDate }" class="input datepicker" style="width:90px;" >
					<a class="calendar datepicker" style="cursor: pointer;"></a> -

					<input type="text" name="endDate" value="${search.endDate }" class="input datepicker" style="width:90px;" >
					<a class="calendar datepicker" style="cursor: pointer;"></a>


				</div>
				<div class="right">
					<tt:select list="${type3 }" id="type3" name="type3" listKey="value" listValue="text" indexKey="" indexValue="검색조건" selected="${search.type3 }" />

					<input type="text" class="search_word searchWord" name="searchWord" placeholder="검색어" value="${search.searchWord }" />
					<a onclick="javascript:doSearch()" class="search_btn" id="btnSearch">검색</a>
					<a onclick="javascript:doReset()" class="search_btn">검색초기화</a>

					<select id="recordCount" name="recordCount">
						<option value="10" <c:if test="${search.recordCount eq 10 }">selected</c:if>>10줄씩 보기</option>
						<option value="20" <c:if test="${search.recordCount eq 20 }">selected</c:if>>20줄씩 보기</option>
					</select>
				</div>
			</div>


				<!-- <div style="width:100%; height:575px; border-top:2px solid #000; border-left:1px solid #ddd;" id="myGrid" class="inquiry"></div> -->
			<div style="width: 100%; overflow-x: auto;">
				<table class="inquiry" style="white-space: nowrap; border-collapse: collapse;">
					<colgroup>
						<col width="2.5%">
						<col width="4%">
						<col width="4%">
						<col width="4%">
						<col width="3.5%">
						<col width="5%">
						<col width="8%">
						<col width="*">
						<col width="4.5%">
						<col width="5%">
						<col width="6%">
						<col width="8%">
						<col width="8%">
						<col width="8%">
						<col width="5%">
						<col width="5%">
						<col width="6%">
						<col width="6%">
						<col width="6%">
					</colgroup>
					<thead>
						<tr>
							<!-- 항목 수정 시 상단 colgroup 및 하단 colspan 함께 수정 바람!!! -->
							<th class="chk03"><span id="allChkView"><input type="checkbox" id="allChk" /><label for="allChk"></label></span></th>
							<th>업체구분</th>
							<th>상품구분</th>
							<th>접수상태</th>
							<th>구분</th>
							<th>그룹번호</th>
							<th>픽업희망일</th>
							<th>골프 접수번호</th>
							<th>결제상태</th>
							<th>택배접수일</th>							
							<th>운송장번호</th>
							<th id="startBr">발송 영업소</th> <input type="hidden" value="" name="type7" id="brSorting">
							<th id="endBr">도착 영업소</th>
							<th>외부업체</th>
							<th>접수자</th>
							<th>보내는 분</th>
							<th>받는 분</th>
							<th>도착골프장</th>
							<th>운임</th>
							<!-- 항목 수정 시 상단 colgroup 및 하단 colspan 함께 수정 바람!!! -->
						</tr>
					</thead>
					<tbody>

<c:set var="delKind" value="N"/>						
<c:forEach var="m" items="${list}" varStatus="ss">	
						<tr class="detail" id="${m.rcpInfSeq }" <c:if test="${m.rcpInfStat eq '3'}">style="background: #fff9d5;"</c:if>>
							<td class="chk03" id="${ss.index }">
								<input type="checkbox" id="chk01[${ss.index }]" class="golfChk" value="${m.rcpInfSeq }"/> 
								<label for="chk01[${ss.index }]"></label>
								<input type="hidden" id="rcpKindName" value="${m.rcpKindName}"/>
								<input type="hidden" id="delKind" name = "delKind" value="${m.delKind}"/>
								<input type="hidden" id="rcpInfStat" name="rcpInfStatChk" value="${m.rcpInfStat}"/>
								<input type="hidden" id="payStat" name="payStatChk" value="${m.payStat}"/>
								<input type="hidden" id="grpSeq" name="grpSeq" value="GRP${m.rcpSeq }"/>
								<input type="hidden" id="rcpSeq" name="rcpSeq" value="${m.rcpSeq }"/>
								<input type="hidden" id="rcpKind" name="rcpKind" value="${m.rcpKind }"/>
								
							</td>
							<td style="margin-left: 0;">${m.rcpKindName}</td>
							<td style="margin-left: 0;">${m.prdTypeName}</td>
							<td style="margin-left: 0;">${m.rcpInfStatName}</td>
							<td>
	<c:if test="${m.delKind eq '1' }">
								편도
	</c:if>
	<c:if test="${m.delKind eq '2' }">
								${m.srTypeName }
	</c:if>
							</td>	
			
<c:set var ="rowSpan" value = "1"/>
<c:if test = "${list[ss.index + 1].rcpSeq == m.rcpSeq }">
		<c:set var ="rowSpan" value = "2"/>
</c:if>		
						
<c:choose>							
	<c:when test="${m.delKind eq '2' && m.srType eq 'R'  && rowSpan == 2}">
	<!-- 왕복 1,2차 모두 표시되는 경우 -->
							<td rowspan="${rowSpan}">
								GRP${m.rcpSeq }
							</td>
	</c:when>
	
	<c:when test="${m.delKind eq '2' && rowSpan == 1 && list[ss.index - 1].rcpSeq != m.rcpSeq}">
	<!-- 이전 그룹번호와 같지 않고 왕복 1,2차 중 한개만 표시되는 경우 -->
								<td>
									GRP${m.rcpSeq }
								</td>
	</c:when>
							
	<c:otherwise>	
		<!-- 편도는 그룹번호 표시 안함 -->
		<c:if test="${m.delKind eq '1'}">
			<td></td>			
		</c:if>	
	</c:otherwise>		
</c:choose>							
							
							<td>${fn:substring(m.picReqDt, 2, 10) }</td>	
							<td>${m.rcpNumber } 
								<input type="hidden" name="rcpNumber" id="rcpNumber" value="${m.rcpNumber}"/>
							</td>
							<td>${m.payStatName }</td>
							<td>${fn:substring(m.pdRecDt, 2, 10) }</td>
							<td>
								${m.pdBarcode }
<!-- 인수완료시간 -->
<%-- <c:if test="${m.scTime ne ''}">
								${fn:substring(m.scTime, 2, 16) }
</c:if> --%>
							</td>
							<td>${m.brStrNm }</td>
							<td>${m.brEndNm }<input type ="hidden" name="cancelBrEndNm" id="${m.brEndNm}"/></td>
							<td>${m.extComp }</td>
							<td>${m.cusReceiptNm }</td>
							<td>${m.cusSendNm }</td>
							<td>${m.cusReNm }</td>
							<td>
<c:choose>
	<c:when test="${fn:length(m.recvGolfName) gt 10}">
		<c:out value="${fn:substring(m.recvGolfName, 0, 10)}"></c:out>...
	</c:when>
	<c:otherwise>
        <c:out value="${m.recvGolfName}">
	</c:out></c:otherwise>
</c:choose>
							<td><fmt:formatNumber value="${m.pdPay }" /></td>
						</tr>
</c:forEach>
<c:if test="${empty list }">
						<tr>
							<td colspan="18" class="chk03" style="height: 118px;"><s:message code="common.noData" /></td>
						</tr>
</c:if>

					</tbody>
				</table>
			</div>
			<%@ include file="/WEB-INF/jsp/common/inc-paging-admin.jsp"%>
		</div>
	</form>
	<div class="fare" <c:if test="${empty list }">style="margin-top: 50px;"</c:if>>
		<div>
			<ul>
				<li>&middot&nbsp;&nbsp;배송운임<span class="fee"><fmt:formatNumber value="${statsMap.PD_DELI_PAY_SUM eq null ? 0 : statsMap.PD_DELI_PAY_SUM }" />&nbsp;&nbsp;원</span></li>
				<li>&middot&nbsp;&nbsp;할증운임<span class="fee"><fmt:formatNumber value="${statsMap.PD_ETC_PAY_SUM eq null ? 0 : statsMap.PD_ETC_PAY_SUM }" />&nbsp;&nbsp;원</span></li>
				<li>&middot&nbsp;&nbsp;도서운임<span class="fee"><fmt:formatNumber value="${statsMap.PD_DOSUN_PAY_SUM eq null ? 0 : statsMap.PD_DOSUN_PAY_SUM }" />&nbsp;&nbsp;원</span></li>
				<li>&middot&nbsp;&nbsp;총운임<span class="fee"><fmt:formatNumber value="${statsMap.PD_PAY_SUM eq null ? 0 : statsMap.PD_PAY_SUM }" />&nbsp;&nbsp;원</span></li>
			</ul>
		</div>
	</div>

	<!-- 선택취소 레이어 -->
		<div class="dim-layer" style="display: none;">
			<div class="dimBg"></div>

			<div id="indivPrice" class="pop-layer pop-layerCancle">
				<div class="pop-container">
					<div class="pop-conts">
						<!-- content -->
						<div class="layerT">
							<p class="ctxt">선택취소</p>
							<a href="javascript:;" role="cancel" class="btn-layerClose" style="right: 0;"></a>
						</div>
						<div class="layerCont layerCont01" style="border-bottom: 1px solid #000;">
							<div>
								<p class="ti">취소사유</p>
								<select name="canReasonSel" class="cancleR">
									<option value="날짜변경">날짜변경</option>
									<option value="단순변심">단순변심</option>
									<option value="기타">기타</option>
								</select>
							</div>
							<div class="detailReason" style="display: none;">
								<p class="ti">상세사유</p>
								<input type="text" class="cancleR" name="canReason"/>
							</div>
						</div>
						<div class="layer_btn layer_btn02">
							<a href="javascript:;" role="save" class="btn acen">확인</a>
							<a href="javascript:;" role="cancel" class="btn acen" style="margin-left: 5px;">취소</a>
						</div>
						<!-- content -->
					</div>
				</div>
			</div>
		</div>
		<!-- 선택취소 레이어 -->
<form name="copyForm" id="copyForm" method="post">
	<input type= "hidden" name="rcpISeq" id="rcpISeq"/>
</form>
</body>

<script type="text/javascript">
	var now = new Date();
	var year= now.getFullYear();
	var mon = (now.getMonth()+1)>9 ? ''+(now.getMonth()+1) : '0'+(now.getMonth()+1);
	var day = now.getDate()>9 ? ''+now.getDate() : '0'+now.getDate();
	var chan_val = year+mon+ day;
	var allChk = true;
	var stateChk = "";
	
	$(document).ready(function() {
		// 상세보기 가능한 영역에 손바닥 표시
		$('td').not('td.chk03').css('cursor', 'pointer');

		$('input[id^=chk01]').each(function(i,elements){
			var index = 0;
			index = $(elements).index(".golfChk");

			if(allChk==true){
				if(stateChk=="" || stateChk == $("input[name=payStatChk]").eq(index).val() + $("input[name=rcpInfStatChk]").eq(index).val()){
					stateChk = $("input[name=payStatChk]").eq(index).val() + $("input[name=rcpInfStatChk]").eq(index).val();
					allChk = true;
				}else{
					allChk = false;
					$("#allChkView").hide();
					return;
				}
			}else{
				allChk = false;
				$("#allChkView").hide();
				return;
			}
			
		});
		// 체크박스 전체선택
		$("#allChk").click(function() {
			if($("#allChk").prop("checked")) {
				$("input[type=checkbox]").prop("checked", true);
			} else {
				$("input[type=checkbox]").prop("checked", false);
			}
		});

		// 상세보기 레이어01
		$("td").not('td.chk03').click(function() {
			var rcpInfSeq = $(this).parents("tr").attr('id');
    		var sHref = './receiptDetail.do?rcpISeq=' + rcpInfSeq;
    		$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1230,
    			height: 820,
    		});
    	});


		// 셀렉트박스
		$('#type1, #type4, #type5, #type6, #recordCount').change(function () {
			doSearch();
		});

		// 선택취소 레이어 숨김
		$("[role=cancel]").click(function(){
			$(".dim-layer").hide();
		});

		// 선택취소 레이어 저장(확인버튼)
		$("[role=save]").click(function(){
			$(".dim-layer").hide();
			doUpdate();
		});

		$('select[name=canReasonSel]').change(function() {

			if ($(this).val() == '기타') {
				$('.detailReason').show();
			} else
				$('.detailReason').hide();
		});
		
		//접수상태, 결제상태가 같은 리스트만 체크
		$(".golfChk").change(function(){
			var index = 0;
			if($(".golfChk:checked").length > 0){
				$(".golfChk:checked").each(function(i,elements){
					index = $(elements).index(".golfChk");
					//처음 체크한 접수상태와 결제상태값 설정
					if($("#chkNum").val() == ""){
						$("#chkNum").val($("input[name=payStatChk]").eq(index).val() + $("input[name=rcpInfStatChk]").eq(index).val());
					}
					
					//처음 체크한 접수상태와 결제상태값과 같지 않은 경우 체크박스 자동 해제처리
					if($("#chkNum").val() != $("input[name=payStatChk]").eq(index).val() + $("input[name=rcpInfStatChk]").eq(index).val()){
						$(".golfChk").eq(index).attr("checked", false);
						alert("접수상태 또는 결제상태가 같지 않습니다.");
					}
				});								
			}else{
				$("#chkNum").val("");
			}
		});
	});

	//픽업요청, 선택취소 시 상태체크
	var getState = function(seq, target){
		var retData = {};
		$.ajax({
			type : "POST",
			async: false ,
			url : "/common/getState.do",
			data: {
				rcpInfSeq: seq,
				action: target
			},
			success : function(data) {
				retData = data;
	        },error : function(xhr, status, error) {
				alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
			}
		});

		var isCheck = false;
		if(retData != "success"){
			if(retData == "error1"){
				alert("픽업요청은 골프접수 단계에서만 가능합니다.");
			}else if(retData == "error3"){
				alert("결제완료는 골프접수 단계에서만 가능합니다.");
			}else if(retData == "error4"){
				alert("결제취소는 골프취소,픽업취소,택배취소 단계에서만 가능합니다.");
			}else if(retData == "error2"){
				alert("취소는 접수대기, 골프접수 또는 픽업요청 단계에서만 가능합니다.");
			}else if(retData == "error5"){
				alert("픽업요청은 결제가 완료되어야 가능합니다.");
			}else if(retData == "error6"){
				alert("골프접수는 접수대기 단계에서만 가능합니다.");
			}
		}else{
			isCheck = true;
		}
		return isCheck;
	}

	//결제완료 및 결제취소
	function setGolfDeliveryPayment(strType){
		var index = "";
		var rcpInfStat = "";
		var payStat = "";
		var rcpInfSeqArray  = new Array();
		var rcpNumberArray = new Array();
		var orderType = "";
		var state = true;
		var rcpKind = "";
		
		if($(".golfChk:checked").length > 0){
			$(".golfChk:checked").each(function(i,elements){
				index = $(elements).index(".golfChk");
				rcpInfStat = $("input[name=rcpInfStatChk]").eq(index).val();
				payStat = $("input[name=payStatChk]").eq(index).val();
				rcpKind = $("input[name=rcpKind]").eq(index).val();
				if(rcpKind=="2"){
					alert("스마트스코어 접수건은 결제취소가 불가능 합니다.");
					state=false;
					return false;
				}
				rcpInfSeqArray.push($(this).val());
				rcpNumberArray.push($("input[name=rcpNumber]").eq(index).val());				
			});	
			
			if(state==true){
				//결제완료
				if(strType=="완료"){
					if(payStat=="1"){
						alert("결제완료 상태입니다.");
						return;
					}
					
					if(rcpInfStat!="0"){
						alert("결제"+strType+"는 골프접수 단계에서만 가능합니다.");
						state = false;
						return;
					}
				}
				//결제취소 (4번 접수대기 추가예정)
				else if(strType=="취소"){
					if(payStat=="9"){
						alert("결제취소 상태입니다.");
						state = false;
						return;
					}
					
					if(rcpInfStat!="0" && rcpInfStat!="8" && rcpInfStat!="7" && rcpInfStat!="9" || payStat!="1"){
						alert("결제"+strType+"는 골프접수, 골프취소, 픽업취소, 택배취소 단계에서만 가능합니다.");
						state = false;
						return;
					}
					orderType = strType;
				}
			
				strType = "결제"+strType;
				$.ajaxSettings.traditional = true;
				var param = { rcpInfSeqArray: rcpInfSeqArray, rcpNumberArray : rcpNumberArray, golfUpdate : orderType};
				if(confirm(strType+" 처리하시겠습니까?")){
					$.ajax({
						type : "POST",
						async: false ,
						url : "./receiptPaymentArray.do",
						data: param,
						success : function(data) {
							if(data == "success"){
								alert(strType+"가 처리 되었습니다.");
							}else{
								alert("변경 된 결제상태가 있습니다.");
							}
							$.Nav('self','./receiptList.do?${pageContext.request.queryString }');
				        },error : function(xhr, status, error) {
							alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
						}
					});
				}
			}
		}else{
			alert("처리 대상을 선택해 주세요.");
			return;
		}
	}
	
	//픽업 요청
	function setGolfDelivery(){
		var seqArray = new Array();
		var rcpNumberArray = new Array();
		var state = true;
		
		if($(".golfChk:checked").length > 0){			
			$(".golfChk:checked").each(function(i,elements){
				
				seqArray.push($(this).val());
				rcpNumberArray.push($(elements).parent().find('#rcpNumber').val());

				//픽업요청 상태 체크
				if(state==true){
					state = getState($(this).val(), "P");
				}
			});	

			$.ajaxSettings.traditional = true;
			if(state==true){
				if(confirm("픽업요청 하시겠습니까?")){
					$.ajax({
						type : "POST",
						async: false ,
						url : "/common/setGolfDeliveryArr.do",
						data: {
							rcpInfSeqArray: seqArray
						   ,rcpNumberArray: rcpNumberArray
						},
						success : function(data) {
							if(data == "success"){
								alert("픽업요청이 완료되었습니다.");
								$.Nav('self','./receiptList.do?${pageContext.request.queryString }');
							}else{
								alert("접수번호 "+data+" 에러발생 관리자에게 문의 바랍니다.");
							}
				        },error : function(xhr, status, error) {
							alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
						}
					});
				}
			}
		}else{
			alert("픽업요청 대상을 선택해 주세요.");
			return;
		}
	}
	
	//접수대기 -> 골프접수로 변경
	function setGolfWait(){
		var rcpKindName = '';
		var state = true;
		var grpSeq = "";
		var delKind = "";
		var rcpInfSeqArray  = new Array();

		$(".golfChk:checked").each(function(i,elements){
			rcpKindName = $(elements).parent().find('#rcpKindName').val();

			if(state==true){				
				if(rcpKindName=="아이콘"){
					alert("접수대기상태가 아닙니다.");
					state = false;
				}else{
					if(grpSeq == $(elements).parent().find('#grpSeq').val()){
						grpSeq = "";
					}else{
						grpSeq = $(elements).parent().find('#grpSeq').val();
					}
					//접수상태 체크
					if(state==true){
						state = getState($(this).val(), "W");
					}
					rcpInfSeqArray.push($(this).val());
				}
			}
		});		
		
		$.ajaxSettings.traditional = true;
		if(state==true){
			if(confirm("골프접수 신청을 하시겠습니까?")){
				$.ajax({
					type : "POST",
					async: false ,
					url : "./receiptReceiptArray.do",
					data: {
						rcpInfSeqArray: rcpInfSeqArray
					},
					success : function(data) {
						if(data == "success"){
							alert("골프접수 신청이 완료되었습니다.");
							$.Nav('self','./receiptList.do?${pageContext.request.queryString }');
						}else{
							alert("접수번호 "+data+" 에러발생 관리자에게 문의 바랍니다.");
						}
			        },error : function(xhr, status, error) {
						alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
					}
				});
			}
		}		
	}
	
	//선택 취소 레이어
	function setGolfDeliveryCancel(){
		if ($("input[type=checkbox]:checked").length < 1) {
			alert("취소 대상을 선택해 주세요.");
			return;
		}

		if ($("input[type=checkbox]:checked").length > 1) {
			alert("취소는 한건만 가능합니다.");
			return;
		}

		var seq = $("input[type=checkbox]:checked").closest("tr").attr("id");
		//선택취소 상태체크
		if (!getState(seq, "D")) {
			return;
		} 
	
		if (confirm("취소처리 하시겠습니까?")) {
			if (seq != "") {

				$(".dim-layer").show();

			} else {
				alert("에러발생 관리자에게 문의 바랍니다.");
			}
		}
	}

	// 선택취소 처리
	function doUpdate() {
		var seq = $("input[type=checkbox]:checked").closest("tr").attr("id");
		var seqNum = $("input[type=checkbox]:checked").closest("td").attr("id");
		var rcpNumber = $("input[name=rcpNumber]:eq("+seqNum+")").val();
		var rcpSeq = $("input[name=rcpSeq]:eq("+seqNum+")").val();
		var canReasonSel = $('select[name=canReasonSel]').val();
		var delKind = $("input[name=delKind]:eq("+seqNum+")").val();
		var rcpKind = $("input[name=rcpKind]:eq("+seqNum+")").val();

		if (canReasonSel != '기타')
			$('input[name=canReason]').val(canReasonSel);


		$.ajax({
			type : "POST",
			async: false ,
			url : "/common/deleteGolfDelivery.do",
			data: {
				  rcpInfSeq: seq
				, rcpNumber: rcpNumber
				, rcpSeq : rcpSeq
				, delKind : delKind
				, rcpKind : rcpKind
				, canReason: $('input[name=canReason]').val()
			},
			success : function(data) {
				if(data == "success"){
					alert("취소가 완료되었습니다.");
					$.Nav('self','./receiptList.do?${pageContext.request.queryString }');
				}else if(data == "error1"){
					alert("이미 삭제된 접수번호 입니다.\n확인 부탁드립니다.");
				}else if(data == "error3"){
					alert("운송장번호가 발급되어 취소가 불가합니다.\n영업소 프로그램으로 확인 부탁드립니다.");
				}else{
					alert("에러발생 관리자에게 문의 바랍니다.");
				}
	        },error : function(xhr, status, error) {
				alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
			}
		});
	}
	
	function doSearch() {
		$('#form71').submit();
	}

	function doReset() {
		$.Nav('self', null, null, true);
	}
	
	function sortingForBranch(br){
		if(br == 'startBr'){
			$('#brSorting').val("startBr");
			
		}else{
			$('#brSorting').val("endBr");
		}
		
		doSearch();
		
	}

	$('#startBr').on('click',function(){
		sortingForBranch("startBr")
	})
	$('#endBr').on('click',function(){
		sortingForBranch("endBr")
	})
</script>
</html>