package xyz.vimtool.commons;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * URL处理工具类
 *
 * @author    qinxiaoqing
 * @date      2017/06/17
 * @version   1.0
 */
public class RequestUtils {

    /**
     * URL编码
     */
    public static String encodeURL(String url) {
        return encodeURL(url, "UTF-8");
    }

    /**
     * URL编码
     */
    public static String encodeURL(String url, String charset) {
        try {
            return URLEncoder.encode(url, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decodeURL(String url) {
        return decodeURL(url, "UTF-8");
    }

    public static String decodeURL(String url, String charset) {
        try {
            return URLDecoder.decode(url, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 追加参数
     *
     * @param url    url
     * @param key    参数名
     * @param value  参数值
     *
     * @return 追加参数的url
     */
    public static String appendParam(String url, String key, String value) {
        return appendParam(url, key + "=" + value);
    }

    /**
     * 追加参数
     *
     * @param url     url
     * @param params  参数
     *
     * @return 追加参数的url
     */
    public static String appendParam(String url, Map<String, Object> params) {
        return appendParam(url, CollectionUtils.mapToString(params));
    }

    /**
     * 追加参数
     *
     * @param url     url
     * @param params  参数
     *
     * @return 追加参数的url
     */
    public static String appendParam(String url, String params) {
        if (StringUtils.isEmpty(params)) {
            return url;
        }

        if (!url.contains("?")) {
            url += "?";
        }

        if (url.endsWith("?")) {
            url += params;
        } else {
            url += "&" + params;
        }
        return url;
    }

    /**
     * 获取请求信息
     *
     * @param request  请求
     *
     * @return 请求信息
     */
    public static String getInfo(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("URL = " + request.getRequestURL() + ", METHOD = " + request.getMethod());
        builder.append("PARAM = " + getParam(request) + ", BODY = " + getBody(request));
        return builder.toString();
    }

    /**
     * 获取请求参数（?后面的字符串）
     *
     * @param request  请求
     *
     * @return 请求参数
     */
    public static String getParam(HttpServletRequest request) {
        String param = request.getQueryString();
        if (Base64.isBase64(param)) {
            param = new String(Base64.decodeBase64(param), StandardCharsets.UTF_8);
        }
        return param;
    }

    /**
     * 获取请求体
     *
     * @param request  请求
     *
     * @return 请求体
     */
    public static String getBody(HttpServletRequest request) {
        try {
            String body = StreamUtils.loadText(request.getInputStream(), "UTF-8");
            if (Base64.isBase64(body)) {
                return new String(Base64.decodeBase64(body), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址
     */
    public String getRemoteIP(HttpServletRequest request) {
        String[] keys = {"X-Forwarded-For", "WL-Proxy-Client-IP",
                "Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

        for (int i = 0; i < keys.length; i++) {
            String ip = request.getHeader(keys[i]);
            if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
                int index = ip.indexOf(",");
                if (index != -1) {
                    return ip.substring(0, index);
                }

                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
