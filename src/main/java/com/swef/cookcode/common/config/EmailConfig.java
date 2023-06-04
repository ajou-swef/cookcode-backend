package com.swef.cookcode.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Getter
@Setter
public class EmailConfig {
    private String host;

    private Long port;

    private String username;

    private String password;
}
