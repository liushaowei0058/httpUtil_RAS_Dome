package com.demo.test;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.demo.Constants;
import com.demo.util.HttpClientUtils;
import com.demo.util.RsaSignature;
import com.demo.util.SerialNumberUtil;
import com.demo.util.ThreeDesUtil;


public class BanknewTest {
	//接口调用地址
	private static String url = "http://47.100.160.202:8765//feign/apiMobileTest/";
	// 3des密钥
	private static String des_key = "0987@@asgd4862##yaag&&&&";
	
	//rsa客户私钥
	private static String rsa_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdZV8cWSs8LfeVxHJs9WbGj9BPMYk7LW1jurd"
			+ "im+Doa6j7OXyz4k2yITBVkQ0avTPv9nhyTlK/1RZ4bIWH3z9/ZiTkYGUI7HCRrhO96Ux12H252lXZDfUyDNfyo7kLC0yRU6MfPMCCc1936AAg"
			+ "Q09gOhtZV5bZ6v+c7pexyDr94GvDD9rgkodkUvZYmoxGPJEz0vJO6dsu5FOvT7K8pQ/R6OUIZDeSAfltkfJQYYKLq21B3y+PiA8Cv99t6i3EI"
			+ "lnZhXFpSPotqvdpQurZrsTUmrDjkRSolaVDLNSr/Y7FS7cbXKtuAw2otLiE+bU2/mApnv1g94I4l+CwKSRG4gvRAgMBAAECggEAVeMlPWDjGN"
			+ "m0isNB9XcL+Dot36kWS3aEhLE0tNh+qBVnhn9eLwLEhvlZWLfoQLkSCFWkcCuROCDdPCVr0DLggXKJmx2GxJHQ843cM/uEv6G9KZec1O6q5/7"
			+ "eMAdrSD2IMJ1FHByYqBaw98tfljtkqwvKNLx5Er0LcuU3Xcn3duUOdGbTxf3N1hBXeDhT5kHHek0QjPh+X8YbTWEedJxmgdu17Z/m+92WQAF9"
			+ "j/JCwLE4SiE8+ciqx1ZbMSd6zz6q5jKnVKfexKTVhL3ne7DaSbms4lNbuwlejtSR0tQLUqado73IyHb5bzSK+NO1q+goTRzW23031bH49GWL6"
			+ "xI4CQKBgQDP0IWQ0Pxg6a7KHaLiP5qCiVguwvWZFF1mnrN7DjjuoPA6F6jm4rZ40KtPr4hpfI9mD4uCzfU6cbWuiJRQIHu8L8eNpM9AaJkes0"
			+ "Qr8PV0at5BJYodakCZ8fkK2kUl0ouRbCSvYXnLUcm52dTnPRglfUXMtDGgu0Fw1K0gQJTBDwKBgQDB5Bl/7e46nAqSBO/f+2WHBv3Fflh7I99"
			+ "eM1NcMPjI8LOMIpzm2yhh3PiFJ2Y23CRA10oLxRkasMlYmZvReqMBygA5IS7WlgNT+3NMO9wMCBEPJXcCXWgAracQKY4o52XR6fNcK7EA5VP6"
			+ "qhCr554vaTyveiI3FWmwQIPYRJClHwKBgAzybc/W5VJYXLZk4Ef+NG8bhW3sYYa2+RaynnX1LeDYD/9eh/xSdZHTVkjxo6sGwpxWLZfpovSQL"
			+ "wR/p9XSIENaKrGNaggWPCvoeT/PlEs7eTtPzmoEu5+brqtZa+NuWZmmxiZTCVZ8cvfsNVJuwPlc86NMOqdL40jXRy3yOBJLAoGBALo4OcWE36"
			+ "mq9PwJgdm9umVVcaadEp4ydfYjlYyV/FwB0nBlTgzrsH1NneNFVAacqLUeER/8zX+1C6zs25r2DllJia1VEYdHzSp79uDhHRbxKHFDqmm7DIi"
			+ "aQDplrwPaOxDBgdqkm/wgRegaLqjsbCFdEcv3oaUGC9wcGoU8VTBBAoGBAKGyfPl5vdSjNDrsePwIadMWCsBr2WIf8mq9UUz/Kn2yzDRKiwNh"
			+ "KdYlZ8byWv9a/L3SXJkGWqf9gXXO68OiAcms8UbJ4tzvW+fVxbQHTLzJm0DIwvlMW3b9G3ICEae1b/udrtBXQygpsKingaYUUXqWmRcASkmeF/htNs8mTpJk";
	//客户帐号
	private static String apiName = "S1447925";
	//客户密码
	private static String password = "pwd3971603";
	
	public static void main(String[] args) throws Exception {
		identyCheckTest("name=刘鸿&idnum=330624198103129947&idtype=01&cardno=6214830103030036&mobile=18655764785");
	}
	
	private static void identyCheckTest(String paramString){
		//接口调用地址
		String urlstr = url + "checkUserInfoN";
		// 订单号
		String order_no = SerialNumberUtil.createBillNo();
		//方法名
		String method = "normal_checkBankInfo";
		
		JSONObject paramJson = new JSONObject();
		paramJson.put("apiName", apiName);
		paramJson.put("password", password);
		paramJson.put("method", method);
		paramJson.put("order_no", order_no);
		paramJson.put("paramString", paramString);
		//生产参数串,参数串需要以参数名的首字母顺序排序,如首字母一样,则往后推一个字母顺序排序，以此类推
		StringBuilder builder = new StringBuilder();
		builder.append("apiName=".toUpperCase() + apiName);
		builder.append("&method=".toUpperCase() + method);
		builder.append("&order_no=".toUpperCase() + order_no);
		builder.append("&paramString=".toUpperCase() + paramString);
		builder.append("&password=".toUpperCase() + password);
		
		//rsa数据加密
		String sign = RsaSignature.rsaSign(builder.toString(), rsa_key, Constants.CHARSET_UTF8);
		paramJson.put("sign", sign);

		System.out.println("发送报文信息:" + builder.toString());
		//3DES加密生成密文
		byte[] requsest = ThreeDesUtil.encryptMode(des_key.getBytes(), paramJson.toJSONString().getBytes());
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("encryptParamStr", ThreeDesUtil.byte2Hex(requsest));
		param.put("appid", apiName);
		//调用接口
		String code = HttpClientUtils.post(urlstr, param);
		System.out.println(paramString+":"+code);
	}
}
