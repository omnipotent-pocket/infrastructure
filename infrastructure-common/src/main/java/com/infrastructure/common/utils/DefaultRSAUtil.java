package com.infrastructure.common.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * RSA签名验签类
 */
public class DefaultRSAUtil {

    /**
     * 签名算法
     */
    public static final String SHA1_ALGORITHMS = "SHA1WithRSA";
    public static final String SHA256_ALGORITHMS = "SHA256WithRSA";
    public static final String RSA = "RSA";


    /**
     * RSA签名
     * @param content 加签参数
     * @param privateKey 私钥
     * @param sngAlgorithms 签名算法，如SHA256WithRSA、SHA1WithRSA
     * @param KeyType 算法Key类型，如RSA、AES
     * @return
     */
    public static byte[] sign(String content, String privateKey,String sngAlgorithms, String KeyType) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(decryptBASE64(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(KeyType);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(sngAlgorithms);
            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));
            byte[] signed = signature.sign();
            return signed;
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    /**
     * RSA签名
     * @param content 加签参数
     * @param privateKey 私钥
     * @param sngAlgorithms 签名算法，如SHA256WithRSA、SHA1WithRSA
     * @return
     */
    public static byte[] defaultRSASign(String content, String privateKey,String sngAlgorithms) {
        return sign(content,privateKey,sngAlgorithms,RSA);
    }

    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 分配给开发商公钥
     * @param sngAlgorithms 签名算法，如SHA256WithRSA、SHA1WithRSA
     * @param KeyType 算法Key类型，如RSA、AES
     * @return
     */
    public static boolean doCheck(String content, String sign, String publicKey,String sngAlgorithms, String KeyType) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KeyType);
            byte[] encodedKey = decryptBASE64(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));


            java.security.Signature signature = java.security.Signature.getInstance(sngAlgorithms);

            signature.initVerify(pubKey);
            signature.update(content.getBytes("UTF-8"));

            boolean bverify = signature.verify(decryptBASE64(sign));
            return bverify;

        } catch (Exception e) {
            return false;
        }
    }
    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 分配给开发商公钥
     * @param sngAlgorithms 签名算法，如SHA256WithRSA、SHA1WithRSA
     * @return
     */
    public static boolean defaultRSACheck(String content, String sign, String publicKey,String sngAlgorithms) {
        return doCheck(content,sign,publicKey,sngAlgorithms,RSA);
    }

    public static String encryptBASE64(byte[] key) {
        String base64encodedString = Base64.getEncoder().encodeToString(key);
        return base64encodedString.replaceAll("[\\s*\t\n\r]", "");
    }
    public static byte[] decryptBASE64(String key) {
        byte[] base64decodedBytes = Base64.getDecoder().decode(key.replaceAll("[\\s*\t\n\r]", ""));
        return base64decodedBytes;
    }

}
