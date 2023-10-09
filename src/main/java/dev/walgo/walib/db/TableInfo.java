package dev.walgo.walib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * DB table metadata info.
 */
public class TableInfo {

    String catalog;
    String schema;
    String name;
    String type; // Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
    // "SYNONYM".
    String remarks;
    String idName; // name of the designated "identifier" column of a typed table (may be null)
    String idGenType; // specifies how values in SELF_REFERENCING_COL_NAME are created. Values are "SYSTEM", "USER",
    // "DERIVED". (may be null)
    List<String> keys;
    Map<String, ColumnInfo> fields;

    private final Connection conn;

    /**
     * Default constructor.
     *
     * @param connection Connection to DB
     * @param catalog    DB catalog (can be null for not-supported ar all)
     * @param schema     DB schema (can be null for not-supported or all)
     * @param tableName  DB table name
     */
    public TableInfo(Connection connection, String catalog, String schema, String tableName) {
        this.conn = connection;
        this.catalog = catalog;
        this.schema = schema;
        this.name = tableName;
    }

    /**
     * Constructor.
     *
     * Set all string fields from
     * {@link #TableInfo(java.sql.Connection, java.lang.String, java.lang.String, java.lang.String)} to null
     *
     * @param connection Connection to DB
     */
    public TableInfo(Connection connection) {
        this.conn = connection;
    }

    /**
     * Gets table catalog
     *
     * @return the catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Gets table schema
     *
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Gets table name
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets table type
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets table comment
     *
     * @return the comment
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Gets table id name
     *
     * @return the id name
     */
    public String getIdName() {
        return idName;
    }

    /**
     * Gets table id generator type
     *
     * @return the id generator type
     */
    public String getIdGenType() {
        return idGenType;
    }

    /**
     * Gets table primary keys
     *
     * @return primary keys
     * @throws java.sql.SQLException throw when error
     */
    public List<String> getKeys() throws SQLException {
        if (keys == null) {
            keys = new ArrayList<>();
            DatabaseMetaData meta = conn.getMetaData();
            String locSchema = schema == null ? null : schema; // StrTool.replaceString(schema,"_", "\\_");
            String locTable = name; // StrTool.replaceString(name,"_", "\\_");
            ResultSet rs = meta.getPrimaryKeys(catalog, locSchema, locTable);
            while (rs.next()) {
                keys.add(rs.getString("COLUMN_NAME"));
            }
        }
        return keys;
    }

    /**
     * Gets table fields
     *
     * @return map column name in lower case -> column info
     * @throws java.sql.SQLException throw when error
     */
    public Map<String, ColumnInfo> getFields() throws SQLException {
        if (fields == null) {
            DatabaseMetaData meta = conn.getMetaData();
            String locSchema = DBUtils.maskPattern(schema);
            String locTable = DBUtils.maskPattern(name);
            try (ResultSet rs = meta.getColumns(catalog, locSchema, locTable, null)) {
                fields = new TreeMap<>();
                getKeys();
                while (rs.next()) {
                    ColumnInfo.Builder field = new ColumnInfo.Builder();
                    field.catalog(rs.getString("TABLE_CAT")); // table catalog (may be null)
                    field.schema(rs.getString("TABLE_SCHEM")); // table schema (may be null)
                    field.ownerName(rs.getString("TABLE_NAME")); // table name
                    String fieldName = rs.getString(AbstractFieldInfo.COLUMN_COLUMN_NAME);
                    field.name(fieldName); // column name
                    field.type(rs.getInt(AbstractFieldInfo.COLUMN_DATA_TYPE)); // SQL type from java.sql.Types
                    field.typeName(rs.getString(AbstractFieldInfo.COLUMN_TYPE_NAME)); // Data source dependent type
                                                                                      // name, for a
                                                                                      // UDT the type name
                    // is fully qualified
                    field.size(rs.getInt("COLUMN_SIZE")); // column size. For char or date types this is the maximum
                                                          // number
                    // of characters, for numeric or decimal types this is precision.
                    field.digits(rs.getInt("DECIMAL_DIGITS")); // the number of fractional digits
                    field.radix(rs.getInt("NUM_PREC_RADIX")); // Radix (typically either 10 or 2)
                    field.isNullable(rs.getInt(AbstractFieldInfo.COLUMN_NULLABLE) == DatabaseMetaData.columnNullable); // is
                                                                                                                       // NULL
                                                                                                                       // allowed.
                    field.comment(rs.getString(AbstractFieldInfo.COLUMN_REMARKS)); // comment describing column (may be
                                                                                   // null)
                    field.defaultValue(rs.getString("COLUMN_DEF")); // default value (may be null)
                    field.position(rs.getInt(AbstractFieldInfo.COLUMN_ORDINAL_POSITION)); // index of column in table
                                                                                          // (starting at
                                                                                          // 1)
                    field.isPrimaryKey(keys.contains(fieldName));
                    fields.put(fieldName.toLowerCase(), field.build());
                }
            }
        }
        return fields;
    }

    @Override
    public String toString() {
        return "TableInfo{"
                + super.toString()
                + ",catalog=" + catalog
                + ", schema=" + schema
                + ", name=" + name
                + ", type=" + type
                + ", remarks=" + remarks
                + ", idName=" + idName
                + ", idGenType=" + idGenType
                + ", keys=" + keys
                + ", fields=" + fields
                + '}';
    }

}
