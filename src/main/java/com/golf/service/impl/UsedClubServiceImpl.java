package com.golf.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.util.SystemOutLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.golf.service.model.GolfInfVO;
import com.golf.service.model.GolfReceiptInfVO;
import com.golf.service.model.GolfReceiptMstVO;
import com.golf.service.model.GolfReceiptPayVO;
import com.golf.service.model.StatusPayVO;
import com.golf.service.model.StatusVO;
import com.golf.service.model.UsedClubInfoVO;
import com.golf.talk.service.SendTalkServiceImpl;
import com.golf.util.EgovMapDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import egovframework.rte.psl.dataaccess.util.EgovMap;

import com.golf.service.model.GolfHistoryVO;
import com.golf.chat.vo.CbCommonVO;
import com.golf.chat.vo.CbGolfInfoVo;
import com.golf.chat.vo.CbReceiptInfoVO;
import com.golf.service.dao.GeneralDAOImpl;
import com.golf.service.impl.GolfApi;

/**
 * 중고채 접수 서비스
 * @author jhkim
 *
 */
@Service("UsedClubService")
public class UsedClubServiceImpl extends BaseServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(UsedClubServiceImpl.class);
	
	private int bostonAmt = 6000;		//보스턴백 편도 운임
	private int bostonAmtR = 6000;		//보스턴백 왕복 운임
	private int bostonDosunAmt = 6000;	//보스턴백 편도 도선
	private int bostonDosunAmtR = 6000;	//보스턴백 왕복 도선
	
	@Autowired
	private GeneralDAOImpl _gDao;
	
	@Resource
	GolfReceiptServiceImpl _receiptService;
	
	@Resource
	SendTalkServiceImpl _sendTalkService;
	
	public int golfCnt(CbCommonVO cbCommon) throws Exception {
		// TODO Auto-generated method stub
		return (int)_gDao.selectByKey("cbGolfInfo.selectByCnt", cbCommon);
	}
	
	@SuppressWarnings("unchecked")
	public List<CbGolfInfoVo> selectReceiptGolfList(CbCommonVO cbCommon) {
		// TODO Auto-generated method stub
		return (List<CbGolfInfoVo>)_gDao.selectList("cbGolfInfo.selectBySearch", cbCommon);
	}
	

	/**
	 * 
	 * @param list
	 * 챗봇 접수 시 운임체계(기본운임), 품목명(국제골프택배기본), 포장상태(편도/왕복 따라감), 배송구분(현불택배) 로 기본값 설정
	 * 골프접수번호는 insert시 생성
	 * @return
	 * @throws Exception 
	 */
	public List<GolfReceiptInfVO> receiptProcConfirm(List<CbReceiptInfoVO> list) throws Exception {
		// TODO Auto-generated method stub
		
		List<GolfReceiptInfVO> receiptList = new ArrayList<GolfReceiptInfVO>();

		String rcpInfStat = "4";			
		String payStat = "1";
		String company = "스마트스코어";
		String kakaoPayStat = "";
		String kakaoPayMessage = "";
		int bostonCnt = 0;
		
		if ( list != null ) {
			
			//접수 등록
			GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
			mstVO.setDelKind(list.get(0).getDelKind());
			mstVO.setFareType("1"); // 운임체계
			//mstVO.setGameDt(list.get(0));
			mstVO.setRegUser(list.get(0).getUserId()); // 등록유저
			mstVO.setUseYn("Y");
			
			_gDao.insert("golfReceipt.insertReceiptMst", mstVO);
			//int rcpSeq = _gDao3.insert("golfReceipt.insertReceiptMst", mstVO);
			
			//int rcpSeq = ((GolfReceiptMstVO)_gDao3.selectByKey("golfReceipt.selectMaxReceiptKey", mstVO)).getRcpSeq();
			int rcpSeq = mstVO.getRcpSeq();
			logger.info("insertReceiptMst 실행 이후"+mstVO.getRcpSeq());
			logger.info("insertReceiptMst 실행 이후 max select rcpSeq"+rcpSeq);
			for ( CbReceiptInfoVO cbReceiptInfoVO : list ) {
				
				logger.debug("접수 반복");
				GolfReceiptInfVO receiptInfVO = new GolfReceiptInfVO();
				
				receiptInfVO.setRcpSeq(rcpSeq);
						
				if("1".equals(cbReceiptInfoVO.getRcpKind())) { //결제상태 - 카카오 : 입금대기, 스마트스코어 : 결제완료
					company = "카카오";
					payStat = "0";
					kakaoPayStat = "READY";
					kakaoPayMessage = "결제요청";
				}
				if("0".equals(cbReceiptInfoVO.getRcpKind())) { //결제상태 - 카카오 : 입금대기, 스마트스코어 : 결제완료
					company = "아이콘";
					payStat = "0";
					kakaoPayStat = "READY";
					kakaoPayMessage = "결제요청";
				}
				
				//스마트 스코어 일때 접수상태 골프접수
				if("2".equals(cbReceiptInfoVO.getRcpKind()) || "0".equals(cbReceiptInfoVO.getRcpKind()) || "3".equals(cbReceiptInfoVO.getRcpKind())){
					receiptInfVO.setRcpInfStat("0"); 
				}else{
					receiptInfVO.setRcpInfStat(rcpInfStat);
				}
				
				logger.debug("바디 빌드");
				//receiptInfVO.setRcpInfStat(rcpInfStat); // 접수상태 - 카카오 , 스마트스코어: 접수대기		
				receiptInfVO.setSrType(cbReceiptInfoVO.getSrType());
				receiptInfVO.setPayType("0"); // 배송지불타입(현불)				
				receiptInfVO.setPdNm("스마트스코어중고채");
				receiptInfVO.setPdPack("1".equals(cbReceiptInfoVO.getDelKind()) ? "편도" : "왕복");
				
				receiptInfVO.setDelKind(cbReceiptInfoVO.getDelKind());
				receiptInfVO.setCusSendNm(cbReceiptInfoVO.getCusSendNm());
				receiptInfVO.setCusSendTelnum(cbReceiptInfoVO.getCusSendTelnum());
				receiptInfVO.setCusSendZip(cbReceiptInfoVO.getCusSendZip());
				receiptInfVO.setCusSendAddr1(cbReceiptInfoVO.getCusSendAddr1());
				receiptInfVO.setCusSendAddr2(cbReceiptInfoVO.getCusSendAddr2());
				
				
				// 고촌본사 고정
				receiptInfVO.setCusReNm("아이콘x스마트스코어 중고채");
				receiptInfVO.setCusReTelnum2("010-4763-0983");
				receiptInfVO.setCusReZip("10136");
				receiptInfVO.setCusReAddr1("경기도 김포시 고촌읍 아라육로 57번길 70");
				receiptInfVO.setCusReAddr2("4층 신설사업부");
				
				
				receiptInfVO.setPdMemo(cbReceiptInfoVO.getPdMemo());
				//receiptInfVO.setPicReqDt(null); // 배송예정일이라 함 - 접수시에는 입력하지 않음
				receiptInfVO.setPicReqDt(cbReceiptInfoVO.getPicReqDt()); // 픽업희망일
				
				//receiptInfVO.setRcpKind("1");
				receiptInfVO.setRcpKind(cbReceiptInfoVO.getRcpKind()); // 접수구분 - 현재 챗봇에서 넘겨줌
				receiptInfVO.setUserId(cbReceiptInfoVO.getUserId()); // 카카오챗봇ID
				
				receiptInfVO.setBoxSize(cbReceiptInfoVO.getBoxSize());
				receiptInfVO.setBoxCnt(cbReceiptInfoVO.getBoxCnt());
				receiptInfVO.setPrdType(2);
				
				
				logger.debug("바디 빌드 종료");
				
				preInsertReceiptInfo(receiptInfVO, cbReceiptInfoVO);
				logger.debug("프리 인서트 실패?");
				
				logger.debug("챗봇 골프접수 데이터");
				logger.debug("-->"+new Gson().toJson(receiptInfVO));
				_gDao.insert("golfReceipt.insertReceiptInfChat", receiptInfVO);
				receiptList.add(receiptInfVO);
				
				//결제금액
				GolfReceiptPayVO payVO = new GolfReceiptPayVO();
				payVO.setPayAmt(receiptInfVO.getPdPay());
				payVO.setUserId(cbReceiptInfoVO.getUserId());
				payVO.setKakaoPayStat(kakaoPayStat);
				payVO.setKakaoPayMessage(kakaoPayMessage);
				payVO.setPayStat(payStat); // 결제상태 - 입금대기
				
				// PAY TABLE ORDER ID 추가
				if ( "1".equals(list.get(0).getDelKind()) ) {
					// 편도
					payVO.setOrderId(receiptInfVO.getRcpNumber());
				} else {
					// 왕복
					payVO.setOrderId("GRP"+mstVO.getRcpSeq());
				}
				
				_gDao.insert("golfReceiptPay.insertReceiptPay", payVO);
								
				//히스토리 등록
				String historyMemo = company + " 중고채접수 편도";
				
				if( !"1".equals(list.get(0).getDelKind()) && receiptInfVO.getSrType()!=null ) {
					if(receiptInfVO.getSrType().equals("S")) {
						historyMemo = company + " 중고채접수 왕복1차";
					}else if(receiptInfVO.getSrType().equals("R")) {
						historyMemo = company + " 중고채접수 왕복2차";	
					}
				}
				insertGolfHistory(String.valueOf(receiptInfVO.getRcpNumber()), historyMemo);
			}

			if("0".equals(list.get(0).getRcpKind()) || "3".equals(list.get(0).getRcpKind())){
				
				// 아이콘 프로젝트 접수시 결제정보 전송하려면 사용
				_sendTalkService.sendPayRequest(receiptList, mstVO);
			}
		}
		
		return receiptList;
	}
	
	/**
	 * 카카오 골프 수정
	 * @param url
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<GolfReceiptInfVO> updateReceipt(List<CbReceiptInfoVO> list) throws Exception {
		// TODO Auto-generated method stub
		
		GolfReceiptInfVO infVo = new GolfReceiptInfVO();
		List<GolfReceiptInfVO> result = new ArrayList<GolfReceiptInfVO>();
		
		// 리스트로 들어온 각각의 데이터에 관해 수정작업
		for ( CbReceiptInfoVO cbReceiptInfoVO : list ) {
			infVo.setRcpNumber(cbReceiptInfoVO.getRcpNumber());
			infVo = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpNumber", infVo);
			// 골프접수 상태는 모든 정보 수정 가능(주소가 바뀌면 금액도 업데이트 됨)
			// 결제가 완료되어서 픽업요청 상태인 경우 일부정보만 수정 가능
			// 택배 접수가 되면 모든 정보 수정 불가
			
			if ( infVo == null ) {
				
				logger.info("접수번호를 찾을 수 없습니다.");
				
			} else if ( "7".equals(infVo.getRcpInfStat()) || "8".equals(infVo.getRcpInfStat()) || "9".equals(infVo.getRcpInfStat()) ){
				// 골프취소, 픽업취소, 택배취소
				logger.info("이미 삭제된 접수번호 입니다. 확인 부탁드립니다.");
				
			} else if ( "2".equals(infVo.getRcpInfStat()) || "3".equals(infVo.getRcpInfStat()) || "1".equals(infVo.getRcpInfStat()) ){
				// 택배접수, 배송완료, 픽업요청 - 수정불가
				logger.info("운송장번호가 발급되어 수정이 불가합니다. 영업소 프로그램으로 확인 부탁드립니다.");			
				
			} else if ( "0".equals(infVo.getRcpInfStat()) || "4".equals(infVo.getRcpInfStat()) ){
				
				// 결제가 완료된 골프접수 건은 일부정보만 수정 가능
				// CUS_SEND_NM, CUS_SEND_TELNUM, CUS_RE_NM, CUS_RE_TELNUM, PD_MEMO
				if ( "1".equals(infVo.getPayStat() ) && infVo.getDelKind().equals(list.get(0).getDelKind()) ) {
					// 골프접수 이고 결제 상태가 결제 
					if ( cbReceiptInfoVO.getCusReceiptNm() != null && !"".equals(cbReceiptInfoVO.getCusReceiptNm()) ) infVo.setCusReceiptNm(cbReceiptInfoVO.getCusReceiptNm());
					if ( cbReceiptInfoVO.getCusSendNm() != null && !"".equals(cbReceiptInfoVO.getCusSendNm()) ) infVo.setCusSendNm(cbReceiptInfoVO.getCusSendNm());
					if ( cbReceiptInfoVO.getCusSendTelnum() != null && !"".equals(cbReceiptInfoVO.getCusSendTelnum()) ) infVo.setCusSendTelnum(cbReceiptInfoVO.getCusSendTelnum());
					if ( cbReceiptInfoVO.getCusReNm() != null && !"".equals(cbReceiptInfoVO.getCusReNm()) ) infVo.setCusReNm(cbReceiptInfoVO.getCusReNm());
					if ( cbReceiptInfoVO.getCusReTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReTelNum()) ) infVo.setCusReTelnum2(cbReceiptInfoVO.getCusReTelNum());
					if ( cbReceiptInfoVO.getPdMemo() != null && !"".equals(cbReceiptInfoVO.getPdMemo()) ) infVo.setPdMemo(cbReceiptInfoVO.getPdMemo());
					_gDao.update("golfReceiptInf.updateSelective", infVo);
					String company = "카카오";
					
					if(cbReceiptInfoVO.getRcpKind().equals("2")) {
						company = "스마트 스코어";
					}else if(cbReceiptInfoVO.getRcpKind().equals("0")){
						company = "아이콘";
					}
					//히스토리 등록
					String historyMemo = company + " 골프 수정";
					insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
					result.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
				} else if ( "0".equals(infVo.getPayStat()) ) {
					String historyMemo = "스마트 스코어 골프 수정";
					
					if(infVo.getRcpKind().equals("1")) { //카카오
						historyMemo = "카카오 골프 수정";
					}
					
					logger.info("receive 수정정보 ", new Gson().toJson(cbReceiptInfoVO));
					logger.info("결제 대기 카카오 골프 수정 ");
					if ( cbReceiptInfoVO.getSendGolfSeq() != 0 ) infVo.setSendGolfSeq(cbReceiptInfoVO.getSendGolfSeq());
					if ( cbReceiptInfoVO.getCusSendNm() != null && !"".equals(cbReceiptInfoVO.getCusSendNm()) ) infVo.setCusSendNm(cbReceiptInfoVO.getCusSendNm());
					if ( cbReceiptInfoVO.getCusSendTelnum() != null && !"".equals(cbReceiptInfoVO.getCusSendTelnum()) ) infVo.setCusSendTelnum(cbReceiptInfoVO.getCusSendTelnum());
					if ( cbReceiptInfoVO.getCusSendZip() != null && !"".equals(cbReceiptInfoVO.getCusSendZip()) ) infVo.setCusSendZip(cbReceiptInfoVO.getCusSendZip());
					if ( cbReceiptInfoVO.getCusSendAddr1() != null && !"".equals(cbReceiptInfoVO.getCusSendAddr1()) ) infVo.setCusSendAddr1(cbReceiptInfoVO.getCusSendAddr1());
					if ( cbReceiptInfoVO.getCusSendAddr2() != null && !"".equals(cbReceiptInfoVO.getCusSendAddr2()) ) infVo.setCusSendAddr2(cbReceiptInfoVO.getCusSendAddr2());
					
					if ( cbReceiptInfoVO.getCusReNm() != null && !"".equals(cbReceiptInfoVO.getCusReNm()) ) infVo.setCusReNm(cbReceiptInfoVO.getCusReNm());
					if ( cbReceiptInfoVO.getCusReTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReTelNum()) ) infVo.setCusReTelnum2(cbReceiptInfoVO.getCusReTelNum());
					if ( cbReceiptInfoVO.getCusReZip() != null && !"".equals(cbReceiptInfoVO.getCusReZip()) ) infVo.setCusReZip(cbReceiptInfoVO.getCusReZip());
					if ( cbReceiptInfoVO.getCusReAddr1() != null && !"".equals(cbReceiptInfoVO.getCusReAddr1()) ) infVo.setCusReAddr1(cbReceiptInfoVO.getCusReAddr1());
					if ( cbReceiptInfoVO.getCusReAddr2() != null && !"".equals(cbReceiptInfoVO.getCusReAddr2()) ) infVo.setCusReAddr2(cbReceiptInfoVO.getCusReAddr2());

					if ( cbReceiptInfoVO.getPdCnt() != 0 ) infVo.setPdCnt(cbReceiptInfoVO.getPdCnt()); // 골프백 수량	
					//if ( cbReceiptInfoVO.getBostonCnt() != 0 ) infVo.setBostonCnt(cbReceiptInfoVO.getBostonCnt()); // 보스턴백 수량	
					if ( cbReceiptInfoVO.getPdIndivPrice() != null && !"".equals(cbReceiptInfoVO.getPdIndivPrice()) ) infVo.setPdIndivPrice(cbReceiptInfoVO.getPdIndivPrice()); // 개별단가
					if ( cbReceiptInfoVO.getPdGolfCnt() != null && !"".equals(cbReceiptInfoVO.getPdGolfCnt()) ) infVo.setPdGolfCnt(cbReceiptInfoVO.getPdGolfCnt()); // 골프채 수량
					
					if ( cbReceiptInfoVO.getPdMemo() != null && !"".equals(cbReceiptInfoVO.getPdMemo()) ) infVo.setPdMemo(cbReceiptInfoVO.getPdMemo());
					//receiptInfVO.setPicReqDt(null); // 배송예정일이라 함 - 접수시에는 입력하지 않음
					if ( cbReceiptInfoVO.getPicReqDt() != null && !"".equals(cbReceiptInfoVO.getPicReqDt()) ) infVo.setPicReqDt(cbReceiptInfoVO.getPicReqDt()); // 픽업희망일
					
					// 접수자이름
					if ( cbReceiptInfoVO.getCusReceiptNm() != null && !"".equals(cbReceiptInfoVO.getCusReceiptNm()) ) infVo.setCusReceiptNm(cbReceiptInfoVO.getCusReceiptNm());
					// 접수자전화번호
					if ( cbReceiptInfoVO.getCusReceiptTelNum() != null && !"".equals(cbReceiptInfoVO.getCusReceiptTelNum()) ) infVo.setCusReceiptTelNum(cbReceiptInfoVO.getCusReceiptTelNum());
					preInsertReceiptInfo(infVo, cbReceiptInfoVO);
					_gDao.update("golfReceiptInf.updateSelective", infVo);
					
					// 기존 편도 데이텨 payAmt 변경
					GolfReceiptPayVO payVO = new GolfReceiptPayVO();
					payVO.setPayAmt(infVo.getPdPay());
					payVO.setRcpInfSeq(infVo.getRcpInfSeq());
					_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", payVO);
					
					//히스토리 등록
					insertGolfHistory(String.valueOf(infVo.getRcpNumber()), historyMemo);
					
					if ( "2".equals(infVo.getDelKind()) && "1".equals(list.get(0).getDelKind()) ) {
						// 추가 챗봇에서는 왕복->편도로 수정이 가능함
						
						// 해당 RCP_NUMBER에 해당하는 ROW의 RCP_SEQ와 일치하는 GOLF_RECEIPT_MST의 DEL_KIND = 1
						GolfReceiptMstVO mstVO = new GolfReceiptMstVO();
						mstVO.setDelKind("1");
						mstVO.setRcpSeq(infVo.getRcpSeq());
						_gDao.update("golfReceiptMst.updateSelective", mstVO);
						
						// 변경해야 할 타겟이 아닌 ROW 골프 취소
						if ( "S".equals(infVo.getSrType()) ) {
							infVo.setSrType("R");
						} else {
							infVo.setSrType("S");
						}
						GolfReceiptInfVO cancelInfVo = (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo);
						cancelInfVo.setRcpInfStat(StatusVO.ReceiptCancel.code());
						_receiptService.updateReceiptInfSel(cancelInfVo, false);
						
						// 리턴을 위해 원래대로 변경
						if ( "S".equals(infVo.getSrType()) ) {
							infVo.setSrType("R");
						} else {
							infVo.setSrType("S");
						}
						
						// 취소한 건에 대한 pay table 변경 - USE_YN='N'
						GolfReceiptPayVO cancelPayVO = new GolfReceiptPayVO();
						cancelPayVO.setRcpInfSeq(cancelInfVo.getRcpInfSeq());
						cancelPayVO.setUseYn("N");
						_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", cancelPayVO);
						
						// 기존 편도 데이텨 order id 변경 - ORDER_ID= GRP로 시작하는 그룹코드가 아닌 rcpNumber
						GolfReceiptPayVO modifyPayVO = new GolfReceiptPayVO();
						modifyPayVO.setOrderId(infVo.getRcpNumber());
						modifyPayVO.setRcpInfSeq(infVo.getRcpInfSeq());
						_gDao.update("golfReceiptPay.updateSelectiveByRcpInfSeq", modifyPayVO);
						
						// 모두 처리 이후 편도<->왕복 변경시 CHANGE_DELTYPE_YN = 'Y'
						infVo.setChangeDeltypeYn("Y");
						_gDao.update("golfReceiptInf.updateSelective", infVo);
						
						return (List<GolfReceiptInfVO>)_gDao.selectList("golfReceiptInf.selectDetailByRcpSeq", infVo);
					} else if ( "1".equals(infVo.getDelKind()) && "2".equals(list.get(0).getDelKind()) ) {
						// 추가 챗봇에서는 편도->왕복으로 수정이 가능함
						//return changeRoundReceipt(list, infVo);
					}
					result.add( (GolfReceiptInfVO)_gDao.selectByKey("golfReceiptInf.selectDetailByRcpSeq", infVo) );
				 } else {
					logger.info("결제 완료 건중 수정할 수 없는 데이터가 포함되어 있거나 결제 취소건이라 수정이 불가능합니다. ");
					logger.info("또는 골프 접수 상태에서 편도/왕복은 수정이 불가능합니다. ");
				}
				
			} else {
				logger.info("기타 사유로 수정 처리 불가");
			}
		}
		
		return result;
	}
	
	

	/**
	 * GOLF_RECEIPT_MST, GOLF_RECEIPT_INF INSERT 전 처리
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도서산간구분 set 
	 * 보내는 분, 받는 분 주소지가 골프장이 아닐 경우 받는 분 주소에 의거한 도착영업소명, 도착영업소코드 set, 골프장일 경우 골프장 테이블에 있는 도착영업소명, 도착영업소코드 set
	 * 각종 운임 set(총금액까지)
	 * @param cbReceiptInfoVo 
	 * @param receiptInfVO 
	 */
	public void preInsertReceiptInfo(GolfReceiptInfVO receiptInfVO, CbReceiptInfoVO cbReceiptInfoVO)  throws Exception{ 
		
		GolfInfVO search = new GolfInfVO();		
		GolfInfVO sendGolfInfVo = null;
		GolfInfVO recvGolfInfVo = null;
		
		GolfInfVO infVO = new GolfInfVO();
		infVO.setType1("A");
		infVO.setKeyword(cbReceiptInfoVO.getCusSendZip());
		Map<String, String> branchMap = getBranchInfo(infVO);
		logger.info("보내는분 도착영업소 : "+branchMap.get("brCode")+","+branchMap.get("brNm"));
		receiptInfVO.setBrStrCode(branchMap.get("brCode"));
		receiptInfVO.setBrStrNm(branchMap.get("brNm"));
		
		infVO.setKeyword(cbReceiptInfoVO.getCusReZip()); 
		receiptInfVO.setBrEndCode("311103");
		receiptInfVO.setBrEndNm("김포고촌전호745");
		 
		// 도서산간구분, 운임계산
		searchIsl(receiptInfVO, cbReceiptInfoVO, sendGolfInfVo, recvGolfInfVo);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getBranchInfo(GolfInfVO infVO) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();
		Map<String, String> paramMap = new HashMap<String, String>();
		
		paramMap.put("SEARCH_TYPE", infVO.getType1());
		paramMap.put("SEARCH_WORD", infVO.getSearchWord());
		EgovMap apiMap = callUrl(getApiUrl("GetBranchInfo"), gson.toJson(paramMap));
		
		if(apiMap.get("listData") != null && ((List<EgovMap>)apiMap.get("listData")).size()>0) {
			List<EgovMap> apiMapList = (List<EgovMap>)apiMap.get("listData");
			result.put("brCode", apiMapList.get(0).get("brCode").toString());
			result.put("brNm", apiMapList.get(0).get("brNm").toString());
		}
		return result;
	}
	
	public void searchIsl(GolfReceiptInfVO receiptInfVO, CbReceiptInfoVO cbReceiptInfoVO, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {
		
		int amtArray[] = {12500, 13500, 15000};
		int dosun1 = 0; int dosun2 = 0;
		
		receiptInfVO.setSendIslYn("N");
		receiptInfVO.setRecvIslYn("N");
		
		int pdDosunPay = 0;
		int pdDeliPay = 0;
		int iconPay = 0;
		int extraPrice = 0;
		int pdPay = 0;
			
		String str = cbReceiptInfoVO.getBoxSize();

		long lCnt = str.chars().filter(a->a == 'L').count();
		long mCnt = str.chars().filter(a->a == 'M').count();
		long sCnt = str.chars().filter(a->a == 'S').count();
		
		pdDeliPay += (amtArray[0]*sCnt);
		pdDeliPay += (amtArray[1]*mCnt);
		pdDeliPay += (amtArray[2]*lCnt);
				
		
		// 아이콘 수수료
		iconPay = calculateIconFee(cbReceiptInfoVO);
			
		pdPay = pdDeliPay + iconPay;

		receiptInfVO.setPdDosunPay(pdDosunPay);
		receiptInfVO.setPdDeliPay(pdDeliPay);
		receiptInfVO.setIconFee(iconPay);
		receiptInfVO.setPdEtcPay(extraPrice);
		receiptInfVO.setPdPay(pdPay);		
		
	}
	
	
	public int calculateIconFee(CbReceiptInfoVO cbReceiptInfoVO) {
		
	  int sumIconFee = 0;
	  int sFee = 4000;
	  int mFee = 4000;
	  int lFee = 4500;
	  
	  String str = cbReceiptInfoVO.getBoxSize();
	
	  long sCnt = str.chars().filter(a->a == 'S').count();
	  long mCnt = str.chars().filter(a->a == 'M').count();
	  long lCnt = str.chars().filter(a->a == 'L').count();
	  
	  sumIconFee += (lCnt*lFee);
	  sumIconFee += (sCnt*sFee);
	  sumIconFee += (mCnt*mFee);
	  
	  return sumIconFee;
		
	}
	
	// 아이콘 수수료 수수료*수량으로 수정 20210517
	public int calculateIconFee(CbReceiptInfoVO cbReceiptInfoVo, GolfInfVO sendGolfInfVo, GolfInfVO recvGolfInfVo) {
		int sumIconFee = 0;
		int sendGolfFee = 0; 
		int recevGolfFee = 0; 
		int pdCnt = cbReceiptInfoVo.getPdCnt();
		int bostonCnt = 0;
		int sendApiGolfFee = 0;
		int sendApiBostonFee = 0;
		int recvApiGolfFee = 0;
		int recvApiBostonFee = 0;
		
		if("2".equals(cbReceiptInfoVo.getRcpKind()) || "0".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind())) { //스마트스코어
			bostonCnt = cbReceiptInfoVo.getBostonCnt();
		}
		
		if ( cbReceiptInfoVo.getSendGolfSeq() > 0) {
			//스마트스코어 접수
			if("2".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind())) {
				sendApiGolfFee = apiFee(cbReceiptInfoVo.getDelKind(), "G");
				sendApiBostonFee = apiFee(cbReceiptInfoVo.getDelKind(), "B");
			}else {
				if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
					sendApiGolfFee = sendGolfInfVo.getFee1();
					sendApiBostonFee = sendGolfInfVo.getFee7();
				}else {
					sendApiGolfFee = sendGolfInfVo.getFee4();
					sendApiBostonFee = sendGolfInfVo.getFee10();
				}
			}
			
			sendGolfFee = (sendApiGolfFee * pdCnt) + (sendApiBostonFee * bostonCnt);
			System.out.println("샌드" +sendGolfFee);
		}

		if ( cbReceiptInfoVo.getRecvGolfSeq() > 0) {
			//스마트스코어 접수
			if("2".equals(cbReceiptInfoVo.getRcpKind()) || "3".equals(cbReceiptInfoVo.getRcpKind())) {
				recvApiGolfFee = apiFee(cbReceiptInfoVo.getDelKind(), "G");
				System.out.println("리시브에이피아이" + recvApiGolfFee);
				recvApiBostonFee = apiFee(cbReceiptInfoVo.getDelKind(), "B");
				System.out.println("리시브에이피아이보스톤" + recvApiBostonFee);
			}else {
				if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
					recvApiGolfFee = recvGolfInfVo.getFee1();
					recvApiBostonFee = recvGolfInfVo.getFee7();
				}else {
					recvApiGolfFee = recvGolfInfVo.getFee4();
					recvApiBostonFee = recvGolfInfVo.getFee10();					
				}
				
			}
			recevGolfFee = (recvApiGolfFee * pdCnt) + (recvApiBostonFee * bostonCnt);
			System.out.println("리시브" +recevGolfFee);
		}
		
		if ( "1".equals(cbReceiptInfoVo.getDelKind()) ) {
			if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				sumIconFee = (8000 * pdCnt) + (bostonAmt * bostonCnt) ;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() > 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				// 출발지가 골프장일 경우
				sumIconFee = sendGolfFee;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() > 0 ) {
				sumIconFee = recevGolfFee;

			} else {
				sumIconFee = sendGolfFee < recevGolfFee ? sendGolfFee : recevGolfFee;
			}
		} else {			
			if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				sumIconFee = (4500 * pdCnt) + (3000 * bostonCnt) ;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() > 0 && cbReceiptInfoVo.getRecvGolfSeq() == 0 ) {
				// 출발지가 골프장일 경우
				sumIconFee = sendGolfFee;

			} else if ( cbReceiptInfoVo.getSendGolfSeq() == 0 && cbReceiptInfoVo.getRecvGolfSeq() > 0 ) {
				sumIconFee = recevGolfFee;

			} else {
				sumIconFee = sendGolfFee < recevGolfFee ? sendGolfFee : recevGolfFee;
			}
		}
		return sumIconFee;
	}
	
	public int apiFee(String delKind, String gubun) {		
		int apiFee = 8000;
		
		//보스턴백
		if("B".equals(gubun)) {
			apiFee = 6000;
			if("2".equals(delKind)) {
				apiFee = 3000;
			}
		}else {			
			if("2".equals(delKind)) {
				apiFee = 4500;
			}
		}
		return apiFee;
	}
	public void insertGolfHistory(String rcpNumber, String historyMemo){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		HttpSession session = request.getSession(true);
		
		GolfHistoryVO golfHistoryVO = new GolfHistoryVO();
		golfHistoryVO.setRcpNumber(rcpNumber);
		golfHistoryVO.setHistoryMemo(historyMemo);

		if(!super.getConfig().getWebMode().equals("local")) { 
			if(session.getAttribute("historyEmpCode")!=null) {
				golfHistoryVO.setRegUser(session.getAttribute("historyEmpCode").toString());
			}else {
				golfHistoryVO.setRegUser("영업소 API");
			}
		}else {
			golfHistoryVO.setRegUser("local");
		}
		
		_gDao.insert("golfHistory.insertHistory", golfHistoryVO);
	}	
	
	public EgovMap gsonCallHttp(String urlAddr, String param) throws Exception{

		HttpURLConnection con;
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("charset", "utf-8");
        con.setDoInput(true);
        con.setDoOutput(true);

        try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
		    os.write(param);
		    os.flush();
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}
		con.disconnect();

		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(sb.toString(), EgovMap.class);

		return rtnMap;
	}
	
	public EgovMap gsonCallHttps(String urlAddr, String param) throws Exception{

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		HttpURLConnection con;
		URL url = new URL(urlAddr);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("charset", "utf-8");
		con.setDoInput(true);
		con.setDoOutput(true);

		try(OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream());){
			os.write(param);
			os.flush();
		}

		StringBuffer sb = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				sb.append(responseLine.trim());
			}
		}
		con.disconnect();

		Gson gson = new GsonBuilder().registerTypeAdapter(EgovMap.class, new EgovMapDeserializer()).setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").serializeNulls().create();
		EgovMap rtnMap = gson.fromJson(sb.toString(), EgovMap.class);

		return rtnMap;
	}
	
	/**
	 * api주소 분기 처리
	 * @param urlAddr, param
	 * @throws Exception
	 */

	public EgovMap callUrl(String urlAddr, String param) throws Exception{
		if(super.getConfig().getWebMode().equals("local")){
			return gsonCallHttp(urlAddr, param);
		}else{
			return gsonCallHttps(urlAddr, param);
		}

	}
	
	/**
	 * api주소 분기 처리
	 * @param url
	 * @throws Exception
	 */

	public String getApiUrl(String url){
		StringBuffer sb = new StringBuffer();
		sb.append(super.getConfig().getWebMode().equals("op") ? "https://kdapi.kditlab.com:447/RndGolf/" : "https://kdapi.kditlab.com:444/RndGolf/").append(url);
		return sb.toString();
	}
	
	
}
