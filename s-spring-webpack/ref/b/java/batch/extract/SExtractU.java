package ift.batch.kicc.extract;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import seung.commons.arguments.SMap;

@Component("sExtractU")
public class SExtractU {

	private static final Logger logger = LoggerFactory.getLogger(SExtractU.class);
	
	@Resource(name="sExtractS")
	private SExtractSI sExtractS;
	
	@Async("ts01")
	public void request01(
			String batchSeq
			, ArrayList<SMap> requestL
			) {
		
		logger.debug("{}.{}", "SExtractU", "request01");
		
		for(SMap requestO : requestL) {
			
			switch (requestO.getString("ORG_CD")) {
				case "hometax":
					sExtractS.hometaxUL(batchSeq, requestO);
					break;
				case "pbk":
					sExtractS.cbkUL(batchSeq, requestO);
					break;
				case "cbk":
					sExtractS.cbkUL(batchSeq, requestO);
					break;
				case "sbk":
					sExtractS.sbkUL(batchSeq, requestO);
					break;
				case "pcd":
					sExtractS.ccdUL(batchSeq, requestO);
					break;
				case "ccd":
					sExtractS.ccdUL(batchSeq, requestO);
					break;
				default:
					break;
			}
			
		}// end of loop requestL
		
	}
	
	@Async("ts02")
	public void request02(
			String batchSeq
			, ArrayList<SMap> requestL
			) {
		
		logger.debug("{}.{}", "SExtractU", "request02");
		
		sExtractS.cardsalesUL(batchSeq, requestL);
		
	}
	
	@Async("ts03")
	public void request03(
			String target
			, List<SMap> bmcCloseInfoSL
			) {
		
		logger.debug("{}.{}", "SExtractU", "request03");
		
		sExtractS.hometaxZ0001UL(target, bmcCloseInfoSL);
		
	}
	
}
