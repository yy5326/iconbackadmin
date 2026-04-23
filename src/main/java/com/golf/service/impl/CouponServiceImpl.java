package com.golf.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.golf.service.model.CouponVO;

/**
 * 제휴사 쿠폰 발급 및 관리 서비스
 */
@Service("CouponService")
public class CouponServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

	/**
	 * 쿠폰 마스터 목록 카운트 조회
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public int selectCouponListCnt(CouponVO search) throws Exception {
		return getDAO().getCountBySearch("coupon.selectCouponListCnt", search);
	}

	/**
	 * 쿠폰 마스터 목록 조회 (페이징 포함)
	 * @param search
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectCouponList(CouponVO search) throws Exception {
		return (List<Map<String, Object>>) getDAO().selectBySearch("coupon.selectCouponList", search, "selectCouponListCnt");
	}

	/**
	 * 제휴사 쿠폰 대량 발급
	 * @param paramMap
	 * @throws Exception
	 */
	public void createBulkCoupons(Map<String, Object> paramMap) throws Exception {

		// 1. 등록자 세션 정보 세팅 (기존 히스토리 등록 방식 차용)
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);

		if(super.getConfig().getWebMode().equals("op")) {
			if(session.getAttribute("historyEmpCode") != null) {
				paramMap.put("regUser", session.getAttribute("historyEmpCode").toString());
			} else {
				paramMap.put("regUser", "관리자 API");
			}
		} else {
			paramMap.put("regUser", "local");
		}

		// 2. 쿠폰 마스터 등록 (XML의 useGeneratedKeys로 paramMap에 couponSeq가 담겨 반환됨)
		getDAO().insert("coupon.insertCouponMaster", paramMap);

		Long couponSeq = Long.valueOf(paramMap.get("couponSeq").toString());
		int targetCount = Integer.parseInt(paramMap.get("issueCount").toString());

		// 3. 지정된 수량만큼 중복 없는 난수 생성
		List<Map<String, Object>> detailList = new ArrayList<>();
		Set<String> uniqueCodes = new HashSet<>();

		while (uniqueCodes.size() < targetCount) {
			uniqueCodes.add(generateRandomCouponCode());
		}

		// 4. 생성된 난수들을 Map으로 묶어서 List에 담기
		for (String code : uniqueCodes) {
			Map<String, Object> detail = new HashMap<>();
			detail.put("couponCode", code);
			detail.put("couponSeq", couponSeq);
			detailList.add(detail);
		}

		// 5. 쿠폰 디테일 대량 등록 (Batch Insert)
		getDAO().insert("coupon.insertCouponDetailBulk", detailList);

		logger.info("제휴사 쿠폰 발급 완료 [쿠폰마스터번호: " + couponSeq + " / 발행수량: " + targetCount + "]");
	}

	/**
	 * 영문 대문자 + 숫자로 구성된 12자리 난수 생성 메서드
	 * @return String (예: ABCD-1234-EFGH)
	 */
	private String generateRandomCouponCode() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 12; i++) {
			if (i > 0 && i % 4 == 0) {
				sb.append("-");
			}
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return sb.toString();
	}
}