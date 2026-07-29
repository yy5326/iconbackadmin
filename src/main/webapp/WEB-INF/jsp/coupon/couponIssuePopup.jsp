<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>제휴사 쿠폰 대량 발급</title>

    <link rel="stylesheet" href="/common/css/common.css" />
    <link rel="stylesheet" href="/common/css/reset.css" />
    <link rel="stylesheet" href="/common/css/base.css" />
    <link rel="stylesheet" href="/common/css/style.css" />
    <link rel="stylesheet" href="/common/SlickGrid-2.4.19/css/smoothness/jquery-ui.css" type="text/css"/>

    <script type="text/javascript" src="/common/js/jquery/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="/common/js/jquery/jquery-ui-1.10.3.custom.min.js"></script>
    <script type="text/javascript" src="/common/js/jquery/jquery.ui.datepicker.custom.js"></script>
</head>
<body style="background: #fff;"> <div class="pop-conts" style="padding:20px;">
    <div class="layerT">
        <p class="ctxt">제휴사 쿠폰 대량 발급</p>
    </div>

    <div class="layerCont layerCont01">
        <table class="writeTable">
            <colgroup>
                <col width="30%">
                <col width="*">
            </colgroup>
            <tr>
                <th>제휴업체</th>
                <td>
                    <select id="popPartnerSeq" style="width:100%;">
                        <option value="1">SG골프</option>
                        <option value="2">카카오골프</option>
                        <option value="3">스마트스코어</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>쿠폰명</th>
                <td><input type="text" id="popCouponName" style="width:100%;" placeholder="예: [A제휴사] 전용 1만원 할인"></td>
            </tr>
            <tr>
                <th>할인 방식</th>
                <td>
                    <input type="radio" name="popDiscountType" value="FIXED" checked> 정액(원)
                    <input type="radio" name="popDiscountType" value="PERCENT" style="margin-left:10px;"> 정률(%)
                </td>
            </tr>
            <tr>
                <th>할인값</th>
                <td><input type="number" id="popDiscountValue" style="width:100px;"> <span id="unitText">원/ %</span></td>
            </tr>
            <tr>
                <th>발행 수량</th>
                <td><input type="number" id="popIssueCount" style="width:100px;" value="100"> 매</td>
            </tr>
            <tr>
                <th>유효 기간</th>
                <td>
                    <input type="text" id="popStartDate" class="input datepicker" style="width:90px;"> ~
                    <input type="text" id="popEndDate" class="input datepicker" style="width:90px;">
                </td>
            </tr>
        </table>
    </div>

    <div class="layer_btn layer_btn02" style="margin-top:20px; text-align:center;">
        <a href="javascript:void(0);" onclick="fnIssueCoupon();" class="btn acen" style="background:#7d448d; border:none; color:#fff;">쿠폰 생성 및 발급</a>
        <a href="javascript:parent.$.colorbox.close();" class="btn acen" style="margin-left: 5px;">닫기</a>
    </div>
</div>

<script>
    $(document).ready(function() {
        // 팝업이 로드될 때 달력(Datepicker) 기능 활성화
        if($('.datepicker').length > 0) {
            $('.datepicker').datepicker();
        }
    });

    function fnIssueCoupon() {
        // 필수값 체크
        if(!$('#popCouponName').val()) { alert('쿠폰명을 입력해주세요.'); return; }
        if(!$('#popDiscountValue').val()) { alert('할인값을 입력해주세요.'); return; }
        if($('#popIssueCount').val() <= 0) { alert('발행 수량을 확인해주세요.'); return; }
        if(!$('#popStartDate').val() || !$('#popEndDate').val()) { alert('유효기간을 선택해주세요.'); return; }

        if(confirm("지정한 수량만큼 난수 쿠폰이 생성됩니다.\n진행하시겠습니까?")) {
            var param = {
                partnerSeq: $('#popPartnerSeq').val(),
                partnerName: $('#popPartnerSeq option:selected').text(),
                couponName: $('#popCouponName').val(),
                discountType: $('input[name=popDiscountType]:checked').val(),
                discountValue: $('#popDiscountValue').val(),
                issueCount: $('#popIssueCount').val(),
                startDate: $('#popStartDate').val(),
                endDate: $('#popEndDate').val()
            };

            $.ajax({
                type: "POST",
                url: "/coupon/issueBulk.do", // 변경된 컨트롤러 경로
                contentType: "application/json",
                data: JSON.stringify(param),
                success: function(data) {
                    if(data == "success") {
                        alert("성공적으로 발급되었습니다.");
                        parent.location.reload(); // 부모창 새로고침 (목록 갱신)
                    } else {
                        alert("발급 중 오류가 발생했습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    alert("서버 통신 에러가 발생했습니다.");
                }
            });
        }
    }
</script>
</body>
</html>