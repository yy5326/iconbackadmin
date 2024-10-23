﻿/**
* first coded date : 2010.09
* coded by : @wimy1, wimy1@hanmail.net
* description : 일반화 처리를 위한 jquery를 이용한 common.js
*			: common.js와 같이 사용해야 합니다.
*			: 자유롭게 변경해서 사용가능하나, 이 설명 내용을 제거하지 마십시오.
*			: 가급적이면 변경 이력을 추가해서 사용하십시오.
*			: 이 코드들이 제대로 작동하지 않을 경우, 페이지의 하단에 추가해 보십시오.
*
*	2016.01.27 : lib.js와 같이 사용하도록 변경
*
*/

//전역 : 모바일 브라우저 여부
var __isMobile = false;
// 전역 : 읽기전용 필드 background-color 처리
var __readonlyColor = '#dadada';


/**
 * onload
 */
$(document).ready(function() {
	_initPageLoad();
});


/**
 * 골프택배 : 권한 체크
 * @param type - c: 등록권한 체크, ud: 수정/삭제권한 체크
 * @returns json : 결과 참조
 */
function hasAuth(type) {
	var jsonAuth = {};

	$.ajax({
		url: '/common/hasAuth_json.do'
		, async: false
		, type: 'post'
		, dataType: 'json'
		, data: {
			url: location.pathname
			, type: type
		}
		, success: function (data) {
			//console.log(data)
			jsonAuth = data;
		}
		, error: function (data) {
			alert("error ajax :\n" + data.responseText);
		}
	});

	var isStop = false;
	if (!jsonAuth.hasSession) {
		isStop = true;
		alert('로그인 정보가 만료되었습니다.\n로그인 페이지로 이동합니다.');
		top.location.href = '/login/login.do';
	}

	if (jsonAuth.hasSession && !jsonAuth.hasAuth) {
		alert(jsonAuth.authTypeName + '이 없습니다.');
		isStop = true;
	}

	return isStop;
}

/**
 * 골프택배 - 출금 CMS 생성 권한 체크
 * @param payTpCd : 출금유형코드 [0041]
 */
function hasCMSAuth(payTpCd) {
	var jsonAuth = {};

	$.ajax({
		url: '/common/hasCMSAuth_json.do'
		, async: false
		, type: 'post'
		, dataType: 'json'
		, data: {
			url: location.pathname
			, pTpCd: payTpCd
		}
		, success: function (data) {
			console.log(data)
			jsonAuth = data;
		}
		, error: function (data) {
			alert("error ajax :\n" + data.responseText);
		}
	});

	var isStop = false;
	if (!jsonAuth.hasSession) {
		isStop = true;
		alert('로그인 정보가 만료되었습니다.\n로그인 페이지로 이동합니다.');
		top.location.href = '/login/login.do';
	}

	if (jsonAuth.hasSession) {
		if (jsonAuth.appYn == 'Y') {
			alert('이미 승인 처리된 요청입니다.');
			isStop = true;
		}

		if (!jsonAuth.hasCMSAuth) {
			alert('[' + jsonAuth.payTpCdName + '] CMS 생성일이 아니거나, 권한이 없습니다.');
			isStop = true;
		}
	}

	return isStop;
}


/**
 * 골프택배 - 출금 CMS 생성 권한 체크
 * - 해당 출금유형의 담당자인지만 체크
 * @param payTpCd : 출금유형코드 [0041]
 * @return boolean - true: 권함있음, false: 권한없음
 */
function hasCmsAuthUser(payTpCd) {
	var jsonAuth = {};

	$.ajax({
		url: '/common/hasCMSAuth_json.do'
		, async: false
		, type: 'post'
		, dataType: 'json'
		, data: {
			url: location.pathname
			, pTpCd: payTpCd
			, authTp : '1'
		}
		, success: function (data) {
			console.log(data)
			jsonAuth = data;
		}
		, error: function (data) {
			alert("error ajax :\n" + data.responseText);
		}
	});

	var hasAuth = false;

	if (jsonAuth.hasSession) {
		if (jsonAuth.hasCMSAuth) {
			hasAuth = true;
		}
	}

	return hasAuth;
}


/**
 * 골프택배 :	리스트 헤더 클릭 정렬
 * 	- 헤더 정렬 테이블 : orderingHeader 클래스 추가
 *  - 리스트 컬럼 개수 동적 변경으로 인해 중첩 루프로 변경
 * @param ho : 정렬 파라미터 ${search.ho};
 * @param mapHeader : 헤더 매핑 json
 */
function bindOrderingHeader(ho, mapHeader) {
	var sHeaderOrder = ho;
	sHeaderOrder = $.trim(sHeaderOrder.toLowerCase());
	var arrOrder = ['', ''];
	if (sHeaderOrder.length > 0) {
		if (sHeaderOrder.indexOf(' ') > -1)
			arrOrder = sHeaderOrder.split(' ');
		else {
			arrOrder[0] = sHeaderOrder;
			arrOrder[1] = 'asc';
		}
	}

	//mapHeader = [{"선택": ""}, {"상태": "status_tp_cd"}, {"법인": "corp_seq"}, {"지점": "branch_tp_cd"}, {"차량번호": "car_number"}
	//, {"차대번호": "vin"}, {"형식": "forklift_type_tp_cd"}, {"톤수": "weight_tp_cd"}, {"연식": "make_year", "default": "desc"}, {"등록일": "reg_date", "default": "desc"}
	//, {"검사만료일": "inspect_dt"}];

	var $oh = $('.orderingHeader');
	if ($oh.length == 0)
		return;

	if ($oh.find('thead th').length > mapHeader.length) {
		alert('목록 헤더 개수가 mapHeader 개수보다 큽니다.');
		return;
	}

	$oh.find('thead th').each(function(i) {
		var $th = $(this);
		var sText = $.trim($(this).text());

		$.each(mapHeader, function(i, el) {
			//빈 값 스킵 : 체크박스 등
			if (el[sText] == '')
				return true;

			var sPropName = Object.getOwnPropertyNames(el)[0];
			if (sText == sPropName) {
				var $anc = $('<a onclick="javascript:changeOrderingHeader(this);" column="' + el[sText] + '" style="cursor: pointer;">' + sText  + '</a>');
				//기본 정렬 값
				if (el['default'] != null)
					$anc.attr('default', el['default']);

				//정렬 조건이 get파라미터에 있을 경우 해당 정렬에 대한 화살표 아이콘 표시
				if (el[sText] === arrOrder[0]) {
					var $spanLayout = $('<span class="align_arr mL3">');
					if (arrOrder[1] === 'asc')
						$spanLayout.append('<a href="javascript:;" class="align_up">');
					else if (arrOrder[1] === 'desc')
						$spanLayout.append('<a href="javascript:;" class="align_down">');

					$anc.append($spanLayout);
					$anc.attr('order', arrOrder[1]);		//현재 정렬 방향
				}
				$th.empty().append($anc);
			}
		});
	});
	// ordering arrows
	//<span class="align_arr mL3"><a href="" class="align_up"></a><a href="" class="align_down"></a></span>
}

/**
 * 골프택배 : 정렬 헤더 클릭 이벤트
 * 	- jquery.locator 필요
 * @param obj
 */
function changeOrderingHeader(obj) {
	var sOrder = $(obj).attr('order') || '';
	var sDefault = $(obj).attr('default') || '';

	if (sOrder === '' && sDefault === 'desc')
		sOrder = 'asc';

	if (sOrder === '')
		sOrder = 'asc';
	else if (sOrder === 'asc')
		sOrder = 'desc';
	else if (sOrder === 'desc')
		sOrder = 'asc';

	var sHo = $(obj).attr('column');
	sHo += ' ' + sOrder;
	$.Nav('self', {ho: sHo});
}

/**
 * 골프택배 : 다음 주소찾기
 * <script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script> 필요
 * @param zipCodeName 우편번호 input name
 * @param addr1Name 주소1 input name
 * @param addr2Name 주소2 input name (null 가능)
 */
function openZipCode(zipCodeName, addr1Name, addr2Name) {
	if ($('input[name=' + zipCodeName + ']').length == 0) {
		alert('올바른 엘리먼트 이름을 지정해 주세요.');
		return;
	}

	if ($('input[name=' + addr1Name + ']').length == 0) {
		alert('올바른 엘리먼트 이름을 지정해 주세요.');
		return;
	}

	new daum.Postcode({
		oncomplete : function(data) {
			$('input[name=' + zipCodeName + ']').val(data.zonecode);
			$('input[name=' + addr1Name + ']').val(data.address);
			if (addr2Name != null) {
				$('input[name=' + addr2Name + ']').focus();
			}
		}
	}).open();
}

/**
 * 골프택배 : 파일 다운로드
 * @param physicalName 저장 파일 (디렉토리 경로 포함)
 * @param fileName 원본 파일명 (다운로드될 파일명)
 */
function downloadFile(physicalName, fileName) {
	//전역 설정의 UPLOAD_ROOT
	var UPLOAD_ROOT = '/upload';

	$.ajax({
		url: '/common/file_exist_json.do'
		, async: false
		, type: 'post'
		, dataType: 'json'
		, data: {
			filePath: UPLOAD_ROOT + physicalName
		}
		, success: function (data) {
			if (data.isFile) {
				var sUrl = '/common/download.do?filename=' + encodeURIComponent(physicalName) + '&originalName=' + encodeURIComponent(fileName);
				location.href = sUrl;
			}
			else
				alert('파일이 존재하지 않습니다.');
		}
		, error: function (data) {
			console.log(data);
			alert("error ajax :\n" + data.responseText);
		}
	});
}

/**
 * 압축 일괄 다운로드 폼 서브밋
 * @param files : 물리경로파일명 (kde_file.physical_name) : 파이프라인(|)을 구분자로 가지는 배열형 문자열
 * @param names : 파일명 (kde_file.file_name) : 파이프라인(|)을 구분자로 가지는 배열형 문자열
 */
function downloadAllZip(files, names) {
	var sFormId = 'zipDownloadForm';
	if ($('#' + sFormId).length > 0)
		$('#' + sFormId).remove();
	$('<form id="' + sFormId + '">').appendTo(document.body);
	var $form = $('#' + sFormId);
	$form.attr('action', '/common/downloadAll.do');
	$form.attr('method', 'post');
	$('<input name="files">').appendTo($form);
	$('<input name="names">').appendTo($form);

	$form.find('input[name=files]').val(files);
	$form.find('input[name=names]').val(names);
	$form.submit();
}

/**
 * 골프택배 : 파일 보기 json
 *  - 파일 존재 여부 판단 처리
 * @param filePath
 */
function viewFile(filePath) {
	$.ajax({
		url: '/common/file_exist_json.do'
		, async: false
		, type: 'post'
		, dataType: 'json'
		, data: {
			filePath: filePath
		}
		, success: function (data) {
			if (data.isFile)
				window.open(filePath, '_blank');
			else
				alert('파일이 존재하지 않습니다.');
		}
		, error: function (data) {
			console.log(data)
			alert("error ajax :\n" + data.responseText);
		}
	});
}


/**
 * 해당일자가 휴일/공휴일인경우 해당일자 이전의 가장 최근 근무일을 가져온다.
 *
 * ※일자는 YYYY-MM-DD 형식으로만 체크 가능함. 오류시 빈값 반환
 * ex) 12월25일(화)인 경우 공휴일이므로 12월24일(월)을 반환함
 *
 * lib.js > checkPattern 함수 사용
 */
function getWorkDay(checkDay){

	var workday = '';

	//YYYY-MM-DD형식만 가능
	if (checkPattern(checkDay, 'DATE')){

		$.ajax({
			url: '/common/getWorkDay_json.do'
			, async: false
			, type: 'post'
			, dataType: 'json'
			, data: {
				today: checkDay
			}
			, success: function (data) {
				if (data.workday != '')
					workday = data.workday;
			}
			, error: function (data) {
				//console.log("error getWorkDay :\n" + data.responseText);
			}
		});
	}

	return workday;
}

/**
 * check login
 * ********************************************************************************
 */
// 비동기 함수이므로 붙여서 쓸 것
// ajax 로그인 체크
// jquery 필요
// type : 'front', 'admin'
// return : true:로그인, false:로그인 아님
//function checkLoginAjax(type) {
//	$.ajax({
//		async : false
//		, url : '/pages/ajax/checkLogin.aspx'
//		, type : "post"
//		, dataType : 'text'
//		, data : {
//			checkType : type == null ? 'front' : type
//		}
//		, success : function(msg) {
//			var isLogin = eval(msg);
//			return isLogin;
//		}
//	});
//}
/** ********************************************************************************************* */


/**
 * 일반화 전처리 메서드 : 여러가지 일반화 기능이 들어있습니다. css class name 등으로 접근 가능합니다. 상세 내역은 각 기능별
 * 항목별 단일 주석을 참조하십시오.
 * 		- ※ ajax 등을 이용하여 비동기로 추가 로드한 HTML 엘리먼트들에 전처리가 필요한 요소들이 포함되어 있을 경우 ajax 콜백에서 이 메서드를 별도로 실행해준다.
 * navigator.userAgent.match(/android/i)
 * navigator.userAgent.match(/(iphone)|(ipod)|(ipad)/i)
 */
function _initPageLoad() {
	var arrKeywords = new Array('iPhone', 'iPod', 'BlackBerry', 'Android', 'Windows CE', 'LG', 'MOT', 'SAMSUNG', 'SonyEricsson');
	var isMobile = false;
	for ( var key in arrKeywords) {
		if (navigator.userAgent.indexOf(arrKeywords[key]) > -1) {
			isMobile = true;
			__isMobile = true;
			break;
		}
	}

	// 모바일 브라우저; URL 입력폼 숨김 스크롤
	if (isMobile) {
		setTimeout(function() {
			window.scrollTo(0, 1);
		}, 100);
	}

	// 웹 접근성을 적용할 경우 사용 금지
	// $('img, a').focusin(function () {
	// $(this).blur();
	// });

	// readonly 속성일 경우 색상 변경
	// 추가 : nochangebgcolor 클래스를 가지고 있으면 변경안함
	$('input, select, textarea').each(function() {
		if ($(this).attr('readonly') != null && !$(this).hasClass('nochangebgcolor')) {
			if ($(this).attr('readonly') == 'readonly' || $(this).attr('readonly') == true) {
				$(this).css({
					'background-color' : __readonlyColor
				});

				if ($(this).parent('span').parent('span.input').length = 1) {
					$(this).parent('span').parent('span.input').addClass('readonly');
				}
			}
		}
	});

	// html object cursor:pointer 속성 설정
	$('.mousePointer').each(function() {
		$(this).css({
			cursor : 'pointer'
		});
		// if ($(this).context.nodeName.toLowerCase() == "a")
		// $(this).css({ 'font-weight': 'bold' });
	});

	// cursor:pointer 속성 설정2 : img, a 태그에 onclick 이벤트가 있을 경우
	$('a, img').each(function() {
		if ($(this).attr('onclick') != null) {
			$(this).css({
				cursor : 'pointer'
			// , 'font-weight': 'bold'
			});
		}
	});

	// , input[type=image]
	// 이미지태그, 이미지 버튼 이미지 연결 오류시
	// alert($('img').length);
	$('.onerror').each(function() {
		// alert($(this).attr('complete'))
		var sText = '이미지가 없습니다.';
		$(this).error(function() {
			$(this).prop('alt', sText).prop('title', sText);
		});
	});

	// 단순 개발중 경고 이벤트
	$('.devAlert').css('cursor', 'pointer').click(function() {
		alert('구현중입니다.');
		event.preventDefault();
	});

	// 날짜 선택기 설정 - 캘린더 이미지는 텍스트폼 다음에 있어야 함
	// lib.js 필요 - getyyyyMMdd(), getDate(), getDateByjQueryDateFormat() 필요
	// maxDate, minDate 속성 지원
	$('.datepicker').each(function() {
		if ($(this).context.nodeName == "INPUT") {
			$(this).css({
				'ime-mode' : 'disabled', 'width' : '110px'
			});
			$(this).datepicker({
				showOtherMonths : true, showButtonPanel : true, selectOtherMonths : true, changeMonth : true, changeYear : true, option : {
					width : 600
				}, minDate : $(this).attr('minDate'), maxDate : $(this).attr('maxDate')
			});

			if ($(this).attr('maxDate') != null) {
				// 최대 날짜 등록
				var maxDate = toTimeObject(getyyyyMMdd(getDateByjQueryDateFormat($(this).attr('maxDate'), new Date())));
				$(this).data('maxDate', maxDate);
			}
		} else if ($(this).context.nodeName == "IMG" || $(this).context.nodeName == "A") {
			$(this).css({
				cursor : 'pointer'
			});
			$(this).click(function() {
				$(this).prev().focus();
			});
		}
	});

	// 날짜 범위 자동 설정
	// input.datepicker필요
	// lib.js 필요 - getyyyyMMdd(), getDate(), getDateByjQueryDateFormat() 필요
	// n개의 세트 지원
	// baseDay 속성 지원 : 기본값:오늘 (0).  ex) baseDay = 1이면 내일, -1이면 어제로 기본일 설정
	// range 속성 지원 : jquery datepicker의  '1y', '1m', '1d' 형식의 값. 설정하면 두 개의 날짜 선택기에 해당 기간 만큼의 날짜가 설정됨.
	// reverse 속성 지원 : range속성이 있을 경우만 사용 가능. 두 개의 날짜 선택기에 표시될 값을 뒤집어서 표시 : 기본값 (사용안함), true : 뒤집기
	if ($('input.datepicker').length > 0) {

		var objDatepickers = $('input.datepicker');
		var iFormCount = 1;
		if (objDatepickers.length > 2)
			iFormCount = objDatepickers.length / 2; // datepicker 세트수

		var objDateRange = $('.dateRange');
		var iDateRangeCount = objDateRange.length / iFormCount; // 날짜 범위 설정 컨트롤
																// 개수

		objDateRange.each(function(idx, elm) {
			$(this).click(function() {
				//alert('iFormCount : ' + iFormCount);
				//alert($(this).prevAll('input.datepicker:eq(0)').attr('id'));
				//alert($(this).prevAll('input.datepicker:eq(1)').attr('id'));
				try {
					//TODO : 주의 : 버그 있음
					//특정 datepicker세트 다음에 dateRange세트가 있고, 특정 datepicker세트 다음에  dateRange세트가 없을 경우 비정상 작동
					//	-> dateRange 전체 개수를 formCount 개수로 나눠서 datepicker로 할당하기 때문에 오류 가능성 있음.
					//		alert($(this).prevAll('input.datepicker:eq(0)').attr('id'));
					//		alert($(this).prevAll('input.datepicker:eq(1)').attr('id')); 로 바꿔야 함.
					if (iFormCount > 1) {
						var iCurrentIndex = Math.floor(idx / iDateRangeCount);
						var iMin = iCurrentIndex * 2 - 1;
						var iMax = iCurrentIndex * 2 + 2 - 1;
						//alert('iCurrentIndex : ' + iCurrentIndex + '\niMin : ' + iMin + "\niMax : " + iMax);
						if (iMin < 1) {
							iMax = 2;
							objDatepickers = $('input.datepicker:lt(' + iMax + ')');
						} else
							objDatepickers = $('input.datepicker:gt(' + iMin + '):lt(' + iMax + ')');
					}
//					if(iFormCount > 1) {
//
//					}

					var today = new Date();
					var baseDay = $(this).attr('baseDay');
					if (baseDay != null) {
						// 오늘 - 1 이 기본값
						today.setDate(today.getDate() + parseInt(baseDay, 10));
					}

					var range = $(this).attr('range');
					if (range == 'none') {
						objDatepickers.each(function(idxDate, elmDate) {
							$(this).val('');
						});
					} else {
						var startDate = getDateByjQueryDateFormat(range, today);
						//alert(startDate + "\n" + today);
						//alert(getyyyyMMdd(startDate) + "\n" + getyyyyMMdd(today));
						if(!eval($(this).attr('reverse'))) {
							$(objDatepickers[0]).val(getyyyyMMdd(startDate));
							$(objDatepickers[1]).val(getyyyyMMdd(today));
						}
						else {
							$(objDatepickers[0]).val(getyyyyMMdd(today));
							$(objDatepickers[1]).val(getyyyyMMdd(startDate));
						}
					}

					if ($(this).data('click') != null) {
						$('#' + $(this).data('click')).click();
					}
				} catch (e) {
					alert('datepicker 날짜 범위 설정에서 오류 발생\n' + e.message);
				}
			});
		});
	}

	// 우편번호 양식 설정 : 총 4개의 입력 양식을 기준으로 설정 (우편번호, 우편번호 찾기 버튼, 주소1, 주소2)
	// 한 화면에 N개의 우편번호 양식이 존재할 경우 : 우편번호 팝업에 인덱스 값을 넘김
	// 인덱스 값을 다시 보내므로 인덱스 값으로 바인딩할 부분을 맞춰서 매핑 처리해야함
	$('.inputZipCode').each(function(idx, elm) {

		var i = idx % 4;
		var iFormIndex = 0;

		// 우편번호
		if (i == 0 && $(this).context.nodeName == "INPUT") {
			$(this).attr('readonly', 'true');
			$(this).css({
				'background-color' : __readonlyColor
			});
			$(this).attr('maxlength', '7');

			$(this).click(function() {
				$(this).nextAll('img.inputZipCode').click();
			});
		}
		// 버튼
		else if (i == 1 && ($(this).context.nodeName == "IMG" || $(this).context.nodeName == "A" || $(this).attr('type') == "button")) {
			$(this).css({
				'cursor' : 'pointer'
			});
			$(this).click(function() {
				iFormIndex = Math.floor(idx / 4);
				// alert("주소 검색 팝업 열기"); 외부 함수 필요 - common.js
				if (openPop != null)
					// openPop("?formIndex=" + iFormIndex, "openZipCode");
					openPop('', 'openZipcode');
				else
					alert('주소창을 열기위한 코드가 필요합니다.');
			});
		}
		// 주소1
		else if (i == 2 && $(this).context.nodeName == "INPUT") {
			$(this).attr('readonly', 'true');
			$(this).css({
				'background-color' : __readonlyColor
			});
			$(this).click(function() {
				$(this).prevAll('img.inputZipCode').click();
			});
		} else if (i == 3 && $(this).context.nodeName == "INPUT") {
			$(this).css({
				'ime-mode' : 'active'
			});
		}
	});

	// 전화번호 양식 설정
	$('.inputPhone').each(function(idx, elm) {
		var objPhones = $('.inputPhone');
		var iFormSet = 1;
		var iDivisor = 3; // n개의 세트 구분용
		if (objPhones.length > iDivisor)
			iFormSet = objPhones.length / iDivisor;

		if ($(this).context.nodeName == "INPUT") {

			$(this).css({
				'ime-mode' : 'disabled'
			});
			$(this).attr('maxlength', ((idx % iDivisor) == 0) ? '3' : '4');

			if ($(this) != null) {
				$(this).keydown(function(event) {
					var iKeyCode = event.which;
					if (event.shiftKey)
						event.preventDefault();
					if ((iKeyCode < 48 || iKeyCode > 57) && (iKeyCode < 96 || iKeyCode > 105) && (iKeyCode < 37 || iKeyCode > 40) && iKeyCode != 8 && iKeyCode != 9 && iKeyCode != 13)
						event.preventDefault();
				});
			}

			// 두번째 입력 폼으로 이동
			if ((idx % iDivisor) == 0) {
				$(this).keyup(function() {
					if ($(this).val().length == 3)
						$($('.inputPhone')[idx + 1]).focus();
				});
			}
			// 세번째 입력폼으로 이동
			else if ((idx % iDivisor) == 1) {
				$(this).keyup(function() {
					if ($(this).val().length == 4)
						$($('.inputPhone')[idx + 1]).focus();
				});
			}
		}
	});

	// 이메일 입력 양식 설정 : (이메일 도메인 입력 폼, 이메일 도메인 선택 셀렉트 박스 엘리먼트 기준)
	$('input.inputEmail').each(function(idx) {
		$(this).css({
			'ime-mode' : 'disabled'
		});
	});

	// 이메일 입력 양식 설정2 - 셀렉트 박스
	$('select.inputEmail').each(function(idx) {
		var iFormSet = 2; // n개의 세트 구분용
		var objEmails = $('input.inputEmail');
		$(this).change(function() {
			if (objEmails.length == 0)
				return;

			if ($(this).val() == "") {
				$(objEmails[(idx * iFormSet) + 1]).val('');
				$(objEmails[(idx * iFormSet) + 1]).removeAttr('readonly');
				$(objEmails[(idx * iFormSet) + 1]).css({
					'background-color' : '#ffffff'
				});
				$(objEmails[(idx * iFormSet) + 1]).focus();
				// 프론트용 일부 조건 추가
				// if ($(objEmails[(idx * iFormSet) +
				// 1]).parent('span').parent('span.input').length = 1) {
				// $(objEmails[(idx * iFormSet) +
				// 1]).parent('span').parent('span.input').removeClass('readonly');
				// }
			} else {
				$(objEmails[(idx * iFormSet) + 1]).val($(this).val());
				$(objEmails[(idx * iFormSet) + 1]).attr('readonly', 'readonly');
				$(objEmails[(idx * iFormSet) + 1]).css({
					'background-color' : __readonlyColor
				});
				// 프론트용 일부 조건 추가
				// if ($(objEmails[(idx * iFormSet) +
				// 1]).parent('span').parent('span.input').length = 1) {
				// $(objEmails[(idx * iFormSet) +
				// 1]).parent('span').parent('span.input').addClass('readonly');
				// }
			}
		});
	});

	// 주민등록번호 입력양식(이름 포함) : 3개 입력 폼 필요
	$('input.inputRegistNumber').each(function(idx, elm) {
		if (idx == 0) {
			$(this).css('ime-mode', 'active');
			$(this).attr('maxlength', '20');
		} else {
			$(this).css('ime-mode', 'disabled');

			// 첫번째 주민번호 입력 폼
			if (idx == 1) {
				$(this).attr('maxlength', '6');
				$(this).keyup(function() {
					if ($(this).val().length == 6) // 포커스 자동이동
						$($('input.inputRegistNumber')[idx + 1]).focus();
				});
			}
			// 두번째 주민번호 입력 폼
			else if (idx == 2) {
				$(this).attr('maxlength', '7');
			}

			$(this).keydown(function(event) {
				var iKeyCode = event.which;
				if (event.shiftKey)
					event.preventDefault();
				if ((iKeyCode < 48 || iKeyCode > 57) && (iKeyCode < 96 || iKeyCode > 105) && (iKeyCode < 37 || iKeyCode > 40) && iKeyCode != 8 && iKeyCode != 9 && iKeyCode != 13)
					event.preventDefault();
			});
		}
	});

	// 일반 팝업 페이지 사이즈 자동 조절 : 브라우저마다 iAddWidth 값을 조절할 필요가 있음
	$('.popupResizeJ').each(function() {

		var iAddWidth = 20;
		// if ($(document.body).innerWidth() > $(this).width())
		// iAddWidth += $(this).width() - $(document.body).innerWidth();

		var iAddHeight = 100;

		var iWidth = 0;
		var iHeight = 0;

		if (iWidth == 0)
			iWidth = $(this).width() + iAddWidth;

		if (iHeight == 0)
			iHeight = $(this).height() + iAddHeight;

		window.resizeTo(iWidth, iHeight);

		var left = (screen.availWidth - iWidth) / 2;
		var top = (screen.availHeight - iHeight) / 2;

		window.moveTo(left, top);
	});

	// iframe 너비, 높이 자동 조절 : 로컬 리소스만 가능
	// 주의 : html DOCTYPE이 쿼크 모드일 경우 제대로 작동하지 않음
	$('iframe.autoIframe').each(function() {
		// $(this).width($(this).contents().width() + 10);
		// $(this).height($(this).contents().height());

		$(this).load(function() {
			$(this).width($(this).contents().width());
			$(this).height($(this).contents().height());
		});
	});

	// iframe 높이 자동 조절
	$('iframe.resizeIframe').each(function() {
		$(this).load(function() {
			$(this).height($(this).contents().height());
			//$(this).height($(this).contents().find(document.body).height());
		});
	});

	// 해당 클래스 하위 자식으로 존재하는 이미지(<img>)의 가로 사이즈를 조절 : 0.125배율로 축소
	// 이미지에 팝업 링크 포함시킴
	$('.editorChangeMode').ready(function() {
		var children = $('.editorChangeMode').children().find('img');
		children.each(function() {
			var iWidth = $(this).width();
			if (iWidth && iWidth > 630) {
				while (iWidth > 630) {
					iWidth = iWidth * 0.875;
				}

				$(this).width(iWidth);
			}
			$(this).css({
				'cursor' : 'pointer'
			});
			$(this).click(function() {
				// 프로젝트에서 사용되는 이미지 팝업 스크립트 적용
				openFileViewPop($(this).attr('src'), $(this).attr('src').substr($(this).attr('src').lastIndexOf("/") + 1));
			});
		});
	});

	// 검색 입력 폼에서 엔터키 입력시 지정된 검색 버튼 실행 처리 : 해당 객체에 이미 이벤트가 있으면 이 로직은 무시됨
	// 검색 필드 class에 searchWord가 있어야 함
	// 검색 버튼의 ID에는 btnSearch가 있어야 함
	// 텍스트 폼에 validate 속성이 있을 경우, 빈 공백으로 엔터키 입력시 해당 속성값을 경고창으로 띄움
	$('input.searchWord').each(function() {
		// 텍스트 폼 일 경우

		var btnObj = $('input[id*=btnSearch], a[id*=btnSearch]');

		// 검색 필드에 btnid 값이 있을 경우 이 값에 해당하면 검색 버튼 사용
		if ($(this).data('btnid') != null && $(this).data('btnid') != '') {
			btnObj = $('input[id*=' + $(this).data('btnid') + ']');
		}

		if ($(this).attr('type') == "text") {
			$(this).keypress(function(event) {
				if (event.which == 13) {
					if ($(this).attr('validate') != null) {
						if ($.trim($(this).val()) == "") {
							alert($(this).attr('validate'));
							event.preventDefault();
						} else {
							btnObj.click();
							event.preventDefault();
						}
					} else {
						btnObj.click();
						event.preventDefault();
					}
				}
			});
		}

	});

	// 엔터 금지
	$('.disableEnter').each(function() {
		$(this).keydown(function(event) {
			var iKeyCode = event.which;
			if (iKeyCode == 13)
				event.preventDefault();
		});
	});

	// 숫자만 입력
	$('input.onlyNumber').each(function() {
		var ctrlDown = false, ctrlKey = 17, cmdKey = 91, vKey = 86, cKey = 67;
		$(this).css({
			 '-webkit-ime-mode': 'disabled'
			, 'moz-ime-mode': 'disabled'
			, '-ms-ime-mode': 'disabled'
			, 'ime-mode': 'disabled'
		}).keydown(function(event) {
			var iKeyCode = event.which;
			//alert(iKeyCode);
			//console.log('keyCode : ' + iKeyCode);
			if (event.shiftKey)
				event.preventDefault();

			//for chrome
			if (iKeyCode >= 229) {
				//alert('한글은 입력할 수 없습니다.');		//alert을 띄우면 한글 표시를 막을 수는 있음
				event.preventDefault();
			}

			// 8 : backspace, 9 : tab, 45 : insert, 46 : delete, 188 : comma
			if ((iKeyCode < 48 || iKeyCode > 57)
					&& (iKeyCode < 96 || iKeyCode > 105)
					&& (iKeyCode < 37 || iKeyCode > 40)
					&& iKeyCode != 8
					&& iKeyCode != 9
					&& iKeyCode != 46
					&& iKeyCode != 13
					&& iKeyCode != ctrlKey
					&& iKeyCode != cmdKey
					&& iKeyCode != vKey
					&& iKeyCode != cKey)
				event.preventDefault();

			if (iKeyCode == ctrlKey)
				ctrlDown = true;
		});

		//for chrome, firefox
		if (!window.event.keyCode) {
			$(this).keyup(function(event) {
				if (event.which == ctrlKey)
					ctrlDown = false;
				$(this).val($(this).val().replace(/[^0-9]/g, ''));
			}).focusout(function () {
				$(this).val($(this).val().replace(/[^0-9]/g, ''));
			});
		}
	});

	// 숫자만 입력 - 컴마 구분 추가 - lib.js : addComma 메서드 필요
	$('input.onlyNumberWithComma').each(function() {
		var ctrlDown = false, ctrlKey = 17, cmdKey = 91, vKey = 86, cKey = 67;
		$(this).css({
			 '-webkit-ime-mode': 'disabled'
			, 'moz-ime-mode': 'disabled'
			, '-ms-ime-mode': 'disabled'
			, 'ime-mode': 'disabled'
		}).keydown(function(event) {
			var iKeyCode = event.which;
			//alert(iKeyCode);
			//console.log('keyCode : ' + iKeyCode);
			if (event.shiftKey)
				event.preventDefault();

			//for chrome
			if (iKeyCode >= 229) {
				//alert('한글은 입력할 수 없습니다.');		//alert을 띄우면 한글 표시를 막을 수는 있음
				event.preventDefault();
			}
			// 8 : backspace, 9 : tab, 45 : insert, 46 : delete, 188 : comma
			if ((iKeyCode < 48 || iKeyCode > 57)
					&& (iKeyCode < 96 || iKeyCode > 105)
					&& (iKeyCode < 37 || iKeyCode > 40)
					&& iKeyCode != 8
					&& iKeyCode != 9
					&& iKeyCode != 46
					&& iKeyCode != 13
					&& iKeyCode != ctrlKey
					&& iKeyCode != cmdKey
					&& iKeyCode != vKey
					&& iKeyCode != cKey)
				event.preventDefault();

			if (iKeyCode == ctrlKey)
				ctrlDown = true;
		});

		//for chrome, firefox
		if (!window.event.keyCode) {
			$(this).keyup(function(event) {
				if (event.which == ctrlKey)
					ctrlDown = false;
				var val = $(this).val().replace(/^0+|[^\d]/g, '');
				//0만 적는 경우를 위해 예외 처리
				if ($(this).val().length == 1)
					val = $(this).val().replace(/[^\d]/g, '');

				val = addComma(val);
				$(this).val(val);
			}).focusout(function () {
				var val = $(this).val();
				val = $(this).val().replace(/^0+|[^\d]/g, '');
				val = addComma(val);
			});
		}
	});

	// 숫자, 소수점, 마이너스 가능
	// 실수 정규식 : /^[+-]?\d*(\.?\d*)$/
	$('input.onlyRealNumber').each(function() {
		$(this).css({
			 '-webkit-ime-mode': 'disabled'
			, '-moz-ime-mode': 'disabled'
			, '-ms-ime-mode': 'disabled'
			, 'ime-mode': 'disabled'
		}).keydown(function(event) {
			var iKeyCode = event.which;
			// alert(iKeyCode);
			if (event.shiftKey)
				event.preventDefault();

			//for chrome
			if (iKeyCode >= 229) {
				//alert('한글은 입력할 수 없습니다.');		//alert을 띄우면 한글 표시를 막을 수는 있음
				event.preventDefault();
			}

			if ((iKeyCode < 48 || iKeyCode > 57) && (iKeyCode < 96 || iKeyCode > 105) && (iKeyCode < 37 || iKeyCode > 40) && iKeyCode != 8 && iKeyCode != 9 && iKeyCode != 46 && iKeyCode != 13 && iKeyCode != 190 && iKeyCode != 110 && iKeyCode != 189 && iKeyCode != 109)
				event.preventDefault();

			if ($(this).data('number') !== undefined){
				var pointPattern = '';

				if ($(this).data('number') == 1)
					pointPattern = /^\d*[.]\d{1}$/;
				else if ($(this).data('number') == 2)
					pointPattern = /^\d*[.]\d{2}$/;
				else ($(this).data('number') == 3)
					pointPattern = /^\d*[.]\d{3}$/;

				//소수점 둘째자리까지만 입력가능
				if (pointPattern.test($(this).val())){
					if (iKeyCode != 8 && (iKeyCode < 37 || iKeyCode > 40) && iKeyCode != 46)
						event.preventDefault();
				}
			}

		});

		//for chrome, firefox
		if (!window.event.keyCode) {
			$(this).keyup(function(event) {
				$(this).val($(this).val().replace(/[^0-9\.\-]/g, ''));

				if ($(this).data('number') !== undefined){
					var iPointNumber = $.inArray('.', $(this).val());
					if (iPointNumber > 0){
						var newVal = $(this).val().substr(0, (iPointNumber + 1) + $(this).data('number'));
						$(this).val(newVal);
					}
				}
			}).focusout(function () {
				$(this).val($(this).val().replace(/[^0-9\.\-]/g, ''));

				if ($(this).data('number') !== undefined){
					var iPointNumber = $.inArray('.', $(this).val());
					if (iPointNumber > 0){
						var newVal = $(this).val().substr(0, (iPointNumber + 1) + $(this).data('number'));
						$(this).val(newVal);
					}
				}

				//마지막 문자가 숫자가 아닌 경우 제거 처리
				var idx = 1;
				var length = $(this).val().length;

				//문자열 length 만큼 for loop
				for(idx = 1; idx <= length; idx++){
					//마지막 문자열 가져오기
					var lastChar = $(this).val().substr(length - idx, 1);
					if (!isNaN(lastChar)) break;
				}

				if (idx > 1)
					$(this).val($(this).val().substr(0, length - (idx - 1)));

			});
		}
	});


	//대문자로 변환
	$('.toUpperCase').each(function() {
		$(this).css({
			 '-webkit-ime-mode': 'disabled'
			, '-moz-ime-mode': 'disabled'
			, '-ms-ime-mode': 'disabled'
			, 'ime-mode': 'disabled'
		}).keyup(function(event) {
			$(this).val($(this).val().toUpperCase());
		});
	});


	// jquery validator 기본값 설정
	if ($.validator != null) {
		$.validator.setDefaults({
			debug : false
			, onfocusout : false
			, onkeyup : false
			, onclick : false
			, invalidHandler : function(form, validator) {
				var errors = validator.numberOfInvalids();
				if (errors) {
					var msg = validator.errorList[0].message;
					alert(msg);
					validator.errorList[0].element.focus();
				}
			}
			, errorPlacement : function(error, element) {
				// Override error placement to not show error messages beside
				// elements
			}
		});
	}
}


/**
 * ajaXray : Multilevel subselect (jquery-1.3.x) 자식 select > option 개체에
 * class="sub_부모카테고리값"을 추가하면 동적 바인딩 가능
 */
function makeSublist(parent, child, isSubselectOptional, childVal) {

	$("body").append("<select style='display:none' id='" + parent + child + "'></select>");
	$('#' + parent + child).html($("#" + child + " option"));

	var parentValue = $('#' + parent).val();
	$('#' + child).html($("#" + parent + child + " .sub_" + parentValue).clone());

	$('#' + parent).change(function() {
		var parentValue = $('#' + parent).val();
		$('#' + child).html($("#" + parent + child + " .sub_" + parentValue).clone());

		if (isSubselectOptional)
			$('#' + child).prepend("<option value='-1' selected='selected'>-- 선택 --</option>");

		$('#' + child).trigger('change');
		// $('#' + child).focus();

		childVal = (childVal == null) ? "" : childVal;
		$('#' + child).val(childVal).attr('selected', 'selected');
	});
}

/**
 * for ASP.NET asp:CheckBoxList 객체 요소 체크 여부 확인 uniqueID : asp.net의 UniqueID 필요
 * 체크된 개수 반환
 */
function checkCheckBoxList(uniqueID) {
	var iCount = $('input[name*="' + uniqueID + '"]:checked').length;
	return iCount;
}

/**
 * 라디오버튼 초기화 radioName : html 객체 또는 이름
 */
function clearRadioButtons(radioName) {
	// alert(typeof radioName);
	var jqObj = $(radioName);
	if (typeof radioName == "string") {
		jqObj = $('input[name="' + radioName + '"]:radio');
	}

	if (jqObj == null)
		return;

	jqObj.each(function() {
		$(this).attr('checked', false);
	});
}

/**
 * 라디오버튼 값 리턴, name으로 가져옴
 */
function returnRadioButton(radioName) {
	$.trim($('input:radio[name="' + radioName + '"]:checked').val());
	if (typeof ($('input:radio[name="' + radioName + '"]:checked').val()) == 'undefined') {
		return '';
	} else {
		return $('input:radio[name="' + radioName + '"]:checked').val();
	}
}

/**
 * 라디오 버튼 체크 처리
 * @param name 라디오 버튼 그룹 이름
 * @param value 체크 처리될 값
 * @param defaultValue value가 빈 값일 때 적용할 기본값
 */
function checkRadio(selectorName, value, defaultValue) {
	if (defaultValue != null && value.length == 0)
		value = defaultValue;

	$('input[name="' + selectorName + '"]:radio').each(function() {
		if ($(this).val() == value) {
			$(this).prop('checked', true);
			return false;
		}
	});
}

/**
 * 셀렉트 박스 선택 처리
 * @param selectorName : 셀렉트박스 이름
 * @param value : 선택할 값
 * @param defaultValue : 기본값
 */
function selectSelectBox(selectorName, value, defaultValue) {
	if (defaultValue != null && value.length == 0)
		value = defaultValue;

	$('select[name="' + selectorName + '"] > option').each(function() {
		if ($(this).val() == value) {
			$(this).prop('selected', true);
			return false;
		}
	});
}



/**
 * 체크박스 체크 처리
 * @param checkboxSelectorName : 체크박스 셀렉터 이름
 * @param hiddenName : 처리될 히든 폼 (실제 저장될 필드)
 * @param value : 값 배열(쉼표 구분자)
 * @param defaultValue 값이 없을 경우 기본값
 */
function checkCheckbox(checkboxSelectorName, hiddenName, value, defaultValue) {
	if (defaultValue != null && value.length == 0)
		value = defaultValue;
	var values = value.split(',');
	var $checks = $('input[name="' + checkboxSelectorName + '"]:checkbox');
	var $hidden = $('input[name="' + hiddenName + '"]');
	$hidden.val(value);
	$checks.each(function(i, el) {
		$.each(values, function(j, val) {
			if ($(el).val() == val) {
				$(el).prop('checked', true);
			}
		});

		$(el).click(function() {
			var newVals = '';
			$checks.each(function() {
				if ($(this).prop('checked'))
					newVals += $(this).val() + ',';
			});
			newVals = newVals.replace(/,$/g, '');
			$hidden.val(newVals);
		});
	});
}

/**
 * 리스트 박스의 내용을 히든폼으로 복사 : 구분자 설정 가능
 * valueAttr : 히든폼으로 옮길 속성 : 기본값 = value
 * separator : 구분자 : 기본값 = ;
 */
function copyToHiddenFromListBox(listBoxID, hiddenID, valueAttr, separator) {

	if (separator == null)
		separator = ";"

	var hidden = $('#' + hiddenID);
	var options = $('#' + listBoxID + ' > option');

	var value = '';
	options.each(function() {
		if (valueAttr == null)
			value += $(this).val() + separator;
		else
			value += $(this).attr(valueAttr) + separator;
	});

	value = value.substr(0, value.lastIndexOf(separator));
	hidden.val(value);
}

/**
 * required blockUI plugin
 * element block : loading
 */
function loadingIcon(element, imageURL, msg) {
	if (element == null)
		return;

	if (imageURL == null)
		imageURL = '/images/preload_72x72.gif';

	if (msg == null)
		msg = '';

	var sMsg = '<img src="' + imageURL + '" />';
	if (msg != '')
		sMsg += '<br/>' + msg;

	// blockUI 환경 변수
	var varBlockUI = {
		message : sMsg, fadeIn : 100, fadeOut : 100, centerY : true, centerX : true, css : {
			border : 'none'
		}, overlayCSS : {
			backgroundColor : '#fff', opacity : 0.7
		}
	};

	$(element).block(varBlockUI);
}

/**
 * jQuery 경고 : jqobj 객체를 받아 해당 객체 위치의 바로 아래에 growlUI출력
 * jQuery.blockUI 필요
 */
function warnForm(jqobj, addedMsg, delayTime) {

	// 여러 개의 객체일 경우 처음 한 개를 지정
	var jqobjTarget = jqobj;
	if (jqobjTarget.length > 1) {
		jqobj.each(function(idx, elm) {
			jqobjTarget = $(elm);
			return false;
		});
	}

	// 표시용 메시지 만들기
	if ($('div.growlUI').length == 0) {
		$(document.body).after("<div class='growlUI'><h1>Alert</h1><h2>Message Here.</h2></div>");
	}

	var htmlTag = jqobjTarget.context.nodeName.toLowerCase();
	if (htmlTag == "#document" || htmlTag == null)
		var htmlTag = jqobjTarget.attr('type');

	var htmlObjectType = jqobjTarget.attr('type');
	// alert("nodeName : " + htmlTag + "\nobject Type : " + htmlObjectType);

	var msg = "Please fill this form."
	if (htmlObjectType.indexOf("select") > -1 || htmlTag == "select")
		msg = "Please select this form.";
	else if (htmlObjectType.indexOf("checkbox") > -1 || htmlObjectType.indexOf("radio") > -1)
		msg = "Please check this form(s) at least one.";

	if (addedMsg != null && addedMsg != "")
		msg = addedMsg;

	$('div.growlUI > h2').html(msg);

	var bgColor = jqobjTarget.css('background-color');

	// 브라우저가 스크롤되어 경고 대상 객체가 스크롤 위에 있을 때, 우선적으로 화면 스크롤
	if ($(window).scrollTop() > $(jqobjTarget).offset().top) {
		$(window).scrollTop($(jqobjTarget).offset().top + $(jqobjTarget).height() - $(document).scrollTop());
	}

	// blockUI 환경 변수
	var varBlockUI = {
		message : $('div.growlUI'), fadeIn : 500, fadeOut : 500, timeout : (delayTime == null) ? 1500 : delayTime, showOverlay : false, centerY : false, css : {
			width : '250px', top : $(jqobjTarget).offset().top + $(jqobjTarget).height() - $(document).scrollTop(), left : $(jqobjTarget).offset().left, right : '10px', border : 'none', padding : '5px', backgroundColor : '#000', '-webkit-border-radius' : '10px', '-moz-border-radius' : '10px', opacity : .6, color : '#fff', cursor : 'default'
		}
	};

	if (htmlObjectType == 'text' || htmlObjectType == 'password' || htmlObjectType == 'file' || htmlTag.indexOf("select") > -1 || htmlTag == "textarea") {

		jqobjTarget.animate({
			'background-color' : '#ff5555'
		}, 50).animate({
			'background-color' : bgColor
		}, 50).animate({
			'background-color' : '#ff5555'
		}, 50).animate({
			'background-color' : bgColor
		}, 50, function() {
			$.blockUI(varBlockUI);
			$(this).focus();
		});
	} else if (htmlObjectType == "checkbox" || htmlObjectType == "radio" || htmlTag == "a") {
		$.blockUI(varBlockUI);
	} else {
		alert("Undefined HTML objcet. please contact system engineer and check warnForm method.");
	}

	return false;
}

/**
 * jquery blockUI를 이용하여 레이어 메시지 출력
 * jQuery.blockUI 필요
 */
function alertGrowl(title, addedMsg, delayTime) {

	// 표시용 메시지 만들기
	if ($('div.growlUI').length == 0) {
		$(document.body).after("<div class='growlUI'><h1>Information</h1><h2>Message Here.</h2></div>");
	}

	if (title != null && title != "")
		$('div.growlUI > h1').html(title);

	if (addedMsg != null && addedMsg != "")
		$('div.growlUI > h2').html(addedMsg);

	// blockUI 환경 변수
	var varBlockUI = {
		message : $('div.growlUI'), fadeIn : 500, fadeOut : 500, timeout : (delayTime == null) ? 1200 : delayTime, showOverlay : false, centerY : false, centerX : false, css : {
			width : '250px', border : 'none', padding : '5px', backgroundColor : '#000', '-webkit-border-radius' : '10px', '-moz-border-radius' : '10px', opacity : .6, color : '#fff', cursor : 'default'
		}
	};

	$.blockUI(varBlockUI);
}

/**
 * 골프 우측화면 iframe 전용 메서드
 * ajax 처리 후 iframe 높이 변경
 */
function resizeIframeAfterAjaxComplete(id) {

	var $iframe = parent.$('iframe');
	if (id !== undefined)
		$iframe = parent.$('#' + id);

	var iDocHeight = $(document.body).height();
	var iPlusHeight = 22;
	if ($iframe.length > 0) {
		$iframe.height(iDocHeight + iPlusHeight);
	}
}

/**
 * oLoader ajax 처리
 * @param selector
 * @param url
 * @param method
 * @param data
 * @param isWholeWindow
 * @param onComplete 필수 : 완료 후 처리할 콜백 메서드
 */
function oLoader(selector, url, method, data, isWholeWindow, onComplete) {
	if (typeof onComplete !== 'function') {
		alert('onComplete(data) 메서드가 필요합니다.');
		return;
	}

	$(selector).oLoader({
		url: url
		, type: method || 'POST'
		, data: data
		//, image: '/images/loading_4.gif'
		, backgroundColor: '#fff'
		, fadeLevel: 0.9
		, image: '/images/loading_3_32.gif'
		, style: 0
		, wholeWindow: isWholeWindow || false
		, updateContent: false 				//자기 자신에게 업데이트 금지 - 결과를 별도 위치에 표시할 경우 사용 : complete()에서 처리
		, complete: onComplete
		, onError: function(jqXHR, textStatus, errorThrown) {
			//console.log(jqXHR);
			//alert('error : ' + textStatus);
			alert('error : ' + jqXHR.statusText);
			$(selector).oLoader('hide');
		}
	});

	/*
	 * oLoader : http://projects.ownage.sk/
	{
	  image: 'images/ownageLoader/loader1.gif',
	  style: 1,
	  modal: true,
	  fadeInTime: 300, // time in milliseconds
	  fadeOutTime: 300,
	  fadeLevel: 0.7, // overlay opacity (values 0 - 1)
	  backgroundColor: '#000',
	  imageBgColor: '#fff', // background under the loader image
	  imagePadding: '10',
	  showOnInit: true,
	  hideAfter: 0, // time in ms
	  url: false,
	  type: 'GET',
	  data: false,
	  updateContent: true,
	  updateOnComplete: true,
	  showLoader: true, // shows the loader image
	  effect: '',
	  wholeWindow: false, // when true, oLoader covers the whole window
	  lockOverflow: false, // locks body's overflow while loading
	  waitLoad: false, // oLoader will hide after element is loaded (works
						// on imgs, body and iframes)
	  checkIntervalTime: 100, // interval which checks for position
								// changes

	  // callback functions
	  complete: '', // executes on complete
	  onStart: '', // executes when animation starts
	  onError: '' // executes when ajax request fails
	}
	*/
}


/**
 * 전체화면 로딩처리
 */
function oLoaderLoading(){

	//colorbox 처리 미흡
	$('#cboxOverlay').hide();
	$('#colorbox').hide();

	if(parent && parent!=this) {
		parent.$('body').oLoader({
			wholeWindow: true, //makes the loader fit the window size
			lockOverflow: true, //disable scrollbar on body
			backgroundColor: '#000',
			fadeInTime: 1000,
			fadeLevel: 0.7,
			image: '/images/ownageLoader/loader6.gif'
		});
	} else {
		$('body').oLoader({
			wholeWindow: true, //makes the loader fit the window size
			lockOverflow: true, //disable scrollbar on body
			backgroundColor: '#000',
			fadeInTime: 1000,
			fadeLevel: 0.7,
			image: '/images/ownageLoader/loader6.gif'
		});
	}
}


/**
 * input text에서 사원입력시 사원목록 자동완성 처리
 *
 * @param objClass - input의 class값을 입력
 * @return 해당 element에 data로 저장처리
 * <ul>
 * 	<li>uid : 아이디</li>
 * 	<li>uname : 이름</li>
 * 	<li>gname : 그룹명(골프/합동)</li>
 * 	<li>dname : 부서명</li>
 * 	<li>pname : 직급</li>
 * </ul>
 */
function autocompleteMember(objClass) {
	var $obj = $('.' + objClass);
	$obj.each(function() {
		var $objEach = $(this);
		$objEach.autocomplete({
			source: function(req, res) {
				$.ajax({
					url: '/common/get_member_name.do'
					, type: 'post'
					, dataType: 'json'
					, data: req
					, success: function (data) {
						res(data);
					}
				});
			}
			, minLength: 2
			, autoFocus: false
			, focus: function(e, ui) {
				var uid = ui.item.uid;
				var uname = ui.item.uname;
				var gname = ui.item.gname;
				var dname = ui.item.dname;
				var pname = ui.item.pname;
				var phone = ui.item.phone;

				$objEach.data('uid', uid); //아이디
				$objEach.data('uname', uname); //이름
				$objEach.data('gname', gname); //그룹명(골프/합동)
				$objEach.data('dname', dname); //부서명
				$objEach.data('pname', pname); //직급
				$objEach.data('phone', phone); //전화

				// autocompleteMemberCallback 있을시 실행
				if (typeof autocompleteMemberCallback == 'function')
					autocompleteMemberCallback($objEach);

				//key down시 리스트 사라지는 버그 처리
				return false;
			}
		});
	});
}

/**
 * 지정한 class input에서 입력받은 영업소명 자동완성 처리
 *  - 영업소명 입력 시 해당 키워드가 포함된 영업소명 리스트 출력
 *  - 사용예시) $(해당요소).data('officeCode');
 *
 * @param objClass
 */
function autocompleteOffice(objClass) {
	var cache = {};
	var $obj = $('.' + objClass);

	$obj.each(function(){
		var $objEach = $(this);

		$objEach.autocomplete({
			source: function(req, res) {

				$.ajax({
					url: '/common/get_office_name.do'
					, type: 'post'
					, dataType: 'json'
					, data: req
					, success: function (data) {
						res(data);
					}
				});
			}
			, minLength: 2
			, autoFocus: false
			, focus: function(e, ui) {

				//value : list form에서 선택했을 때, input form에 표시되는 값
				//label : list form 에 표시되는 항목
				//code : 영업소 코드

				var officeCode = ui.item.code;
				var officeName = ui.item.value;
				var repName = ui.item.rep;
				var repPhone = ui.item.phone;
				var branchName = ui.item.branch;
				var bizSeq = ui.item.bseq;

				$objEach.data('officeCode', officeCode); //영업소 코드
				$objEach.data('officeName', officeName); //영업소명
				$objEach.data('repName', repName); //소장명
				$objEach.data('repPhone', repPhone); //소장 연락처
				$objEach.data('branchName', branchName); //지점
				$objEach.data('bizSeq', bizSeq); //지점

				if (typeof selectOfficeAjax == 'function')
					selectOfficeAjax(officeCode, officeName, repName, repPhone, branchName, bizSeq);

				//key down시 리스트 사라지는 버그 처리
				return false;
			}
		});
	});
}


/**
 * 지정한 class input에서 입력받은 법인명 자동완성 처리
 *  - 법인명 입력 시 해당 키워드가 포함된 법인명 리스트 출력
 *  - class 속성과 alt 속성 필수
 *
 *  - ex) $(해당요소).data('corpSeq');

 * @param objClass
 */
function autocompleteCorp(objClass) {
	var cache = {};
	var $obj = $('.' + objClass);

	$obj.each(function(){
		var $objEach = $(this);

		$objEach.autocomplete({
			source: function(req, res) {

				$.ajax({
					url: '/common/get_corp_name.do'
					, type: 'post'
					, dataType: 'json'
					, data: {'corpName' : $objEach.val(), 'corpType' : $objEach.data('type')}
					, success: function (data) {
						res(data);
					}
				});
			}
			, minLength: 0
			, autoFocus: false
			, focus: function(e, ui) {

				//value : list form에서 선택했을 때, input form에 표시되는 값
				//label : list form 에 표시되는 항목
				//seq : 법인순번

				var corpSeq = ui.item.seq;
				var corpName = ui.item.value;

				$objEach.data('corpSeq', corpSeq); //법인순번
				$objEach.data('corpName', corpName); //법인명

				/*if (typeof selectCorpAjax == 'function' && objClass == 'cCorp')
					selectCorpAjax(corpSeq, corpName);

				if (typeof selectCorpAjax2 == 'function' && objClass == 'pCorp')
					selectCorpAjax2(corpSeq, corpName);*/

				//key down시 리스트 사라지는 버그 처리
				return false;
			}
		});
	});
}



/**
 *	CMS생성할 목록 (생성전에 목록 노출)
	<li>00410110 - 종책보험(/finance/ci/ci_list.do)</li>
	<li>00410201 - 용차보험료(/cms/req/pay_req_list.do)</li>
	<li>00410100 - 지입료(/cms/req/pay_req_list.do)</li>
	<li>00410160 - 적재물보험(/finance/itemUpdate/iu_list.do)</li>
	<li>00410120 - 자차공제(/finance/selfStats/selfStats_list.do)</li>
	<li>00410130 - 과태료(/vehicle/fine/fine_list.do)</li>
 */
function getPayCmsList(payTpCd, seq){

	var isStop = hasCMSAuth(payTpCd);
	if (isStop)
		return;

	var typeName = '';
	if (payTpCd == '00410110') typeName = '종책보험';
	else if (payTpCd == '00410201') typeName = '용차보험료';
	else if (payTpCd == '00410100') typeName = '지입료';
	else if (payTpCd == '00410160') typeName = '적재물보험';
	else if (payTpCd == '00410120') typeName = '자차공제';
	else if (payTpCd == '00410150') typeName = '지게차공제';
	else if (payTpCd == '00410130') typeName = '과태료';

	if (typeName == '') {
		alert('생성할 품목이 없습니다.');
		return;
	}

	var dataMap = {pTpCd: payTpCd};
	if (typeof seq != "undefined")
		dataMap = {pTpCd: payTpCd, aseq : seq};

	$.ajax({
		url: '/cms/req/common_cms_list_json.do'
		, async: false
		, type: 'post'
		, data : dataMap
		, dataType: 'json'
		, success: function (data) {

			var sHtml = '';
			var iTotPrice = 0;
			var iTotCnt = 0;

			var iHeight = 270;

			if (data.list.length > 0){

				var obj = data.list;
				//목록
				for(var i in obj){

					sHtml += '<tr>';
					sHtml += '	<td>' + obj[i].BIZ_NAME + '</td>';
					sHtml += '	<td>' + addComma(obj[i].CNT) + '</td>';
					sHtml += '	<td class="ag_right">' + addComma(obj[i].TOT_PRICE) + ' 원</td>';
					sHtml += '</tr>';

					iTotPrice += obj[i].TOT_PRICE;
					iTotCnt += obj[i].CNT;
				}

				//합계
				sHtml += '<tr>';
				sHtml += '	<td class="bg_red">합계</td>';
				sHtml += '	<td class="bg_red">' + addComma(iTotCnt) + '</td>';
				sHtml += '	<td class="bg_red ag_right">' + addComma(iTotPrice) + ' 원</td>';
				sHtml += '</tr>';

				//최대 개수만큼 높이값 추가 (15개 초과시에는 15개로만 고정)
				iHeight += ((obj.length > 15 ? 15 : obj.length) * 35);

			}else{

				sHtml += '<tr>';
				sHtml += '	<td colspan="3">데이터가 없습니다.</td>';
				sHtml += '</tr>';
			}

			var popHtml = '';
			popHtml += '<div id="pay_cms_list_pop" style="display: none;">';
			popHtml += '	<div id="pay_cms_list_layer" class="pop">';
			popHtml += '		<div class="pop_tit">CMS생성 목록</div>';

			popHtml += '		<div class="cons">';
			popHtml += '			<table class="pop_list">';
			popHtml += '				<colgroup>';
			popHtml += '					<col width="60%" />';
			popHtml += '					<col width="15%" />';
			popHtml += '					<col width="25%" />';
			popHtml += '				</colgroup>';
			popHtml += '				<thead>';
			popHtml += '					<tr>';
			popHtml += '						<th>법인명</th>';
			popHtml += '						<th>건수</th>';
			popHtml += '						<th>금액</th>';
			popHtml += '					</tr>';
			popHtml += '				</thead>';
			popHtml += '				<tbody id="payCmsList">' + sHtml + '</tbody>';
			popHtml += '			</table>';
			popHtml += '		</div>';

			popHtml += '		<div class="btn">';

			//데이터가 있는 경우만
			if (data.list.length > 0)
				popHtml += '			<a href="javascript: makeCmsPayLog(\''+ payTpCd +'\', \'' + typeName + '\', \'' + seq + '\');" class="btn_blue">CMS생성</a>';

			popHtml += '			<a href="javascript: $.colorbox.close();" class="btn_grey">취소</a>';
			popHtml += '		</div>';
			popHtml += '	</div>';
			popHtml += '</div>';

			//있는 경우 초기화 후 추가
			if ($('#pay_cms_list_pop').length > 0)
				$('#pay_cms_list_pop').remove();

			$('body').append(popHtml);


			$.colorbox({
				inline : true
				, href : $("#pay_cms_list_layer")
				, overlayClose: false
				, width: 500
				, height: iHeight
			});

		}
		, error: function (data) {
			alert('getPayCmsList : ' + data.responseText);
			return;
		}
	});

}



/**
 * 기존 파일을 새로운 파일로 변경
 * @param seq (KDE_FILE.FILE_SEQ : 파일순번)
 * @param pageType (후처리 사용, disuse : 말소내역, changeown : 명의변경내역, office : 출고현황) - 기본값은 office
 */
function modifyFileLayer(seq, pageType){

	parent.$.colorbox({
		inline : false
		, href : '/common/modifyFileLayer.do?pageType=' + pageType + '&fileSeq=' + seq
		, overlayClose: false
		, width: 500
		, height: 300
	});

}


//CMS생성 처리
function makeCmsPayLog(payTpCd, typeName, seq) {

	var isStop = hasCMSAuth(payTpCd);
	if (isStop)
		return;

	if (!confirm(typeName + '에 대한 CMS 출금 원본을 생성하시겠습니까?'))
		return;

	// 아이프레임 판단 여부
	var parentYn = "";
	if(parent && parent!=this) {
		parentYn = 'parent';
	} else {
		parentYn = 'go';
	}

	var dataMap = {pTpCd: payTpCd, parentYn: parentYn};
	if (typeof seq == "undefined"  )
		dataMap = {pTpCd: payTpCd, aseq : seq, parentYn: parentYn};

	oLoaderLoading();

	$.Nav('go', '/cms/req/makeCmsPayLog_proc.do', dataMap);
}





/**
 * 객체 화면 출력
 */
function documentWrite(obj) {
	if ($('div.divDebug').length == 0)
		$(document.body).append('<div class="divDebug"></div>');

	$('div.divDebug').html('<xmp>' + obj + '</xmp>');
}

/**
 * 인풋박스를 만들어서 form 안에 넣는다.
 * @param frm		form 객체
 * @param name		태그 이름
 * @param value		태그 값
 */
function createElementInput(frm, name, value) {
	var useElement = null;
	try {
		for ( var i=0 ; i < frm.elements.length ; i++ ) {
			if (frm.elements[i].type == "hidden" && frm.elements[i].name == name) {
				useElement = frm.elements[i];
			}
		}
		if (useElement == null) {
			var input = document.createElement("input");
			input.type = "hidden";
			input.id = name;
			input.name = name;
			input.value = value;
			frm.appendChild(input);
		} else {
			useElement.value = value;
		}
	}
	catch(ex) {
		try {
			$(frm).find('input[name='+name+']').remove();
			$(frm).append('<input type="hidden" name="'+name+'" \>');
			$(frm).find('input[name='+name+']').val(value);
		}
		catch(ex) {};
	};
}


/**
 * 자바스크립트 객체 속성 확인
 * for debugging : loop element
 * obj : 루프가 가능한 객체
 * @param obj
 */
function debugObject(obj) {

	if ($('div.divDebug').length == 0)
		$(document.body).append('<div class="divDebug"></div>');
	var str = debugObjectLoop(obj);
	$('div.divDebug').html(str);

	function debugObjectLoop(obj) {
		var sStr = '';
		sStr +=	'<table border="1" cellspacing="0" cellpadding="0" style="width:100%; margin: 30px 0 30px 0;">';
		sStr += '<colgroup><col style="width:10%;" /><col style="width:10%;" /><col style="width:80%;" /></colgroup>';
		sStr += '<tbody>';
		sStr += '<tr><td colspan="3" style="padding:0 0 0 20px; height: 50px;">' + String(obj) + '</td></tr>';
		sStr += '<tr style="background:#eee;"><th>key</th><th>type</th><th>value</th></tr>';
		for ( var x in obj) {
			// 자바스크립트 객체는 재귀시 무한 루프에 걸릴 수 있으므로 한 단계만 확인
			// if (typeof obj[x] != 'object')
			// sStr += '<tr><th style="font-weight:bold;">' + x + '</th><td>' +
			// (obj[x] == null || obj[x] == '' ? '&nbsp;' : obj[x]) + '</td></tr>';
			// else {
			// //var sInnerObject = debugObjectLoop(obj[x]);
			// sStr += '<tr><th style="font-weight:bold;">' + x + '</th><td><a>' +
			// obj[x] + '</a></td></tr>';
			// }
			sStr += '<tr><th style="font-weight:bold; background:#0ef;">' + x + '</th><th>' + (typeof obj[x]) + '</th><td>' + (obj[x] == null || obj[x] == '' ? '&nbsp;' : obj[x]) + '</td></tr>';
		}
		sStr += '</tbody></table>';
		return sStr;
	};
}

//콤마찍기
function comma(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

//콤마제거
function uncomma(str) {
    str = String(str);
    return str.replace(/[^\d]+/g, '');
}

