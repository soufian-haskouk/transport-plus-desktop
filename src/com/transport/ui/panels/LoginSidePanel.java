package com.transport.ui.panels;

import com.transport.ui.components.GradientPanel;
import javax.swing.*;
import java.awt.*;

public class LoginSidePanel extends GradientPanel {
    // Sizing
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 500;
    
    // Branding elements
    private static final String TAGLINE = "Voyagez en toute sécurité";
    private static final String[] FEATURES = {
        " Réservation en ligne rapide",
        " Paiement sécurisé", 
        " Support 24/7"
    };
    
    // Font sizes
    private static final int TAGLINE_FONT_SIZE = 18;
    private static final int FEATURE_FONT_SIZE = 14;
    
    // Colors
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SEPARATOR_COLOR = new Color(255, 255, 255, 180);
    private static final Color FEATURE_ICON_COLOR = new Color(76, 175, 80); // Vert
    
    // Layout
    private static final int CONTENT_PADDING = 20;
    private static final int ELEMENT_SPACING = 15;
    
    public LoginSidePanel() {
        super();
        configurePanel();
        displayBrandContent();
    }
    
    // ===== PANEL CONFIGURATION =====
    private void configurePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(new BorderLayout());
    }
    
    // ===== CONTENT DISPLAY =====
    private void displayBrandContent() {
        add(buildContentArea(), BorderLayout.CENTER);
    }
    
    private JPanel buildContentArea() {
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setOpaque(false);
        
        // Titre en haut
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(Box.createVerticalGlue());
        topPanel.add(createTaglinePanel());
        topPanel.add(Box.createRigidArea(new Dimension(0, ELEMENT_SPACING * 2)));
        topPanel.add(createSeparator());
        topPanel.add(Box.createVerticalGlue());
        
        
        
        return contentArea;
    }
    
    // ===== BRANDING ELEMENTS =====
    private JPanel createTaglinePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(createTaglineLabel());
        return panel;
    }
    
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(SEPARATOR_COLOR);
        separator.setMaximumSize(new Dimension(250, 2));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        return separator;
    }
    
 
    
    
    
    // ===== COMPONENT CREATION =====
    private JLabel createTaglineLabel() {
        JLabel label = new JLabel(TAGLINE);
        label.setFont(new Font("Arial", Font.PLAIN, TAGLINE_FONT_SIZE));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    // ===== PUBLIC INTERFACE =====
    public void updateBrandName(String newName) {
        removeAll();
        displayBrandContent();
        revalidate();
        repaint();
    }
}