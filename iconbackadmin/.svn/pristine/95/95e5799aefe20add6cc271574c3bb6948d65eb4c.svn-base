/* 한글화 */
jQuery(function($){
	$.datepicker.regional['ko'] = {
		closeText: '닫기',
		prevText: '이전달',
		nextText: '다음달',
		currentText: '오늘',
		monthNames: ['1월','2월','3월','4월','5월','6월',
		'7월','8월','9월','10월','11월','12월'],
		monthNamesShort: ['1월','2월','3월','4월','5월','6월',
		'7월','8월','9월','10월','11월','12월'],
		dayNames: ['일요일','월요일','화요일','수요일','목요일','금요일','토요일'],
		dayNamesShort: ['일','월','화','수','목','금','토'],
		dayNamesMin: ['일','월','화','수','목','금','토'],
		weekHeader: 'Wk',
		dateFormat: 'yy-mm-dd',
		firstDay: 0,
		isRTL: false,
		showMonthAfterYear: true,
		yearSuffix: '',
		yearRange:"1900:c+10 "
	};
	$.datepicker.setDefaults($.datepicker.regional['ko']);
});




/* 주간 선택 start */
function setCurrWk( v1, v2 ) {
	$( v1 + ", " + v2 ).datepicker( getCurrWkOpt( v1, v2 ) );
}

function getCurrWkOpt( v1, v2 ) {
	var startDate, endDate;

	return {
		changeYear			:	true,
		changeMonth		:	true,
		numberOfMonths	:	1,
		showOn				:	"both",
		buttonImage			:	"/images/btn_calendar.gif",
		buttonImageOnly	:	true,
		showButtonPanel	:	true,
		yearRange			:	"c-120:c+120",

		showOtherMonths	:	true,
		selectOtherMonths	:	true,
		onSelect				:	function(dateText, inst) {
			var date = $(this).datepicker('getDate');
			startDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay());
			endDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() - date.getDay() + 6);

			var dateFormat = inst.settings.dateFormat || $.datepicker._defaults.dateFormat;
			$( v1 ).val($.datepicker.formatDate( dateFormat, startDate, inst.settings ));
			$( v2 ).val($.datepicker.formatDate( dateFormat, endDate, inst.settings ));

			getCurrWk();
		},
		beforeShowDay		:	function(date) {
			var cssClass = '';
			if(date >= startDate && date <= endDate)
				cssClass = 'ui-datepicker-current-day';
			return [true, cssClass];
		},
		onChangeMonthYear: function(year, month, inst) {
			getCurrWk();
		}
	};
}

function getCurrWk() {
	window.setTimeout(function () {
		$('#ui-datepicker-div').find('.ui-datepicker-current-day a').addClass('ui-state-active');
	}, 1);
}
/* end 주간 선택 */




/* 단일로 사용 될 경우 */
//		setDtpicker("#txt_sdt");
//		<input type="text" id="txt_sdt" name="txt_sdt" class="input_type" style="width:100px;">
function setDtpicker( v1 ) {
	$( v1 ).datepicker({
		changeYear			:	true,
		changeMonth		:	true,
		numberOfMonths	:	1,
		showOn				:	"both",
		buttonImage			:	"/images/btn_calendar.gif",
		buttonImageOnly	:	true,
		showButtonPanel	:	true,
		yearRange			:	"c-120:c+120",
		beforeShow: function( input ) {  
		 setTimeout(function() {  
			var buttonPane = $( input )  
			     .datepicker( "widget" )  
			     .find( ".ui-datepicker-buttonpane" );  
			var btn = $('<button class="ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all" type="button">Clear</button>');  
		btn.unbind("click").bind("click", function () {  
			$.datepicker._clearDate( input );  
		});  
		btn.appendTo( buttonPane );  
	 }, 1 );  
	}  
	});
}

/* 한주 선택한 것 표시 */
$(function(){
	$('.ui-datepicker-calendar tr').on('mousemove', function() {
		$(this).find('td a').addClass('ui-state-hover');
	}).on('mouseleave', function() {
		$(this).find('td a').removeClass('ui-state-hover');
	});
});