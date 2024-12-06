package com.aldhafara.genealogicalTree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

@SpringBootApplication
public class GenealogicalTreeApplication {

    private static final String DB_NAME = "database.name";
    private static final String DB_USERNAME = "database.username";
    private static final String DB_PASSWORD = "database.password";
    private static final String DB_URL = "database.url";

    public static void main(String[] args) {

        try {
            setDbProperties();
            createDatabaseIfNotExists();

            SpringApplication.run(GenealogicalTreeApplication.class, args);
        } catch (IOException e) {
            System.out.println("File with DataBase credential not found. Make sure that it exist");
        }
    }

    private static void setDbProperties() throws IOException {
        String password = Files.readString(Paths.get("src/main/resources/secrets/bdPassword")).trim();
        System.setProperty(DB_PASSWORD, password);
        String username = Files.readString(Paths.get("src/main/resources/secrets/bdUsername")).trim();
        System.setProperty(DB_USERNAME, username);
        String dbName = Files.readString(Paths.get("src/main/resources/secrets/bdName")).trim();
        System.setProperty(DB_NAME, dbName);
        String dbUrl = Files.readString(Paths.get("src/main/resources/secrets/bdUrl")).trim();
        System.setProperty(DB_URL, dbUrl);
    }

    private static void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection(
                System.getProperty(DB_URL), System.getProperty(DB_USERNAME), System.getProperty(DB_PASSWORD))) {
            ResultSet resultSet = conn.getMetaData().getCatalogs();
            boolean dbExists = false;
            String dbName = System.getProperty(DB_NAME);
            while (resultSet.next()) {
                if (dbName.equals(resultSet.getString(1))) {
                    dbExists = true;
                    break;
                }
            }
            if (!dbExists) {
                conn.createStatement().executeUpdate("CREATE DATABASE " + dbName);
                System.out.format("Database '%s' created successfully.", dbName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
