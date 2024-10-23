<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<div class="contents cont_layerDetail lts">
	<div id="original">
		<h3 class="acen">택배접수 상세보기</h3>
		<div class="receiver">
			<p class="type"><img src="../../images/dot_p.jpg" alt="점" />보내는 분 <c:if test="${vo.rcpKind eq '1'}"> &nbsp;(접수자 : ${vo.cusReceiptNm}, 연락처 : ${vo.cusReceiptTelNum})</c:if></p>
			<div class="tableBox">
				<table>
					<tr>
						<th>이름 </th>
						<td>${vo.cusSendNm}</td>
					</tr>
					<tr style="line-height: 30px;">
						<th>연락처 </th>
						<td>
							${vo.cusSendTelnum}
       					</td>
					</tr>
					<tr>
						<th>골프장 상호</th>
						<td>
							${vo.sendGolfName}
						</td>
					</tr>
					<tr>
						<th rowspan="3" class="address">주소 </th>
						<td>
							${vo.cusSendZip}
						</td>
					</tr>
					<tr>
						<td>
							${vo.cusSendAddr1}
						</td>
					</tr>
					<tr>
						<td>
							${vo.cusSendAddr2}
						</td>
					</tr>
					<tr>
						<th>발송영업소 </th>
						<td>
							${vo.brStrNm}
						</td>
					</tr>
					<tr>
						<th>외부업체</th>
						<td>
							${vo.extComp}
						</td>
					</tr>
					<tr>
						<th>업체연락처</th>
						<td>
							${vo.extCompTelnum}
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="sender">
			<p class="type"><img src="../../images/dot_p.jpg" alt="점" />받는 분</p>
			<div class="tableBox">
				<table>
					<tr>
						<th>이름 </th>
						<td>${vo.cusReNm}</td>
					</tr>
					<tr>
						<th>연락처 </th>
						<td>
							${vo.cusReTelnum2}
	      				</td>
					</tr>
					<tr>
						<th>골프장 상호 
<c:if test="${vo.srType eq 'S' }">
							
</c:if>							
						</th>
						<td>${vo.recvGolfName}</td>
					</tr>
					<tr>
						<th rowspan="3" class="address">주소 </th>
						<td>
							${vo.cusReZip}
						</td>
					</tr>
					<tr>
						<td>
							${vo.cusReAddr1}
						</td>
					</tr>
					<tr>
						<td>
							${vo.cusReAddr2}
						</td>
					</tr>
					<tr>
						<th>도착영업소 </th>
						<td>
							${vo.brEndNm}
						</td>
					</tr>
				</table>
			</div>
		</div>
		<div class="product_info">
			<p class="type"><img src="../../images/dot_p.jpg" alt="점" />물품정보</p>
			<div class="tableBox">
				<table>
					<tr>
						<th>${vo.prdType eq 1 ? '골프' : vo.prdType eq 2 ? '중고채' : '캐리어'}접수일 </th>
						<td >
							${vo.regDate}
						</td>
						<th class="th">입력단가 </th>
						<td colspan="3">
							${vo.pdIndivPrice }
						</td>
					</tr>
					<tr>
						<th>경기일 </th>
						<td>
							${vo.gameDt}
						</td>						
						<td class="th">픽업희망일 </td>
						<td>${vo.picReqDt}</td>
					</tr>
					<tr>
						<th>품목명 </th>
						<td>
							${vo.pdNm}
						</td>
						<th class="th">포장상태 </th>
						<td>${vo.pdPack}</td>						
					</tr>
					<tr>
						<th>배송구분 </th>
						<td>
							${vo.payType eq '0' ? '현불택배' : '착불택배'}
						</td>
						<td class="th">운임구분 </td>
						<td>${vo.payStatName }</td>
					</tr>
					<tr style="line-height: 30px;">
						<th>보스턴백 수량 </th>
						<td>
							${vo.bostonCnt}<span class="unit">개</span>
						</td>
						<td class="th">${vo.prdType eq 1 ? '골프백' : vo.prdType eq 2 ? '중고채' : '캐리어'} 수량 </td>
						<td>${vo.pdCnt}<span class="unit">개</span></td>	
						
					</tr>
					<tr>
						<th>골프채 수량</th>
						<td><fmt:formatNumber value="${vo.pdGolfCnt}" pattern="#,###"/><span class="unit">개</span></td>
						<td class="th">할증운임</td>
						<td>
							<fmt:formatNumber value="${vo.pdEtcPay}" pattern="#,###"/><span class="unit">원</span>
							<!-- <a href="#" class="btn">개별단가</a>
							<span class="th_fee">기타 운임 </span>
							<input type="text" class="w265" /><span class="unit">원</span> -->
						</td>
					</tr>
					
					<tr>
						<th>배송운임 </th>
						<td><fmt:formatNumber value="${vo.pdDeliPay}" pattern="#,###"/><span class="unit">원</span></td>
						<td class="th">도서운임 </td>
						<td colspan="3"><fmt:formatNumber value="${vo.pdDosunPay}" pattern="#,###"/><span class="unit">원</span></td>
					</tr>
					
					<tr>
						<th>아이콘수수료 </th>
						<td><fmt:formatNumber value="${vo.iconFee}" pattern="#,###"/><span class="unit">원</span></td>
						<td class="th">총 합계</th>
						<td colspan="3"><fmt:formatNumber value="${vo.pdPay}" pattern="#,###"/><span class="unit">원</span></td>
					</tr>
					<tr>
						<th>메모</th>
						<td colspan="3" class="short_memo"><pre>${vo.pdMemo}</pre></td>
					</tr>
					<!-- <tr>
						<th>파일 </th>
						<td>
							<div class="fileBox">
								<label for="uploadBtn" class="btn_file upload acen">파일 선택</label>
								<input type="text" class="fileName" readonly="readonly" placeholder="선택한 파일이 없음" />
								<input type="file" id="uploadBtn" class="upload uploadBtn" />
							</div>
						</td>
					</tr> -->
				</table>
			</div>
		</div>
		<div class="delivery_nb">
			<p class="type"><img src="../../images/dot_p.jpg" alt="점" />택배정보</p>
			<div class="tableBox">
				<table>
					<tr>
						<th>회수접수번호 </th>
						<td colspan="3">
							${vo.rtNumber}
						</td>
					</tr>
					<tr>
						<th>택배접수일</th>
						<td>${vo.pdRecDt }</td>
					</tr>
					<tr>
						<th>운송장번호</th>
						<td>${vo.pdBarcode }</td>
					</tr>
					<tr>
						<th>인수완료시간</th>
						<td>${vo.scTime }</td>
					</tr>
				</table>
			</div>
		</div>
		
		<div class="cancle_reason">
			<p class="type"><img src="../../images/dot_p.jpg" alt="점" />취소사유</p>
			<div class="tableBox cancleMemo" style="padding: 4px 20px !important; font-size: 15px;">${vo.canReason }</div>
		</div>
	

	
		<div class="receipt_layer receipt_layer02">
<c:if test="${vo.rcpInfStat eq '0' }">
			<a href="javascript:;" id="update" class="btn acen">수정</a>
</c:if>
<c:if test="${vo.rcpInfStat ne '0' }">
			<a href="javascript:;" class="btn acen">수정불가</a>
</c:if>	
			<a href="javascript:;" id = "photo" class="btn acen">택배사진</a>
			<a href="javascript:;" id = "receiptCopy" class="btn acen cancle">재접수</a>
			<a href="javascript:$.colorbox.close();" class="btn acen cancle">닫기</a>
		</div>
	</div>
</div>
</body>

<script type="text/javascript">

	$(document).ready(function(){
		
		$('#receiptCopy').click(function () {
			$("#rcpISeq").val('${vo.rcpInfSeq}');
			if (confirm("재접수 하시겠습니까?")){
				$('#copyForm').attr('action','./receiptWriteCp.do').submit();
			}
		});
		
		$('#update').click(function () {
			
			var rcpInfSeq = '${vo.rcpInfSeq}';
    		var sHref = './receiptDetailWrite.do?rcpISeq=' + rcpInfSeq;
    		$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1230,
    			height: 870
    		});
			
		});

		$('#photo').click(function () {
			var rcpInfSeq = '${vo.rcpInfSeq}';
    		var sHref = './receiptPhotoLayer.do?rcpInfSeq=' + rcpInfSeq;
    		$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 900,
    			height: 700
    		});
		});
		
	});
</script>
