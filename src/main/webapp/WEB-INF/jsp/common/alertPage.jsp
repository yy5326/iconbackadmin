<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp" %>
<%--
	공통 alert page
--%>

<!-- 모바일인 경우만 메타정보 추가 -->
<c:if test="${alert.mobile }">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
</c:if>
	<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>

	<c:choose>
		<c:when test="${alert.layered}">

			<%@ include file="/WEB-INF/include/admin_header_html_none.jsp"%>

			<div class="pop" style="padding-top: 34px;">

				<div class="cons">
					<div class="scroll" style="max-height: 820px;">
						<table class="pop_list">
							<colgroup>
								<col width="100%" />
							</colgroup>

							<tbody>
								<tr>
									<td>
										<c:if test="${!empty alert.message}">
											${fn:replace(alert.message, '\\r', '<br/>') }
										</c:if>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>

				<c:if test="${!empty alert.script }">
					<div class="btn">
						<a href="javascript: ${alert.script };" class="btn_blue">확인</a>
					</div>
				</c:if>

			</div>

		</c:when>

		<c:otherwise>
			<c:if test="${!empty alert }">
				<c:if test="${!empty alert.message}">
					<script type="text/javascript">

						var sMsg = '${fn:escapeXml(alert.message)}';
						//var sMsg = '${alert.message }';
						sMsg = sMsg.replace(/(?:\\[rn]|[\r\n])/g,"\r")
						//sMsg = sMsg.replace(/\r\n/g, replaceValue)
						alert(sMsg);
					</script>
				</c:if>

				<c:if test="${!empty alert.href}">
					<script type="text/javascript">
						location.href = '${alert.href }';
					</script>
				</c:if>

				<c:if test="${!empty alert.script}">
					<script type="text/javascript">
						${alert.script }
					</script>
				</c:if>
			</c:if>

		</c:otherwise>
	</c:choose>


	<%@ include file="/WEB-INF/include/admin_end_html.jsp"%>