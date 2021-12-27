package xyz.vimtool.http;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    public static void main(String[] args) throws Exception {
//        File file = new File("/Users/xiao/Pictures/timg.jpeg");
//        byte[] content = FileUtils.readFileToByteArray(file);
//        String url = "http://oss.xfinfr.com/11W2MYCO/rescloud1/test.jpg";
//        String md5 = DigestUtils.md5Hex(content);
//        HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-MD5", md5);
//        HttpEntity<byte[]> entity = new HttpEntity<>(content, headers);
//        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
//        System.out.println(exchange.getBody());

        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Future<String> future = executorService.submit(() -> {
//                return restTemplate.postForObject("http://127.0.0.1:8889/api/app/record/wine/notify/ada", null, String.class);
                return restTemplate.postForObject("http://127.0.0.1:8083/start/thread", null, String.class);
            });
            futures.add(future);
        }

        while (CollectionUtils.isNotEmpty(futures)) {
            Iterator<Future<String>> iterator = futures.iterator();
            while (iterator.hasNext()) {
                Future<String> next = iterator.next();
                if (next.isDone()) {
                    iterator.remove();
                    System.out.println(next.get());
                }
            }
        }
        executorService.shutdownNow();
        System.out.println("over");

//        for (int i = 755369; i <= 999999; i++) {
//            if (i % 30 == 0) {
//                TimeUnit.SECONDS.sleep(60);
//            }
//            String s = String.format("%06d", i);
//            String result = null;
//            try {
//                result = restTemplate.getForObject("https://www.meipian.cn/app/http/transpwd.php?id=300g6mel&pwd=" + s, String.class);
//            } catch (RestClientException e) {
//                System.out.println(s + "  " + e.getMessage());
//            }
//
//            if (Objects.isNull(result)) {
//                continue;
//            }
//            if (!result.contains("访问失败")) {
//                System.out.println(result);
//                System.out.println("查找成功，" + s);
//                break;
//            }
//            System.out.println(s);
//            TimeUnit.MILLISECONDS.sleep(100);
//        }
    }

//    public static void main(String[] args) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
//        multiValueMap.add("photo", new FileSystemResource("/Users/xiao/Desktop/kjk.jpg"));
//        multiValueMap.add("productNo", "123rdafae134");
//        multiValueMap.add("channelNo", "123rafae134");
//        multiValueMap.add("traceId", "123re1adad34");
//        multiValueMap.add("resId", "23");
//        multiValueMap.add("operatorName", "123dadadre134");
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(multiValueMap, headers);
//        ResponseEntity<JSONObject> exchange = restTemplate.exchange("http://127.0.0.1:8080/bizinterface_innerservice/worksmanage/addaivideo",
//                HttpMethod.POST, entity, JSONObject.class);
//        System.out.printf("exchange");
//    }

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
