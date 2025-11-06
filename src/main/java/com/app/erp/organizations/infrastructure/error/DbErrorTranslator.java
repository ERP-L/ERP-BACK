package com.app.erp.organizations.infrastructure.error;

import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.exceptions.ResourceConflictException;

import java.sql.SQLException;

/**
 * Traduce errores de BD a excepciones de aplicación, preservando el código y mensaje reales.
 * Por ahora, mapea genérico y deja hooks para códigos conocidos.
 */
public final class DbErrorTranslator {
    private DbErrorTranslator() {}

    /**
     * Traducción mínima: conserva mensaje y causa. Puedes ampliar con mapeos por errorCode si el SP los define.
     */
    public static RuntimeException translate(SQLException ex) {
        int code = ex.getErrorCode();
        String msg = ex.getMessage();

        // Hooks de ejemplo: ajusta cuando acuerdes códigos de negocio del SP
        // if (code == 51001) return new ResourceConflictException(msg);
        // if (code == 51002) return new NotFoundException(msg);

        return new ApplicationException(msg, ex);
    }
}
