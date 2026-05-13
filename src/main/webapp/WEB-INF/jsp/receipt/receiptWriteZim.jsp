<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<%@ include file="/WEB-INF/include/admin_start_html.jsp"%>

<div class="contents">
    <form action="${pageContext.request.contextPath}/receipt/partnerReceiptProc.do" method="post" id="zimForm">
        <input type="hidden" name="partner" value="zimcarry_111" />
        <input type="hidden" name="delKind" value="1" />

        <%-- [1] 보내는 분: 타이틀(p.type)과 테이블 박스 전체를 히든 처리 --%>
        <div id="original">
            <div class="receiver" style="display:none;">
                <%-- 제목도 가려야 하므로 p태그를 이 안으로 이동하거나 제거 --%>
                <input type="hidden" name="list[0].cusSendNm" value="아이콘"/>
                <input type="hidden" name="list[0].brStrNm" value="김포고촌토탈운송서비스"/>
                <input type="hidden" name="list[0].brStrCode" value="02138"/>
                <input type="hidden" name="list[0].cusSendZip" value="10136">
                <input type="hidden" name="list[0].cusSendAddr1" value="경기도 김포시 고촌읍 전호리 745">
                <input type="hidden" name="list[0].cusSendAddr2" value=" ">
                <input type="hidden" name="list[0].srType" value="S"/>
                <input type="hidden" name="list[0].cusSendTelnum" id="real_cusSendTelnum" value="">
                <input type="hidden" name="list[0].rcpKind" value="4"/>
            </div>

            <%-- [2] 받는 분 섹션: 여기에 '운임체계'를 통합하여 노출 --%>
            <div class="sender">
                <p class="type">접수 정보 <span class="red">*</span><span>표시는 필수 입력사항입니다</span></p>
                <div class="tableBox">
                    <table>
                        <%-- 운임체계만 유일하게 노출되는 설정 항목 --%>
                        <tr>
                            <th>운임체계</th>
                            <td>
                                <select id="fareType" name="fareType" class="w265">
                                    <option value="1">인천공항 출발</option>
                                    <option value="2">인천공항 도착</option>
                                    <option value="3">운임체계 3</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <th>받는 분 이름 <span class="red">*</span></th>
                            <td><input type="text" name="list[0].cusReNm" id="cusReNm" class="notNull w410" data-name="이름"/></td>
                        </tr>
                        <tr>
                            <th>연락처 <span class="red">*</span></th>
                            <td>
                                <select role="ephone2" class="phone">
                                    <c:forEach var="list" items="${phoneList}">
                                        <option value="${list}">${list}</option>
                                    </c:forEach>
                                </select> -
                                <input type="tel" role="ephone2" class="phone onlyNumber" maxlength="4" /> -
                                <input type="tel" role="ephone2" class="phone onlyNumber" maxlength="4" />
                                <input type="hidden" role="ephone2" name="list[0].cusReTelnum2">
                            </td>
                        </tr>
                        <tr>
                            <th>골프장 상호</th>
                            <td>
                                <input type="text" role="golfSearch2" name="list[0].recvGolfName" class="w410" readonly="readonly"/>
                                <input type="hidden" role="golfSearch2" name="list[0].recvGolfSeq" value="0"/>
                                <a href="javascript:;" role="golfSearch2" class="btn">찾기</a>
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
                            <td><input type="text" role="zip2" name="list[0].cusReAddr2" class="w586" placeholder="상세주소를 입력해 주세요" data-name="상세주소"/></td>
                        </tr>
                        <%-- 도착영업소 정보는 내부적으로만 관리 --%>
                        <tr style="display:none;">
                            <td colspan="2">
                                <input type="hidden" id="office2" name="list[0].brEndNm" value="도착지 확인필요"/>
                                <input type="hidden" id="officeCode2" name="list[0].brEndCode" value="00000"/>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

            <%-- [3] 물품정보 --%>
            <div class="product_info">
                <p class="type">물품정보</p>
                <div class="tableBox">
                    <table>
                        <tr>
                            <th>접수일 / 픽업희망일</th>
                            <td>
                                <input type="text" name="gameDt" id="gameDt" class="notNull input" style="width:90px;" readonly="readonly"/> /
                                <input type="text" name="list[0].picReqDt" id="picReqDt" class="notNull input" style="width:90px;" readonly="readonly"/>
                            </td>
                        </tr>
                        <tr>
                            <th>품목명 <span class="red">*</span></th>
                            <td>
                                <select name="list[0].pdNm" id="zimcarry_pdNm" class="notNull w265" data-name="품목명">
                                    <option value="">선택하세요</option>
                                    <c:forEach var="item" items="${iconGolfItems}">
                                        <option value="${item.itemName}">${item.itemName}</option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>

                        <tr id="row_pdCnt" style="display:none;">
                            <th>골프백 수량 <span class="red">*</span></th>
                            <td>
                                <input type="text" name="list[0].pdCnt" id="pdCnt0" class="w265 input onlyNumber" value="0"/><span class="unit">개</span>
                                <input type="hidden" name="list[0].pdIndivPrice" value="200"/>
                            </td>
                        </tr>
                        <tr id="row_carrierCnt" style="display:none;">
                            <th>캐리어 수량 <span class="red">*</span></th>
                            <td><input type="text" name="list[0].carrierCnt" id="carrierCnt0" class="w265 input onlyNumber" value="0"/><span class="unit">개</span></td>
                        </tr>
                        <tr id="row_bostonCnt" style="display:none;">
                            <th>보스턴백 수량 <span class="red">*</span></th>
                            <td><input type="text" name="list[0].bostonCnt" id="bostonCnt0" class="w265 input onlyNumber" value="0"/><span class="unit">개</span></td>
                        </tr>

                        <%-- 백엔드 필수 파라미터 히든 세팅 --%>
                        <tr style="display:none;">
                            <td colspan="2">
                                <input type="hidden" name="list[0].pdPack" value="편도"/>
                                <input type="hidden" name="list[0].payType" value="0"/>
                                <input type="hidden" name="list[0].pdGolfCnt" value="0"/>
                                <input type="hidden" name="list[0].pdEtcPay" value="0"/>
                                <input type="hidden" name="list[0].pdDosunPay" value="0"/>
                                <input type="hidden" name="list[0].pdDeliPay" value="0"/>
                                <input type="hidden" name="list[0].iconFee" value="0"/>
                            </td>
                        </tr>

                        <tr>
                            <th>총 합계</th>
                            <td>
                                <input type="text" name="list[0].pdPay" id="pdPay" class="w265 input" readonly="readonly" value="0" style="font-weight:bold; color:blue;"/><span class="unit">원</span>
                            </td>
                        </tr>
                        <tr>
                            <th class="memo">메모</th>
                            <td><textarea name="list[0].pdMemo" cols="30" rows="5"></textarea></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </form>
    <div class="receipt"><a href="javascript:;" id="btnZimAdd" class="btn acen">접수완료</a></div>
</div>

<script type="text/javascript">
    var gContextPath = "${pageContext.request.contextPath}";
    if(gContextPath === "/") gContextPath = "";

    var iconGolfItems = {
        <c:forEach var="item" items="${iconGolfItems}" varStatus="status">
        "${item.itemName}": { "price": ${item.price} }${!status.last ? ',' : ''}
        </c:forEach>
    };

    $(document).ready(function(){
        // 날짜 고정
        var todayStr = new Date().toISOString().substring(0, 10);
        $("#gameDt, #picReqDt").val(todayStr);

        // 연락처 실시간 동기화 (받는분 -> 보내는분 히든필드)
        $(document).on("input change", "[role='ephone2']", function() {
            var parts = [];
            $("[role='ephone2']").not("input[type=hidden]").each(function() {
                if($(this).val()) parts.push($(this).val());
            });
            $("#real_cusSendTelnum").val(parts.join("-"));
        });

        // 품목 선택 시 입력칸 제어
        $("#zimcarry_pdNm").on("change", function() {
            var val = $(this).val();
            $("#row_pdCnt, #row_carrierCnt, #row_bostonCnt").hide().find("input").val(0);
            if(val === "인천공항골프백") $("#row_pdCnt").show();
            else if(val === "인천공항캐리어") $("#row_carrierCnt").show();
            else if(val === "인천공항보스턴백") $("#row_bostonCnt").show();
            searchIslZimcarry();
        });

        // 수량 변경 감시
        $(document).on("propertychange change keyup paste input", "#pdCnt0, #bostonCnt0, #carrierCnt0", function() {
            searchIslZimcarry();
        });

        // 골프장 검색 (절대 경로)
        $("[role^=golfSearch]").click(function(){
            $.colorbox({
                href : gContextPath + "/receipt/receiptGolfLayer.do?type1=" + $(this).attr("role"),
                overlayClose : false, width : 1200, height : 740, iframe: true
            });
        });

        // 최종 접수
        $("#btnZimAdd").click(function(){
            var cnt = Number($("#pdCnt0").val()) + Number($("#bostonCnt0").val()) + Number($("#carrierCnt0").val());
            if(cnt < 1) { alert("품목 수량을 입력해주세요."); return; }

            var valid = true;
            $(".notNull:visible").each(function(){
                if(!$.trim($(this).val())){
                    alert($(this).attr("data-name") + " 항목을 입력해 주세요.");
                    $(this).focus();
                    valid = false; return false;
                }
            });

            if(valid && confirm("접수하시겠습니까?")){
                $(window).off("beforeunload");

                // 전화번호 히든필드 조립
                var parts = [];
                $("[role='ephone2']").not("input[type=hidden]").each(function() {
                    if($(this).val()) parts.push($(this).val());
                });
                $("input[role='ephone2'][type=hidden]").val(parts.join("-"));

                // 이름 자동 보정
                var nameInput = $("#cusReNm");
                if(nameInput.val().indexOf("아이콘") === -1) nameInput.val("아이콘 " + nameInput.val());

                // 금액 콤마 제거
                $("#pdPay").val($("#pdPay").val().replace(/,/g, ''));
                $("#zimForm").submit();
            }
        });

        $(window).on("beforeunload", function(){ return "페이지를 벗어나면 입력 내용이 사라집니다."; });
    });

    function searchIslZimcarry(){
        var itemName = $("#zimcarry_pdNm").val();
        var unitPrice = (iconGolfItems[itemName]) ? iconGolfItems[itemName].price : 0;
        var total_cnt = parseInt($("#pdCnt0").val() || 0) + parseInt($("#bostonCnt0").val() || 0) + parseInt($("#carrierCnt0").val() || 0);

        var isJeju = ($("input[role=zip2]:eq(1)").val() || "").indexOf("제주") > -1;
        var totalDeliPay = unitPrice * total_cnt;
        var totalDosunPay = isJeju ? (5000 * total_cnt) : 0;

        $("#pdPay").val(comma(totalDeliPay + totalDosunPay));
        $("input[name='list[0].pdDeliPay']").val(totalDeliPay);
        $("input[name='list[0].pdDosunPay']").val(totalDosunPay);
        $("input[name='list[0].recvIslYn']").val(isJeju ? "Y" : "N");
    }

    function searchPostcode(roleName) {
        new daum.Postcode({
            oncomplete: function(data) {
                var addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;
                $("input[role="+roleName+"]:eq(0)").val(data.zonecode);
                $("input[role="+roleName+"]:eq(1)").val(addr);
                searchIslZimcarry();
            }
        }).open();
    }

    function searchIsl() {
        console.log("팝업에서 호출됨: 짐캐리 운임 계산 시작");
        searchIslZimcarry();
    }

    // 이전에 추가한 수수료 관련 더미 함수들도 잘 있는지 확인하세요!
    function setIconFee(type, iconFeeArr) { }
    function initIconFee(type) { }

    function setIconFee(type, iconFeeArr) {
        // 짐캐리는 별도 수수료 계산이 필요 없으므로 로직을 비워둡니다.
        console.log("짐캐리 페이지: 수수료 설정 스킵 (" + type + ")");
    }

    // 혹시 모르니 초기화 함수도 같이 넣어주세요.
    function initIconFee(type) {
        console.log("짐캐리 페이지: 수수료 초기화 스킵 (" + type + ")");
    }

    function comma(x) { return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","); }
</script>