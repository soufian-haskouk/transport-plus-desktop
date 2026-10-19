package com.transport.config;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import javax.swing.UIManager;

public class ThemeConfig {
    
    // Couleurs de base
    public static final Color PRIMARY_COLOR = new Color(0, 51, 102);
    public static final Color SECONDARY_COLOR = new Color(0, 102, 204);
    public static final Color ACCENT_COLOR = new Color(255, 204, 0);
    public static final Color LIGHT_GRAY = new Color(211, 211, 211);
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);

    // Couleurs d'états
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    public static final Color ERROR_COLOR = new Color(220, 53, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);
    public static final Color INFO_COLOR = new Color(23, 162, 184);

    /**
     * Charge la police FontAwesome. 
     * IMPORTANT : Le fichier fa-solid-900.ttf doit être dans src/com/transport/resources/
     */
    public static Font getIconFont(float size) {
        try {
            // Chemin pour le fichier .otf
            String path = "/com/transport/resources/Font Awesome 6 Free-Solid-900.otf";
            InputStream is = ThemeConfig.class.getResourceAsStream(path);
            
            if (is == null) {
                // Essayer d'autres noms possibles (sans espaces)
                String[] alternativePaths = {
                    "/com/transport/resources/fa-solid-900.otf",
                    "/com/transport/resources/fontawesome-solid.otf",
                    "/com/transport/resources/FontAwesome6-Solid.otf"
                };
                
                for (String altPath : alternativePaths) {
                    is = ThemeConfig.class.getResourceAsStream(altPath);
                    if (is != null) {
                        path = altPath;
                        break;
                    }
                }
                
                if (is == null) {
                    System.err.println("❌ ERREUR : Fichier de police .otf introuvable !");
                    System.err.println("Noms essayés :");
                    System.err.println("- Font Awesome 6 Free-Solid-900.otf");
                    System.err.println("- fa-solid-900.otf");
                    System.err.println("- fontawesome-solid.otf");
                    System.err.println("- FontAwesome6-Solid.otf");
                    return new Font("SansSerif", Font.PLAIN, (int)size);
                }
            }
            
            System.out.println("✅ Police trouvée : " + path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            System.out.println("✅ Police chargée : " + font.getFontName());
            System.out.println("✅ Format : " + font.getFontName() + " - Taille disponible : Oui");
            is.close();
            
            return font.deriveFont(Font.PLAIN, size);
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la police .otf : " + e.getMessage());
            e.printStackTrace();
            return new Font("SansSerif", Font.PLAIN, (int)size);
        }
    }

    public static void applyTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}