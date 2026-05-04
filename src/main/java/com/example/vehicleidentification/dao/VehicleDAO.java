package com.example.vehicleidentification.dao;

import com.example.vehicleidentification.model.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO extends BaseDAO<Vehicle> {

    @Override
    public Vehicle getById(int id) throws SQLException {
        String sql = "SELECT * FROM Vehicle WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Vehicle> getAll() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle ORDER BY vehicle_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByOwner(int ownerId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE owner_id = ? ORDER BY year DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByMake(String make) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE make ILIKE ? ORDER BY model";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + make + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByYearRange(int startYear, int endYear) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE year BETWEEN ? AND ? ORDER BY year";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, startYear);
            pstmt.setInt(2, endYear);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }

    @Override
    public boolean insert(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO Vehicle (registration_number, make, model, year, owner_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, vehicle.getRegistrationNumber());
            pstmt.setString(2, vehicle.getMake());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setInt(5, vehicle.getOwnerId());

            boolean inserted = executeUpdate(pstmt);
            if (inserted) {
                int generatedId = getLastInsertId(pstmt);
                vehicle.setVehicleId(generatedId);
            }
            return inserted;
        }
    }

    @Override
    public boolean update(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE Vehicle SET registration_number=?, make=?, model=?, year=?, owner_id=? WHERE vehicle_id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getRegistrationNumber());
            pstmt.setString(2, vehicle.getMake());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setInt(5, vehicle.getOwnerId());
            pstmt.setInt(6, vehicle.getVehicleId());

            return executeUpdate(pstmt);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE vehicle_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return executeUpdate(pstmt);
        }
    }

    public boolean deleteByOwner(int ownerId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            return executeUpdate(pstmt);
        }
    }

    public Vehicle getByRegistrationNumber(String registrationNumber) throws SQLException {
        String sql = "SELECT * FROM Vehicle WHERE registration_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, registrationNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public int getVehicleCountByOwner(int ownerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vehicle WHERE owner_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, ownerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("vehicle_id"),
                rs.getString("registration_number"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getInt("owner_id")
        );
    }

    @Override
    protected String getTableName() {
        return "Vehicle";
    }
}