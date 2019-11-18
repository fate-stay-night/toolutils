package xyz.vimtool.digest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;

/**
 * hmac
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/9/19
 */
public class SignUtils {

    /**
     * hmacSha1算法
     *
     * @param source 源字符串
     * @return 目标字符串
     */
    public static String hmacMd5(String source, String secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, secret.getBytes());
        byte[] bytes = mac.doFinal(source.getBytes());
        return Hex.encodeHexString(bytes);
    }

    /**
     * hmacSha1算法
     *
     * @param source 源字符串
     * @return 目标字符串
     */
    public static String hmacMd5(byte[] source, byte[] secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_MD5, secret);
        byte[] bytes = mac.doFinal(source);
        return Hex.encodeHexString(bytes);
    }

    /**
     * hmacSha1算法
     *
     * @param source 源字符串
     * @return 目标字符串
     */
    public static String hmacSha1(String source, String secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_1, secret.getBytes());
        byte[] bytes = mac.doFinal(source.getBytes());
        return Hex.encodeHexString(bytes);
    }

    /**
     * hmacSha1算法
     *
     * @param source 源字符串
     * @return 目标字符串
     */
    public static String hmacSha256(String source, String secret) {
        Mac mac = HmacUtils.getInitializedMac(HmacAlgorithms.HMAC_SHA_256, secret.getBytes());
        byte[] bytes = mac.doFinal(source.getBytes());
        return Hex.encodeHexString(bytes);
    }
}
