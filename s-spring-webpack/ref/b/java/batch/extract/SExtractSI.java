package ift.batch.kicc.extract;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import seung.app.conf.dao.SDao;
import seung.commons.SCommonU;
import seung.commons.SHttpsU;
import seung.commons.arguments.SMap;
import seung.commons.http.SHttpVO;

@Component("sExtractS")
public class SExtractSI {

	private static final Logger logger = LoggerFactory.getLogger(SExtractSI.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="iftCode")
	private SMap iftCode;
	
	@Resource(name="sDao")
	private SDao sDao;
	
	@Resource(name="kiccU")
	private KiccU kiccU;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional
	public void hometaxUL(String batchSeq, SMap requestO) {
		
		logger.debug("{}.{}", "SExtractSI", "hometaxUL");
		
		SHttpVO sHttpVO       = new SHttpVO();
		String  reqCd         = "";
		int     iftLogHistIR  = -1;
		String  responseText  = "";
		SMap    responseSMap  = null;
		List    outJsonList   = null;
		SMap    inJson        = new SMap();
		SMap    workerSR      = null;
		String  camelCase     = "";
		SMap    outJson       = null;
		
		try {
			
			/*
			 * desc: inJson 생성 - underscore to camel and null to blank
			 */
			for(Object keys : requestO.keySet()) {
				camelCase = "";
				for(String key : ((String) keys).split("_")) {
					if(key.length() > 0) {
						camelCase += camelCase.length() == 0 ? key.toLowerCase() : key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
					}
				}
				inJson.put(camelCase, requestO.getString((String) keys));
			}
			
			/*
			 * 로그 등록
			 */
			reqCd = SCommonU.digestToHexString("SHA", inJson.toJsonString());
			requestO.put("reqCd"     , reqCd);
			requestO.put("rst"       , "");
			requestO.put("msg"       , "");
			requestO.put("srvCd"     , "");
			requestO.put("DT_FROM"   , requestO.getString("INQR_DT_STRT"));
			requestO.put("DT_TO"     , requestO.getString("INQR_DT_END"));
			requestO.put("batchSeq"  , batchSeq);
			iftLogHistIR = sDao.ds01IR("iftLogHistIR", requestO);
			
			if(iftLogHistIR != 1) {
				throw new Exception("ift_log_hist 등록에 실패하였습니다.");
			}
			
			/*
			 * 수집 요청
			 */
			sHttpVO.setIsSSL(true);
			sHttpVO.setReadTimeout(1000 * 60 * 60);
			sHttpVO.setRequestUrl(configProperties.getProperty("config.extract.url", "https://127.0.0.1:9431/route/rest/ext"));
			sHttpVO.setRequestParameter("reqCd"     , reqCd);
			sHttpVO.setRequestParameter("inJsonList", SCommonU.encodeURI(String.format("[%s]", inJson.toJsonString())));
			
			int maxRequest = 60;
			while(true) {
				
				SHttpsU.request(sHttpVO);
				
				if(sHttpVO.getResponseCode() != 200) {
					throw new Exception(sHttpVO.getExceptionMessage());
				}
				
				responseText = new String(sHttpVO.getResponse());
				responseSMap = new SMap(new ObjectMapper().readValue(responseText, Map.class));
				
				if("V201".equals(responseSMap.getString("resCd"))) {
					Thread.sleep(1000);
					continue;
				} else if("V901".equals(responseSMap.getString("resCd"))) {
					Thread.sleep(1000);
					continue;
				} else if("V903".equals(responseSMap.getString("resCd"))) {
					if(--maxRequest > 0) {
						Thread.sleep(1000);
						continue;
					}
				}
				
				if("0000".equals(responseSMap.getString("resCd"))) {
					break;
				} else {
					throw new Exception(responseSMap.getString("resMsg"));
				}
				
			}
			
			outJsonList = responseSMap.getList("outJsonList");
			if(outJsonList == null || outJsonList.size() == 0) {
				System.out.println("error::" + responseText);
				throw new Exception("outJsonList 가 존재하지 않습니다.");
			}
			
			if(responseSMap.getString("worker").length() > 0) {
				workerSR = sDao.ds01SR("workerSR", responseSMap);
				if(workerSR == null) {
					sDao.ds01IR("workerIR", responseSMap);
					workerSR = sDao.ds01SR("workerSR", responseSMap);
				}
				requestO.put("SRV_CD", workerSR.getString("SRV_CD"));
			} else {
				requestO.put("SRV_CD", "");
			}
			
			outJson = new SMap((Map) outJsonList.get(0));
			
			if("Y".equals(outJson.getString("errYn"))) {
				throw new Exception(outJson.getString("errMsg"));
			}
			
			ArrayList<LinkedHashMap> list        = null;
			String                   SAL_TYPE_CD = "";
			String                   APRV_YN     = "";
			String                   TRX_TYPE    = "";
			switch (requestO.getString("SVC_CD")) {
			
				case "Z3001":
				case "Z3002":
				case "Z3003":
				case "Z3004":
					
					int bssTaxInvcBkdnIR = 0;
					list = (ArrayList<LinkedHashMap>) outJson.get("list");
					
					for(LinkedHashMap bill : list) {
						bill.put("KICC_ID"    , requestO.getString("KICC_ID"));
						if(requestO.getInt("TYPE_CD") == 2 || requestO.getInt("TYPE_CD") == 4) {
							if(!requestO.getString("BIZ_NO").equals((String) bill.get("supBizNo"))) {
								throw new Exception(String.format("%s [BIZNO: %s, supBizNo: %s]", "공급자사업자번호가 일치하지 않습니다.", requestO.getString("BIZ_NO"), (String) bill.get("supBizNo")));
							}
						} else if(requestO.getInt("TYPE_CD") == 1 || requestO.getInt("TYPE_CD") == 3) {
							if(!requestO.getString("BIZ_NO").equals((String) bill.get("byrBizNo"))) {
								throw new Exception(String.format("%s [BIZNO: %s, byrBizNo: %s]", "공급자받는자사업자번호가 일치하지 않습니다.", requestO.getString("BIZ_NO"), (String) bill.get("byrBizNo")));
							}
						}
						bill.put("TYPE_CD"    , requestO.getInt("TYPE_CD"));
						bill.put("SVC_CD"     , requestO.getString("SVC_CD"));
						SAL_TYPE_CD = iftCode.getString("ift.h.Z3001.demandGb." + bill.get("demandGb"));
						if(SAL_TYPE_CD == null || SAL_TYPE_CD.length() == 0) {
							throw new Exception("[SAL_TYPE_CD]을 확인할 수 없습니다. demandGb: " + bill.get("demandGb"));
						} else {
							bill.put("SAL_TYPE_CD", SAL_TYPE_CD);
						}
						bssTaxInvcBkdnIR += sDao.ds01IR("bssTaxInvcBkdnIR", new SMap(bill));
					}
					requestO.put("msg", String.format("list:%d, bssTaxInvcBkdnIR:%d", list == null ? 0 : list.size(), bssTaxInvcBkdnIR));
					
					break;
					
				case "Z4001":
					
					int bssSaleCashBkdnIR = 0;
					list = (ArrayList<LinkedHashMap>) outJson.get("list");
					for(LinkedHashMap bill : list) {
						bill.put("KICC_ID" , requestO.getString("KICC_ID"));
						bill.put("SVC_CD"  , requestO.getString("SVC_CD"));
						APRV_YN = iftCode.getString("ift.h.Z4001.trGb." + bill.get("trGb"));
						if(APRV_YN == null || APRV_YN.length() == 0) {
							throw new Exception("[CARD_TYPE]을 확인할 수 없습니다. trGb: " + bill.get("trGb"));
						} else {
							bill.put("APRV_YN", APRV_YN);
						}
						TRX_TYPE = iftCode.getString("ift.h.Z4001.issueGb." + bill.get("issueGb"));
						if(TRX_TYPE == null || TRX_TYPE.length() == 0) {
							throw new Exception("[TRX_TYPE]을 확인할 수 없습니다. issueGb: " + bill.get("issueGb"));
						} else {
							bill.put("TRX_TYPE", TRX_TYPE);
						}
						bssSaleCashBkdnIR += sDao.ds01IR("bssSaleCashBkdnIR", new SMap(bill));
					}
					requestO.put("msg", String.format("list:%d, bssSaleCashBkdnIR:%d", list == null ? 0 : list.size(), bssSaleCashBkdnIR));
					
					break;
					
				case "Z4002":
					
					int bssCashBkdnIR = 0;
					list = (ArrayList<LinkedHashMap>) outJson.get("list");
					for(LinkedHashMap bill : list) {
						bill.put("KICC_ID" , requestO.getString("KICC_ID"));
						bill.put("SVC_CD"  , requestO.getString("SVC_CD"));
						bill.put("APRV_YN" , iftCode.getString("ift.h.Z4002.trGb." + bill.get("trGb")));
						bill.put("DEDCT_YN", iftCode.getString("ift.h.Z4002.ddcYn." + bill.get("ddcYn")));
						bssCashBkdnIR += sDao.ds01IR("bssSaleCashBkdnIR", new SMap(bill));
					}
					requestO.put("msg", String.format("list:%d, bssCashBkdnIR:%d", list == null ? 0 : list.size(), bssCashBkdnIR));
					
					break;
					
				default:
					break;
			}
			
			requestO.put("rst", responseSMap.getString("resCd"));
			
			kiccU.kiccAPI(requestO.getString("KICC_ID"), inJson.getString("orgCd"), inJson.getString("svcCd"), "");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("" + e);
			requestO.put("rst", "S999");
			requestO.put("msg", String.format("%s%s", requestO.getString("msg").length() > 0 ? requestO.getString("msg") + ", error: " : "", "" + e));
		} finally {
			
			try {
				
				requestO.put("stateCd", "D");
				sDao.ds01IR("iftProcHistIR", requestO);
				if(!"0000".equals(requestO.getString("rst"))) {
					requestO.put("responseText", responseText);
					sDao.ds01IR("iftSchErrIR"  , requestO);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("" + e);
			}
			
		}
		
	}
	
	
}
