package com.plazoleta.trazabilidad.domain.ports.in;

import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityClientResponse;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;

import java.time.LocalDateTime;

public interface TraceabilityServicePort {
    TraceabilityModel save(TraceabilityModel traceabilityModel, String authHeader);
    PagedResult<TraceabilityModel> getLogsByOrderId(
            Long orderId,
            int page,
            int size,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState
    );
    OrderEfficiencyModel getEfficiencyByOrder(Long orderId);
}
