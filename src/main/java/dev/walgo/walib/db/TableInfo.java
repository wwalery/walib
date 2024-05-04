package dev.walgo.walib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** DB table metadata info. */
public class TableInfo {

//    private static final Logger LOG = LoggerFactory.getLogger(TableInfo.class);

    String catalog;
    String schema;
    String name;
    String type; // Typical types are "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL
                 // TEMPORARY", "ALIAS",
    // "SYNONYM".
    String remarks;
    String idName; // name of the designated "identifier" column of a typed table (may be null)
    String idGenType; // specifies how values in SELF_REFERENCING_COL_NAME are created. Values are
                      // "SYSTEM", "USER",
    // "DERIVED". (may be null)
    List<String> keys;
    Map<String, ColumnInfo> fields;

    private final Connection conn;

    private ResultSetMetaData metaData;

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
     * <p>
     * Set all string fields from {@link #TableInfo(java.sql.Connection, java.lang.String,
     * java.lang.String, java.lang.String)} to null
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
//                ResultSetMetaData rsMeta = rs.getMetaData();
                while (rs.next()) {
                    ColumnInfo.Builder field = new ColumnInfo.Builder();
                    field.catalog(rs.getString("TABLE_CAT")); // table catalog (may be null)
                    field.schema(rs.getString("TABLE_SCHEM")); // table schema (may be null)
                    field.ownerName(rs.getString("TABLE_NAME")); // table name
                    String fieldName = rs.getString(AbstractFieldInfo.COLUMN_COLUMN_NAME);
                    field.name(fieldName); // column name
                    field.type(rs.getInt(AbstractFieldInfo.COLUMN_DATA_TYPE)); // SQL type from java.sql.Types
                    // Data source dependent type name, for a UDT the type name
                    String typeName = rs.getString(AbstractFieldInfo.COLUMN_TYPE_NAME);
                    // Workaround for PostgreSQL
                    // TODO Can be replaced with specific database class with custom behavior
                    if (typeName.startsWith("_")) {
                        typeName = typeName.substring(1);
                    }
                    field.typeName(typeName);
                    // is fully qualified column size. For char or date types this is the maximum
                    field.size(rs.getInt("COLUMN_SIZE"));
                    // number of characters, for numeric or decimal types this is precision.
                    field.digits(rs.getInt("DECIMAL_DIGITS")); // the number of fractional digits
                    field.radix(rs.getInt("NUM_PREC_RADIX")); // Radix (typically either 10 or 2)
                    // is NULL allowed
                    field.isNullable(
                            rs.getInt(AbstractFieldInfo.COLUMN_NULLABLE) == DatabaseMetaData.columnNullable);
                    // comment describing column (may be null)
                    field.comment(rs.getString(AbstractFieldInfo.COLUMN_REMARKS));
                    // default value (may be null)
                    field.defaultValue(rs.getString("COLUMN_DEF"));
                    // index of column in table (starting at 1)
                    field.position(rs.getInt(AbstractFieldInfo.COLUMN_ORDINAL_POSITION));

                    field.isPrimaryKey(keys.contains(fieldName));
                    fields.put(fieldName.toLowerCase(Locale.ROOT), field.build());

//                    for (int idx = 1; idx <= rsMeta.getColumnCount(); idx++) {
//                        LOG.info(
//                                "{} ({}): [{}]",
//                                rsMeta.getColumnName(idx),
//                                rsMeta.getColumnClassName(idx),
//                                rs.getString(idx));
//                    }
                }
            }
        }
        return fields;
    }

    /**
     * Gets table metadata based on table result set columns
     * 
     * @return table result set meta data
     * @throws SQLException
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        if (metaData != null) {
            return metaData;
        }
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM %s WHERE 0 = 1".formatted(name))) {
                metaData = rs.getMetaData();
            }
        }
        return metaData;
    }

    @Override
    public String toString() {
        return "TableInfo{"
                + super.toString()
                + ",catalog="
                + catalog
                + ", schema="
                + schema
                + ", name="
                + name
                + ", type="
                + type
                + ", remarks="
                + remarks
                + ", idName="
                + idName
                + ", idGenType="
                + idGenType
                + ", keys="
                + keys
                + ", fields="
                + fields
                + '}';
    }
}
