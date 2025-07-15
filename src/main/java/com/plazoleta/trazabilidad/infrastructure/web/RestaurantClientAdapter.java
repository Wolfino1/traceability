package com.plazoleta.trazabilidad.infrastructure.web;

import com.plazoleta.trazabilidad.domain.ports.out.RestaurantClientPort;
import com.plazoleta.trazabilidad.infrastructure.web.dto.RestaurantClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class RestaurantClientAdapter implements RestaurantClientPort {

    @Qualifier("restaurantWebClient")
    private final WebClient restaurantWebClient;

    @Override
    public RestaurantClientDto getById(Long restaurantId, String authHeader) {
        return restaurantWebClient.get()
                .uri("/restaurant/{id}", restaurantId)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .bodyToMono(RestaurantClientDto.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el restaurante " + restaurantId
                ));
    }
}
