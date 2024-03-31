package dev.walgo.walib.db;

import javax.annotation.Nullable;

/**
 * Common field info for table and callable statements
 */
public abstract class AbstractFieldInfo {

    static final String COLUMN_REMARKS = "REMARKS";
    static final String COLUMN_COLUMN_NAME = "COLUMN_NAME";
    static final String COLUMN_FUNCTION_CAT = "FUNCTION_CAT";
    static final String COLUMN_RADIX = "RADIX";
    static final String COLUMN_FUNCTION_NAME = "FUNCTION_NAME";
    static final String COLUMN_NULLABLE = "NULLABLE";
    static final String COLUMN_DATA_TYPE = "DATA_TYPE";
    static final String COLUMN_SCALE = "SCALE";
    static final String COLUMN_PRECISION = "PRECISION";
    static final String COLUMN_TYPE_NAME = "TYPE_NAME";
    static final String COLUMN_ORDINAL_POSITION = "ORDINAL_POSITION";
    static final String COLUMN_FUNCTION_SCHEM = "FUNCTION_SCHEM";
    static final String COLUMN_COLUMN_TYPE = "COLUMN_TYPE";

    /**
     * Table catalog.
     * 
     * @return table catalog (may be null)
     */
    @Nullable
    public abstract String catalog();

    /**
     * Table schema.
     * 
     * @return table schema (may be null)
     */
    @Nullable
    public abstract String schema();

    /**
     * Table/procedure/function name.
     * 
     * @return table/procedure/function name
     */
    public abstract String ownerName();

    /**
     * Column name.
     * 
     * @return column name
     */
    public abstract String name();

    /**
     * SQL type from java.sql.Types.
     * 
     * @return SQL type from java.sql.Types
     */
    public abstract int type();

    /**
     * Data source dependent type name, for a UDT the type name is fully qualified.
     * 
     * @return Data source dependent type name
     */
    public abstract String typeName();

    /**
     * Column size.
     * For char or date types this is the maximum number of characters, for numeric or decimal types this is precision.
     * 
     * @return column size
     */
    public abstract int size();

    /**
     * The number of fractional digits.
     * 
     * @return the number of fractional digits
     */
    public abstract int digits();

    /**
     * Radix.
     * 
     * typically either 10 or 2.
     * 
     * @return radix
     */
    public abstract int radix();

    /**
     * Is NULL allowed.
     * 
     * columnNoNulls - might not allow NULL values
     * columnNullable - definitely allows NULL values
     * columnNullableUnknown - nullability unknown
     * 
     * @return is NULL allowed
     */
    public abstract boolean isNullable(); // .

    /**
     * Comment describing column.
     * 
     * @return comment describing column (may be null)
     */
    @Nullable
    public abstract String comment();

    /**
     * Default value.
     * 
     * @return default value (may be null)
     */
    @Nullable
    public abstract String defaultValue();

    /**
     * Index of column in table.
     * 
     * @return index of column in table (starting at 1)
     */
    public abstract int position();

    /**
     * Is field has string type?
     * 
     * @return TRUE if it's string field
     */
    public boolean isString() {
        return DBUtils.isStringField(type());
    }

    /**
     * Is field has numeric type?
     * 
     * @return TRUE if it's numeric field
     */
    public boolean isNumeric() {
        return DBUtils.isNumericField(type());
    }

}
