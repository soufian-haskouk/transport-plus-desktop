package com.transport.models;

import java.util.Date;

public class Voyage {
    private int id;
    private String villeDepart;
    private String villeArrivee;
    private Date dateDepart;
    private int vehiculeId;
    private String typeVehicule;
    private double prix;
    private int placesTotales;
    private int placesDisponibles;
    private String statut;
    private Date dateCreation;
    private int tauxOccupation;
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    
    public Date getDateDepart() { return dateDepart; }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }
    
    public int getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(int vehiculeId) { this.vehiculeId = vehiculeId; }
    
    public String getTypeVehicule() { return typeVehicule; }
    public void setTypeVehicule(String typeVehicule) { this.typeVehicule = typeVehicule; }
    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    
    public int getPlacesTotales() { return placesTotales; }
    public void setPlacesTotales(int placesTotales) { this.placesTotales = placesTotales; }
    
    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    
    public int getTauxOccupation() { return tauxOccupation; }
    public void setTauxOccupation(int tauxOccupation) { this.tauxOccupation = tauxOccupation; }
    
    public String getDisponibilite() {
        if (placesDisponibles == 0) return "🔴 COMPLET";
        if (placesDisponibles <= 5) return "🟡 LIMITÉ";
        if (tauxOccupation > 80) return "🟡 BIENTÔT COMPLET";
        return "🔵 DISPONIBLE";
    }
}