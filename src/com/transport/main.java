package com.transport;

import com.transport.ui.frames.LoginFrame;
import javax.swing.*;

class main {
    public static void main(String[] args) {
    	//exécute ce code plus tard, sur le fil spécial de Swing"
        SwingUtilities.invokeLater(() -> {
            try {
                // Appliquer le look and feel du système
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Démarrer l'application
            new LoginFrame();
        });
    }
}