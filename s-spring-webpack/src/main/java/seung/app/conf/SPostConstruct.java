package seung.app.conf;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import seung.commons.SHttpsU;
import seung.commons.http.SHttpVO;

@Component
public class SPostConstruct {

	private static final Logger logger = LoggerFactory.getLogger(SPostConstruct.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@PostConstruct
	public void init() throws Exception {
		try {
			logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configProperties));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("" + e);
		}
	}
	
}
