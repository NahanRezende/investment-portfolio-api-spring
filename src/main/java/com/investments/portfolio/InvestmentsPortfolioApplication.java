package com.investments.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.investments.portfolio")
public class InvestmentsPortfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestmentsPortfolioApplication.class, args);
	}

}
