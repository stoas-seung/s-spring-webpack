package seung.app;

import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seung.commons.arguments.SRequestMap;

@Controller
public class SAppC {

	private static final Logger logger = LoggerFactory.getLogger(SAppC.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@RequestMapping(value = {"/reflect"}, method = {RequestMethod.GET})
	public String reflect(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SAppC", "appProps");
		
		model.addAttribute("res", sRequestMap);
		
		return "jsonView";
	}
	
	@RequestMapping(value = {"/system/config"}, method = {RequestMethod.GET})
	public String config(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SAppC", "config");
		
		model.addAttribute("res", configProperties);
		
		return "jsonView";
	}
	
}
