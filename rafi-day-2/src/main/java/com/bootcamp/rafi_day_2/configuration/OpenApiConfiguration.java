package com.bootcamp.rafi_day_2.configuration;

import com.bootcamp.rafi_day_2.controller.CategoryController;
import com.bootcamp.rafi_day_2.controller.ProductController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.*;

/**
 * Configures the global API metadata and defines the API groupings
 * for the Spring Boot application using Springdoc OpenAPI.
 */
@Configuration
public class OpenApiConfiguration {
    /**
     * Configures the global OpenAPI metadata for the application.
     * Sets up the API title, description, and version that will appear at the top of the Swagger UI.
     *
     * @return an {@link OpenAPI} instance containing the global API metadata
     */
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("API Documentation Demo Test")
                        .description("API Documentation Demo Test")
                        .version("v1.0.0")
        );
    }

    /**
     * Defines a specific API group named "API A".
     * This group scans and includes all endpoints located within the controller package,
     * matching any URL paths under the root directory.
     *
     * @return a {@link GroupedOpenApi} instance configured for the specified package and paths
     */
    @Bean
    public GroupedOpenApi apiGroupA() {
        return GroupedOpenApi
                .builder()
                .group("API A")
                .pathsToMatch("/**")
                .packagesToScan(CategoryController.class.getPackageName(), ProductController.class.getPackageName())
                .build();
    }
}