/*!
* jQuery locator plugin
* Version 0.9
* @requires jQuery v1.2.3 or later
*
* author : @wimy1
* first dev date : 2010-09-14
* 	- url 이동시 get 파라미터 상태 유지, 변경등을 편리하게 적용할 수 있게 해주는 유틸리티 
* 
*	- jQuery 펑션 제거, $.Nav 로 접근 가능
*	- getSearch 메서드를 내부 메서드로 전환 : 메서드 내용 변경 : argument 2개 까지 받음 : argument는 존재할 경우 반드시 map형식
*	- 기본 이동 메서드에 추가적인 파라미터가 붙도록 수정
*	- 추가 파라미터, 제거할 파라미터 적용 가능 : map type : 모두 생략 가능
		-- list, view, write, edit, suffix 등의 기본 메서드일 경우	2번째 파라미터 : 추가할 파라미터
																	3번째 파라미터 : 제거할 파라미터
																	4번째 파라미터 : isAllClear (bool) : 기존 파라미터 모두 제거
		-- go 메서드 :		3번째 파라미터 : 추가할 파라미터
							4번째 파라미터 : 제거할 파라미터
							5번째 파라미터 : isAllClear (bool) : 기존 파라미터 모두 제거

		-- 제거할 파라미터는 map 파라미터에서 키 값만 설정하면 됨. 
			(추가할 파라미터에 빈 값을 강제 매핑하면 해당 파라미터는 제거되므로 제거 파라미터는 사용하지 않아도 됨)
			

*	- 2011-09 : 기존 파라미터를 모두 제외하고 새로 바인딩할 파라미터만 추가 가능
*	- 2016-01-27 : 0.8 업버전 : func 옵션 추가 (location을 변경하지 않고, 실행하고 싶은 함수 실행 가능)
*	- 2017-06-19 : iframe 이동 메서드 추가 - timeout 150ms 
*			
* 주의 : 
*	1. 자동으로 구성되는 파라미터 값들은 encodeURIComponent 메서드를 사용함
*	2. 맵 파라미터는 기본적으로 싱글 쿼테이션을 사용하고, 값에 싱글 쿼테이션이 있을 경우 파라미터 바인딩 시 스크립트 문제가 되므로 싱글 쿼테이션이 포함되지 않도록 주의
*
*
* syntax)
		$.Nav('list' [, {'cpage':'6', 'foo':'bar'} [, {'foo':'', 'bar': ''}]]);
		$.Nav('suffix', '접미사 이름' [, {'cpage':'6', 'foo':'bar'} [, {'foo':'', 'bar': ''}]]);
			: 접미사이름: 언더스코어(_)와 확장자 사이의 문자열 : test_list.aspx 에서 $.Nav('suffix', 'info'); 링크일 경우 test_info.aspx로 이동
		$.Nav('go', '/page/index.asp' [, {'cpage':'6', 'foo':'bar'} [,  {'foo':'', 'bar': ''}]]);
		$.Nav('func', functionName[, object]);
		$.Nav('iframe', '#ifrm[.ifrm]', '[page url]');

* ex)
		현재 url : ./test_write.[확장자]?cpage=4&foo=bar 일 때

		$.Nav('list', {cpage:'1', foo2:'bar2'});
			-> ./test_write.[확장자]?cpage=1&foo=bar&foo2=bar2 로 이동

		$.Nav('list', {cpage:'1'});
			-> ./test_write.[확장자]?cpage=1&foo=bar 로 이동

		$.Nav('list', {cpage:'1', foo:''});
			-> ./test_write.[확장자]?cpage=1 로 이동

		$.Nav('list', {foo2:'bar2'}, null, true);
			-> ./test_list.[확장자]?foo2=bar2 로 이동
		
		$.Nav('go', './test2_list.[확장자]');
			-> ./test2_list.[확장자]?cpage=4&foo=bar 로 이동

		$.Nav('func', functionName[, object]);
			-> 현재 페이지에 있는 함수 functionName() 실행 
				-> 파라미터1 : [object]
				-> 파라미터2 : 현재 URL의 queryString 
		
* 도움 주신 분, 아이디어 : @jynius
*/

(function ($) {
	
	var delay = 20000; //엑셀 중복방지
	var submitted = false; //엑셀중복방지
	var LOC_PARAM_CURR_PAGE = 'cpage'; 			//사용안함
	var Nav_pathname = window.location.pathname;
	var Nav_search = window.location.search;

	//패턴1 : 페이지URL에서 (_)까지 구분해준다. 확장자는 매치 결과에 두 번째로 기록된다.
	var Nav_pattern_suffix = /([a-z0-9]+_)*(?:[a-z0-9]+)\.(\w+)/;
	//패턴2 : 싱글 쿼테이션 문자열
	var Nav_pattern_single_quot = /&squot;/g;

	var methods = {
		//직접 사용하지 말 것, 페이지 규칙이 있는 페이지에만 사용할 것.
		getPathname: function (p) {
			Nav_pattern_suffix.exec(Nav_pathname);
			//alert('p : ' + p + '\nNav_pathname : ' + Nav_pathname + '\nNav_pattern_suffix : ' + Nav_pattern_suffix + '\nRegExp.$1 : ' + RegExp.$1 + '\nRegExp.$2 : ' + RegExp.$2 + '\n\npath : ' + Nav_pathname.replace(Nav_pattern_suffix, RegExp.$1 + p + '.' + RegExp.$2));
			return Nav_pathname.replace(Nav_pattern_suffix, RegExp.$1 + p + "." + RegExp.$2);
		}

		//args[0] : 추가할 파라미터 : map
		//args[1] : 제거할 파라미터 : map	: {'key1':'', 'key2':''}
		//args[2] : bool : 기존 파라미터 모두 제거 : true = 모두 제거, false or null = 유지(default)
		, getSearch: function () {
			var args = methods.getSearch.arguments;
			var s = location.search;
			var sAnchor = '';
			if (s.indexOf("?") > -1) {
				s = s.replace(/[\?]/g, "");
			}
			//문자열 끝 부분의 앵커 태그, &기호 제거
			s = s.replace(/#[.]*$/g, "");
			s = s.replace(/&$/g, "");

			if (args.length == 0)
				return "?" + s;

			var arrParams = s.split("&");

			//기존 파라미터 제거 여부에 따라...
			if (args[2] == null || !args[2]) {
				//args[0]
				if (args[0] != null && typeof args[0] == "object") {
					//중복 파라미터 제거 : 기존 파라미터 값을 새 파라미터 값으로 변경
					var arrAddParams = new Array();
					var isNew = true;

					for (var x in args[0]) {
						isNew = true;
						for (var i = arrParams.length - 1; i >= 0; i--) {
							if (arrParams[i].split("=")[0] == x) {
								arrParams[i] = x + "=" + encodeURIComponent(String(args[0][x]).replace(Nav_pattern_single_quot, "'"));
								isNew = false;
								break;
							}
						}
						if (isNew) {
							arrAddParams[arrAddParams.length] = x + "=" + encodeURIComponent(String(args[0][x]).replace(Nav_pattern_single_quot, "'"));
						}
					}
					//파라미터 추가
					for (var i = 0; i < arrAddParams.length; i++) {
						arrParams[arrParams.length] = arrAddParams[i];
					}
				}

				//args[1] 제거할 파라미터 체크
				if (args[1] != null && typeof args[1] == "object") {

					//파라미터 제거 : args[1] : 실제로 제거하지 않고 값을 빈 값으로 변환
					for (var i = 0; i < arrParams.length; i++) {
						for (var y in args[1]) {
							if (arrParams[i].split("=")[0] == y)
								arrParams[i] = arrParams[i].split("=")[0] + "=";
						}
					}
				}
				else if (args[1] != null && typeof args[1] == "string") {
					sAnchor = '#' + args[1];
				}
			}
			else {
				//파라미터 새로 구성
				arrParams = new Array();
				var i = 0;
				if (args[0] != null && typeof args[0] == "object") {
					for (var x in args[0]) {
						//alert(x + " : " + args[0][x]);
						arrParams[i] = x + "=" + encodeURIComponent(String(args[0][x]).replace(Nav_pattern_single_quot, "'"));
						i++;
					}
				}

				//args[1] 제거할 파라미터 체크
				if (args[1] != null && typeof args[1] == "object") {
					//파라미터 제거 : args[1] : 실제로 제거하지 않고 값을 빈 값으로 변환
					for (var i = 0; i < arrParams.length; i++) {
						for (var y in args[1]) {
							if (arrParams[i].split("=")[0] == y)
								arrParams[i] = arrParams[i].split("=")[0] + "=";
						}
					}
				}
				else if (args[1] != null && typeof args[1] == "string") {
					sAnchor = '#' + args[1];
				}
			}
			//빈 파라미터를 제외하고 파라미터 문자열 구성
			var search = "";
			for (var x in arrParams) {
				if (arrParams[x].split("=")[1] == "")
					continue;
				search += arrParams[x] + "&";
			}
			search = search.replace(/^&|&$/g, "");
			if (search)
				search = "?" + search;
			if (sAnchor != '')
				search += sAnchor;
			return search;
		}

		//지정된 suffix 페이지로 이동한다.
		//p: suffix
		//s: 추가할 파라미터(map)
		//s2 : 제거할 파라미터(map)
		, suffix: function (p, s, s2, isAllClear) {

			Nav_search = methods.getSearch(s, s2, isAllClear);

			//alert(p + " : " + methods.getPathname(p));
			location.href = methods.getPathname(p) + Nav_search;
		}

		//특정 URL로 이동.
		, go: function (p, s, s2, isAllClear) {

			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = p + Nav_search;
		}

		, list: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = methods.getPathname('list') + Nav_search;
		}

		, edit: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = methods.getPathname('edit') + Nav_search;
		}

		, write: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = methods.getPathname('write') + Nav_search;
		}

		, del: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = methods.getPathname('del') + Nav_search;
		}

		, view: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = methods.getPathname('view') + Nav_search;
		}
		
		, excel: function (p, s, s2, isAllClear) {
			if (submitted == true) {
				alert("잠시만 기다려주세요.");
				return;
			} else {
				Nav_search = methods.getSearch(s, s2, isAllClear);
				window.location.href = p + Nav_search;
				
				setTimeout (function(){
					submitted = false;
				}, delay);
				submitted = true;
			};
        }

		, self: function (s, s2, isAllClear) {
			Nav_search = methods.getSearch(s, s2, isAllClear);
			window.location.href = Nav_pathname + Nav_search;
		}

        , parent: function (p, s, s2, isAllClear) {
        	Nav_search = methods.getSearch(s, s2, isAllClear);
        	parent.location.href = p + Nav_search;
        }
        
        , iframe: function (ifrmId, p, s, s2, isAllClear) {
        	//parent.location.href = p + Nav_search;
        	//1. IE11에서 iframe 호출로 인해 정적 리소스들이(특히 css) 정상 작동하지 않는 문제가 있음. 
			//2. 또는 톰캣 구동 시 server.xml 내 host, context 중복 호출로 인해 static 리소스 스프라이트 문제가 발생하는 것일 수도 있음. 이 문제는 IE11에서만 발생
			//로드 타이밍을 늦춰 해결.
        	
        	Nav_search = methods.getSearch(s, s2, isAllClear);
        	setTimeout(function() {
        		$(ifrmId).attr('src', p + Nav_search);
        	}, 150);
        }

		, func: function (fn, s, s2, isAllClear) {
			try {
				//Nav_search = methods.getSearch(s, s2, isAllClear);
				if (Nav_search.indexOf('?') == 0)
					Nav_search = Nav_search.substr(1);
				if(typeof fn === 'function')
					fn(s, Nav_search);
				else
					alert('함수 형식의 파라미터가 필요합니다.');
			} catch(e) {
				alert(e);
			}
		}
		
	};


	$.Nav = function (method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			return $.error('Method ' + method + ' does not exist on jQuery.Nav');
		}
	};

})(jQuery);