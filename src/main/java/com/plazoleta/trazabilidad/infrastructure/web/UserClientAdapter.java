package com.plazoleta.trazabilidad.infrastructure.web;

import com.plazoleta.trazabilidad.domain.exceptions.WrongArgumentException;
import com.plazoleta.trazabilidad.domain.ports.out.UserClientPort;
import com.plazoleta.trazabilidad.infrastructure.web.dto.UserClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClientAdapter implements UserClientPort {

    @Qualifier("userWebClient")
    private final WebClient userWebClient;

    @Override
    public UserClientDto getUserById(Long userId, String authorizationHeader) {
        if (userId == null) {
            throw new WrongArgumentException("No se puede consultar usuario con id null");
        }

        return userWebClient
                .get()
                .uri("/user/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(UserClientDto.class)
                .block();
    }
}