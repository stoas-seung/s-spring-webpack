package ift.bridge.docker.redis;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import seung.commons.SCommonU;
import seung.commons.arguments.SMap;

@Component("sRedisU")
public class SRedisU {

	private static final Logger logger = LoggerFactory.getLogger(SRedisU.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="sRedisD")
	private SRedisD sRedisD;
	
	public int init() {
		
		try {
			
			// clear running list by hostname
			for(String key : sRedisD.keys("ift.*")) {
				
				if("ift.account.run".equals(key)) {
					for(String value : sRedisD.smembers(key)) {
						if(value.indexOf(configProperties.getProperty("config.hostname")) > -1) {
							sRedisD.srem(key, value);
						}
					}
				} else if(key.indexOf(".run") > -1 && key.indexOf("cardsales") > -1) {
					for(String value : sRedisD.smembers(key)) {
						if(value.indexOf(configProperties.getProperty("config.hostname")) > -1) {
							sRedisD.srem(key, value);
							sRedisD.rpush(String.format("%s.wait", key.replaceAll(".run", "")), String.format("%s#%s", value.split("#")[0], SCommonU.getDateString("yyyyMMddHHmmss")));
						}
					}
				} else if(key.indexOf(".run") > -1 && key.indexOf("list") > -1) {
					for(String value : sRedisD.smembers(key)) {
						if(value.indexOf(configProperties.getProperty("config.hostname")) > -1) {
							sRedisD.srem(key, value);
							sRedisD.rpush(String.format("%s.wait", key.replaceAll(".run", "")), value.split(",")[0]);
						}
					}
				} else if(key.indexOf(".run") > -1) {
					for(String value : sRedisD.smembers(key)) {
						if(value.indexOf(configProperties.getProperty("config.hostname")) > -1) {
							sRedisD.srem(key, value);
							sRedisD.sadd(String.format("%s.wait", key.replaceAll(".run", "")), value.split(",")[0]);
						}
					}
				}
				
			}// end of key loop
			
			// refresh common worker
			CopyOnWriteArrayList<String> workers          = new CopyOnWriteArrayList<String>();
			CopyOnWriteArrayList<String> commonWorkerWait = new CopyOnWriteArrayList<String>();
			for(String worker : sRedisD.smembers("ift.common.worker.wait")) {
				commonWorkerWait.add(worker);
			}
			CopyOnWriteArrayList<String> commonWorkerRun  = new CopyOnWriteArrayList<String>();
			for(String worker : sRedisD.smembers("ift.common.worker.run")) {
				commonWorkerRun.add(worker);
			}
			for(String workerList : sRedisD.smembers("ift.worker.list")) {
				for(int num = 0; num < Integer.parseInt(configProperties.getProperty("config.worker.pool.max-active")); num++) {
					workers.add(String.format("%s#%d", workerList, num));
				}
			}
			
			// 설정에 따라 동작해야 하는 common.worker 목록과 common.worker.wait 목록을 비교한다.
			for(String workerA : workers) {
				for(String workerB : commonWorkerWait) {
					if(workerA.equals(workerB)) {
						workers.remove(workerA);
						commonWorkerWait.remove(workerA);
					}
				}
			}
			
			// 설정에 따라 동작해야 하는 common.worker 목록과 common.worker.run 목록을 비교한다.
			for(String workerA : workers) {
				for(String workerB : commonWorkerRun) {
					if(workerA.equals(workerB.split(",")[0])) {
						workers.remove(workerA);
						commonWorkerRun.remove(workerB);
					}
				}
			}
			
			// 추가되어야 하는 common.worker를 common.worker.wait 목록에 추가한다.
			for(String worker : workers) {
				if(worker.length() > 0) {
					sRedisD.sadd("ift.common.worker.wait", worker);
				}
			}
			
			// 제외되어야 하는 common.worker를 common.worker.wait 과 common.worker.run 에서 제외한다.
			for(String worker : commonWorkerWait) {
				if(worker.length() > 0) {
					sRedisD.srem("ift.common.worker.wait", worker);
					for(String workerB : commonWorkerRun) {
						if(worker.equals(workerB.split(",")[0])) {
							sRedisD.srem("ift.common.worker.run", workerB);
						}
					}
				}
			}
			
			// 제외되어야 하는 common.worker를 common.worker.wait 과 common.worker.run 에서 제외한다.
			for(String worker : commonWorkerRun) {
				if(worker.length() > 0) {
					sRedisD.srem("ift.common.worker.run", worker);
					for(String workerB : commonWorkerWait) {
						if(worker.equals(workerB)) {
							sRedisD.srem("ift.common.worker.wait", workerB);
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("" + e);
			return 0;
		}
		
		return 1;
	}
	
	public int addWorker() {
		
		
		return 1;
	}
	
	public String getAuthHash(SMap inJson) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		logger.debug("{}.{}", "SExtractU", "getAuthHash");
		
		String authHash = "";
		
		switch(inJson.getString("orgCd") + inJson.getString("svcCd")) {
			case "hometaxZ3001":
			case "hometaxZ3002":
			case "hometaxZ3003":
			case "hometaxZ3004":
			case "hometaxZ4001":
			case "hometaxZ4002":
				authHash = SCommonU.digestToBase64(
						"SHA-256"
						, inJson.getString("orgCd")
						+ inJson.getString("signCert")
						+ inJson.getString("userId")
						);
				break;
			case "bankB0001":
			case "bankB0002":
			case "pbkB0001":
			case "pbkB0002":
			case "cbkB0001":
			case "cbkB0002":
				authHash = SCommonU.digestToBase64(
						"SHA-256"
						, inJson.getString("orgCd")
							+ inJson.getString("signCert")
							+ inJson.getString("userId")
							+ inJson.getString("bankCd")
						);
				break;
			case "cardC0001":
			case "cardC0002":
			case "cardC0003":
			case "cardC0004":
			case "cardC0005":
			case "cardC0006":
			case "cardC0007":
			case "pcdC0001":
			case "pcdC0002":
			case "pcdC0003":
			case "pcdC0004":
			case "pcdC0005":
			case "pcdC0006":
			case "pcdC0007":
			case "ccdC0001":
			case "ccdC0002":
			case "ccdC0003":
			case "ccdC0004":
			case "ccdC0005":
			case "ccdC0006":
			case "ccdC0007":
				authHash = SCommonU.digestToBase64(
						"SHA-256"
						, inJson.getString("orgCd")
						+ inJson.getString("signCert")
						+ inJson.getString("userId")
						+ inJson.getString("cardCd")
						);
				break;
			case "cardsalesB0000":
			case "cardsalesB0001":
			case "cardsalesB0002":
			case "cardsalesB0003":
			case "cardsalesB0011":
			case "cardsalesB0012":
			case "cardsalesB0021":
			case "cardsalesB0022":
			case "cardsalesB0031":
			case "cardsalesB0032":
			case "cardsalesB0041":
			case "cardsalesB0051":
				authHash = SCommonU.digestToBase64(
						"SHA-256"
						, inJson.getString("orgCd")
						+ inJson.getString("userId")
						);
				break;
			default:
//				authHash = "default";
				break;
		}
		
		return authHash;
		
	}
	
	public synchronized boolean hasAuth(Set<String> accounts) {
		
		logger.debug("{}.{}", "SExtractU", "hasAuth");
		
		boolean hasAuth = false;
		
		Set<String> authIR = new HashSet<String>();
		for(String account : accounts) {
			if(1 == sRedisD.sadd("ift.account.run", account)) {
				authIR.add(account);
			} else {
				hasAuth = true;
				break;
			}
		}
		
		if(hasAuth) {
			for(String account : authIR) {
				sRedisD.srem("ift.account.run", account);
			}
		}
		
		if(!hasAuth) {
			for(String account : accounts) {
				sRedisD.sadd("ift.account.run", String.format("%s,%s", account, configProperties.getProperty("config.hostname")));
			}
		}
		
		return hasAuth;
	}
	
	public int clearAuth(Set<String> accounts) {
		
		logger.debug("{}.{}", "SExtractU", "clearAuth");
		
		int clearAuth = 0;
		
		for(String account : accounts) {
			clearAuth += sRedisD.srem("ift.account.run", String.format("%s,%s", account, configProperties.getProperty("config.hostname")));
		}
		
		return clearAuth;
	}
	
	public String getRedisKey(List<SMap> inJsonList) {
		
		logger.debug("{}.{}", "SExtractU", "getRedisKey");
		
		String redisKey = "ift.common.worker";
		
		for(SMap inJson : inJsonList) {
			
			if("cardsales".equals(inJson.getString("orgCd"))) {
				if("1".equals(inJson.getString("isBatch"))) {
					redisKey = "ift.cardsales.cardsales1";
				} else {
					redisKey = "ift.cardsales.cardsales0";
				}
				
				break;
			}
			
			switch(inJson.getString("orgCd") + inJson.getString("svcCd")) {
				case "hometaxZ0001":
					redisKey = "ift.set.hometaxZ0001";
					break;
				default:
					break;
			}
			
		}
		
		return redisKey;
	}
	
	public String getWorker(String redisKey) throws ParseException {
		
		logger.debug("{}.{}", "SExtractU", "getWorker");
		
		String worker = "";
		
		String workerKeyWait = String.format("%s.wait", redisKey);
		String workerKeyRun  = String.format("%s.run", redisKey);
		switch(redisKey.split("\\.")[1]) {
			case "common":
				worker = sRedisD.spop(workerKeyWait);
				if(worker == null || worker.length() == 0) {
					if(sRedisD.scard(workerKeyWait) + sRedisD.scard(workerKeyRun) == 0) {
						for(String workerList : sRedisD.smembers("ift.worker.list")) {
							for(int num = 0; num < Integer.parseInt(configProperties.getProperty("config.worker.pool.max-active")); num++) {
								sRedisD.sadd(workerKeyWait, String.format("%s#%d", workerList, num));
							}
						}
						worker = sRedisD.spop(workerKeyWait);
					}
				}
				break;
			case "set":
				worker = sRedisD.spop(workerKeyWait);
				if(worker == null || worker.length() == 0) {
					if(sRedisD.scard(workerKeyWait) + sRedisD.scard(workerKeyRun) == 0) {
						for(String workerList : sRedisD.smembers("ift.worker.list")) {
							sRedisD.sadd(workerKeyWait, workerList);
						}
						worker = sRedisD.spop(workerKeyWait);
					}
				}
				break;
			case "list":
				worker = sRedisD.lpop(workerKeyWait);
				if(worker == null || worker.length() == 0) {
					if(sRedisD.llen(workerKeyWait) + sRedisD.llen(workerKeyRun) == 0) {
						for(String workerList : sRedisD.smembers("ift.worker.list")) {
							sRedisD.rpush(workerKeyWait, String.format("%s#%s", workerList, SCommonU.getDateString("yyyyMMdd000000")));
						}
						worker = sRedisD.lpop(workerKeyWait);
					}
				}
				break;
			case "cardsales":
				worker = sRedisD.lpop(workerKeyWait);
				if(worker == null || worker.length() == 0) {
					if(sRedisD.llen(workerKeyWait) + sRedisD.llen(workerKeyRun) == 0) {
						for(String workerList : sRedisD.smembers("ift.worker.list")) {
							sRedisD.rpush(workerKeyWait, String.format("%s#%s", workerList, SCommonU.getDateString("yyyyMMdd000000")));
						}
						worker = sRedisD.lpop(workerKeyWait);
					}
				} else {
					long diff = TimeUnit.SECONDS.convert(
							SCommonU.stringToDate(SCommonU.getDateString("yyyyMMddHHmmss"), "yyyyMMddHHmmss").getTime() - SCommonU.stringToDate(worker.split("#")[1], "yyyyMMddHHmmss").getTime()
							, TimeUnit.MILLISECONDS
							);
					if(diff <= Long.parseLong(configProperties.getProperty("config.cardsales.interval", "" + (60 * 20)))) {
						sRedisD.lpush(workerKeyWait, worker);
						worker = "";
					}
				}
				break;
			default:
				break;
		}
		
		if(worker == null || worker.length() == 0) {
			worker = "";
		} else {
			sRedisD.sadd(workerKeyRun, String.format("%s,%s", worker, configProperties.getProperty("config.hostname")));
		}
		
		return worker;
	}
	
	public int clearWorker(String redisKey, String worker) {
		
		logger.debug("{}.{}", "SExtractU", "clearWorker");
		
		String workerKeyWait = String.format("%s.%s", redisKey, "wait");
		String workerKeyRun  = String.format("%s.run", redisKey);
		
		try {
			switch(redisKey.split("\\.")[1]) {
				case "list":
					if(sRedisD.srem(workerKeyRun , String.format("%s,%s", worker, configProperties.getProperty("config.hostname"))) > 0) {
						sRedisD.rpush(workerKeyWait, worker);
					}
					break;
				case "cardsales":
					if(sRedisD.srem(workerKeyRun , String.format("%s,%s", worker, configProperties.getProperty("config.hostname"))) > 0) {
						sRedisD.rpush(workerKeyWait, String.format("%s#%s", worker.split("#")[0], SCommonU.getDateString("yyyyMMddHHmmss")));
					}
					break;
				default:
					if(sRedisD.srem(workerKeyRun , String.format("%s,%s", worker, configProperties.getProperty("config.hostname"))) > 0) {
						sRedisD.sadd(workerKeyWait, worker);
					}
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("" + e);
			return 0;
		}
		
		return 1;
	}
	
}
