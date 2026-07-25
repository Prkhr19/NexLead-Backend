package com.NexLead.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adoreDezignOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Adore Dezign CRM API")
                        .description("Business Management Platform backend for Adore Dezign")
                        .version("v1")
                        .contact(new Contact()
                                .name("Adore Dezign")
                                .email("contact@adoredezign.com")));
    }
}
