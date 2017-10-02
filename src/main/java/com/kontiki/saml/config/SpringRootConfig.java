package com.kontiki.saml.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.kontiki.saml.dao" })
public class SpringRootConfig {
}