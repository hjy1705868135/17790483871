package com.md.basePlatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.md.basePlatform.repository")
public class BasePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasePlatformApplication.class, args);
	}

}
