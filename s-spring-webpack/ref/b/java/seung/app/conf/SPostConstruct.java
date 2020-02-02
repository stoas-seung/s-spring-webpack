package seung.app.conf;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import seung.app.conf.dao.SDao;
import seung.commons.arguments.SMap;

@Component
public class SPostConstruct {

	private static final Logger logger = LoggerFactory.getLogger(SPostConstruct.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="iftCode")
	private SMap iftCode;
	
	@Resource(name="sDao")
	private SDao sDao;
	
	@PostConstruct
	public void init() {
		
		try {
			
			logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(configProperties));
			
//			ObjectMapper objectMapper = new ObjectMapper();
			try {
				
//				logger.error("{}: {}", "ds01.version", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sDao.ds01SL("versionOracle")));
//				logger.error("{}: {}", "ds02.version", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sDao.ds02SL("versionOracle")));
				
				for(SMap iftCodeSR : sDao.ds01SL("iftCodeSL")) {
					iftCode.put(iftCodeSR.getString("CODE_K"), iftCodeSR.getString("CODE_V"));
				}
				
//				logger.error(iftCode.toJsonString(true));
				
			} catch (JsonProcessingException e) {
				logger.error("" + e);
			} catch (Exception e) {
				logger.error("" + e);
			}
			
		} catch (JsonProcessingException e) {
			logger.error("" + e);
		}
		
	}
	
}
