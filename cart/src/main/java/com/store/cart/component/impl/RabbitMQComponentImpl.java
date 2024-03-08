package com.store.cart.component.impl;

import com.store.cart.component.RabbitMQComponent;
import com.store.cart.service.impl.CartServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQComponentImpl implements RabbitMQComponent {

    @Value("=${rabbitmq.queue.2.name}")
    private String queue2;

    @Autowired
    private CartServiceImpl cartService;

    @RabbitListener(queues = "payment_cart")
    @Override
    public void changeStatus(String message) {
        cartService.changeStatus(message); //CONSOME DA FILA PAYMENT->CART
    }
}
