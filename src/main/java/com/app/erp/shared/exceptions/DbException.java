package com.app.erp.shared.exceptions;

public class DbException extends ApplicationException {
    private final int dbCode;
    private final String sqlState;

    public DbException(String message, int dbCode, String sqlState) {
        super(message);
        this.dbCode = dbCode;
        this.sqlState = sqlState;
    }

    public int getDbCode() { return dbCode; }
    public String getSqlState() { return sqlState; }
}
