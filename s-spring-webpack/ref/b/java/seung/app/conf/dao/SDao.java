package seung.app.conf.dao;

import java.util.List;

import seung.commons.arguments.SMap;

public interface SDao {

	/**
	 * @desc select list
	 */
	List<SMap> ds01SL(String statement) throws Exception;
	List<SMap> ds01SL(String statement, String value) throws Exception;
	List<SMap> ds01SL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc select record
	 */
	SMap ds01SR(String statement) throws Exception;
	SMap ds01SR(String statement, String value) throws Exception;
	SMap ds01SR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc insert record
	 */
	int ds01IR(String statement) throws Exception;
	int ds01IR(String statement, String value) throws Exception;
	int ds01IR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc insert list
	 */
	int ds01IL(String statement) throws Exception;
	int ds01IL(String statement, String value) throws Exception;
	int ds01IL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc update record
	 */
	int ds01UR(String statement) throws Exception;
	int ds01UR(String statement, String value) throws Exception;
	int ds01UR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc update list
	 */
	int ds01UL(String statement) throws Exception;
	int ds01UL(String statement, String value) throws Exception;
	int ds01UL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc delete record
	 */
	int ds01DR(String statement) throws Exception;
	int ds01DR(String statement, String value) throws Exception;
	int ds01DR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc delete list
	 */
	int ds01DL(String statement) throws Exception;
	int ds01DL(String statement, String value) throws Exception;
	int ds01DL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc select list
	 */
	List<SMap> ds02SL(String statement) throws Exception;
	List<SMap> ds02SL(String statement, String value) throws Exception;
	List<SMap> ds02SL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc select record
	 */
	SMap ds02SR(String statement) throws Exception;
	SMap ds02SR(String statement, String value) throws Exception;
	SMap ds02SR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc insert record
	 */
	int ds02IR(String statement) throws Exception;
	int ds02IR(String statement, String value) throws Exception;
	int ds02IR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc insert list
	 */
	int ds02IL(String statement) throws Exception;
	int ds02IL(String statement, String value) throws Exception;
	int ds02IL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc update record
	 */
	int ds02UR(String statement) throws Exception;
	int ds02UR(String statement, String value) throws Exception;
	int ds02UR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc update list
	 */
	int ds02UL(String statement) throws Exception;
	int ds02UL(String statement, String value) throws Exception;
	int ds02UL(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc delete record
	 */
	int ds02DR(String statement) throws Exception;
	int ds02DR(String statement, String value) throws Exception;
	int ds02DR(String statement, SMap sMap) throws Exception;
	
	/**
	 * @desc delete list
	 */
	int ds02DL(String statement) throws Exception;
	int ds02DL(String statement, String value) throws Exception;
	int ds02DL(String statement, SMap sMap) throws Exception;
	
}
