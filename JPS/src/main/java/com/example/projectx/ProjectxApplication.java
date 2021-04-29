package com.example.projectx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class ProjectxApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER=LoggerFactory.getLogger(ProjectxApplication.class);
	
	public static void main(String[] args) {
		LOGGER.info("********** Starting Application ***************");
		SpringApplication.run(ProjectxApplication.class, args);
		LOGGER.info("********** Application started ***************");
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ProjectxApplication.class);
    }
	
	@Bean
	  public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(2);
	    executor.setMaxPoolSize(2);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("EmailService-");
	    executor.initialize();
	    return executor;
	  }

}
