package dev.walgo.walib.db;

import java.sql.DatabaseMetaData;

/**
 * Result type for callable.
 */
public enum CallableType {

    /**
     * Unknown return type.
     */
    UNKNOWN,
    /**
     * Callable returns nothing.
     */
    VOID,
    /**
     * Callable returns value.
     */
    VALUE,
    /**
     * Callable returns table.
     */
    TABLE;

    static CallableType fromProcedure(short procedureReturnType) {
        return switch (procedureReturnType) {
            case DatabaseMetaData.procedureResultUnknown -> UNKNOWN;
            case DatabaseMetaData.procedureNoResult -> VOID;
            case DatabaseMetaData.procedureReturnsResult -> VALUE;
            default -> UNKNOWN;
        };
    }

    static CallableType fromFunction(short functionReturnType) {
        return switch (functionReturnType) {
            case DatabaseMetaData.functionResultUnknown -> UNKNOWN;
            case DatabaseMetaData.functionNoTable -> VALUE;
            case DatabaseMetaData.functionReturnsTable -> TABLE;
            default -> UNKNOWN;
        };
    }

}
