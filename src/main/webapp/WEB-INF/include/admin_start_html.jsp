<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>

<html>
<head>
	<title><s:eval expression="@globals['site.baseTitle']" /></title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<link rel="stylesheet" href="/common/css/common.css" />
	<link rel="stylesheet" href="/common/css/reset.css" />
	<link rel="stylesheet" href="/common/css/base.css" />
	<link rel="stylesheet" href="/common/css/style.css" />
	<link rel="stylesheet" href="/common/css/jquery/colorbox/colorbox.css" type="text/css" />
	<link rel="stylesheet" href="/common/SlickGrid-2.4.19/css/smoothness/jquery-ui.css" type="text/css"/>

	<script type="text/javascript" src="/common/js/jquery/jquery-1.11.0.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery-migrate-1.4.1.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery.ui.datepicker.custom.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery.locator.js"></script>
	<script type="text/javascript" src="/common/js/jquery/colorbox/jquery.colorbox.js"></script>
	<script type="text/javascript" src="/common/js/jquery/jquery.oLoader.min.js"></script>
	<script type="text/javascript" src="/common/js/lib.js"></script>
    <script type="text/javascript" src="/common/js/common.jquery.util.js"></script>
    <script type="text/javascript" src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<script type="text/javascript">
	$(document).ready(function(){
		$(".clearfix").find(".hmenu").each(function(index, item){
			if($(this).attr('href') == $(location).attr('pathname')){
				$(".hmenuNm").html($(this).html());
			}
		});
	});
</script>

<body class="body_bg">
	<div id="wrap">
		<div class="header clearfix">
			<div class="header_acen">
				<h1><a href="/receipt/receiptList.do"><img alt="" src="/common/images/logo/KD_logo1.jpg"></a></h1>
				<div class="gnb">
					<ul class="clearfix">
						<li><a href="/receipt/receiptWrite.do" class="hmenu">택배접수</a></li>
						<li><a href="/receipt/receiptList.do" class="hmenu">택배조회</a></li>
                        <c:if test="${partner != 'zimcarry_111'}">
                        <li><a href="#" class="hmenu">정산</a></li>
						<li><a href="/golfInf/golfInfList.do" class="hmenu">골프장관리</a></li>
                        <li><a href="/coupon/couponList.do" class="hmenu">제휴쿠폰관리</a></li>
                        </c:if>
					</ul>
				</div>
			</div>
		</div>
		<div class="menu">
			<a href="/receipt/receiptWrite.do" class="home">홈</a>
			<a href="${path} " class="hmenuNm"></a>
		</div>