package com.golf.controller;

import com.golf.chat.service.CbReceiveServiceImpl;
import com.golf.chat.service.RentalApiServiceImpl;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.chat.vo.CbReceiveResultVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.CarrierServiceImpl;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.util.StringUtil;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 아이콘렌탈 관련 API 컨트롤러
 * 참고 :
 *  - /kakao/cbReceive/receiptProcConfirm
 *  - CbReceiveController.java
 */
@RestController
@RequestMapping("/rental")
public class RentalApiController extends PrtController {

    @Resource
    GeneralDAOImpl _gDao;
    @Resource
    RentalApiServiceImpl _receiptService;
    @Resource
    CbReceiveServiceImpl _receiveService;
    @Resource
    SendTalkServiceImpl _sendTalkService;

    private static final Logger logger = LoggerFactory.getLogger(RentalApiController.class);

    @PostMapping("/receiptProcConfirm")
    @ApiOperation(value = "골프 접수/수정/예상운임비 확인", notes = "<DR-3>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
            @ApiResponse(code = 311, message = "잘못된 파라메터")
    })
    public JSONObject receiptProcConfirm(@RequestBody List<CbReceiptInfoVO> list){
        JSONObject responseObj = new JSONObject();
        responseObj.put("httpStatus", "OK");
        JSONArray resultArray = new JSONArray();

        try {

            if (CollectionUtils.isEmpty(list)) {
                logger.info("요청한 데이터 없음");
                resultArray.add(buildErrorResult("요청한 데이터 없음"));
                responseObj.put("result", resultArray);
                return responseObj;
            }

            // 1: 편도, 2: 왕복 => 렌탈은 무조건 왕복 2
            String delKind = list.get(0).getDelKind();
            
            // 왕복건인데 list가 2개가 아니면 패스
            if ("2".equals(delKind) && list.size() != 2) {
                logger.info("왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)");
                resultArray.add(buildErrorResult("왕복 건 입력 오류(왕복 건은 데이터가 2개 입력되어야 합니다)"));
                responseObj.put("result", resultArray);
                return responseObj;
            }
            
            // 왕복이 아니면 패스
            if (!"2".equals(delKind)) {
                logger.info("왕복 유형 코드 오류");
                resultArray.add(buildErrorResult("아이콘 렌탈은 2(왕복)이어야 합니다."));
                responseObj.put("result", resultArray);
                return responseObj;
            }
            
            // 접수 등록
            List<GolfReceiptInfVO> receiptList = _receiptService.receiptProcConfirm(list);
            logger.info("아이콘 렌탈 접수요청 확인");

            // 왕복 1차 정보
            GolfReceiptInfVO firstReceipt = receiptList.get(0);
            String regDate = _receiveService.selectDetailByRcpSeq(firstReceipt.getRcpSeq(), "S").getRegDate();

            // 왕복건 결제 정보
            // 챗봇접수번호 - 왕복시 그룹번호 rcpSeq / 편도시 rcpNumber
            String orderId = "GRP" + firstReceipt.getRcpSeq(); // 결제용 묶음코드
            // getPdPay값 전부 합산
            int totalPay = receiptList.stream().mapToInt(GolfReceiptInfVO::getPdPay).sum();
            for (GolfReceiptInfVO vo : receiptList) {
                totalPay += vo.getPdPay();
            }
            StringBuilder rcpNumberBuilder = new StringBuilder();
            for (GolfReceiptInfVO vo : receiptList) {
                rcpNumberBuilder.append(vo.getRcpNumber()).append("|");
            }

            String rcpNumber = rcpNumberBuilder.substring(0, rcpNumberBuilder.length() - 1);

            JSONObject data = new JSONObject();
            data.put("cbPay", totalPay);
            data.put("orderId", orderId);
            data.put("rcpNumber", rcpNumber);
            data.put("regDate", regDate);

            logger.info("챗봇접수번호 : {}", rcpNumber);
            logger.info("결제용 묶음코드 : {}", orderId);
            logger.info("합산 금액 : {}", totalPay);
            logger.info("결제 금액 : {}", receiptList.get(0).getPdPay());
            logger.info("접수 상태 : {}", firstReceipt.getRcpInfStat());
            logger.info("접수 일시 : {}", regDate);
            logger.info("할인 : {}", firstReceipt.getDiscount());

            resultArray.add(data);

        } catch ( Exception e ) {
            // 영업소 프로그램 및 기타 에러
            logger.error("영업소 프로그램 및 기타 에러", e);
            resultArray.add(buildErrorResult(e.getMessage()));
        }
        // 결과 response
        responseObj.put("result", resultArray);
        return responseObj;
    }

    @PostMapping("/receiptProcConfirm")
    @ApiOperation(value = "골프 접수/수정/예상운임비 확인", notes = "<DR-3>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
            @ApiResponse(code = 311, message = "잘못된 파라메터")
    })
    public JSONObject receiptProcConfirm(@RequestBody List<CbReceiptInfoVO> list){

    }


    private JSONObject buildErrorResult(String errorMsg) {
        JSONObject error = new JSONObject();
        error.put("cbPay", 0);
        error.put("cbReceiptStatus", "-1");
        error.put("errorMsg", errorMsg);
        return error;
    }

}

