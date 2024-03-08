package com.store.cart.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("=${rabbitmq.queue.name}")
    private String queue;

    @Value("=${rabbitmq.exchange.name}")
    private String exchange;

    @Value("=${rabbitmq.routing.key}")
    private String routingKey;

    @Value("=${rabbitmq.queue.2.name}")
    private String queue2;

    @Value("=${rabbitmq.routing.2.key}")
    private String routingKey2;

    @Value("=${rabbitmq.queue.3.name}")
    private String queue3;

    @Value("=${rabbitmq.routing.3.key}")
    private String routingKey3;

    @Bean
    public Queue queue() { return new Queue(queue); }

    @Bean
    public Queue queue2() { return new Queue(queue2); }

    @Bean
    public Queue queue3() { return new Queue(queue3); }

    @Bean
    public TopicExchange exchange() { return new TopicExchange(exchange); }

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    public Binding binding2(){
        return BindingBuilder
                .bind(queue2())
                .to(exchange())
                .with(routingKey2);
    }

    @Bean
    public Binding binding3(){
        return BindingBuilder
                .bind(queue3())
                .to(exchange())
                .with(routingKey3);
    }
}
