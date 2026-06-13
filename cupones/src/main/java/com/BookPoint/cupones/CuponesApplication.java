package com.BookPoint.cupones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = org.springdoc.core.configuration.SpringDocHateoasConfiguration.class)
public class CuponesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CuponesApplication.class, args);
	}

}
