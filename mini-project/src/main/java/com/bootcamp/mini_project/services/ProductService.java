package com.bootcamp.mini_project.services;

import com.bootcamp.mini_project.dto.common.PageResponse;
import com.bootcamp.mini_project.dto.products.*;
import com.bootcamp.mini_project.entity.*;
import com.bootcamp.mini_project.exceptions.custom.ResourceNotFoundException;
import com.bootcamp.mini_project.mappers.ProductMapper;
import com.bootcamp.mini_project.repositories.*;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles business logic, entity relationship validations, and caching operations for inventory products.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final CacheManager cacheManager;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    /**
     * Retrieves a paginated list of all products mapped to response DTOs.
     *
     * @param pageable pagination configuration
     * @return a {@link PageResponse} wrapping {@link ProductResponse} items
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(Pageable pageable) {
        log.debug("[PRODUCT] Fetching paginated products page: {}", pageable.getPageNumber());
        return PageResponse.from(productRepository.findAll(pageable).map(productMapper::toResponse));
    }

    /**
     * Retrieves a specific product by ID and caches the result in Redis.
     *
     * @param id the unique product identifier
     * @return the corresponding {@link ProductResponse} DTO
     * @throws ResourceNotFoundException if target product is not found
     */
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("[CACHE MISS] Fetching product from database for ID: {}", id);

        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[PRODUCT NOT FOUND] Read operation failed. Product ID {} does not exist.", id);
            return new ResourceNotFoundException("Product not found with ID: " + id);
        });

        return productMapper.toResponse(product);
    }

    /**
     * Retrieves a specific product by name and caches the result in Redis.
     *
     * @param name the product name
     * @return the corresponding {@link ProductResponse} DTO
     * @throws ResourceNotFoundException if target product is not found
     */
    @Cacheable(value = "products", key = "'name:' + #name")
    @Transactional(readOnly = true)
    public ProductResponse getProductByName(String name) {
        log.info("[CACHE MISS] Fetching product from database for name: {}", name);

        Product product = productRepository.findByName(name).orElseThrow(() -> {
            log.warn("[PRODUCT NOT FOUND] Read operation failed. Product name '{}' does not exist.", name);
            return new ResourceNotFoundException("Product not found with name: " + name);
        });

        return productMapper.toResponse(product);
    }

    /**
     * Creates a new product record after validating category and supplier cross-entity references.
     *
     * @param request payload containing product creation details
     * @return the created {@link ProductResponse} DTO
     * @throws IllegalArgumentException  if product name already exists
     * @throws ResourceNotFoundException if referenced category or supplier is not found
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            log.warn("[PRODUCT DUPLICATE] Creation failed. Name '{}' already exists.", request.getName());
            throw new IllegalArgumentException("Product name already exists: " + request.getName());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> {
            log.warn("[CATEGORY NOT FOUND] Product creation failed. Category ID {} does not exist.", request.getCategoryId());
            return new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId());
        });

        Supplier supplier = null;

        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> {
                log.warn("[SUPPLIER NOT FOUND] Product creation failed. Supplier ID {} does not exist.", request.getSupplierId());
                return new ResourceNotFoundException("Supplier not found with ID: " + request.getSupplierId());
            });
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSupplier(supplier);

        Product savedProduct = productRepository.save(product);
        log.info("[PRODUCT] Successfully created product with ID: {}", savedProduct.getId());

        return productMapper.toResponse(savedProduct);
    }

    /**
     * Updates an existing product record, re-validating relationships and clearing cache entries.
     *
     * @param id      the unique product identifier
     * @param request payload containing updated product details
     * @return the updated {@link ProductResponse} DTO
     * @throws ResourceNotFoundException if product, category, or supplier is not found
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[PRODUCT NOT FOUND] Update operation failed. Product ID {} does not exist.", id);
            return new ResourceNotFoundException("Product not found with ID: " + id);
        });

        String oldName = product.getName();

        if (!oldName.equalsIgnoreCase(request.getName()) && productRepository.existsByName(request.getName())) {
            log.warn("[PRODUCT DUPLICATE] Update failed. Name '{}' already exists.", request.getName());
            throw new IllegalArgumentException("Product name already exists: " + request.getName());
        }

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> {
            log.warn("[CATEGORY NOT FOUND] Product update failed. Category ID {} does not exist.", request.getCategoryId());
            return new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId());
        });

        Supplier supplier = null;

        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElseThrow(() -> {
                log.warn("[SUPPLIER NOT FOUND] Product update failed. Supplier ID {} does not exist.", request.getSupplierId());
                return new ResourceNotFoundException("Supplier not found with ID: " + request.getSupplierId());
            });
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setSupplier(supplier);

        Product updatedProduct = productRepository.save(product);
        evictProductCache(id, oldName, updatedProduct.getName());
        log.info("[PRODUCT] Updated product ID: {} and evicted cache entries.", id);

        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Soft-deletes a product by ID and removes its Redis cache entries.
     *
     * @param id the unique product identifier
     * @throws ResourceNotFoundException if product is not found
     */
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.warn("[PRODUCT NOT FOUND] Delete operation failed. Product ID {} does not exist.", id);
            return new ResourceNotFoundException("Product not found with ID: " + id);
        });

        evictProductCache(product.getId(), product.getName());
        productRepository.delete(product);
        log.info("[PRODUCT] Soft-deleted product ID: {} and evicted cache entries.", id);
    }

    /**
     * Helper method to programmatically evict product cache entries without nested IF blocks.
     *
     * @param id    product ID key to evict
     * @param names dynamic array of product name keys to evict
     */
    private void evictProductCache(Long id, String... names) {
        Cache cache = cacheManager.getCache("products");
        if (cache == null) {
            return;
        }

        if (id != null) {
            cache.evict(id);
        }

        Stream.of(names)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(name -> cache.evict("name:" + name));
    }
}