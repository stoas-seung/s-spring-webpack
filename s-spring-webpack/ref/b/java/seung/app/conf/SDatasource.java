package seung.app.conf;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
//@MapperScan(value = "ift.batch.kicc.ds01", sqlSessionFactoryRef = "sqlSessionFactory01")
@EnableTransactionManagement
public class SDatasource {

	private static final Logger logger = LoggerFactory.getLogger(SDatasource.class);
	
	@Primary
	@Bean(name="dataSource01",destroyMethod="close")
	@ConfigurationProperties(prefix="datasource.ds01")
	public DataSource dataSource01() {
		logger.debug("{}.{}", "SDatasource", "dataSource01");
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build()
			;
	}
	
	@Primary
	@Bean(name = "sqlSessionFactory01")
	public SqlSessionFactory sqlSessionFactory01(
			@Qualifier("dataSource01") DataSource dataSource01
			, ApplicationContext applicationContext
			) throws Exception {
		logger.debug("{}.{}", "SDatasource", "sqlSessionFactory01");
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource01);
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setCallSettersOnNulls(true);
		configuration.setJdbcTypeForNull(JdbcType.VARCHAR);
		sqlSessionFactoryBean.setConfiguration(configuration);
//		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis-conf.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:sql/ds01/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}
	
	@Primary
	@Bean(name = "sqlSessionTemplate01")
	public SqlSessionTemplate sqlSessionTemplate01(
			@Qualifier("sqlSessionFactory01") SqlSessionFactory sqlSessionFactory01
			) throws Exception {
		logger.debug("{}.{}", "SDatasource", "sqlSessionTemplate01");
		logger.debug("sqlSessionFactory01.mappedStatementNames: {}", sqlSessionFactory01.getConfiguration().getMappedStatementNames().toString());
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory01);
		return sqlSessionTemplate;
	}
	
	@Bean(name="dataSource02",destroyMethod="close")
	@ConfigurationProperties(prefix="datasource.ds02")
	public DataSource dataSource02() {
		logger.debug("{}.{}", "SDatasource", "dataSource02");
		return DataSourceBuilder.create()
			.type(HikariDataSource.class)
			.build()
			;
	}
	
	@Bean(name = "sqlSessionFactory02")
	public SqlSessionFactory sqlSessionFactory02(
			@Qualifier("dataSource02") DataSource dataSource02
			, ApplicationContext applicationContext
			) throws Exception {
		logger.debug("{}.{}", "SDatasource", "sqlSessionFactory02");
		SqlSessionFactoryBean sqlSessionFactoryBean02 = new SqlSessionFactoryBean();
		sqlSessionFactoryBean02.setDataSource(dataSource02);
		org.apache.ibatis.session.Configuration configuration02 = new org.apache.ibatis.session.Configuration();
		configuration02.setCallSettersOnNulls(true);
		configuration02.setJdbcTypeForNull(JdbcType.VARCHAR);
		sqlSessionFactoryBean02.setConfiguration(configuration02);
//		sqlSessionFactoryBean02.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
		sqlSessionFactoryBean02.setMapperLocations(applicationContext.getResources("classpath:sql/ds02/*.xml"));
		return sqlSessionFactoryBean02.getObject();
	}
	
	@Bean(name = "sqlSessionTemplate02")
	public SqlSessionTemplate sqlSessionTemplate02(
			@Qualifier("sqlSessionFactory02") SqlSessionFactory sqlSessionFactory02
			) throws Exception {
		logger.debug("{}.{}", "SDatasource", "sqlSessionTemplate02");
		logger.debug("sqlSessionFactory02.mappedStatementNames: {}", sqlSessionFactory02.getConfiguration().getMappedStatementNames().toString());
		SqlSessionTemplate sqlSessionTemplate02 = new SqlSessionTemplate(sqlSessionFactory02);
		return sqlSessionTemplate02;
	}
	
}
