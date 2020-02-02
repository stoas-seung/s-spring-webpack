package ift.bridge.docker.extract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import ift.bridge.docker.redis.SRedisU;
import seung.commons.SCommonU;
import seung.commons.SHttpU;
import seung.commons.SHttpsU;
import seung.commons.arguments.SMap;
import seung.commons.arguments.SRequestMap;
import seung.commons.http.SHttpVO;

@Service("sExtractS")
public class SExtractSI implements SExtractS {

	private static final Logger logger = LoggerFactory.getLogger(SExtractSI.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="sRedisU")
	private SRedisU sRedisU;
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap restHometaxZ0001(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SExtractSI", "restHometaxZ0001");
		
		SMap res = new SMap();
		res.put("bridgeAppVer", configProperties.getProperty("info.app.ver"));
		res.put("bridgeOsNm"  , configProperties.getProperty("os.name"));
		res.put("bridgeHostNm", sRequestMap.getNetwork().getString("hostName"));
		res.put("resCd"       , "");
		res.put("resMsg"      , "");
		res.put("bridgeReqDt" , SCommonU.getDateString("yyyyMMddHHmmssSSS"));
		
		SHttpVO sHttpVO = new SHttpVO();
		
		sHttpVO.setReadTimeout(1000 * 60 * 60);
		sHttpVO.setRequestUrl("http://api.infotech3.co.kr/hometax/Z0001");
		sHttpVO.setRequestParameter("ch"  , sRequestMap.getQuery().getString("ch"));
		sHttpVO.setRequestParameter("usr" , sRequestMap.getQuery().getString("usr"));
		sHttpVO.setRequestParameter("inq" , sRequestMap.getQuery().getString("inq"));
		sHttpVO.setRequestParameter("type", sRequestMap.getQuery().getString("type"));
		sHttpVO.setRequestParameter("no"  , sRequestMap.getQuery().getString("no"));
		
		SHttpU.request(sHttpVO);
		
		String responseText = "";
		if(sHttpVO.getResponseCode() == 200) {
			
			responseText = new String(sHttpVO.getResponse());
			res.putAll(new ObjectMapper().readValue(responseText, Map.class));
			
			SHttpVO httpCSV = new SHttpVO();
			httpCSV.setRequestUrl(res.getString("url"));
			
			int maxRequest = 10;
			int currentReq = 0;
			while(true) {
				
				Thread.sleep(1000);
				
				SHttpU.request(httpCSV);
				
				if(httpCSV.getResponseCode() == 200) {
					responseText = new String(httpCSV.getResponse());
					if(responseText != null && responseText.indexOf("END") > -1) {
						res.put("csv", responseText);
						break;
					}
				}
				
				if(++currentReq >= maxRequest) {
					break;
				}
				
			}
			
			res.put("resCd", "0000");
			
		} else {
			logger.debug("{} - {}", "SHttpVO", sHttpVO.toString());
			res.put("resCd" , "V903");
			res.put("resMsg", sHttpVO.getExceptionMessage());
		}
		
		res.put("bridgeResDt", SCommonU.getDateString("yyyyMMddHHmmssSSS"));
		return res;
	}
	
	@SuppressWarnings("unchecked")
	public SMap extract(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SExtractSI", "ext");
		
		SMap       res         = new SMap();
		String     reqCd       = sRequestMap.getQuery().getString("reqCd");
		List<SMap> inJsonList  = new ArrayList<SMap>();
		List<SMap> outJsonList = new ArrayList<SMap>();
		String     outJsonText = "";
		
		String       redisKey     = "";
		String       worker       = "";
		Set<String>  accounts     = new HashSet<String>();
		boolean      hasAuth      = false;
		ObjectMapper objectMapper = new ObjectMapper();
		
		res.put("bridgeAppVer", configProperties.getProperty("info.app.ver"));
		res.put("bridgeOsNm"  , configProperties.getProperty("os.name"));
		res.put("bridgeHostNm", sRequestMap.getNetwork().getString("hostName"));
		res.put("resCd"       , "");
		res.put("resMsg"      , "");
		res.put("outJsonList" , outJsonList);
		res.put("bridgeReqDt" , SCommonU.getDateString("yyyyMMddHHmmssSSS"));
		
		// json string validation
		try {
			new ObjectMapper().readTree(sRequestMap.getQuery().getString("inJsonList"));
		} catch (Exception e) {
			logger.error("" + e);
			res.put("resCd", "V001");
			res.put("resMsg", "" + e);
			return res;
		}
		
		try {
			
			inJsonList = objectMapper.readValue(sRequestMap.getQuery().getString("inJsonList"), objectMapper.getTypeFactory().constructCollectionLikeType(List.class, SMap.class));
			logger.debug("inJsonList = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(inJsonList));
			
			// list up account hash
			String authHash = "";
			for(SMap inJson : inJsonList) {
				// appCd validation
				if("".equals(inJson.getString("appCd"))) {
					res.put("resCd", "V002");
					throw new Exception("Field [appCd] can not be null or empty.");
				}
				authHash = sRedisU.getAuthHash(inJson);
				if(authHash.length() > 0) {
					accounts.add(authHash);
				}
			}
			logger.debug("accounts = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(accounts));
			
			// check auth
			hasAuth = sRedisU.hasAuth(accounts);
			if(accounts.size() > 0 && hasAuth) {
				res.put("resCd", "V201");
				throw new Exception("Duplicate entry for account." + (reqCd.length() > 0 ? String.format("(reqCd: %s)", reqCd) : ""));
			}
			
			// get redis key
			redisKey = sRedisU.getRedisKey(inJsonList);
			logger.debug("redisKey = {}", redisKey);
			
			// get worker
			worker = sRedisU.getWorker(redisKey);
			res.put("worker", worker);
			logger.debug("worker = {}", worker);
			
			if(worker.length() == 0) {
				res.put("resCd", "V901");
				throw new Exception("No worker available.");
			}
			
			// worker value object
			SHttpVO sHttpVO = new SHttpVO();
			
			sHttpVO.setReadTimeout(1000 * 60 * 60);
			sHttpVO.setRequestUrl(String.format("%s/rest/ext", worker.split(",")[0].split("#")[0]));
			sHttpVO.setRequestParameter("reqCd"     , reqCd);
			sHttpVO.setRequestParameter("inJsonList", SCommonU.encodeURI(sRequestMap.getQuery().getString("inJsonList")));
			SHttpsU.request(sHttpVO);
			
			if(sHttpVO.getResponseCode() == 200) {
				outJsonText = new String(sHttpVO.getResponse());
				res.putAll(objectMapper.readValue(outJsonText, Map.class));
			} else {
				logger.debug("{} - {}", "SHttpVO", sHttpVO.toString());
				res.put("resCd", "V903");
				throw new Exception(sHttpVO.getExceptionMessage());
			}
			
			res.put("resCd", "0000");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("" + e);
			if("".equals(res.getString("resCd"))) {
				res.put("resCd", "E999");
			}
			if("".equals(res.getString("resMsg"))) {
				res.put("resMsg", "" + e);
			}
		} finally {
			
			// clear auth
			if(!hasAuth) {
				sRedisU.clearAuth(accounts);
			}
			
			// clear worker
			if(worker.length() > 0) {
				sRedisU.clearWorker(redisKey, worker);
			}
			
		}
		
		res.put("reqCd"      , reqCd);
		res.put("bridgeResDt", SCommonU.getDateString("yyyyMMddHHmmssSSS"));
		logger.debug("{} = {}", "res", res.toJsonString());
		
		return res;
	}
	
	@Override
	public SMap demo(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SExtractSI", "extDemo");
		
		SMap res = new SMap();
		
		return res;
	}

}
