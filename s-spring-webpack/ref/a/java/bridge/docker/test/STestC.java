package ift.bridge.docker.test;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seung.commons.arguments.SRequestMap;

@Controller
public class STestC {

	private static final Logger logger = LoggerFactory.getLogger(STestC.class);
	
	@Resource(name="sTestS")
	private STestS sTestS;
	
	@RequestMapping(value = {"/test/stress"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String stress(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "ping");
		
		Thread.sleep(1000 * 10);
		model.addAttribute("res", sRequestMap);
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/ping"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String ping(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "ping");
		
		model.addAttribute("res", sTestS.ping(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/keys"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String keys(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "keys");
		
		model.addAttribute("res", sTestS.keys(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/del"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String delAll(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "delAll");
		
		sRequestMap.putQuery("key", "");
		
		model.addAttribute("res", sTestS.del(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/del/{key}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String del(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "del");
		
		sRequestMap.putQuery("key", key);
		
		model.addAttribute("res", sTestS.del(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/zadd/{key}/{value}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String add(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			, @PathVariable("value") String value
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "add");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("value", value);
		
		model.addAttribute("res", sTestS.zadd(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/zrange/{key}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String zrangeAll(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "zrangeAll");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("start", "0");
		sRequestMap.putQuery("stop" , "-1");
		
		model.addAttribute("res", sTestS.zrange(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/zrange/{key}/{start}/{stop}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String zrange(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			, @PathVariable("start") String start
			, @PathVariable("stop")  String stop
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "zrange");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("start", start);
		sRequestMap.putQuery("stop" , stop);
		
		model.addAttribute("res", sTestS.zrange(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/hset/{key}/{field}/{value}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String hset(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			, @PathVariable("field") String field
			, @PathVariable("value") String value
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "hset");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("field", field);
		sRequestMap.putQuery("value", value);
		
		model.addAttribute("res", sTestS.hset(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/redis/hget/{key}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String hgetAll(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "hgetAll");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("field", "");
		
		model.addAttribute("res", sTestS.hget(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/test/test/redis/hget/{key}/{field}"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String hget(
			Model model
			, SRequestMap sRequestMap
			, @PathVariable("key")   String key
			, @PathVariable("field") String field
			) throws Exception {
		
		logger.debug("{}.{}", "STestC", "hget");
		
		sRequestMap.putQuery("key"  , key);
		sRequestMap.putQuery("field", field);
		
		model.addAttribute("res", sTestS.hget(sRequestMap.getQuery()));
		
		return "jsonView";
	}
	
}
