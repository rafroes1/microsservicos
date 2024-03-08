package com.store.cart.repository;

import com.store.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    void addProductToCart(Long product_id, Long cart_id);

    void excludeProductFromCart(Long product_id, Long cart_id);

    void sendToPayment(Long cart_id);
}
