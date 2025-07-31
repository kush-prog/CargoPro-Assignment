package com.kush.cargoProAssignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.kush.cargoProAssignment")
public class LoadBookingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoadBookingSystemApplication.class, args);
	}

}
