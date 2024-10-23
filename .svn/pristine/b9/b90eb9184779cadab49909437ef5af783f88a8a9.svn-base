package com.golf.chat.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbCommonResultVO;
import com.golf.chat.vo.CbDeliveryVO;

import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.BaseServiceImpl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


@RestController
@RequestMapping("/kakao/cbDelivery")
@Api(value = "/kakao/cbDelivery", description = "택배조회")
public class CbDeliveryController extends BaseServiceImpl {
	
	@Autowired
	private GeneralDAOImpl _gDao; //GeneralDAOImpl 변환
	
	@PostMapping("/deliveryCnt")
    @ApiOperation(value = "접수 목록 갯수 조회", notes = "등록 된 택배접수 개수 (TT-1)")//여기
	@ApiResponses(value = {
		      @ApiResponse(code = 200, message = "성공", response = CbCommonResultVO.class)
	})
	public JSONObject DeliveryCnt(@RequestBody CbCommonVO cbCommon) throws Exception{
		int cnt = 0;
		int status = 200;

		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		obj.put("httpStatus","OK");

		try {			
			cnt = (int)_gDao.selectByKey("cbDelivery.selectByCnt", cbCommon);
		}catch(Exception e) {
			status = 500;
			System.out.println("DeliveryCnt Exception  : " + e);	
		}finally {
			data.put("status", status);	
			data.put("cnt", cnt);
			obj.put("result", data);			
			return obj;	
		}
	}
	
	@PostMapping("/deliveryList")
	@ApiOperation(value = "접수내역 조회", notes = "사용자가 접수한 내역 리스트 (TT-2)")//여기
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "성공", response = CbDeliveryVO.class)
	})
	public JSONObject DeliveryList(@RequestBody CbCommonVO cbCommon) throws Exception{
		JSONObject obj = new JSONObject();
		JSONArray kakaoArray = new JSONArray();
		obj.put("httpStatus","OK");	

		try {
			List<CbDeliveryVO> deliveryList = (List<CbDeliveryVO>)_gDao.selectList("cbDelivery.selectBySearch", cbCommon);
			
			for(CbDeliveryVO vo : deliveryList) {
				JSONObject data = new JSONObject();
				data.put("rcpNumber", vo.getRcpNumber());
				data.put("golfName", vo.getGolfName());
				data.put("cusReGolfName", vo.getCusReGolfName());
				data.put("delKind", vo.getDelKind());
				data.put("srType", vo.getSrType());
				data.put("rcpInfStat", vo.getRcpInfStat());
				data.put("pdPay", vo.getPdPay());
				data.put("orderId", vo.getOrderId());
				data.put("extComp", vo.getExtComp());
				data.put("pdBarcode", vo.getPdBarcode());
				data.put("pdRecDt", vo.getPdRecDt());
				data.put("regDate", vo.getRegDate());
				data.put("canReason", vo.getCanReason());
				data.put("cusReAddr1", vo.getCusReAddr1());
				data.put("cusReAddr2", vo.getCusReAddr2());
				data.put("extCompTelNum", vo.getExtCompTelNum());
				kakaoArray.add(data);
			}
		}catch(Exception e) {
			System.out.println("DeliveryList Exception  : " + e);	
		}finally {
			obj.put("result",kakaoArray);
			return obj;		
		}
	}
		
	@PostMapping("/deliveryPhoto")
	@ApiOperation(value = "골프백 사진", notes = "골프백 내부,외부 사진 조회 (TT-3-32)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "성공", response = CbDeliveryVO.class)
	})
	public JSONObject DeliveryPhoto(@RequestBody CbCommonVO cbCommon) throws Exception{
		String golfPhoto = "";
		String rcpInfSeq = "";
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray kakaoArray = new JSONArray();
		obj.put("httpStatus","OK");	

		try {			
			List<CbDeliveryVO> golfPhotoList = (List<CbDeliveryVO>)_gDao.selectList("cbDelivery.selectByGolfPhoto", cbCommon);
			
			if(golfPhotoList.size()>0) {
				for(CbDeliveryVO vo : golfPhotoList) {
					rcpInfSeq = Integer.toString(vo.getRcpInfSeq());
					golfPhoto += super.getConfig().getHost()+"/upload/files/pickup/"+rcpInfSeq+"/"+vo.getGolfPhoto()+"|";					
				}
				golfPhoto = golfPhoto.substring(0, golfPhoto.length()-1);
			}			
					
		}catch(Exception e) {
			System.out.println("DeliveryPhoto Exception  : " + e);	
		}finally {		
			data.put("golfPhoto", golfPhoto);
			kakaoArray.add(data);
			obj.put("result",kakaoArray);
			return obj;	
		}
	}
	
	@PostMapping("/deliveryDetail")
	@ApiOperation(value = "접수 상세내역 조회", notes = "상세 조회, 픽업예정일조회 (TT-3-30, TT-3-33-330)")//여기
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "성공", response = CbDeliveryVO.class)
	})
	public JSONObject DeliveryDetail(@RequestBody CbCommonVO cbCommon) throws Exception{
		cbCommon.setUserId("");
		JSONObject obj = new JSONObject();
		JSONArray kakaoArray = new JSONArray();
		obj.put("httpStatus","OK");	

		try {			
			CbDeliveryVO vo = (CbDeliveryVO)_gDao.selectOne("cbDelivery.selectBySearch", cbCommon);
			//접수기본정보
			JSONObject data = new JSONObject();
			data.put("cusReceiptNm", vo.getCusReceiptNm());
			data.put("cusReceiptTelNum", vo.getCusReceiptTelNum());
			data.put("gameDt", vo.getGameDt());
			data.put("gameEndDt", vo.getGameEndDt());		
			data.put("rcpInfStat", vo.getRcpInfStat());	
			data.put("delKind", vo.getDelKind());
			data.put("srType", vo.getSrType());
			data.put("pdGolfCnt", vo.getPdGolfCnt());
			data.put("pdCnt", vo.getPdCnt());			
			data.put("bostonCnt", vo.getBostonCnt());			
			data.put("pdPay", vo.getPdPay());		
			data.put("pdIndivPrice", vo.getPdIndivPrice());
			data.put("regDate", vo.getRegDate());
			data.put("orderId", vo.getOrderId());
			data.put("changeDeltypeYn", vo.getChangeDeltypeYn());
			
			//보내는곳
			data.put("cusSendNm", vo.getCusSendNm());
			data.put("golfName", vo.getGolfName());
			data.put("cusSendTelNum", vo.getCusSendTelNum());
			data.put("cusSendAddr1", vo.getCusSendAddr1());
			data.put("cusSendAddr2", vo.getCusSendAddr2());
			data.put("cusSendZip", vo.getCusSendZip());		
			data.put("picReqDt", vo.getPicReqDt());			
			data.put("sendGolfSeq", vo.getSendGolfSeq());	
			data.put("sendIslYn", vo.getSendIslYn());		
						
			//받는곳
			data.put("cusReNm", vo.getCusReNm());
			data.put("cusReTelNum", vo.getCusReTelNum());
			data.put("cusReGolfName", vo.getCusReGolfName());
			data.put("cusReAddr1", vo.getCusReAddr1());
			data.put("cusReAddr2", vo.getCusReAddr2());
			data.put("cusReZip", vo.getCusReZip());
			data.put("rtPicReqDt", vo.getRtPicReqDt());
			data.put("recvGolfSeq", vo.getRecvGolfSeq());
			data.put("recvIslYn", vo.getRecvIslYn());
			data.put("pdMemo", vo.getPdMemo());	
	
			//왕복도착지
			data.put("rtCusReNm", vo.getRtCusReNm());
			data.put("rtCusReTelNum", vo.getRtCusReTelNum());
			data.put("rtCusReAddr1", vo.getRtCusReAddr1());
			data.put("rtCusReAddr2", vo.getRtCusReAddr2());
			data.put("rtCusReZip", vo.getRtCusReZip());
			
			//배송정보
			data.put("pdBarcode", vo.getPdBarcode());
			data.put("extComp", vo.getExtComp());
			data.put("extCompTelNum", vo.getExtCompTelNum());
			
			kakaoArray.add(data);
					
		}catch(Exception e) {
			System.out.println("DeliveryDetail Exception  : " + e);	
		}finally {
			obj.put("result",kakaoArray);
			return obj;	
		}
	}
	
	@PostMapping("/deliveryRcp")
	@ApiOperation(value = "골프접수번호 조회", notes = "결제묶음번호 pair 조회 (TT-4)")
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "성공", response = CbDeliveryVO.class)
	})
	public JSONObject DeliveryRcp(@RequestBody CbCommonVO cbCommon) throws Exception{
		String rcpNumber = "";
		JSONObject obj = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray kakaoArray = new JSONArray();
		obj.put("httpStatus","OK");	

		try {			
			List<CbDeliveryVO> rcpNumberList = (List<CbDeliveryVO>)_gDao.selectList("cbDelivery.selectByRcpNumber", cbCommon);

			if(rcpNumberList.size()>0) {
				for(CbDeliveryVO vo : rcpNumberList) {
					rcpNumber += vo.getRcpNumber()+"|";
				}
	
				rcpNumber = rcpNumber.substring(0, rcpNumber.length()-1);
			}					
		}catch(Exception e) {
			System.out.println("DeliveryDetail Exception  : " + e);	
		}finally {
			data.put("rcpNumber", rcpNumber);
			kakaoArray.add(data);
			obj.put("result",kakaoArray);
			return obj;
		}		
	}
}
