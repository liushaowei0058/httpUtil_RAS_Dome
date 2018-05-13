package com.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SerialNumberUtil {

	/**
	 * 生成订单号
	 * 
	 * @author liuh
	 * @date 2018-03-10
	 * @see [相关类/方法]
	 * @since [产品/模块版本]
	 */
	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式yyyyMMddHHmmss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateString = formatter.format(currentTime);
		System.out.println("TIME:::" + dateString);
		return dateString;
	}

	/**
	 * 由年月日时分秒+3位随机数 生成流水号
	 * 
	 * @return
	 */
	public static String createBillNo() {
		String t = getStringDate();
		int x = (int) (Math.random() * 900) + 100;
		String billNo = t + x;
		return billNo;
	}
}
