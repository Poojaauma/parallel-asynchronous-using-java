package com.learnjava.service;

import com.learnjava.domain.checkout.Cart;
import com.learnjava.domain.checkout.CartItem;
import com.learnjava.domain.checkout.CheckoutResponse;
import com.learnjava.domain.checkout.CheckoutStatus;

import java.util.List;

import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static com.learnjava.util.LoggerUtil.log;
import static java.util.stream.Collectors.summingDouble;

public class CheckoutService {
    private PriceValidatorService priceValidatorService;

    public CheckoutService(PriceValidatorService priceValidatorService) {
        this.priceValidatorService = priceValidatorService;
    }

    public CheckoutResponse checkout(Cart cart) {
        startTimer();
        List<CartItem> priceValidationItem = cart.getCartItemList().parallelStream()
                .map(cartItem -> {
                    boolean isPriceInValid = priceValidatorService.isCartItemInvalid(cartItem);
                    cartItem.setExpired(isPriceInValid);
                    return cartItem;
                }).filter(CartItem::isExpired).toList();
        timeTaken();
        if(!priceValidationItem.isEmpty()) {
            return new CheckoutResponse(CheckoutStatus.FAILURE, priceValidationItem);
        }
        double finalPrice = calculateFinalPrice(cart);

        double finalPrice1 = calculateFinalPriceWithReduce(cart);
        log("Checkout complete. Final Price is : " + finalPrice);
        return new CheckoutResponse(CheckoutStatus.SUCCESS, finalPrice);
    }

    private double calculateFinalPrice(Cart cart) {
        return cart.getCartItemList().parallelStream()
                .map(cartItem -> cartItem.getQuantity() * cartItem.getRate())
                .mapToDouble(Double::doubleValue).sum();
    }

    private double calculateFinalPriceWithReduce(Cart cart) {
        return cart.getCartItemList().parallelStream()
                .map(cartItem -> cartItem.getQuantity() * cartItem.getRate())
                .reduce(0.0, Double::sum);
    }
}
