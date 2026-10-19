package com.transport.ui.components;

import com.transport.config.ThemeConfig;
import com.transport.utils.FontAwesomeLoader;
import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color startColor;
    private Color endColor;
    private String title;
    private String subtitle;
    private java.util.List<String> features;
    private boolean showIcon;
    
    // Style constants
    private static final String DEFAULT_TITLE = "TRANSPORT+";
    private static final String DEFAULT_SUBTITLE = "Voyagez en toute sécurité";
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 16);
    private static final Font FEATURE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font AWESOME_FONT = FontAwesomeLoader.getSolidFont(16);
    
    public GradientPanel() {
        this(ThemeConfig.PRIMARY_COLOR, ThemeConfig.SECONDARY_COLOR);
    }
    
    public GradientPanel(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.title = DEFAULT_TITLE;
        this.subtitle = DEFAULT_SUBTITLE;
        this.showIcon = true;
        
        // Default features avec Font Awesome
        // \uf017 = ⌚ (clock)
        // \uf09d = 💳 (credit card)
        // \uf0f3 = 🔔 (bell)
        this.features = java.util.Arrays.asList(
            "\uf017 Réservation en ligne rapide",
            "\uf09d Paiement sécurisé",
            "\uf0f3 Support 24/7",
            "\uf124 Plus de 100 destinations"
        );
        
        setOpaque(false);
    }
    
    
    // Builder-style setters for intuitive configuration
    public GradientPanel withTitle(String title) {
        this.title = title;
        return this;
    }
    
    public GradientPanel withSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }
    
    public GradientPanel withFeatures(java.util.List<String> features) {
        this.features = features;
        return this;
    }
    
    public GradientPanel withFeature(String feature) {
        this.features.add(feature);
        return this;
    }
    
    public GradientPanel withIcon(boolean showIcon) {
        this.showIcon = showIcon;
        return this;
    }
    
    public GradientPanel withGradient(Color start, Color end) {
        this.startColor = start;
        this.endColor = end;
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        applyRenderingHints(g2d);
        drawGradientBackground(g2d);
        drawContent(g2d);
    }
    
    private void applyRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    private void drawGradientBackground(Graphics2D g2d) {
        GradientPaint gradient = new GradientPaint(
            0, 0, startColor,
            getWidth(), 0, endColor // Horizontal gradient
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawContent(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int currentY = 50; // Start position
        
        // Draw logo/icon
        if (showIcon) {
            currentY = drawTransportLogo(g2d, centerX, currentY) + 40;
        }
        
        // Draw title (single instance)
        currentY = drawCenteredText(g2d, title, TITLE_FONT, Color.WHITE, centerX, currentY) + 10;
        
        // Draw subtitle (single instance)
        currentY = drawCenteredText(g2d, subtitle, SUBTITLE_FONT, new Color(255, 255, 255, 220), centerX, currentY) + 30;
        
        // Draw features
        drawFeatures(g2d, centerX, currentY);
    }
    
    private int drawTransportLogo(Graphics2D g2d, int centerX, int y) {
        int logoSize = 80;
        int logoY = y + logoSize / 2;
        
        // Modern transport logo
        g2d.setColor(Color.WHITE);
        
        // Main circle
        g2d.fillOval(centerX - logoSize/2, logoY - logoSize/2, logoSize, logoSize);
        
        // "T+" inside
        g2d.setColor(ThemeConfig.PRIMARY_COLOR);
        g2d.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g2d.getFontMetrics();
        String tPlus = "T+";
        int textX = centerX - fm.stringWidth(tPlus) / 2;
        int textY = logoY + fm.getHeight() / 4;
        g2d.drawString(tPlus, textX, textY);
        
        return logoY + logoSize / 2;
    }
    
    private int drawCenteredText(Graphics2D g2d, String text, Font font, Color color, int centerX, int y) {
        g2d.setFont(font);
        g2d.setColor(color);
        
        FontMetrics fm = g2d.getFontMetrics();
        int textX = centerX - fm.stringWidth(text) / 2;
        int textY = y + fm.getHeight();
        
        g2d.drawString(text, textX, textY);
        return textY;
    }
    
    private void drawFeatures(Graphics2D g2d, int centerX, int startY) {
        if (features == null || features.isEmpty()) {
            return;
        }
        
        g2d.setFont(FEATURE_FONT);
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight() + 5;
        int currentY = startY;
        
        // Feature box background
        int boxWidth = 250;
        int boxHeight = features.size() * lineHeight + 20;
        int boxX = centerX - boxWidth / 2;
        int boxY = currentY - 10;
        
        // Semi-transparent background for features
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        // Feature border
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 15, 15);
        
        // Draw each feature
        for (String feature : features) {
            // Extraire l'icône et le texte
            String icon = feature.substring(0, 1);
            String text = feature.substring(2);
            
            // Vérifier si Font Awesome est chargé
            if (FontAwesomeLoader.isFontLoaded()) {
                // Dessiner l'icône Font Awesome
                g2d.setFont(AWESOME_FONT);
                g2d.setColor(new Color(76, 175, 80)); // Green icon
                g2d.drawString(icon, boxX + 15, currentY + fm.getHeight() / 4);
                
                // Revenir à la police normale pour le texte
                g2d.setFont(FEATURE_FONT);
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, boxX + 35, currentY + fm.getHeight() / 4);
            } else {
                // Fallback: utiliser des caractères Unicode standards
                g2d.setColor(new Color(76, 175, 80)); // Green checkmark
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                
                // Utiliser ✓ comme fallback si Font Awesome n'est pas chargé
                String fallbackIcon = "\u2713"; // ✓
                g2d.drawString(fallbackIcon, boxX + 15, currentY + fm.getHeight() / 4);
                
                // Draw feature text
                g2d.setColor(Color.WHITE);
                g2d.setFont(FEATURE_FONT);
                g2d.drawString(text, boxX + 35, currentY + fm.getHeight() / 4);
            }
            
            currentY += lineHeight;
        }
    }
    
    public static GradientPanel createLoginPanel() {
        return new GradientPanel()
            .withTitle("TRANSPORT+")
            .withSubtitle("Voyagez en toute sécurité")
            .withFeatures(java.util.Arrays.asList(
                "\uf017 Réservation en ligne rapide",
                "\uf09d Paiement sécurisé",
                "\uf0f3 Support 24/7"
            ));
    }
    
    public static GradientPanel createRegistrationPanel() {
        return new GradientPanel()
            .withTitle("REJOIGNEZ-NOUS")
            .withSubtitle("Créez votre compte en 2 minutes")
            .withFeatures(java.util.Arrays.asList(
                "\uf0e7 Tarifs préférentiels",          // trophy icon
                "\uf1da Historique des voyages",        // history icon
                "\uf0f3 Notifications personnalisées",  // bell icon
                "\uf06b Fidélité récompensée"          // gift icon
            ));
    }
    
    
}