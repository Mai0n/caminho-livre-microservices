package br.com.fiap.ms.ocorrencias.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Microsserviço de Ocorrências")
                        .description("API responsável pelo registro e priorização das barreiras de acessibilidade nas calçadas")
                        .version("v1"))

                .servers(List.of(
                        new Server().url("/ms-ocorrencias")
                ));
    }
}
