package com.plazoleta.trazabilidad.infrastructure.mappers;

import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PageMapperInfra {
    public <T> PagedResult<T> fromPage(Page<T> page) {
        return new PagedResult<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
}