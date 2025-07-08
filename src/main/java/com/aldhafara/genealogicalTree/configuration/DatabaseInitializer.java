package com.aldhafara.genealogicalTree.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@Profile("!test")
@Component
public class DatabaseInitializer implements ApplicationRunner {

    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USERNAME}")
    private String dbUsername;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    @Value("${DB_NAME}")
    private String dbName;

    @Override
    public void run(ApplicationArguments args) {
        String adminUrl = dbUrl.endsWith("/") ? dbUrl + "postgres" : dbUrl + "/postgres";
        try (Connection conn = DriverManager.getConnection(adminUrl, dbUsername, dbPassword)) {
            ResultSet resultSet = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            while (resultSet.next()) {
                if (dbName.equals(resultSet.getString(1))) {
                    dbExists = true;
                    break;
                }
            }
            if (!dbExists) {
                String sql = "CREATE DATABASE \"" + dbName + "\"";
                conn.createStatement().executeUpdate(sql);
                System.out.format("Database '%s' created successfully.%n", dbName);
            } else {
                System.out.format("Database '%s' already exists.%n", dbName);
            }
        } catch (SQLException e) {
            System.err.println("Database creation failed: " + e.getMessage());
        }
    }
}
