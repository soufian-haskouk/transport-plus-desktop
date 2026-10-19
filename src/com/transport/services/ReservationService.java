package com.transport.services;

import com.transport.dao.ReservationDAO;
import com.transport.models.Reservation;
import java.util.List;
import java.util.Optional;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    
    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }
    
    public List<Reservation> getClientReservations(int clientId) {
        return reservationDAO.getReservationsByClient(clientId);
    }
    
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }
    
    public boolean createReservation(Reservation reservation) {
        return reservationDAO.createReservation(reservation);
    }
    
    public boolean confirmReservation(int reservationId) {
        return reservationDAO.updateReservationStatus(reservationId, "CONFIRME");
    }
    
    public boolean annulerReservation(int reservationId) {
        return reservationDAO.annulerReservation(reservationId);
    }
    
    public Optional<Reservation> getReservationById(int id) {
        // Récupérer toutes les réservations et filtrer par ID
        List<Reservation> reservations = reservationDAO.getAllReservations();
        for (Reservation reservation : reservations) {
            if (reservation.getId() == id) {
                return Optional.of(reservation);
            }
        }
        return Optional.empty();
    }
}