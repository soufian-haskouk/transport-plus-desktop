package com.transport.dao;

import com.transport.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {
    
    public Optional<User> authenticate(String email, String password, User.UserRole role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, role.toString());
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setTelephone(rs.getString("telephone"));
                user.setCin(rs.getString("cin"));
                user.setRole(User.UserRole.valueOf(rs.getString("role")));
                
                // Gérer le timestamp
                Timestamp timestamp = rs.getTimestamp("date_inscription");
                if (timestamp != null) {
                    user.setDateInscription(new Date(timestamp.getTime()));
                }
                
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeResultSet(rs);
            DBConnection.closeStatement(stmt);
            DBConnection.closeConnection(conn);
        }
        
        return Optional.empty();
    }
    
    public List<User> getAllClients() {
        List<User> clients = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CLIENT' ORDER BY date_inscription DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setTelephone(rs.getString("telephone"));
                user.setCin(rs.getString("cin"));
                user.setRole(User.UserRole.valueOf(rs.getString("role")));
                
                // Gérer le timestamp
                Timestamp timestamp = rs.getTimestamp("date_inscription");
                if (timestamp != null) {
                    user.setDateInscription(new Date(timestamp.getTime()));
                }
                
                clients.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return clients;
    }
    
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, " +
                     "telephone = ?, cin = ? WHERE id = ?";
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql = "UPDATE users SET nom = ?, prenom = ?, email = ?, " +
                  "telephone = ?, cin = ?, password = ? WHERE id = ?";
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getTelephone());
            pstmt.setString(5, user.getCin());
            
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                pstmt.setString(6, user.getPassword());
                pstmt.setInt(7, user.getId());
            } else {
                pstmt.setInt(6, user.getId());
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (nom, prenom, email, password, telephone, cin, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getPrenom());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getTelephone());
            pstmt.setString(6, user.getCin());
            pstmt.setString(7, user.getRole().toString());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setTelephone(rs.getString("telephone"));
                    user.setCin(rs.getString("cin"));
                    user.setRole(User.UserRole.valueOf(rs.getString("role")));
                    
                    // Gérer le timestamp
                    Timestamp timestamp = rs.getTimestamp("date_inscription");
                    if (timestamp != null) {
                        user.setDateInscription(new Date(timestamp.getTime()));
                    }
                    
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean userExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
}