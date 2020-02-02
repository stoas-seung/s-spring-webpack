package seung.app.conf;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ift.bridge.docker.redis.SRedisU;

@Component
public class SPostConstruct {

	private static final Logger logger = LoggerFactory.getLogger(SPostConstruct.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="sRedisU")
	private SRedisU sRedisU;
	
	@PostConstruct
	public void init() {
		
		try {
			logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configProperties));
			sRedisU.init();
		} catch (JsonProcessingException e) {
			logger.error("" + e);
		}
		
	}
	
}
