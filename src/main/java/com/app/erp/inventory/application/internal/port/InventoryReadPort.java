package com.app.erp.inventory.application.internal.port;

/** Lecturas del BC Inventory para validaciones previas. */
public interface InventoryReadPort {
    /**
     * @return CompanyID dueño de la categoría, o null si CategoryID no existe.
     */
    Integer getCategoryCompanyId(int categoryId);
}
