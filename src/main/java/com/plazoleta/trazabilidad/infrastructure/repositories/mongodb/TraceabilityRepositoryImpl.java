package com.plazoleta.trazabilidad.infrastructure.repositories.mongodb;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.infrastructure.entity.TraceabilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TraceabilityRepositoryImpl implements TraceabilityRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public TraceabilityRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<TraceabilityEntity> findByFilters(
            Long orderId,
            Long clientId,
            LocalDateTime date,
            OrderStatus previousState,
            OrderStatus newState,
            Pageable pageable) {

        List<Criteria> criteriaList = new ArrayList<>();

        if (orderId != null)        criteriaList.add(Criteria.where("orderId").is(orderId));
        if (clientId != null)       criteriaList.add(Criteria.where("clientId").is(clientId));
        if (date != null)           criteriaList.add(Criteria.where("date").is(date));
        if (previousState != null)  criteriaList.add(Criteria.where("previousState").is(previousState));
        if (newState != null)       criteriaList.add(Criteria.where("newState").is(newState));

        Query query = new Query();
        if (!criteriaList.isEmpty())
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        query.with(pageable);

        List<TraceabilityEntity> list = mongoTemplate.find(query, TraceabilityEntity.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), TraceabilityEntity.class);

        return new PageImpl<>(list, pageable, total);
    }
}

