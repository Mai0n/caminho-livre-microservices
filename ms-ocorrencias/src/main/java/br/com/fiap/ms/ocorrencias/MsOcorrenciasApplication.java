package br.com.fiap.ms.ocorrencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsOcorrenciasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsOcorrenciasApplication.class, args);
	}

}
