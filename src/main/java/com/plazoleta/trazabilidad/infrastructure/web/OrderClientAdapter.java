package com.plazoleta.trazabilidad.infrastructure.web;

import com.plazoleta.trazabilidad.domain.ports.out.OrderClientPort;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderClientDto;
import com.plazoleta.trazabilidad.infrastructure.web.dto.OrderSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
                .header(AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToFlux(OrderClientDto.class)
                .collectList()
                .block();

        return orders.stream()
                .filter(o -> o.orderId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró la orden " + orderId +
                                " para el restaurante " + restaurantId
                ));
    }

    @Override
    public List<OrderSummaryDto> getOrdersByRestaurant(String authorizationHeader,
                                                       Long restaurantId) {
        PagedResult<OrderClientDto> paged = orderWebClient.get()
                .uri(uri -> uri
                        .path("/order/restaurants/{restaurantId}/orders")
                        .queryParam("page", 0)
                        .queryParam("size", 1000)
                        .build(restaurantId))
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PagedResult<OrderClientDto>>() {})
                .block();

        if (paged == null || paged.getContent().isEmpty()) {

            throw new RuntimeException(
                    "No se encontraron órdenes para el restaurante " + restaurantId);
        }

        return paged.getContent().stream()
                .map(o -> new OrderSummaryDto(o.orderId()))
                .collect(Collectors.toList());
    }
}