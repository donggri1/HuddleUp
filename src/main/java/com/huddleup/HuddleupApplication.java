package com.huddleup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HuddleupApplication {

	public static void main(String[] args) {
		SpringApplication.run(HuddleupApplication.class, args);
		System.out.println(new BCryptPasswordEncoder().encode("password"));
		
	}

}
