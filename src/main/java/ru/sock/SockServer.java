package ru.sock;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Sock Manager API", version = "1.0", description = "API для управления складом носков"))
public class SockServer {
    public static void main(String[] args) {
        SpringApplication.run(SockServer.class, args);
    }
}