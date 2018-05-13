/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;

import com.demo.Constants;
import com.sun.xml.internal.ws.handler.HandlerException;





public class RsaSignature
{
    
    /** RSA最大加密明文大小 */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /** RSA最大解密密文大小 */
    private static final int MAX_DECRYPT_BLOCK = 128;
    
    /**
     * 
     * @param sortedParams
     * @return
     */
    
    public static String rsaSign(String content, String privateKey, String charset)
        throws HandlerException
    {
        try
        {
            PrivateKey priKey = getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));
            
            Signature signature = Signature.getInstance(Constants.SIGN_ALGORITHMS);
            
            signature.initSign(priKey);
            
            if (StringUtils.isEmpty(charset))
            {
                signature.update(content.getBytes());
            }
            else
            {
                signature.update(content.getBytes(charset));
            }
            
            byte[] signed = signature.sign();
            
            return new String(Base64.encodeBase64(signed));
        }
        catch (Exception e)
        {
            throw new HandlerException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }
    
    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
        throws Exception
    {
        if (ins == null || StringUtils.isEmpty(algorithm))
        {
            return null;
        }
        
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        
        byte[] encodedKey = StreamUtil.readText(ins).getBytes();
        
        encodedKey = Base64.decodeBase64(encodedKey);
        
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }
    
    public static String getSignCheckContentV1(Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }
        
        params.remove("sign");
        params.remove("sign_type");
        
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        
        for (int i = 0; i < keys.size(); i++)
        {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }
        
        return content.toString();
    }
    
    public static String getSignCheckContentV2(Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }
        
        params.remove("sign");
        
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        
        for (int i = 0; i < keys.size(); i++)
        {
            String key = keys.get(i);
            String value = params.get(key);
            content.append((i == 0 ? "" : "&") + key + "=" + value);
        }
        
        return content.toString();
    }
    
    public static boolean rsaCheckV1(Map<String, String> params, String publicKey, String charset)
        throws HandlerException
    {
        String sign = params.get("sign");
        String content = getSignCheckContentV1(params);
        
        return rsaCheckContent(content, sign, publicKey, charset);
    }
    
    public static boolean rsaCheckV2(Map<String, String> params, String publicKey, String charset)
        throws HandlerException
    {
        String sign = params.get("sign");
        String content = getSignCheckContentV2(params);
        
        return rsaCheckContent(content, sign, publicKey, charset);
    }
    
    public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset)
        throws HandlerException
    {
        try
        {
            PublicKey pubKey = getPublicKeyFromX509(Constants.SIGN_TYPE_RSA, new ByteArrayInputStream(publicKey.getBytes()));
            
            java.security.Signature signature = java.security.Signature.getInstance(Constants.SIGN_ALGORITHMS);
            
            signature.initVerify(pubKey);
            
            if (StringUtils.isEmpty(charset))
            {
                signature.update(content.getBytes());
            }
            else
            {
                signature.update(content.getBytes(charset));
            }
            
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        }
        catch (Exception e)
        {
            throw new HandlerException("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }
    
    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
        throws Exception
    {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        
        byte[] encodedKey = writer.toString().getBytes();
        
        encodedKey = Base64.decodeBase64(encodedKey);
        
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }
    
    
    /**
     * 公钥加密
     * 
     * @param content 待加密内容
     * @param publicKey 公钥
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 密文内容
     * @throws HandlerException
     */
    public static String rsaEncrypt(String content, String publicKey, String charset)
        throws HandlerException
    {
        try
        {
            PublicKey pubKey =
                getPublicKeyFromX509(Constants.SIGN_TYPE_RSA, new ByteArrayInputStream(publicKey.getBytes()));
            Cipher cipher = Cipher.getInstance(Constants.SIGN_TYPE_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] data = StringUtils.isEmpty(charset) ? content.getBytes() : content.getBytes(charset);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK)
                {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                }
                else
                {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = Base64.encodeBase64(out.toByteArray());
            out.close();
            
            return StringUtils.isEmpty(charset) ? new String(encryptedData) : new String(encryptedData, charset);
        }
        catch (Exception e)
        {
            throw new HandlerException("EncryptContent = " + content + ",charset = " + charset, e);
        }
    }
    
    /**
     * 私钥解密
     * 
     * @param content 待解密内容
     * @param privateKey 私钥
     * @param charset 字符集，如UTF-8, GBK, GB2312
     * @return 明文内容
     * @throws HandlerException
     */
    public static String rsaDecrypt(String content, String privateKey, String charset) throws HandlerException
    {
        try
        {
            PrivateKey priKey =
                getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));
            Cipher cipher = Cipher.getInstance(Constants.SIGN_TYPE_RSA);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] encryptedData =
                StringUtils.isEmpty(charset) ? Base64.decodeBase64(content.getBytes())
                    : Base64.decodeBase64(content.getBytes(charset));
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0)
            {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK)
                {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                }
                else
                {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            
            return StringUtils.isEmpty(charset) ? new String(decryptedData) : new String(decryptedData, charset);
        }
        catch (Exception e)
        {
            throw new HandlerException("EncodeContent = " + content + ",charset = " + charset, e);
        }
    }
    
    /**
     * 用RSA公钥解密
     * 
     * @param privKeyInByte
     *            公钥打包成byte[]形式
     * @param data
     *            要解密的数据
     * @return 解密数据
     */
    public static byte[] decryptByRSA1(byte[] pubKeyInByte, byte[] data) {
     try {
       KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
       X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
       PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
       Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
       cipher.init(Cipher.DECRYPT_MODE, pubKey);
       return cipher.doFinal(data);
      } catch (Exception e) {
       return null;
      }
    }
    
}
