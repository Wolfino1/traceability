package com.plazoleta.trazabilidad.domain.usecase;

import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.domain.exceptions.IncompleteTraceabilityException;
import com.plazoleta.trazabilidad.domain.exceptions.InvalidOrderException;
import com.plazoleta.trazabilidad.domain.exceptions.UnauthorizedException;
import com.plazoleta.trazabilidad.domain.exceptions.WrongArgumentException;
import com.plazoleta.trazabilidad.domain.models.EmployeeEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.ports.in.TraceabilityServicePort;
import com.plazoleta.trazabilidad.domain.ports.out.OrderClientPort;
import com.plazoleta.trazabilidad.domain.ports.out.RestaurantClientPort;
import com.plazoleta.trazabilidad.domain.ports.out.TraceabilityPersistencePort;
import com.plazoleta.trazabilidad.domain.ports.out.UserClientPort;
import com.plazoleta.trazabilidad.domain.util.contants.DomainConstants;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.security.JwtUtil;
import com.plazoleta.trazabilidad.infrastructure.web.dto.RestaurantClientDto;
import com.plazoleta.trazabilidad.infrastructure.web.dto.UserClientDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class TraceabilityUseCase implements TraceabilityServicePort {

    private final TraceabilityPersistencePort persistencePort;
    private final RestaurantClientPort restaurantClient;     private final UserClientPort userClientPort;
    private final JwtUtil jwtUtil;

    public TraceabilityUseCase(TraceabilityPersistencePort persistencePort, RestaurantClientPort restaurantClient,
                               UserClientPort userClientPort, JwtUtil jwtUtil) {
        this.persistencePort = persistencePort;
        this.restaurantClient = restaurantClient;
        this.userClientPort = userClientPort;
        this.jwtUtil= jwtUtil;
    }

    @Override
    public TraceabilityModel save(TraceabilityModel model, String authHeader) {

        Long clientId = model.getClientId();
        Long employeeId = model.getEmployeeId();

        if (clientId == null) {
            throw new WrongArgumentException("clientId no puede ser null en trazabilidad");
        }

        UserClientDto client = userClientPort.getUserById(clientId, authHeader);
        model.setClientEmail(client != null ? client.email() : null);

        if (employeeId != null) {
            UserClientDto employee = userClientPort.getUserById(employeeId, authHeader);
            model.setEmployeeEmail(employee != null ? employee.email() : null);
        } else {
            model.setEmployeeEmail(null);
        }

        model.setDate(LocalDateTime.now());
        return persistencePort.save(model);
    }

    @Override
    public PagedResult<TraceabilityModel> getLogsByOrderId(
            Long orderId,
            int page,
            int size,
            Long clientIdFromToken,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState
    ) {
        if (!orderBelongsToClient(orderId, clientIdFromToken)) {
            throw new UnauthorizedException(DomainConstants.NOT_ALLOWED_TO_CHECK_OTHER_CLIENTS_ORDERS);
        }
        return persistencePort.getLogsByOrderId(
                orderId, page, size, clientIdFromToken, date, previousState, newState
        );
    }

    @Override
    public OrderEfficiencyModel getEfficiencyByOrder(Long orderId) {
        if (orderId == null) {
            throw new InvalidOrderException(DomainConstants.ORDER_CANT_BE_NULL);
        }

        List<TraceabilityModel> logs = persistencePort
                .findByOrderId(orderId).stream()
                .sorted(Comparator.comparing(TraceabilityModel::getDate))
                .toList();

        if (logs.isEmpty()) {
            throw new InvalidOrderException(DomainConstants.TRAZ_NOT_FOUND);
        }

        TraceabilityModel start = logs.stream()
                .filter(l -> l.getNewState() == OrderStatus.EN_PREPARACION)
                .findFirst()
                .orElseThrow(() -> new IncompleteTraceabilityException(
                        DomainConstants.TRACEABILITY_INCOMPLETED));

        TraceabilityModel end = logs.stream()
                .filter(l -> l.getNewState() == OrderStatus.ENTREGADO)
                .findFirst()
                .orElseThrow(() -> new IncompleteTraceabilityException(
                        DomainConstants.TRACEABILITY_INCOMPLETED));

        Duration timeTaken = Duration.between(start.getDate(), end.getDate());
        Long employeeId = start.getEmployeeId();

        return new OrderEfficiencyModel(orderId, employeeId, timeTaken);
    }

    @Override
    public List<EmployeeEfficiencyModel> getEmployeesEfficiency(String authHeader, Long restaurantId) {
        if (restaurantId == null) {
            throw new WrongArgumentException("restaurantId es obligatorio");
        }

        Long ownerId = jwtUtil.getOwnerIdFromSecurityContext();
        RestaurantClientDto rest = restaurantClient.getById(restaurantId, authHeader);
        if (!rest.ownerId().equals(ownerId)) {
            throw new UnauthorizedException(DomainConstants.NOT_ALLOWED);
        }

        List<TraceabilityModel> logs = persistencePort.findByRestaurantId(authHeader, restaurantId);
        if (logs.isEmpty()) {
            throw new WrongArgumentException("No hay trazabilidad para el restaurante " + restaurantId);
        }

        // 1) Agrupa TODOS los logs por empleado
        Map<Long, List<TraceabilityModel>> logsPorEmpleado = logs.stream()
                .collect(Collectors.groupingBy(TraceabilityModel::getEmployeeId));

        return logsPorEmpleado.entrySet().stream()
                // 2) Sólo empleados que tengan al menos una entrega
                .filter(e -> e.getValue().stream()
                        .anyMatch(l -> l.getNewState() == OrderStatus.ENTREGADO))
                .map(e -> {
                    Long empId = e.getKey();
                    List<TraceabilityModel> trazasEmp = e.getValue();

                    // 3) Agrupa las trazas de este empleado por cada orderId
                    Map<Long, List<TraceabilityModel>> trazasPorOrden = trazasEmp.stream()
                            .collect(Collectors.groupingBy(TraceabilityModel::getOrderId));

                    // 4) Calcula el promedio sólo sobre órdenes que sí llegaron a ENTREGADO
                    double avgMillis = trazasPorOrden.entrySet().stream()
                            .filter(o -> o.getValue().stream()
                                    .anyMatch(l -> l.getNewState() == OrderStatus.ENTREGADO))
                            .mapToLong(o -> {
                                Long orderId = o.getKey();
                                List<TraceabilityModel> t = o.getValue();

                                // marca de salida de preparación
                                LocalDateTime start = t.stream()
                                        .filter(l -> l.getPreviousState() == OrderStatus.EN_PREPARACION)
                                        .findFirst()
                                        .orElseThrow(() -> new WrongArgumentException(
                                                "Falta estado EN_PREPARACION para orderId = " + orderId))
                                        .getDate();

                                // marca de entrega
                                LocalDateTime end = t.stream()
                                        .filter(l -> l.getNewState() == OrderStatus.ENTREGADO)
                                        .findFirst()
                                        .orElseThrow(() -> new WrongArgumentException(
                                                "Falta estado ENTREGADO para orderId = " + orderId))
                                        .getDate();

                                return Duration.between(start, end).toMillis();
                            })
                            .average()
                            .orElse(0);

                    return new EmployeeEfficiencyModel(
                            empId,
                            Duration.ofMillis((long) avgMillis)
                    );
                })
                // 5) Orden descendente por tiempo promedio
                .sorted(Comparator.comparing(EmployeeEfficiencyModel::getAverageTime).reversed())
                .toList();
    }











    private boolean orderBelongsToClient(Long orderId, Long clientId) {
        return true;
    }
}

