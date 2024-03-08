package com.store.payment.service;

import com.store.payment.domain.Payment;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService extends GenericService<Payment>{
    void processCartPayment(Long cart_id);
}
