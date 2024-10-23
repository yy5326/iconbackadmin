package com.golf.model;

import java.io.Serializable;

/**
 * @Class Name : FileVO.java
 * @Description : 파일정보 처리를 위한 VO 클래스
 * @Modification Information
 *
 *               수정일 수정자 수정내용 ------------ ------- -------------------
 * 
 *
 * @author
 * @since 2013. 3. 27.
 * @version
 * @see
 *
 */

@Deprecated
@SuppressWarnings("serial")
public class AttachFileVO implements Serializable {
	/** 파일순번 **/
	private String file_seq = "";
	/** 파일구분 1.썸네일,2.이미지,3.첨부파일 **/
	private String file_code = "";
	/** 파일경로 **/
	private String file_path = "";
	/** 저장파일명 **/
	private String file_name = "";
	/** 원본파일명 **/
	private String file_oname = "";
	/** 파일설명 **/
	private String file_desc = "";
	/** 파일구분키 **/
	private String file_key = "";
	/** 테이블명 **/
	private String table_nm = "";
	/** 게시글번호 **/
	private String table_seq = "";
	/** 다운수 **/
	private String file_down = "";
	/** mode **/
	private String mode = "";
	private String input_type = "";
	private String atch_file_id = "";

	/* del file List */
	private String del_file_seq_list;

	/* 추가 걸럼 */
	private String etc01;
	private String etc02;
	private String etc03;

	/**
	 * @return the etc01
	 */
	public String getEtc01() {
		return etc01;
	}

	/**
	 * @param etc01
	 *            the etc01 to set
	 */
	public void setEtc01(String etc01) {
		this.etc01 = etc01;
	}

	/**
	 * @return the etc02
	 */
	public String getEtc02() {
		return etc02;
	}

	/**
	 * @param etc02
	 *            the etc02 to set
	 */
	public void setEtc02(String etc02) {
		this.etc02 = etc02;
	}

	/**
	 * @return the etc03
	 */
	public String getEtc03() {
		return etc03;
	}

	/**
	 * @param etc03
	 *            the etc03 to set
	 */
	public void setEtc03(String etc03) {
		this.etc03 = etc03;
	}

	/**
	 * @return the del_file_seq_list
	 */
	public String getDel_file_seq_list() {
		return del_file_seq_list;
	}

	/**
	 * @param del_file_seq_list
	 *            the del_file_seq_list to set
	 */
	public void setDel_file_seq_list(String del_file_seq_list) {
		this.del_file_seq_list = del_file_seq_list;
	}

	public String getAtch_file_id() {
		return atch_file_id;
	}

	public void setAtch_file_id(String atch_file_id) {
		this.atch_file_id = atch_file_id;
	}

	public String getFile_seq() {
		return file_seq;
	}

	public void setFile_seq(String file_seq) {
		this.file_seq = file_seq;
	}

	public String getFile_code() {
		return file_code;
	}

	public void setFile_code(String file_code) {
		this.file_code = file_code;
	}

	public String getFile_path() {
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_oname() {
		return file_oname;
	}

	public void setFile_oname(String file_oname) {
		this.file_oname = file_oname;
	}

	public String getFile_desc() {
		return file_desc;
	}

	public void setFile_desc(String file_desc) {
		this.file_desc = file_desc;
	}

	public String getFile_key() {
		return file_key;
	}

	public void setFile_key(String file_key) {
		this.file_key = file_key;
	}

	public String getTable_nm() {
		return table_nm;
	}

	public void setTable_nm(String table_nm) {
		this.table_nm = table_nm;
	}

	public String getTable_seq() {
		return table_seq;
	}

	public void setTable_seq(String table_seq) {
		this.table_seq = table_seq;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

	public String getFile_down() {
		return file_down;
	}

	public void setFile_down(String file_down) {
		this.file_down = file_down;
	}
}
