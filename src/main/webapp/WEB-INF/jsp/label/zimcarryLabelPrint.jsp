<script src="/common/label/jquery-3.6.3.min.js"></script>
<script src="/common/label/jquery-ui.js"></script>
<script src="/common/label/jquery-util.js"></script>
<script src="/common/label/JsBarcode.all.min.js"></script>
<script src="/common/label/qrcode.min.js"></script>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!--<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scale=no" />-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script th:src="@{/js/jquery-3.6.3.min.js}"                     type="text/javascript" ></script>
    <script th:src="@{/js/jquery-ui.js}"></script>
    <script th:src="@{/js/jquery-util.js}"                          type="text/javascript" ></script>
    <script th:src="@{/js/JsBarcode.all.min.js}"></script>
    <script th:src="@{/js/qrcode.min.js}"></script>
    <title>라벨지</title>
    <style>
        /* Edge에서 여백 설정 */
        @page {
            margin: -3mm 0mm; /* 기본 페이지 여백을 0으로 설정 */
        }
        body {
            margin: 0mm 0mm; /* 상하 여백 0mm, 좌우 여백 4mm */
        }
        .overflow-text
        {
            overflow: hidden;
        }
        @media print {
            .page-break {
                page-break-before: always;
            }
        }
    </style>
</head>
<script type="text/javascript">
    window.onload = function() {

        var trmText = '';
        var data = localStorage.getItem("data");

        //console.debug("data: " + data);
        document.getElementById('divBody').innerHTML = data;

        //바코드 생성
        var inputs = $("input[name='prodCode']");
        var svgs = $("svg[name='barcode']");

        inputs.each(function(index) {
            var barcodeValue = $(this).val();
            var svgElement = svgs.get(index); // index를 통해 각 svg 요소를 가져옴

            JsBarcode(svgElement, barcodeValue, {
                format: "CODE128",
                displayValue: false,
                fontOptions: "bold", // 택스트를 굵게 지정
                fontSize: 18,
                height: 45, // 바코드 높이를 50으로 설정
                textMargin: 1 // 바코드와 텍스트 사이의 간격을 5로 설정
            });
        });
        //QR코드 생성
        // 동일한 name 속성을 가진 input 및 div 태그들을 찾아 각 QR 코드를 생성
        var inputs = $("input[name='prodCode']");
        var divs1 = $("div[name='qrcode1']");
        var divs2 = $("div[name='qrcode2']");

        inputs.each(function(index) {
            var qrValue = $(this).val();
            var qrDiv1 = divs1.get(index); // index를 통해 각 div 요소를 가져옴

            new QRCode(qrDiv1, {
                text: qrValue,
                width: 50,
                height: 50,
                correctLevel: QRCode.CorrectLevel.H
            });
        });

        inputs.each(function(index) {
            var qrValue = $(this).val();
            var qrDiv2 = divs2.get(index); // index를 통해 각 div 요소를 가져옴

            new QRCode(qrDiv2, {
                text: qrValue,
                width: 50,
                height: 50,
                correctLevel: QRCode.CorrectLevel.H
            });
        });

        //감열부분 설정
        var trmNames = $("input[name='trmName']");
        var trmDivs = $("div[name='terBox']");
        trmNames.each(function(index) {
            var trmName = $(this).val();
            //alert(index +": " + trmName);
            var trmDiv = trmDivs.get(index); // index를 통해 각 div 요소를 가져옴
            trmText = setTrmBox(trmName);
            trmDiv.innerHTML = trmText;
        });

        // 이미지 렌더링 완료 후 출력
        waitForImagesToLoad(() => {
            // 약간의 렌더 안정화 시간(0.3초) 후 출력
            setTimeout(() => window.print(), 300);
        });
    }

    // 이미지 로딩 체크 함수
    function waitForImagesToLoad(callback) {
        const imgs = Array.from(document.images);
        if (imgs.length === 0) {
            callback();
            return;
        }

        let loaded = 0;
        imgs.forEach(img => {
            if (img.complete) {
                loaded++;
                if (loaded === imgs.length) callback();
            } else {
                img.addEventListener("load", () => {
                    loaded++;
                    if (loaded === imgs.length) callback();
                });
                img.addEventListener("error", () => {
                    loaded++; // 로드 실패한 이미지도 카운트
                    if (loaded === imgs.length) callback();
                });
            }
        });
    }

    //감열부분 설정
    function setTrmBox(trmName) {

        let trmBoxCode = '';
        switch (trmName) {
            case '고촌':		//고촌
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '군포':		//군포
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '화성':		//화성
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '칠곡':		//칠곡
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '양산':		//양산
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '장성':		//장성
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">장성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">인천</li>';
                trmBoxCode += '</ul>';
                break;
            case '인천':		//인천
                trmBoxCode += '<ul style="height: 100%; list-style: none; padding:0; margin:0;">';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">고촌</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">군포</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">화성</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">칠곡</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">양산</li>';
                trmBoxCode += '<li style="height:calc(100% / 7); text-indent: -9999px; background:rgba(0, 0, 0, 0.7);">장성</li>';
                trmBoxCode += '<li class="active" style="height:calc(100% / 7); text-indent: -9999px; background: none;">인천</li>';
                trmBoxCode += '</ul>';
                break;
        }

        return trmBoxCode;
    }

</script>
<body style="margin: 0;">
<div id="divBody" style="margin: 0;">

</div>
</body>
</html>