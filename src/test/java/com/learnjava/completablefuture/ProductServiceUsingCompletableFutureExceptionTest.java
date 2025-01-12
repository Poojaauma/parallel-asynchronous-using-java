package com.learnjava.completablefuture;

import com.learnjava.domain.Product;
import com.learnjava.service.InventoryService;
import com.learnjava.service.ProductInfoService;
import com.learnjava.service.ProductService;
import com.learnjava.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceUsingCompletableFutureExceptionTest {
    @Mock
    private ProductInfoService pisMock ;
    @Mock
    private ReviewService reviewMock ;
    @Mock
    private InventoryService invMock ;

    @InjectMocks
    ProductServiceUsingCompletableFuture productServiceUsingCompletableFuture;

    @Test
    void retrieveProductDetailsApproach2() {
        //given
        String pid = "ABC123";
        when(pisMock.retrieveProductInfo(any())).thenCallRealMethod();
        when(reviewMock.retrieveReviews(any())).thenThrow(new RuntimeException());
        when(invMock.retrieveInventory(any())).thenCallRealMethod();

        Product product = productServiceUsingCompletableFuture
                .retrieveProductDetailsWithInventoryApproach2(pid);
        assertNotNull(product);
        assertFalse(product.getProductInfo().getProductOptions().isEmpty());
        assertNotNull(product.getReview());
        assertEquals(0, product.getReview().getNoOfReviews());
        assertEquals(0.0, product.getReview().getOverallRating());
        product.getProductInfo().getProductOptions().forEach(option -> {
            assertNotNull(option.getInventory());
        });

    }

    @Test
    void retrieveProductDetailsApproach2_dup() {
        //given
        String pid = "ABC123";
        when(pisMock.retrieveProductInfo(any())).thenThrow(new RuntimeException());
        when(reviewMock.retrieveReviews(any())).thenCallRealMethod();

        Assertions.assertThrows(RuntimeException.class,
                () -> productServiceUsingCompletableFuture
                        .retrieveProductDetailsWithInventoryApproach2(pid));

    }
}