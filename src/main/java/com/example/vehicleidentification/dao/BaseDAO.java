package com.example.vehicleidentification.dao;

import java.sql.*;
import java.util.List;

// Abstract base DAO class demonstrating inheritance and generics
public abstract class BaseDAO<T> {

    protected Connection connection;

    public BaseDAO() {
        try {
            this.connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
        }
    }

    // Abstract methods to be implemented by child classes
    public abstract T getById(int id) throws SQLException;
    public abstract List<T> getAll() throws SQLException;
    public abstract boolean insert(T entity) throws SQLException;
    public abstract boolean update(T entity) throws SQLException;
    public abstract boolean delete(int id) throws SQLException;

    // Common utility methods
    protected void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null && conn != this.connection) conn.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    protected void closeResources(PreparedStatement pstmt, Connection conn) {
        closeResources(null, pstmt, conn);
    }

    protected int getLastInsertId(PreparedStatement pstmt) throws SQLException {
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Failed to get generated ID");
        }
    }

    protected boolean executeUpdate(PreparedStatement pstmt) throws SQLException {
        return pstmt.executeUpdate() > 0;
    }

    // Polymorphic method - can be overridden by child classes
    public int getCount() throws SQLException {
        String tableName = getTableName();
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Abstract method to be implemented
    protected abstract String getTableName();

    // Template method pattern
    public final boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM " + getTableName() + " WHERE " + getIdColumnName() + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    protected String getIdColumnName() {
        return getTableName().toLowerCase() + "_id";
    }
}