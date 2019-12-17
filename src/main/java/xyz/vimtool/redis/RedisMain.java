package xyz.vimtool.redis;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 
 *
 * @author  zhangzheng
 * @version 1.0.0
 * @date    2019/11/14
 */
public class RedisMain {

    public static void main(String[] args) {
        RedisTemplate<String, Object> redisTemplate = redisTemplate(lettuceFactory());
        String redisKey = "pipeline:test";
        byte[] redisKeyBytes = redisKey.getBytes();

        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            nodes.add(new Node());
        }
        redisTemplate.opsForList().rightPushAll(redisKey, nodes.toArray(new Node[0]));

        List<Object> nodes1 = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Node execute(RedisOperations operations) throws DataAccessException {
                ListOperations ops = operations.opsForList();
                for (int i = 0; i < 50; i++) {
                    ops.leftPop(redisKey);
                }
                return null;
            }
        });

        List<Object> nodes2 = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            connection.openPipeline();
            for (int i = 0; i < 50; i++) {
                connection.listCommands().lPop(redisKeyBytes);
            }
            return null;
        });
        System.out.println(nodes1);
        System.out.println(nodes2);
    }

    @Data
    public static class Node {

        private String uuid = UUID.randomUUID().toString();

        private Long timestamp = System.currentTimeMillis();
    }

    /**
     * redis模板操作类，覆盖默认模版类
     *
     * @param factory redis连接工厂（Lettuce/Jedis），这里使用Lettuce
     * @return RedisTemplate
     */
    private static RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 使用StringRedis序列化
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        // 使用fastjson序列化
//        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();

        redisTemplate.setConnectionFactory(factory);

        // key的序列化采用StringRedisSerializer
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);

        // value值的序列化采用fastJsonRedisSerializer
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private static RedisConnectionFactory jedisFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(0);
        config.setHostName("127.0.0.1");
        config.setPort(6397);
        config.setPassword("passwd_redis");
        return new JedisConnectionFactory(config);
    }

    private static RedisConnectionFactory lettuceFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setDatabase(0);
        config.setHostName("127.0.0.1");
        config.setPort(6397);
        config.setPassword("passwd_redis");
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, getLettucePool());
        factory.afterPropertiesSet();
        return factory;
    }

    public static LettucePoolingClientConfiguration getLettucePool() {
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
//        builder.commandTimeout(Duration.ofSeconds(15));
        builder.poolConfig(getRedisConfig());
//        builder.shutdownTimeout(Duration.ZERO);
        LettucePoolingClientConfiguration pool = builder.build();
        return pool;
    }

    public static GenericObjectPoolConfig getRedisConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(8);
        genericObjectPoolConfig.setMaxTotal(16);
        genericObjectPoolConfig.setMinIdle(8);
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        genericObjectPoolConfig.setBlockWhenExhausted(true);
        genericObjectPoolConfig.setMaxWaitMillis(10000);
        //在borrow一个实例时，是否提前进行alidate操作；如果为true，则得到的实例均是可用的
        genericObjectPoolConfig.setTestOnBorrow(true);
        //调用returnObject方法时，是否进行有效检查
        genericObjectPoolConfig.setTestOnReturn(true);
        //在空闲时检查有效性, 默认false
        genericObjectPoolConfig.setTestWhileIdle(true);
        //表示idle object evitor两次扫描之间要sleep的毫秒数；
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        //表示一个对象至少停留在idle状态的最短时间，
        //然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(10000);
        return genericObjectPoolConfig;
    }
}
