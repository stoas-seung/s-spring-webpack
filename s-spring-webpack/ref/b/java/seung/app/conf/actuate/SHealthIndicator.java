package seung.app.conf.actuate;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class SHealthIndicator implements HealthIndicator {

	@Override
	public Health health() {
		return Health.up()
			.withDetail("customInfo", "your message.")
			.build()
			;
	}
	
}
