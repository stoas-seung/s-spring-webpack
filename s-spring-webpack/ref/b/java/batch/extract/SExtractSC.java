package ift.batch.kicc.extract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import seung.app.conf.dao.SDao;
import seung.commons.SCommonU;
import seung.commons.arguments.SMap;

@Component("sExtractSC")
public class SExtractSC {

	private static final Logger logger = LoggerFactory.getLogger(SExtractSC.class);
	
	@Resource(name="configProperties")
	private Properties configProperties;
	
	@Resource(name="iftCode")
	private SMap iftCode;
	
	@Resource(name="requestTarget")
	private SMap requestTarget;
	
	@Resource(name="cardsalesTarget")
	private SMap cardsalesTarget;
	
	@Resource(name="ts01")
	private ThreadPoolTaskExecutor ts01;
	
	@Resource(name="ts02")
	private ThreadPoolTaskExecutor ts02;
	
	@Resource(name="ts03")
	private ThreadPoolTaskExecutor ts03;
	
	@Resource(name="sExtractU")
	private SExtractU sExtractU;
	
	@Resource(name="sDao")
	private SDao sDao;
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 1000)
//	@Transactional
	public void batchRequest() {
		
		logger.debug("{}.{}", "SExtractSC", "batchRequest");
		
		int    actBatchHistIR = -1;
		int    actBatchHistUR = -1;
		String batchSeq       = "";
		
		List<SMap> reqSL = null;
		try {
			
			// 쓰레드 카운트 체크
			int activeCount = ts01.getActiveCount();
			logger.debug("{}: {}", "activeCount", activeCount);
			logger.debug("{}: {}", "batchSeq", requestTarget.getString("batchSeq"));
			
			/*
			 * desc : 수집되는 쓰레드가 없고 이전 수집이 진행된 경우 다음 순서 수집을 진행한다.
			 * order: hometax > pbk > cbk > sbk > pcd > ccd
			 */
			if(activeCount == 0 && requestTarget.getString("batchSeq").length() > 0) {
				
				requestTarget.put("endDtm"    , new Date());
				requestTarget.put("elapsedTm" , Long.toString(TimeUnit.MILLISECONDS.convert(Math.abs(((Date) requestTarget.get("endDtm")).getTime() - ((Date) requestTarget.get("startDtm")).getTime()), TimeUnit.MILLISECONDS)));
				requestTarget.put("jobStatus" , "COMPLETE");
				requestTarget.put("resCd"     , "B001".equals(requestTarget.getString("resCd")) ? "0000" : requestTarget.getString("resCd"));
				requestTarget.put("manualYn"  , "N");
				requestTarget.put("batchPrgCd", "");
				
				actBatchHistUR = sDao.ds01UR("actBatchHistUR", requestTarget);
				
				if(actBatchHistUR != 1) {
					throw new Exception("수집내역 업데이트에 실패하였습니다.");
				}
				
				requestTarget.put("batchSeq", "");
				
			}
			
			/*
			 * desc : 수집되고 있다면 다음 수집까지 진행하지 않는다
			 */
			if(activeCount > 0) {
				return;
			}
			
			/*
			 * desc : 배치명을 생성하고 요청내역을 확인한다. 이때 어플리케이션코드(appCd)를 배치명으로 설정한다.
			 */
			requestTarget.put("currentTarget", requestTarget.getString(requestTarget.getString("currentTarget")));
			requestTarget.put("batchNm"      , String.format("kicc.batch.%s.%s", requestTarget.getString("currentTarget"), SCommonU.getDateString("yyyyMMddHHmmss")));
			logger.debug("{}: {}", "currentTarget", requestTarget.getString("currentTarget"));
			reqSL = sDao.ds01SL("reqSL", requestTarget);
			logger.debug("{}: {}", "reqSL.size", reqSL.size());
			
			/*
			 * desc : 요청내역이 없을 경우 다음 수집을 진행하기 위해 현재 진행을 중지한다.
			 */
			if(reqSL == null || reqSL.size() == 0) {
				return;
			}
			
			requestTarget.put("startDtm"  , new Date());
			requestTarget.put("endDtm"    , "");
			requestTarget.put("elapsedTm" , -1);
			requestTarget.put("jobStatus" , "START");
			requestTarget.put("resCd"     , "B001");
			requestTarget.put("manualYn"  , "N");
			requestTarget.put("errMsg"    , "");
			requestTarget.put("batchPrgCd", "");
			
			/*
			 * desc: 배치내용을 등록한다.
			 */
			actBatchHistIR = sDao.ds01IR("actBatchHistIR", requestTarget);
			batchSeq       = requestTarget.getString("batchSeq");
			logger.debug("{}: {}", "actBatchHistIR", actBatchHistIR);
			
			if(actBatchHistIR != 1) {
				throw new Exception("수집내역 등록에 실패하였습니다.");
			}
			
			/*
			 * desc: 요청전문별 비밀번호를 복호화 하고 계정별로 그룹을 생성한다.
			 */
			HashMap<String, ArrayList<SMap>> requestL = new HashMap<String, ArrayList<SMap>>();
			ArrayList<SMap>                  groupL   = null;
			String                           hash     = "";
			for(SMap reqSR : reqSL) {
				
				reqSR.putAll(sDao.ds02SR("keySR", reqSR));
				
				//SHA256(orgCd, signCert, signPri, signPw, userId, userPw, bankCd/cardCd/memGrpId)
				switch(requestTarget.getString("currentTarget")) {
					case "hometax":
						hash = SCommonU.digestToHexString(
								"SHA-256"
								, reqSR.getString("ORG_CD")
								+ reqSR.getString("SIGN_CERT")
								+ reqSR.getString("USER_ID")
								);
						break;
					case "pbk":
					case "cbk":
						hash = SCommonU.digestToHexString(
								"SHA-256"
								, reqSR.getString("ORG_CD")
								+ reqSR.getString("SIGN_CERT")
								+ reqSR.getString("USER_ID")
								+ reqSR.getString("BANK_CD")
								);
						break;
					case "sbk":
						hash = SCommonU.digestToHexString(
								"SHA-256"
								, reqSR.getString("ORG_CD")
								+ reqSR.getString("KICC_ID")
								+ reqSR.getString("ACCT_NO")
								);
						break;
					case "pcd":
					case "ccd":
						hash = SCommonU.digestToHexString(
								"SHA-256"
								, reqSR.getString("ORG_CD")
								+ reqSR.getString("SIGN_CERT")
								+ reqSR.getString("USER_ID")
								+ reqSR.getString("CARD_CD")
								);
						break;
					default:
						break;
				}// end of account hash
				
				if(requestL.containsKey(hash)) {
					requestL.get(hash).add(reqSR);
				} else {
					groupL = new ArrayList<SMap>();
					groupL.add(reqSR);
					requestL.put(hash, groupL);
				}
				
			}// end of loop reqSL
			
			/*
			 * desc: 그룹별로 수집요청을 진행한다.
			 */
			for(String key : requestL.keySet()) {
				sExtractU.request01(batchSeq, requestL.get(key));
			}
			
			/*
			 * desc: 배치그룹 진행내역을 업데이트 한다.
			 */
			requestTarget.put("errMsg"   , String.format("reqSL: %d, requestL: %d", reqSL.size(), requestL.keySet().size()));
			requestTarget.put("jobStatus", "EXECUTE");
			sDao.ds01UR("actBatchHistUR", requestTarget);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("" + e);
			
			try {
				
				requestTarget.put("resCd" , "B002");
				requestTarget.put("errMsg", requestTarget.getString("errMsg") + e);
				sDao.ds01UR("actBatchHistUR", requestTarget);
				
			} catch (Exception e1) {
				logger.error("" + e);
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Scheduled(fixedDelay = 1000)
//	@Transactional
	public void batchCardsales() {
		
		logger.debug("{}.{}", "SExtractSC", "batchCardsales");
		
		int    actBatchHistIR  = -1;
		int    actBatchHistUR  = -1;
		String batchSeq        = "";
		
		List<SMap> reqSL = null;
		try {
			
			// 쓰레드 카운트 체크
			int activeCount = ts02.getActiveCount();
			logger.debug("{}: {}", "activeCount", activeCount);
			logger.debug("{}: {}", "batchSeq", cardsalesTarget.getString("batchSeq"));
			
			if(activeCount == 0 && cardsalesTarget.getString("batchSeq").length() > 0) {
				
				cardsalesTarget.put("endDtm"    , new Date());
				cardsalesTarget.put("elapsedTm" , Long.toString(TimeUnit.MILLISECONDS.convert(Math.abs(((Date) cardsalesTarget.get("endDtm")).getTime() - ((Date) cardsalesTarget.get("startDtm")).getTime()), TimeUnit.MILLISECONDS)));
				cardsalesTarget.put("jobStatus" , "COMPLETE");
				cardsalesTarget.put("resCd"     , "B001".equals(cardsalesTarget.getString("resCd")) ? "0000" : cardsalesTarget.getString("resCd"));
				cardsalesTarget.put("manualYn"  , "N");
				cardsalesTarget.put("batchPrgCd", "");
				
				actBatchHistUR = sDao.ds01UR("actBatchHistUR", cardsalesTarget);
				
				if(actBatchHistUR != 1) {
					throw new Exception("수집내역 업데이트에 실패하였습니다.");
				}
				
				cardsalesTarget.put("batchSeq", "");
				
			}
			
			/*
			 * desc : 수집되고 있다면 다음 수집까지 진행하지 않는다
			 */
			if(activeCount > 0) {
				return;
			}
			
			/*
			 * desc : 배치명을 생성하고 요청내역을 확인한다. 이때 어플리케이션코드(appCd)를 배치명으로 설정한다.
			 */
			cardsalesTarget.put("currentTarget", "cardsales");
			cardsalesTarget.put("batchNm"      , String.format("kicc.batch.%s.%s", cardsalesTarget.getString("currentTarget"), SCommonU.getDateString("yyyyMMddHHmmss")));
			logger.debug("{}: {}", "currentTarget", cardsalesTarget.getString("currentTarget"));
			reqSL = sDao.ds01SL("reqSL", cardsalesTarget);
			logger.debug("{}: {}", "reqSL.size", reqSL.size());
			
			/*
			 * desc : 요청내역이 없을 경우 다음 수집을 진행하기 위해 현재 진행을 중지한다.
			 */
			if(reqSL == null || reqSL.size() == 0) {
				return;
			}
			
			cardsalesTarget.put("startDtm"  , new Date());
			cardsalesTarget.put("endDtm"    , "");
			cardsalesTarget.put("elapsedTm" , -1);
			cardsalesTarget.put("jobStatus" , "START");
			cardsalesTarget.put("resCd"     , "B001");
			cardsalesTarget.put("manualYn"  , "N");
			cardsalesTarget.put("errMsg"    , "");
			cardsalesTarget.put("batchPrgCd", "");
			
			/*
			 * desc: 배치내용을 등록한다.
			 */
			actBatchHistIR = sDao.ds01IR("actBatchHistIR", cardsalesTarget);
			batchSeq       = cardsalesTarget.getString("batchSeq");
			logger.debug("{}: {}", "actBatchHistIR", actBatchHistIR);
			
			if(actBatchHistIR != 1) {
				throw new Exception("수집내역 등록에 실패하였습니다.");
			}
			
			/*
			 * desc: 요청전문별 비밀번호를 복호화 하고 계정별로 그룹을 생성한다.
			 */
			HashMap<String, ArrayList<SMap>> requestL = new HashMap<String, ArrayList<SMap>>();
			ArrayList<SMap>                  groupL   = null;
			String                           hash     = "";
			for(SMap reqSR : reqSL) {
				
				reqSR.putAll(sDao.ds02SR("keySR", reqSR));
				
				//SHA256(orgCd, signCert, signPri, signPw, userId, userPw, bankCd/cardCd/memGrpId)
				switch(cardsalesTarget.getString("currentTarget")) {
				case "cardsales":
					hash = SCommonU.digestToHexString(
							"SHA-256"
							, reqSR.getString("ORG_CD")
							+ reqSR.getString("USER_ID")
							+ reqSR.getString("USER_PW")
							);
					break;
				default:
					break;
				}// end of account hash
				
				if(requestL.containsKey(hash)) {
					requestL.get(hash).add(reqSR);
				} else {
					groupL = new ArrayList<SMap>();
					groupL.add(reqSR);
					requestL.put(hash, groupL);
				}
				
			}// end of loop reqSL
			
			/*
			 * desc: 그룹별로 수집요청을 진행한다.
			 */
			for(String key : requestL.keySet()) {
				sExtractU.request02(batchSeq, requestL.get(key));
			}
			
			/*
			 * desc: 배치그룹 진행내역을 업데이트 한다.
			 */
			cardsalesTarget.put("errMsg"   , String.format("reqSL: %d, requestL: %d", reqSL.size(), requestL.keySet().size()));
			cardsalesTarget.put("jobStatus", "EXECUTE");
			sDao.ds01UR("actBatchHistUR", cardsalesTarget);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("" + e);
			
			try {
				
				cardsalesTarget.put("resCd" , "B002");
				cardsalesTarget.put("errMsg", cardsalesTarget.getString("errMsg") + e);
				sDao.ds01UR("actBatchHistUR", cardsalesTarget);
				
			} catch (Exception e1) {
				logger.error("" + e);
			}
			
		}
		
	}
	
	@Scheduled(fixedDelay = 60000)
//	@Transactional
	public void hometaxZ0001Request() {
		
		logger.debug("{}.{}", "SExtractSC", "hometaxZ0001Request");
		
		try {
			
			// 쓰레드 카운트 체크
			int activeCount = ts03.getActiveCount();
			logger.debug("{}: {}", "activeCount", activeCount);
			
			if(activeCount > 0) {
				return;
			}
			
			String     target           = "";
			List<SMap> bmcCloseInfoSL   = null;
			List<SMap> bmcCloseInfoSL01 = null;
			List<SMap> bmcCloseInfoSL02 = null;
			List<SMap> bmcCloseInfoSL03 = null;
			int        limit            = 10000;
			int        cnt              = 0;
			
			String days      = configProperties.getProperty("config.hometax.Z0001.day", "");
			String dayOfWeek = "" + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			
			/*
			 * 전체수집
			 */
			if(days.indexOf(dayOfWeek) > -1) {
//				hometaxZ0001VO.put("currentTarget", "hometax.Z0001");
//				bmcCloseInfoSL = sDao.ds01SR("bmcCloseInfoSR01", hometaxZ0001VO);
				target = "hometax.Z0001";
				cnt    = sDao.ds01SR("bmcCloseInfoSR01").getInt("CNT");
			/*
			 * 신규수집
			 */
			} else {
//				hometaxZ0001VO.put("currentTarget", "hometax.Z0001.99");
//				bmcCloseInfoSL = sDao.ds01SR("bmcCloseInfoSR02", hometaxZ0001VO);
				target = "hometax.Z0001.99";
				cnt    = sDao.ds01SR("bmcCloseInfoSR02").getInt("CNT");
			}
			
			/*
			 * 수집 미실행
			 */
			if(cnt == 0) {
				return;
			}
			
			if("hometax.Z0001.99".equals(target)) {
				
				bmcCloseInfoSL = sDao.ds01SL("bmcCloseInfoSL02");
				sExtractU.request03("hometax.Z0001.99", bmcCloseInfoSL01);
				
			} else {
				
				bmcCloseInfoSL   = sDao.ds01SL("bmcCloseInfoSL01");
				bmcCloseInfoSL01 = new ArrayList<SMap>();
				bmcCloseInfoSL02 = new ArrayList<SMap>();
				bmcCloseInfoSL03 = new ArrayList<SMap>();
				for(int i = 0; i < bmcCloseInfoSL.size(); i++) {
					if(i < limit) {
						bmcCloseInfoSL01.add(bmcCloseInfoSL.get(i));
					} else if(i < limit * 2) {
						bmcCloseInfoSL02.add(bmcCloseInfoSL.get(i));
					} else {
						bmcCloseInfoSL03.add(bmcCloseInfoSL.get(i));
					}
				}
				
				if(bmcCloseInfoSL01.size() > 0) {
					sExtractU.request03("hometax.Z0001.01", bmcCloseInfoSL01);
				}
				if(bmcCloseInfoSL02.size() > 0) {
					sExtractU.request03("hometax.Z0001.02", bmcCloseInfoSL02);
				}
				if(bmcCloseInfoSL03.size() > 0) {
					sExtractU.request03("hometax.Z0001.03", bmcCloseInfoSL03);
				}
				
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.error("" + e);
			
		}
		
	}
	
}
