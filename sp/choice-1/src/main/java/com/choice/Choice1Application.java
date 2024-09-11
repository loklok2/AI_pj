package com.choice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Choice1Application {

	public static void main(String[] args) {
		SpringApplication.run(Choice1Application.class, args);
	}

}
