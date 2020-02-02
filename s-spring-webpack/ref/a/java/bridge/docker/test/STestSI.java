package ift.bridge.docker.test;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import seung.commons.arguments.SMap;

@Service("sTestS")
public class STestSI implements STestS {

	private static final Logger logger = LoggerFactory.getLogger(STestSI.class);
	
	@SuppressWarnings("rawtypes")
	@Resource(name="redisTemplate")
	private RedisTemplate redisTemplate;
	
	@Override
	public SMap ping(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "ping");
		
		SMap res = new SMap();
		res.put("query", query);
		
		try {
			res.put("ping", redisTemplate.getConnectionFactory().getConnection().ping());
		} catch (Exception e) {
			res.put("ping", "" + e);
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap keys(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "keys");
		
		SMap res = new SMap();
		res.put("query", query);
		
		res.put("keys", redisTemplate.keys("*"));
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap del(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "del");
		
		SMap res = new SMap();
		res.put("query", query);
		
		if(query.getString("key").length() == 0) {
			res.put("del", redisTemplate.delete(redisTemplate.keys("*")));
		} else {
			res.put("del", redisTemplate.delete(query.getString("key")));
		}
		
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SMap zadd(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "zadd");
		
		SMap res = new SMap();
		res.put("query", query);
		
		res.put("add", redisTemplate.opsForZSet().add(query.getString("key"), query.getString("value"), 0));
		
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SMap zrange(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "zrange");
		
		SMap res = new SMap();
		res.put("query", query);
		
		res.put("zrange", redisTemplate.opsForZSet().range(query.getString("key"), query.getLong("start"), query.getLong("stop")));
		
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SMap hset(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "hset");
		
		SMap res = new SMap();
		res.put("query", query);
		
		redisTemplate.opsForHash().put(query.getString("key"), query.getString("field"), query.getString("value"));
		res.put("hset", redisTemplate.opsForHash().size(query.getString("key")));
		
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SMap hget(SMap query) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "hget");
		
		SMap res = new SMap();
		res.put("query", query);
		
		if(query.getString("field").length() == 0) {
			res.put("hget", redisTemplate.opsForHash().entries(query.get("key")));
		} else {
			res.put("hget", redisTemplate.opsForHash().get(query.getString("key"), query.getString("field")));
		}
		
		return res;
	}
	
}
