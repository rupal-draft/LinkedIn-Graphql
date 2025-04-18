package com.linkedIn.LinkedIn.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LinkedInAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkedInAppApplication.class, args);
	}

}
