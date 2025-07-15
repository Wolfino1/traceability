package com.plazoleta.trazabilidad.infrastructure.adapters.persistence;

import com.plazoleta.trazabilidad.application.mappers.TraceabilityMapper;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.ports.out.TraceabilityPersistencePort;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.entity.TraceabilityEntity;
import com.plazoleta.trazabilidad.infrastructure.repositories.mongodb.TraceabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraceabilityPersistenceAdapter implements TraceabilityPersistencePort {

    private final TraceabilityRepository repository;
    private final TraceabilityMapper mapper;

    @Override
    public TraceabilityModel save(TraceabilityModel traceabilityModel) {
        TraceabilityEntity entity = mapper.toEntity(traceabilityModel);
        TraceabilityEntity savedEntity = repository.save(entity);
        return mapper.entityToModel(savedEntity);
    }


    @Override
    public List<TraceabilityModel> findByClientId(Long clientId) {
        List<TraceabilityEntity> entities = repository.findByClientId(clientId);
        return entities.stream()
                .map(mapper::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public PagedResult<TraceabilityModel> getLogsByOrderId(
            Long orderId,
            int page,
            int size,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<TraceabilityEntity> pageEnt = repository.findByFilters(
                orderId, clientId, date, previousState, newState, pageable
        );

        List<TraceabilityModel> content = pageEnt.stream()
                .map(mapper::entityToModel)
                .toList();

        return new PagedResult<>(
                content,
                pageEnt.getNumber(),
                pageEnt.getSize(),
                pageEnt.getTotalElements()
        );
    }

    @Override
    public List<TraceabilityModel> findByOrderId(Long orderId) {

        return repository.findByOrderId(orderId).stream()
                .map(mapper::entityToModel)
                .collect(Collectors.toList());
    }
}
