package wsb.bugtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WsbBugtracker {

	public static void main(String[] args) {
		SpringApplication.run(WsbBugtracker.class, args);
	}

}
