package com.store.cart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.cart.domain.Cart;
import com.store.cart.domain.CartItem;
import com.store.cart.repository.CartItemRepository;
import com.store.cart.repository.CartRepository;
import com.store.cart.service.CartService;
import com.store.cart.util.CartStatus;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.LinkedList;

@Service
public class CartServiceImpl extends GenericServiceImpl<Cart, Long, CartRepository> implements CartService {
    private final WebClient webClient;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Value("=${rabbitmq.exchange.name}")
    private String exchange;

    @Value("=${rabbitmq.routing.key}")
    private String routingKey;

    @Value("=${rabbitmq.routing.2.key}")
    private String routingKey2;

    private final AmqpTemplate rabbitTemplate;

    public CartServiceImpl(CartRepository repository, WebClient webClient, AmqpTemplate rabbitTemplate){
        super(repository);
        this.webClient = webClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void save(Cart cart){

            this.webClient.get()
                    .uri("8082/api/user/" + String.valueOf(cart.getUser_id()))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.OK)) {
                            Cart auxCart = repository.save(cart);
                            for(CartItem item: auxCart.getCartItems()){
                                CartItem cartItem = new CartItem();

                                cartItem.setCart(auxCart);
                                cartItem.setProduct_id(item.getProduct_id());

                                cartItemRepository.save(cartItem);
                            }

                            //this.sendNotification(auxCart); //sera usado no payment
                            return response.toEntity(String.class);
                        } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)){
                            return response.toEntity(String.class);
                        } else {
                            return response.createError();
                        }
                    }).block();
    }

    @Override
    public void addProductToCart(Long product_id, Long cart_id) {
        this.webClient.get()
                .uri("8083/api/product/" + String.valueOf(product_id))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        Cart cart = repository.getReferenceById(cart_id);

                        CartItem newItem = new CartItem();

                        newItem.setProduct_id(product_id);
                        newItem.setCart(cart);

                        cartItemRepository.save(cartItem);
                        repository.save(cart);

                        return response.toEntity(String.class);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return response.toEntity(String.class);
                    } else {
                        return response.createError();
                    }
                }).block();
    }

    @Override
    public void excludeProductFromCart(Long product_id, Long cart_id) {
        this.webClient.get()
                .uri("8083/api/product/" + String.valueOf(product_id))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        Cart cart = repository.getReferenceById(cart_id);
                        Collection<CartItem> removeCandidates = new LinkedList<CartItem>();

                        for(CartItem item: cart.getCartItems()){
                            if(item.getProduct_Id == product_id){
                                removeCandidates.add(item);
                            }
                        }

                        cart.getCartItems().removeAll(removeCandidates);
                        repository.save(cart);

                        return response.toEntity(String.class);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)){
                        return response.toEntity(String.class);
                    } else {
                        return response.createError();
                    }
                }).block();
    }

    @Override
    public void sendToPayment(Long cart_id) {
        Cart cart = repository.getReferenceById(cart_id);
        cart.setStatus = CartStatus.PROCESSING.ordinal();
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(cart);
            rabbitTemplate.convertAndSend(exchange, routingKey, json); //ENVIA PARA A FILA DE CART->PAYMENT
        }catch(JsonProcessingException e){
        }
    }

    @Override
    public void changeStatus(String cart_id) {
        Cart cart = repository.getReferenceById(Long.parseLong(cart_id));
        cart.setStatus = CartStatus.PAID.ordinal();

        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            String json = mapper.writeValueAsString(cart);
            rabbitTemplate.convertAndSend(exchange, routingKey2, json); //ENVIA PARA A FILA DE CART->NOTIFICATION
        }catch(JsonProcessingException e){
        }
    }
}

