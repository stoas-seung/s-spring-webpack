package ift.bridge.docker.test;

import seung.commons.arguments.SMap;

public interface STestS {

	/**
	 * @desc redis:6379> ping
	 * @param query
	 * @return
	 */
	SMap ping(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> keys *
	 * @param query
	 * @return
	 */
	SMap keys(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> del key
	 * @param query
	 * @return
	 */
	SMap del(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> zadd key value score
	 * @param query
	 * @return
	 */
	SMap zadd(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> lrange array 0 -1
	 * @desc redis:6379> lrange array start stop
	 * @param query
	 * @return
	 */
	SMap zrange(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> hset key field value
	 * @param query
	 * @return
	 * @throws Exception
	 */
	SMap hset(SMap query) throws Exception;
	
	/**
	 * @desc redis:6379> hgetall key
	 * @desc redis:6379> hget key field
	 * @param query
	 * @return
	 * @throws Exception
	 */
	SMap hget(SMap query) throws Exception;
	
}
