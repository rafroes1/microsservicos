package com.store.cart.controller;

import com.store.cart.domain.Cart;
import com.store.cart.domain.CartProduct;
import com.store.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/cart")
public class CartController extends GenericController<Cart>{
    public CartController(CartService service) { super(service); }

    @RequestMapping(path = "/addToCart", method = RequestMethod.POST)
    public ResponseEntity<Void> addProductToCart(@RequestBody CartProduct cartProduct){
        service.addProductToCart(cartProduct.getProduct_id, cartProduct.getCart_id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/removeFromCart/{cart_id}/{product_id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> excludeProductFromCart(Long cart_id, Long product_id){
        service.excludeProductFromCart(product_id, cart_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sendToPayment")
    public ResponseEntity<Void> sendToPayment(@RequestParam("cart_id") Long cart_id){
        service.sendToPayment(cart_id);
        return ResponseEntity.ok().build();
    }
}

