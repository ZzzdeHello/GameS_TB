package com.zzzde.game.springboot.my.redis.config;


import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author zzzde
 * @version 1.0
 * @date 2023/4/26 16:08
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    // 自定义缓存key生成策略
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
                StringBuffer sb = new StringBuffer();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    // 缓存管理器：缓存管理器是用来管理（检索）一类缓存的。通常来讲，缓存管理器是与缓存组件类型相关联的。
    // 我们知道，spring 缓存抽象的目的是为使用不同缓存组件类型提供统一的访问接口，以向开发者屏蔽各种缓存组件的差异性

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory, CacheProperties cacheProperties) {

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return new RedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(factory), config);
    }

    //    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//        // 设置缓存的默认过期时间，也是使用Duration设置
//        config = config.entryTtl(Duration.ofMinutes(5))
//                .disableCachingNullValues();
//        // 使用自定义的缓存配置初始化一个cacheManager
//        return RedisCacheManager
//                .builder(redisConnectionFactory)
//                .cacheDefaults(config)
//                .transactionAware()
//                .build();
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // key采用String的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // value序列化方式采用java
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        // hash的value序列化方式采用java
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }
}
