package com.example.accessingdatamysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.TimeZone;

@SpringBootApplication
public class AccessingDataMysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataMysqlApplication.class, args);
	}

	@Autowired
	public void configureJackson(ObjectMapper objectMapper) {
		objectMapper.setTimeZone(TimeZone.getDefault());
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

}