package com.codecompiler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codecompiler.helper.ExcelPOIHelper;


@Configuration
public class MvcConfig {

    @Bean
    public ExcelPOIHelper excelPOIHelper() {
        return new ExcelPOIHelper();
    }
}
