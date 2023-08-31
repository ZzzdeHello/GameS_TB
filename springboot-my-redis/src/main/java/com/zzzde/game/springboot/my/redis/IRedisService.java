package com.zzzde.game.springboot.my.redis;

import java.util.List;
import java.util.Map;

/**
 * @author zzzde
 * @version 1.0
 * @date 2023/5/20 15:50
 */
public interface IRedisService {

    boolean setExpire(String key, long time);

    long getExpire(String key);

    boolean hasKey(String key);

    Object get(String key);

    boolean set(String key, Object value);

    boolean set(String key, Object value, Long time);

    boolean listRightPush(String key, Object... values);

    List<?> rangeList(String key);

    boolean hmset(String key, Map<String,Object> map);

    boolean hmset(String key,Map<String,Object> map, long time);

    boolean hset(String key,String field, Object value);

    boolean hset(String key,String field,Object value, long time);

    Object hget(String key,String field);

    Map<Object,Object> hmget(String key);

}
