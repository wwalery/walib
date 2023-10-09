package dev.walgo.walib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callable (function or procedure) info.
 */
public class CallableInfo {

    private static final Logger LOG = LoggerFactory.getLogger(CallableInfo.class);

    String catalog;
    String schema;
    String name;
    CallableType type;
    String comment;
    List<ParameterInfo> columns;
    boolean function;

    private final Connection conn;

    /**
     * Default constructor.
     * 
     * @param connection connection to DB
     * @param catalog    DB catalog
     * @param schema     DB schema
     * @param isFunction is callable function
     */
    public CallableInfo(Connection connection, String catalog, String schema, boolean isFunction) {
        this.conn = connection;
        this.catalog = catalog;
        this.schema = schema;
        this.function = isFunction;
    }

    /**
     * Constructor with "catalog", "schema" set to null.
     * 
     * @param connection connection to DB
     */
    public CallableInfo(Connection connection) {
        this.conn = connection;
    }

    /**
     * Gets callable name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets callable type.
     * 
     * @return the type
     */
    public CallableType getType() {
        return type;
    }

    /**
     * Gets callable comments.
     * 
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Is callable function?
     * 
     * @return function or not?
     */
    public boolean isFunction() {
        return function;
    }

    /**
     * Gets fields from callable with given name
     * 
     * @param namePattern callable name
     * @return field list
     * @throws SQLException when method got error
     */
    public List<ParameterInfo> getColumns(String namePattern) throws SQLException {
        if (columns != null) {
            return columns;
        } else if (function) {
            return getFunctionColumns(namePattern);
        } else {
            return getProcedureColumns(namePattern);
        }
    }

    private List<ParameterInfo> getProcedureColumns(String namePattern) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String locSchema = DBUtils.maskPattern(schema);
        String locName = DBUtils.maskPattern(namePattern);
        try (ResultSet rs = meta.getProcedureColumns(catalog, locSchema, locName, null)) {
            columns = new ArrayList<>();
            while (rs.next()) {
                ParameterInfo.Builder column = new ParameterInfo.Builder();
                column.catalog(rs.getString("PROCEDURE_CAT")); // table catalog (may be null)
                column.schema(rs.getString("PROCEDURE_SCHEM")); // table schema (may be null)
                column.ownerName(rs.getString("PROCEDURE_NAME")); // table name
                column.name(rs.getString(AbstractFieldInfo.COLUMN_COLUMN_NAME)); // column name
                column.columnType(ColumnType.fromProcedure(rs.getShort(AbstractFieldInfo.COLUMN_COLUMN_TYPE))); // column
                                                                                                                // type
                column.type(rs.getInt(AbstractFieldInfo.COLUMN_DATA_TYPE)); // SQL type from java.sql.Types
                column.typeName(rs.getString(AbstractFieldInfo.COLUMN_TYPE_NAME)); // Data source dependent type name,
                                                                                   // for a UDT
                                                                                   // the type name
                // is fully qualified
                column.size(rs.getInt(AbstractFieldInfo.COLUMN_PRECISION)); // column size. For char or date types this
                                                                            // is the
                                                                            // maximum number
                // of characters, for numeric or decimal types this is precision.
                column.digits(rs.getShort(AbstractFieldInfo.COLUMN_SCALE)); // scale - null is returned for data types
                                                                            // where
                                                                            // SCALE is not
                // applicable.
                column.radix(rs.getShort(AbstractFieldInfo.COLUMN_RADIX)); // Radix (typically either 10 or 2)
                column.isNullable(rs.getInt(AbstractFieldInfo.COLUMN_NULLABLE) == DatabaseMetaData.columnNullable); // is
                                                                                                                    // NULL
                                                                                                                    // allowed.
                try {
                    column.position(rs.getInt(AbstractFieldInfo.COLUMN_ORDINAL_POSITION)); // he ordinal position,
                                                                                           // starting from
                                                                                           // 1,
                } catch (SQLException ex) {
                    LOG.error("Column ORDINAL_POSITION not found in procedure info");
                }
//   for the input and output parameters for a procedure. A value of 0 is returned if this row describes the procedure's return value.
// For result set columns, it is the ordinal position of the column in the result set starting from 1.
// If there are multiple result sets, the column ordinal positions are implementation defined.
                columns.add(column.build());
            }
        }
        return columns;
    }

    private List<ParameterInfo> getFunctionColumns(String namePattern) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String locSchema = DBUtils.maskPattern(schema);
        String locName = DBUtils.maskPattern(namePattern);
        try (ResultSet rs = meta.getFunctionColumns(catalog, locSchema, locName, null)) {
            columns = new ArrayList<ParameterInfo>();
            while (rs.next()) {
                ParameterInfo.Builder column = new ParameterInfo.Builder();
                column.catalog(rs.getString(AbstractFieldInfo.COLUMN_FUNCTION_CAT)); // table catalog (may be null)
                column.schema(rs.getString(AbstractFieldInfo.COLUMN_FUNCTION_SCHEM)); // table schema (may be null)
                column.ownerName(rs.getString(AbstractFieldInfo.COLUMN_FUNCTION_NAME)); // table name
                column.name(rs.getString(AbstractFieldInfo.COLUMN_COLUMN_NAME)); // column name
                column.columnType(ColumnType.fromFunction(rs.getShort(AbstractFieldInfo.COLUMN_COLUMN_TYPE))); // column
                                                                                                               // type
                column.type(rs.getInt(AbstractFieldInfo.COLUMN_DATA_TYPE)); // SQL type from java.sql.Types
                column.typeName(rs.getString(AbstractFieldInfo.COLUMN_TYPE_NAME)); // Data source dependent type name,
                                                                                   // for a UDT
                                                                                   // the type name
                // is fully qualified
                column.size(rs.getInt(AbstractFieldInfo.COLUMN_PRECISION)); // column size. For char or date types this
                                                                            // is the
                                                                            // maximum number
                // of characters, for numeric or decimal types this is precision.
                column.digits(rs.getShort(AbstractFieldInfo.COLUMN_SCALE)); // scale - null is returned for data types
                                                                            // where
                                                                            // SCALE is not
                // applicable.
                column.radix(rs.getShort(AbstractFieldInfo.COLUMN_RADIX)); // Radix (typically either 10 or 2)
                column.isNullable(rs.getInt(AbstractFieldInfo.COLUMN_NULLABLE) == DatabaseMetaData.columnNullable); // is
                                                                                                                    // NULL
                                                                                                                    // allowed.
                column.comment(rs.getString(AbstractFieldInfo.COLUMN_REMARKS)); // comment describing column/parameter
                column.position(rs.getInt(AbstractFieldInfo.COLUMN_ORDINAL_POSITION)); // the ordinal position, starting
                                                                                       // from 1,
                // for the input and output parameters. A value of 0 is returned if this row describes the function's
                // return value.
                // For result set columns, it is the ordinal position of the column in the result set starting from 1
                columns.add(column.build());
            }
        }
        return columns;
    }

}
