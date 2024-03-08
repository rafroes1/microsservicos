package com.store.payment.component.impl;

import com.store.payment.component.RabbitMQComponent;
import com.store.payment.service.impl.PaymentServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitMQComponentImpl implements RabbitMQComponent {

    @Value("=${rabbitmq.queue.name}")
    private String queue;

    @Autowired
    private PaymentServiceImpl paymentService;

    @RabbitListener(queues = "cart_payment")
    @Override
    public void processPayment(String message) {
        Map<String, Object> obj = paymentService.convertToObject(message); //CONSOME DA FILA CART->PAYMENT

        Long cart_id = (Long) obj.get("cart_id");

        paymentService.processCartPayment(cart_id);
    }
}
