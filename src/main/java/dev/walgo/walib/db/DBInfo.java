package dev.walgo.walib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Database metadata
 */
public class DBInfo {

    private final Connection conn;
    private final String catalog;
    private final String schema;
    private final String namePattern;

    private List<TableInfo> tables;
    private List<CallableInfo> functions;
    private List<CallableInfo> procedures;

    /**
     * Default constructor.
     *
     * @param connection  Connection to DB
     * @param catalog     DB catalog (can be null for not-supported ar all)
     * @param schema      DB schema (can be null for not-supported or all)
     * @param namePattern DB object name pattern (can be null for all). Like pattern syntax should be used
     */
    public DBInfo(Connection connection, String catalog, String schema, String namePattern) {
        this.conn = connection;
        this.catalog = catalog;
        this.schema = schema;
        this.namePattern = namePattern;
    }

    /**
     * Gets DB tables.
     * 
     * @return DB tables metadata map: table name in lower case to table info
     * @throws SQLException table info extraction error
     */
    public Map<String, TableInfo> getTablesAsMap() throws SQLException {
        return getTables().stream()
                .collect(Collectors.toMap(it -> it.name.toLowerCase(), it -> it));
    }

    /**
     * Gets DB tables.
     *
     * @return DB tables metadata list
     * @throws SQLException throw when error
     */
    public List<TableInfo> getTables() throws SQLException {
        if (tables == null) {
            DatabaseMetaData meta = conn.getMetaData();
            String locSchema = DBUtils.maskPattern(schema);
            String locName = DBUtils.maskPattern(namePattern);
            try (ResultSet rs = meta.getTables(catalog, locSchema, locName, null)) {
                tables = new ArrayList<>();
                while (rs.next()) {
                    TableInfo table = new TableInfo(conn);

                    table.catalog = rs.getString("TABLE_CAT"); // table catalog (may be null)
                    table.schema = rs.getString("TABLE_SCHEM"); // table schema (may be null)
                    table.name = rs.getString("TABLE_NAME"); // table name
                    table.type = rs.getString("TABLE_TYPE"); // SQL type from java.sql.Types
                    table.remarks = rs.getString(AbstractFieldInfo.COLUMN_REMARKS); // comment describing column (may be
                                                                                    // null)
//   table.idName = rs.getString("SELF_REFERENCING_COL_NAME"); // default value (may be null)
//   table.idGenType = rs.getString("REF_GENERATION"); // default value (may be null)
                    tables.add(table);
                }
            }
        }
        return tables;
    }

    /**
     * Gets DB functions.
     *
     * @return DB functions metadata list
     * @throws SQLException throw when error
     */
    public List<CallableInfo> getFunctions() throws SQLException {
        if (functions == null) {
            DatabaseMetaData meta = conn.getMetaData();
            String locSchema = DBUtils.maskPattern(schema);
            String locName = DBUtils.maskPattern(namePattern);
            try (ResultSet rs = meta.getFunctions(catalog, locSchema, locName)) {
                functions = new ArrayList<>();
                while (rs.next()) {
                    CallableInfo func = new CallableInfo(conn);
                    func.catalog = rs.getString("FUNCTION_CAT"); // function catalog (may be null)
                    func.schema = rs.getString("FUNCTION_SCHEM"); // function schema (may be null)
                    func.name = rs.getString("FUNCTION_NAME"); // function name
                    func.comment = rs.getString(AbstractFieldInfo.COLUMN_REMARKS); // explanatory comment on the
                                                                                   // procedure
                    func.type = CallableType.fromFunction(rs.getShort("FUNCTION_TYPE")); // kind of function
                    functions.add(func);
                }
            }
        }
        return functions;
    }

    /**
     * Gets DB procedures.
     *
     * @return DB procedures metadata list
     * @throws SQLException throw when error
     */
    public List<CallableInfo> getProcedures() throws SQLException {
        if (procedures != null) {
            DatabaseMetaData meta = conn.getMetaData();
            String locSchema = DBUtils.maskPattern(schema);
            String locName = DBUtils.maskPattern(namePattern);
            try (ResultSet rs = meta.getProcedures(catalog, locSchema, locName)) {
                procedures = new ArrayList<>();
                while (rs.next()) {
                    CallableInfo proc = new CallableInfo(conn);
                    proc.catalog = rs.getString("PROCEDURE_CAT"); // procedure catalog (may be null)
                    proc.schema = rs.getString("PROCEDURE_SCHEM"); // procedure schema (may be null)
                    proc.name = rs.getString("PROCEDURE_NAME"); // procedure name
                    proc.comment = rs.getString(AbstractFieldInfo.COLUMN_REMARKS); // explanatory comment on the
                                                                                   // procedure
                    proc.type = CallableType.fromProcedure(rs.getShort("PROCEDURE_TYPE")); // kind of procedure
                    procedures.add(proc);
                }
            }
        }
        return procedures;
    }

}
