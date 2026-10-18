package com.ycyw.chat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Point d'entrée de la PoC tchat Your Car Your Way. */
@SpringBootApplication
@OpenAPIDefinition(
    info =
        @Info(
            title = "API REST du tchat Your Car Your Way (PoC)",
            version = "0.0.1",
            description =
                "Lectures et ouverture de conversation. L'envoi et la réception des messages en"
                    + " direct passent par WebSocket (STOMP) et ne figurent pas ici."))
public class ChatPocApplication {

  public static void main(String[] args) {
    SpringApplication.run(ChatPocApplication.class, args);
  }
}
