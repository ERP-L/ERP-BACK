package com.app.erp.inventory.application.dtos.results;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecentMovementResult {
    private String tipo;
    private String producto;
    private BigDecimal cantidad;
    private LocalDate fecha;
    private String usuario;
    private String referencia;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
