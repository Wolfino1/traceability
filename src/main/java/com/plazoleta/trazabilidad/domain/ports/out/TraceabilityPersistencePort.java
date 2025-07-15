package com.plazoleta.trazabilidad.domain.ports.out;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;

import java.time.LocalDateTime;
import java.util.List;

public interface TraceabilityPersistencePort {

    TraceabilityModel save(TraceabilityModel traceabilityModel);

    List<TraceabilityModel> findByClientId(Long clientId);

    PagedResult<TraceabilityModel> getLogsByOrderId(
            Long orderId,
            int page,
            int size,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState
    );
    List<TraceabilityModel> findByOrderId(Long orderId);
}
