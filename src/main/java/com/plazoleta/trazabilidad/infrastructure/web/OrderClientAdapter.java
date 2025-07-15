package com.plazoleta.trazabilidad.infrastructure.web;

import com.plazoleta.trazabilidad.domain.ports.out.OrderClientPort;
import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderClientDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderClientAdapter implements OrderClientPort {

    @Qualifier("orderWebClient")
    private final WebClient orderWebClient;

    @Override
    public OrderClientDto getOrderById(Long restaurantId,
                                       Long orderId,
                                       String authorizationHeader) {
        List<OrderClientDto> orders = orderWebClient.get()
                .uri("/order/{id}", orderId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToFlux(OrderClientDto.class)
                .collectList()
                .block();

        return orders.stream()
                .filter(o -> o.id().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró la orden " + orderId +
                                " para el restaurante " + restaurantId
                ));
    }
}


