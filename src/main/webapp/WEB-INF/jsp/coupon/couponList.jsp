<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>

<form action="./couponList.do" id="couponForm" style="overflow:hidden !important;">
    <div class="contents inqueryCont">

        <div class="top_btn clearfix">
            <div class="left">
                <a href="javascript:openIssuePopup();" class="search_btn" style="margin-left: 0; background-color: #7d448d; color: #fff;">신규 쿠폰 발급</a>
            </div>
            <div class="right">
                <a class="search_btn" href="javascript:downloadExcel();">엑셀저장</a>
            </div>
        </div>

        <div class="top clearfix">
            <div class="left">
                <select id="partnerSeq" name="partnerSeq" style="margin-left:0px;">
                    <option value="">제휴사 전체</option>
                    <option value="1" <c:if test="${search.partnerSeq == 1}">selected</c:if>>SG골프</option>
                    <option value="2" <c:if test="${search.partnerSeq == 2}">selected</c:if>>카카오골프</option>
                    <option value="3" <c:if test="${search.partnerSeq == 3}">selected</c:if>>스마트스코어</option>
                </select>

                <tt:select list="${useYnList}" id="useYn" name="useYn" listKey="value" listValue="text" indexKey="" indexValue="사용여부" selected="${search.useYn}" />

                <input type="text" name="startDate" value="${search.startDate}" class="input datepicker" style="width:90px;" placeholder="발급시작일">
                <a class="calendar datepicker" style="cursor: pointer;"></a> -
                <input type="text" name="endDate" value="${search.endDate}" class="input datepicker" style="width:90px;" placeholder="발급종료일">
                <a class="calendar datepicker" style="cursor: pointer;"></a>
            </div>
            <div class="right">
                <input type="text" class="search_word" name="couponName" placeholder="쿠폰명 검색" value="${search.couponName}" />
                <a onclick="javascript:doSearch()" class="search_btn" id="btnSearch">검색</a>
                <a onclick="javascript:doReset()" class="search_btn">초기화</a>
            </div>
        </div>

        <div style="width: 100%; overflow-x: auto;">
            <table class="inquiry" style="white-space: nowrap; border-collapse: collapse;">
                <thead>
                <tr>
                    <th>No</th>
                    <th>제휴사명</th>
                    <th>쿠폰명</th>
                    <th>할인방식</th>
                    <th>할인값</th>
                    <th>발행수량</th>
                    <th>사용수량</th>
                    <th>유효기간</th>
                    <th>상태</th>
                    <th>등록일</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="m" items="${list}" varStatus="ss">
                    <tr class="detail" onclick="javascript:goDetail('${m.couponSeq}')">
                        <td>${m.couponSeq}</td>
                        <td>${m.partnerName}</td>
                        <td style="text-align: left; padding-left: 10px;">${m.couponName}</td>
                        <td>${m.discountType == 'FIXED' ? '정액' : '정률'}</td>
                        <td><fmt:formatNumber value="${m.discountValue}" /> ${m.discountType == 'FIXED' ? '원' : '%'}</td>
                        <td><fmt:formatNumber value="${m.issueCount}" /></td>
                        <td><b style="color:red;"><fmt:formatNumber value="${m.usedCount}" /></b></td>
                        <td>${fn:substring(m.startDate, 0, 10)} ~ ${fn:substring(m.endDate, 0, 10)}</td>
                        <td>${m.useYn == 'Y' ? '활성' : '중지'}</td>
                        <td><fmt:formatDate value="${m.regDate}" pattern="yyyy-MM-dd" /></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty list}">
                    <tr><td colspan="10" style="height: 118px;">발급된 쿠폰 내역이 없습니다.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
        <%@ include file="/WEB-INF/jsp/common/inc-paging-admin.jsp"%>
    </div>
</form>

<script type="text/javascript">
    $(document).ready(function() {
        // 기존 스타일 유지: 마우스 포인터 표시
        $('td').css('cursor', 'pointer');
    });

    function openIssuePopup() {
        // 기존 시스템에서 사용하던 colorbox 활용
        $.colorbox({
            href: "./couponIssuePopup.do",
            width: "600px",
            height: "550px",
            overlayClose: false,
            iframe: true
        });
    }

    function doSearch() { $('#couponForm').submit(); }
    function doReset() { $.Nav('self', null, null, true); }
</script>