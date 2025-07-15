package com.plazoleta.trazabilidad.application.service.impl;

import com.plazoleta.trazabilidad.application.dto.request.CreateTraceabilityRequest;
import com.plazoleta.trazabilidad.application.dto.response.EmployeeEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityClientResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityResponse;
import com.plazoleta.trazabilidad.application.mappers.TraceabilityMapper;
import com.plazoleta.trazabilidad.application.service.TraceabilityService;
import com.plazoleta.trazabilidad.domain.models.EmployeeEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.ports.in.TraceabilityServicePort;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraceabilityServiceImpl implements TraceabilityService {

    private final TraceabilityMapper mapper;
    private final TraceabilityServicePort service;
    private final JwtUtil jwtUtil;

    @Override
        public TraceabilityResponse create(
                CreateTraceabilityRequest request,
                String authHeader
        ) {
            TraceabilityModel model = mapper.toModel(request);
            TraceabilityModel saved = service.save(model, authHeader);
            return mapper.toResponse(saved);
        }

    @Override
    public PagedResult<TraceabilityClientResponse> getLogsByOrderId(
            String authHeader,
            Long orderId,
            int page,
            int size,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState) {

        PagedResult<TraceabilityModel> logs = service.getLogsByOrderId(
                orderId, page, size, clientId , date, previousState, newState
        );

        List<TraceabilityClientResponse> content = logs.getContent().stream()
                .map(log -> new TraceabilityClientResponse(
                        log.getOrderId(),
                        log.getDate(),
                        log.getPreviousState(),
                        log.getNewState()
                ))
                .collect(Collectors.toList());

        return new PagedResult<>(
                content,
                logs.getPage(),
                logs.getSize(),
                logs.getTotalElements()
        );
    }

    @Override
    public OrderEfficiencyResponse getEfficiencyByOrder(Long orderId) {
        OrderEfficiencyModel eff = service.getEfficiencyByOrder(orderId);

        return new OrderEfficiencyResponse(
                eff.getOrderId(),
                eff.getEmployeeId(),
                eff.getTimeTaken()
        );
    }

    @Override
    public List<EmployeeEfficiencyResponse> getEmployeesEfficiency(String authHeader, Long restaurantId) {
        List<EmployeeEfficiencyModel> models = service.getEmployeesEfficiency(authHeader, restaurantId);
        return models.stream()
                .map(m -> new EmployeeEfficiencyResponse(
                        m.getEmployeeId(),
                        m.getAverageTime()
                ))
                .collect(Collectors.toList());
    }
}
