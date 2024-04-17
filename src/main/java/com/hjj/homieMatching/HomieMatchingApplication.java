package com.hjj.homieMatching;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.hjj.homieMatching.mapper")//扫描mapper包下的文件
@EnableScheduling
public class HomieMatchingApplication {
	public static void main(String[] args) {
		SpringApplication.run(HomieMatchingApplication.class, args);
	}

}
