package com.store.payment.component;

public interface RabbitMQComponent {
    void processPayment(String message);
}
