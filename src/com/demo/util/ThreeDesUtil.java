package com.demo.util;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ThreeDesUtil {

  private static final String Algorithm = "DESede"; // 定义 加密算法,可用
                                                    // DES,DESede,Blowfish

  static {
    Security.addProvider(new com.sun.crypto.provider.SunJCE());
  }

  // 加密字符串
  public static byte[] encryptMode(byte[] keybyte, byte[] src) {
    try {
      // 生成密钥
      SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); // 加密
      Cipher c1 = Cipher.getInstance(Algorithm);
      c1.init(Cipher.ENCRYPT_MODE, deskey);
      return c1.doFinal(src);
    } catch (java.security.NoSuchAlgorithmException e) {
    } catch (javax.crypto.NoSuchPaddingException e) {
    } catch (java.lang.Exception e) {
    }
    return null;
  }
  
  // 解密字符串
  public static byte[] decryptMode(byte[] keybyte, byte[] src) {
    try {
      // 生成密钥
      SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); // 加密
      Cipher c1 = Cipher.getInstance(Algorithm);
      c1.init(Cipher.DECRYPT_MODE, deskey);
      return c1.doFinal(src);
    } catch (java.security.NoSuchAlgorithmException e) {
    } catch (javax.crypto.NoSuchPaddingException e) {
    } catch (java.lang.Exception e) {
    }
    return null;
  }

  // 转换成十六进制字符串
  public static String byte2Hex(byte[] b) {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
      if (stmp.length() == 1) {
        hs = hs + "0" + stmp;
      } else {
        hs = hs + stmp;
      }
      // if (n < b.length - 1)
      // hs = hs + ":";
    }
    return hs.toUpperCase();
  }

  public static byte[] hexStringToBytes(String hexString) {
    if (hexString == null || hexString.equals("")) {
      return null;
    }
    hexString = hexString.toUpperCase();
    int length = hexString.length() / 2;
    char[] hexChars = hexString.toCharArray();
    byte[] d = new byte[length];
    for (int i = 0; i < length; i++) {
      int pos = i * 2;
      d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
    }
    return d;
  }

  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }

  public static void main(String[] args) throws Exception {
	  String str = "6E3A32D833F2EFA9812A324F791060369CB47EA0E7535BEDFCDE05EDD08A81AD2D104CA56AB4CD5575C95AE8DB3D7644F1C5A869CB9F736250985EE38A4DEFEA28178631D0651AE850E6479A434421787F5412549DF2C0DFC21ADFE9B74C663BFE5266A4D174015EF498DBCB428B68235F85AFAFDA60F75A33C8FE25881163C557861794F5990CD17529597809EAEFBFFF9EA1E5B02FC06B2CCC0712D4F7D61877DBAF98EEAD255C5A83E0D98A5D32D7A4288B03ACB0F4562C8BB2EE7EFDA9C68F186132D2043DFE78D9FEE228CD5E63C519C4BFD4D4BB98D05862688B7C86F0C5CA38AB909388765487680F8A72AC1CEC82AEA15232616B291E3F0A59BB8F8B33E685354695B3767B7EDA40B2A316C16CFC47DE1A10EB00866BED5F4BCB6D2BDD70CF282BCF8CC1E4518D6732E0934A842515BCBBC4D3345ABCCF6465160827D7DCD606CF02ADACE6FA2F2C648D0E156A5541B6639867B9BF674054F43D78AC89D7F3B04F7B43FFA799FD5C24A2D902AEC324A5E51B173C";
	  String des_key = "7523@@abcd7523@@abcd####";
	  byte[] keybyte = des_key.getBytes();
	  String result = new String(decryptMode(keybyte,hexStringToBytes(str)));
	  System.out.println(result);
  }
}