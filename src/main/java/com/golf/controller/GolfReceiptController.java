package com.golf.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.golf.service.impl.KdngeAPI;
import com.golf.util.IconGolfItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.golf.model.common.StringPair;
import com.golf.service.GlobalProperty;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfReceiptServiceImpl;
import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.service.model.StatusPayVO;
import com.golf.service.model.StatusVO;
import com.golf.service.model.search.GolfReceiptSearchVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.talk.vo.SendRequestVO;
import com.golf.util.ExcelDownloadUtil;
import com.golf.util.StringUtil;
import com.google.gson.Gson;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 골프택배 컨트롤러
 * @author khs
 *
 */
@Controller
@RequestMapping(value = "/receipt")
public class GolfReceiptController extends PrtController {

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	@Resource
	GeneralDAOImpl _gDao;

	@Resource
	GolfReceiptServiceImpl _receiptService;
	
	@Resource
	SendTalkServiceImpl _sendTalkService;
		
	@Resource
	CommonController common; 
	
	@Autowired
	GlobalProperty _globalProperty;

	@Autowired
	private KdngeAPI kdngeApi;
	
	/** 택배조회 리스트
	 *
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptList.do")
	public String GolfReceiptList(HttpServletRequest request,  HttpSession session, GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		
		String sUrl = request.getRequestURI();
		//발송 및 토큰갱신, 토큰값얻기, 위탁장소 지정관련 페이지가 아닌경우
		
		//logger.info("파트너 리다이렉트 정보 : {} ", session.getAttribute("partner"));
		
		if(!(_globalProperty.getWebMode().equals("op"))) {
			return "redirect:/receipt/" + session.getAttribute("partner") +"/receiptList.do";
		} else {
			if(!"superUser_092_02".equals(session.getAttribute("partner"))) {
                return "redirect:/receipt/" + session.getAttribute("partner") +"/receiptList.do";
            }
		
		
			if (search.getRecordCount() < 20)
				search.setRecordCount(10);
			
			if(StringUtil.isEmpty(search.getStartDate())) {
				search.setStartDate(StringUtil.getDateToString(StringUtil.getDate(0, 0, -7), "yyyy-MM-dd"));
			}		
	
			if(StringUtil.isEmpty(search.getEndDate())) {
				search.setEndDate(StringUtil.getDateToString("yyyy-MM-dd"));
			}
	
			if(StringUtil.isEmpty(search.getType2())) {
				search.setType2("regDate");
			}
			
			List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectList", search);
			
			model.addAttribute("list", receiptList);
	
			//인수완료시간 UPDATE
			try {
				if(receiptList.size()>0) {
					List<GolfReceiptInfVO> apiList = common.scTimeApi(receiptList, "list");
					model.addAttribute("list", apiList);
				}
			}catch(Exception e) {
				System.out.println("receiptList 인수완료시간 Exceptin : " + e);
			}
			// 하단 통계
			Map<String, Object> statsMap = (Map<String, Object>) _gDao.selectOne("golfReceiptInf.selectTotalStats", search);
			search.getPaging().setTotalCount(Integer.parseInt(statsMap.get("TOTAL_COUNT").toString()));
	
			// 접수 상태
			List<StringPair> type1 = new ArrayList<StringPair>();
			type1.add(new StringPair("접수대기", "4"));
			type1.add(new StringPair("골프접수", "0"));
			type1.add(new StringPair("픽업요청", "1"));
			type1.add(new StringPair("택배접수", "2"));
			type1.add(new StringPair("배송완료", "3"));
			type1.add(new StringPair("골프취소", "7"));
			type1.add(new StringPair("픽업취소", "8"));
			type1.add(new StringPair("택배취소", "9"));
	
			// 구분
			List<StringPair> type4 = new ArrayList<StringPair>();
			type4.add(new StringPair("왕복", "2"));
			type4.add(new StringPair("편도", "1"));
	
	
			// 날짜
			List<StringPair> type2 = new ArrayList<StringPair>();
			type2.add(new StringPair("골프접수일", "regDate"));
			type2.add(new StringPair("픽업희망일", "picReqDt"));
			type2.add(new StringPair("택배접수일", "pdRecDt"));
	
			// 결제상태
			List<StringPair> type5 = new ArrayList<StringPair>();
			type5.add(new StringPair("입금대기", "0"));
			type5.add(new StringPair("결제완료", "1"));
			type5.add(new StringPair("결제취소", "9"));
	
			// 검색조건
			List<StringPair> type3 = new ArrayList<StringPair>();
			type3.add(new StringPair("골프 접수번호", "rcpNumber"));
			type3.add(new StringPair("그룹번호", "orderId"));
			type3.add(new StringPair("발송 영업소", "brStrNm"));
			type3.add(new StringPair("도착 영업소", "brEndNm"));
			type3.add(new StringPair("접수자", "cusReceiptNm"));
			type3.add(new StringPair("보낸 분", "cusSendNm"));
			type3.add(new StringPair("받는 분", "cusReNm"));
			type3.add(new StringPair("외부업체", "extComp"));
			type3.add(new StringPair("도착 골프장", "recvGolfName"));
	
			// 등록구분 상태
			List<StringPair> type6 = new ArrayList<StringPair>();
			type6.add(new StringPair("아이콘", "0"));
//			type6.add(new StringPair("카카오", "1"));
			type6.add(new StringPair("스마트", "2"));
			type6.add(new StringPair("현대C", "3"));
			type6.add(new StringPair("짐캐리", "4"));
			type6.add(new StringPair("팬스타", "9"));

			model.addAttribute("search", search);
			model.addAttribute("statsMap", statsMap);
	
			model.addAttribute("type1", type1);
			model.addAttribute("type2", type2);
			model.addAttribute("type3", type3);
			model.addAttribute("type4", type4);
			model.addAttribute("type5", type5);
			model.addAttribute("type6", type6);
	
			return "/receipt/receiptList";
		}
	}
	
	
	/** 택배조회 리스트 (파트너)
	 * 
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{partner}/receiptList.do")
	public String GolfReceiptList(@PathVariable String partner, HttpServletRequest request,GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		HttpSession session = request.getSession();

		boolean isValidPartner = true;
		switch(partner) {
		// 첫자리 : RcpKind , 두번쨰 : 연동방식(0:api 1:view 9:super) , 세번째 : 구현단계(0:개발, 1:테스트, 2:운영)
			case "superUser_092_02" : search.setType6(""); // 아이콘 및 슈퍼유저
				break;
//			case "kakao_102" : search.setType6("1"); // 카카오챗봇
//				break;
			case "score_202" : search.setType6("2"); // 스마트스코어
				break;
			case "hyundaiCard_311" : search.setType6("3"); // 현대카드
				break;
//			case "lotteRent_401" : search.setType6("4"); // 롯데렌터카
//				break;
            case "zimcarry_111" : search.setType6("4"); // 짐캐리
                break;
			//default : search.setType6("99");
			default :
				isValidPartner = false; // 정의되지 않은 파트너
				break;
		}

		// 잘못된 경로로 접근 시 메인이나 에러페이지로 튕겨냄
		if(!isValidPartner || "null".equals(partner)) {
			session.removeAttribute("partner"); // 잘못된 파트너 정보 삭제
			return "redirect:/receipt/receiptList.do";
		}

		session.setAttribute("partner", partner);
		logger.info("파트너 정보 : {}", session.getAttribute("partner"));
		
		if (search.getRecordCount() < 20)
			search.setRecordCount(10);
		
		if(StringUtil.isEmpty(search.getStartDate())) {
			search.setStartDate(StringUtil.getDateToString(StringUtil.getDate(0, 0, -7), "yyyy-MM-dd"));
		}		

		if(StringUtil.isEmpty(search.getEndDate())) {
			search.setEndDate(StringUtil.getDateToString("yyyy-MM-dd"));
		}

		if(StringUtil.isEmpty(search.getType2())) {
			search.setType2("regDate");
		}
		
		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectList", search);
		model.addAttribute("list", receiptList);

		//인수완료시간 UPDATE
		try {
			if(receiptList.size()>0) {
				List<GolfReceiptInfVO> apiList = common.scTimeApi(receiptList, "list");
				model.addAttribute("list", apiList);
			}
		}catch(Exception e) {
			System.out.println("receiptList 인수완료시간 Exceptin : " + e);
		}
		// 하단 통계
		Map<String, Object> statsMap = (Map<String, Object>) _gDao.selectOne("golfReceiptInf.selectTotalStats", search);
		search.getPaging().setTotalCount(Integer.parseInt(statsMap.get("TOTAL_COUNT").toString()));

		// 접수 상태
		List<StringPair> type1 = new ArrayList<StringPair>();
		type1.add(new StringPair("접수대기", "4"));
		type1.add(new StringPair("골프접수", "0"));
		type1.add(new StringPair("픽업요청", "1"));
		type1.add(new StringPair("택배접수", "2"));
		type1.add(new StringPair("배송완료", "3"));
		type1.add(new StringPair("골프취소", "7"));
		type1.add(new StringPair("픽업취소", "8"));
		type1.add(new StringPair("택배취소", "9"));

		// 구분
		List<StringPair> type4 = new ArrayList<StringPair>();
		type4.add(new StringPair("왕복", "2"));
		type4.add(new StringPair("편도", "1"));


		// 날짜
		List<StringPair> type2 = new ArrayList<StringPair>();
		type2.add(new StringPair("골프접수일", "regDate"));
		type2.add(new StringPair("픽업희망일", "picReqDt"));
		type2.add(new StringPair("택배접수일", "pdRecDt"));

		// 결제상태
		List<StringPair> type5 = new ArrayList<StringPair>();
		type5.add(new StringPair("입금대기", "0"));
		type5.add(new StringPair("결제완료", "1"));
		type5.add(new StringPair("결제취소", "9"));

		// 검색조건
		List<StringPair> type3 = new ArrayList<StringPair>();
		type3.add(new StringPair("골프 접수번호", "rcpNumber"));
		type3.add(new StringPair("그룹번호", "orderId"));
		type3.add(new StringPair("발송 영업소", "brStrNm"));
		type3.add(new StringPair("도착 영업소", "brEndNm"));
		type3.add(new StringPair("접수자", "cusReceiptNm"));
		type3.add(new StringPair("보낸 분", "cusSendNm"));
		type3.add(new StringPair("받는 분", "cusReNm"));
		type3.add(new StringPair("외부업체", "extComp"));
		type3.add(new StringPair("도착 골프장", "recvGolfName"));

		// 등록구분 상태
		List<StringPair> type6 = new ArrayList<StringPair>();
//		type6.add(new StringPair("아이콘", "0"));
//		type6.add(new StringPair("카카오", "1"));
//		type6.add(new StringPair("스마트", "2"));
//		type6.add(new StringPair("현대C", "3"));
//		type6.add(new StringPair("짐캐리", "4"));
		
		
		model.addAttribute("search", search);
		model.addAttribute("statsMap", statsMap);

		model.addAttribute("type1", type1);
		model.addAttribute("type2", type2);
		model.addAttribute("type3", type3);
		model.addAttribute("type4", type4);
		model.addAttribute("type5", type5);
		model.addAttribute("type6", type6);

		return "/receipt/receiptList";
	}

	/**
	 * 택배접수 뷰
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptWrite.do")
	public String GolfReceiptWrite(GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		if(session.getAttribute("partner") != null) {
            return "redirect:/receipt/" + session.getAttribute("partner") +"/receiptWrite.do";
        }
		if(!(_globalProperty.getWebMode().equals("op")) && !("superUser_092_02".equals(session.getAttribute("partner")))) {
			return "redirect:/receipt/" + session.getAttribute("partner") +"/receiptList.do";
		} else {
			//국번 폰번호 불러오기
			model.addAttribute("phoneList", StringUtil.getMobilePhoneList());
			model.addAttribute("search", search);
	
			return "/receipt/receiptWrite";
		}
	}

    /**
     * 파트너 택배접수 뷰
     * @param vo
     * @param result
     * @param model
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{partner}/receiptWrite.do")
    public String PartnerGolfReceiptWrite(@PathVariable String partner, GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		// [유효성 검증] 허용된 파트너 리스트인지 확인
		List<String> allowedPartners = Arrays.asList("superUser_092_02", "score_202", "hyundaiCard_311", "zimcarry_111");
		if (partner == null || "null".equalsIgnoreCase(partner) || !allowedPartners.contains(partner)) {
			// 유효하지 않은 파트너라면 목록 페이지로
			return "redirect:/receipt/receiptList.do";
		}

		if(result.hasErrors()) {
            return super.setBindingResult(result, model);
        }
            //국번 폰번호 불러오기
            model.addAttribute("phoneList", StringUtil.getMobilePhoneList());
            model.addAttribute("search", search);
            model.addAttribute("partner", partner);

			List<IconGolfItem> filteredItems;
			if ("zimcarry_111".equals(partner)) {
				// 짐캐리일 경우 RCP_KIND가 "4"인 아이템만 추출
				filteredItems = Arrays.stream(IconGolfItem.values())
						.filter(item -> "4".equals(item.getRcpKind()))
						.collect(Collectors.toList());
			} else {
				// 그 외 파트너는 일단 전체를 주거나, 빈 리스트 혹은 기본 리스트 설정
				filteredItems = Arrays.asList(IconGolfItem.values());
			}
			model.addAttribute("iconGolfItems", filteredItems);
            return "/receipt/receiptWrite";

    }
	
	/**
	 * 택배접수 복사뷰
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptWriteCp.do", method=RequestMethod.POST)
	public String GolfReceiptWriteCopy(GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		GolfReceiptInfVO infoVO = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", search.getRcpISeq());
		
		//국번 폰번호 불러오기
		model.addAttribute("phoneList", StringUtil.getMobilePhoneList());
		model.addAttribute("search", search);
		model.addAttribute("vo", infoVO);
		
		return "/receipt/receiptWriteCp";
	}
	

	/**
	 * 택배접수 입력 확인
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptProcConfirm.do", method=RequestMethod.POST)
	public String GolfReceiptProcConfirm(GolfReceiptMstVO mstVO, GolfReceiptInfVO list, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);
		Gson gson = new Gson();

		model.addAttribute("vo", mstVO);
		model.addAttribute("list", list.getList());
		model.addAttribute("jsonVO", gson.toJson(mstVO));
		model.addAttribute("jsonList", gson.toJson(list));

		return "/receipt/receiptWriteConfirm";
	}


	/**
	 * 택배접수 등록 처리
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptProc.do", method=RequestMethod.POST)
	@ResponseBody
	public String GolfReceiptProc(@RequestBody String jsonData, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if (result.hasErrors()) return super.setBindingResult(result, model);

		Gson gson = new Gson();
		GolfReceiptMstVO mstVO = gson.fromJson(jsonData, GolfReceiptMstVO.class);
		GolfReceiptInfVO list = gson.fromJson(jsonData, GolfReceiptInfVO.class);

		//등록
		try {
			_receiptService.insertReceipt(mstVO, list.getList());
			// 알림톡 전송
			list.getList().get(0).setDelKind(mstVO.getDelKind());
			/*if ( !super.getConfig().getWebMode().equals("op") ) {
				_sendTalkService.sendPayRequest(list.getList());
			}*/
			_sendTalkService.sendPayRequest(list.getList(), mstVO);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new Gson().toJson("false");
		}
		return new Gson().toJson("true");
	}

	/**
	 * 제휴사 택배접수 등록 처리
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/partnerReceiptProc.do", method=RequestMethod.POST)
	public String PartnerReceiptProc(GolfReceiptMstVO mstVO, GolfReceiptInfVO listVO, RedirectAttributes rttr) throws Exception {
		try {
			if ("zimcarry_111".equals(mstVO.getPartner())) {
				mstVO.setDelKind("1"); // 편도 고정

				GolfReceiptInfVO firstInf = listVO.getList().get(0);

				// [추가] 짐캐리 전용 기본값 세팅
				firstInf.setSrType("S");
				firstInf.setRcpKind("4");
				firstInf.setRcpInfStat("0"); // 골프접수 상태
				firstInf.setPayStat("1");    // 결제완료 상태
				firstInf.setPrdType(1);      // 골프택배

				// [영업소 코드 매칭] 우편번호로 실제 6자리 영업소 코드를 조회.
				try {
					GolfInfVO searchVO = new GolfInfVO();
					searchVO.setType1("zipcode");
					searchVO.setKeyword(firstInf.getCusSendZip()); // 보내는 분 우편번호 기준

					Map<String, String> branchMap = kdngeApi.getBranchInfo(searchVO);
					if (branchMap != null && branchMap.get("brCode") != null) {
						firstInf.setBrStrCode(branchMap.get("brCode")); // 예: 311143
						firstInf.setBrStrNm(branchMap.get("brNm"));
					}

					// 도착지는 보통 물류센터나 특정 골프장이므로 고정하거나 해당 우편번호로 조회
					// 만약 고정이라면:
					firstInf.setBrEndCode("331003");
					firstInf.setBrEndNm("강원홍천상오안74");
				} catch (Exception e) {
					logger.error("경동 영업소 정보 조회 실패: " + e.getMessage());
				}

				// [단가 보정]단가가 0이면 거절, 최소 금액 강제로 세팅.
				/*if (firstInf.getPdPay() <= 0) {
					firstInf.setPdPay(1);     // 테스트 성공을 위한 임시금액 (1만원)
					firstInf.setPdDeliPay(1);
					firstInf.setPdIndivPrice("1");
				}*/
				if (firstInf.getPdIndivPrice() == null || "0".equals(firstInf.getPdIndivPrice()) || "".equals(firstInf.getPdIndivPrice())) {
					firstInf.setPdIndivPrice("200");
				}

			}

			_receiptService.insertReceipt(mstVO, listVO.getList());

			try {
				_sendTalkService.sendPayRequest(listVO.getList(), mstVO);
			} catch (Exception e) {
				logger.error("알림톡 발송 실패: " + e.getMessage());
			}

			rttr.addFlashAttribute("saveMsg", "정상적으로 접수되었습니다.");

			// [수정] 파트너 코드가 있으면 해당 경로를 유지하도록 리다이렉트 경로 변경
			if (mstVO.getPartner() != null && !mstVO.getPartner().isEmpty()) {
				return "redirect:/receipt/" + mstVO.getPartner() + "/receiptWrite.do";
			}

			return "redirect:/receipt/receiptWrite.do";

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rttr.addFlashAttribute("saveMsg", "저장에 실패하였습니다.");

			if (mstVO.getPartner() != null && !mstVO.getPartner().isEmpty()) {
				return "redirect:/receipt/" + mstVO.getPartner() + "/receiptWrite.do";
			}
			return "redirect:/receipt/receiptWrite.do";
		}
	}

	/**
	 * 택배조회 상세보기
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/receiptDetail.do")
	public String GolfReceiptDetail(GolfReceiptSearchVO search, BindingResult result, Model model) {
		if (result.hasErrors())
			return super.setBindingResult(result, model);


		GolfReceiptInfVO infoVO = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", search.getRcpISeq());

		model.addAttribute("vo", infoVO);
		model.addAttribute("search", search);

		return "/receipt/receiptDetailLayer";
	}
	
	/**
	 * 택배조회 상세보기 (파트너)
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{partner}/receiptDetail.do")
	public String GolfReceiptDetail(@PathVariable String partner, HttpServletRequest request, GolfReceiptSearchVO search, BindingResult result, Model model) {
		if (result.hasErrors())
			return super.setBindingResult(result, model);
		
		GolfReceiptInfVO infoVO = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", search.getRcpISeq());
		// 241126 임시 주석
//		if(!(_globalProperty.getWebMode().equals("op"))) {
//			infoVO.setPdDeliPay(infoVO.getPdDeliPay() + infoVO.getIconFee());
//			infoVO.setIconFee(0);
//		}
		
		model.addAttribute("vo", infoVO);
		model.addAttribute("search", search);

		return "/receipt/receiptDetailLayer";
	}

	/**
	 * 택배조회 상세보기 수정 뷰
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptDetailWrite.do")
	public String GolfReceiptDetailWrite(GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if (result.hasErrors())
			return super.setBindingResult(result, model);

		GolfReceiptInfVO infoVO = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", search.getRcpISeq());
		List<String> phoneList = StringUtil.getMobilePhoneList();

		
		// 241126 임시 주석
//		if(!(_globalProperty.getWebMode().equals("op"))) {
//			infoVO.setPdDeliPay(infoVO.getPdDeliPay() + infoVO.getIconFee());
//			infoVO.setIconFee(0);
//		}
		
		
		model.addAttribute("vo", infoVO);
		model.addAttribute("pList", phoneList);
		model.addAttribute("search", search);

		return "/receipt/receiptDetailWriteLayer";
	}
	
	/**
	 * 택배조회 상세보기 수정 뷰
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{partner}/receiptDetailWrite.do")
	public String GolfReceiptDetailWrite(@PathVariable String partner, HttpServletRequest request, GolfReceiptSearchVO search, BindingResult result, Model model) throws Exception {
		if (result.hasErrors())
			return super.setBindingResult(result, model);

		GolfReceiptInfVO infoVO = (GolfReceiptInfVO) _gDao.selectByKey("golfReceiptInf.selectDetailByKey", search.getRcpISeq());
		List<String> phoneList = StringUtil.getMobilePhoneList();

		
		// 241126 임시 주석
//		if(!(_globalProperty.getWebMode().equals("op"))) {
//			infoVO.setPdDeliPay(infoVO.getPdDeliPay() + infoVO.getIconFee());
//			infoVO.setIconFee(0);
//		}
		
		
		model.addAttribute("vo", infoVO);
		model.addAttribute("pList", phoneList);
		model.addAttribute("search", search);

		return "/receipt/receiptDetailWriteLayer";
	}

	
	/**
	 * 택배접수 수정 처리(택배조회>수정)
	 * @param vo
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping("/receiptUpdateAjax.do")
	@ResponseBody
	public String GolfReceiptUpdate(@RequestBody(required = false) String body, GolfReceiptSearchVO search, GolfReceiptMstVO mstVo, GolfReceiptInfVO infVo, BindingResult result, Model model) {
		if (result.hasErrors())
			return super.setBindingResult(result, model);

		Map<String, Object> map = new HashMap<String, Object>();

		String sts = "";
		String rs = "";


		try {
			rs =_receiptService.updateReceipt(mstVo, infVo);
			sts = "succ";

		} catch (Exception e) {
			sts = "fail";
			logger.error(e.getMessage(), e);
		}

		//am.setScript("$.Nav('parent', './receiptList.do', {rcpISeq : " + infVo.getRcpInfSeq() + "}, null, true);");

		map.put("rs", rs);
		map.put("sts", sts);

		return new Gson().toJson(map);
	}
	
	/**
	 * 택배접수 수정 처리(택배조회>수정)
	 * @param vo
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping("/{partner}/receiptUpdateAjax.do")
	@ResponseBody
	public String GolfReceiptUpdate(@PathVariable String partner, HttpServletRequest request,@RequestBody(required = false) String body, GolfReceiptSearchVO search, GolfReceiptMstVO mstVo, GolfReceiptInfVO infVo, BindingResult result, Model model) {
		if (result.hasErrors())
			return super.setBindingResult(result, model);

		Map<String, Object> map = new HashMap<String, Object>();

		String sts = "";
		String rs = "";


		try {
			rs =_receiptService.updateReceipt(mstVo, infVo);
			sts = "succ";

		} catch (Exception e) {
			sts = "fail";
			logger.error(e.getMessage(), e);
		}

		//am.setScript("$.Nav('parent', './receiptList.do', {rcpISeq : " + infVo.getRcpInfSeq() + "}, null, true);");

		map.put("rs", rs);
		map.put("sts", sts);

		return new Gson().toJson(map);
	}

	/**
	 * 골프장 찾기 레이어 팝업(택배접수 페이지에서)
	 * @param vo
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptGolfLayer.do")
	public String GolfReceiptSearch(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		// 검색조건
		List<StringPair> type2 = new ArrayList<StringPair>();
		type2.add(new StringPair("골프장명", "0"));
		type2.add(new StringPair("지정영업소", "1"));
		type2.add(new StringPair("주소", "2"));

		model.addAttribute("type2", type2);
		model.addAttribute("list", _receiptService.selectReceiptGolfList(search));
		model.addAttribute("search", search);
		return "/receipt/receiptGolfLayer";
	}

	/**
	 * 골프장 상호 찾기 팝업(택배조회 상세-수정 레이어에서)
	 * @param search
	 * @param result
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptGolfPopup.do")
	public String receiptGolfPopup(GolfInfVO search, BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if(result.hasErrors())
			return super.setBindingResult(result, model);

		// 검색조건
		List<StringPair> type2 = new ArrayList<StringPair>();
		type2.add(new StringPair("골프장명", "0"));
		type2.add(new StringPair("지정영업소", "1"));
		type2.add(new StringPair("주소", "2"));

		model.addAttribute("type2", type2);
		model.addAttribute("list", _receiptService.selectReceiptGolfList(search));
		model.addAttribute("search", search);
		return "/common/golfPopup";
	}

	/**
	 * 택배조회 엑셀저장
	 * @param search
	 * @param result
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/receiptListExcel.do")
	public String GolfReceiptListExcel(GolfReceiptSearchVO search, BindingResult result, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		if (result.hasErrors())
			return super.setBindingResult(result, model);

		search.setCpage(1);
		search.setRecordCount(Integer.MAX_VALUE);
		
		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectList", search);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("list", receiptList);
		map.put("search", search);
		map.put("subject1", "택배조회 목록");

		ExcelDownloadUtil.downloadExcel(request, response, map, "", "golf_receipt_list.xlsx");

		return null;
	}

	/**
	 * 결제완료/취소 처리
	 * @param vo
	 * @param infVo
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/receiptPaymentArray.do")
	public @ResponseBody String GolfReceiptPayment(GolfReceiptInfVO infVo, HttpServletRequest request) throws Exception {
		String result="success";
		//등록
		try {
			
			// 골프접수 상태변경시 알림톡 전송 리스트 생성
			List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVo);
			
			List<SendRequestVO> userList = new ArrayList<SendRequestVO>();
			List<SendRequestVO> iconList = new ArrayList<SendRequestVO>();
			String payFunction = StringUtils.isEmpty(infVo.getGolfUpdate()) ? StatusPayVO.Complete.code() : StatusPayVO.Cancel.code();
			if(payFunction.equals(StatusPayVO.Cancel.code())){
				//userList = _sendTalkService.createPaymentArrayUserList(receiptList, "hdexp_0006"); // 결제 취소, 알림톡 유저
				//iconList = _sendTalkService.createPaymentArrayIconList(receiptList, "hdexp_0007"); // 결제 취소, 알림톡 관리자
			} else {
				
				/*if ( !super.getConfig().getWebMode().equals("op") ) {
					userList = _sendTalkService.createPaymentArrayUserList(receiptList, "hdexp_0004"); // 결제 완료, 알림톡 유저
					iconList = _sendTalkService.createPaymentArrayIconList(receiptList, "hdexp_0005"); // 결제 완료, 알림톡 관리자
					_sendTalkService.sendTalk(userList);
					_sendTalkService.sendTalk(iconList);
				}*/
				
				userList = _sendTalkService.createPaymentArrayList(receiptList, "hdexp_0004", "user"); // 결제 완료, 알림톡 유저
				iconList = _sendTalkService.createPaymentArrayList(receiptList, "hdexp_0005", "icon"); // 결제 완료, 알림톡 관리자
				_sendTalkService.sendTalk(userList);
				_sendTalkService.sendTalk(iconList);
				
			}
			
			// 결제 완료 / 취소 처리
			result = _receiptService.updateReceiptPaymentArray(infVo);
			
			// 알림톡 전송 - 결제 취소시 알림톡 전송 안함
			//_sendTalkService.sendTalk(userList);
			//_sendTalkService.sendTalk(iconList);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new Gson().toJson("fail");
		}
		return new Gson().toJson(result);
	}

	/**
	 * 결제완료/취소 처리 (파트너 제공)
	 * @param vo
	 * @param infVo
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{partner}/receiptPaymentArray.do")
	public @ResponseBody String GolfReceiptPayment(@PathVariable String partner, GolfReceiptInfVO infVo, HttpServletRequest request) throws Exception {
		String result="success";
		//등록
		try {
			
			// 골프접수 상태변경시 알림톡 전송 리스트 생성
			List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVo);
			
			List<SendRequestVO> userList = new ArrayList<SendRequestVO>();
			List<SendRequestVO> iconList = new ArrayList<SendRequestVO>();
			String payFunction = StringUtils.isEmpty(infVo.getGolfUpdate()) ? StatusPayVO.Complete.code() : StatusPayVO.Cancel.code();
			if(payFunction.equals(StatusPayVO.Cancel.code())){
				//userList = _sendTalkService.createPaymentArrayUserList(receiptList, "hdexp_0006"); // 결제 취소, 알림톡 유저
				//iconList = _sendTalkService.createPaymentArrayIconList(receiptList, "hdexp_0007"); // 결제 취소, 알림톡 관리자
			} else {
				
				/*if ( !super.getConfig().getWebMode().equals("op") ) {
					userList = _sendTalkService.createPaymentArrayUserList(receiptList, "hdexp_0004"); // 결제 완료, 알림톡 유저
					iconList = _sendTalkService.createPaymentArrayIconList(receiptList, "hdexp_0005"); // 결제 완료, 알림톡 관리자
					_sendTalkService.sendTalk(userList);
					_sendTalkService.sendTalk(iconList);
				}*/
				
				userList = _sendTalkService.createPaymentArrayList(receiptList, "hdexp_0004", "user"); // 결제 완료, 알림톡 유저
				iconList = _sendTalkService.createPaymentArrayList(receiptList, "hdexp_0005", "icon"); // 결제 완료, 알림톡 관리자
				
				logger.info("전송 내용 {}", userList);
				_sendTalkService.sendTalk(userList);
				_sendTalkService.sendTalk(iconList);
				
			}
			
			// 결제 완료 / 취소 처리
			result = _receiptService.updateReceiptPaymentArray(infVo);
			
			// 알림톡 전송 - 결제 취소시 알림톡 전송 안함
			//_sendTalkService.sendTalk(userList);
			//_sendTalkService.sendTalk(iconList);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new Gson().toJson("fail");
		}
		return new Gson().toJson(result);
	}

	/**
	 * 골프접수처리
	 * @param infVO
	 * @param request
	 * @return
	 * @return
	 * @throws Exception
	 */ 
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/receiptReceiptArray.do")
	public @ResponseBody String GolfReceiptReceipt(GolfReceiptInfVO infVO) throws Exception {
		List<GolfReceiptInfVO> receiptList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectDetailByKeyArr", infVO);
		String returnVal = "success";
		
		List<GolfReceiptInfVO> kakao = new ArrayList<GolfReceiptInfVO>();
		
		for(GolfReceiptInfVO vo: receiptList) {		
			vo.setRcpInfStat(StatusVO.Receipt.code());
			_receiptService.updateReceiptInfSel(vo, false);
			
			//스마트스코어인 경우 알림톡 발송 안함
			if(!"2".equals(vo.getRcpKind())) {
				kakao.add(vo);
			}
		}
		
		// 골프접수 상태변경시 알림톡 전송
		/*if ( !super.getConfig().getWebMode().equals("op") ) {
			_sendTalkService.sendPayRequest(receiptList);
		}*/
		
		//스마트스코어 접수건은 결제가 완료된 건으로 결제완료처리 알림톡 전송
		_sendTalkService.sendPayRequest(kakao);
		return  new Gson().toJson(returnVal); 
	}

	/**
	 * 택배사진 상세보기
	 * @param search
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/receiptPhotoLayer.do")
	public String GolfReceiptPhoto(GolfReceiptInfVO search, BindingResult result, Model model) {
		List<GolfReceiptInfVO> photoList = (List<GolfReceiptInfVO>) _gDao.selectList("golfReceiptInf.selectByPhoto", search);
		int i = 1;

		model.addAttribute("photo1", "/common/images/photo.jpg");
		model.addAttribute("photo2", "/common/images/photo.jpg");
		
		if(photoList.size()>0) {			
			for(GolfReceiptInfVO receiptInfVO : photoList){
				model.addAttribute("photo"+Integer.toString(i), super.getConfig().getHost()+"/upload/files/pickup/"+search.getRcpInfSeq()+"/"+receiptInfVO.getFileNm());
				i++;
			}
		}
		
		model.addAttribute("rcpInfSeq", search.getRcpInfSeq());
		return "/receipt/receiptPhotoLayer";
	}

	/**
	 * 짐캐리 송장 출력 팝업 뷰
	 */
	@RequestMapping(value = "/labelPrint.do")
	public String zimcarryLabelPrint(Model model) {
		return "/label/zimcarryLabelPrint";
	}

	/**
	 * 경동 API로부터 송장 HTML 데이터 가져오기 (AJAX용)
	 */
	@RequestMapping(value = "/getZimcarryLabelData.do", method = RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	public String getZimcarryLabelData(String prodCode) throws Exception {
		return kdngeApi.getZimcarryLabelData(prodCode);
	}
}
