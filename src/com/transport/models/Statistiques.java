package com.transport.models;

public class Statistiques {
    private double revenusTotal;
    private int totalReservations;
    private int totalVoyages;
    private int totalClients;
    private String destination;
    private double revenus;
    private double prixMoyen;
    
    // Getters et Setters
    public double getRevenusTotal() { return revenusTotal; }
    public void setRevenusTotal(double revenusTotal) { this.revenusTotal = revenusTotal; }
    
    public int getTotalReservations() { return totalReservations; }
    public void setTotalReservations(int totalReservations) { this.totalReservations = totalReservations; }
    
    public int getTotalVoyages() { return totalVoyages; }
    public void setTotalVoyages(int totalVoyages) { this.totalVoyages = totalVoyages; }
    
    public int getTotalClients() { return totalClients; }
    public void setTotalClients(int totalClients) { this.totalClients = totalClients; }
    
    
    
    // Getters et setters
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
      
        public double getRevenus() { return revenus; }
    public void setRevenus(double revenus) { this.revenus = revenus; }
    
    public double getPrixMoyen() { return prixMoyen; }
    public void setPrixMoyen(double prixMoyen) { this.prixMoyen = prixMoyen; }
    
    @Override
    public String toString() {
        return String.format("%s: %d voyages, %d réservations, %.2f DH", 
            destination, totalVoyages, totalReservations, revenus);
    }
}