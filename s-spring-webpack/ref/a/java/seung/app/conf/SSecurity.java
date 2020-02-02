package seung.app.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment environment;
	
	@Override
	public void configure(WebSecurity webSecurity) throws Exception {
		webSecurity.ignoring().antMatchers("/res/**");
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		String       systemIp  = environment.getProperty("config.system.ip", "0:0:0:0:0:0:0:1,127.0.0.1");
		StringBuffer systemIps = new StringBuffer();
		String       serviceIp = environment.getProperty("config.service.ip", "");
		StringBuffer serviceIps = new StringBuffer();
		
		if(!"".equals(systemIp)) {
			for(String ip : systemIp.split(",")) {
				if(systemIps.length() > 0) {
					systemIps.append(" or ");
				}
				systemIps.append(String.format("hasIpAddress('%s')", ip.trim()));
			}
		}
		
		if(!"".equals(serviceIp)) {
			for(String ip : serviceIp.split(",")) {
				if(serviceIps.length() > 0) {
					serviceIps.append(" or ");
				}
				serviceIps.append(String.format("hasIpAddress('%s')", ip.trim()));
			}
		}
		
		if(serviceIps.length() > 0) {
			httpSecurity.authorizeRequests()
				.antMatchers("/rest/redis/workerUR").permitAll()
				.antMatchers("/reflect/**").permitAll()
				.antMatchers("/view/**").permitAll()
				.antMatchers("/system/**").access(systemIps.toString())
				.antMatchers("/test/**").access(systemIps.toString())
				.antMatchers("/rest/**").access(serviceIps.toString())
				.anyRequest().authenticated()
				;
		} else {
			httpSecurity.authorizeRequests()
				.antMatchers("/rest/redis/workerUR").permitAll()
				.antMatchers("/reflect/**").permitAll()
				.antMatchers("/view/**").permitAll()
				.antMatchers("/system/**").access(systemIps.toString())
				.antMatchers("/test/**").access(systemIps.toString())
				.antMatchers("/rest/**").permitAll()
				.anyRequest().authenticated()
				;
		}
		
//		httpSecurity.cors().disable();
		httpSecurity.csrf().disable();
	}
	
}
