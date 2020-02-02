package seung.app.conf;

import java.util.Properties;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import seung.commons.arguments.SMap;

@Configuration
@ComponentScan({"seung","ift"})
@EnableScheduling
@EnableAsync
@PropertySources({
	@PropertySource(value="classpath:application.properties")
	, @PropertySource(value="${config.datasource.path}",ignoreResourceNotFound=true)
})
public class SConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SConfiguration.class);
	
	@Autowired
	private Environment environment;
	
	@Bean(name = "configProperties")
	public Properties addConfigProperties() {
		
		logger.debug("{}.{}", "SConfiguration", "configProperties");
		
		Properties properties = Binder.get(environment).bind("", Bindable.of(Properties.class)).get();
		
		return properties;
	}
	
	@Bean(name = "requestTarget")
	public SMap addRequestTarget() {
		
		SMap batchTarget = new SMap();
		
		String[] order = {
			"hometax",
			"pbk",
			"cbk",
			"sbk",
			"pcd",
			"ccd"
			}
			;
		
		for(int i = 0; i < order.length; i++) {
			if(i == order.length - 1) {
				batchTarget.put(order[i], order[0]);
			} else {
				batchTarget.put(order[i], order[i + 1]);
			}
		}
		
		batchTarget.put("currentTarget", order[order.length - 1]);
		
		return batchTarget;
	}
	
	@Bean(name = "cardsalesTarget")
	public SMap addCardsalesTarget() {
		return new SMap();
	}
	
	@Bean(name = "iftCode")
	public SMap addIftCode() {
		return new SMap();
	}
	
	@Bean(name = "ts01", destroyMethod = "shutdown")
	public Executor addTaskScheduler01() {
		
		logger.debug("{}.{}", "SConfiguration", "addTaskScheduler01");
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(Integer.parseInt(System.getProperty("spring.extract.pool.max-active", "10")));
		taskExecutor.setMaxPoolSize(Integer.parseInt(System.getProperty("spring.extract.pool.max-active", "10")));
		taskExecutor.setThreadNamePrefix("ts01-");
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	@Bean(name = "ts02", destroyMethod = "shutdown")
	public Executor addTaskScheduler02() {
		
		logger.debug("{}.{}", "SConfiguration", "addTaskScheduler02");
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(Integer.parseInt(System.getProperty("spring.extract.cardsales.pool.max-active", "10")));
		taskExecutor.setMaxPoolSize(Integer.parseInt(System.getProperty("spring.extract.cardsales.pool.max-active", "10")));
//		taskExecutor.setMaxPoolSize(Integer.parseInt(System.getProperty("spring.extract.pool.max-active", "10")));
		taskExecutor.setThreadNamePrefix("ts02-");
		taskExecutor.initialize();
		return taskExecutor;
	}
	
	@Bean(name = "ts03", destroyMethod = "shutdown")
	public Executor addTaskScheduler03() {
		
		logger.debug("{}.{}", "SConfiguration", "addTaskScheduler02");
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(3);
		taskExecutor.setThreadNamePrefix("ts03-");
		taskExecutor.initialize();
		return taskExecutor;
	}
	
}
