package com.golf.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.golf.service.impl.CouponServiceImpl;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.model.CouponVO;

/**
 * 제휴사 쿠폰 관리 컨트롤러
 * GNB 메뉴의 [제휴쿠폰관리] 탭
 */
@Controller
@RequestMapping("/coupon")
public class CouponController extends PrtController{

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	CouponServiceImpl _couponService;

	/**
	 * 1. 제휴사 쿠폰 목록 화면 (메인 탭)
	 */
	@RequestMapping("/couponList.do")
	public ModelAndView couponList(CouponVO vo, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView("/coupon/couponList");

		int totalCount = _couponService.selectCouponListCnt(vo);
		List<Map<String, Object>> couponList = _couponService.selectCouponList(vo);

		mav.addObject("list", couponList);
		mav.addObject("totalCount", totalCount);
		mav.addObject("search", vo);

		return mav;
	}

	/**
	 * 2. 신규 쿠폰 발급 팝업창
	 */
	@RequestMapping("/couponIssuePopup.do")
	public ModelAndView couponIssuePopup(HttpServletRequest request) throws Exception {
		return new ModelAndView("/coupon/couponIssuePopup");
	}

	/**
	 * 3. 쿠폰 대량 발급 처리 (Ajax 호출)
	 */
	@RequestMapping(value = "/issueBulk.do", method = RequestMethod.POST)
	@ResponseBody
	public String issueBulk(@RequestBody Map<String, Object> paramMap, HttpSession session) {
		try {
			// 세션에서 로그인한 관리자 ID 가져오기
			String adminId = (String) session.getAttribute("admin");
			if (adminId == null || adminId.isEmpty()) {
				adminId = "superUser_092_02"; // 세션이 끊겼을 때를 대비한 하드코딩
			}
			paramMap.put("regUser", adminId);

			// 서비스의 [마스터 1건 + 디테일 난수 1000건] 트랜잭션 로직 호출
			_couponService.createBulkCoupons(paramMap);

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
}