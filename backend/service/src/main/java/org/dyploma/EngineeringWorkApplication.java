package org.dyploma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EngineeringWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngineeringWorkApplication.class, args);
	}

}