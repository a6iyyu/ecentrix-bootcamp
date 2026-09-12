package com.bootcamp.rafi_day_2.service;

import com.bootcamp.rafi_day_2.dto.products.*;
import com.bootcamp.rafi_day_2.entity.*;
import com.bootcamp.rafi_day_2.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class responsible for handling business logic related to products.
 * This class acts as a bridge between the controller and the product data repository.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private ProductResponse mapToResponse(Products product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .build();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Starting to create product with name: {}", productRequest.getName());

        Categories category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found."));

        Products product = new Products();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());

        category.addProduct(product);

        Products savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());

        return mapToResponse(savedProduct);
    }

    public Page<ProductResponse> getAllProducts(Long categoryId, Pageable pageable) {
        log.info("Fetching paginated products. Category filter: {}, Page: {}, Size: {}", categoryId, pageable.getPageNumber(), pageable.getPageSize());

        Page<Products> productsPage = (categoryId != null)
                ? productRepository.findByCategoryId(categoryId, pageable)
                : productRepository.findAll(pageable);

        return productsPage.map(this::mapToResponse);
    }

    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);

        Products product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("Fetch failed: Product with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        });

        log.info("Product found: {}", product.getName());
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Starting update process for product ID: {}", id);

        Products product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("Update failed: Product with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        });

        Categories category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> {
            log.warn("Update failed: Category with ID {} not found.", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found.");
        });

        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());

        category.addProduct(product);

        Products updatedProduct = productRepository.save(product);
        log.info("Product with ID: {} updated successfully.", updatedProduct.getId());

        return mapToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        log.info("Attempting to delete product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            log.warn("Delete failed: Product with ID {} does not exist!", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }

        productRepository.deleteById(id);
        log.info("Product with ID: {} deleted successfully.", id);
    }
}