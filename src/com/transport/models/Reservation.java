package com.transport.models;

import java.util.Date;

public class Reservation {
    private int id;
    private int clientId;
    private int voyageId;
    private Date dateReservation;
    private int nombrePlaces;
    private double montantTotal;
    private String statut;
    private String siegeNumbers;
    private String numeroBillet;
    
    // Pour l'affichage
    private String clientNom;
    private String trajet;
    private Date dateDepart;
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    
    public int getVoyageId() { return voyageId; }
    public void setVoyageId(int voyageId) { this.voyageId = voyageId; }
    
    public Date getDateReservation() { return dateReservation; }
    public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }
    
    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    
    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getSiegeNumbers() { return siegeNumbers; }
    public void setSiegeNumbers(String siegeNumbers) { this.siegeNumbers = siegeNumbers; }
    
    public String getNumeroBillet() { return numeroBillet; }
    public void setNumeroBillet(String numeroBillet) { this.numeroBillet = numeroBillet; }
    
    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }
    
    public String getTrajet() { return trajet; }
    public void setTrajet(String trajet) { this.trajet = trajet; }
    
    public Date getDateDepart() { return dateDepart; }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }
    
    public String getStatutIcon() {
        switch(statut) {
            case "CONFIRME": return "✅ CONFIRMÉ";
            case "EN_ATTENTE": return "⏳ EN ATTENTE";
            case "ANNULE": return "❌ ANNULÉ";
            case "REMBOURSE": return "💰 REMBOURSÉ";
            default: return statut;
        }
    }
}