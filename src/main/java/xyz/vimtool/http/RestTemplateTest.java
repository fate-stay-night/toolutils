package xyz.vimtool.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/8/28
 */
public class RestTemplateTest {

    private static final RestTemplate restTemplate = new RestTemplate(createFactory());

    public static String exchange(byte[] content, String filename) {
//		String reqUrl = MessageFormat.format("{0}/{1}/{2}/{3}",
//				FileConstants.URL, FileConstants.APPID, FileConstants.POOL, filename);
//		String md5 = Md5Util.md5Hex(content);
//
        HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-MD5", md5);

        String reqUrl = "http://localhost:8081/api_war/movie/submitmovie";

//		ByteArrayResource contentsAsResource = new ByteArrayResource(content);
        HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);

        ResponseEntity<String> exchange = restTemplate.exchange(reqUrl, HttpMethod.PUT, entity, String.class);
        return exchange.getBody();
    }

    public static void main(String[] args) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("photo", new FileSystemResource("/Users/xiao/Desktop/kjk.jpg"));
        multiValueMap.add("productNo", "123rdafae134");
        multiValueMap.add("channelNo", "123rafae134");
        multiValueMap.add("traceId", "123re1adad34");
        multiValueMap.add("resId", "23");
        multiValueMap.add("operatorName", "123dadadre134");
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(multiValueMap, headers);
        ResponseEntity<JSONObject> exchange = restTemplate.exchange("http://127.0.0.1:8080/bizinterface_innerservice/worksmanage/addaivideo",
                HttpMethod.POST, entity, JSONObject.class);
        System.out.printf("exchange");
    }

    private static ClientHttpRequestFactory createFactory() {
        HttpClient httpClient = httpClient();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        factory.setConnectTimeout(50000);
        factory.setReadTimeout(50000);
        return factory;
    }

    private static HttpClient httpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // 设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(200);
        // 路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(100);
        // 服务器返回数据(response)的时间，超过该时间抛出read timeout
        // 连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
        // 从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(1000)
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build();
    }

    public static void download(String url, String localPath) {
        try {
            // 打开连接
            URLConnection connection = new URL(url).openConnection();

            connection.setConnectTimeout(30*1000);
            connection.setReadTimeout(30*1000);

            // 输入流
            InputStream in = connection.getInputStream();

            // 输出的文件流
            OutputStream out = new FileOutputStream(localPath);

            try {
                // 1K的数据缓冲
                byte[] bs = new byte[1024];

                // 开始读取
                int len;
                while ((len = in.read(bs)) != -1) {
                    out.write(bs, 0, len);
                }
            } finally {
                // 完毕，关闭所有链接
                out.close();
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
