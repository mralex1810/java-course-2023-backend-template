package edu.java.scrapper;

import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MigrationCheckTest extends IntegrationTest {

    Connection connection;

    @BeforeEach
    public void init() throws SQLException {
        connection = POSTGRES.createConnection("");
    }

    @Test
    public void testLinksTableExists() throws SQLException {
        connection.prepareCall("SELECT * FROM links").execute();
    }
}
