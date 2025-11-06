package com.app.erp.shared.infrastructure.error;

import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.shared.exceptions.DbException;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.exceptions.ResourceConflictException;

import java.sql.SQLException;

/**
 * Traduce SQLException a excepciones de aplicación preservando código y mensaje.
 */
public final class DbErrorTranslator {
    private DbErrorTranslator() {}

    public static RuntimeException translate(SQLException ex) {
        int code = ex.getErrorCode();
        String msg = ex.getMessage();
        String sqlState = ex.getSQLState();

        // Unique/dup errors
        if (code == 2601 || code == 2627 || code == 50002) {
            return new ResourceConflictException(attachDbInfo(msg, code, sqlState));
        }

        // Duplicate referenceNumber (business-level idempotency)
        if ((code >= 50010 && code <= 50013) || (code >= 60010 && code <= 60013)) {
            return new ResourceConflictException(attachDbInfo(msg, code, sqlState));
        }

        // Validation / business rule errors (map to InvalidInput)
        if ((code >= 60020 && code <= 60060) || code == 50030 || code == 50040 || code == 50050 || code == 50051 || code == 50060 || code == 60056) {
            return new InvalidInputException(attachDbInfo(msg, code, sqlState));
        }

        // Not found-like
        if (code == 50001 || code == 50012 || code == 50010) {
            return new NotFoundException(attachDbInfo(msg, code, sqlState));
        }

        // Default: wrap in DbException preserving codes
        return new DbException(attachDbInfo(msg, code, sqlState), code, sqlState);
    }

    private static String attachDbInfo(String msg, int code, String sqlState) {
        return String.format("%s (dbCode=%d, sqlState=%s)", msg, code, sqlState);
    }
}
