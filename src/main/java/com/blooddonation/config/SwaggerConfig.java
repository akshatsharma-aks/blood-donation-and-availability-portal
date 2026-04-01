package com.blooddonation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(new Info()
                        .title("Blood Donation Portal API")
                        .version("1.0.0")
                        .description("REST API for Blood Donation & Availability Portal")
                        .contact(new Contact()
                                .name("Blood Donation Team")
                                .email("admin@blooddonation.com")
                        )
                )

                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Auth")
                )

                .components(
                        new Components().addSecuritySchemes(
                                "Bearer Auth",
                                new SecurityScheme()
                                        .name("Bearer Auth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}