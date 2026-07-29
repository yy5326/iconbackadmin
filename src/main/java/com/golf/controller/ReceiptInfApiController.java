package com.golf.controller;

import java.util.List;

import javax.annotation.Resource;

import com.golf.service.model.GolfReceiptInfVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.golf.service.dao.GeneralDAOImpl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 골프 접수 발송 API 컨트롤러
 */
@RestController
public class ReceiptInfApiController extends PrtController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptInfApiController.class);

    @Resource
    GeneralDAOImpl _gDao;

    /**
     * 골프 접수 발송 목록 조회
     * RCP_NUMBER 목록을 IN절로 조회
     */
    @PostMapping("/receipt/list")
    @ApiOperation(value = "골프 접수 발송 목록 조회", notes = "골프 접수 발송 목록 조회")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "성공"),
        @ApiResponse(code = 400, message = "잘못된 파라메터"),
        @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<?> getReceiptInfList(@RequestBody GolfReceiptInfVO vo) {
        try {
            if (vo.getRcpNumberArray() == null || vo.getRcpNumberArray().isEmpty()) {
                return ResponseEntity.badRequest().body("rcpNumberArray는 필수입니다.");
            }

            List<GolfReceiptInfVO> list = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceipt.selectReceiptInfList", vo);

            logger.debug("골프 접수 발송 목록 조회 결과: {}건", list.size());

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            logger.error("골프 접수 발송 목록 조회 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
