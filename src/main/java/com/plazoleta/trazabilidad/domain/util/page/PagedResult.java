package com.plazoleta.trazabilidad.domain.util.page;

import com.plazoleta.trazabilidad.domain.exceptions.InvalidPaginationException;
import com.plazoleta.trazabilidad.domain.util.contants.DomainConstants;

import java.util.List;

public class PagedResult<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PagedResult(List<T> content, int page, int size, long totalElements) {
        if (page < 0) {
            throw new InvalidPaginationException(DomainConstants.PAGE_NUMBER_INVALID);
        }

        if (size <= 0) {
            throw new InvalidPaginationException(DomainConstants.PAGE_SIZE_INVALID);
        }

        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
