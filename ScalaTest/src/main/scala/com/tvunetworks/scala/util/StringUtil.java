package com.tvunetworks.scala.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * String Utility Class This is used to encode passwords programmatically
 * 
 * <p>
 * <a h ref="StringUtil.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class StringUtil {
	// ~ Static fields/initializers
	// =============================================

	private final static Log log = LogFactory.getLog(StringUtil.class);

	// ~ Methods
	// ================================================================

	/**
	 * Encode a string using algorithm specified in web.xml and return the
	 * resulting encrypted password. If exception, the plain credentials string
	 * is returned
	 * 
	 * @param password
	 *            Password or other credentials to use in authenticating this
	 *            username
	 * @param algorithm
	 *            Algorithm used to do the digest
	 * 
	 * @return encypted password based on the algorithm.
	 */
	public static String encodePassword(String password, String algorithm) {
		byte[] unencodedPassword = password.getBytes();

		MessageDigest md = null;

		try {
			// first create an instance, given the provider
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			log.error("Exception: " + e);

			return password;
		}

		md.reset();

		// call the update method one or more times
		// (useful when you don't know the size of your data, eg. stream)
		md.update(unencodedPassword);

		// now calculate the hash
		byte[] encodedPassword = md.digest();

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}

			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}

		return buf.toString();
	}

	/**
	 * check if a string is null or empty
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullEmp(String str) {
		// 2007.1.15 by Ryu Pan
		// if(str == null || str.length()==0){
		if (str == null || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * check if a string is null or empty
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullEmpTrim(String str) {

		if (str == null || str.length() == 0 || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param str
	 * @return
	 */
	public static String null2Empty(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * @param date
	 * @return
	 */
	public static String null2Empty(Date date) {
		if (date == null) {
			return "";
		} else {
			return date.toString();
		}

	}

	public static String null2Empty(Long var) {
		if (var == null) {
			return "";
		} else {
			return var.toString();
		}

	}

	public static String null2Empty(BigInteger var) {
		if (var == null) {
			return "";
		} else {
			return var.toString();
		}
	}

	public static String null2Empty(Integer var) {
		if (var == null) {
			return "";
		} else {
			return var.toString();
		}

	}

	public static String empty2Null(String str) {
		if (str == null || str.trim().length() == 0) {
			return null;
		} else {
			return str;
		}

	}

	public static int utf8ByteSize(String str) {
		if (str == null) {
			return 0;
		} else {
			try {
				return str.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				return 0;
			}
		}
	}

	public static String convertCurrency(Object obj) {
		try {
			NumberFormat nf = new DecimalFormat("0.00");
			return nf.format(obj);
		} catch (Exception e) {
			return "";
		}

	}
	
	public static boolean formatCheck(String reg,String str){
		if(str == null){
			return true;
		}else{
			boolean tem = false;
			try {
				tem = Pattern.compile(reg).matcher(str).matches();
			} catch (Exception e) {
				tem = false;
				e.printStackTrace();
			}
			return tem;
		}
	}

	public static String hexToLowerString(String hexStr) {
		if(hexStr==null){
			return null;
		}
		hexStr=hexStr.toLowerCase();
		if(hexStr.indexOf("0x")==0&&hexStr.length()>2){
			hexStr=hexStr.substring(2);
		}
		if(!hexStr.matches("^[\\da-f]+$")){
			hexStr=hexStr.replaceAll("[^\\da-f]+","");
		}
		return hexStr;
	}
	
	/**
	 * 
	 * @param currentTime: current timestamp
	 * @param type: three values, 1 mean three month, 2 mean 6month, 3 mean one year
	 * @return expire timestamp
	 */
	public static long getExpireTime(long currentTime, int type) {
		Date date = new Date(currentTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if(type == 1) {
			calendar.add(Calendar.MONTH, 3);
		} else if(type == 2) {
			calendar.add(Calendar.MONTH, 6);
		} else {
			calendar.add(Calendar.YEAR, 1);
		}
		Date theDate = calendar.getTime();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//System.out.println(sdf.format(theDate));
		return theDate.getTime();
	}
	
	public static double getTwoPointValue(String value) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal bd2 = bd.setScale(2 , BigDecimal.ROUND_HALF_UP);
		return Double.parseDouble(bd2.toString());
	}
	
	public static double divideAndGet2PointValue(long value1, long value2) {
		BigDecimal bd1 = new BigDecimal(value1);
		BigDecimal bd2 = new BigDecimal(value2);
		Double result = bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}
	
	public static String weixinFormatTime() {
		Date time = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(time);
	}
	
	public static String specifyWeixinFormatTime(long time) {
		Date date = new Date(time);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}
	
	public static String convertWeixinTimeToUtcSeconds(String weixinTime) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = simpleDateFormat.parse(weixinTime);
		BigDecimal curNum = new BigDecimal(date.getTime());
		BigDecimal ms = new BigDecimal(1000);
		BigDecimal result = curNum.divide(ms).setScale(0 , BigDecimal.ROUND_HALF_UP);
		return result.toString();
	}
	
	public static String formatTime() {
		Date time = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(time);
	}
	
	public static String formatXmlValue(String value){
		if(value!=null && !value.equals("")){
			value = "<![CDATA["+value+"]]>";
		}
		return value;
	}
	
	public static String getUTCSeconds() {
		long current = System.currentTimeMillis();
		BigDecimal curNum = new BigDecimal(current);
		BigDecimal ms = new BigDecimal(1000);
		BigDecimal result = curNum.divide(ms).setScale(0 , BigDecimal.ROUND_HALF_UP);
		return result.toString();
	}
	
	/**
	 * 将对象转换为xml格式字符串
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static String changeObjToXml(Object obj) throws Exception {
		String xml = "";
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JAXBContext context;
		System.getProperty("file.encoding");
		context = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.marshal(obj, os);
		xml = new String(os.toByteArray(), "UTF-8");
		return xml;
	}
	
	public static String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static String getRandomString(int length) {
		StringBuffer stringBuffer = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			stringBuffer.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return stringBuffer.toString();
	}
	
	/**
	 * 获得当前子类及其父类中的方法声明
	 * @param subClass
	 * @param methodName
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getUnitClass(Class<?> subClass, String methodName) throws NoSuchMethodException {
		for (Class<?> superClass = subClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, String.class);
			} catch (NoSuchMethodException e) {
				//Method不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchMethodException();
	}
	
	/**
	 * 将sql查询的关键字进行特殊字符转义
	 * @param str
	 * @return
	 */
	public static String escapeSpecialChar4Sql(String str) {
		if(str != null && !str.equals("")) {
			str = str.replaceAll("'", "''");
			str = str.replaceAll("%", "\\\\%");
		}
		return str;
	}
	
	/**
	 * Get the time of first day in the month
	 * @param date
	 * @return
	 */
	public static long getMonthFirstDayTime() {
		Date date = new Date();
		GregorianCalendar gcFirst = (GregorianCalendar) Calendar.getInstance();
		gcFirst.setTime(date);
		gcFirst.set(Calendar.DAY_OF_MONTH, 1);
		gcFirst.set(Calendar.HOUR_OF_DAY, 0);
		gcFirst.set(Calendar.MINUTE, 0);
		gcFirst.set(Calendar.SECOND, 0);
		gcFirst.set(Calendar.MILLISECOND, 0);
		return gcFirst.getTimeInMillis();
	}
	
	
	public static void main(String[] args) {
		//System.out.println(hexToLowerString("f248tfc428vbocgq3948fhq39gf2789429"));
		//System.out.println(Math.floor(5/1.8));
		//System.out.println(System.currentTimeMillis());
		System.out.println(getUTCSeconds());
		try {
			System.out.println(convertWeixinTimeToUtcSeconds("20161018183650"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(specifyWeixinFormatTime(System.currentTimeMillis()));
	}
}
