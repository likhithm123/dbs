package edu.campus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}
    public static Connection get() throws SQLException {
        String type = System.getenv().getOrDefault("DB_TYPE", "mysql").toLowerCase();
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (url == null || user == null || password == null) {
            throw new SQLException("Set DB_URL, DB_USER and DB_PASSWORD environment variables.");
        }
        if (type.equals("oracle")) DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        else DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        return DriverManager.getConnection(url, user, password);
    }
}