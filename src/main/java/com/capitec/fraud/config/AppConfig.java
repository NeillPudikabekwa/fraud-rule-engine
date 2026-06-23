package com.capitec.fraud.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FraudRuleProperties.class)
public class AppConfig {}
