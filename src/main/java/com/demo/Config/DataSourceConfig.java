package com.demo.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        try {
            // test connection to target DB
            try (Connection c = DriverManager.getConnection(jdbcUrl, username, password)) {
                return buildHikari(jdbcUrl, username, password);
            }
        } catch (SQLException e) {
            // Postgres SQLState for "database does not exist" = 3D000
            if ("3D000".equals(e.getSQLState()) || e.getMessage().toLowerCase().contains("does not exist")) {
                String dbName = extractDbName(jdbcUrl);
                String adminUrl = replaceDbName(jdbcUrl, "postgres");
                try (Connection admin = DriverManager.getConnection(adminUrl, username, password);
                     Statement st = admin.createStatement()) {
                    st.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                }
                return buildHikari(jdbcUrl, username, password);
            }
            throw e;
        }
    }

    private HikariDataSource buildHikari(String url, String user, String pass) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }

    private String extractDbName(String url) {
        int q = url.indexOf('?');
        int end = q >= 0 ? q : url.length();
        int slash = url.lastIndexOf('/', end - 1);
        return url.substring(slash + 1, end);
    }

    private String replaceDbName(String url, String newDb) {
        int q = url.indexOf('?');
        String params = q >= 0 ? url.substring(q) : "";
        int slash = url.lastIndexOf('/', q >= 0 ? q - 1 : url.length() - 1);
        String prefix = url.substring(0, slash + 1);
        return prefix + newDb + params;
    }
}