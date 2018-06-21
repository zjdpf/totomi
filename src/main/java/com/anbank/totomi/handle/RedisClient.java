package com.anbank.totomi.handle;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {
	private Jedis jedis;						//非切片额客户端连接
    private JedisPool jedisPool;				//非切片连接池

    
    public RedisClient() { 
    	jedisPool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);
        jedis = jedisPool.getResource(); 
    }
    
    public String get(String key) {
    	return jedis.get(key);
    }
    
    public void set(String key, String value) {
    	jedis.set(key, value);
    }
    
    public void forceClean() {
    	jedis.flushDB();
    }
    
    // main for test
    public static void main(String[] args) {
    	RedisClient client = new RedisClient();
    	client.forceClean();
    }
}
