package com.datasynops.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan()
public class JobMgtApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobMgtApplication.class, args);
	}

}
