package com.app.erp.inventoryrefactor.common;

import java.util.List;

public record PageResult<T>(long totalCount, List<T> items) {}
