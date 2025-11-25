package com.app.erp.shared.dtos;

import java.util.List;

public record PagedResponse<T>(
    List<T> items,
    int pageNumber,
    int pageSize,
    long totalItems,
    int totalPages
) {}
