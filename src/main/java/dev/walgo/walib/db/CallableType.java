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
        switch (procedureReturnType) {
            case DatabaseMetaData.procedureResultUnknown:
                return UNKNOWN;
            case DatabaseMetaData.procedureNoResult:
                return VOID;
            case DatabaseMetaData.procedureReturnsResult:
                return VALUE;
            default:
                return UNKNOWN;
        }
    }

    static CallableType fromFunction(short functionReturnType) {
        switch (functionReturnType) {
            case DatabaseMetaData.functionResultUnknown:
                return UNKNOWN;
            case DatabaseMetaData.functionNoTable:
                return VALUE;
            case DatabaseMetaData.functionReturnsTable:
                return TABLE;
            default:
                return UNKNOWN;
        }
    }

}
