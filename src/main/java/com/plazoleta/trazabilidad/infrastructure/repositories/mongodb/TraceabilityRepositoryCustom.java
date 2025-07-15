package com.plazoleta.trazabilidad.infrastructure.repositories.mongodb;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.infrastructure.entity.TraceabilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TraceabilityRepositoryCustom {
    Page<TraceabilityEntity> findByFilters(
            Long orderId,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState,
            Pageable pageable
    );
}
