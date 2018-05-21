package xyz.vimtool.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * mqtt服务端
 *
 * @author zhangzheng
 * @version 1.0.0
 * @date 2018/5/21
 * @since jdk1.8
 */
public class Server {

    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    public static final String HOST = "tcp://127.0.0.1:61613";

    //定义一个主题
    public static final String TOPIC = "topic11";

    //定义MQTT的ID，可以在MQTT服务配置中指定
    private static final String clientId = "server11";

    private MqttClient client;
    private MqttTopic topic11;
    private String userName = "admin";
    private String passWord = "password";

    private MqttMessage message;

    /**
     * 构造函数
     *
     * @throws MqttException
     */
    public Server() throws MqttException {
        // MemoryPersistence设置clientId的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientId, new MemoryPersistence());
        connect();

        // 订阅消息
        int[] Qos  = {1};
        String[] topic1 = {TOPIC};
        client.subscribe(topic1, Qos);
    }

    /**
     * 用来连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());

        // 设置超时时间
        options.setConnectionTimeout(10);

        // 设置会话心跳时间
        options.setKeepAliveInterval(20);

        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic11 = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param topic
     * @param message
     * @throws MqttPersistenceException
     * @throws MqttException
     */
    public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! " + token.isComplete());
    }

    /**
     * 启动入口
     *
     * @param args
     * @throws MqttException
     */
    public static void main(String[] args) throws MqttException {
        Server server = new Server();

        server.message = new MqttMessage();
        server.message.setQos(1);
        server.message.setRetained(true);
        server.message.setPayload("hello,topic11".getBytes());
        server.publish(server.topic11, server.message);
        System.out.println("ratained状态:" + server.message.isRetained());
    }
}
