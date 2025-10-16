package dev.walgo.walib.db;

import java.sql.DatabaseMetaData;

/**
 * Column type for callable.
 */
public enum ColumnType {

    /**
     * Unknown column.
     */
    UNKNOWN,

    /**
     * Input column.
     */
    IN,

    /**
     * Input/Output column.
     */
    IN_OUT,

    /**
     * Output column.
     */
    OUT,

    /**
     * Return column.
     */
    RETURN,

    /**
     * Function/Procedure result.
     */
    RESULT;

    static ColumnType fromFunction(short functionColumnType) {
        return switch (functionColumnType) {
            case DatabaseMetaData.functionColumnUnknown -> UNKNOWN;
            case DatabaseMetaData.functionColumnIn -> IN;
            case DatabaseMetaData.functionColumnInOut -> IN_OUT;
            case DatabaseMetaData.functionColumnOut -> OUT;
            case DatabaseMetaData.functionColumnResult -> RESULT;
            default -> UNKNOWN;
        };
    }

    static ColumnType fromProcedure(short procedureColumnType) {
        return switch (procedureColumnType) {
            case DatabaseMetaData.procedureColumnUnknown -> UNKNOWN;
            case DatabaseMetaData.procedureColumnIn -> IN;
            case DatabaseMetaData.procedureColumnInOut -> IN_OUT;
            case DatabaseMetaData.procedureColumnOut -> OUT;
            case DatabaseMetaData.procedureColumnReturn -> RETURN;
            case DatabaseMetaData.procedureColumnResult -> RESULT;
            default -> UNKNOWN;
        };
    }

}
