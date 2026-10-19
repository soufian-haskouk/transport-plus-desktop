package com.transport.services;

import com.transport.dao.UserDAO;
import com.transport.models.User;
import java.util.List;

public class ClientService {
    private final UserDAO userDAO;
    
    public ClientService() {
        this.userDAO = new UserDAO();
    }
    
    public List<User> getAllClients() {
        return userDAO.getAllClients();
    }
    
    public boolean updateClient(User user) {
        return userDAO.updateUser(user);
    }
}