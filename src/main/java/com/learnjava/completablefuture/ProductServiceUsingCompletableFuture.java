package com.learnjava.completablefuture;

import com.learnjava.domain.*;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ReviewService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

public class ProductServiceUsingCompletableFuture {
    private ProductInfoService productInfoService;
    private ReviewService reviewService;
    private InventoryService inventoryService;

    public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public ProductServiceUsingCompletableFuture(
            ProductInfoService productInfoService,
            ReviewService reviewService,
            InventoryService inventoryService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
        this.inventoryService = inventoryService;
    }

    public Product retrieveProductDetails(String productId) {
        stopWatch.start();
        CompletableFuture<ProductInfo> productInfoCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Review> reviewCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        reviewService.retrieveReviews(productId));
        Product product = productInfoCompletableFuture
                .thenCombine(reviewCompletableFuture,
                        (productInfo, review)->
                                new Product(productId, productInfo, review)).join();

        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return product;
    }

    //As List size for Product Options Grows, the time execution increases
    public Product retrieveProductDetailsWithInventory(String productId) {
        stopWatch.start();
        CompletableFuture<ProductInfo> productInfoCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        productInfoService.retrieveProductInfo(productId))
                        .thenApply(productInfo-> {
                            productInfo.setProductOptions(updateInventory(productInfo));
                            return productInfo;
                        });
        CompletableFuture<Review> reviewCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        reviewService.retrieveReviews(productId));
        Product product = productInfoCompletableFuture
                .thenCombine(reviewCompletableFuture,
                        (productInfo, review)->
                                new Product(productId, productInfo, review)).join();

        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return product;
    }

    //Optimization for the above
    public Product retrieveProductDetailsWithInventoryApproach2(String productId) {
        stopWatch.start();
        CompletableFuture<ProductInfo> productInfoCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                                productInfoService.retrieveProductInfo(productId))
                        .thenApply(productInfo-> {
                            productInfo.setProductOptions(updateInventoryApproach2(productInfo));
                            return productInfo;
                        });
        CompletableFuture<Review> reviewCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        reviewService.retrieveReviews(productId))
                        .exceptionally((e) -> {
                            log("Handled the Exception in reviewService : "+ e.getMessage());
                            return Review.builder().noOfReviews(0).overallRating(0.0).build();
                        });
        Product product = productInfoCompletableFuture
                .thenCombine(reviewCompletableFuture,
                        (productInfo, review)->
                                new Product(productId, productInfo, review))

                .whenComplete((productInfo, exception)->{
                    log("Inside whenComplete : " + productInfo + "exception : " + exception);

                })
                .join();

        stopWatch.stop();
        log("Total Time Taken : "+ stopWatch.getTime());
        return product;
    }

    private List<ProductOption> updateInventoryToProductOption_approach3(ProductInfo productInfo) {



        List<CompletableFuture<ProductOption>> productOptionList = productInfo.getProductOptions()

                .stream()

                .map(productOption ->

                        CompletableFuture.supplyAsync(() -> inventoryService.retrieveInventory(productOption))

                                .exceptionally((ex) -> {

                                    log("Exception in Inventory Service : " + ex.getMessage());

                                    return Inventory.builder()

                                            .count(1).build();

                                })

                                .thenApply((inventory -> {

                                    productOption.setInventory(inventory);

                                    return productOption;

                                })))

                .toList();



        CompletableFuture<Void> cfAllOf = CompletableFuture.allOf(productOptionList.toArray(new CompletableFuture[productOptionList.size()]));

        return cfAllOf

                .thenApply(v->{

                    return  productOptionList.stream().map(CompletableFuture::join)

                            .collect(Collectors.toList());

                })

                .join();



    }

    private List<ProductOption> updateInventory(ProductInfo productInfo) {
        List<ProductOption> productOptionList = productInfo.getProductOptions().parallelStream().map(productOption -> {
            Inventory inventory = inventoryService.retrieveInventory(productOption);
            productOption.setInventory(inventory);
            return productOption;
        }).toList();
        return productOptionList;
    }

    private List<ProductOption> updateInventoryApproach2(ProductInfo productInfo) {
        List<CompletableFuture<ProductOption>> productOptionList = productInfo.getProductOptions().parallelStream()
                .map(productOption -> CompletableFuture.supplyAsync(
                        () -> inventoryService.retrieveInventory(productOption))
                        .thenApply(inventory -> {
                            productOption.setInventory(inventory);
                            return productOption;
                        })).toList();
        return productOptionList.stream().map(CompletableFuture::join).toList();
    }

    public CompletableFuture<Product> retrieveProductDetailsApproach2(String productId) {
        stopWatch.start();
        CompletableFuture<ProductInfo> productInfoCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Review> reviewCompletableFuture =
                CompletableFuture.supplyAsync(() ->
                        reviewService.retrieveReviews(productId));
        return productInfoCompletableFuture
                .thenCombine(reviewCompletableFuture,
                        (productInfo, review)->
                                new Product(productId, productInfo, review));

    }

    public static void main(String[] args) {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        log("Product is " + product);

    }
}
