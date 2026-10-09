package com.mobiles.mobil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MobilApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobilApplication.class, args);
	}

}
