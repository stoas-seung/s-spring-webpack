package ift.bridge.docker.redis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import seung.commons.SCommonU;
import seung.commons.SCommonV;
import seung.commons.SHttpU;
import seung.commons.SHttpsU;
import seung.commons.arguments.SMap;
import seung.commons.arguments.SRequestMap;
import seung.commons.http.SHttpVO;

@Service("sRedisS")
public class SRedisSI implements SRedisS {

	private static final Logger logger = LoggerFactory.getLogger(SRedisSI.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="sRedisD")
	private SRedisD sRedisD;
	
	@Override
	public SMap help(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "help");
		
		SMap res = new SMap();
		String[] help = {
			"$ redis-cli -p $PORT",
			">",
			"> info server",
			">",
			"> auth $PASSWORD",
			">",
			"> keys ift:*",
			">",
			"> sadd ift.worker.list http(s)://$IP:$PORT",
			">",
			"> sadd ift.common.worker.wait http(s)://$IP:$PORT#$THREAD_NUMBER",
			"> spop ift.common.worker.wait",
			"> sadd ift.common.worker.run http(s)://$IP:$PORT#$THREAD_NUMBER,$HOST_NAME",
			"> srem ift.common.worker.run http(s)://$IP:$PORT#$THREAD_NUMBER,$HOST_NAME",
			">",
			"> sadd ift.account.run $SHA256($orgCd + $signCert + $userId + $bankCd/$cardCd/$memGrpId),$HOST_NAME",
			"> srem ift.account.run $SHA256($orgCd + $signCert + $userId + $bankCd/$cardCd/$memGrpId),$HOST_NAME",
			">",
			"> rpush ift.cardsales.cardsales0.wait http(s)://$IP:$PORT#$yyyyMMddHHmmss",
			"> lpop  ift.cardsales.cardsales0.wait",
			"> sadd  ift.cardsales.cardsales0.run http(s)://$IP:$PORT#$yyyyMMddHHmmss,$HOST_NAME",
			"> srem  ift.cardsales.cardsales0.run http(s)://$IP:$PORT#$yyyyMMddHHmmss,$HOST_NAME",
			">",
			"> rpush ift.cardsales.cardsales1.wait http(s)://$IP:$PORT#$yyyyMMddHHmmss",
			"> lpop  ift.cardsales.cardsales1.wait",
			"> sadd  ift.cardsales.cardsales1.run http(s)://$IP:$PORT#$yyyyMMddHHmmss,$HOST_NAME",
			"> srem  ift.cardsales.cardsales1.run http(s)://$IP:$PORT#$yyyyMMddHHmmss,$HOST_NAME",
			">",
			"> sadd ift.set.($orgCd + $svcCd).wait http(s)://$IP:$PORT",
			"> spop ift.set.($orgCd + $svcCd).wait",
			"> sadd ift.set.($orgCd + $svcCd).run http(s)://$IP:$PORT,$HOST_NAME",
			"> srem ift.set.($orgCd + $svcCd).run http(s)://$IP:$PORT,$HOST_NAME",
			">",
			"> rpush ift.set.($orgCd + $svcCd).wait http(s)://$IP:$PORT",
			"> lpop  ift.set.($orgCd + $svcCd).wait",
			"> sadd  ift.set.($orgCd + $svcCd).run http(s)://$IP:$PORT,$HOST_NAME",
			"> srem  ift.set.($orgCd + $svcCd).run http(s)://$IP:$PORT,$HOST_NAME",
			">"
		};
		res.put("help", help);
		
		return res;
	}
	
	@Override
	public SMap workerUR(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "workerUR");
		
		SMap res = new SMap();
		
		String protocol = "true".equals(sRequestMap.getQuery().getString("serverSslEnabled")) ? "https" : "http";
		String ip       = (sRequestMap.getQuery().getString("hostAddress").length() > 0 ? sRequestMap.getQuery().getString("hostAddress") : sRequestMap.getNetwork().getString("remoteAddr"));
		String port     = sRequestMap.getQuery().getString("serverPort");
		
		String addWorker = String.format("%s://%s:%s", protocol, ip, port);
		
		for(String worker : sRedisD.smembers("ift.worker.stop")) {
			if(worker.equals(addWorker)) {
				sRedisD.srem("ift.worker.stop", worker);
			}
		}
		res.put("ift.worker.list", sRedisD.sadd("ift.worker.list", addWorker));
		
		for(String key : sRedisD.keys("ift.*")) {
			if(key.equals("ift.common.worker.wait")) {
				int commonWorkerWait = 0;
				for(int num = 0; num < Integer.parseInt(configProperties.getProperty("config.worker.pool.max-active")); num++) {
					commonWorkerWait += sRedisD.sadd(key, String.format("%s#%d", addWorker, num));
				}
				res.put(key, commonWorkerWait);
			} else if(key.indexOf(".wait") > -1 && key.indexOf("cardsales") > -1) {
				res.put(key, sRedisD.rpush(key, String.format("%s#%s", addWorker, SCommonU.getDateString("yyyyMMddHHmmss"))));
			} else if(key.indexOf(".wait") > -1 && key.indexOf("list") > -1) {
				res.put(key, sRedisD.rpush(key, String.format("%s#%s", addWorker, SCommonU.getDateString("yyyyMMddHHmmss"))));
			} else if(key.indexOf(".wait") > -1) {
				res.put(key, sRedisD.sadd(key, addWorker));
			}
		}
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap redisSL(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "workerSL");
		
		SMap res = new SMap();
		
		res.putAll(sRedisD.data("ift*"));
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap workerSL(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "workerSL");
		
		SMap res = new SMap();
		
		ArrayList<SMap> workerSL = new ArrayList<SMap>();
		SMap    workerSR         = null;
		SHttpVO sHttpVO          = null;
		for(String url : sRedisD.smembers("ift.worker.list")) {
			workerSR = new SMap();
			workerSR.put("url", url);
			sHttpVO = new SHttpVO();
			sHttpVO.setConnectionTimeout(1000);
			sHttpVO.setRequestMethod(SCommonV._S_METHOD_GET);
			sHttpVO.setRequestUrl(url + "/rest/ext/info");
			if(url.startsWith("https")) {
				SHttpsU.request(sHttpVO);
			} else {
				SHttpU.request(sHttpVO);
			}
			if(sHttpVO.getResponseCode() == 200) {
				workerSR.putAll(new SMap(new String(sHttpVO.getResponse())));
			} else {
				workerSR.put("exceptionMessage", sHttpVO.getExceptionMessage());
			}
			workerSL.add(workerSR);
		}
		for(String url : sRedisD.smembers("ift.worker.stop")) {
			workerSR = new SMap();
			workerSR.put("url", url);
			sHttpVO = new SHttpVO();
			sHttpVO.setConnectionTimeout(1000);
			sHttpVO.setRequestMethod(SCommonV._S_METHOD_GET);
			sHttpVO.setRequestUrl(url + "/rest/ext/info");
			if(url.startsWith("https")) {
				SHttpsU.request(sHttpVO);
			} else {
				SHttpU.request(sHttpVO);
			}
			if(sHttpVO.getResponseCode() == 200) {
				workerSR.putAll(new SMap(new String(sHttpVO.getResponse())));
			} else {
				workerSR.put("exceptionMessage", sHttpVO.getExceptionMessage());
			}
			workerSL.add(workerSR);
		}
		
		res.put("workerSL", workerSL);
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap refresh(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "refresh");
		
		SMap res = new SMap();
		
		res.putAll(sRedisD.data("ift*"));
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap stop(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "workerSL");
		
		SMap res = new SMap();
		
		for(String keys : sRedisD.keys("ift.*")) {
			
			if("ift.worker.stop".equals(keys)) {
				continue;
			}
			
			if("ift.worker.list".equals(keys)) {
				
				for(String worker : sRedisD.smembers(keys)) {
					if(worker.indexOf(sRequestMap.getQuery().getString("worker")) > -1) {
						res.put(String.format("srem %s %s", keys, worker), res.getInt(String.format("srem %s %s", keys, worker), 0) + sRedisD.srem(keys, worker));
						res.put(String.format("sadd %s %s", "ift.worker.stop", worker), res.getInt(String.format("sadd %s %s", "ift.worker.stop", worker), 0) + sRedisD.sadd("ift.worker.stop", worker));
					}
				}
				
			} else if(keys.indexOf("cardsales") > -1 || keys.indexOf("list") > -1) {
				
				for(String worker : sRedisD.lrange(keys)) {
					if(worker.indexOf(sRequestMap.getQuery().getString("worker")) > -1) {
						res.put(String.format("lrem %s 999 %s", keys, worker), res.getInt(String.format("lrem %s 999 %s", keys, worker), 0) + sRedisD.lrem(keys, 999, worker));
					}
				}
				
			} else {
				
				for(String worker : sRedisD.smembers(keys)) {
					if(worker.indexOf(sRequestMap.getQuery().getString("worker")) > -1) {
						res.put(String.format("srem %s %s", keys, worker), res.getInt(String.format("srem %s %s", keys, worker), 0) + sRedisD.srem(keys, worker));
					}
				}
				
			}
			
		}// end of delete loop
		
		res.putAll(sRedisD.data("ift*"));
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SMap wait(SRequestMap sRequestMap) throws Exception {
		
		logger.debug("{}.{}", "SRedisSI", "workerSL");
		
		SMap res = new SMap();
		
		Set<String> workers         = new HashSet<String>();
		String      yyyyMMddHHmmss  = "";
		boolean     isCardsalesStop = false;
		
		for(String worker : sRedisD.smembers("ift.worker.stop")) {
			if(worker.indexOf(sRequestMap.getQuery().getString("worker")) > -1) {
				res.put(String.format("srem ift.worker.stop %s", worker), res.getInt(String.format("srem ift.worker.stop %s", worker), 0) + sRedisD.srem("ift.worker.stop", worker));
				workers.add(worker);
			}
		}
		
		for(String worker : workers) {
			res.put(String.format("sadd %s %s", "ift.worker.list", worker), res.getInt(String.format("sadd %s %s", "ift.worker.list", worker), 0) + sRedisD.sadd("ift.worker.list", worker));
		}
		
		if(workers.size() > 0) {
			
			for(String keys : sRedisD.keys("ift.*")) {
				
				if("ift.worker.stop".equals(keys) || "ift.worker.list".equals(keys)) {
					continue;
				}
				
				if(keys.indexOf(".wait") == -1) {
					continue;
				}
				
				if("ift.common.worker.wait".equals(keys)) {
					
					for(String worker : workers) {
						for(int num = 0; num < Integer.parseInt(configProperties.getProperty("config.worker.pool.max-active")); num++) {
							res.put(String.format("sadd %s %s", keys, String.format("%s#%d", worker, num)), res.getInt(String.format("sadd %s %s", keys, String.format("%s#%d", worker, num)), 0) + sRedisD.sadd(keys, String.format("%s#%d", worker, num)));
						}
					}
					
				} else if(keys.indexOf("cardsales") > -1) {
					
					for(String worker : workers) {
						isCardsalesStop = false;
						for(String stops : sRedisD.smembers(keys.replace(".wait", ".stop"))) {
							if(stops.indexOf(worker) == -1) {
								isCardsalesStop = true;
								break;
							}
						}
						if(!isCardsalesStop) {
							yyyyMMddHHmmss = SCommonU.getDateString("yyyyMMddHHmmss");
							res.put(String.format("rpush %s %s", keys, String.format("%s#%s", worker, yyyyMMddHHmmss)), res.getInt(String.format("rpush %s %s", keys, String.format("%s#%s", worker, yyyyMMddHHmmss)), 0) + sRedisD.rpush(keys, String.format("%s#%s", worker, yyyyMMddHHmmss)));
						}
					}
					
				} else if(keys.indexOf("list") > -1) {
					
					for(String worker : workers) {
						res.put(String.format("rpush %s %s", keys, worker), res.getInt(String.format("rpush %s %s", keys, worker), 0) + sRedisD.rpush(keys, worker));
					}
					
				} else {
					
					for(String worker : workers) {
						res.put(String.format("sadd %s %s", keys, worker), res.getInt(String.format("sadd %s %s", keys, worker), 0) + sRedisD.sadd(keys, worker));
					}
					
				}
				
			}// end of delete loop
			
		}
		
		res.putAll(sRedisD.data("ift*"));
		
		return res;
	}
	
}
