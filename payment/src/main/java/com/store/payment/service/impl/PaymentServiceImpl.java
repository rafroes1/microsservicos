package com.store.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.payment.domain.Payment;
import com.store.payment.repository.PaymentRepository;
import com.store.payment.service.PaymentService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentServiceImpl extends GenericServiceImpl<Payment, Long, PaymentRepository> implements PaymentService {
    @Value("=${rabbitmq.exchange.name}")
    private String exchange;

    @Value("=${rabbitmq.routing.2.key}")
    private String routingKey2;


    private final AmqpTemplate rabbitTemplate;

    public PaymentServiceImpl(PaymentRepository repository, AmqpTemplate rabbitTemplate){
        super(repository);
        this.rabbitTemplate = rabbitTemplate;
    }

    public void processCartPayment(Long cart_id){
        //FAZ A CHAMADA COM O GATEWAY DE PAGAMENTO, PROCESSA O PAGAMENTO E RETORNA
        rabbitTemplate.convertAndSend(exchange, routingKey2, String.valueOf(cart_id)); //ENVIA PARA A FILA PAYMENT->CART
    }

    public Map<String, Object> convertToObject(String jsonS){
        try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonS, Map.class);
            return map;
        }catch(JsonProcessingException e){
            return null;
        }
    }
}

