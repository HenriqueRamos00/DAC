package br.ufpr.bantads.saga.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient clienteRestClient(
        @Value("${cliente.url}") String clienteUrl
    ) {
        return RestClient.builder()
            .baseUrl(clienteUrl)
            .build();
    }
}
