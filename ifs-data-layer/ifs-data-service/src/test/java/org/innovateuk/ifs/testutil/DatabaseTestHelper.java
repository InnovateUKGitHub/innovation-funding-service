package org.innovateuk.ifs.testutil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Helper class to assert that no database changes have occurred during a given piece of work.
 */
@Component
public class DatabaseTestHelper {

    @Value("${spring.flyway.url}")
    private String databaseUrl;

    @Value("${spring.flyway.user}")
    private String databaseUser;

    @Value("${spring.flyway.password}")
    private String databasePassword;

    /**
     * Assert that no database changes occur during the running of the given action
     */
    public void assertingNoDatabaseChangesOccur(Runnable action) {

        assertingNoDatabaseChangesOccur(() -> {
            action.run();
            return null;
        });
    }

    /**
     * Assert that no database changes occur during the running of the given action
     */
    public <T> T assertingNoDatabaseChangesOccur(Supplier<T> action) {

        try {
            String startingContent = getDatabaseContents();

            try {
                return action.get();
            } finally {
                String endingContent = getDatabaseContents();

                assertThat(startingContent).isEqualTo(endingContent);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getDatabaseContents() throws SQLException {

        Connection connection = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        DatabaseMetaData schemaMetadata = connection.getMetaData();
        ResultSet schemaResults = schemaMetadata.getTables(null, null, "%", null);

        String schemaAsString = "";

        while (schemaResults.next()) {

            String tableName = schemaResults.getString(3);
            CallableStatement tableResultsQuery = connection.prepareCall("SELECT * FROM " + tableName);
            ResultSet tableResults = tableResultsQuery.executeQuery();

            String tableAsString = "";

            while (tableResults.next()) {

                ResultSetMetaData tableMetadata = tableResults.getMetaData();
                int columnCount = tableMetadata.getColumnCount();

                String rowAsString = IntStream.range(1, columnCount + 1).mapToObj(i -> {
                    try {
                        Object cell = tableResults.getObject(i);
                        return cell != null ? cell.toString() : "null";
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(joining(","));

                tableAsString += rowAsString + "\n";
            }

            schemaAsString += tableAsString;
        }

        return schemaAsString;
    }
}
