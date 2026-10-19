package com.transport.services;

import com.transport.dao.OffreSpecialDAO;
import com.transport.models.OffreSpecial;
import java.util.List;

public class OffreService {
    private final OffreSpecialDAO offreDAO;
    
    public OffreService() {
        this.offreDAO = new OffreSpecialDAO();
    }
    
    public List<OffreSpecial> getActiveOffres() {
        return offreDAO.getOffresActives();
    }
    
    public List<OffreSpecial> getAllOffres() {
        return offreDAO.getAllOffres();
    }
    
    public boolean createOffre(OffreSpecial offre) {
        return offreDAO.createOffre(offre);
    }
    
    public boolean updateOffre(OffreSpecial offre) {
        return offreDAO.updateOffre(offre);
    }
    
    public boolean deleteOffre(int id) {
        return offreDAO.deleteOffre(id);
    }
}