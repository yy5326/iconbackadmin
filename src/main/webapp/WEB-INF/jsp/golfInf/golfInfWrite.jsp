<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>
		<div class="contents" style="margin-top: 120px;">
			<form name="form3" id = "form3" action="golfInfProc.do" method="post">
				<div id="original">
					<div class="receiver">
						<p class="type">기본정보 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
						<div class="tableBox">
							<table id="golf">
								<tr>
									<th>골프장 상호명 <span class="red">*</span></th>
									<td>
										<input type="text" name="golfName" id="golfName" class="notNull w410" data-name="골프장 상호명" value="${vo.golfName}"/>
									</td>
								</tr>
								<tr>
									<th>연락처 <span class="red">*</span></th>
									<td>
										<tt:select list="${type1}" id="phone1" name="phone1" listKey="value" listValue="text" indexValue="전체" data-name="연락처" class="notNull phone" selected="${fn:split(vo.golfTelnum,'-')[0]}" />
										 -
		        						<input name="phone2" id="phone2" type='tel' class="notNull phone onlyNumber" maxlength="4" data-name="연락처" value="${fn:split(vo.golfTelnum,'-')[1]}"/> -
		        						<input name="phone3" id="phone3" type='tel' class="notNull phone onlyNumber" maxlength="4" data-name="연락처" value="${fn:split(vo.golfTelnum,'-')[2]}"/>
		        						<input type="hidden" role="phone1" name="golfTelnum" id="golfTelnum">
		        						<span class="th">기타 연락처 </span>
		        						<input name="ephone1" id="ephone1" type='tel' class="phone onlyNumber" maxlength="4"value="${fn:split(vo.golfTelnum2,'-')[0]}"/> -
		        						<input name="ephone2" id="ephone2" type='tel' class="phone onlyNumber" maxlength="4"value="${fn:split(vo.golfTelnum2,'-')[1]}"/>
		        						<input type="hidden" role="ephone1" name="golfTelnum2" id="golfTelnum2">
		        					</td>
								</tr>
								
								<tr>
									<th rowspan="3" class="address">주소 <span class="red">*</span></th>
									<td>
										<input type="text" role="zip1" name="zipCode" id="zipCode" data-name="우편번호" class="notNull" placeholder="우편번호" readonly="readonly" onclick="javascript:searchPostcode('zip1');" value="${vo.zipCode}"/>
										<a href="javascript:searchPostcode('zip1');" class="postcode btn">우편번호 검색</a>
									</td>
								</tr>
								<tr>
									<td><input type="text" role="zip1" name="golfAddr1" id="golfAddr1" data-name="기본주소" class="notNull w586" placeholder="기본주소" readonly="readonly" onclick="javascript:searchPostcode('zip1');" value="${vo.golfAddr1}"/>
										<input type="hidden" name="list[0].sendIslYn"/>
									</td>
								</tr>
								<tr>
									<td>
										<input type="text" name="golfAddr2" id="golfAddr2" class="w586" data-name="상세주소" placeholder="상세주소를 입력해 주세요" value="${vo.golfAddr2}"/>
									</td>
								</tr>
								<tr class="office1">
									<th>픽업영업소 <span class="red">*</span></th>
									<td>
										<input type="text" role="golfSearch1" name="picOffice1"  id="office1" class="notNull" data-name="픽업영업소" value="${vo.picOffice1}" readOnly/>
										<input type="hidden" role="golfSearch1" name="picOfficeCode1" id="officeCode1" value="${vo.picOfficeCode1}"/>
										<a href="#" role="1" class="officeLayer btn">찾기</a>
										<span></span>
										<span>
											<!-- <a href="#" class="btn" id="picOffice">영업소추가</a> -->
										</span>
									</td>
								</tr>
								
<%-- <c:if test="${vo.picOffice2 != '' && vo.picOffice2 != null}">	
								<tr class="office2">
									<th>픽업영업소2</th>
									<td>
										<input type="text" name="picOffice2"  id="picOffice2" data-name="픽업영업소" value="${vo.picOffice2}"/>
										<input type="hidden" name="picOfficeCode2"/>
										<a href="#" class="btn">찾기</a>
										<span></span>
										<span>
											<a href="javascript:officeDel('2')" class="btn" id="picOffice">삭제</a>
										</span>
									</td>
								</tr>
</c:if>
								
<c:if test="${vo.picOffice3 != '' && vo.picOffice3 != null}">	
								<tr class="office3">
									<th>픽업영업소3</th>
									<td>
										<input type="text" name="picOffice3"  id="picOffice3" data-name="픽업영업소" value="${vo.picOffice3}"/>
										<input type="hidden" name="picOfficeCode3"/>
										<a href="#" class="btn">찾기</a>
										<span></span>
										<span>
											<a href="javascript:officeDel('3')" class="btn" id="picOffice">삭제</a>
										</span>
									</td>
								</tr>
</c:if> --%>
							</table>
						</div>
					</div>
				</div>
		 <input type="hidden" name="golfSeq" id="updateGolfSeq" value="0"/>
		 <input type="hidden" name="type2" id="type2" value="I"/>
		 </form>
		<div class="receipt receipt02">
			<a href="#" id="add" class="add btn acen">등록</a>	
		<a href="javascript:history.back();" id="detail" class="btn acen" style="margin-left: 16px;">취소</a>
		</div>	
	</div>

</body>

<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var msg = "골프장을 등록 하시겠습니까?";
		
		//영업소 추가(보류)
		$("#picOffice").click(function(){                
			var officeNo = $("#golf tr:last").attr("class").replace("office", "");

			if(officeNo>2){
				alert("픽업영업소는 최대 3개까지 가능합니다.");
				return;
			}else{
				var clone = $("#golf tr:eq(5)").clone();
				var nextNo = parseInt(officeNo)+1;
				
				//id, 제목변경
				clone.find("th:last").html("픽업영업소"+nextNo);
				clone.find("input[name=picOffice1]").attr('name','picOffice'+nextNo);
				clone.find("input[name=picOfficeCode1]").attr('name','picOfficeCode'+nextNo);
				
				//영업소 찾기 버튼 삭제
				clone.find("span:last").remove();
				
				//클래스 초기화
				clone.removeClass();
				clone.find("input[name=picOffice"+nextNo+"]").attr('class',''); //빈값체크 제외
				
				//클래스명 변경
				clone.addClass("office"+(parseInt(officeNo)+1));
				
				clone.find("span:last").append("&nbsp;<a href='javascript:officeDel("+nextNo+")' class='btn'>삭제</a>");
	            $("#golf").append(clone);	

				clone.find("input[name=picOffice"+nextNo+"]").val("");
				clone.find("input[name=picOfficeCode1"+nextNo+"]").val("");
			}
		});
		
		//저장
		$("#add").click(function(){
			var notNullChk = true;
			$("#form3").find(".notNull").each(function(index, item){
				//빈칸체크
               if ($(this).val().trim() == '') {                	
                    alert($(this).attr("data-name")+" 항목을 입력하세요.");
                    $(this).focus();
                    notNullChk = false;
                    return false;
                }
            });	
			if(notNullChk==true){
				$("#golfTelnum").val($("#phone1").val()+"-"+$("#phone2").val()+"-"+$("#phone3").val());
				$("#golfTelnum2").val($("#ephone1").val()+"-"+$("#ephone2").val());
				if(confirm(msg)){ 
					$("#form3").submit();
				}
			}
		}); 

    	//영업소 찾기
		$(".officeLayer").click(function(evt){
			evt.preventDefault();
			window.open("../common/officePopup.do?type1=A&type2="+$(this).attr('role')+"&searchWord="+$("#zipCode").val(), "officeSearch", "top=200, left=100, width=800px, height=480px");    	
		});    	
	});	
	
	//영업소 삭제(보류)
	function officeDel(officeNo){
		if(officeNo==3){
			$("#golf tr:last").remove();
		}else{
			$("input[name=picOffice2]").val($("input[name=picOffice3]").val());
			$("input[name=picOfficeCode2]").val($("input[name=picOfficeCode3]").val());
			$("#golf tr:last").remove();
		}
	}		
	
	//다음주소 입력 창 오픈
	function searchPostcode(roleName) {
		new daum.Postcode({
	        oncomplete: function(data) {

	            var fullAddr = '';
	            var extraAddr = '';

	            if (data.userSelectedType === 'R') {
	                fullAddr = data.roadAddress;

	            } else {
	                fullAddr = data.jibunAddress;
	            }

	            if(data.userSelectedType === 'R'){
	                if(data.bname !== ''){
	                    extraAddr += data.bname;
	                }
	                if(data.buildingName !== ''){
	                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
	                }
	                fullAddr += (extraAddr !== '' ? ' ('+ extraAddr +')' : '');
	            }

	            //동시 사용 처리
	            $("input[role="+roleName+"]").each(function(idx){
					if(idx == 0)
						$(this).val(data.zonecode);
					else if(idx == 1)
						$(this).val(fullAddr);
					else
						$(this).focus();
	            });	        
	         }
	    }).open();
	}
</script>
</html>
