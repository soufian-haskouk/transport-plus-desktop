package com.transport.dao;

import com.transport.models.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    
    public List<Reservation> getReservationsByClient(int clientId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, v.ville_depart, v.ville_arrivee, v.date_depart, " +
                     "u.nom, u.prenom " +
                     "FROM reservations r " +
                     "JOIN voyages v ON r.voyage_id = v.id " +
                     "JOIN users u ON r.client_id = u.id " +
                     "WHERE r.client_id = ? " +
                     "ORDER BY r.date_reservation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(extractReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, v.ville_depart, v.ville_arrivee, v.date_depart, " +
                     "u.nom, u.prenom " +
                     "FROM reservations r " +
                     "JOIN voyages v ON r.voyage_id = v.id " +
                     "JOIN users u ON r.client_id = u.id " +
                     "ORDER BY r.date_reservation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reservations;
    }
    
    public boolean createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (client_id, voyage_id, nombre_places, " +
                     "montant_total, statut, siege_numbers) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Vérifier d'abord la disponibilité des places (avec un lock)
            String checkSql = "SELECT places_disponibles FROM voyages WHERE id = ? FOR UPDATE";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, reservation.getVoyageId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int placesDisponibles = rs.getInt("places_disponibles");
                        if (placesDisponibles < reservation.getNombrePlaces()) {
                            conn.rollback();
                            return false; // Pas assez de places
                        }
                    } else {
                        conn.rollback();
                        return false; // Voyage non trouvé
                    }
                }
            }
            
            // 2. Créer la réservation
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, reservation.getClientId());
                pstmt.setInt(2, reservation.getVoyageId());
                pstmt.setInt(3, reservation.getNombrePlaces());
                pstmt.setDouble(4, reservation.getMontantTotal());
                pstmt.setString(5, reservation.getStatut());
                pstmt.setString(6, reservation.getSiegeNumbers());
                
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
                
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
            // 3. Mettre à jour les places disponibles
            String updateSql = "UPDATE voyages SET places_disponibles = places_disponibles - ? " +
                              "WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, reservation.getNombrePlaces());
                updateStmt.setInt(2, reservation.getVoyageId());
                
                int updatedRows = updateStmt.executeUpdate();
                if (updatedRows == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public boolean updateReservationStatus(int reservationId, String statut) {
        String sql = "UPDATE reservations SET statut = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, statut);
            pstmt.setInt(2, reservationId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean annulerReservation(int reservationId) {
        String sql = "UPDATE reservations SET statut = 'ANNULE' WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reservationId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setClientId(rs.getInt("client_id"));
        reservation.setVoyageId(rs.getInt("voyage_id"));
        reservation.setDateReservation(rs.getTimestamp("date_reservation"));
        reservation.setNombrePlaces(rs.getInt("nombre_places"));
        reservation.setMontantTotal(rs.getDouble("montant_total"));
        reservation.setStatut(rs.getString("statut"));
        reservation.setSiegeNumbers(rs.getString("siege_numbers"));
        reservation.setNumeroBillet(rs.getString("numero_billet"));
        
        // Informations supplémentaires
        reservation.setClientNom(rs.getString("nom") + " " + rs.getString("prenom"));
        reservation.setTrajet(rs.getString("ville_depart") + " → " + rs.getString("ville_arrivee"));
        reservation.setDateDepart(rs.getTimestamp("date_depart"));
        
        return reservation;
    }
}