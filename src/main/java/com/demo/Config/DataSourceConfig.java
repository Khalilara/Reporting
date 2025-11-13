package com.demo.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static final String TARGET_DB = "sales_db";

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            String finalJdbcUrl = ensureDatabaseExistsAndReturnJdbcUrlForTarget(jdbcUrl, TARGET_DB);
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(finalJdbcUrl);
            ds.setUsername(username);
            ds.setPassword(password);
            return ds;
        } catch (SQLException e) {
            logger.error("Unable to prepare database '{}' : {}", TARGET_DB, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensure the target database exists and return a JDBC URL that points to it.
     * It will try to connect to the target DB first; if not present it will
     * connect to the administrative 'postgres' database and create the target DB.
     */
    private String ensureDatabaseExistsAndReturnJdbcUrlForTarget(String originalUrl, String targetDb) throws SQLException {
        // parse jdbc url: jdbc:postgresql://host:port[/dbname][?params]
        Pattern p = Pattern.compile("jdbc:postgresql://([^/]+)(?:/([^?]+))?(\\?.*)?");
        Matcher m = p.matcher(originalUrl);
        if (!m.matches()) {
            logger.warn("Unable to parse JDBC URL '{}', using as-is", originalUrl);
            return originalUrl;
        }
        String hostPort = m.group(1);         // host:port or host
        String params = m.group(3) != null ? m.group(3) : "";

        String targetJdbcUrl = "jdbc:postgresql://" + hostPort + "/" + targetDb + params;

        // Try to connect to the target DB directly
        try (Connection c = DriverManager.getConnection(targetJdbcUrl, username, password)) {
            logger.info("Connected to target database '{}'", targetDb);
            return targetJdbcUrl;
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            if (!"3D000".equals(sqlState) && !e.getMessage().toLowerCase().contains("does not exist") && !e.getMessage().toLowerCase().contains("n'existe pas")) {
                // different error (auth/host) -> rethrow
                throw e;
            }
            logger.info("Target database '{}' not available yet, attempting to create it (reason: {})", targetDb, e.getMessage());
        }

        // Connect to administrative DB 'postgres' to create the target DB
        String adminJdbcUrl = "jdbc:postgresql://" + hostPort + "/postgres" + params;
        try (Connection adminConn = DriverManager.getConnection(adminJdbcUrl, username, password);
             Statement st = adminConn.createStatement()) {

            // Check if DB exists
            try (ResultSet rs = st.executeQuery("SELECT 1 FROM pg_database WHERE datname = '" + targetDb.replace("'", "''") + "'")) {
                if (rs.next()) {
                    logger.info("Database '{}' already exists", targetDb);
                } else {
                    // create database
                    st.executeUpdate("CREATE DATABASE \"" + targetDb.replace("\"", "\"\"") + "\"");
                    logger.info("Database '{}' created", targetDb);
                }
            }
        } catch (SQLException ex) {
            logger.error("Failed to create or verify database '{}': {}", targetDb, ex.getMessage());
            throw ex;
        }

        return targetJdbcUrl;
    }
}
