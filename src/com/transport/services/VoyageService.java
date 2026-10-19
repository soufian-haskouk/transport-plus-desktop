package com.transport.services;

import com.transport.dao.VoyageDAO;
import com.transport.models.Voyage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VoyageService {
    private final VoyageDAO voyageDAO;
    
    public VoyageService() {
        this.voyageDAO = new VoyageDAO();
    }
    
    public List<Voyage> getAllVoyages() {
        return voyageDAO.getAllVoyages();
    }
    
    public List<Voyage> searchVoyages(String depart, String arrivee, Date date) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return voyageDAO.searchVoyages(depart, arrivee, sqlDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public boolean createVoyage(Voyage voyage) {
        return voyageDAO.createVoyage(voyage);
    }
    
    public boolean updateVoyage(Voyage voyage) {
        return voyageDAO.updateVoyage(voyage);
    }
    
    public boolean deleteVoyage(int id) {
        return voyageDAO.deleteVoyage(id);
    }
    
    public List<String> getVehiculesDisponibles() {
        return voyageDAO.getVehiculesDisponibles();
    }
    
    public List<String> getVillesArriveeByDepart(String villeDepart) {
        return voyageDAO.getVillesArriveeByDepart(villeDepart);
    }

    public List<String> getAllVillesDepart() {
        return voyageDAO.getAllVillesDepart();
    }
    
    public List<Voyage> rechercherVoyagesDisponibles(String depart, String arrivee, Date date, int nombrePassagers) {
        try {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            
            // Récupérer les voyages correspondant aux villes et date
            List<Voyage> voyages = voyageDAO.searchVoyages(depart, arrivee, sqlDate);
            
            // Filtrer uniquement les voyages avec suffisamment de places
            List<Voyage> voyagesDisponibles = new ArrayList<>();
            for (Voyage voyage : voyages) {
                if (voyage.getPlacesDisponibles() >= nombrePassagers) {
                    voyagesDisponibles.add(voyage);
                }
            }
            
            System.out.println("Service: " + voyagesDisponibles.size() + "/" + voyages.size() + 
                              " voyages avec assez de places pour " + nombrePassagers + " passager(s)");
            
            return voyagesDisponibles;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}