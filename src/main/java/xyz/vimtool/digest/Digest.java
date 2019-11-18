package xyz.vimtool.digest;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/11/1
 */
public class Digest {

    public static void main(String[] args) throws Exception {
        String src = "中文";
        byte[] bytes = src.getBytes();
        System.out.println(digest(bytes, "MD5"));
        System.out.println(digest(bytes, "SHA-1"));
        System.out.println(digest(bytes, "SHA-256"));
        System.out.println(digest(bytes, "SHA-512"));

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println(hmac(bytes, "HmacMD5"));

//        System.out.println(hmac(bytes, "HmacSHA1"));
//        System.out.println(hmac(bytes, "HmacSHA256"));
//        System.out.println(hmac(bytes, "HmacSHA512"));
    }

    public static String digest(byte[] source, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(source);
        byte[] result = digest.digest();
        return new BigInteger(1, result).toString(16);
    }

    public static String hmac(byte[] source, String algorithm) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        SecretKey key = keyGen.generateKey();

        String s = SignUtils.hmacMd5(source, key.getEncoded());
        System.out.println(s);

        Mac mac = Mac.getInstance(algorithm);
        mac.init(key);
        mac.update(source);
        byte[] result = mac.doFinal();
        return new BigInteger(1, result).toString(16);
    }
}
