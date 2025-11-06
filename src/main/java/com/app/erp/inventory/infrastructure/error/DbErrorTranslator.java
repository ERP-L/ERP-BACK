package com.app.erp.inventory.infrastructure.error;

import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.exceptions.ResourceConflictException;

import java.sql.SQLException;

/**
 * Traduce errores de SQL Server a excepciones de aplicación, preservando el código y mensaje reales.
 * Ajusta los códigos según los que retornen tus SPs de Inventory.
 */
public final class DbErrorTranslator {
    private DbErrorTranslator() {}

    public static RuntimeException translate(SQLException ex) {
        int code = ex.getErrorCode();
        String msg = ex.getMessage();

        // Mapeos de ejemplo basados en ProductCategoryWriteGateway y convenciones actuales
        switch (code) {
            case 50001: // referencia no encontrada (ej. compañía)
            case 50012: // categoría padre no encontrada
            case 50010: // categoría no encontrada
                return new NotFoundException(msg);
            case 50002: // duplicado nombre categoría
                return new ResourceConflictException(msg);
            case 50011: // validación/estado inválido
            case 50013: // otra regla de negocio
                return new ApplicationException(msg, ex);
            default:
                return new ApplicationException(msg, ex);
        }
    }
}
