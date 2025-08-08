package com.example.libraryService.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI libraryOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Library Service API")
                        .description("This API handles library books, integrated with Kafka, Email, and Inventory.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Library API Team")
                                .email("support@library.com")
                                .url("https://library.example.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Library Service Wiki Documentation")
                        .url("https://library-docs.example.com"));
    }
}
