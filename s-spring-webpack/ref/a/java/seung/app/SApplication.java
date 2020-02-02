package seung.app;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SApplication {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(SApplication.class)
			.web(WebApplicationType.SERVLET)
//			.web(WebApplicationType.NONE)
//			.web(WebApplicationType.REACTIVE)
			.run(args)
			;
	}
	
}
