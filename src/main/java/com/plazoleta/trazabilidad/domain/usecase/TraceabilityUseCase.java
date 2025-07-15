package com.plazoleta.trazabilidad.domain.usecase;

import com.plazoleta.trazabilidad.application.dto.response.OrderEfficiencyResponse;
import com.plazoleta.trazabilidad.domain.exceptions.IncompleteTraceabilityException;
import com.plazoleta.trazabilidad.domain.exceptions.InvalidOrderException;
import com.plazoleta.trazabilidad.domain.exceptions.UnauthorizedException;
import com.plazoleta.trazabilidad.domain.exceptions.WrongArgumentException;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.ports.in.TraceabilityServicePort;
import com.plazoleta.trazabilidad.domain.ports.out.OrderClientPort;
import com.plazoleta.trazabilidad.domain.ports.out.TraceabilityPersistencePort;
import com.plazoleta.trazabilidad.domain.ports.out.UserClientPort;
import com.plazoleta.trazabilidad.domain.util.contants.DomainConstants;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.web.dto.UserClientDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


public class TraceabilityUseCase implements TraceabilityServicePort {

    private final TraceabilityPersistencePort persistencePort;
    private final OrderClientPort orderClientPort;
    private final UserClientPort userClientPort;

    public TraceabilityUseCase(TraceabilityPersistencePort persistencePort, OrderClientPort orderClientPort, UserClientPort userClientPort) {
        this.persistencePort = persistencePort;
        this.orderClientPort = orderClientPort;
        this.userClientPort = userClientPort;
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

    private boolean orderBelongsToClient(Long orderId, Long clientId) {
        return true;
    }
}

