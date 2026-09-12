package com.bootcamp.mini_project.configuration;

import com.bootcamp.mini_project.controllers.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.*;

/**
 * Configures OpenAPI metadata and API groupings for Swagger UI documentation.
 */
@Configuration
public class OpenApiConfig {
    /**
     * Provides global metadata information for the application API.
     *
     * @return an {@link OpenAPI} instance containing global API specifications
     */
    @Bean
    public OpenAPI baseMetadataOpenAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Inventory & Transaction API")
                        .description("REST API documentation for Bootcamp Mini Project")
                        .version("v1.0.0")
        );
    }

    /**
     * Configures API documentation grouping for Authentication endpoints.
     *
     * @return a {@link GroupedOpenApi} instance for Authentication
     */
    @Bean
    public GroupedOpenApi authGroupApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/api/v1/auth/**")
                .packagesToScan(AuthController.class.getPackageName())
                .build();
    }

    /**
     * Configures API documentation grouping for Category endpoints.
     *
     * @return a {@link GroupedOpenApi} instance for Categories
     */
    @Bean
    public GroupedOpenApi categoryGroupApi() {
        return GroupedOpenApi.builder()
                .group("Categories")
                .pathsToMatch("/api/v1/categories/**")
                .packagesToScan(CategoryController.class.getPackageName())
                .build();
    }

    /**
     * Configures API documentation grouping for Product endpoints.
     *
     * @return a {@link GroupedOpenApi} instance for Products
     */
    @Bean
    public GroupedOpenApi productGroupApi() {
        return GroupedOpenApi.builder()
                .group("Products")
                .pathsToMatch("/api/v1/products/**")
                .packagesToScan(ProductController.class.getPackageName())
                .build();
    }

    @Bean
    public GroupedOpenApi reportGroupApi() {
        return GroupedOpenApi.builder()
                .group("Reports")
                .pathsToMatch("/api/v1/reports/**")
                .packagesToScan(ReportController.class.getPackageName())
                .build();
    }

    /**
     * Configures API documentation grouping for Supplier endpoints.
     *
     * @return a {@link GroupedOpenApi} instance for Suppliers
     */
    @Bean
    public GroupedOpenApi supplierGroupApi() {
        return GroupedOpenApi.builder()
                .group("Suppliers")
                .pathsToMatch("/api/v1/suppliers/**")
                .packagesToScan(SupplierController.class.getPackageName())
                .build();
    }

    /**
     * Configures API documentation grouping for Transaction endpoints.
     *
     * @return a {@link GroupedOpenApi} instance for Transactions
     */
    @Bean
    public GroupedOpenApi transactionGroupApi() {
        return GroupedOpenApi.builder()
                .group("Transactions")
                .pathsToMatch("/api/v1/transactions/**")
                .packagesToScan(TransactionController.class.getPackageName())
                .build();
    }
}