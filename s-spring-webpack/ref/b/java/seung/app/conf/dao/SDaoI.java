package seung.app.conf.dao;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import seung.commons.arguments.SMap;

@Repository("sDao")
public class SDaoI implements SDao {
	
	private static final Logger logger = LoggerFactory.getLogger(SDaoI.class);
	
	@Resource(name="sqlSessionTemplate01")
	private SqlSession sqlSessionTemplate01;
	
	@Resource(name="sqlSessionTemplate02")
	private SqlSession sqlSessionTemplate02;
	
	@Override
	public List<SMap> ds01SL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.selectList(statement);
	}
	
	@Override
	public List<SMap> ds01SL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.selectList(statement, value);
	}
	
	@Override
	public List<SMap> ds01SL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.selectList(statement, sMap);
	}
	
	@Override
	public SMap ds01SR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.selectOne(statement);
	}
	
	@Override
	public SMap ds01SR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.selectOne(statement, value);
	}
	
	@Override
	public SMap ds01SR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.selectOne(statement, sMap);
	}
	
	@Override
	public int ds01IR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.insert(statement);
	}
	
	@Override
	public int ds01IR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.insert(statement, value);
	}
	
	@Override
	public int ds01IR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.insert(statement, sMap);
	}
	
	@Override
	public int ds01IL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.insert(statement);
	}
	
	@Override
	public int ds01IL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.insert(statement, value);
	}
	
	@Override
	public int ds01IL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.insert(statement, sMap);
	}
	
	@Override
	public int ds01UR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.update(statement);
	}
	
	@Override
	public int ds01UR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.update(statement, value);
	}
	
	@Override
	public int ds01UR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.update(statement, sMap);
	}
	
	@Override
	public int ds01UL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.update(statement);
	}
	
	@Override
	public int ds01UL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.update(statement, value);
	}
	
	@Override
	public int ds01UL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.update(statement, sMap);
	}
	
	@Override
	public int ds01DR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.delete(statement);
	}
	
	@Override
	public int ds01DR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.delete(statement, value);
	}
	
	@Override
	public int ds01DR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.delete(statement, sMap);
	}
	
	@Override
	public int ds01DL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate01.delete(statement);
	}
	
	@Override
	public int ds01DL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate01.delete(statement, value);
	}
	
	@Override
	public int ds01DL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate01.delete(statement, sMap);
	}
	
	@Override
	public List<SMap> ds02SL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.selectList(statement);
	}
	
	@Override
	public List<SMap> ds02SL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.selectList(statement, value);
	}
	
	@Override
	public List<SMap> ds02SL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.selectList(statement, sMap);
	}
	
	@Override
	public SMap ds02SR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.selectOne(statement);
	}
	
	@Override
	public SMap ds02SR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.selectOne(statement, value);
	}
	
	@Override
	public SMap ds02SR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.selectOne(statement, sMap);
	}
	
	@Override
	public int ds02IR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.insert(statement);
	}
	
	@Override
	public int ds02IR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.insert(statement, value);
	}
	
	@Override
	public int ds02IR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.insert(statement, sMap);
	}
	
	@Override
	public int ds02IL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.insert(statement);
	}
	
	@Override
	public int ds02IL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.insert(statement, value);
	}
	
	@Override
	public int ds02IL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.insert(statement, sMap);
	}
	
	@Override
	public int ds02UR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.update(statement);
	}
	
	@Override
	public int ds02UR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.update(statement, value);
	}
	
	@Override
	public int ds02UR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.update(statement, sMap);
	}
	
	@Override
	public int ds02UL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.update(statement);
	}
	
	@Override
	public int ds02UL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.update(statement, value);
	}
	
	@Override
	public int ds02UL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.update(statement, sMap);
	}
	
	@Override
	public int ds02DR(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.delete(statement);
	}
	
	@Override
	public int ds02DR(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.delete(statement, value);
	}
	
	@Override
	public int ds02DR(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.delete(statement, sMap);
	}
	
	@Override
	public int ds02DL(String statement) throws Exception {
		logger.debug("[{}] - {}", statement, "");
		return sqlSessionTemplate02.delete(statement);
	}
	
	@Override
	public int ds02DL(String statement, String value) throws Exception {
		logger.debug("[{}] - {}", statement, value);
		return sqlSessionTemplate02.delete(statement, value);
	}
	
	@Override
	public int ds02DL(String statement, SMap sMap) throws Exception {
		logger.debug("[{}] - {}", statement, sMap.toJsonString(true));
		return sqlSessionTemplate02.delete(statement, sMap);
	}
	
}
