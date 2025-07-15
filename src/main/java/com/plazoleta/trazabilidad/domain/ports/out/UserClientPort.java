package com.plazoleta.trazabilidad.domain.ports.out;

import com.plazoleta.trazabilidad.infrastructure.web.dto.UserClientDto;

public interface UserClientPort {
    UserClientDto getUserById(Long userId,
                              String authorizationHeader);

}
