package com.golf.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.golf.model.common.StringPair;
import com.nhncorp.lucy.security.xss.XssFilter;

/**
 * 공통 문자열 처리 유틸리티 <br>
 * @author wimy
 *
 */
public class StringUtil {

	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);

	private static long dummy = 0l;

	/**
	 * 문자열 빈 값 여부 - null 포함
	 * @param str
	 * @return true: 입력받은 String 이 빈 문자열 또는 null인 경우
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * 숫자 여부 판단 - 실수 가능
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str.replaceAll("\\,", ""));
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * 모든 태그 제거
	 *
	 * @param content
	 * @return
	 * @throws SQLException
	 */
	public static String removeTag(String content) {
		Pattern SCRIPTS = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

		Pattern STYLE = Pattern.compile("<style[^>]*>.*</style>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");

		Pattern TAGS = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");
		// Pattern nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>");
		Pattern ENTITY_REFS = Pattern.compile("&[^;]+;");
		Pattern WHITESPACE = Pattern.compile("\\s\\s+");
		Pattern ETC = Pattern.compile("nbsp;");

		Matcher m;

		m = SCRIPTS.matcher(content);
		content = m.replaceAll("");

		m = STYLE.matcher(content);
		content = m.replaceAll("");

		m = TAGS.matcher(content);
		content = m.replaceAll("");
		m = ENTITY_REFS.matcher(content);
		content = m.replaceAll("");
		m = WHITESPACE.matcher(content);
		content = m.replaceAll(" ");

		m = ETC.matcher(content);
		content = m.replaceAll(" ");

		return content;
	}

	/**
	 * 랜덤 문자열 생성
	 *
	 * @param type : GenType 참조
	 * @param length : 길이
	 * @return
	 */
	public static String getRandomValue(GenType type, int length) {

		StringBuffer strPwd = new StringBuffer();
		char str[] = new char[1];
		// 특수기호 포함
		if (type.equals(GenType.SpecialMarks)) {
			for (int i = 0; i < length; i++) {
				str[0] = (char) ((Math.random() * 94) + 33);
				strPwd.append(str);
			}
			// 대문자로만
		}
		else if (type.equals(GenType.UpperCase)) {
			for (int i = 0; i < length; i++) {
				str[0] = (char) ((Math.random() * 26) + 65);
				strPwd.append(str);
			}
			// 소문자로만
		}
		else if (type.equals(GenType.LowerCase)) {
			for (int i = 0; i < length; i++) {
				str[0] = (char) ((Math.random() * 26) + 97);
				strPwd.append(str);
			}
			// 숫자형으로
		}
		else if (type.equals(GenType.Number)) {
			int strs[] = new int[1];
			for (int i = 0; i < length; i++) {
				strs[0] = (int) (Math.random() * 9);
				strPwd.append(strs[0]);
			}
			// 소문자, 숫자형
		}
		else if (type.equals(GenType.LowerCaseAndNumber)) {
			Random rnd = new Random();
			for (int i = 0; i < length; i++) {
				if (rnd.nextBoolean()) {
					strPwd.append((char) ((rnd.nextInt(26)) + 97));
				}
				else {
					strPwd.append((rnd.nextInt(10)));
				}
			}
		}

		return strPwd.toString();
	}


	/**
	 * 소수 생성
	 * @param length
	 * @return
	 */
	public static String getRandomPrimeNumber(int length){
		String sRtn = "";
		if (length == 4){
			//(오늘일자(dd) + 계좌마지막번호(농협은 모두 1)) % 10
			int addNum = (Integer.parseInt(new StringUtil().getDateToString("dd")) + 1) % 10;
			//랜덤숫자
			int i = Integer.parseInt(new StringUtil().getRandomValue(GenType.Number, 1));
			String[] tempKey = null;

			//addNum의 마지막숫자에 따라 변경
			if (addNum == 1){
				tempKey = new String[] {"1009", "1013", "1019", "1033", "1039", "1049", "1063", "1069", "1087", "1093"};
			}else if (addNum == 3){
				tempKey = new String[] {"1009", "1019", "1021", "1031", "1039", "1049", "1051", "1061", "1069", "1081"};
			}else if (addNum == 7){
				tempKey = new String[] {"1009", "1013", "1019", "1021", "1031", "1123", "1033", "1039", "1049", "1051"};
			}else if (addNum == 9){
				tempKey = new String[] {"1151", "1021", "1031", "1021", "1051", "1061", "1087", "1091", "1097", "1117"};
			}else{
				tempKey = new String[] {"1009", "1013", "1019", "1021", "1087", "1123", "1129", "1171", "1193", "1217"};
			}

			sRtn = tempKey[i];
		}

		return sRtn;
	}

	/**
	 * 날짜 형식 문자열 비교
	 *
	 * @param Object
	 * @return boolean
	 */
	public static boolean diffOfDate(String begin, String end) throws Exception {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date thisdate = formatter.parse(formatter.format(new Date()));

			Date beginDate = formatter.parse(begin);
			Date endDate = formatter.parse(end);

			if ((thisdate.equals(beginDate) || thisdate.after(beginDate)) && (thisdate.equals(endDate) || thisdate.before(endDate))) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Get String(if object is null, return empty string).
	 *
	 * @param Object
	 * @return String
	 */
	public static String getString(Object obj) {
		if (obj == null)
			return "";
		else
			return String.valueOf(obj);
	}

	/**
	 * Get int
	 * @param obj
	 * @return
	 */
	public static int getInt(Object obj) {
		String sRtn = getString(obj).trim();
		if (sRtn.equals(""))
			return 0;
		else
			return Integer.parseInt(sRtn);
	}

	/**
	 * escaped html 태그 변경
	 *
	 * @param i
	 * @return
	 */
	public static String replaceEditorTag(String content) {
		String str = content;
		if (str != null && !str.equals("")) {
			str = str.replaceAll("&amp;", "&");
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&gt;", ">");

			str = str.replaceAll("&quot;", "\"");
			str = str.replaceAll("&apos;", "'");
		}
		else {
			str = "";
		}
		return str;
	}


	/**
	 * 오늘로부터 원하는 기간을 설정하여 Date로 반환
	 * <br><i>문자열로 가져오고 싶은 경우 getDateToString(Date date, String format) 함수에 적용하여 사용하면 됨.</i>
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Date getDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);

		return cal.getTime();
	}

	/**
	 * yyyy-MM-dd 형식의 문자열을 Date 객체로 가져옵니다.
	 *
	 * @param input
	 * @return
	 */
	public static Date getDate(String input) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(input);
	}

	/**
	 * 특정 날짜 기준에서 특정 값을 더하거나 뺀 java.util.Date 형식의 날짜를 가져옵니다.
	 * @param date 특정 날짜입니다. null일 경우 오늘입니다.
	 * @param calendarField Calendar 상수값
	 * @param amount 더하거나 뺄 값
	 * @return
	 */
	public static Date getDate(Date date, int calendarField, int amount) {
		Calendar cal = Calendar.getInstance();
		if (date == null)
			date = new Date();

		cal.setTime(date);

		cal.add(calendarField, amount);

		return cal.getTime();
	}

	/**
	 * 오늘 날짜를 형식에 맞는 문자열로 가져옵니다.
	 *
	 * @param format
	 * @return
	 */
	public static String getDateToString(String format) {
		Date date = new Date();
		return getDateToString(date, format);
	}

	/**
	 * 날짜 객체를 형식에 맞는 문자열로 가져옵니다.
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateToString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}


	/**
	 * 특정 날짜 기준에서 그 주의 해당하는 요일의 Date를 yyyy-mm-dd 의 형식으로 가져옵니다.
	 * 일요일 : 1, 월요일 : 2, 화요일 : 3, 수요일 : 4, 목요일 : 5, 금요일 : 6, 토요일 : 7
	 * @param date 특정 날짜 null일 경우 당일
	 * @param iWeek 요일의 해당하는 정수
	 * @return
	 */
	public static String getStringDateToWeekDate (Date date, int iWeek) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

 		Calendar cal = Calendar.getInstance();
		if (date == null)
			date = new Date();

		cal.setTime(date);

 		cal.set(Calendar.DAY_OF_WEEK, iWeek);

		return formatter.format(cal.getTime());
	}

	/**
	 * date1에 대한 date2의 일수 차이를 가져옵니다. - date2가 date1보다 미래일 경우 음수가 나옵니다.
	 *
	 * @param date1
	 * @param date2
	 * @return
	 * @throws Exception
	 */
	public static long compareTwoDate(Date date1, Date date2) throws Exception {
		// logger.debug(">>>>> date1 : {}, date2 : {}", date1, date2);
		long interval = (date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000);
		return interval;
	}

	/**
	 * 타임스탬프 형식 문자열
	 * @return
	 */
	public static String getTimestamp() {
		String rtnStr = null;

		// 문자열로 변환하기 위한 패턴 설정(년도-월-일 시:분:초:초(자정이후 초))
		String pattern = "yyyyMMddhhmmssSSS";
		SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		rtnStr = sdfCurrent.format(ts.getTime());
		return rtnStr;
	}


	/**
	 * 년도 목록 (해당 년도만)
	 * @return
	 */
	public static List<StringPair> getYearList() {
		return getYearList(0);
	}


	/**
	 * 년도 목록
	 * @param minYear이 시작 연도, 해당 연도가 마지막 년도가 됨.
	 * @return
	 */
	public static List<StringPair> getYearList(int minYear) {
		List<StringPair> yearList = new ArrayList<StringPair>();
		Calendar cal = Calendar.getInstance();
		int iYear = cal.get(Calendar.YEAR);

		if (minYear <= 0)
			minYear = iYear;

		for(int i = iYear; i > 1900; i--) {
			yearList.add(new StringPair(i + "년", String.valueOf(i)));

			if (minYear >= i)
				break;
		}

		return yearList;
	}






	/**
	 * 1월 부터 12월 리스트 출력
	 * @return
	 */
	public static List<StringPair> getMonthList() {
		List<StringPair> monthList = new ArrayList<StringPair>();
		for(int i = 1; i < 13; i++) {
			if ( i < 10 ) {
				monthList.add(new StringPair(i + "월", "0" + i + ""));
			} else {
				monthList.add(new StringPair(i + "월", i + ""));
			}
		}
		return monthList;
	}



	/**
	 * 시간 값을 이용하여 20자리 고유 문자열 반환
	 *
	 * @return 숫자형태의 문자
	 */
	public static synchronized String getUniqueNumber() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 17 characters
		String sReturn = sdf.format(Calendar.getInstance().getTime());
		try {
			++dummy;
		}
		catch (Exception e) {
			dummy = 0;
		}
		sReturn += padLeft(String.valueOf(dummy % 1000), 3, '0');

		return sReturn;
	}

	/**
	 * java.util.UUID를 이용한 32자리 랜덤 문자열 : 하이픈 제거
	 *
	 * @return
	 */
	public static String getRandomStringByUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	// @Deprecated
	// public static String getWeekName(String wk) {
	// int tmp = Integer.parseInt(wk);
	// if (getString(wk).equals("")) {
	// tmp = 0;
	// }
	// String[] str = { "*", "일", "월", "화", "수", "목", "금", "토" };
	// return str[tmp];
	// }

	/**
	 * 16진 문자열 -&gt; byte 배열 변환 <br>
	 * 	- for RSA encrypt/decrypt <br>
	 */
	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() % 2 != 0) {
			return new byte[] {};
		}

		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
			bytes[(int) Math.floor(i / 2)] = value;
		}
		return bytes;
	}

	/**
	 * 휴대폰 국번 리스트
	 *
	 * @return
	 * @throws Exception
	 */
	public static List<String> getMobileList() throws Exception {
		ArrayList<String> list = new ArrayList<String>();

		list.add("010");
		list.add("011");
		list.add("016");
		list.add("017");
		list.add("018");
		list.add("019");

		return list;
	}

	/**
	 * 전화번호 지역번호 기타 국번 리스트
	 *
	 * @return
	 * @throws Exception
	 */
	public static List<String> getPhoneList() throws Exception {
		ArrayList<String> list = new ArrayList<String>();

		// list.add("010");
		// list.add("011");
		// list.add("016");
		// list.add("017");
		// list.add("018");
		// list.add("019");
		list.add("02");
		list.add("031");
		list.add("032");
		list.add("033");
		list.add("041");
		list.add("042");
		list.add("043");
		list.add("051");
		list.add("052");
		list.add("053");
		list.add("054");
		list.add("055");
		list.add("061");
		list.add("062");
		list.add("063");
		list.add("064");
		list.add("070");

		return list;
	}

	/**
	 * 핸드폰 국번/일반 지역번호, 기타 국번 가져오기
	 *
	 * @return
	 * @throws Exception
	 */
	public static List<String> getMobilePhoneList() throws Exception {
		List<String> list = getMobileList();
		List<String> list2 = getPhoneList();
		list.addAll(list2);
		return list;
	}

	/**
	 * IPIN용 특수 문자 변환
	 * @param paramValue
	 * @param gubun
	 * @return
	 */
	public static String requestReplace(String paramValue, String gubun) {
		String result = "";

		if (paramValue != null) {

			paramValue = paramValue.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

			paramValue = paramValue.replaceAll("\\*", "");
			paramValue = paramValue.replaceAll("\\?", "");
			paramValue = paramValue.replaceAll("\\[", "");
			paramValue = paramValue.replaceAll("\\{", "");
			paramValue = paramValue.replaceAll("\\(", "");
			paramValue = paramValue.replaceAll("\\)", "");
			paramValue = paramValue.replaceAll("\\^", "");
			paramValue = paramValue.replaceAll("\\$", "");
			paramValue = paramValue.replaceAll("'", "");
			paramValue = paramValue.replaceAll("@", "");
			paramValue = paramValue.replaceAll("%", "");
			paramValue = paramValue.replaceAll(";", "");
			paramValue = paramValue.replaceAll(":", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll("#", "");
			paramValue = paramValue.replaceAll("--", "");
			paramValue = paramValue.replaceAll("-", "");
			paramValue = paramValue.replaceAll(",", "");

			if (gubun != "encodeData") {
				paramValue = paramValue.replaceAll("\\+", "");
				paramValue = paramValue.replaceAll("/", "");
				paramValue = paramValue.replaceAll("=", "");
			}

			result = paramValue;

		}
		return result;
	}

	/**
	 * 문자수 단위로 문자열을 자른다.<br>
	 *
	 * @param s : 자를 문자열
	 * @param i : 자를 수
	 * @param plusStr : 플러스될 문자열
	 */
	public static String cutStringPlus(String s, int i, String plusStr) {
		String str = "";
		if (s.length() <= i)
			return s;
		str = s.substring(0, i);
		return str + plusStr;
	}

	/**
	 * 바이트 단위로 문자열을 자른다.<br>
	 *
	 * @param s : 자를 문자열
	 * @param i : 자를 수
	 * @param plusStr : 플러스될 문자열
	 */
	public static String cutStringBytesPlus(String s, int i, String plusStr) {
		if (getString(s).equals(""))
			return "";

		byte abyte0[] = s.getBytes();
		int j = abyte0.length;
		int k = 0;
		if (i >= j)
			return s;
		for (int l = i - 1; l >= 0; l--)
			if ((abyte0[l] & 0x80) != 0)
				k++;
		String str = new String(abyte0, 0, i - k % 3);
		return str + plusStr;
	}

	/**
	 * 바이트 기준 문자열 길이 가져오기 <br><br>
	 * 출처: http://cofs.tistory.com/257 [CofS]<br>
	 * 		- 소스 일부 변경<br>
	 * @param value
	 * @return
	 */
	public static int getByteLength(String value) {
		if (isEmpty(value))
			return 0;

		// 바이트 체크 (영문 1, 한글 2, 특문 1)
		int en = 0;
		int ko = 0;
		int etc = 0;

		char[] txtChar = value.toCharArray();
		for (int j = 0; j < txtChar.length; j++) {
			if (txtChar[j] >= 'A' && txtChar[j] <= 'z') {
				en++;
			}
			else if (txtChar[j] >= '\uAC00' && txtChar[j] <= '\uD7A3') {
				ko++;
				ko++;
			}
			else {
				etc++;
			}
		}
		return (en + ko + etc);

	}





	/**
	 * 문자열 자르기 - 한글 깨지지 않도록<br><br>
	 * 출처 : 박쿤's Blog<br>
	 * @param value 대상 문자열
	 * @param startStr 기준 인덱스 정의할 문자열 (null)
	 * @param length 자를 길이
	 * @param nPrev (0)
	 * @param isNotag 태그 불가 : true
	 * @param isAddDot : 문자열 끝 ... 추가 : false
	 * @return
	 */
	public static String cutString(String value, String startStr, int length, int nPrev, boolean isNotag, boolean isAddDot) {

		String r_val = value;
		int oF = 0, oL = 0, rF = 0, rL = 0;
		int nLengthPrev = 0;
		Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE); // 태그제거
																					// 패턴

		if (isNotag) {
			r_val = p.matcher(r_val).replaceAll("");
		} // 태그 제거
		r_val = r_val.replaceAll("&amp;", "&");
		r_val = r_val.replaceAll("(!/|\r|\n|&nbsp;)", ""); // 공백제거

		try {
			byte[] bytes = r_val.getBytes("UTF-8"); // 바이트로 보관
			if (startStr != null && !startStr.equals("")) {
				nLengthPrev = (r_val.indexOf(startStr) == -1) ? 0 : r_val.indexOf(startStr); // 일단 위치찾고
				nLengthPrev = r_val.substring(0, nLengthPrev).getBytes("MS949").length; // 위치까지길이를 byte로 다시 구한다
				nLengthPrev = (nLengthPrev - nPrev >= 0) ? nLengthPrev - nPrev : 0; // 좀 앞부분부터 가져오도록한다.
			}

			// x부터 y길이만큼 잘라낸다. 한글안깨지게.
			int j = 0;

			if (nLengthPrev > 0)
				while (j < bytes.length) {
					if ((bytes[j] & 0x80) != 0) {
						oF += 2;
						rF += 3;
						if (oF + 2 > nLengthPrev) {
							break;
						}
						j += 3;
					}
					else {
						if (oF + 1 > nLengthPrev) {
							break;
						}
						++oF;
						++rF;
						++j;
					}
				}

			j = rF;

			while (j < bytes.length) {
				if ((bytes[j] & 0x80) != 0) {
					if (oL + 2 > length) {
						break;
					}
					oL += 2;
					rL += 3;
					j += 3;
				}
				else {
					if (oL + 1 > length) {
						break;
					}
					++oL;
					++rL;
					++j;
				}
			}

			r_val = new String(bytes, rF, rL, "UTF-8"); // charset 옵션

			if (isAddDot && rF + rL + 3 <= bytes.length) {
				r_val += "...";
			} // ...을 붙일지말지 옵션
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return r_val;
	}

	/**
	 * 지정한 문자열 반복 결합 후 리턴 <br>
	 * @param character 반복할 문자열
	 * @param repeatCount 반복 수
	 * @return
	 */
	public static String repeatChar(char character, int repeatCount) {
		StringBuffer sb = new StringBuffer(repeatCount);

		while (sb.length() < repeatCount)
			sb.append(character);

		return sb.toString();
	}

	/**
	 * 문자열 오른쪽 패딩<br>
	 * @param value 적용시킬 문자열
	 * @param width 패딩 길이
	 * @param paddingChar 채울 문자
	 * @return 오른쪽으로 패딩된 문자열
	 */
	public static String padRight(String value, int width, char paddingChar) {
		try {
			if (value.length() >= width)
				return value;

			String sReturn = value;
			while (sReturn.length() < width)
				sReturn = sReturn + String.valueOf(paddingChar);

			return sReturn;
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * 문자열 왼쪽 패딩<br>
	 * @param value 적용시킬 문자열
	 * @param width 패딩 길이
	 * @param paddingChar 채울 문자
	 * @return 왼쪽으로 패딩된 문자열
	 */
	public static String padLeft(String value, int width, char paddingChar) {
		try {
			if (value.length() >= width)
				return value;

			String sReturn = value;
			while (sReturn.length() < width)
				sReturn = String.valueOf(paddingChar) + sReturn;

			return sReturn;
		}
		catch (Exception e) {
			return "";
		}
	}

	/**
	 * 마스킹 : 지정 문자열 별표 처리
	 * @param value 별표 처리할 문자열
	 * @param startIndex 별표 처리 시작할 문자열 인덱스
	 * @param length 별표 길이
	 * @return
	 */
	public static String maskingChar(String value, int startIndex, int length) {
		try {
			if (value.length() < (startIndex + 1))
				return value;

			char[] chars = value.toCharArray();
			for (int i = 0; i < chars.length ; i++) {
				if (i >= startIndex && i <= startIndex + length - 1)
					chars[i] = '*';
			}

			return new StringBuffer().append(chars).toString();
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "null";
		}
	}

	/**
	 * 마스킹 : 지정문자열 별표 처리 - 인덱스 이상 문자열 모두 별표 처리
	 * @param value 별표 처리할 문자열
	 * @param startIndex 별표 처리 시작할 문자열 인덱스
	 * @return
	 */
	public static String maskingChar(String value, int startIndex) {
		return maskingChar(value, startIndex, value.length() - startIndex);
	}

	/**
	 * 파일 용량 읽기 쉬운 형태로 가져오기 - KB, MB, GB 단위로 가져옴
	 *
	 * @param fileSize
	 * @return
	 */
	public static String getFileSize(long fileSize) {
		try {
			String sRtn = "0KB";

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			// GB
			if (fileSize >= Math.pow(2, 30))
				sRtn = nf.format((double) (fileSize / Math.pow(2, 30))) + "GB";
			else if (fileSize >= Math.pow(2, 20))
				sRtn = nf.format((double) (fileSize / Math.pow(2, 20))) + "MB";
			else if (fileSize > 0)
				sRtn = nf.format((double) (fileSize / Math.pow(2, 10))) + "KB";

			return sRtn;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "0KB";
		}
	}

	// TODO : 코드 수정 필요
	// QRCode 생성기 호출 : StringUtil.qrCodeGenerator("좌석번호 : 132번");
	// public static void qrCodeGenerator(String text) {
	// QRCodeWriter qrCodeWriter = new QRCodeWriter();
	// try {
	// text = new String(text.getBytes("UTF-8"), "ISO-8859-1");
	// BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);
	// String filename = EgovProperties.getProperty("Globals.fileStorePath") + EgovProperties.getProperty("Globals.fileCommonStorePath") + "qrcode/" + getTimestamp() + getRandomValue(GenType.Number, 3) + ".png";
	// MatrixToImageWriter.writeToFile(bitMatrix, "png", new File(filename));
	// } catch (WriterException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * XSS parsing<br>
	 *  - HTML 태그 불가능, script 태그 불가능
	 *
	 * @param value
	 * @return
	 */
	public static String lucyXSS(String value) {
		return lucyXSS(value, true, false);
	}

	/**
	 * XSS parsing<br>
	 *
	 * @param value
	 * @param disableHTMLTag
	 *            - true: html 태그 사용 불가능, false: html 태그 사용 가능
	 * @return
	 */
	public static String lucyXSS(String value, boolean disableHTMLTag) {
		return lucyXSS(value, disableHTMLTag, false);
	}

	/**
	 * XSS parsing<br>
	 *
	 * @param value
	 * @param disableHTMLTag
	 *            - true: html 태그 사용 불가능, false: html 태그 사용 가능
	 * @param disableXSSParse
	 *            - true:XSS 파싱 안함, false: XSS 파싱 사용
	 * @return
	 */
	public static String lucyXSS(String value, boolean disableHTMLTag, boolean disableXSSParse) {
		if (disableXSSParse)
			return value;

		if (isEmpty(value))
			return value;

		try {
			String sParser = "../lucy-xss.xml";
			if (disableHTMLTag)
				sParser = "../lucy-xss-disableTag.xml";
			XssFilter filter = XssFilter.getInstance(sParser);

			// 변경되는 태그 앞에 생성되는 주석 문자열
			// <!-- Not Allowed Attribute Filtered -->
			// <!-- Not Allowed Tag Filtered --><
			String sRtn = filter.doFilter(value);
			sRtn = sRtn.replaceAll("<!-- Not Allowed Attribute Filtered -->", "");
			sRtn = sRtn.replaceAll("<!-- Not Allowed Tag Filtered -->", "");

			return sRtn;
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return value;
		}
	}




	/**
	 * 서버 OS 반환
	 * <ul>
	 * 	<li>windows</li>
	 * 	<li>linux</li>
	 * 	<li>darwin(mac)</li>
	 * 	<li>solaris</li>
	 * </ul>
	 * @return os.name
	 */
	public static String getOperatingSystemName() {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if(osName.startsWith("windows"))
			return "windows";
		else if(osName.startsWith("linux"))
			return "linux";
		else if(osName.startsWith("mac os"))
			return "darwin";
		else if(osName.startsWith("sunos")||osName.startsWith("solaris"))
			return "solaris";
		else return osName;
	}


	/**
	 * 모바일접속 여부 확인
	 * @param request
	 * @return true(모바일), false(그 외)
	 */
	public static boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        boolean mobile1 = userAgent.matches(".*(iPhone|iPod|Android|Windows CE|BlackBerry|Symbian|Windows Phone|webOS|Opera Mini|Opera Mobi|POLARIS|IEMobile|lgtelecom|nokia|SonyEricsson).*");
        boolean mobile2 = userAgent.matches(".*(LG|SAMSUNG|Samsung).*");
        if(mobile1 || mobile2) {
            return true;
        }
        return false;
    }


	/**
	 * 서버의 고정IP 가져오기
	 * @return
	 */
	public static String getServerIP(){

		String hostAddr = "";

		try {

			Enumeration<NetworkInterface> nienum = NetworkInterface.getNetworkInterfaces();

			while (nienum.hasMoreElements()) {

				NetworkInterface ni = nienum.nextElement();
				Enumeration<InetAddress> kk= ni.getInetAddresses();

				while (kk.hasMoreElements()) {

					InetAddress inetAddress = kk.nextElement();

					if (
						!inetAddress.isLoopbackAddress()
						&& !inetAddress.isLinkLocalAddress()
						&& inetAddress.isSiteLocalAddress())
					{
						hostAddr = inetAddress.getHostAddress().toString();
					}
				}

			}

		} catch (SocketException e) {
			logger.error("getServerIP : {}", e.getMessage());
		}

		return hostAddr;
	}

	/**
	 * API XML태그 제거
	 * @return
	 */
	public String xmlFormat(String line) {
        line = line.replace("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\">", "");
        line = line.replace("</string>", "");
		return line;
	}

	public static boolean isEmpty(Object object) {
		String str = String.valueOf(object);
		return str == null || str.length() == 0;
	}
}