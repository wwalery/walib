package dev.walgo.walib.db;

import java.sql.Types;
import org.apache.commons.lang3.StringUtils;

/**
 * Various custom DM methods
 */
public class DBUtils {

    private DBUtils() {
        // do nothing
    }

    static String maskPattern(String pattern) {
        return pattern != null ? StringUtils.replace(pattern, "_", "\\_") : null;
    }

    /**
     * Is field has string type?
     * 
     * @param fieldType JDBC SQL type
     * @return TRUE if it's string field
     */
    public static boolean isStringField(final int fieldType) {
        return fieldType == Types.CHAR
                || fieldType == Types.CLOB
                || fieldType == Types.LONGNVARCHAR
                || fieldType == Types.LONGVARCHAR
                || fieldType == Types.NCHAR
                || fieldType == Types.NCLOB
                || fieldType == Types.NVARCHAR
                || fieldType == Types.VARCHAR;
    }

    /**
     * Is field has numeric type?
     * 
     * @param fieldType JDBC SQL type
     * @return TRUE if it's numeric field
     */
    public static boolean isNumericField(final int fieldType) {
        return fieldType == Types.BIGINT
                || fieldType == Types.DECIMAL
                || fieldType == Types.DOUBLE
                || fieldType == Types.FLOAT
                || fieldType == Types.INTEGER
                || fieldType == Types.NUMERIC
                || fieldType == Types.REAL
                || fieldType == Types.SMALLINT
                || fieldType == Types.TINYINT;
    }

}
