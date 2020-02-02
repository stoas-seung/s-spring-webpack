package ift.bridge.docker.extract;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seung.commons.arguments.SRequestMap;

@Controller
public class SExtractC {

	private static final Logger logger = LoggerFactory.getLogger(SExtractC.class);
	
	@Resource(name="sExtractS")
	private SExtractS sExtractS;
	
	@RequestMapping(value = {"/rest/hometax/Z0001"}, method = {RequestMethod.POST})
	public String restHometaxZ0001(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SExtactC", "ext");
		
		model.addAttribute("res", sExtractS.restHometaxZ0001(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/ext"}, method = {RequestMethod.POST})
	public String ext(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SExtactC", "ext");
		
		model.addAttribute("res", sExtractS.extract(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/extract/single"}, method = {RequestMethod.POST})
	public String restExtractSingle(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SExtactC", "ext");
		
		model.addAttribute("res", sExtractS.extract(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/extract/multi"}, method = {RequestMethod.POST})
	public String restExtractMulti(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SExtactC", "ext");
		
		model.addAttribute("res", sExtractS.extract(sRequestMap));
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/rest/ext/demo"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String demo(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SExtactC", "demo");
		
		model.addAttribute("res", sExtractS.demo(sRequestMap));
		
		return "/ext/demo";
	}
	
	@RequestMapping(value = {"/view/guide"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String guide(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		logger.debug("{}.{}", "SExtactC", "guide");
		return "/guide";
	}
	
}
