package com.hjj.homieMatching.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<HttpSessionFilter> loggingFilter(){
        FilterRegistrationBean<HttpSessionFilter> registrationBean 
          = new FilterRegistrationBean<>();

        registrationBean.setFilter(new HttpSessionFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;    
    }
}
