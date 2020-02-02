package ift.bridge.docker.redis;

import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import seung.commons.arguments.SMap;

@Component("sRedisD")
public class SRedisD {

	@SuppressWarnings("rawtypes")
	@Resource(name="redisTemplate")
	private RedisTemplate redisTemplate;
	
	public String ping() {
		return redisTemplate.getConnectionFactory().getConnection().ping();
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}
	
	@SuppressWarnings("unchecked")
	public DataType type(String key) {
		return redisTemplate.type(key);
	}
	
	@SuppressWarnings("unchecked")
	public SMap data(String pattern) {
		SMap data = new SMap();
		for(String key : keys(pattern)) {
			switch(redisTemplate.type(key)) {
			case STRING:
				data.put(key, redisTemplate.opsForValue().get(key));
				break;
			case SET:
				data.put(key, redisTemplate.opsForSet().members(key));
				break;
			case LIST:
				data.put(key, redisTemplate.opsForList().range(key, 0, -1));
				break;
			case HASH:
				data.put(key, redisTemplate.opsForHash().entries(key));
				break;
			case ZSET:
				data.put(key, redisTemplate.opsForZSet().range(key, 0, -1));
				break;
			default:
				break;
		}
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public Boolean del(String key) {
		return redisTemplate.delete(key);
	}
	
	@SuppressWarnings("unchecked")
	public int scard(String key) {
		return redisTemplate.opsForSet().size(key).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> smembers(String key) {
		ArrayList<String> smembers = null;
		if(redisTemplate.hasKey(key) && redisTemplate.opsForSet().size(key).intValue() > 0) {
			smembers = new ArrayList<>(redisTemplate.opsForSet().members(key));
		} else {
			smembers = new ArrayList<String>();
		}
		return smembers;
	}
	
	@SuppressWarnings("unchecked")
	public int sadd(String key, String values) {
		return redisTemplate.opsForSet().add(key, values).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public int srem(String key, String values) {
		if(redisTemplate.hasKey(key) && redisTemplate.opsForSet().size(key).intValue() > 0) {
			return redisTemplate.opsForSet().remove(key, values).intValue();
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public String spop(String key) {
		if(redisTemplate.hasKey(key) && redisTemplate.opsForSet().size(key).intValue() > 0) {
			return (String) redisTemplate.opsForSet().pop(key);
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public int llen(String key) {
		return redisTemplate.opsForList().size(key).intValue();
	}
	
	public ArrayList<String> lrange(String key) {
		return lrange(key, 0, -1);
	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> lrange(String key, int start, int end) {
		if(redisTemplate.hasKey(key) && redisTemplate.opsForList().size(key).intValue() > 0) {
			return (ArrayList<String>) redisTemplate.opsForList().range(key, start, end);
		}
		return new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	public int lpush(String key, String value) {
		return redisTemplate.opsForList().leftPush(key, value).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public int rpush(String key, String value) {
		return redisTemplate.opsForList().rightPush(key, value).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public int lrem(String key, int count, String value) {
		return redisTemplate.opsForList().remove(key, count, value).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public String lpop(String key) {
		return (String) redisTemplate.opsForList().leftPop(key);
	}
	
}
