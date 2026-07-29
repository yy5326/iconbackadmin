package com.golf.service.model; // 실제 패키지 경로에 맞게 조정하세요

import lombok.Data;
import com.golf.model.common.SearchVO;
import java.util.List;

/**
 * 제휴사 쿠폰 관리를 위한 VO
 */
public @Data class CouponVO extends SearchVO {

	private static final long serialVersionUID = 1L;

	// [1] 마스터 테이블 컬럼
	private Long couponSeq;        // 쿠폰 마스터 일련번호
	private Long partnerSeq;       // 제휴사 일련번호
	private String partnerName;    // 제휴사명 (JOIN 해서 가져올 이름)
	private String couponName;     // 쿠폰명
	private String discountType;   // 할인구분 (FIXED / PERCENT)
	private Integer discountValue; // 할인값
	private Integer issueCount;    // 발행 수량
	private Integer usedCount;     // 사용 수량 (통계용)
	private String startDate;      // 유효기간 시작일
	private String endDate;        // 유효기간 종료일
	private String useYn;          // 사용 여부
	private String regUser;        // 등록자
	private String regDate;        // 등록일시
	private String modUser;        // 수정자
	private String modDate;        // 수정일시

	// [2] 검색용 추가 파라미터
	private String searchCouponName; // 쿠폰명 검색어

	// [3] 리스트 체크박스용 (다중 선택/삭제 등에 활용)
	private List<Long> seqs;

	// ※ 주의: PhoneVO 같은 특별한 객체 변환 로직이 필요한 경우에만
	// GolfInfVO처럼 수동으로 Getter/Setter를 추가해주면 됩니다.
	// 쿠폰 정보는 단순 문자열/숫자이므로 추가 로직이 필요 없습니다.
}