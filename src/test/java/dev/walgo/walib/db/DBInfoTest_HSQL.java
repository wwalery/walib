//CHECKSTYLE:OFF
package dev.walgo.walib.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DBInfoTest_HSQL {

    private static final String PUBLIC = "PUBLIC";

    private static final String DB_USER = "sa";
    private static final String DB_URL = "jdbc:hsqldb:mem:testdb";
    private static final String TABLE_1 = "TEST_TABLE_1";
    private static final String DB_SQL = "src/test/resources/db/create_db_hsql.sql";

    private static Connection conn;

    /**
     * Initializer.
     * 
     * @throws SQLException SQLException
     * @throws IOException  IOException
     * @throws SqlToolError SqlToolError
     */
    @BeforeAll
    public static void before() throws SQLException, IOException, SqlToolError {
        conn = DriverManager.getConnection(DB_URL, DB_USER, "");
        try (InputStream inputStream = new FileInputStream(DB_SQL)) {
            SqlFile sqlFile = new SqlFile(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                    "init",
                    System.out,
                    "UTF-8",
                    false,
                    new File("."));
            sqlFile.setConnection(conn);
            sqlFile.execute();
        }
    }

    /**
     * Destroyer.
     * 
     * @throws SQLException SQLException
     * @throws IOException  IOException
     */
    @AfterAll
    public static void after() throws SQLException, IOException {
        conn.close();
        // Files.delete(Paths.get("testdb.log"));
        // Files.delete(Paths.get("testdb.properties"));
        // Files.delete(Paths.get("testdb.script"));
        // Files.delete(Paths.get("testdb.tmp"));
    }

    @Test
    public void testGetTables() throws SQLException {
        DBInfo info = new DBInfo(conn, null, null /* "PUBLIC" */, "TEST_TABLE%");

        List<TableInfo> tables = info.getTables();
        assertThat(tables)
                .isNotNull()
                .hasSize(3);
        TableInfo table1 = tables.get(0);
        assertThat(table1.getName()).isEqualTo(TABLE_1);
        assertThat(table1.getType()).isEqualTo("TABLE");
        assertThat(table1.getCatalog()).isEqualTo(PUBLIC);
        assertThat(table1.getSchema()).isEqualTo(PUBLIC);
        List<String> keys = table1.getKeys();
        assertThat(keys)
                .isNotNull()
                .hasSize(1);
        assertThat(keys.get(0)).isEqualTo("ID");
        Map<String, ColumnInfo> fields = table1.getFields();
        assertThat(fields)
                .isNotNull()
                .hasSize(20);

        // id
        int fieldNum = 0;
        ColumnInfo field = fields.get("id");
        assertThat(field.name()).isEqualTo("ID");
        assertThat(field.isNullable()).isFalse();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.INTEGER);
        assertThat(field.typeName()).isEqualTo("INTEGER");
        assertThat(field.isPrimaryKey()).isTrue();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // enum_field
        fieldNum = 1;
        field = fields.get("enum_field");
        assertThat(field.name()).isEqualTo("ENUM_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.VARCHAR);
        assertThat(field.typeName()).isEqualTo("VARCHAR");
        assertThat(field.size()).isEqualTo(50);
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isFalse();
        assertThat(field.isString()).isTrue();

        // big_field
        fieldNum = 2;
        field = fields.get("big_field");
        assertThat(field.name()).isEqualTo("BIG_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.VARCHAR);
        assertThat(field.typeName()).isEqualTo("VARCHAR");
        assertThat(field.size()).isEqualTo(1000);
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isFalse();
        assertThat(field.isString()).isTrue();

        // read_only
        fieldNum = 3;
        field = fields.get("read_only");
        assertThat(field.name()).isEqualTo("READ_ONLY");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.INTEGER);
        assertThat(field.typeName()).isEqualTo("INTEGER");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // is_deleted
        fieldNum = 4;
        field = fields.get("is_deleted");
        assertThat(field.name()).isEqualTo("IS_DELETED");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.TINYINT);
        assertThat(field.typeName()).isEqualTo("TINYINT");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // double_field
        fieldNum = 5;
        field = fields.get("double_field");
        assertThat(field.name()).isEqualTo("DOUBLE_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.DOUBLE);
        assertThat(field.typeName()).isEqualTo("DOUBLE");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

    }
}
