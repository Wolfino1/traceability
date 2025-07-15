package com.plazoleta.trazabilidad.application.mappers;

import com.plazoleta.trazabilidad.application.dto.request.CreateTraceabilityRequest;
import com.plazoleta.trazabilidad.application.dto.response.TraceabilityResponse;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.infrastructure.entity.TraceabilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraceabilityMapper {

    @Mapping(target = "id", ignore = true)
    TraceabilityModel toModel(CreateTraceabilityRequest request);

    TraceabilityResponse toResponse(TraceabilityModel model);

    TraceabilityEntity toEntity(TraceabilityModel model);

    TraceabilityModel entityToModel(TraceabilityEntity entity);
}
