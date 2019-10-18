package xyz.vimtool.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

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
        //设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(200);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(100);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setConnectTimeout(5000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectionRequestTimeout(1000)//从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .build();
    }
}
