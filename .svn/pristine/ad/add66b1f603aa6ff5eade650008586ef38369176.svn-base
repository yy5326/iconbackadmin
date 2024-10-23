<%@ page language="java" contentType="text/html;" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/tags.jsp"%>
<div class="contents cont_layerPhoto lts">
	<div id="original">
		<div class="Layerphoto">
			<h3 class="acen">골프백 사진</h3>
			<div class="tableBox cancleMemo" style="padding: 12px 20px !important; font-size: 15px;">${vo.canReason }</div>
			
			<div class="photo">
				<div class="filebox">
					<label for="ex_file">
						<img id="picex_file" src="${photo1}" alt="사진"/>
					</label>
		  		</div>
				<input accept="file_extension|image"style="display: none"/>
			
				<div class="filebox">
					<label for="ex_file2">
						<img id="picex_file2" src="${photo2}" alt="사진"/>
					</label>
				</div>
				<input accept="file_extension|image" style="display: none"/>
							
			</div>
		</div>
	

	
		<div class="receipt_layer receipt_layer02 receipt_layerPT">
			<a href="javascript:" id="back" class="btn acen">이전</a>
			<a href="javascript:$.colorbox.close();" class="btn acen cancle">닫기</a>
		</div>
	</div>
</div>
</body>

<script type="text/javascript">

	$(document).ready(function(){
		var rcpInfSeq = ${rcpInfSeq};
		
		$('#back').click(function () {
    		var sHref = './receiptDetail.do?rcpISeq=' + rcpInfSeq;
    		$.colorbox({
    			href : sHref,
    			overlayClose: false,
    			width: 1230,
    			height: 820,
    		});
			
		});
		
		
	});
</script>
