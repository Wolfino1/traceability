package com.plazoleta.trazabilidad.infrastructure.repositories.mongodb;

import com.plazoleta.trazabilidad.infrastructure.entity.TraceabilityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TraceabilityRepository extends MongoRepository<TraceabilityEntity, String>, TraceabilityRepositoryCustom {
    List<TraceabilityEntity> findByClientId(Long clientId);
    List<TraceabilityEntity> findByOrderId(Long orderId);
    List<TraceabilityEntity> findByRestaurantId(Long restaurantId);
    List<TraceabilityEntity> findByOrderIdIn(List<Long> orderIds);

}
