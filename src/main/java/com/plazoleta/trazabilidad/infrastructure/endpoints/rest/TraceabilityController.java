package com.plazoleta.trazabilidad.infrastructure.endpoints.rest;

import com.plazoleta.trazabilidad.application.dto.response.EmployeeEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityClientResponse;
import com.plazoleta.trazabilidad.application.service.TraceabilityService;
import com.plazoleta.trazabilidad.application.dto.request.CreateTraceabilityRequest;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityResponse;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trazabilidad")
@RequiredArgsConstructor
public class TraceabilityController {

    private final TraceabilityService traceabilityService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<TraceabilityResponse> createTrace(

            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody @Valid CreateTraceabilityRequest request
    ) {
                TraceabilityResponse response = traceabilityService.create(request, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PagedResult<TraceabilityClientResponse>> getLogsByOrder
            (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
             @PathVariable Long orderId,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(required = false) Long clientId,
             @RequestParam(required = false) LocalDateTime date,
             @RequestParam(required = false) OrderStatus previousState,
             @RequestParam(required = false) OrderStatus newState) {
        return ResponseEntity.ok(traceabilityService.getLogsByOrderId(authHeader, orderId , page, size, clientId, date,
                previousState,newState));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{orderId}/efficiency")
    public ResponseEntity<OrderEfficiencyResponse> getEfficiencyByOrder(@PathVariable Long orderId) {
        OrderEfficiencyResponse resp = traceabilityService.getEfficiencyByOrder(orderId);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/employees/efficiency")
    public ResponseEntity<List<EmployeeEfficiencyResponse>> getEmployeesEfficiency(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam Long restaurantId) {
        List<EmployeeEfficiencyResponse> resp =
                traceabilityService.getEmployeesEfficiency(authHeader, restaurantId);
        return ResponseEntity.ok(resp);
    }
}

