package com.swef.cookcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class CookcodeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookcodeBackendApplication.class, args);
    }

}
