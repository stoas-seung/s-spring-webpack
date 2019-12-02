package s.spring.webpack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import seung.commons.arguments.SRequestMap;

@Controller
public class SSpringWebpackC {

	private static final Logger logger = LoggerFactory.getLogger(SSpringWebpackC.class);
	
	@RequestMapping(value = {"/"}, method = {RequestMethod.GET,RequestMethod.POST})
	public String index(
			Model model
			, SRequestMap sRequestMap
			) throws Exception {
		
		logger.debug("{}.{}", "SSpringWebpackC", "root");
		
		return "/index";
	}
	
}
