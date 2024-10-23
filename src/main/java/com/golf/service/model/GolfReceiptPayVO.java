package com.golf.service.model;

import java.util.ArrayList;
import java.util.List;

import com.golf.model.GeneralModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

//골프 접수 결제 내역
public @Data class GolfReceiptPayVO implements GeneralModel {

	private static final long serialVersionUID = 5835188151523792726L;
	
	@ApiModelProperty(notes = "결제순번" , hidden=true)
	private int paySeq;
	
	@ApiModelProperty(notes = "발송순번" , hidden=true)
	private int rcpInfSeq = 0;
	
	@ApiModelProperty(notes = "발송순번 리스트", hidden=true)
    private List<Integer> rcpInfSeqArray = new ArrayList<Integer>();
    
	@ApiModelProperty(notes = "결제주문번호(order_id)")
	private String orderId;
	
	@ApiModelProperty(notes = "결제방식 (현금 : 0, 카드 : 1)")
	private String payMeans;
	
	@ApiModelProperty(notes = "전체금액")
	private int payAmt = 0;
	
	@ApiModelProperty(notes = "결제상태(1: 결제완료, 9: 결제취소)")
	private String payStat;
	
	@ApiModelProperty(notes = "결제고유ID(tid)")
	private String tid;
	
	@ApiModelProperty(notes = "결제요청고유(aid)")
	private String aid;
	
	@ApiModelProperty(notes = "카카오페이 상태코드 ")
	private String kakaoPayStat;
	
	@ApiModelProperty(notes = "카카오페이 상태메세지 ")
	private String kakaoPayMessage;
	
	@ApiModelProperty(notes = "시퀀스넘버")
	private String seqNo;
	
	@ApiModelProperty(notes = "사용자ID")
	private String userId;
	
	@ApiModelProperty(notes = "아이콘 결제가맹점코드(CID)")
	private String cid = "CADONE5665";
	
	@ApiModelProperty(notes = "상품명")
	private String pdName;
	
	@ApiModelProperty(notes = "상품수량")
	private String pdCnt;
	
	@ApiModelProperty(notes = "보스턴백수량")
	private String bostonCnt;
	
	@ApiModelProperty(notes = "비과세금액", example="0")
	private String taxFree = "0";
	
	@ApiModelProperty(notes = "VAT가격", example="0")
	private String vat  = "0";
	
	@ApiModelProperty(notes = "포인트가격", example="0")
	private String point = "0";

	@ApiModelProperty(notes = "할인금액", example="0")
	private String discount = "0";
	
	@ApiModelProperty(notes = "접수상태")
	private String rcpInfStat;	

	@ApiModelProperty(notes = "결제주문생성일자") 
    private String createdDate;
	
	@ApiModelProperty(notes = "결제주문승인일자")
    private String approvedDate;
	
	@ApiModelProperty(notes = "매입카드사 이름 ")
    private String purchaseCorp;
	
	@ApiModelProperty(notes = "매입카드사 코드 ")
    private String purchaseCorpCode;
	
	@ApiModelProperty(notes = "카드발금사 이름 ")
    private String issuerCorp;
	
	@ApiModelProperty(notes = "카드발급사 코드 ")
    private String issuerCorpCode;
	
	@ApiModelProperty(notes = "카카오 페이 매입사명 ")
    private String kpPurchaseCorp;
	
	@ApiModelProperty(notes = "카카오 페이 매입사코드 ")
    private String kpPurchaseCorpCode;
	
	@ApiModelProperty(notes = "카카오 페이 발급사명 ")
    private String kpIssuerCorp;
	
	@ApiModelProperty(notes = "카카오 페이 발급사 코드 ")
    private String kpIssuerCorpCode;
	
	@ApiModelProperty(notes = "카드 BIN ")
    private String bin;
	
	@ApiModelProperty(notes = "카드타입 ")
    private String cardType;
	
	@ApiModelProperty(notes = "할부개월수 ")
    private String installMonth;
	
	@ApiModelProperty(notes = "카드사 승인번호 ")
    private String approvedId;
	
	@ApiModelProperty(notes = "카드사 가맹점번호 ")
    private String cardMid;
	
	@ApiModelProperty(notes = "무이자 할부여부 ")
    private String intersetFreeInstall;
	
	@ApiModelProperty(notes = "카드상품코드 ")
    private String cardCode;
	
	@ApiModelProperty(notes = "실패코드 ")
    private String failCode;
	
	@ApiModelProperty(notes = "실패메세지 ")
    private String failMessage;
	
	@ApiModelProperty(notes = "실패상세결과 실패코드 ")
    private String failDetailCode;
	
	@ApiModelProperty(notes = "실패상세결과메세지 ")
    private String failDetailMessage;
	
	@ApiModelProperty(notes = "취소가능전체금액")
	private int payCanAmt = 0;
	
	@ApiModelProperty(notes = "취소비과세금액", example="0")
	private String canTaxFree = "0";
	
	@ApiModelProperty(notes = "취소VAT가격", example="0")
	private String canVat  = "0";
	
	@ApiModelProperty(notes = "결제취소일자") 
    private String cancelDate;

	@ApiModelProperty(notes = "결제구분(0:아이콘, 1:카카오)" , hidden=true) 
	private String payGubun;
	
	@ApiModelProperty(notes = "사용여부" , hidden=true) 
	private String useYn;

	@ApiModelProperty(notes = "접수구분(0 : 관리자, 1 : 챗봇, 2 : 어플)" , hidden=true) 
	private String rcpKind;
}
