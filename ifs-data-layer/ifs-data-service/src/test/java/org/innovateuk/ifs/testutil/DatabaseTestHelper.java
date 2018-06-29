package org.innovateuk.ifs.testutil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * TODO DW - document this class
 */
@Component
public class DatabaseTestHelper {

    @Value("${flyway.url}")
    private String databaseUrl;

    @Value("${flyway.user}")
    private String databaseUser;

    @Value("${flyway.password}")
    private String databasePassword;

    public void assertingNoDatabaseChangesOccur(Runnable runnable) {

        try {
            int startingHash = getDatabaseHash();

            try {
                runnable.run();
            } catch (Exception e) {
                int endingHash = getDatabaseHash();

                assertThat(startingHash).isEqualTo(endingHash);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private int getDatabaseHash() throws SQLException {

        Connection conn = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);

        int dbHash = 0;

        while (rs.next()) {
            String tableName = rs.getString(3);
            CallableStatement selectAll = conn.prepareCall("SELECT * FROM " + tableName);
            ResultSet resultsFromTable = selectAll.executeQuery();

            int tableHash = 0;

            while (resultsFromTable.next()) {
                ResultSetMetaData tableMetadata = resultsFromTable.getMetaData();
                int columnCount = tableMetadata.getColumnCount();

                int rowHash = IntStream.range(1, columnCount + 1).mapToObj(i -> {
                    try {
                        Object cell = resultsFromTable.getObject(i);
                        return cell != null ? cell.toString() : "null";
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).mapToInt(String::hashCode).sum();

                tableHash += rowHash;
            }

            dbHash += tableHash;
        }

        long after = System.currentTimeMillis();

        return dbHash;
    }
}
