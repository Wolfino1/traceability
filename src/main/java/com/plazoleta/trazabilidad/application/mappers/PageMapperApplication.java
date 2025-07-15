package com.plazoleta.trazabilidad.application.mappers;

import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageMapperApplication {
    public <T> PagedResult<T> fromPage(List<T> content, PagedResult<?> r) {
        return new PagedResult<>(
                content,
                r.getPage(),
                r.getSize(),
                r.getTotalElements()
        );
    }
}