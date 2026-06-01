package mypharmacist.database;

import java.sql.*;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String SERVER   = "FAHRIZAL";   // Bisa juga diganti "localhost" jika terjadi error jaringan
    private static final String PORT     = "1433";
    private static final String DATABASE = "MyPharmacist"; 

    private static final String URL =
        "jdbc:sqlserver://" + SERVER + ":" + PORT +
        ";databaseName=" + DATABASE +
        ";integratedSecurity=true;encrypt=false;trustServerCertificate=true";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                
                connection = DriverManager.getConnection(URL);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver SQL Server tidak ditemukan.\nPastikan mssql-jdbc.jar sudah ditambahkan ke classpath.",
                "Error Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database:\n" + e.getMessage() +
                "\n\nPastikan:\n1. SQL Server berjalan\n2. TCP/IP sudah Enable\n3. File mssql-jdbc_auth.dll sudah diletakkan di dalam folder project",
                "Error Koneksi", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean testConnection() {
        Connection conn = getConnection();
        return conn != null;
    }
}