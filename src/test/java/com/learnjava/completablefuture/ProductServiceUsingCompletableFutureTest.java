package com.learnjava.completablefuture;

import com.learnjava.domain.Product;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceUsingCompletableFutureTest {

    ProductInfoService productInfoService = new ProductInfoService();
    ReviewService reviewService = new ReviewService();
    InventoryService inventoryService = new InventoryService();
    ProductServiceUsingCompletableFuture productServiceUsingCompletableFuture =
            new ProductServiceUsingCompletableFuture(productInfoService, reviewService, inventoryService);

    @Test
    void retrieveProductDetails() {
       Product product =
               productServiceUsingCompletableFuture.retrieveProductDetails("ABC123");
       assertNotNull(product);
        assertFalse(product.getProductInfo().getProductOptions().isEmpty());
        assertNotNull(product.getReview());

    }

    @Test
    void retrieveProductDetailsApproach2() {
        CompletableFuture<Product> productFuture = productServiceUsingCompletableFuture
                .retrieveProductDetailsApproach2("ABC123");
        productFuture.thenAccept(product -> {
            assertNotNull(product);
            assertFalse(product.getProductInfo().getProductOptions().isEmpty());
            assertNotNull(product.getReview());
        }).join();
    }

    //Takes >3000ms
    @Test
    void retrieveProductDetailsWithInventory() {
        Product product =
                productServiceUsingCompletableFuture.retrieveProductDetailsWithInventory(
                        "ABC123");
        assertNotNull(product);
        assertFalse(product.getProductInfo().getProductOptions().isEmpty());
        assertNotNull(product.getReview());
        product.getProductInfo().getProductOptions().forEach(option -> {
            assertNotNull(option.getInventory());
        });
    }

    //Takes 1500ms
    @Test
    void retrieveProductDetailsWithInventoryApproach2() {
        Product product =
                productServiceUsingCompletableFuture.retrieveProductDetailsWithInventoryApproach2(
                        "ABC123");
        assertNotNull(product);
        assertFalse(product.getProductInfo().getProductOptions().isEmpty());
        assertNotNull(product.getReview());
        product.getProductInfo().getProductOptions().forEach(option -> {
            assertNotNull(option.getInventory());
        });
    }
}