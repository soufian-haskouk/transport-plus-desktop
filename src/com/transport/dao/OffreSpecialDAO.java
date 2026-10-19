package com.transport.dao;

import com.transport.models.OffreSpecial;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreSpecialDAO {
    
    public List<OffreSpecial> getOffresActives() {
        List<OffreSpecial> offres = new ArrayList<>();
        String sql = "SELECT * FROM offres_speciales " +
                     "WHERE statut = 'ACTIVE' " +
                     "AND date_debut <= CURDATE() " +
                     "AND date_fin >= CURDATE() " +
                     "ORDER BY date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                offres.add(extractOffreFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return offres;
    }
    
    public List<OffreSpecial> getAllOffres() {
        List<OffreSpecial> offres = new ArrayList<>();
        String sql = "SELECT * FROM offres_speciales ORDER BY date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                offres.add(extractOffreFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return offres;
    }
    
    public boolean createOffre(OffreSpecial offre) {
        String sql = "INSERT INTO offres_speciales (titre, description, code_promo, " +
                     "reduction_pourcentage, reduction_fixe, date_debut, date_fin, conditions) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, offre.getTitre());
            pstmt.setString(2, offre.getDescription());
            pstmt.setString(3, offre.getCodePromo());
            pstmt.setDouble(4, offre.getReductionPourcentage());
            pstmt.setDouble(5, offre.getReductionFixe());
            pstmt.setDate(6, new java.sql.Date(offre.getDateDebut().getTime()));
            pstmt.setDate(7, new java.sql.Date(offre.getDateFin().getTime()));
            pstmt.setString(8, offre.getConditions());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateOffre(OffreSpecial offre) {
        String sql = "UPDATE offres_speciales SET titre = ?, description = ?, " +
                     "code_promo = ?, reduction_pourcentage = ?, reduction_fixe = ?, " +
                     "date_debut = ?, date_fin = ?, conditions = ?, statut = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, offre.getTitre());
            pstmt.setString(2, offre.getDescription());
            pstmt.setString(3, offre.getCodePromo());
            pstmt.setDouble(4, offre.getReductionPourcentage());
            pstmt.setDouble(5, offre.getReductionFixe());
            pstmt.setDate(6, new java.sql.Date(offre.getDateDebut().getTime()));
            pstmt.setDate(7, new java.sql.Date(offre.getDateFin().getTime()));
            pstmt.setString(8, offre.getConditions());
            pstmt.setString(9, offre.getStatut());
            pstmt.setInt(10, offre.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteOffre(int id) {
        String sql = "DELETE FROM offres_speciales WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private OffreSpecial extractOffreFromResultSet(ResultSet rs) throws SQLException {
        OffreSpecial offre = new OffreSpecial();
        offre.setId(rs.getInt("id"));
        offre.setTitre(rs.getString("titre"));
        offre.setDescription(rs.getString("description"));
        offre.setCodePromo(rs.getString("code_promo"));
        offre.setReductionPourcentage(rs.getDouble("reduction_pourcentage"));
        offre.setReductionFixe(rs.getDouble("reduction_fixe"));
        offre.setDateDebut(rs.getDate("date_debut"));
        offre.setDateFin(rs.getDate("date_fin"));
        offre.setStatut(rs.getString("statut"));
        offre.setConditions(rs.getString("conditions"));
        offre.setDateCreation(rs.getTimestamp("date_creation"));
        return offre;
    }
}