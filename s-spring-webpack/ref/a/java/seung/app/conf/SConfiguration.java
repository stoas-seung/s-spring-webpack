package seung.app.conf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.MustacheEnvironmentCollector;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.TemplateLoader;

import seung.app.conf.arguments.SHandlerMethodArgumentResolver;
import seung.app.conf.views.SMappingJackson2JsonView;

@Configuration
@ComponentScan({"seung","ift"})
//@PropertySources({
//	@PropertySource(value="classpath:application.properties")
//	, @PropertySource(value="classpath:ift-bridge.properties")
//	, @PropertySource(value="file:${config.work.path}/ift-bridge.properties",ignoreResourceNotFound=true)
//})
public class SConfiguration extends WebMvcConfigurationSupport {

	private static final Logger logger = LoggerFactory.getLogger(SConfiguration.class);
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	@Bean(name = "configProperties")
	public Properties addConfigProperties() {
		
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addConfigProperties");
		
		Properties properties = Binder.get(environment).bind("", Bindable.of(Properties.class)).get();
		try {
			properties.setProperty("config.hostname", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error("" + e);
		}
		
		return properties;
	}
	
	/**
	 * desc add SReqMap mapping
	 */
	@Override
	protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addArgumentResolvers");
		super.addArgumentResolvers(argumentResolvers);
		argumentResolvers.add(new SHandlerMethodArgumentResolver());
	}
	
	/**
	 * resource handler
	 */
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addResourceHandlers");
		if(!registry.hasMappingForPattern("/res/**")) {
			registry.addResourceHandler("/res/**").addResourceLocations("classpath:/static/res/");
		}
	}
	
	/**
	 * desc bean name resolver
	 */
	@Bean
	public ViewResolver addBeanNameViewResolver() {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addBeanNameViewResolver");
		BeanNameViewResolver beanNameViewResolver = new BeanNameViewResolver();
		beanNameViewResolver.setOrder(1);
		return beanNameViewResolver;
	}
	
	/**
	 * json view resolver
	 */
	@Bean(name="jsonView")
	public MappingJackson2JsonView addSMappingJackson2JsonView() {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addSMappingJackson2JsonView");
		SMappingJackson2JsonView sMappingJackson2JsonView = new SMappingJackson2JsonView();
		sMappingJackson2JsonView.addNotWrappedModelKeys("res");
		return sMappingJackson2JsonView;
	}
	
	/**
	 * mustache view resolver
	 */
	@Bean
	public ViewResolver addMustacheViewResolver() {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addMustacheViewResolver");
		MustacheViewResolver mustacheViewResolver = new MustacheViewResolver();
		mustacheViewResolver.setPrefix("classpath:/templates/views");
		mustacheViewResolver.setSuffix(".html");
		mustacheViewResolver.setCharset("UTF-8");
		mustacheViewResolver.setContentType("text/html; charset=utf-8");
		mustacheViewResolver.setCache(false);
		mustacheViewResolver.setOrder(2);
		return mustacheViewResolver;
	}
	
	/**
	 * set mustache default null value
	 */
	@Bean
	public com.samskivert.mustache.Mustache.Compiler addMustacheCompiler(TemplateLoader templateLoader, Environment environment) {
		logger.debug("{}.{}", "WebMvcConfigurationSupport", "addMustacheCompiler");
		MustacheEnvironmentCollector mustacheEnvironmentCollector = new MustacheEnvironmentCollector();
		mustacheEnvironmentCollector.setEnvironment(environment);
		com.samskivert.mustache.Mustache.Compiler compiler = Mustache.compiler().defaultValue("").withLoader(templateLoader).withCollector(mustacheEnvironmentCollector);
		return compiler;
	}
	
	/**
	 * redis
	 */
	@Bean(name="redisTemplate")
	public RedisTemplate<String, Object> addRedisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setDefaultSerializer(new StringRedisSerializer());
		redisTemplate.setConnectionFactory(redisConnectionFactory);
//		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}
	
}
