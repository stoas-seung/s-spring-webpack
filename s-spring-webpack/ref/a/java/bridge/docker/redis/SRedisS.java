package ift.bridge.docker.redis;

import seung.commons.arguments.SMap;
import seung.commons.arguments.SRequestMap;

public interface SRedisS {

	/**
	 * @desc used keys and decription
	 * @param query
	 * @return
	 */
	SMap help(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc update worker information update
	 * @param query
	 * @return
	 */
	SMap workerUR(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc redis data
	 * @param query
	 * @return
	 */
	SMap redisSL(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc select server list - hget workerL keys
	 * @param query
	 * @return
	 */
	SMap workerSL(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc refresh server list
	 * @param query
	 * @return
	 */
	SMap refresh(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc move to stop list
	 * @param query
	 * @return
	 */
	SMap stop(SRequestMap sRequestMap) throws Exception;
	
	/**
	 * @desc move to stop list
	 * @param query
	 * @return
	 */
	SMap wait(SRequestMap sRequestMap) throws Exception;
	
}
