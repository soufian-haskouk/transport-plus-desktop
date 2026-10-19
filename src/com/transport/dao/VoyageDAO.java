package com.transport.dao;

import com.transport.models.Voyage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoyageDAO {
    
    public List<Voyage> getAllVoyages() {
        List<Voyage> voyages = new ArrayList<>();
        String sql = "SELECT v.*, vh.type_vehicule, vh.capacite " +
                     "FROM voyages v " +
                     "JOIN vehicules vh ON v.vehicule_id = vh.id " +
                     "WHERE v.statut = 'ACTIF' " +
                     "ORDER BY v.date_depart";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Voyage voyage = extractVoyageFromResultSet(rs);
                voyages.add(voyage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return voyages;
    }
    
    public List<Voyage> searchVoyages(String depart, String arrivee, java.sql.Date date) {
        List<Voyage> voyages = new ArrayList<>();
        
        String sql = "SELECT v.*, vh.type_vehicule, vh.capacite " +
                     "FROM voyages v " +
                     "JOIN vehicules vh ON v.vehicule_id = vh.id " +
                     "WHERE UPPER(v.ville_depart) = UPPER(?) " +
                     "AND UPPER(v.ville_arrivee) = UPPER(?) " +
                     "AND DATE(v.date_depart) = DATE(?) " +
                     "AND v.places_disponibles > 0 " +
                     "AND v.statut = 'ACTIF' " +
                     "ORDER BY v.date_depart";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, depart);
            pstmt.setString(2, arrivee);
            pstmt.setDate(3, date);
            
            System.out.println("\n🔍 REQUÊTE SQL searchVoyages");
            System.out.println("SQL: " + sql);
            System.out.println("Paramètres:");
            System.out.println("  Départ: '" + depart + "'");
            System.out.println("  Arrivée: '" + arrivee + "'");
            System.out.println("  Date: " + date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    voyages.add(extractVoyageFromResultSet(rs));
                }
                System.out.println("✅ " + count + " voyage(s) trouvé(s)");
                
                if (count == 0) {
                    System.out.println("\n🔎 DEBUG - Vérification des données:");
                    System.out.println("Villes dans la base de données:");
                    
                    String checkSql = "SELECT DISTINCT ville_depart, ville_arrivee " +
                                     "FROM voyages WHERE statut = 'ACTIF'";
                    
                    try (Statement stmt = conn.createStatement();
                         ResultSet checkRs = stmt.executeQuery(checkSql)) {
                        
                        while (checkRs.next()) {
                            String dbDepart = checkRs.getString("ville_depart");
                            String dbArrivee = checkRs.getString("ville_arrivee");
                            System.out.println("  • " + dbDepart + " -> " + dbArrivee);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL dans searchVoyages:");
            e.printStackTrace();
        }
        
        return voyages;
    }
    
    public Optional<Voyage> getVoyageById(int id) {
        String sql = "SELECT v.*, vh.type_vehicule, vh.capacite " +
                     "FROM voyages v " +
                     "JOIN vehicules vh ON v.vehicule_id = vh.id " +
                     "WHERE v.id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractVoyageFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public boolean createVoyage(Voyage voyage) {
        if (!vehiculeExists(voyage.getVehiculeId())) {
            System.err.println("Erreur: Véhicule ID " + voyage.getVehiculeId() + " n'existe pas");
            return false;
        }
        
        String sql = "INSERT INTO voyages (ville_depart, ville_arrivee, date_depart, " +
                     "vehicule_id, prix, places_totales, places_disponibles) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, voyage.getVilleDepart());
            pstmt.setString(2, voyage.getVilleArrivee());
            pstmt.setTimestamp(3, new Timestamp(voyage.getDateDepart().getTime()));
            pstmt.setInt(4, voyage.getVehiculeId());
            pstmt.setDouble(5, voyage.getPrix());
            pstmt.setInt(6, voyage.getPlacesTotales());
            pstmt.setInt(7, voyage.getPlacesDisponibles());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean vehiculeExists(int vehiculeId) {
        String sql = "SELECT COUNT(*) as count FROM vehicules WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, vehiculeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getVehiculesDisponibles() {
        List<String> vehicules = new ArrayList<>();
        String sql = "SELECT id, type_vehicule, marque, modele, capacite FROM vehicules WHERE statut = 'ACTIF'";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String vehicule = String.format("ID: %d - %s %s %s (%d places)",
                    rs.getInt("id"),
                    rs.getString("type_vehicule"),
                    rs.getString("marque"),
                    rs.getString("modele"),
                    rs.getInt("capacite"));
                vehicules.add(vehicule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return vehicules;
    }
    
    public boolean updateVoyage(Voyage voyage) {
        String sql = "UPDATE voyages SET ville_depart = ?, ville_arrivee = ?, " +
                     "date_depart = ?, vehicule_id = ?, prix = ?, " +
                     "places_totales = ?, places_disponibles = ?, statut = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, voyage.getVilleDepart());
            pstmt.setString(2, voyage.getVilleArrivee());
            pstmt.setTimestamp(3, new Timestamp(voyage.getDateDepart().getTime()));
            pstmt.setInt(4, voyage.getVehiculeId());
            pstmt.setDouble(5, voyage.getPrix());
            pstmt.setInt(6, voyage.getPlacesTotales());
            pstmt.setInt(7, voyage.getPlacesDisponibles());
            pstmt.setString(8, voyage.getStatut());
            pstmt.setInt(9, voyage.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteVoyage(int id) {
        String sql = "DELETE FROM voyages WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePlacesDisponibles(int voyageId, int placesReservees) {
        String sql = "UPDATE voyages SET places_disponibles = places_disponibles - ? " +
                     "WHERE id = ? AND places_disponibles >= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, placesReservees);
            pstmt.setInt(2, voyageId);
            pstmt.setInt(3, placesReservees);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Voyage extractVoyageFromResultSet(ResultSet rs) throws SQLException {
        Voyage voyage = new Voyage();
        voyage.setId(rs.getInt("id"));
        voyage.setVilleDepart(rs.getString("ville_depart"));
        voyage.setVilleArrivee(rs.getString("ville_arrivee"));
        voyage.setDateDepart(rs.getTimestamp("date_depart"));
        voyage.setVehiculeId(rs.getInt("vehicule_id"));
        voyage.setTypeVehicule(rs.getString("type_vehicule"));
        voyage.setPrix(rs.getDouble("prix"));
        voyage.setPlacesTotales(rs.getInt("places_totales"));
        voyage.setPlacesDisponibles(rs.getInt("places_disponibles"));
        voyage.setStatut(rs.getString("statut"));
        voyage.setDateCreation(rs.getTimestamp("date_creation"));
        
        int occupation = (int) ((1 - (double) voyage.getPlacesDisponibles() / voyage.getPlacesTotales()) * 100);
        voyage.setTauxOccupation(occupation);
        
        return voyage;
    }
    
    public List<String> getVillesArriveeByDepart(String villeDepart) {
        List<String> villes = new ArrayList<>();
        String sql = "SELECT DISTINCT ville_arrivee FROM voyages " +
                     "WHERE UPPER(ville_depart) = UPPER(?) AND statut = 'ACTIF' " +
                     "AND date_depart > NOW() " +
                     "ORDER BY ville_arrivee";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, villeDepart);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    villes.add(rs.getString("ville_arrivee"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return villes;
    }

    public List<String> getAllVillesDepart() {
        List<String> villes = new ArrayList<>();
        String sql = "SELECT DISTINCT ville_depart FROM voyages " +
                     "WHERE statut = 'ACTIF' AND date_depart > NOW() " +
                     "ORDER BY ville_depart";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                villes.add(rs.getString("ville_depart"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return villes;
    }
}