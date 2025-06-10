<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>  <!-- 택배접수 -->
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" href="../common/css/common.css" />
	<link rel="stylesheet" href="../common/css/reset.css" />
	<link rel="stylesheet" href="../common/css/base.css" />
	<link rel="stylesheet" href="../common/css/style.css" />
	<script type="text/javascript" src="/common/js/jquery/jquery-1.11.0.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery-migrate-1.4.1.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery.easing.1.3.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery.bxslider.min.js"></script>	
<title>골프택배</title>
</head>
<body>
	<div id="wrap">
		<div class="header clearfix">
			<div class="header_acen">
				<h1><a href="#">로고</a></h1>
				<div class="gnb">
					<ul class="clearfix">
						<li><a href="#">택배접수</a></li>
						<li><a href="#">택배조회</a></li>
						<li><a href="#">정산</a></li>
						<li><a href="#">골프장관리</a></li>
					</ul>
				</div>
			</div>
		</div>
		<div class="menu">
			<a href="#" class="home">홈</a>
			<a href="#" class="">택배접수</a>
		</div>
		<div class="contents">
			<h2 class="acen">택배접수</h2>
			<div class="receiver">
				<p>보내는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
				<form action="">
					<table>
						<tr>
							<th>편도/왕복 <span class="red">*</span></th>
							<td>
								<input type="radio" id="oneWay" name="way" checked />
  								<label for="oneWay"><span>편도</span></label>
								<input type="radio" id="back" name="way" />
  								<label for="back"><span>왕복</span></label>
							</td>
						</tr>
						<tr>
							<th>이름 <span class="red">*</span></th>
							<td><input type="text" class="w410" /></td>
						</tr>
						<tr>
							<th>연락처 <span class="red">*</span></th>
							<td>
								<select name="" id="" class="phone">
									<option value="010">010</option>
									<option value="011">011</option>
									<option value="016">016</option>
									<option value="017">017</option>
									<option value="02">02</option>
									<option value="032">032</option>
									<option value="031">031</option>
								</select> -
        						<input type='tel' name='phone2' class="phone" /> -
        						<input type='tel' name='phone3' class="phone" />
        						<span class="th">기타 연락처 <span class="red">*</span></span>
								<select name="" id="" class="phone">
									<option value="010">010</option>
									<option value="011">011</option>
									<option value="016">016</option>
									<option value="017">017</option>
									<option value="02">02</option>
									<option value="032">032</option>
									<option value="031">031</option>
								</select> -
        						<input type='tel' name='phone2' class="phone" /> -
        						<input type='tel' name='phone3' class="phone" />
        					</td>
						</tr>
						<tr>
							<th rowspan="3" class="address">주소 <span class="red">*</span></th>
							<td>
								<input type="text" id="sender_postcode" name="" placeholder="우편번호" />
								<a href="javascript:searchPostcode('sender');" class="postcode btn">우편번호 검색</a>
							</td>
						</tr>
						<tr>
							<td><input type="text" id="sender_address" name="" class="w586" placeholder="기본주소" /></td>
						</tr>
						<tr>
							<td>
								<input type="text" id="sender_detailAddress" name="" class="w586" placeholder="상세주소를 입력해 주세요" />
								<input type="text" id="sender_extraAddress" name="" placeholder="참고항목" style="width: 412px;">
							</td>
						</tr>
						<tr>
							<th>발송영업소 <span class="red">*</span></th>
							<td>
								<input type="text" />
								<a href="#" class="btn">찾기</a>
							</td>
						</tr>
						<tr>
							<th>운송장번호</th>
							<td><input type="number" readonly="readonly" class="w410" /></td>
						</tr>
					</table>
				</form>
			</div>
			<div class="sender">
				<p>받는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
				<form action="">
					<table>
						<tr>
							<th>이름 <span class="red">*</span></th>
							<td><input type="text" class="w410" /></td>
						</tr>
						<tr>
							<th>연락처 <span class="red">*</span></th>
							<td>
								<select name="" id="" class="phone">
									<option value="010">010</option>
									<option value="011">011</option>
									<option value="016">016</option>
									<option value="017">017</option>
									<option value="02">02</option>
									<option value="032">032</option>
									<option value="031">031</option>
								</select> -
        						<input type='tel' name='phone2' class="phone" /> -
        						<input type='tel' name='phone3' class="phone" />
        						<span class="th">기타 연락처 <span class="red">*</span></span>
								<select name="" id="" class="phone">
									<option value="010">010</option>
									<option value="011">011</option>
									<option value="016">016</option>
									<option value="017">017</option>
									<option value="02">02</option>
									<option value="032">032</option>
									<option value="031">031</option>
								</select> -
        						<input type='tel' name='phone2' class="phone" /> -
        						<input type='tel' name='phone3' class="phone" />
        					</td>
						</tr>
						<tr>
							<th>골프장 상호 <span class="red">*</span></th>
							<td>
								<input type="text" class="w410" />
								<a href="#" class="btn">찾기</a>
							</td>
						</tr>
						<tr>
							<th rowspan="3" class="address">주소 <span class="red">*</span></th>
							<td>
								<input type="text" id="receiver_postcode" name="" placeholder="우편번호" />
								<a href="javascript:searchPostcode('receiver');" class="postcode btn">우편번호 검색</a>
							</td>
						</tr>
						<tr>
							<td><input type="text" id="receiver_address" name="" class="w586" placeholder="기본주소" /></td>
						</tr>
						<tr>
							<td>
								<input type="text" id="receiver_detailAddress" name="" class="w586" placeholder="상세주소를 입력해 주세요" />
								<input type="text" id="receiver_extraAddress" name="" placeholder="참고항목" style="width: 412px;">
							</td>
						</tr>
						<tr>
							<th>도착영업소 <span class="red">*</span></th>
							<td><input type="text" class="w410" /></td>
						</tr>
					</table>
				</form>
			</div>
			<div class="product_info">
				<p>물품정보 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
				<form action="">
					<table>
						<tr>
							<th>경기일 <span class="red">*</span></th>
							<td>
								<select name="date" id="" class="w265">
									<option>2020-03-24</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>픽업희망일 <span class="red">*</span></th>
							<td>
								<select name="date" id="" class="w265">
									<option>2020-03-22</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>품목명 <span class="red">*</span></th>
							<td>
								<select name="date" id="" class="w265">
									<option>골프백</option>
									<option>보스턴백</option>
									<option>기타</option>
								</select>
								<input type="text" class="etc w265" style="display: none; margin-left: 5px;" />
							</td>
						</tr>
						<tr>
							<th>포장상태 <span class="red">*</span></th>
							<td><input type="text" class="w410" /></td>
						</tr>
						<tr>
							<th>추가상품 <span class="red">*</span></th>
							<td>
								<input type="radio" id="bag" name="product" checked />
  								<label for="bag"><span>비닐백</span></label>
								<input type="radio" id="no" name="product" />
  								<label for="no"><span>선택안함</span></label>
							</td>
						</tr>
						<tr>
							<th>배송구분 <span class="red">*</span></th>
							<td>
								<select name="date" id="" class="w265">
									<option>현불택배</option>
									<option>착불택배</option>
									<option>현불택배</option>
								</select>
							</td>
						</tr>
						<tr>
							<th>수량 <span class="red">*</span></th>
							<td>
								<input type="text" class="w265" /><span class="unit">개</span>
								<a href="#" class="btn fee">개별단가</a>
								<span class="th_fee">기타 운임 <span class="red">*</span></span>
								<input type="text" class="w265" /><span class="unit">원</span>
							</td>
						</tr>
						<tr>
							<th>도서운임 <span class="red">*</span></th>
							<td><input type="text" class="w265" /><span class="unit">원</span></td>
						</tr>
						<tr>
							<th>총 합계 <span class="red">*</span></th>
							<td><input type="text" class="w265" /><span class="unit">원</span></td>
						</tr>
						<tr>
							<th class="memo">메모</th>
							<td><textarea name="" id="" cols="30" rows="10"></textarea></td>
						</tr>
						<tr>
							<th>파일 <span class="red">*</span></th>
							<td>
								<div class="fileBox">
									<label for="uploadBtn" class="btn_file upload acen">파일 선택</label>
									<input type="text" class="fileName" readonly="readonly" placeholder="선택한 파일이 없음" />
									<input type="file" id="uploadBtn" class="upload uploadBtn" />
								</div>
							</td>
						</tr>
						<tr>
							<th>운임구분 <span class="red">*</span></th>
							<td><input type="text" readonly="readonly" class="w265" placeholder="입금대기" /></td>
						</tr>
					</table>
				</form>
				<div class="receipt"><a href="#" class="btn acen">접수</a></div>
			</div>
		</div>
		<div class="footer">
			<div class="footer_list">
				<ul class="clearfix">
					<li><a href="#">이용약관</a></li>
					<li><a href="#">개인정보취급방침</a></li>
					<li><a href="#">이메일주소 무단수집거부</a></li>
					<li><a href="#">택배표준약관</a></li>
					<li><a href="#">기업고객</a></li>
				</ul>
			</div>
				<div class="info">
					<address>(주)경동택배 경기도 김포시 고촌읍 아라육로 57번길(전호리 745)</address>
					<span>대표 백영길 사업자 등록번호 135-81-29340</span>
					<p>CopyrightⓒKYUNGDONG R&D ALL RIGHTS RESERVED</p>
				</div>
			
		</div>	
	
	</div>
</body>
<script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script>
    /*
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.
            // 예제를 참고하여 다양한 활용법을 확인해 보세요.
        }
    }).open();
    */
</script>
<script>
	function searchPostcode(type) {
	    new daum.Postcode({
	        oncomplete: function(data) {
	            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
	
	            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
	            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
	            var addr = ''; // 주소 변수
	            var extraAddr = ''; // 참고항목 변수
	
	            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
	            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
	                addr = data.roadAddress;
	            } else { // 사용자가 지번 주소를 선택했을 경우(J)
	                addr = data.jibunAddress;
	            }
	
	            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
	            if(data.userSelectedType === 'R'){
	                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
	                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
	                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
	                    extraAddr += data.bname;
	                }
	                // 건물명이 있고, 공동주택일 경우 추가한다.
	                if(data.buildingName !== '' && data.apartment === 'Y'){
	                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
	                }
	                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
	                if(extraAddr !== ''){
	                    extraAddr = ' (' + extraAddr + ')';
	                }
	                // 조합된 참고항목을 해당 필드에 넣는다.
	                document.getElementById(type + "_extraAddress").value = extraAddr;
	            
	            } else {
	                document.getElementById(type + "_extraAddress").value = '';
	            }
	
	            // 우편번호와 주소 정보를 해당 필드에 넣는다.
	            document.getElementById(type + '_postcode').value = data.zonecode;
	            document.getElementById(type + "_address").value = addr;
	            // 커서를 상세주소 필드로 이동한다.
	            document.getElementById(type + "_detailAddress").focus();
	        }
	    }).open();
	}
</script>



<script type="text/javascript">
	
	
	$(document).ready(function(){
		var uploadFile = $('.fileBox .uploadBtn');
		uploadFile.on('change', function(){
			if(window.FileReader){
				var filename = $(this)[0].files[0].name;
			} else {
				var filename = $(this).val().split('/').pop().split('\\').pop();
			}
			$(this).siblings('.fileName').val(filename);
		});
	
		
	});


</script>



</html>
