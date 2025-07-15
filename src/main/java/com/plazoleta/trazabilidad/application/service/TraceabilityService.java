package com.plazoleta.trazabilidad.application.service;

import com.plazoleta.trazabilidad.application.dto.request.CreateTraceabilityRequest;
import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityClientResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityResponse;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;

import java.time.LocalDateTime;

public interface TraceabilityService {

    TraceabilityResponse create(CreateTraceabilityRequest request, String authHeader);
    PagedResult<TraceabilityClientResponse> getLogsByOrderId(
            String authHeader,
            Long orderId,
            int page,
            int size,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState
    );
    OrderEfficiencyResponse getEfficiencyByOrder(Long orderId);

}
