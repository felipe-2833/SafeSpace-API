package br.com.fiap.safespace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(info = @Info(title = "SafeSpace API", version = "v1", description = "API do projeto safespace", contact = @Contact(name = "Felipe Levy Fidelix", email = "rm556426@fiap.com.br")))
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}
