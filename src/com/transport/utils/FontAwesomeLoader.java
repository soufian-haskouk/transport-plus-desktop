package com.transport.utils;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

public class FontAwesomeLoader {
    private static Font fontAwesomeSolid;
    private static boolean fontLoaded = false;
    
    static {
        loadFont();
    }
    
    private static void loadFont() {
        try {
            // Charger depuis com.transport.resources
            InputStream is = FontAwesomeLoader.class.getResourceAsStream("/com/transport/resources/fa-solid-900.ttf");
            
            if (is == null) {
                // Essayer l'autre format
                is = FontAwesomeLoader.class.getResourceAsStream("/com/transport/resources/Font Awesome 6 Free-Solid-900.otf");
            }
            
            if (is == null) {
                // Essayer le classpath simple
                is = FontAwesomeLoader.class.getClassLoader().getResourceAsStream("fa-solid-900.ttf");
            }
            
            if (is != null) {
                fontAwesomeSolid = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(fontAwesomeSolid);
                fontLoaded = true;
                System.out.println("✅ Font Awesome chargé avec succès depuis " + 
                    (fontAwesomeSolid != null ? "resources" : "inconnu"));
            } else {
                System.err.println("❌ Aucun fichier Font Awesome trouvé dans com.transport.resources");
                // Créer une police par défaut
                fontAwesomeSolid = new Font("Arial", Font.PLAIN, 14);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement Font Awesome: " + e.getMessage());
            e.printStackTrace();
            fontAwesomeSolid = new Font("Arial", Font.PLAIN, 14);
        }
    }
    
    public static Font getSolidFont(float size) {
        if (fontLoaded && fontAwesomeSolid != null) {
            return fontAwesomeSolid.deriveFont(Font.PLAIN, size);
        }
        // Fallback à Arial
        return new Font("Arial", Font.PLAIN, (int)size);
    }
    
    public static boolean isFontLoaded() {
        return fontLoaded;
    }
    
    // Méthode pour déboguer
    public static void printAvailableFonts() {
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();
        
        System.out.println("=== Polices disponibles ===");
        for (String name : fontNames) {
            if (name.contains("Awesome") || name.contains("Font")) {
                System.out.println("Found: " + name);
            }
        }
        System.out.println("Font Awesome chargé: " + fontLoaded);
    }
}