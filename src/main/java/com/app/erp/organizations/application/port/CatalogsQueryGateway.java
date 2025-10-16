package com.app.erp.organizations.application.port;

/** Consultas al BC de catálogos (Ubigeo). */
public interface CatalogsQueryGateway {
    /** Verifica existencia de Ubigeo por código exacto (DDPPDD). */
    boolean existsUbigeo(String ubigeoId);
}
