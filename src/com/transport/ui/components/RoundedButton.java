package com.transport.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color hoverColor;
    private Color textColor;
    private Color iconColor; // Nouvelle couleur pour l'icône
    private int radius;
    private boolean isHovered = false;
    private String iconCode; // Code de l'icône
    
    public RoundedButton(String text) {
        super(text);
        this.backgroundColor = new Color(0, 102, 204);
        this.hoverColor = new Color(0, 82, 184);
        this.textColor = Color.WHITE;
        this.iconColor = Color.WHITE; // Par défaut, même couleur que le texte
        this.radius = 25;
        this.iconCode = "";
        
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(textColor);
        
        // Effet hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    // Nouveau constructeur avec icône
    public RoundedButton(String iconCode, String text) {
        this(text);
        this.iconCode = iconCode;
    }
    
    public RoundedButton(String text, Color bgColor, Color hoverColor, Color textColor) {
        this(text);
        this.backgroundColor = bgColor;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        setForeground(textColor);
    }
    
    // Setters pour les couleurs
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }
    
    public void setTextColor(Color color) {
        this.textColor = color;
        setForeground(color);
        repaint();
    }
    
    public void setIconColor(Color color) {
        this.iconColor = color;
        repaint();
    }
    
    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
        repaint();
    }
    
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            setForeground(Color.GRAY);
        } else {
            setForeground(textColor);
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond
        Color currentBgColor = isHovered && isEnabled() ? hoverColor : backgroundColor;
        if (!isEnabled()) {
            currentBgColor = new Color(200, 200, 200);
        }
        
        g2.setColor(currentBgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Si on a une icône, on dessine d'abord l'icône puis le texte
        if (!iconCode.isEmpty()) {
            // Définir la police FontAwesome pour l'icône
            try {
                Font iconFont = getIconFont();
                g2.setFont(iconFont);
            } catch (Exception e) {
                // Fallback
                g2.setFont(getFont());
            }
            
            // Calculer la position pour centrer l'icône et le texte
            FontMetrics fmIcon = g2.getFontMetrics();
            FontMetrics fmText = g2.getFontMetrics(getFont());
            
            int totalWidth = fmIcon.stringWidth(iconCode) + 5 + fmText.stringWidth(getText());
            int startX = (getWidth() - totalWidth) / 2;
            
            // Dessiner l'icône avec sa couleur spécifique
            g2.setColor(isEnabled() ? iconColor : Color.GRAY);
            int iconY = (getHeight() - fmIcon.getHeight()) / 2 + fmIcon.getAscent();
            g2.drawString(iconCode, startX, iconY);
            
            // Dessiner le texte
            g2.setFont(getFont());
            g2.setColor(getForeground());
            int textX = startX + fmIcon.stringWidth(iconCode) + 5;
            int textY = (getHeight() - fmText.getHeight()) / 2 + fmText.getAscent();
            g2.drawString(getText(), textX, textY);
        } else {
            // Texte seul
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), textX, textY);
        }
        
        // Bordure si focus
        if (isFocusOwner()) {
            g2.setColor(new Color(0, 123, 255, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
        }
        
        g2.dispose();
    }
    
    private Font getIconFont() {
        // Charger la police FontAwesome
        try {
            return Font.createFont(Font.TRUETYPE_FONT, 
                getClass().getResourceAsStream("/fonts/fontawesome.ttf"))
                .deriveFont(getFont().getSize() * 1.2f); // Légèrement plus grand
        } catch (Exception e) {
            return getFont(); // Fallback
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.width = Math.max(size.width, 80);
        size.height = Math.max(size.height, 35);
        return size;
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}