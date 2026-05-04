package com.example.vehicleidentification.dao;

import com.example.vehicleidentification.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends BaseDAO<Customer> {

    @Override
    public Customer getById(int id) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Customer> getAll() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY customer_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        }
        return customers;
    }

    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE name ILIKE ? OR email ILIKE ? OR phone ILIKE ? ORDER BY name";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(extractCustomerFromResultSet(rs));
                }
            }
        }
        return customers;
    }

    public Customer getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCustomerFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Customer> getCustomersWithVehicles() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM Customer c " +
                "INNER JOIN Vehicle v ON c.customer_id = v.owner_id " +
                "ORDER BY c.name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        }
        return customers;
    }

    @Override
    public boolean insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (name, address, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());

            boolean inserted = executeUpdate(pstmt);
            if (inserted) {
                int generatedId = getLastInsertId(pstmt);
                customer.setUserId(generatedId);
            }
            return inserted;
        }
    }

    @Override
    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE Customer SET name=?, address=?, phone=?, email=? WHERE customer_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setInt(5, customer.getUserId());

            return executeUpdate(pstmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Customer WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return executeUpdate(pstmt);
        }
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        // Fixed: Customer constructor expects (customerId, name, address, phone, email)
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("email")
        );
    }

    @Override
    protected String getTableName() {
        return "Customer";
    }
}