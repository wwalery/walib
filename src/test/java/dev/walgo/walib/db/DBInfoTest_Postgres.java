//CHECKSTYLE:OFF
package dev.walgo.walib.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

public class DBInfoTest_Postgres {

    private static final String PUBLIC = "PUBLIC";

    private static final String DB_NAME = "test";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "password";
    private static final String TABLE_1 = "TEST_TABLE_1";
    private static final String DB_SQL = "db/create_db_postgres.sql";

    private static final PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USER)
            .withPassword(DB_PASSWORD)
            .withInitScript(DB_SQL);

    private static Connection conn;

    /**
     * Initializer.
     *
     * @throws SQLException SQLException
     * @throws IOException  IOException
     */
    @BeforeAll
    public static void before() throws SQLException, IOException {
        dbContainer.start();
        conn = DriverManager.getConnection(dbContainer.getJdbcUrl(), DB_USER, DB_PASSWORD);
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
        dbContainer.stop();
    }

    @Test
    public void testGetTables() throws SQLException {
        DBInfo info = new DBInfo(conn, null, null /* "PUBLIC" */, "test_table%");

        List<TableInfo> tables = info.getTables();
        assertThat(tables)
                .isNotNull()
                .hasSize(3);
        TableInfo table1 = tables.get(0);
        assertThat(table1.getName()).isEqualToIgnoringCase(TABLE_1);
        assertThat(table1.getType()).isEqualToIgnoringCase("TABLE");
        assertThat(table1.getCatalog()).isNull();
        assertThat(table1.getSchema()).isEqualToIgnoringCase(PUBLIC);
        List<String> keys = table1.getKeys();
        assertThat(keys)
                .isNotNull()
                .hasSize(1);
        assertThat(keys.get(0)).isEqualToIgnoringCase("ID");
        Map<String, ColumnInfo> fields = table1.getFields();
        assertThat(fields)
                .isNotNull()
                .hasSize(20);

        // id
        int fieldNum = 0;
        ColumnInfo field = fields.get("id");
        assertThat(field.name()).isEqualToIgnoringCase("ID");
        assertThat(field.isNullable()).isFalse();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.INTEGER);
        assertThat(field.typeName()).isEqualToIgnoringCase("int4");
        assertThat(field.isPrimaryKey()).isTrue();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // enum_field
        fieldNum = 1;
        field = fields.get("enum_field");
        assertThat(field.name()).isEqualToIgnoringCase("ENUM_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.VARCHAR);
        assertThat(field.typeName()).isEqualToIgnoringCase("VARCHAR");
        assertThat(field.size()).isEqualTo(50);
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isFalse();
        assertThat(field.isString()).isTrue();

        // big_field
        fieldNum = 2;
        field = fields.get("big_field");
        assertThat(field.name()).isEqualToIgnoringCase("BIG_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.VARCHAR);
        assertThat(field.typeName()).isEqualToIgnoringCase("VARCHAR");
        assertThat(field.size()).isEqualTo(1000);
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isFalse();
        assertThat(field.isString()).isTrue();

        // read_only
        fieldNum = 3;
        field = fields.get("read_only");
        assertThat(field.name()).isEqualToIgnoringCase("READ_ONLY");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.INTEGER);
        assertThat(field.typeName()).isEqualToIgnoringCase("int4");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // is_deleted
        fieldNum = 4;
        field = fields.get("is_deleted");
        assertThat(field.name()).isEqualToIgnoringCase("IS_DELETED");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.SMALLINT);
        assertThat(field.typeName()).isEqualToIgnoringCase("int2");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

        // double_field
        fieldNum = 5;
        field = fields.get("double_field");
        assertThat(field.name()).isEqualToIgnoringCase("DOUBLE_FIELD");
        assertThat(field.isNullable()).isTrue();
        assertThat(field.position()).isEqualTo(fieldNum + 1);
        assertThat(field.type()).isEqualTo(Types.DOUBLE);
        assertThat(field.typeName()).isEqualToIgnoringCase("float8");
        assertThat(field.isPrimaryKey()).isFalse();
        assertThat(field.isNumeric()).isTrue();
        assertThat(field.isString()).isFalse();

    }
}
