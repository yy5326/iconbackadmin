package com.golf.controller;

import com.golf.chat.service.CbReceiveServiceImpl;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.chat.vo.CbReceiveResultVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.RentalServiceImpl;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


// TODO 아이콘 렌탈일시 데이터는 등록.
// TODO 중요!! 골프택배용 카톡 메시지는 발송되면 안됨.

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
    RentalServiceImpl rentalServiceImpl;
    @Resource
    CbReceiveServiceImpl _receiveService;

    private static final Logger logger = LoggerFactory.getLogger(RentalApiController.class);


    @PostMapping("/receiptProcConfirm/{orderNo}")
    @ApiOperation(value = "아이콘렌탈 골프 접수/수정/예상운임비 확인", notes = "<DR-3>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
            @ApiResponse(code = 311, message = "잘못된 파라메터")
    })
    public JSONObject receiptProcConfirm(@RequestBody List<CbReceiptInfoVO> list
        , @PathVariable("orderNo") Long orderNo){

        JSONObject responseObj = new JSONObject();
        responseObj.put("httpStatus", "OK");
        JSONArray resultArray = new JSONArray();

        try {

            if (CollectionUtils.isEmpty(list) || orderNo == null) {
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
            List<GolfReceiptInfVO> receiptList = rentalServiceImpl.receiptProcConfirm(list);
            logger.info("아이콘 렌탈 접수요청 확인");

            // 왕복 1차 정보
            GolfReceiptInfVO firstReceipt = receiptList.get(0);
            String regDate = _receiveService.selectDetailByRcpSeq(firstReceipt.getRcpSeq(), "S").getRegDate();

            // 왕복건 결제 정보
            String orderId = "GRP" + firstReceipt.getRcpSeq(); // 결제용 묶음코드
            // getPdPay값 전부 합산
            int totalPay = receiptList.stream().mapToInt(GolfReceiptInfVO::getPdPay).sum();

            StringBuilder rcpNumberBuilder = new StringBuilder();
            for (GolfReceiptInfVO vo : receiptList) {
                rcpNumberBuilder.append(vo.getRcpNumber()).append("|");

                JSONObject data = new JSONObject();
                data.put("rcpInfSeq", vo.getRcpInfSeq());
                data.put("rcpSeq", vo.getRcpSeq());
                data.put("rcpInfStat", vo.getRcpInfStat());
                data.put("rcpNumber", vo.getRcpNumber());
                data.put("srType", vo.getSrType());
                data.put("pdEtcPay", vo.getPdEtcPay());
                data.put("pdDosunPay", vo.getPdDosunPay());
                data.put("pdDeliPay", vo.getPdDeliPay());
                data.put("iconFee", vo.getIconFee());
                data.put("pdPay", vo.getPdPay());
                data.put("regDate", regDate);
                resultArray.add(data);

            }
            String rcpNumber = rcpNumberBuilder.substring(0, rcpNumberBuilder.length() - 1);


            logger.info("챗봇접수번호 : {}", rcpNumber);
            logger.info("결제용 묶음코드 : {}", orderId);
            logger.info("합산 금액 : {}", totalPay);
            logger.info("결제 금액 : {}", receiptList.get(0).getPdPay());
            logger.info("접수 상태 : {}", firstReceipt.getRcpInfStat());
            logger.info("접수 일시 : {}", regDate);
            logger.info("할인 : {}", firstReceipt.getDiscount());


            responseObj.put("orderNo", orderNo);
            responseObj.put("orderId", orderId);
            responseObj.put("totalPay", totalPay);

        } catch ( Exception e ) {
            // 영업소 프로그램 및 기타 에러
            logger.error("영업소 프로그램 및 기타 에러", e);
            resultArray.add(buildErrorResult(e.getMessage()));
        }
        // 결과 response
        responseObj.put("list", resultArray);
        logger.debug("responseObj {}", responseObj);
        return responseObj;
    }

    @GetMapping("/test")
    @ApiOperation(value = "아이콘렌탈 ", notes = "<DR-3>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
            @ApiResponse(code = 311, message = "잘못된 파라메터")
    })
//    public JSONObject receiptSync(@RequestBody List<Integer> rcpInfSeqs) throws Exception {
    public JSONObject test() throws Exception {
        JSONObject responseObj = new JSONObject();
        responseObj.put("httpStatus", "OK");
//        logger.debug("rcpInfSeqs {}", new Gson().toJson(rcpInfSeqs));
        GolfReceiptInfVO vo = new GolfReceiptInfVO();
        vo.setRcpKind("6");
        vo.setRcpInfSeq(62);
        vo.setRcpSeq(34);
        vo.setRcpNumber("GF2025072362");

        rentalServiceImpl.updateApi(vo,"test");

        return responseObj;
    }

    @PostMapping("/getExtraPrice")
    @ApiOperation(value = "골프 운임비 확인", notes = "<DR-3>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "확인 성공", response = CbReceiveResultVO.class),
            @ApiResponse(code = 311, message = "잘못된 파라메터")
    })
    public JSONObject getExtraPrice(@RequestBody List<GolfReceiptInfVO> golfReceiptInfList){

        /**
         * 필수 전달값
         * sendGolfSeq
         * cusSendAddr1
         * cusSendAddr2
         *
         * recvGolfSeq
         * cusReAddr1
         * cusReAddr2
         */


        JSONObject responseObj = new JSONObject();
        responseObj.put("httpStatus", "OK");
        JSONArray resultArray = new JSONArray();

        int pdCnt = 1; // 골프백 수 -> 아이콘렌탈은 무조건 1개
        String pdIndivPrice = "200"; // 기본단가

        int pdDeliPay = 0;
        int pdDosunPay = 0;
        int iconFee = 0;
        int extraPrice = 0;
        int pdPay = 0;

        if(golfReceiptInfList != null && !golfReceiptInfList.isEmpty()){

            for (GolfReceiptInfVO golfReceiptInfVo : golfReceiptInfList) {
                golfReceiptInfVo.setPdCnt(pdCnt); // 골프백 수
                golfReceiptInfVo.setPdIndivPrice(pdIndivPrice); //골프백 단가

                int sendGolfFee = 0;
                int recvGolfFee = 0;

                int sendGolfSeq = golfReceiptInfVo.getSendGolfSeq();
                if(sendGolfSeq > 0) {
                    // 보내는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
                    GolfInfVO sendGolfInfVo = rentalServiceImpl.getGolfInfo((long) sendGolfSeq);
                    sendGolfFee = sendGolfInfVo.getFee4();
                }

                int recvGolfSeq = golfReceiptInfVo.getRecvGolfSeq();
                if(recvGolfSeq > 0) {
                    // 받는 분 도착영업소명, 도착영업코드 set, 골프장일 경우 주소 및 우편번호 set
                    GolfInfVO recvGolfInfVo = rentalServiceImpl.getGolfInfo((long) recvGolfSeq);
                    recvGolfFee = recvGolfInfVo.getFee4();
                }

                /** 배송비 계산 **/
                pdDeliPay += rentalServiceImpl.calculateDeliveryPay(20000, pdCnt);

                /** 도서산간 계산 **/
                // 보내는 사람과 받는 사람 도서산간 여부 초기화
                golfReceiptInfVo.setSendIslYn("N");
                golfReceiptInfVo.setRecvIslYn("N");
                pdDosunPay += rentalServiceImpl.calculateDosunPay(golfReceiptInfVo, 5000);

                /** 아이콘 수수료 계산 **/
                iconFee += rentalServiceImpl.calculateIconFeeByKind(sendGolfFee, recvGolfFee, pdCnt, 4500);

                /** 할증 운임가 **/
                extraPrice += rentalServiceImpl.getExtraPrice(pdIndivPrice);

                /** 총 계산 **/
                pdPay += rentalServiceImpl.calculateTotalPay(pdDosunPay, pdDeliPay, iconFee, extraPrice, 1);

                logger.debug("========== golfReceiptInfVo ============");
                logger.debug("pdDeliPay = {}", pdDeliPay);
                logger.debug("pdDosunPay = {}", pdDosunPay);
                logger.debug("iconFee = {}", iconFee);
                logger.debug("extraPrice = {}", extraPrice);
                logger.debug("pdPay = {}", pdPay);
                logger.debug("=======================================");
            }
        }


        logger.debug("========== 총합계 ============");
        logger.debug("pdDeliPay = {}", pdDeliPay);
        logger.debug("pdDosunPay = {}", pdDosunPay);
        logger.debug("iconFee = {}", iconFee);
        logger.debug("extraPrice = {}", extraPrice);
        logger.debug("pdPay = {}", pdPay);
        logger.debug("=======================================");
        
        responseObj.put("pdDeliPay", pdDeliPay);
        responseObj.put("iconFee", iconFee);
        responseObj.put("extraPrice", extraPrice);
        responseObj.put("pdPay", pdPay);

        return responseObj;
    }


    private JSONObject buildErrorResult(String errorMsg) {
        JSONObject error = new JSONObject();
        error.put("cbPay", 0);
        error.put("cbReceiptStatus", "-1");
        error.put("errorMsg", errorMsg);
        return error;
    }

}

