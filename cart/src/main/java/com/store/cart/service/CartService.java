package com.store.cart.service;

import com.store.cart.domain.Cart;
import org.springframework.stereotype.Service;

@Service
public interface CartService extends GenericService<Cart>{

    void addProductToCart(Long product_id, Long cart_id);

    void excludeProductFromCart(Long product_id, Long cart_id);

    void sendToPayment(Long cart_id);

    void changeStatus(String cart_id);
}
