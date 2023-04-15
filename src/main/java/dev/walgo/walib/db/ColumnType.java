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
    switch (functionColumnType) {
      case DatabaseMetaData.functionColumnUnknown:
        return UNKNOWN;
      case DatabaseMetaData.functionColumnIn:
        return IN;
      case DatabaseMetaData.functionColumnInOut:
        return IN_OUT;
      case DatabaseMetaData.functionColumnOut:
        return OUT;
      case DatabaseMetaData.functionColumnResult:
        return RESULT;
      default:
        return UNKNOWN;
    }
  }

  static ColumnType fromProcedure(short procedureColumnType) {
    switch (procedureColumnType) {
      case DatabaseMetaData.procedureColumnUnknown:
        return UNKNOWN;
      case DatabaseMetaData.procedureColumnIn:
        return IN;
      case DatabaseMetaData.procedureColumnInOut:
        return IN_OUT;
      case DatabaseMetaData.procedureColumnOut:
        return OUT;
      case DatabaseMetaData.procedureColumnReturn:
        return RETURN;
      case DatabaseMetaData.procedureColumnResult:
        return RESULT;
      default:
        return UNKNOWN;
    }
  }

}
