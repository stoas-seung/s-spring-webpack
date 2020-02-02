package ift.bridge.docker.redis;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seung.commons.arguments.SRequestMap;

@Controller
public class SRedisC {

	private static final Logger logger = LoggerFactory.getLogger(SRedisC.class);
	
	@Resource(name="sRedisS")
	private SRedisS sRedisS;
	
	@RequestMapping(value = {"/rest/redis/help"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String help(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/help");
		
		model.addAttribute("res", sRedisS.help(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/workerUR"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String workerUR(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/workerUR");
		
		model.addAttribute("res", sRedisS.workerUR(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/redisSL"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String redisSL(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/redisSL");
		
		model.addAttribute("res", sRedisS.redisSL(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/workerSL"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String workerSL(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/workerSL");
		
		model.addAttribute("res", sRedisS.workerSL(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/refresh"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String refresh(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/refresh");
		
		model.addAttribute("res", sRedisS.refresh(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/stop"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String stop(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/stop");
		
		model.addAttribute("res", sRedisS.stop(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/redis/wait"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String wait(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SRedisC", "/rest/redis/wait");
		
		model.addAttribute("res", sRedisS.wait(sRequestMap));
		
		return "jsonView";
	}
	
}
