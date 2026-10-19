package com.transport.models;

import java.util.Date;

public class OffreSpecial {
    private int id;
    private String titre;
    private String description;
    private String codePromo;
    private double reductionPourcentage;
    private double reductionFixe;
    private Date dateDebut;
    private Date dateFin;
    private String statut;
    private String conditions;
    private Date dateCreation;
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCodePromo() { return codePromo; }
    public void setCodePromo(String codePromo) { this.codePromo = codePromo; }
    
    public double getReductionPourcentage() { return reductionPourcentage; }
    public void setReductionPourcentage(double reductionPourcentage) { this.reductionPourcentage = reductionPourcentage; }
    
    public double getReductionFixe() { return reductionFixe; }
    public void setReductionFixe(double reductionFixe) { this.reductionFixe = reductionFixe; }
    
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    
    public String getConditions() { return conditions; }
    public void setConditions(String conditions) { this.conditions = conditions; }
    
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
    
    public boolean isActive() {
        Date now = new Date();
        return "ACTIVE".equals(statut) && 
               dateDebut.before(now) && 
               dateFin.after(now);
    }
    
    public String getReductionText() {
        if (reductionPourcentage > 0) {
            return reductionPourcentage + "% de réduction";
        } else if (reductionFixe > 0) {
            return reductionFixe + " DH de réduction";
        }
        return "Offre spéciale";
    }
}