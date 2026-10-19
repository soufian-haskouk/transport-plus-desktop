package com.transport.services;

import com.transport.dao.UserDAO;
import com.transport.models.User;
import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public Optional<User> login(String email, String password, User.UserRole role) {
        // Validation
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return Optional.empty();
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            return Optional.empty();
        }
        
        return userDAO.authenticate(email.trim(), password, role);
    }
    
    public boolean register(User user) {
        // Implémenter l'inscription
        return userDAO.createUser(user);
    }
    
    // Nouvelle méthode pour vérifier si un utilisateur existe
    public boolean userExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userDAO.userExists(email.trim());
    }
    
    // Nouvelle méthode pour faciliter l'inscription
    public boolean registerUser(String firstName, String lastName, String email, 
                                String phone, String password, User.UserRole role) {
        // Créer l'objet User
        User user = new User();
        user.setPrenom(firstName);
        user.setNom(lastName);
        user.setEmail(email);
        user.setTelephone(phone);
        user.setPassword(password);
        user.setRole(role);
        
        // Générer un CIN temporaire (vous pourriez modifier cela selon vos besoins)
        String cin = "CIN_" + System.currentTimeMillis() % 100000;
        user.setCin(cin);
        
        // Enregistrer l'utilisateur
        return register(user);
    }
}