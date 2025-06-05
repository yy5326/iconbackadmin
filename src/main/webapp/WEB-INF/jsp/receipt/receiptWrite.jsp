<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>

		<div class="contents">
			<form action="receiptProcConfirm.do" method="post">
				<div id="original">
					<div class="receiver">
						<p class="type">보내는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
						<div class="tableBox">
							<table>
								<tr>
									<th>운임체계</th>
									<td>
										<select id="fareType" name="fareType" class="w265">
											<option value="1">기본운임</option>
											<option value="2">스코어운임</option>
											<option value="3">특판가</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>편도/왕복 <span class="red">*</span></th>
									<td>
										<input type="radio" id="oneWay" name="delKind" value="1" checked />
		  								<label for="oneWay"><span>편도</span></label>
										<input type="radio" id="back" name="delKind" value="2"/>
		  								<label for="back"><span>왕복</span></label>
		  								<input type="hidden" name="list[0].rcpKind" value="0"/>
									</td>
								</tr>
								<tr>
									<th>이름 <span class="red">*</span></th>
									<td><input type="text" name="list[0].cusSendNm" class="notNull w410" data-name="이름"/>
										<input type="hidden" name="list[0].srType" value="S"/>
									</td>
								</tr>
								<tr>
									<th>연락처 <span class="red">*</span></th>
									<td>
										<select role="ephone1" class="phone">
										<c:forEach var="list" items="${phoneList}">
											<option value="${list}">${list}</option>
										</c:forEach>
										</select> -
		        						<input role="ephone1" type="tel" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/> -
		        						<input role="ephone1" type="tel" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/>
		        						<input type="hidden" role="ephone1" name="list[0].cusSendTelnum">
		        					</td>
								</tr>
								<tr>
									<th>골프장 상호</th>
									<td>
										<input type="text" role="golfSearch1" name="list[0].sendGolfName" class="w410" readonly="readonly"/>
										<input type="hidden" role="golfSearch1" name="list[0].sendGolfSeq" class="w410" value="0"/>
										<a href="javascript:;" role="golfSearch1" class="btn">찾기</a>
									</td>
								</tr>
								<tr>
									<th rowspan="3" class="address">주소 <span class="red">*</span></th>
									<td>
										<input type="text" role="zip1" name="list[0].cusSendZip" placeholder="우편번호" readonly="readonly" onclick="javascript:searchPostcode('zip1');" class="notNull" data-name="주소"/>
										<a href="javascript:searchPostcode('zip1');" class="postcode btn">우편번호 검색</a>
									</td>
								</tr>
								<tr>
									<td><input type="text" role="zip1" name="list[0].cusSendAddr1" class="w586" placeholder="기본주소" readonly="readonly" onclick="javascript:searchPostcode('zip1');"/>
										<input type="hidden" name="list[0].sendIslYn"/>
									</td>
								</tr>
								<tr>
									<td>
										<input type="text" role="zip1" name="list[0].cusSendAddr2" class="w586" placeholder="상세주소를 입력해 주세요" data-name="상세주소"/>
									</td>
								</tr>
								<tr>
									<th>발송영업소 <span class="red">*</span></th>
									<td>
										<input type="text" role="golfSearch1" id="office1" name="list[0].brStrNm" class="notNull" data-name="발송영업소" readonly="readonly"/>
										<input type="hidden" role="golfSearch1" id="officeCode1" name="list[0].brStrCode"/>
										<a href="javascript:;" role="1" class="officeLayer btn">찾기</a>
									</td>
								</tr>
								<tr>
									<th>외부업체</th>
									<td>
										<input type="text" name="list[0].extComp"/>
									</td>
								</tr>
								<tr>
									<th>업체연락처</th>
									<td>
										<select role="ecphone" class="phone">
										<c:forEach var="list" items="${phoneList}">
											<option value="${list}">${list}</option>
										</c:forEach>
										</select> -
		        						<input role="ecphone" type="tel" class="phone onlyNumber" maxlength="4" data-name="연락처"/> -
		        						<input role="ecphone" type="tel" class="phone onlyNumber" maxlength="4" data-name="연락처"/>
		        						<input type="hidden" role="ecphone" name="list[0].extCompTelnum">
		        					</td>
								</tr>
							</table>
						</div>
					</div>
					<div class="sender">
						<p class="type">받는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
						<div class="tableBox">
							<table>
								<tr>
									<th>이름 <span class="red">*</span></th>
									<td><input type="text" name="list[0].cusReNm" class="notNull w410"  data-name="이름"/></td>
								</tr>
								<tr>
									<th>연락처 <span class="red">*</span></th>
									<td>
										<select role="ephone2" class="phone">
										<c:forEach var="list" items="${phoneList}">
											<option value="${list}">${list}</option>
										</c:forEach>
										</select> -
		        						<input type="tel" role="ephone2" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/> -
		        						<input type="tel" role="ephone2" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/>
		        						<input type="hidden" role="ephone2"  name="list[0].cusReTelnum2">
		        					</td>
								</tr>
								<tr>
									<th>골프장 상호</th>
									<td>
										<input type="text" role="golfSearch2" name="list[0].recvGolfName"  class="w410" data-name="골프장 상호" readonly="readonly"/>
										<input type="hidden" role="golfSearch2" name="list[0].recvGolfSeq" class="w410" value="0"/>
										<a href="javascript:;" role="golfSearch2" class="btn">찾기</a>
										<input type="hidden" id="golfSeq" name="golfSeq" class="w410" value="0"/>
									</td>
								</tr>
								<tr>
									<th rowspan="3" class="address">주소 <span class="red">*</span></th>
									<td>
										<input type="text" role="zip2" name="list[0].cusReZip" placeholder="우편번호" readonly="readonly" onclick="javascript:searchPostcode('zip2');" class="notNull" data-name="주소"/>
										<a href="javascript:searchPostcode('zip2');" class="postcode btn">우편번호 검색</a>
									</td>
								</tr>
								<tr>
									<td><input type="text" role="zip2" name="list[0].cusReAddr1" class="w586" placeholder="기본주소" readonly="readonly" onclick="javascript:searchPostcode('zip2');"/>
										<input type="hidden" name="list[0].recvIslYn"/>
									</td>
								</tr>
								<tr>
									<td>
										<input type="text" role="zip2" name="list[0].cusReAddr2" class="w586" placeholder="상세주소를 입력해 주세요" data-name="상세주소"/>
									</td>
								</tr>
								<tr>
									<th>도착영업소 <span class="red">*</span></th>
									<td>
										<input type="text" role="golfSearch2" id="office2" name="list[0].brEndNm" class="notNull" data-name="도착영업소" readonly="readonly"/>
										<input type="hidden" role="golfSearch2" id="officeCode2" name="list[0].brEndCode"/>
										<a href="javascript:;" role="2" class="officeLayer btn">찾기</a>
									</td>
								</tr>
							</table>
						</div>
					</div>
					<div class="product_info">
						<p class="type">물품정보 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
						<div class="tableBox">
							<table>
								<tr>
									<th>경기일 <span class="red">*</span></th>
									<td>
										<input type="text" name="gameDt" class="notNull input datepicker" style="width:90px;" data-name="경기일" minDate="0" readonly="readonly"/>
										<a class="calendar datepicker" style="cursor: pointer;"></a>
										<!-- <span>&nbsp;&nbsp;(익일부터 영업일 기준으로 3일 후)</span> -->
									</td>
								</tr>
								<tr>
									<th>픽업희망일 <span class="red">*</span></th>
									<td>
										<input type="text" name="list[0].picReqDt" class="notNull input datepicker customPicker1" minDate="0"  style="width:90px;" data-name="픽업희망일" readonly="readonly"/>
										<a class="calendar datepicker customPicker1" style="cursor: pointer;"></a>
										<!-- <span>&nbsp;&nbsp;(익일부터 경기일 포함 영업일 기준으로 3일 전)</span> -->
									</td>
								</tr>
								<tr>
									<th>품목명 <span class="red">*</span></th>
									<td>
										<select name="list[0].pdNm" class="notNull w265" data-name="품목명">
											<option value="국제골프택배기본">국제골프택배기본</option>
											<option value="국제골프택배세트">국제골프택배세트</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>포장상태 <span class="red">*</span></th>
									<td>
										<!-- <input type="text" name="list[0].pdPack" class="notNull w410"  data-name="포장상태"/> -->
										<!-- <input type="radio" id="pdPackOneWay0" name="list[0].pdPack" value="편도" checked />
		  								<label for="oneWay"><span>편도</span></label>
										<input type="radio" id="pdPackBack0" name="list[0].pdPack" value="왕복"/>
		  								<label for="back"><span>왕복</span></label> -->
		  								<select id="list[0].pdPack" name="list[0].pdPack" class="notNull w265" data-name="포장상태">
											<option value="편도">편도</option>
											<option value="왕복">왕복</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>배송구분 <span class="red">*</span></th>
									<td>
										<select name="list[0].payType" class="notNull w265" data-name="배송구분">
											<option value="0">현불택배</option>
										</select>
									</td>
								</tr>
								<tr>
									<th>보스턴백 수량 <span class="red onlyNumber">*</span></th>
									<td>
										<input type="text" name="list[0].bostonCnt" id="bostonCnt0" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
									</td>
								</tr>
								<tr>
									<th>골프백 수량 <span class="red onlyNumber">*</span></th>
									<td>
										<input type="text" name="list[0].pdCnt" id="pdCnt0" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
										<a href="javascript:;" role="openPriceLayer1" class="btn unitPrice">개별단가</a>
										<span class="th_fee">할증 운임 <span class="red">*</span></span>
										<input type="text" role="pay" name="list[0].pdEtcPay" class="w265" readonly="readonly"/><span class="unit">원</span>
										<input type="hidden" name="list[0].pdIndivPrice" class="notNull" data-name="개별단가"/>
									</td>
								</tr>
								<tr>
									<th>골프채 수량 <span class="red">*</span></th>
									<td>
										<input type="text" name="list[0].pdGolfCnt" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
									</td>
								</tr>
								<tr>
									<th>도서운임 <span class="red">*</span></th>
									<td>
										<input type="text" role="pay" name="list[0].pdDosunPay" class="w265 input" readonly="readonly"/><span class="unit">원</span>
									</td>
								</tr>
								<tr>
									<th>배송운임 <span class="red">*</span></th>
									<td>
										<input type="text" role="pay" name="list[0].pdDeliPay" class="w265 input" readonly="readonly"/><span class="unit">원</span>
									</td>
								</tr>
								<tr>
									<th>아이콘수수료 <span class="red">*</span></th>
									<td>
										<input type="text" role="pay" id="iconFee0" name="list[0].iconFee" class="w265 input" value="8,000" readonly="readonly"/><span class="unit">원</span>
									</td>
								</tr>
								<tr>
									<th>총 합계 <span class="red">*</span></th>
									<td>
										<input type="text" role="pay" name="list[0].pdPay" class="w265 input" readonly="readonly"/><span class="unit">원</span>
									</td>
								</tr>
								<tr>
									<th class="memo">메모</th>
									<td>
										<textarea name="list[0].pdMemo" cols="30" rows="10"></textarea>
									</td>
								</tr>
								<!-- <tr>
									<th>파일 <span class="red">*</span></th>
									<td>
										<div class="fileBox">
											<label for="uploadBtn" class="btn_file upload acen">파일 선택</label>
											<input type="text" class="fileName" readonly="readonly" placeholder="선택한 파일이 없음" />
											<input type="file" id="uploadBtn" class="upload uploadBtn" />
										</div>
									</td>
								</tr> -->
								<tr>
									<th>운임구분 <span class="red">*</span></th>
									<td><input type="text" readonly="readonly" class="w265" placeholder="입금대기" /></td>
								</tr>
							</table>
						</div>
					</div>
				</div>

				<div id="rotate" style="display: none;">
					<div class="rot_tit">
						<span></span>
						<p>왕복 배송지</p>
					</div>
					<div class="receiver" style="position: relative;">
					<p class="type" style="margin-top: 40px;">보내는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span><span class="red"></span></p>
					<div class="chk">
						<input type="checkbox" id="paste"/>
			  			<label for="paste"><span>위에서 입력한 정보와 같습니다.</span></label>
		  			</div>
					<div class="tableBox">
						<table>
							<tr>
								<th>이름 <span class="red">*</span></th>
								<td><input type="text" name="list[1].cusSendNm" class="notNull w410" data-name="이름"/>
									<input type="hidden" name="list[1].srType" value="R"/>
								</td>
							</tr>
							<tr>
								<th>연락처 <span class="red">*</span></th>
								<td>
									<select role="ephone3" class="phone">
									<c:forEach var="list" items="${phoneList}">
										<option value="${list}">${list}</option>
									</c:forEach>
									</select> -
	        						<input role="ephone3" type="tel" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/> -
	        						<input role="ephone3" type="tel" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/>
	        						<input type="hidden" role="ephone3" name="list[1].cusSendTelnum">
	        					</td>
							</tr>
							<tr>
								<th>골프장 상호</th>
								<td>
									<input type="text" role="golfSearch3" name="list[1].sendGolfName" role="golfName3" class="w410" readonly="readonly"/>
									<input type="hidden" role="golfSearch3" name="list[1].sendGolfSeq" class="w410" value="0"/>
									<a href="javascript:;" role="golfSearch3" class="btn">찾기</a>
								</td>
							</tr>
							<tr>
								<th rowspan="3" class="address">주소 <span class="red">*</span></th>
								<td>
									<input type="text" role="zip3" name="list[1].cusSendZip" placeholder="우편번호" readonly="readonly" onclick="javascript:searchPostcode('zip3');" class="notNull" data-name="주소"/>
									<a href="javascript:searchPostcode('zip3');" class="postcode btn">우편번호 검색</a>
								</td>
							</tr>
							<tr>
								<td><input type="text" role="zip3" name="list[1].cusSendAddr1" class="w586" placeholder="기본주소" readonly="readonly" onclick="javascript:searchPostcode('zip3');"/>
									<input type="hidden" name="list[1].sendIslYn"/>
								</td>
							</tr>
							<tr>
								<td>
									<input type="text" role="zip3" name="list[1].cusSendAddr2" class="w586" placeholder="상세주소를 입력해 주세요" data-name="상세주소"/>
								</td>
							</tr>
							<tr>
								<th>발송영업소 <span class="red">*</span></th>
								<td>
									<input type="text" role="golfSearch3" id="office3" name="list[1].brStrNm" class="notNull" data-name="발송영업소" readonly="readonly"/>
									<input type="hidden" role="golfSearch3" id="officeCode3" name="list[1].brStrCode"/>
									<a href="javascript:;" role="3" class="officeLayer btn">찾기</a>
								</td>
							</tr>
							<tr>
								<th>외부업체 </th>
								<td>
									<input type="text" name="list[1].extComp"/>
								</td>
							</tr>
							<tr>
								<th>업체연락처</th>
								<td>
									<select role="ecphone1" class="phone">
									<c:forEach var="list" items="${phoneList}">
										<option value="${list}">${list}</option>
									</c:forEach>
									</select> -
	        						<input role="ecphone1" type="tel" class="phone onlyNumber" maxlength="4" data-name="연락처"/> -
	        						<input role="ecphone1" type="tel" class="phone onlyNumber" maxlength="4" data-name="연락처"/>
	        						<input type="hidden" role="ecphone1" name="list[1].extCompTelnum">
	        					</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="sender">
					<p class="type">받는 분 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
					<div class="tableBox">
						<table>
							<tr>
								<th>이름 <span class="red">*</span></th>
								<td><input type="text" name="list[1].cusReNm" class="notNull w410" data-name="이름"/></td>
							</tr>
							<tr>
								<th>연락처 <span class="red">*</span></th>
								<td>
									
									<select role="ephone4" class="phone">
									<c:forEach var="list" items="${phoneList}">
										<option value="${list}">${list}</option>
									</c:forEach>
									</select> -
	        						<input type="tel" role="ephone4" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/> -
	        						<input type="tel" role="ephone4" class="phone onlyNumber" maxlength="4" data-name="기타 연락처"/>
	        						<input type="hidden" role="ephone4"  name="list[1].cusReTelnum2">
	        					</td>
							</tr>
							<tr>
								<th>골프장 상호</th>
								<td>
									<input type="text" role="golfSearch4" name="list[1].recvGolfName" role="golfName4" class="w410" data-name="골프장 상호" readonly="readonly"/>
									<input type="hidden" role="golfSearch4" name="list[1].recvGolfSeq" class="w410" value="0"/>
									<a href="javascript:;" role="golfSearch4" class="btn">찾기</a>
								</td>
							</tr>
							<tr>
								<th rowspan="3" class="address">주소 <span class="red">*</span></th>
								<td>
									<input type="text" role="zip4" name="list[1].cusReZip" placeholder="우편번호" readonly="readonly" onclick="javascript:searchPostcode('zip4');" class="notNull" data-name="주소"/>
									<a href="javascript:searchPostcode('zip4');" class="postcode btn">우편번호 검색</a>
								</td>
							</tr>
							<tr>
								<td>
									<input type="text" role="zip4" name="list[1].cusReAddr1" class="w586" placeholder="기본주소" readonly="readonly" onclick="javascript:searchPostcode('zip4');"/>
									<input type="hidden" name="list[1].recvIslYn"/>
								</td>
							</tr>
							<tr>
								<td>
									<input type="text" role="zip4" name="list[1].cusReAddr2" class="w586" placeholder="상세주소를 입력해 주세요" data-name="상세주소"/>
								</td>
							</tr>
							<tr>
								<th>도착영업소 <span class="red">*</span></th>
								<td>
									<input type="text" role="golfSearch4" id="office4" name="list[1].brEndNm" class="notNull" data-name="도착영업소" readonly="readonly"/>
									<input type="hidden" role="golfSearch4" id="officeCode4" name="list[1].brEndCode"/>
									<a href="javascript:;" role="4" class="officeLayer btn">찾기</a>
								</td>
							</tr>
						</table>
					</div>
				</div>
				<div class="product_info">
					<p class="type">물품정보 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
					<div class="tableBox">
						<table>
							<!-- <tr>
								<th>경기일 <span class="red">*</span></th>
								<td>
									<input type="text" name="gameDt" class="input datepicker" style="width:90px;" >
									<a class="calendar datepicker" style="cursor: pointer;"></a>
								</td>
							</tr> -->
							<tr>
								<th>픽업희망일 <span class="red">*</span></th>
								<td>
									<input type="text" name="list[1].picReqDt" class="notNull input datepicker customPicker2" style="width:90px;" minDate="0" data-name="픽업희망일" readonly="readonly"/>
									<a class="calendar datepicker customPicker2" style="cursor: pointer;"></a>
									<!-- <span>&nbsp;&nbsp;(경기일 포함 영업일 기준으로 6일까지)</span> -->
								</td>
							</tr>
							<tr>
								<th>품목명 <span class="red">*</span></th>
								<td>
									<select name="list[1].pdNm" class="notNull w265" data-name="품목명">
										<option value="국제골프택배기본">국제골프택배기본</option>
										<option value="국제골프택배세트">국제골프택배세트</option>
									</select>
								</td>
							</tr>
							<tr>
								<th>포장상태 <span class="red">*</span></th>
								<td>
									<!-- <input type="text" name="list[1].pdPack" class="notNull w410" data-name="포장상태"/> -->
									<!-- <input type="radio" id="pdPackOneWay1" name="list[1].pdPack" value="편도" checked />
	  								<label for="oneWay"><span>편도</span></label>
									<input type="radio" id="pdPackBack1" name="list[1].pdPack" value="왕복"/>
	  								<label for="back"><span>왕복</span></label> -->
	  								<select id="list[1].pdPack" name="list[1].pdPack" class="notNull w265" data-name="포장상태">
										<option value="편도">편도</option>
										<option value="왕복">왕복</option>
									</select>
								</td>
							</tr>
							<tr>
								<th>배송구분 <span class="red">*</span></th>
								<td>
									<select name="list[1].payType" class="notNull w265" data-name="배송구분">
										<option value="0">현불택배</option>
									</select>
								</td>
							</tr>
							
							<tr>
								<th>보스턴백 수량 <span class="red onlyNumber">*</span></th>
								<td>
									<input type="text" name="list[1].bostonCnt" id="bostonCnt1" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
								</td>
							</tr>
							<tr>
								<th>골프백 수량 <span class="red onlyNumber">*</span></th>
								<td>
									<input type="text" name="list[1].pdCnt" id="pdCnt1" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
									<a href="javascript:;" role="openPriceLayer2" class="btn unitPrice">개별단가</a>
									<span class="th_fee">할증 운임 <span class="red">*</span></span>
									<input type="text" role="pay" name="list[1].pdEtcPay" class="w265" readonly="readonly"/><span class="unit">원</span>
									<input type="hidden" name="list[1].pdIndivPrice" class="notNull" data-name="개별단가"/>
								</td>
							</tr>
								<tr>
									<th>골프채 수량 <span class="red">*</span></th>
									<td>
										<input type="text" name="list[1].pdGolfCnt" class="notNull w265 input onlyNumber" value="1" data-name="수량"/><span class="unit">개</span>
									</td>
								</tr>
							<tr>
								<th>도서운임 <span class="red">*</span></th>
								<td><input type="text" role="pay" name="list[1].pdDosunPay" class="w265 input" readonly="readonly"/><span class="unit">원</span></td>
							</tr>
							<tr>
								<th>배송운임 <span class="red">*</span></th>
								<td>
									<input type="text" role="pay" name="list[1].pdDeliPay" class="w265 input" readonly="readonly"/><span class="unit">원</span>
								</td>
							</tr>
							<tr>
								<th>아이콘 수수료 <span class="red">*</span></th>
								<td>
									<input type="text" role="pay" id="iconFee1" name="list[1].iconFee" class="w265 input" readonly="readonly" value="8,000"/><span class="unit">원</span>
								</td>
							</tr>
							<tr>
								<th>총 합계 <span class="red">*</span></th>
								<td>
									<input type="text" role="pay" name="list[1].pdPay" class="w265 input" readonly="readonly"/><span class="unit">원</span>
								</td>
							</tr>
							<tr>
								<th class="memo">메모</th>
								<td><textarea name="list[1].pdMemo" cols="30" rows="10"></textarea></td>
							</tr>
							<!-- <tr>
								<th>파일 <span class="red">*</span></th>
								<td>
									<div class="fileBox">
										<label for="uploadBtn" class="btn_file upload acen">파일 선택</label>
										<input type="text" class="fileName" readonly="readonly" placeholder="선택한 파일이 없음" />
										<input type="file" id="uploadBtn" class="upload uploadBtn" />
									</div>
								</td>
							</tr> -->
							<!-- <tr>
								<th>운임구분 <span class="red">*</span></th>
								<td><input type="text" readonly="readonly" class="w265" placeholder="입금대기" /></td>
							</tr> -->
						</table>
					</div>
				</div>
			</div>
		 </form>
		<div class="receipt"><a href="javascript:;" id="add" class="btn acen">접수</a></div>

		<!-- 개별단가 레이어 -->
		<div class="dim-layer" style="display: none;">
			<div class="dimBg"></div>

			<div id="indivPrice" class="pop-layer">
				<div class="pop-container">
					<div class="pop-conts">
						<!-- content -->
						<div class="layerT">
							<p class="ctxt">개별단가</p>
							<a href="javascript:;" role="cancel" class="btn-layerClose"></a>
						</div>
						<div class="layerCont layerCont01">
							<p class="t">일괄적용</p>
							<a href="javascript:;" id="batch" class="btn acen">일괄적용</a>
							<div class="chk02">
								<input type="checkbox" id="total" checked="checked"/>
					  			<label for="total"><span><input type="number" class="onlyNumber" min="1" max="1000" role="price"/></span></label>
					  			<p class="unit">만원</p>
				  			</div>
						</div>
						<div class="layerCont layerCont02" id="innerArea">
							<p class="t">개별적용</p>
							<div class="chk02 input">
								<input type="checkbox" id="price1" checked="checked"/>
					  			<label for="price1"><span><input type="number" class="onlyNumber" min="1" max="1000" role="price" readonly="readonly" oninput="javascript:calExtraPriceClick();"/></span></label>
					  			<p class="unit">만원</p>
				  			</div>
						</div>
						<div class="layerCont layerCont_Stat">
							<p class="t" >합계</p>
							<input type="number" id="indivTotalPrice" readonly="readonly"/>
					  		<p class="unit">만원</p>
						</div>
						<div class="layer_btn">
							<a href="javascript:;" onclick="javascript:calExtraPrice(false);" class="btn acen">적용</a>
							<a href="javascript:;" role="cancel" class="btn acen">취소</a>
							<a href="javascript:;" onclick="javascript:reset();" class="btn acen" style="margin-left: 115px;">초기화</a>
						</div>
						<!-- content -->
					</div>
				</div>
			</div>
		</div>
		<!-- 개별단가 레이어 -->
	</div>
</body>

<script type="text/javascript">
	$(document).ready(function(){
		
		//새로고침 또는 페이지 이동 시 확인
	    $(window).on("beforeunload", function(){
	        return "입력사항이 저장되지 않을 수 있습니다.\n저장하고 넘어가시겠습니까?";
	    });
	  	//submit은 이벤트 제외 시킴
	    $(document).on("submit", "form", function(event){
	        $(window).off("beforeunload");
	    });

		//운임체계 변경 시 적용
		$("#fareType").change(function(){
			//운임계산
			searchIsl();
		});

		//경기일 선택 시 픽업희망일 초기화
		$("input[name=gameDt]").change(function(){
			$("input[name$=picReqDt]").val("");
		});

		//경기일  지정에 따라서 희망일 범위 지정
		var noSundays = function(date){
			var day = date.getDay();
			return [(day != 0 && day != 6)];
		}

		//경기일  지정에 따라서 희망일 범위 지정
		var setDatePicker = function(_selObject, m, c, f){
			if(typeof _selObject !== "undefined" || _selObject !== null || _selObject !== ""){
				$(_selObject).datepicker("option", "minDate", (f) ? new Date(tomorrowDate()) : c).datepicker("option", "maxDate", (f) ? c : m).datepicker("option", "beforeShowDay",  noSundays);
			}
		}

		//익일 확인
		var tomorrowDate = function() {
			var rDate = "";
			var today = new Date();
			var tomorrow = new Date(today.valueOf() + (24*60*60*1000));
			var year = tomorrow.getFullYear();
			var month = tomorrow.getMonth() + 1;
			var day = tomorrow.getDate();
			if (day < 10) {
				day = '0' + day
			}
			if (month < 10) {
				month = '0' + month
			}
			rDate = year + '-' + month + '-' + day;
			return rDate;
		}

		//경기일  지정에 따라서 희망일 범위 지정
		$("[class*=customPicker]").click(function(){
			if($("input[name=gameDt]").val() == ""){
				alert("경기일을 선택해 주십시오.");
				$("input[name=gameDt]").focus();
				return;
			}
			/*
			var attrFlag = $(this).attr("class").indexOf("customPicker1") > 0 ? true : false;
			var curDate = $("input[name=gameDt]").datepicker("getDate");
			var minDate = $("input[name=gameDt]").datepicker("getDate");

			//경기일 포함 이전 3일(익일 ~ 경기일포함 영업일 3일 제외)
			
			if(attrFlag){
				curDate.setDate(curDate.getDate() - (curDate.getDay() == 6 ? 4 : curDate.getDay() > 2 ? 3 : 5));
			//경기일 포함 이후 6일
			}else{
				curDate.setDate(curDate.getDate());
				minDate.setDate(minDate.getDate() + (curDate.getDay() > 4 ? 8 : 7));
			}

			//달력 text,image 클릭 시 분기 처리
			if ($(this).context.nodeName == "INPUT") {
				setDatePicker($(this), minDate, curDate, attrFlag);
				$(this).focus();
			}else{
				$(this).css({cursor : 'pointer'});
				setDatePicker($(this).prev(), minDate, curDate, attrFlag);
				$(this).prev().focus();
			}
			*/
		});

		//라디오버튼 편도/왕복 클릭 시 이벤트
		$("input[name=delKind]").click(function(){
			
			// 왕복 배송지 레이어 show / hide
			if($(this).prop("id") == "oneWay"){
				$("#rotate").hide();
				$("input[name$=srType]").attr("disabled",true);
				$("#rotate :input").attr("disabled",true);
			}else{
				$("#rotate").show();
				$("input[name$=srType]").removeAttr("disabled");
				$("#rotate :input").removeAttr("disabled");
			}
			
			// 물품 정보 하위 포장상태를 선택값 동기화 세팅
			if($(this).prop("id") == "oneWay"){				
				//$("input[id^=pdPackOneWay]").prop("checked", true);
				$("select[name$=pdPack]").val("편도").prop("selected", true);
				
			}else{
				//$("input[id^=pdPackBack]").prop("checked", true);
				$("select[name$=pdPack]").val("왕복").prop("selected", true);
			}				
			
			//운임계산
			searchIsl();
		});

		//입력 정보 위와 동일 버튼 체크 시
		$("#paste").click(function(){
			if($(this).prop("checked") == true){
				$("#rotate :input").not("[role*=phone],#paste,[name$=srType],[name$=picReqDt],[role*=ecphone],[name$=payType],[name$=pdPack]").each(function(){
					var element = $(this).attr("name");
					if(typeof element !== "undefined" || element !== null || element !== ""){
						var targetElement = element.replace("list[1]","list[0]");
						if(element.indexOf("cusSend") > -1){
							targetElement = targetElement.replace("cusSend", "cusRe");
						}else if(element.indexOf("cusRe") > -1){
							targetElement = targetElement.replace("cusRe", "cusSend");
						}else if(element.indexOf("recv") > -1){
							targetElement = targetElement.replace("recv", "send");
						}else if(element.indexOf("send") > -1){
							targetElement = targetElement.replace("send", "recv");
						}else if(element.indexOf("brEnd") > -1){
							targetElement = targetElement.replace("brEnd", "brStr");
						}else if(element.indexOf("brStr") > -1){
							targetElement = targetElement.replace("brStr", "brEnd");
						}
						$(this).val($("input[name='"+targetElement+"']").val());

						var readYn = $("input[name='"+targetElement+"']").attr("readonly") == "readonly" ? true : false;
						//if(readYn && (targetElement.indexOf("br") > -1 || targetElement.indexOf("golfName"))){
						if(readYn && (targetElement.indexOf("br") > -1 )){
							$(this).prop("readonly", true);
						}
					}
				});
				$("[role$=phone2]").each(function(idx){
					$("[role$=phone3]:eq("+idx+")").val($(this).val());
				});
				$("[role$=phone1]").each(function(idx){
					$("[role$=phone4]:eq("+idx+")").val($(this).val());
				});
				// 외부업체 연락처 paste
				$("[role$=ecphone]").each(function(idx){
					$("[role$=ecphone1]:eq("+idx+")").val($(this).val());
				});
				$("[role=zip2]").each(function(idx){
					$("[role=zip3]:eq("+idx+")").val($(this).val());
				});
				$("[role=zip1]").each(function(idx){
					$("[role=zip4]:eq("+idx+")").val($(this).val());
				});

				$("select[name$=pdNm]:last").val($("select[name$=pdNm]:first").val());
				
				// 아이콘 수수료 왕복 세팅
				iconFee3 = iconFee2;
				iconFee4 = iconFee1;
				
				//운임계산
	            searchIsl();				
				
			}else{
				$("#rotate :input").not("select,:radio,input[name$=srType]").val("");
				//골프장 선택, 영업소찾기 수동입력 불가로 제거
				/* $("input[role=golfSearch3], input[role=golfSearch4]").not("input[name$=GolfName]").each(function(){
					if($(this).attr("readonly") == "readonly")
						$(this).prop("readonly", false);
				}); */
				
				// 아이콘 수수료 왕복 부분만 초기화
				iconFee3 = [];
				iconFee4 = [];
			}
		});

		//골프장 상호 검색 레이어
		$("[role^=golfSearch]").not("input[name*=br]").click(function(){
			$.colorbox({
				href : './receiptGolfLayer.do?type1='+$(this).attr("role"),
				overlayClose : false,
				width : 1200,
				height : 740,
				iframe: true
				/* onComplete: function() {
					$.colorbox.resize();
				} */
			});
		});

		//접수 시작
		$("#add").click(function(){
			var cnt = Number($("input[name$=pdCnt]:first").val()) + Number($("input[name$=bostonCnt]:first").val());
			
			if(1>cnt){
				alert("백 수량을 입력해주세요.");
				$("input[name$=pdCnt]:first").focus();
				return;
			}
			
			//편도 처리
			if($("input[name=delKind]:checked").val() == "1"){
				$("input[name$=srType]").attr("disabled",true);
				$("#rotate :input").attr("disabled",true);
			}else{
				cnt = Number($("input[name$=pdCnt]:last").val()) + Number($("input[name$=bostonCnt]:last").val());
				if(1>cnt){
					alert("백 수량을 입력해주세요.");
					$("input[name$=pdCnt]:last").focus();
					return;
				}
			}

			//phoneNumber
			addTelNum();

			//입력값 체크
			if(checkData()){
				//등록 전 콤마제거
				$("input[role=pay]").each(function(){
					var uncommaData = uncomma($(this).val());
					$(this).val(uncommaData);
				});
				$("form").submit();
			}

		});

		//개별단가 레이어 보임
		$("a[role^=openPriceLayer]").click(function(){
			var isLayer = true;
			if($(this).attr("role") != "openPriceLayer1"){
				isLayer = false;
			}
			whoIsLayer = (isLayer) ? true : false;
			$("[role=price], #indivTotalPrice").val("");
			$("#innerArea").find("input[type=checkbox]").prop("checked", true);
			var pdIndivPrice = $("input[name$=pdIndivPrice]"+((isLayer) ? ":first" : ":last")).val();
	  		var cnt = Number($("input[name$=pdCnt]"+((isLayer) ? ":first" : ":last")).val()) + Number($("input[name$=bostonCnt]"+((isLayer) ? ":first" : ":last")).val());
			$("input[role=price]:gt(0)").prop("readonly", true);
			var pdIndivPriceArray = pdIndivPrice.split("|");
  			if(pdIndivPrice != "" && pdIndivPriceArray.length == cnt){
  				$("[role=price]:eq(1)").val(pdIndivPriceArray[0]);
  			}else{
  				$("[role=price]:eq(1)").val("");
  			}
	  		$(".chk02.input:gt(0)").remove();
	  		if(cnt > 1){
	  			for(var i=1; i<cnt; i++){
					//click bind
	  				var contents = $(".chk02.input:first").clone(true);
  					contents.find("input[type=checkbox]").prop("id", "price"+(i+1));
  					contents.find("label").prop("for", "price"+(i+1));
  					if(pdIndivPriceArray.length == cnt){
  						contents.find("input[role=price]").val(pdIndivPriceArray[i]);
  					}
	  				$(".chk02.input:last").after(contents);
	  			}
	  		}
  			//할증 운임 계산
  			if(pdIndivPrice != "" && pdIndivPriceArray.length == cnt) calExtraPrice(false);
			$(".dim-layer").show();
		})

		//개별단가 레이어 숨김
		$("[role=cancel]").click(function(){
			$(".dim-layer").hide();
		});

		//개별단가 클릭
		$("#innerArea").find("input[type=checkbox]").click(function(){
			var checked = $(this).attr("checked") == "checked" ? true : false;
			$(this).parent().find("input[role=price]").prop("readonly", checked).val("").css("background-color", "");
		});

		//일괄적용 클릭
		$("#batch").click(function(){
			if($("[role=price]:first").val() == ""){
				alert("일괄적용에 금액이 비어있습니다.");
				$("[role=price]:first").focus();
				return;
			}else if($("[role=price]:first").val() < 1){
				alert("최소값은 1만원 입니다. 다시 입력해 주세요.");
				$("[role=price]:first").focus();
				return;
			}else if($("[role=price]:first").val() > 1000){
				alert("최대값은 1000만원 입니다. 다시 입력해 주세요.");
				$("[role=price]:first").focus();
				return;
			}

			//할증 운임 계산
			calExtraPrice(true);
		});

		//일괄적용 체스박스 클릭
		$("#total").click(function(){
			var element = $("#innerArea").find("input");
			var checked = $(this).attr("checked") == "checked" ? true : false;

			$("#innerArea").find("input").each(function(){
				if($(this).attr("type") == "checkbox"){
					element.prop("checked", checked);
				}else{
					element.prop("readonly", checked).css("background-color", "");
				}
			});

		});

		//영업소 찾기
		$(".officeLayer").click(function(evt){
			evt.preventDefault();
			var targetId = "zip"+$(this).attr("role");
			var searchWord = $("input[role="+targetId+"]").val() == "" ? "" : $("input[role="+targetId+"]").val();
			window.open("/common/officePopup.do?type1=A&type2="+$(this).attr('role')+"&searchWord="+searchWord, "officeSearch", "top=200, left=100, width=800px, height=480px");
		});
	});
	//document.ready

	//개별단가 적용 레이어
	var whoIsLayer = true;
	//할증 운임 계산
	function calExtraPrice(flag){

		var returnVal = true;
		if(!flag){
			$("[role=price]:gt(0)").each(function(idx){
				if($(this).val() == ""){
					alert("물건 가격 "+(idx+1)+"번째가 비어 있습니다.");
					$(this).focus();
					returnVal = false;
				}else if($(this).val() < 1){
					alert("최소값은 1만원 입니다. 다시 입력해 주세요.");
					$(this).focus();
					returnVal = false;
				}else if($(this).val() > 1000){
					alert("최대값은 1000만원 입니다. 다시 입력해 주세요.");
					$(this).focus();
					returnVal = false;
				}
				return returnVal;
			});
			if(!returnVal) return;
		}

		var pdIndivPriceVal = "";
		var price = $("[role=price]:first").val();
		var total = 0;
		var extra = 200;
		var extra_total = 0;
		$("input[role=price]:gt(0)").each(function(){
			if(!flag) price = $(this).val();
			$(this).val(price);
			total += Number(price);
			pdIndivPriceVal += (pdIndivPriceVal != "") ? "|" + price : price;
			if(Number(price) > extra){
				extra_total += (Number(price) - extra) * 50;
			}
		});
		$("#indivTotalPrice").val(total);
		//$("#indivTotalPriceExtra").val(extra_total);
		if(!flag){
			//할증운임료 요청
			var extraTotalServer = getExtraPrice(whoIsLayer, pdIndivPriceVal);
			if(extraTotalServer.length < 1){
			 	alert("개별단가 서버 조회 불가\n관리자 문의 바랍니다.");
			 	return;
			 	//extraTotalServer = extra_total;
			}
			$("input[name$=pdIndivPrice]"+((whoIsLayer) ? ":first" : ":last")).val(pdIndivPriceVal);
			$("input[name$=pdEtcPay]"+((whoIsLayer) ? ":first" : ":last")).val(comma(extraTotalServer));
			$("[role=cancel]").trigger("click");
		}
		//운임계산
		searchIsl();
	}

	//클릭시 단순 계산
	function calExtraPriceClick(){
		var total = 0;
		$("input[role=price]:gt(0)").each(function(){
			total += Number($(this).val());
		});
		$("#indivTotalPrice").val(total);
	}

	//초기화 버튼 클릭
	function reset(){
		$("#indivPrice").find("input").val("");
	}

	//입력값 체크
	function checkData(){
		var type = ($("input[name=delKind]:checked").val() == "1") ? "#original" : "form";
		var returnVal = true;
		$(type+" .notNull").each(function(){
			if ($.trim($(this).val()) == ""){
	            alert($(this).attr("data-name")+" 항목을 입력해 주세요.");
	            if($(this).attr("data-name").indexOf("픽업희망일") > -1){
	            	$(this).trigger("click");
	            }else{
	            	$(this).focus();
	            }
	            returnVal = false;
	            return false;
			}
		});
		return returnVal;
	}

	//운임 계산 및 도서산간구분값 적용
	function searchIsl(){
		// 0 = 기본운임, 1 = 스코어운임, 2 = 특판가 (골프백 도선, 골프백편도, 골프백왕복, 보스턴 도선, 보스턴편도, 보스턴왕복)
		var amtArray = [[5000, 21000, 20000, 6000, 6000, 6000], [5000, 15000, 15000, 6000, 6000, 6000], [5000, 15000, 15000, 6000, 6000, 6000]]; //이 부분 변경 시 golfReceiptInf.selectByKakaoHistory 쿼리 수정 (배송운임 변경관련)
		var amt = amtArray[$("#fareType option").index($("#fareType option:selected"))];
		var dosun1 = 0, dosun2 = 0;
		
		$("input[role=zip1]:eq(1),input[role=zip2]:eq(1),input[role=zip3]:eq(1),input[role=zip4]:eq(1)").each(function(idx){
			if($(this).val().indexOf("제주") > -1){
				(idx < 2) ? dosun1 += 1 : dosun2 += 1;
				$(this).next().val("Y");
				console.log(idx);
				console.log(dosun1);
			}else{
				$(this).next().val("N");
			}
		});
		
		//골프장으로 도서지역 구분
		if(dosun1 == 0 && dosun2 == 0){
			$("input[role=golfSearch1]:eq(0),input[role=golfSearch2]:eq(0),input[role=golfSearch3]:eq(0),input[role=golfSearch4]:eq(0)").each(function(idx){
				if(trim($(this).val()) == "세이지우드 여수경도CC"){
					(idx < 2) ? dosun1 += 1 : dosun2 += 1;
				}
			});
		}

		var cnt = new Array;
		var bostonCnt = new Array;
		
		$("input[name$=pdCnt]").each(function(){
			cnt.push(($(this).val() != "") ? $(this).val() : 0);
		});
		
		$("input[name$=bostonCnt]").each(function(){
			bostonCnt.push(($(this).val() != "") ? $(this).val() : 0);
		});
		
		var extraPrice = new Array;
		$("input[name$=pdEtcPay]").each(function(){
			extraPrice.push(($(this).val() != "") ? Number(uncomma($(this).val())) : 0);
		});
		console.log("test mode2");
		// 아이콘 수수료 계산
		calculateIconFee();
		//편도
		if($("input[name=delKind]:checked").val() == "1"){
			if($("input[role=zip1]:eq(1)").val() != "" && $("input[role=zip2]:eq(1)").val() != ""){
				$("input[name$=pdDosunPay]:first").val((dosun1 == 1) ? comma((amt[0] * cnt[0]) + (amt[3] * bostonCnt[0])) : "0");
				$("input[name$=pdPay]:first").val((dosun1 == 1) ? comma(((amt[0] + amt[1]) * cnt[0]) + ((amt[3] + amt[4]) * bostonCnt[0]) + extraPrice[0] + Number($('#iconFee0').val())) : comma((amt[1] * cnt[0]) + (amt[4] * bostonCnt[0])  + extraPrice[0] + Number($('#iconFee0').val())));
				$("input[name$=pdDeliPay]:first").val(comma(amt[1] * cnt[0] + (amt[4] * bostonCnt[0])));
			}
		//왕복
		}else{
			if($("input[role=zip1]:eq(1)").val() != "" && $("input[role=zip2]:eq(1)").val() != ""){
				$("input[name$=pdDosunPay]:first").val((dosun1 == 1) ? comma((amt[0] * cnt[0]) + (amt[3] * bostonCnt[0]) ) : "0");
				$("input[name$=pdPay]:first").val((dosun1 == 1) ? comma(((amt[0] + amt[2]) * cnt[0]) + ((amt[3] + amt[5]) * bostonCnt[0]) + extraPrice[0] + Number($('#iconFee0').val())) : comma((amt[2] * cnt[0]) + (amt[5] * bostonCnt[0]) + extraPrice[0] + Number($('#iconFee0').val())));
				$("input[name$=pdDeliPay]:first").val(comma(amt[2] * cnt[0] + (amt[5] * bostonCnt[0])));
			}
			if($("input[role=zip3]:eq(1)").val() != "" && $("input[role=zip4]:eq(1)").val() != ""){
				$("input[name$=pdDosunPay]:last").val((dosun2 == 1) ? comma((amt[0] * cnt[1]) + (amt[3] * bostonCnt[1])) : "0");
				$("input[name$=pdPay]:last").val((dosun2 == 1) ? comma(((amt[0] + amt[2]) * cnt[1]) + ((amt[3] + amt[5]) * bostonCnt[1]) + extraPrice[1] + Number($('#iconFee1').val())) : comma((amt[2] * cnt[1]) + (amt[5]  * bostonCnt[1]) + extraPrice[1] + Number($('#iconFee1').val())));
				$("input[name$=pdDeliPay]:last").val(comma(amt[2] * cnt[1] + (amt[5] * bostonCnt[1])));
			}
		}
		
		$('#iconFee0').val(comma($('#iconFee0').val()));
		$('#iconFee1').val(comma($('#iconFee1').val()));
	}

	//전화번호 문자열 확인
	function addTelNum(){
		var telArr = new Array();
		$(".phone").each(function(){
			telArr.push($(this).attr("role"));
		});
		$.unique(telArr);
		if(telArr.length > 0){
			for(var i=0; i<telArr.length; i++){
				var telNum = "";
				$("[role="+telArr[i]+"]").each(function(idx){
					if(idx == 3) return false;
					if($(this).val() != "")
						telNum += (idx == 0) ? $(this).val() : "-" + $(this).val();
						//console.log(idx);
				});
				//10자리 이상만.. 일단?
				if(telNum.length > 10) $("input[role="+telArr[i]+"]:last").val(telNum);
			}
		}
	}

	//할증운임료 계산
	function getExtraPrice(layer, pdIndivPriceVal){

		var returnData = "";
		if(pdIndivPriceVal != ""){
			$.ajax({
				type : "POST",
				async: false ,
				url : "/common/getExtraPrice.do",
				data: {
					payType: $("select[name$=payType]"+((layer) ? ":first" : ":last")).val(),
					pdIndivPrice : pdIndivPriceVal
				},
				success : function(data) {
					if(data != "false")
						returnData = data.pdExtraCharge;
					else
						returnData = "";
		        },error : function(xhr, status, error) {
					alert("에러발생  " + xhr.status + "관리자에게 문의 바랍니다.");
				}
			});
		}
		return returnData;
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
						$(this).val("").focus();
	            });
	    		var targetElement = roleName.replace("zip", "golfSearch");
	    		$("input[role="+targetElement+"]").each(function(idx){
	    			//if(idx == 0) $(this).val("").prop("readonly", (targetElement == "golfSearch2") ? true : false);
	    			if(idx == 0) $(this).val("");
	    			else if(idx == 1) $(this).val("0");
	    			else if(idx == 2) $(this).val("");
	    			else $(this).val("");
	    		});
	    		
	    		initIconFee(targetElement);
	            //운임계산
	            searchIsl();	            
	        }
	    }).open();
	}
	
	var iconFee1 = []; // 편도의 출발지 수수료
	var iconFee2 = []; // 편도의 도착지 수수료
	var iconFee3 = []; // 왕복시 출발지 수수료
	var iconFee4 = []; // 왕복시 도착지 수수료
	
	// 주소 선택시 아이콘 수수료 초기화
	function initIconFee(type) {
		if ( type == 'golfSearch1' ) {
			iconFee1 = [];
		} else if ( type == 'golfSearch2' ) {
			iconFee2 = [];
		} else if ( type == 'golfSearch3' ) {
			iconFee3 = [];
		} else if ( type == 'golfSearch4' ) {
			iconFee4 = [];
		}
	}
	
	// 골프장 선택시 아이콘 수수료 정보 set
	function setIconFee(type, iconFeeArr) {
		if ( type == 'golfSearch1' ) {
			iconFee1 = iconFeeArr;
		} else if ( type == 'golfSearch2' ) {
			iconFee2 = iconFeeArr;
		} else if ( type == 'golfSearch3' ) {
			iconFee3 = iconFeeArr;
		} else if ( type == 'golfSearch4' ) {
			iconFee4 = iconFeeArr;
		}
	}
	
	// 아이콘 수수료 계산
	function calculateIconFee() {
		var delKindIndex = $("input[name=delKind]:checked").val()-1; // 편도/왕복
		var fareTypeIndex = $("#fareType option").index($("#fareType option:selected")); // 운임체계
		
		var pdFee = 8000;
		var bostonFee = 6000;

		if ( $("input[name=delKind]:checked").val() == '2' ) {
			pdFee = 4500;
		}
		if ( iconFee1.length == 0 && iconFee2.length == 0 ) {
			// 출발 도착 모두 골프장이 아닐 경우 - 8000원
			$('#iconFee0').val((pdFee * $('#pdCnt0').val()) + (bostonFee * $('#bostonCnt0').val()));
		} else if ( iconFee1.length > 0 && iconFee2.length == 0 ) {
			// 출발지가 골프장일 경우
			pdFee = delKindFee($('#pdCnt0').val(), $('#pdCnt1').val(), iconFee1[delKindIndex][fareTypeIndex], Math.min(iconFee1[0][0],pdFee));
			bostonFee = delKindFee($('#bostonCnt0').val(), $('#bostonCnt1').val(), iconFee1[delKindIndex + 2][fareTypeIndex], Math.min(iconFee1[delKindIndex + 1][fareTypeIndex],bostonFee));
			$('#iconFee0').val(pdFee + bostonFee);
		} else if ( iconFee1.length == 0 && iconFee2.length > 0 ) {
			// 도착지가 골프장일 경우			
			pdFee = delKindFee($('#pdCnt0').val(), $('#pdCnt1').val(), iconFee2[delKindIndex][fareTypeIndex], Math.min(iconFee2[0][0],pdFee));
			bostonFee = delKindFee($('#bostonCnt0').val(), $('#bostonCnt1').val(), iconFee2[delKindIndex + 2][fareTypeIndex], Math.min(iconFee2[delKindIndex + 1][fareTypeIndex],bostonFee));
			$('#iconFee0').val(pdFee + bostonFee);
		} else {
			// 골프장->골프장일 경우 세팅된 수수료 중 낮은 수수료로 책정한다.
			pdFee = delKindFee($('#pdCnt0').val(), $('#pdCnt1').val(), iconFee1[delKindIndex][fareTypeIndex], Math.min(iconFee1[delKindIndex][fareTypeIndex],pdFee)) + delKindFee($('#bostonCnt0').val(), $('#bostonCnt1').val(), iconFee1[delKindIndex + 2][fareTypeIndex], Math.min(iconFee1[delKindIndex + 1][fareTypeIndex],pdFee));
			bostonFee = delKindFee($('#pdCnt0').val(), $('#pdCnt1').val(), iconFee2[delKindIndex][fareTypeIndex], Math.min(iconFee2[delKindIndex][fareTypeIndex],pdFee)) + delKindFee($('#bostonCnt0').val(), $('#bostonCnt1').val(), iconFee2[delKindIndex + 2][fareTypeIndex], Math.min(iconFee2[delKindIndex + 1][fareTypeIndex],bostonFee));
			$('#iconFee0').val(Math.min(pdFee,bostonFee));
		}
		
		if ( $("input[name=delKind]:checked").val() == '2' ) {
			// 왕복시 수수료 계산
			if ( iconFee3.length == 0 && iconFee4.length == 0 ) {
				// 출발 도착 모두 골프장이 아닐 경우 - 9000원
				pdFee = delKindFee($('#pdCnt0').val(), $('#pdCnt1').val(), 4500, 8000);
				bostonFee = delKindFee($('#bostonCnt0').val(), $('#bostonCnt1').val(), 3000, 3000);
				$('#iconFee0').val(pdFee + bostonFee);

				pdFee = delKindFee($('#pdCnt1').val(), $('#pdCnt0').val(), 4500, 8000);
				bostonFee = delKindFee($('#bostonCnt1').val(), $('#bostonCnt0').val(), 3000, 3000);
				$('#iconFee1').val(pdFee + bostonFee);

			} else if ( iconFee3.length != 0 && iconFee4.length == 0 ) {
				// 출발지가 골프장일 경우
				pdFee = delKindFee($('#pdCnt1').val(), $('#pdCnt0').val(), iconFee3[delKindIndex][fareTypeIndex], Math.min(iconFee3[delKindIndex-1][fareTypeIndex],8000));
				bostonFee = delKindFee($('#bostonCnt1').val(), $('#bostonCnt0').val(), iconFee3[delKindIndex + 2][fareTypeIndex], Math.min(iconFee3[delKindIndex+1][fareTypeIndex],6000));
				$('#iconFee1').val(pdFee + bostonFee);				

			} else if ( iconFee3.length == 0 && iconFee4.length != 0 ) {
				// 도착지가 골프장일 경우
				pdFee = delKindFee($('#pdCnt1').val(), $('#pdCnt0').val(), iconFee4[delKindIndex][fareTypeIndex], Math.min(iconFee4[delKindIndex-1][fareTypeIndex],8000));
				bostonFee = delKindFee($('#bostonCnt1').val(), $('#bostonCnt0').val(), iconFee4[delKindIndex + 2][fareTypeIndex], Math.min(iconFee4[delKindIndex+1][fareTypeIndex],6000));
				$('#iconFee1').val(pdFee + bostonFee);

			} else {
				pdFee = delKindFee($('#pdCnt1').val(), $('#pdCnt0').val(), iconFee3[delKindIndex][fareTypeIndex], Math.min(iconFee3[delKindIndex-1][fareTypeIndex],8000)) + delKindFee($('#bostonCnt1').val(), $('#bostonCnt0').val(), iconFee3[delKindIndex + 2][fareTypeIndex], Math.min(iconFee3[delKindIndex+1][fareTypeIndex],6000));
				bostonFee = delKindFee($('#pdCnt1').val(), $('#pdCnt0').val(), iconFee4[delKindIndex][fareTypeIndex], Math.min(iconFee4[delKindIndex-1][fareTypeIndex],8000)) + delKindFee($('#bostonCnt1').val(), $('#bostonCnt0').val(), iconFee4[delKindIndex + 2][fareTypeIndex], Math.min(iconFee4[delKindIndex+1][fareTypeIndex],6000));
				$('#iconFee1').val(Math.min(pdFee , bostonFee));

			}
		}
		
		//console.log("운임체계 : "+$("#fareType option").index($("#fareType option:selected")));
		//console.log("편도/왕복 : "+$("input[name=delKind]:checked").val());
		//console.log("아이콘 수수료1 : "+$('#iconFee').val());
	}
	
	//fee(왕복요금), changeFee(편도요금)
	function delKindFee(cnt, changeCnt, fee, changeFee){
		fee = fee * cnt;
		if(changeCnt==0){			
			fee = changeFee * cnt;
		}			
		return fee;
	}
	
	
	function numberWithCommas(x) {
		x = x.replace(/[^0-9]/g,'');   // 입력값이 숫자가 아니면 공백
		x = x.replace(/,/g,'');          // ,값 공백처리
		return x.replace(/\B(?=(\d{3})+(?!\d))/g, ","); // 정규식을 이용해서 3자리 마다 , 추가 
	}
</script>
</html>