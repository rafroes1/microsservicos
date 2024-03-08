package com.store.notification.component.impl;

import com.store.notification.component.RabbitMQComponent;
import com.store.notification.service.impl.EmailServiceImpl;
import jakarta.validation.constraints.Email;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RabbitMQComponentImpl implements RabbitMQComponent {
    @Value("=${rabbitmq.queue.name}")
    private String queue;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private final WebClient webClient;

    public RabbitMQComponentImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @RabbitListener(queues = "cart_notification")
    @Override
    public void handleMessage(String message) {
        Map<String, Object> obj = emailService.convertToObject(message);

        int cart_id = (int) obj.get("id");
        int user_id = (int) obj.get("user_id");

        String response = this.webClient.get()
                .uri("/user/" + String.valueOf(user_id))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        Map<String, Object> user = emailService.convertToObject(response);

        String content = emailService.constructOrderContent(String.valueOf(cart_id), (String) user.get("username"));

        emailService.sendEmail(content, (String) user.get("email"), "Notificação De Compra");
    }
}
