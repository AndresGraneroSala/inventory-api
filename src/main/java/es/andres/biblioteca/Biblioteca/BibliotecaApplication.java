package es.andres.biblioteca.Biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class BibliotecaApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BibliotecaApplication.class, args);
		Environment env = context.getEnvironment();

		String port = env.getProperty("server.port");
		String apiDocsPath = env.getProperty("springdoc.api-docs.path");
		String swaggerPath = env.getProperty("springdoc.swagger-ui.path");

		System.out.println("API-DOCS: http://localhost:" + port + apiDocsPath);
		System.out.println("SWAGGER: http://localhost:" + port + swaggerPath);
	}
}