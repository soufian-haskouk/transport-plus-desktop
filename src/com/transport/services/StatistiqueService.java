package com.transport.services;

import com.transport.models.Statistiques;
import com.transport.dao.DBConnection;
import java.sql.*;
import java.util.*;

public class StatistiqueService {
    
    // Classe pour les statistiques des destinations (séparée de Statistiques)
    public static class DestinationData {
        private String destination;
        private int nombreReservations;
        private int nombreVoyages;
        private double revenus;
        
        public DestinationData() {}
        
        public DestinationData(String destination, int nombreReservations) {
            this.destination = destination;
            this.nombreReservations = nombreReservations;
        }
        
        // Getters et setters
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        
        public int getNombreReservations() { return nombreReservations; }
        public void setNombreReservations(int nombreReservations) { this.nombreReservations = nombreReservations; }
        
        public int getNombreVoyages() { return nombreVoyages; }
        public void setNombreVoyages(int nombreVoyages) { this.nombreVoyages = nombreVoyages; }
        
        public double getRevenus() { return revenus; }
        public void setRevenus(double revenus) { this.revenus = revenus; }
    }
    
    public StatistiqueService() {
        // Constructeur vide
    }
    
    public Statistiques getStatistiques() {
        Statistiques stats = new Statistiques();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Requête optimisée qui récupère toutes les statistiques en une seule fois
            String query = "SELECT " +
                         "(SELECT COALESCE(SUM(montant_total), 0) FROM reservations WHERE statut = 'CONFIRME') as total_revenue, " +
                         "(SELECT COUNT(*) FROM reservations) as total_reservations, " +
                         "(SELECT COUNT(*) FROM voyages WHERE statut = 'ACTIF') as total_voyages, " +
                         "(SELECT COUNT(*) FROM users WHERE role = 'CLIENT') as total_clients";
            
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                stats.setRevenusTotal(rs.getDouble("total_revenue"));
                stats.setTotalReservations(rs.getInt("total_reservations"));
                stats.setTotalVoyages(rs.getInt("total_voyages"));
                stats.setTotalClients(rs.getInt("total_clients"));
            } else {
                setDefaultValues(stats);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
            setDefaultValues(stats);
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return stats;
    }
    
    // Version alternative : compter tous les voyages (tous statuts)
    public Statistiques getStatistiquesCompletes() {
        Statistiques stats = new Statistiques();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Cette version compte tous les voyages, peu importe le statut
            String query = "SELECT " +
                         "(SELECT COALESCE(SUM(montant_total), 0) FROM reservations WHERE statut = 'CONFIRME') as total_revenue, " +
                         "(SELECT COUNT(*) FROM reservations) as total_reservations, " +
                         "(SELECT COUNT(*) FROM voyages) as total_voyages, " + // Tous les voyages
                         "(SELECT COUNT(*) FROM users WHERE role = 'CLIENT') as total_clients";
            
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                stats.setRevenusTotal(rs.getDouble("total_revenue"));
                stats.setTotalReservations(rs.getInt("total_reservations"));
                stats.setTotalVoyages(rs.getInt("total_voyages"));
                stats.setTotalClients(rs.getInt("total_clients"));
            } else {
                setDefaultValues(stats);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultValues(stats);
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return stats;
    }
    
    // Méthode pour obtenir les statistiques avec différentes options
    public Statistiques getStatistiquesAvecOptions(boolean inclureVoyagesPasses, boolean inclureVoyagesInactifs) {
        Statistiques stats = new Statistiques();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Construire la condition WHERE pour les voyages dynamiquement
            StringBuilder voyageCondition = new StringBuilder();
            voyageCondition.append("SELECT COUNT(*) FROM voyages WHERE 1=1");
            
            if (!inclureVoyagesInactifs) {
                voyageCondition.append(" AND statut = 'ACTIF'");
            }
            
            if (!inclureVoyagesPasses) {
                voyageCondition.append(" AND date_depart >= CURDATE()");
            }
            
            String query = "SELECT " +
                         "(SELECT COALESCE(SUM(montant_total), 0) FROM reservations WHERE statut = 'CONFIRME') as total_revenue, " +
                         "(SELECT COUNT(*) FROM reservations) as total_reservations, " +
                         "(" + voyageCondition.toString() + ") as total_voyages, " +
                         "(SELECT COUNT(*) FROM users WHERE role = 'CLIENT') as total_clients";
            
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                stats.setRevenusTotal(rs.getDouble("total_revenue"));
                stats.setTotalReservations(rs.getInt("total_reservations"));
                stats.setTotalVoyages(rs.getInt("total_voyages"));
                stats.setTotalClients(rs.getInt("total_clients"));
            } else {
                setDefaultValues(stats);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultValues(stats);
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return stats;
    }
    
    // Méthode pour obtenir les statistiques de la semaine
    public Statistiques getStatistiquesSemaine() {
        Statistiques stats = new Statistiques();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            String query = "SELECT " +
                         "(SELECT COALESCE(SUM(montant_total), 0) FROM reservations " +
                         " WHERE statut = 'CONFIRME' AND date_reservation >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)) as total_revenue, " +
                         "(SELECT COUNT(*) FROM reservations " +
                         " WHERE date_reservation >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)) as total_reservations, " +
                         "(SELECT COUNT(*) FROM voyages " +
                         " WHERE date_depart BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) AND statut = 'ACTIF') as total_voyages, " +
                         "(SELECT COUNT(*) FROM users " +
                         " WHERE role = 'CLIENT' AND date_inscription >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)) as total_clients";
            
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                stats.setRevenusTotal(rs.getDouble("total_revenue"));
                stats.setTotalReservations(rs.getInt("total_reservations"));
                stats.setTotalVoyages(rs.getInt("total_voyages"));
                stats.setTotalClients(rs.getInt("total_clients"));
            } else {
                setDefaultValues(stats);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            setDefaultValues(stats);
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return stats;
    }
    
    
 // Méthode pour obtenir les destinations les plus populaires
    public Map<String, Integer> getPopularDestinations(int limit) {
        Map<String, Integer> destinations = new LinkedHashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            
            // MODIFICATION ICI : Supprimer la condition sur le statut pour compter TOUTES les réservations
            // OU garder seulement les confirmées si c'est ce que vous voulez
            String query = "SELECT v.ville_arrivee, COUNT(r.id) as nombre_reservations " +
                         "FROM voyages v " +
                         "LEFT JOIN reservations r ON v.id = r.voyage_id " +
                         // "WHERE r.statut = 'CONFIRME' " + // LIGNE COMMENTÉE pour compter toutes les réservations
                         "GROUP BY v.ville_arrivee " +
                         "ORDER BY nombre_reservations DESC " +
                         "LIMIT ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, limit);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String destination = rs.getString("ville_arrivee");
                int count = rs.getInt("nombre_reservations");
                destinations.put(destination, count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des destinations populaires: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return destinations;
    }
    
    // Version alternative qui inclut aussi les voyages sans réservations
    public Map<String, Integer> getAllDestinationsWithStats() {
        Map<String, Integer> destinations = new LinkedHashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Requête pour toutes les destinations avec leurs statistiques
            String query = "SELECT " +
                         "v.ville_arrivee, " +
                         "COUNT(DISTINCT v.id) as nombre_voyages, " +
                         "COUNT(r.id) as nombre_reservations, " +
                         "SUM(CASE WHEN r.statut = 'CONFIRME' THEN r.nombre_places ELSE 0 END) as places_vendues " +
                         "FROM voyages v " +
                         "LEFT JOIN reservations r ON v.id = r.voyage_id " +
                         "GROUP BY v.ville_arrivee " +
                         "ORDER BY nombre_reservations DESC, nombre_voyages DESC";
            
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String destination = rs.getString("ville_arrivee");
                int reservations = rs.getInt("nombre_reservations");
                destinations.put(destination, reservations);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return destinations;
    }
    
    // Méthode pour obtenir les destinations avec plus de détails (utilise DestinationData)
    public List<DestinationData> getDestinationStatistics() {
        List<DestinationData> destinations = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            String query = "SELECT " +
                         "v.ville_arrivee, " +
                         "COUNT(DISTINCT v.id) as total_voyages, " +
                         "COUNT(r.id) as total_reservations, " +
                         "SUM(CASE WHEN r.statut = 'CONFIRME' THEN r.montant_total ELSE 0 END) as revenus, " +
                         "AVG(v.prix) as prix_moyen " +
                         "FROM voyages v " +
                         "LEFT JOIN reservations r ON v.id = r.voyage_id " +
                         "WHERE v.statut = 'ACTIF' " +
                         "GROUP BY v.ville_arrivee " +
                         "ORDER BY total_reservations DESC, revenus DESC";
            
            rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                DestinationData data = new DestinationData();
                data.setDestination(rs.getString("ville_arrivee"));
                data.setNombreVoyages(rs.getInt("total_voyages"));
                data.setNombreReservations(rs.getInt("total_reservations"));
                data.setRevenus(rs.getDouble("revenus"));
                destinations.add(data);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return destinations;
    }
    
 // Ajoutez cette méthode à StatistiqueService pour debugger
    public int getTotalReservationsToutes() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT COUNT(*) as total FROM reservations";
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return 0;
    }

    // Et aussi pour les réservations confirmées seulement
    public int getTotalReservationsConfirmees() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT COUNT(*) as total FROM reservations WHERE statut = 'CONFIRME'";
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return 0;
    }
    
    // Méthode pour debugger - affiche les valeurs récupérées
    public void debugStatistiques() {
        Statistiques stats = getStatistiques();
        System.out.println("=== DEBUG STATISTIQUES ===");
        System.out.println("Revenus totaux: " + stats.getRevenusTotal());
        System.out.println("Total réservations: " + stats.getTotalReservations());
        System.out.println("Total voyages: " + stats.getTotalVoyages());
        System.out.println("Total clients: " + stats.getTotalClients());
        System.out.println("==========================");
        
        // Vérifier directement les nombres dans la base
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Compter les voyages
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM voyages WHERE statut = 'ACTIF'");
            if (rs.next()) {
                System.out.println("Voyages ACTIF dans la base: " + rs.getInt("count"));
            }
            
            // Voir les dates des voyages
            rs = stmt.executeQuery("SELECT id, ville_depart, ville_arrivee, date_depart, statut FROM voyages");
            System.out.println("\nListe des voyages:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 ", De " + rs.getString("ville_depart") + 
                                 " à " + rs.getString("ville_arrivee") + 
                                 ", Date: " + rs.getDate("date_depart") + 
                                 ", Statut: " + rs.getString("statut"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Méthode pour obtenir le revenu mensuel
    public double getRevenuMensuel(int annee, int mois) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            String query = "SELECT COALESCE(SUM(montant_total), 0) as revenu_mensuel " +
                         "FROM reservations " +
                         "WHERE statut = 'CONFIRME' " +
                         "AND YEAR(date_reservation) = ? " +
                         "AND MONTH(date_reservation) = ?";
            
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, annee);
            pstmt.setInt(2, mois);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("revenu_mensuel");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, pstmt, conn);
        }
        
        return 0.0;
    }
    
    // Méthode pour obtenir les statistiques des véhicules
    public int getNombreVehiculesActifs() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT COUNT(*) as vehicules_actifs FROM vehicules WHERE statut = 'ACTIF'";
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("vehicules_actifs");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return 0;
    }
    
    // Méthode pour obtenir les offres spéciales actives
    public int getNombreOffresActives() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String query = "SELECT COUNT(*) as offres_actives FROM offres_speciales " +
                         "WHERE statut = 'ACTIVE' " +
                         "AND CURDATE() BETWEEN date_debut AND date_fin";
            rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("offres_actives");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, stmt, conn);
        }
        
        return 0;
    }
    
    private void setDefaultValues(Statistiques stats) {
        stats.setRevenusTotal(0.0);
        stats.setTotalReservations(0);
        stats.setTotalVoyages(0);
        stats.setTotalClients(0);
    }
    
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}