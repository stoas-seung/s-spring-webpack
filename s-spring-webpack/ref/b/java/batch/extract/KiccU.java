package ift.batch.kicc.extract;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import ift.batch.kicc.extract.http.SHttpU;
import ift.batch.kicc.extract.http.SHttpVO;
import seung.commons.SCommonU;
import seung.commons.arguments.SMap;

@Component("kiccU")
public class KiccU {

	private static final Logger logger = LoggerFactory.getLogger(KiccU.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	public SHttpVO kiccAPI(String loginId, String orgCd, String svcCd, String finCd) {
		
		SHttpVO sHttpVO = new SHttpVO();
		
		if(!"true".equals(configProperties.getProperty("config.kicc.api.enabled", "false"))) {
			return sHttpVO;
		}
		
		try {
			
			sHttpVO.setConnectionTimeout(1000 * 3);
			sHttpVO.setReadTimeout(1000 * 10);
			
//			sHttpVO.setUrl("http://testwww.easyshop.co.kr/app/api/iftBatch.kicc");
//			sHttpVO.setUrl("https://www.easyshop.co.kr/app/api/iftBatch.kicc");
			sHttpVO.setUrl(configProperties.getProperty("config.kicc.api.url", "http://testwww.easyshop.co.kr/app/api/iftBatch.kicc"));
			sHttpVO.setMethod("POST");
			sHttpVO.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			sHttpVO.setRequestEncoding("UTF-8");
			sHttpVO.setResponseEncoding("UTF-8");
			
			HashMap<String, String> header = new HashMap<String, String>();
			switch(orgCd + svcCd) {
			case "hometaxZ3001":
				header.put("api_nm", "saleTaxInvc");
				break;
			case "hometaxZ3002":
				header.put("api_nm", "buyTaxInvc");
				break;
			case "hometaxZ3003":
				header.put("api_nm", "saleNotaxInvc");
				break;
			case "hometaxZ3004":
				header.put("api_nm", "buyNotaxInvc");
				break;
			case "hometaxZ4001":
				header.put("api_nm", "saleCash");
				break;
			case "hometaxZ4002":
				header.put("api_nm", "buyCash");
				break;
			case "pbkB0002":
			case "cbkB0002":
			case "sbkB0002":
				header.put("api_nm", "bankAcnt");
				break;
			case "ccdC0005":
			case "pcdC0005":
				header.put("api_nm", "buyCard");
				break;
			case "cardsalesB0011":
			case "cardsalesB0021":
				header.put("api_nm", "saleCard");
				break;
			default:
				break;
			}
			header.put("api_inst", "ES");
			HashMap<String, String> body = new HashMap<String, String>();
			body.put("loginId", loginId);
			if(finCd.length() > 0) {
				logger.debug("finCd: {}", finCd);
				body.put("finCd", finCd);
			}
			SMap requestBody = new SMap();
			requestBody.put("Header", header);
			requestBody.put("Body", body);
			sHttpVO.setRequestBody(SCommonU.encodeURI(requestBody.toJsonString()).getBytes());
			
			new SHttpU().request(sHttpVO);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("" + e);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			logger.error("" + e);
		} finally {
			logger.debug("sHttpVO: {}", sHttpVO.toString());
			try {
				if(sHttpVO.getResponseBody() != null) {
					logger.debug("responseBody: {}", new String(sHttpVO.getResponseBody()));
				}
			} catch (Exception e) {
			}
		}
		
		return sHttpVO;
	}
	
}
