package com.transport.ui.frames;

import com.transport.config.ThemeConfig;
import com.transport.models.*;
import com.transport.services.*;
import com.transport.ui.components.RoundedButton;
import com.transport.ui.components.GradientPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.awt.datatransfer.StringSelection;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;



public class ClientFrame extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    
    // Services
    private VoyageService voyageService;
    private ReservationService reservationService;
    private OffreService offreService;
    private ClientService clientService;
    
    // Composants
    private DefaultTableModel voyagesModel;
    private DefaultTableModel reservationsModel;
    private JTable voyagesTable;
    private JTable reservationsTable;
    
    // Formats de date
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    // Références aux combos du formulaire de recherche (pour filtrage)
    private JComboBox<String> searchDepartCombo;
    private JComboBox<String> searchArriveeCombo;
    
    // Constantes de couleurs
    private static final Color[] TAB_COLORS = {
        new Color(0, 123, 255),    // Bleu pour Tableau de bord
        new Color(40, 167, 69),    // Vert pour Voyages
        new Color(255, 193, 7),    // Jaune/Or pour Réservations
        new Color(220, 53, 69),    // Rouge pour Offres
        new Color(111, 66, 193)    // Violet pour Profil
    };
    
    private static final Color[] ACTION_CARD_COLORS = {
        new Color(0, 123, 255),    // Bleu pour Rechercher
        new Color(40, 167, 69),    // Vert pour Billets
        new Color(255, 193, 7),    // Jaune/Or pour Offres
        new Color(220, 53, 69)     // Rouge pour Contact
    };
    
    // Couleurs supplémentaires
    private static final Color GRAY_COLOR = new Color(108, 117, 125);
    private static final Color LIGHT_GRAY = new Color(230, 230, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color INFO_COLOR = new Color(0, 123, 255);
    
    public ClientFrame(User user) {
        super("Espace Client - " + user.getFullName());
        this.currentUser = user;
        
        // Initialiser les services
        this.voyageService = new VoyageService();
        this.reservationService = new ReservationService();
        this.offreService = new OffreService();
        this.clientService = new ClientService();
        
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        initUI();
        loadInitialData();
    }
    
    private void initUI() {
        ThemeConfig.applyTheme();
        setLayout(new BorderLayout());
        
        // Initialiser les modèles de table AVANT de créer les panels
        voyagesModel = new DefaultTableModel();
        reservationsModel = new DefaultTableModel();
        
        // Créer les composants
        JPanel sidebar = createSidebar();
        JPanel header = createHeader();
        tabbedPane = new JTabbedPane();
        
        // Ajouter un listener pour mettre à jour l'apparence des onglets quand on change
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateAllTabComponents();
                
                // Rafraîchir les données selon l'onglet sélectionné
                int selectedIndex = tabbedPane.getSelectedIndex();
                switch(selectedIndex) {
                    case 2: // Réservations
                        refreshReservationsTable();
                        break;
                    case 3: // Offres
                        refreshOffresTab();
                        break;
                }
            }
        });
        
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Ajouter les onglets
        tabbedPane.addTab("", createHomePanel());
        tabbedPane.addTab("", createSearchPanel());
        tabbedPane.addTab("", createReservationsPanel());
        tabbedPane.addTab("", createOffresPanel());
        tabbedPane.addTab("", createProfilePanel());
        
        // Définir les composants d'onglet avec icônes FontAwesome
        setTabComponent(0, "\uf015", "Tableau de bord");
        setTabComponent(1, "\uf002", "Voyages");
        setTabComponent(2, "\uf3ff", "Réservations");
        setTabComponent(3, "\uf02b", "Offres");
        setTabComponent(4, "\uf007", "Profil");
        
        // Mettre à jour l'apparence initiale
        updateAllTabComponents();
        
        // Ajouter les composants à la fenêtre
        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }
    
    // Méthode pour définir les onglets avec icônes colorées
 // Méthode pour définir les onglets avec icônes colorées
    private void setTabComponent(int index, String icon, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panel.setOpaque(false);
        
        Color tabColor = TAB_COLORS[index % TAB_COLORS.length];
        
        // Créer l'icône colorée
        JLabel iconLabel = createIconLabel(icon, tabColor, 16f);
        
        // Texte
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, 12));
        textLabel.setForeground(tabColor);
        
        panel.add(iconLabel);
        panel.add(textLabel);
        
        // Ajouter un effet visuel quand l'onglet est sélectionné
        if (index == tabbedPane.getSelectedIndex()) {
            panel.setBackground(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 20));
            panel.setOpaque(true);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, tabColor),
                new EmptyBorder(4, 8, 1, 8)
            ));
        } else {
            panel.setBorder(new EmptyBorder(4, 8, 4, 8));
        }
        
        // Rendre TOUT le panel cliquable pour sélectionner l'onglet
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.setSelectedIndex(index);
                updateAllTabComponents();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (index != tabbedPane.getSelectedIndex()) {
                    panel.setBackground(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 10));
                    panel.setOpaque(true);
                    panel.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (index != tabbedPane.getSelectedIndex()) {
                    panel.setOpaque(false);
                    panel.repaint();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (index != tabbedPane.getSelectedIndex()) {
                    panel.setBackground(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 30));
                    panel.repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (index != tabbedPane.getSelectedIndex()) {
                    panel.setBackground(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 10));
                    panel.repaint();
                }
            }
        });
        
        // S'assurer que les labels à l'intérieur du panel ne bloquent pas les événements de souris
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.dispatchEvent(new MouseEvent(panel, 
                    MouseEvent.MOUSE_CLICKED, 
                    System.currentTimeMillis(), 
                    0, 
                    e.getX() + iconLabel.getX(), 
                    e.getY() + iconLabel.getY(), 
                    1, 
                    false));
            }
        });
        
        textLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panel.dispatchEvent(new MouseEvent(panel, 
                    MouseEvent.MOUSE_CLICKED, 
                    System.currentTimeMillis(), 
                    0, 
                    e.getX() + textLabel.getX(), 
                    e.getY() + textLabel.getY(), 
                    1, 
                    false));
            }
        });
        
        tabbedPane.setTabComponentAt(index, panel);
    }
    
    // Méthode utilitaire pour créer des icônes colorées
    private JLabel createIconLabel(String iconCode, Color color, float size) {
        JLabel iconLabel = new JLabel(iconCode);
        try {
            Font iconFont = ThemeConfig.getIconFont(size);
            if (iconFont != null && !iconCode.isEmpty() && iconFont.canDisplay(iconCode.charAt(0))) {
                iconLabel.setFont(iconFont);
            } else {
                // FontAwesome non disponible — masquer le caractère illisible, afficher rien
                iconLabel.setText("");
                iconLabel.setFont(new Font("Dialog", Font.PLAIN, (int) size));
            }
        } catch (Exception e) {
            iconLabel.setText("");
            iconLabel.setFont(new Font("Dialog", Font.PLAIN, (int) size));
        }
        iconLabel.setForeground(color);
        return iconLabel;
    }
    
    
 // Méthode pour créer des boutons avec icônes colorées
    private JButton createIconButton(String iconCode, Color color, String tooltip) {
        JButton button = new JButton(iconCode);
        button.setFont(ThemeConfig.getIconFont(14f));
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Augmenter le padding
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false); // Important pour éviter les problèmes de clic
        button.setOpaque(true); // Mais rendre opaque pour le fond
        
        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }
        
        // Effet de survol amélioré
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 1),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            }
        });
        
        return button;
    }
    
    // Méthode pour créer des boutons arrondis avec icônes
    private RoundedButton createRoundedIconButton(String icon, String text, Color color) {
        // Construire le label du bouton :
        // - Si FontAwesome est disponible : icône FontAwesome + espace + texte normal
        // - Sinon : texte seul (sans caractère FontAwesome illisible)
        String label;
        Font btnFont;
        try {
            Font iconFont = ThemeConfig.getIconFont(14f);
            if (iconFont != null && !icon.isEmpty()) {
                // Police disponible — on garde icône + texte, tout en FontAwesome
                // Pour éviter que le texte soit en FontAwesome (illisible),
                // on n'inclut l'icône que si la police est réellement chargée.
                // On teste en vérifiant que canDisplay retourne true pour le glyphe.
                if (iconFont.canDisplay(icon.charAt(0))) {
                    label = icon + (text.isEmpty() ? "" : "  " + text);
                    btnFont = iconFont.deriveFont(Font.PLAIN, 13f);
                } else {
                    label = text;
                    btnFont = new Font("Dialog", Font.BOLD, 13);
                }
            } else {
                label = text;
                btnFont = new Font("Dialog", Font.BOLD, 13);
            }
        } catch (Exception e) {
            label = text;
            btnFont = new Font("Dialog", Font.BOLD, 13);
        }

        RoundedButton button = new RoundedButton(label);
        button.setFont(btnFont);
        button.setBackgroundColor(color);
        button.setHoverColor(color.darker());
        button.setTextColor(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
    
    // Méthode pour mettre à jour tous les onglets quand on change de sélection
    private void updateAllTabComponents() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tabComp = tabbedPane.getTabComponentAt(i);
            if (tabComp instanceof JPanel) {
                JPanel panel = (JPanel) tabComp;
                
                Color tabColor = TAB_COLORS[i % TAB_COLORS.length];
                
                if (i == tabbedPane.getSelectedIndex()) {
                    // Onglet sélectionné
                    panel.setBackground(new Color(tabColor.getRed(), tabColor.getGreen(), tabColor.getBlue(), 20));
                    panel.setOpaque(true);
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 3, 0, tabColor),
                        new EmptyBorder(4, 8, 1, 8)
                    ));
                    
                    // Rendre le texte plus gras
                    for (Component comp : panel.getComponents()) {
                        if (comp instanceof JLabel) {
                            JLabel label = (JLabel) comp;
                            if (label.getText().length() > 1 || Character.isLetterOrDigit(label.getText().charAt(0))) {
                                label.setFont(new Font("Arial", Font.BOLD, 12));
                            }
                        }
                    }
                } else {
                    // Onglet non sélectionné
                    panel.setOpaque(false);
                    panel.setBorder(new EmptyBorder(4, 8, 4, 8));
                    
                    // Rendre le texte normal
                    for (Component comp : panel.getComponents()) {
                        if (comp instanceof JLabel) {
                            JLabel label = (JLabel) comp;
                            if (label.getText().length() > 1 || Character.isLetterOrDigit(label.getText().charAt(0))) {
                                label.setFont(new Font("Arial", Font.PLAIN, 12));
                            }
                        }
                    }
                }
                panel.repaint();
            }
        }
    }
    
    private void loadInitialData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Charger les données en arrière-plan
                refreshVoyages();
                refreshReservations();
                refreshOffres();
                return null;
            }
            
            @Override
            protected void done() {
                // Mettre à jour l'interface après le chargement
                updatePromoPanel();
            }
        };
        worker.execute();
    }
    
    private JPanel createSidebar() {
        // Utiliser GradientPanel au lieu de JPanel
        GradientPanel sidebar = new GradientPanel();
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        // Désactiver le contenu par défaut (titre, sous-titre, features)
        sidebar.withTitle("");
        sidebar.withSubtitle("");
        sidebar.withFeatures(new java.util.ArrayList<>());
        sidebar.withIcon(false);
        
        // Logo avec icône colorée
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JLabel logoIcon = createIconLabel("\uf238", Color.WHITE, 30f);
        logoIcon.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(logoIcon, BorderLayout.CENTER);
        
        // Menu avec RoundedButton
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new GridLayout(7, 1, 5, 10)); // Réduit l'espacement vertical
        menuPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        // Données des boutons du menu
        Object[][] menuData = {
            {"\uf015", "Accueil", TAB_COLORS[0]},
            {"\uf002", "Rechercher", TAB_COLORS[1]},
            {"\uf3ff", "Mes réservations", TAB_COLORS[2]},
            {"\uf02b", "Offres spéciales", TAB_COLORS[3]},
            {"\uf007", "Mon profil", TAB_COLORS[4]},
            {"\uf059", "Aide", GRAY_COLOR},
            {"\uf2f5", "Déconnexion", ERROR_COLOR}
        };
        
        for (Object[] data : menuData) {
            String iconCode = (String) data[0];
            String text = (String) data[1];
            Color color = (Color) data[2];
            
            // Utiliser RoundedButton au lieu de JPanel
            RoundedButton menuButton = new RoundedButton(iconCode + "  " + text);
            menuButton.setFont(ThemeConfig.getIconFont(14f));
            menuButton.setBackgroundColor(new Color(255, 255, 255, 0)); // Transparent
            menuButton.setHoverColor(new Color(255, 255, 255, 30)); // Hover semi-transparent
            menuButton.setTextColor(Color.WHITE);
            menuButton.setRadius(10); // Bords légèrement arrondis
            menuButton.setBorderPainted(false);
            menuButton.setFocusPainted(false);
            menuButton.setContentAreaFilled(false); // Pour les effets personnalisés
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setBorder(new EmptyBorder(10, 15, 10, 15)); // Padding interne
            
            // S'assurer que la couleur du texte reste blanche même pendant le hover
            menuButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuButton.setForeground(Color.WHITE);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    menuButton.setForeground(Color.WHITE);
                }
            });
            
            // Gérer le clic
            menuButton.addActionListener(e -> handleMenuClick(text));
            
            menuPanel.add(menuButton);
        }
        
        // Info utilisateur
        JPanel userPanel = new JPanel();
        userPanel.setOpaque(false);
        userPanel.setLayout(new BorderLayout(10, 5));
        userPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
        JLabel userIcon = createIconLabel("\uf2bd", Color.WHITE, 30f);
        userIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        String nomComplet = (currentUser.getPrenom() != null ? currentUser.getPrenom() : "") + " " +
                          (currentUser.getNom() != null ? currentUser.getNom() : "");
        JLabel userName = new JLabel(nomComplet.trim().isEmpty() ? "Client" : nomComplet);
        userName.setFont(new Font("Arial", Font.BOLD, 14));
        userName.setForeground(Color.WHITE);
        userName.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel userRole = new JLabel("Espace Client");
        userRole.setFont(new Font("Arial", Font.ITALIC, 11));
        userRole.setForeground(Color.LIGHT_GRAY);
        userRole.setHorizontalAlignment(SwingConstants.CENTER);
        
        userPanel.add(userIcon, BorderLayout.NORTH);
        userPanel.add(userName, BorderLayout.CENTER);
        userPanel.add(userRole, BorderLayout.SOUTH);
        
        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(userPanel, BorderLayout.SOUTH);
        
        return sidebar;
    }
    
    private void handleMenuClick(String menuItem) {
        if (menuItem.contains("Accueil")) {
            tabbedPane.setSelectedIndex(0);
        } else if (menuItem.contains("Rechercher")) {
            tabbedPane.setSelectedIndex(1);
        } else if (menuItem.contains("réservations")) {
            tabbedPane.setSelectedIndex(2);
            refreshReservationsTable();
        } else if (menuItem.contains("Offres")) {
            tabbedPane.setSelectedIndex(3);
            refreshOffresTab();
        } else if (menuItem.contains("profil")) {
            tabbedPane.setSelectedIndex(4);
        } else if (menuItem.contains("Aide")) {
            showHelp();
        } else if (menuItem.contains("Déconnexion")) {
            logout();
        }
    }
    
    // Méthode pour rafraîchir les données de l'onglet Offres
    private void refreshOffresTab() {
        // Trouver le panel des offres dans le tabbedPane
        Component offresComponent = tabbedPane.getComponentAt(3);
        if (offresComponent instanceof JPanel) {
            JPanel offresPanel = (JPanel) offresComponent;
            // Trouver le grid panel
            for (Component comp : offresPanel.getComponents()) {
                if (comp instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) comp;
                    Component view = scrollPane.getViewport().getView();
                    if (view instanceof JPanel) {
                        loadOffresIntoGrid((JPanel) view);
                        break;
                    }
                }
            }
        }
    }
    
    private void showHelp() {
        JDialog helpDialog = new JDialog(this, "Aide et Support", true);
        helpDialog.setSize(400, 300);
        helpDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf059", INFO_COLOR, 20f);
        title.setText("\uf059  Aide et Support");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextArea helpText = new JTextArea(
            "Pour obtenir de l'aide, contactez le support technique:\n\n" +
            "\uf095  Téléphone: +212 5 XX XX XX XX\n" +
            "\uf0e0  Email: support@transportplus.ma\n" +
            "\uf017  Support: 24h/24, 7j/7\n\n" +
            "\uf0a1  FAQ: Consultez notre base de connaissances\n" +
            "\uf15c  Documentation: Guide d'utilisation disponible"
        );
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));
        helpText.setEditable(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setBackground(new Color(248, 249, 250));
        helpText.setForeground(new Color(73, 80, 87));
        
        RoundedButton closeBtn = createRoundedIconButton("\uf00d", "Fermer", ERROR_COLOR);
        closeBtn.addActionListener(e -> helpDialog.dispose());
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(helpText), BorderLayout.CENTER);
        panel.add(closeBtn, BorderLayout.SOUTH);
        
        helpDialog.add(panel);
        helpDialog.setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, LIGHT_GRAY),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        // Titre avec icône - côté gauche
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = createIconLabel("\uf015", TAB_COLORS[0], 20f);
        JLabel textLabel = new JLabel("Espace Client");
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setForeground(TAB_COLORS[0]);
        
        titlePanel.add(iconLabel);
        titlePanel.add(textLabel);
        
        // Panel côté droit : boutons + séparateur + date/heure
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        
        // Bouton Notifications
        RoundedButton notifBtn = createRoundedIconButton("\uf0f3", "", INFO_COLOR);
        notifBtn.setToolTipText("Notifications");
        notifBtn.setBorder(new EmptyBorder(5, 8, 5, 8));
        notifBtn.setBackgroundColor(Color.WHITE);
        notifBtn.setHoverColor(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 20));
        notifBtn.setTextColor(INFO_COLOR);
        notifBtn.setRadius(5);
        notifBtn.addActionListener(e -> showNotifications());
        
        // Bouton Actualiser
        RoundedButton refreshBtn = createRoundedIconButton("\uf021", "", INFO_COLOR);
        refreshBtn.setToolTipText("Actualiser les données");
        refreshBtn.setBorder(new EmptyBorder(5, 8, 5, 8));
        refreshBtn.setBackgroundColor(Color.WHITE);
        refreshBtn.setHoverColor(new Color(INFO_COLOR.getRed(), INFO_COLOR.getGreen(), INFO_COLOR.getBlue(), 20));
        refreshBtn.setTextColor(INFO_COLOR);
        refreshBtn.setRadius(5);
        refreshBtn.addActionListener(e -> refreshAllData());
        
        
   
        
        
        // Séparateur vertical
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(1, 25));
        separator.setForeground(LIGHT_GRAY);
        
        // Panel pour la date/heure
        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        dateTimePanel.setBackground(Color.WHITE);
        
        JLabel clockIcon = createIconLabel("\uf017", GRAY_COLOR, 14f);
        
        JLabel dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateTimeLabel.setForeground(GRAY_COLOR);
        
        // Timer pour mettre à jour la date/heure
        javax.swing.Timer dateTimer = new javax.swing.Timer(1000, e -> {
            dateTimeLabel.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        dateTimer.start();
        
        dateTimePanel.add(clockIcon);
        dateTimePanel.add(dateTimeLabel);
        
        // Ajouter tous les éléments dans l'ordre
        rightPanel.add(notifBtn);
        rightPanel.add(refreshBtn);
        rightPanel.add(separator);
        rightPanel.add(dateTimePanel);
        
        // Ajouter à l'en-tête
        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout(20, 20));
        homePanel.setBackground(Color.WHITE);
        homePanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Message de bienvenue
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 248, 255));
        welcomePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(173, 216, 230), 2),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        String nomComplet = (currentUser.getPrenom() != null ? currentUser.getPrenom() : "") + " " +
                          (currentUser.getNom() != null ? currentUser.getNom() : "");
        if (nomComplet.trim().isEmpty()) nomComplet = "Cher Client";
        
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>"
            + "<h1 style='color: #0066cc;'>Bienvenue, " + nomComplet + " !</h1>"
            + "<p style='font-size: 16px; color: #666;'>Nous sommes ravis de vous revoir. Que souhaitez-vous faire aujourd'hui ?</p>"
            + "</div></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        homePanel.add(welcomePanel, BorderLayout.NORTH);
        
        // Actions rapides
        JPanel quickActionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        quickActionsPanel.setBackground(Color.WHITE);
        quickActionsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        // Icônes FontAwesome pour les actions rapides
        String[][] actions = {
            {"\uf002", "Rechercher", "Recherchez et réservez votre prochain voyage"},
            {"\uf3ff", "Mes billets", "Consultez vos billets et vos voyages à venir"},
            {"\uf02b", "Offres", "Profitez de réductions exclusives"},
            {"\uf075", "Contact", "Besoin d'aide ? Contactez notre support"}
        };
        
        for (int i = 0; i < actions.length; i++) {
            final int actionIndex = i;
            String iconCode = actions[i][0];
            String title = actions[i][1];
            String description = actions[i][2];
            
            JPanel actionCard = createActionCard(iconCode, title, description, actionIndex);
            quickActionsPanel.add(actionCard);
        }
        
        homePanel.add(quickActionsPanel, BorderLayout.CENTER);
        
        // Panel des offres (chargé dynamiquement)
        JPanel promoPanel = new JPanel(new BorderLayout());
        promoPanel.setBackground(new Color(255, 248, 225));
        promoPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 193, 7), 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        promoPanel.setName("promoPanel");
        homePanel.add(promoPanel, BorderLayout.SOUTH);
        
        return homePanel;
    }
    
    private JPanel createActionCard(String iconCode, String title, String description, int actionIndex) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color cardColor = ACTION_CARD_COLORS[actionIndex % ACTION_CARD_COLORS.length];
        
        // En-tête avec icône FontAwesome
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setOpaque(true);
        
        // Créer l'icône FontAwesome
        JLabel iconLabel = createIconLabel(iconCode, cardColor, 24f);
        iconLabel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(cardColor);
        titleLabel.setOpaque(false);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        // Description
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setBackground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        
        // Ajouter effet hover sur TOUTE la carte
        final int finalActionIndex = actionIndex;
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(cardColor, 2),
                    new EmptyBorder(20, 20, 20, 20)
                ));
                card.setBackground(new Color(248, 249, 250));
                headerPanel.setBackground(new Color(248, 249, 250));
                descArea.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(LIGHT_GRAY, 1),
                    new EmptyBorder(20, 20, 20, 20)
                ));
                card.setBackground(Color.WHITE);
                headerPanel.setBackground(Color.WHITE);
                descArea.setBackground(Color.WHITE);
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                switch(finalActionIndex) {
                    case 0:
                        tabbedPane.setSelectedIndex(1); // Onglet Rechercher
                        break;
                    case 1:
                        tabbedPane.setSelectedIndex(2); // Onglet Réservations
                        refreshReservationsTable();
                        break;
                    case 2:
                        tabbedPane.setSelectedIndex(3); // Onglet Offres
                        break;
                    case 3:
                        showHelp();
                        break;
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                card.setBackground(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 10));
                headerPanel.setBackground(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 10));
                descArea.setBackground(new Color(cardColor.getRed(), cardColor.getGreen(), cardColor.getBlue(), 10));
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                headerPanel.setBackground(new Color(248, 249, 250));
                descArea.setBackground(new Color(248, 249, 250));
            }
        });
        
        // Transférer les événements de souris des composants enfants vers la carte
        MouseListener cardMouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                card.dispatchEvent(new MouseEvent(card, 
                    MouseEvent.MOUSE_CLICKED, 
                    System.currentTimeMillis(), 
                    0, 
                    e.getX() + ((Component)e.getSource()).getX(), 
                    e.getY() + ((Component)e.getSource()).getY(), 
                    1, 
                    false));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.dispatchEvent(new MouseEvent(card, 
                    MouseEvent.MOUSE_ENTERED, 
                    System.currentTimeMillis(), 
                    0, 
                    e.getX() + ((Component)e.getSource()).getX(), 
                    e.getY() + ((Component)e.getSource()).getY(), 
                    0, 
                    false));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.dispatchEvent(new MouseEvent(card, 
                    MouseEvent.MOUSE_EXITED, 
                    System.currentTimeMillis(), 
                    0, 
                    e.getX() + ((Component)e.getSource()).getX(), 
                    e.getY() + ((Component)e.getSource()).getY(), 
                    0, 
                    false));
            }
        };
        
        // Ajouter le listener à tous les composants enfants
        iconLabel.addMouseListener(cardMouseListener);
        titleLabel.addMouseListener(cardMouseListener);
        descArea.addMouseListener(cardMouseListener);
        headerPanel.addMouseListener(cardMouseListener);
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);
        
        return card;
    }
    
    
    
 // Voyage PAGE
    private JPanel createSearchPanel() {
        // Panel principal avec layout BorderLayout
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(240, 242, 245));
        
        // ========== PARTIE SUPÉRIEURE : FORMULAIRE DE RECHERCHE ==========
        JPanel searchFormContainer = new JPanel(new BorderLayout());
        searchFormContainer.setBackground(Color.WHITE);
        searchFormContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        
        // En-tête avec titre et icône
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = createIconLabel("\uf002", new Color(0, 102, 204), 24f);
        JLabel titleLabel = new JLabel("Rechercher un voyage");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(51, 51, 51));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);
        
        // Indicateur de statut
        JLabel statusLabel = new JLabel("Sélectionnez vos critères de recherche");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(102, 102, 102));
        statusLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        headerPanel.add(titlePanel, BorderLayout.NORTH);
        headerPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Conteneur du formulaire avec ombre
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BoxLayout(formWrapper, BoxLayout.Y_AXIS));
        formWrapper.setBackground(new Color(245, 247, 250));
        formWrapper.setBorder(new EmptyBorder(20, 30, 30, 30));
        
        // Carte du formulaire
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        formCard.setMaximumSize(new Dimension(1200, 400));
        
        // ========== GRID DES CHAMPS DE RECHERCHE ==========
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        
        // Ville de départ
        JLabel departLabel = createFieldLabel("\uf041  Ville de départ", TAB_COLORS[1]);
        JComboBox<String> departCombo = new JComboBox<>();
        departCombo.addItem("Sélectionnez votre ville de départ");
        styleComboBox(departCombo);
        searchDepartCombo = departCombo;

        // Ville d'arrivée
        JLabel arriveeLabel = createFieldLabel("\uf041  Ville d'arrivée", SUCCESS_COLOR);
        JComboBox<String> arriveeCombo = new JComboBox<>();
        arriveeCombo.addItem("-- Choisissez d'abord la ville de départ --");
        arriveeCombo.setEnabled(false);
        styleComboBox(arriveeCombo);
        arriveeCombo.setBackground(new Color(248, 249, 250));
        searchArriveeCombo = arriveeCombo;

        // Date de départ
        JLabel dateLabel = createFieldLabel("\uf073  Date de départ", INFO_COLOR);
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        dateSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateSpinner.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JButton calendarBtn = createIconButton("\uf133", INFO_COLOR, "Ouvrir le calendrier");
        calendarBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        calendarBtn.addActionListener(e -> dateSpinner.requestFocus());
        
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        datePanel.add(calendarBtn, BorderLayout.EAST);
        
        // Nombre de passagers
        JLabel passagersLabel = createFieldLabel("\uf0c0  Nombre de passagers", GRAY_COLOR);
        JPanel passagersPanel = new JPanel(new BorderLayout());
        passagersPanel.setBackground(Color.WHITE);
        passagersPanel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        
        JSpinner passagersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        passagersSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passagersSpinner.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JButton minusBtn = createIconButton("\uf068", GRAY_COLOR, "Réduire");
        minusBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        minusBtn.addActionListener(e -> {
            int value = (Integer) passagersSpinner.getValue();
            if (value > 1) passagersSpinner.setValue(value - 1);
        });
        
        JButton plusBtn = createIconButton("\uf067", GRAY_COLOR, "Augmenter");
        plusBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        plusBtn.addActionListener(e -> {
            int value = (Integer) passagersSpinner.getValue();
            if (value < 20) passagersSpinner.setValue(value + 1);
        });
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(minusBtn);
        buttonPanel.add(plusBtn);
        
        passagersPanel.add(passagersSpinner, BorderLayout.CENTER);
        passagersPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Ajout des composants au grid
        // Première ligne
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        gridPanel.add(departLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        gridPanel.add(departCombo, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0.3;
        gridPanel.add(arriveeLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 0.7;
        gridPanel.add(arriveeCombo, gbc);
        
        // Deuxième ligne
        gbc.gridx = 0; gbc.gridy = 1;
        gridPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1;
        gridPanel.add(datePanel, gbc);
        
        gbc.gridx = 2;
        gridPanel.add(passagersLabel, gbc);
        
        gbc.gridx = 3;
        gridPanel.add(passagersPanel, gbc);
        
        // ========== BOUTONS D'ACTION ==========
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        RoundedButton searchBtn = createRoundedIconButton("\uf002", "Rechercher des voyages", new Color(0, 102, 204));
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.setPreferredSize(new Dimension(200, 45));
        searchBtn.setBackgroundColor(new Color(0, 102, 204));
        searchBtn.setHoverColor(new Color(0, 86, 179));
        
        RoundedButton resetBtn = createRoundedIconButton("\uf0e2", "Réinitialiser", new Color(108, 117, 125));
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setPreferredSize(new Dimension(150, 45));
        resetBtn.setBackgroundColor(new Color(108, 117, 125));
        resetBtn.setHoverColor(new Color(90, 98, 104));
        
        actionPanel.add(searchBtn);
        actionPanel.add(resetBtn);
        
        // ========== ASSEMBLAGE DU FORMULAIRE ==========
        formCard.add(gridPanel);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(actionPanel);
        
        formWrapper.add(formCard);
        
        searchFormContainer.add(headerPanel, BorderLayout.NORTH);
        searchFormContainer.add(formWrapper, BorderLayout.CENTER);
        
        // ========== PARTIE CENTRALE : RÉSULTATS (CACHÉE AU DÉPART) ==========
        JPanel resultsContainer = new JPanel(new BorderLayout());
        resultsContainer.setBackground(new Color(240, 242, 245));
        resultsContainer.setVisible(false);
        
        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setBackground(Color.WHITE);
        resultsHeader.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel resultsCount = new JLabel("0 voyages trouvés");
        resultsCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultsCount.setForeground(new Color(102, 102, 102));
        
        JLabel resultsTitle = new JLabel("Résultats de recherche");
        resultsTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        resultsTitle.setForeground(new Color(51, 51, 51));
        
        resultsHeader.add(resultsTitle, BorderLayout.WEST);
        resultsHeader.add(resultsCount, BorderLayout.EAST);
        
        // ========== TABLEAU DES RÉSULTATS ==========
        String[] columns = {"ID", "Trajet", "Date & Heure", "Véhicule", "Prix (DH)", "Disponibilité", "Actions"};
        voyagesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Seule la colonne Actions est éditable
            }
        };
        
        voyagesTable = new JTable(voyagesModel);
        styleResultsTable(voyagesTable);
        
        // Cacher la colonne ID
        voyagesTable.getColumnModel().getColumn(0).setMinWidth(0);
        voyagesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        voyagesTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(voyagesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panel vide pour l'état initial
        JPanel emptyStatePanel = createEmptyStatePanel();
        
        resultsContainer.add(resultsHeader, BorderLayout.NORTH);
        resultsContainer.add(scrollPane, BorderLayout.CENTER);
        
        // ========== CONTENEUR PRINCIPAL POUR LES RÉSULTATS DYNAMIQUES ==========
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 242, 245));
        mainContentPanel.add(searchFormContainer, BorderLayout.NORTH);
        mainContentPanel.add(emptyStatePanel, BorderLayout.CENTER);
        
        // Stocker les références
        panel.putClientProperty("resultsContainer", resultsContainer);
        panel.putClientProperty("emptyStatePanel", emptyStatePanel);
        panel.putClientProperty("mainContentPanel", mainContentPanel);
        panel.putClientProperty("resultsCount", resultsCount);
        
        // ========== LOGIQUE DES LISTENERS ==========
        // Listener pour la ville de départ
        departCombo.addActionListener(e -> {
            String selectedDepart = (String) departCombo.getSelectedItem();
            
            if (selectedDepart != null && !selectedDepart.equals("Sélectionnez votre ville de départ")) {
                statusLabel.setText("Chargement des destinations disponibles...");
                statusLabel.setForeground(INFO_COLOR);
                
                SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
                    @Override
                    protected List<String> doInBackground() throws Exception {
                        return voyageService.getVillesArriveeByDepart(selectedDepart);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            List<String> villesArrivee = get();
                            
                            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                            model.addElement("Sélectionnez votre ville d'arrivée");
                            
                            if (villesArrivee.isEmpty()) {
                                model.addElement("Aucune destination disponible");
                                arriveeCombo.setEnabled(false);
                                arriveeCombo.setBackground(new Color(248, 249, 250));
                                statusLabel.setText("Aucune destination disponible depuis " + selectedDepart);
                                statusLabel.setForeground(WARNING_COLOR);
                            } else {
                                for (String ville : villesArrivee) {
                                    model.addElement(ville);
                                }
                                arriveeCombo.setEnabled(true);
                                arriveeCombo.setBackground(Color.WHITE);
                                statusLabel.setText(villesArrivee.size() + " destinations disponibles depuis " + selectedDepart);
                                statusLabel.setForeground(SUCCESS_COLOR);
                            }
                            
                            arriveeCombo.setModel(model);
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            statusLabel.setText("Erreur lors du chargement des destinations");
                            statusLabel.setForeground(ERROR_COLOR);
                        }
                    }
                };
                worker.execute();
            } else {
                arriveeCombo.removeAllItems();
                arriveeCombo.addItem("-- Choisissez d'abord la ville de départ --");
                arriveeCombo.setEnabled(false);
                arriveeCombo.setBackground(new Color(248, 249, 250));
                statusLabel.setText("Sélectionnez vos critères de recherche");
                statusLabel.setForeground(new Color(102, 102, 102));
            }
        });
        
        // Listener pour le bouton Rechercher
        searchBtn.addActionListener(e -> {
            String depart = (String) departCombo.getSelectedItem();
            String arrivee = (String) arriveeCombo.getSelectedItem();
            Date date = (Date) dateSpinner.getValue();
            int passagers = (Integer) passagersSpinner.getValue();
            
            // Validation
            if (depart == null || depart.equals("Sélectionnez votre ville de départ")) {
                showValidationError("Veuillez sélectionner une ville de départ", "Sélection requise");
                departCombo.requestFocus();
                return;
            }
            
            if (arrivee == null || arrivee.equals("Sélectionnez votre ville d'arrivée") || 
                arrivee.equals("Aucune destination disponible") || !arriveeCombo.isEnabled()) {
                showValidationError("Veuillez sélectionner une ville d'arrivée valide", "Sélection requise");
                arriveeCombo.requestFocus();
                return;
            }
            
            if (depart.equals(arrivee)) {
                showValidationError("Les villes de départ et d'arrivée doivent être différentes", "Sélection invalide");
                return;
            }
            
            // Exécuter la recherche
            executeSearch(depart, arrivee, date, passagers, resultsCount, resultsContainer, panel);
        });
        
        // Listener pour le bouton Réinitialiser
        resetBtn.addActionListener(e -> {
            departCombo.setSelectedIndex(0);
            arriveeCombo.removeAllItems();
            arriveeCombo.addItem("-- Choisissez d'abord la ville de départ --");
            arriveeCombo.setEnabled(false);
            arriveeCombo.setBackground(new Color(248, 249, 250));
            dateSpinner.setValue(new Date());
            passagersSpinner.setValue(1);
            statusLabel.setText("Sélectionnez vos critères de recherche");
            statusLabel.setForeground(new Color(102, 102, 102));
            
            // Réinitialiser le tableau
            voyagesModel.setRowCount(0);
            
            // Revenir à l'état vide
            JPanel mainContent = (JPanel) panel.getClientProperty("mainContentPanel");
            JPanel emptyState = (JPanel) panel.getClientProperty("emptyStatePanel");
            JPanel results = (JPanel) panel.getClientProperty("resultsContainer");
            
            if (mainContent != null && emptyState != null && results != null) {
                if (results.getParent() != null) {
                    mainContent.remove(results);
                    mainContent.add(emptyState, BorderLayout.CENTER);
                    mainContent.revalidate();
                    mainContent.repaint();
                }
            }
        });
        
        // ========== CHARGEMENT INITIAL DES VILLES DE DÉPART ==========
        SwingWorker<List<String>, Void> initialLoader = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                return voyageService.getAllVillesDepart();
            }
            
            @Override
            protected void done() {
                try {
                    List<String> villesDepart = get();
                    
                    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                    model.addElement("Sélectionnez votre ville de départ");
                    
                    for (String ville : villesDepart) {
                        model.addElement(ville);
                    }
                    
                    departCombo.setModel(model);
                    
                    if (villesDepart.isEmpty()) {
                        statusLabel.setText("Aucun voyage disponible pour le moment");
                        statusLabel.setForeground(WARNING_COLOR);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    // Villes par défaut en cas d'erreur
                    String[] villesParDefaut = {
                        "Casablanca", "Rabat", "Marrakech", "Fès", "Tanger"
                    };
                    
                    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                    model.addElement("Sélectionnez votre ville de départ");
                    for (String ville : villesParDefaut) {
                        model.addElement(ville);
                    }
                    departCombo.setModel(model);
                }
            }
        };
        initialLoader.execute();
        
        // ========== ASSEMBLAGE FINAL ==========
        panel.add(mainContentPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JLabel createFieldLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(color);
        return label;
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index == 0 && value.toString().contains("Sélectionnez")) {
                    setForeground(new Color(108, 117, 125));
                }
                return c;
            }
        });
    }

    private void showValidationError(String message, String title) {
        JOptionPane.showMessageDialog(this,
            "<html><div style='text-align: center; padding: 10px;'>" +
            "<h3 style='color: #dc3545;'>" + title + "</h3>" +
            "<p style='font-size: 14px;'>" + message + "</p>" +
            "</div></html>",
            title,
            JOptionPane.WARNING_MESSAGE);
    }

    private void executeSearch(String departParam, String arriveeParam, Date date, int passagers,
                              JLabel resultsCount, JPanel resultsContainer, JPanel mainPanel) {

        final String depart  = (departParam  != null) ? departParam.trim()  : "";
        final String arrivee = (arriveeParam != null) ? arriveeParam.trim() : "";
        final int finalPassagers = passagers;

        JPanel mainContentPanel = (JPanel) mainPanel.getClientProperty("mainContentPanel");
        JPanel emptyStatePanel  = (JPanel) mainPanel.getClientProperty("emptyStatePanel");

        if (mainContentPanel == null) {
            showValidationError("Erreur d'initialisation de l'interface", "Erreur système");
            return;
        }

        if (emptyStatePanel != null && emptyStatePanel.getParent() != null) {
            mainContentPanel.remove(emptyStatePanel);
            mainContentPanel.add(resultsContainer, BorderLayout.CENTER);
            resultsContainer.setVisible(true);
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        }

        resultsCount.setText("Recherche en cours...");
        resultsCount.setForeground(INFO_COLOR);
        voyagesModel.setRowCount(0);

        SwingWorker<List<Voyage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Voyage> doInBackground() throws Exception {
                List<Voyage> tous = voyageService.getAllVoyages();
                List<Voyage> filtres = new ArrayList<>();
                for (Voyage v : tous) {
                    boolean departOk  = v.getVilleDepart()  != null && v.getVilleDepart().equalsIgnoreCase(depart);
                    boolean arriveeOk = v.getVilleArrivee() != null && v.getVilleArrivee().equalsIgnoreCase(arrivee);
                    boolean placesOk  = v.getPlacesDisponibles() >= finalPassagers;
                    if (departOk && arriveeOk && placesOk) filtres.add(v);
                }
                return filtres;
            }

            @Override
            protected void done() {
                try {
                    List<Voyage> voyages = get();
                    voyagesModel.setRowCount(0);
                    if (voyages != null) {
                        for (Voyage voyage : voyages) {
                            int places = voyage.getPlacesDisponibles();
                            String dispo;
                            if (places >= 10)     dispo = "Disponible";
                            else if (places >= 3) dispo = "Peu de places";
                            else                  dispo = "Dernieres places";

                            voyagesModel.addRow(new Object[]{
                                voyage.getId(),
                                voyage.getVilleDepart() + " → " + voyage.getVilleArrivee(),
                                datetimeFormat.format(voyage.getDateDepart()),
                                voyage.getTypeVehicule() != null ? voyage.getTypeVehicule() : "Non spécifié",
                                String.format("%.2f DH", voyage.getPrix()),
                                dispo,
                                "Réserver"
                            });
                        }
                    }
                    int count = (voyages != null) ? voyages.size() : 0;
                    if (count > 0) {
                        resultsCount.setText(count + " voyage(s) trouvé(s)");
                        resultsCount.setForeground(SUCCESS_COLOR);
                    } else {
                        resultsCount.setText("Aucun voyage disponible pour " + depart + " \u2192 " + arrivee);
                        resultsCount.setForeground(WARNING_COLOR);
                    }
                    voyagesTable.revalidate();
                    voyagesTable.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    resultsCount.setText("Erreur lors de la recherche");
                    resultsCount.setForeground(ERROR_COLOR);
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        "Erreur lors de la recherche : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    // Méthode pour créer un indicateur de chargement
    private JPanel createLoadingIndicator() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        JLabel loadingIcon = new JLabel("⏳");
        loadingIcon.setFont(new Font("Arial", Font.PLAIN, 40));
        loadingIcon.setForeground(INFO_COLOR);
        loadingIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loadingText = new JLabel("Recherche en cours...");
        loadingText.setFont(new Font("Arial", Font.PLAIN, 14));
        loadingText.setForeground(INFO_COLOR);
        loadingText.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadingText.setBorder(new EmptyBorder(10, 0, 0, 0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loadingIcon);
        centerPanel.add(loadingText);
        centerPanel.add(Box.createVerticalGlue());

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(new EmptyBorder(80, 0, 80, 0));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 242, 245));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = createIconLabel("\uf5a5", new Color(200, 200, 200), 80f);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Aucun résultat pour le moment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(150, 150, 150));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        JLabel descLabel = new JLabel("Vos résultats de recherche s'afficheront ici");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(180, 180, 180));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(iconLabel);
        contentPanel.add(titleLabel);
        contentPanel.add(descLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void styleResultsTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(229, 242, 255));
        table.setSelectionForeground(new Color(0, 51, 102));
        table.setGridColor(new Color(240, 240, 240));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Style de l'en-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 102, 204)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        header.setReorderingAllowed(false);
        
        // Ajuster les largeurs des colonnes
        TableColumnModel columnModel = table.getColumnModel();
        
        // Largeurs des colonnes
        columnModel.getColumn(1).setPreferredWidth(250); // Trajet
        columnModel.getColumn(2).setPreferredWidth(180); // Date & Heure
        columnModel.getColumn(3).setPreferredWidth(150); // Véhicule
        columnModel.getColumn(4).setPreferredWidth(100); // Prix
        columnModel.getColumn(5).setPreferredWidth(150); // Disponibilité
        columnModel.getColumn(6).setPreferredWidth(120); // Actions
        
        // Renderer pour la disponibilité — texte pur sans caractères Unicode problématiques
        columnModel.getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String lower = value != null ? value.toString().toLowerCase() : "";

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                    setText(value != null ? value.toString() : "");
                } else if (lower.contains("disponible") && !lower.contains("peu")) {
                    setBackground(new Color(220, 255, 220));
                    setForeground(new Color(0, 120, 0));
                    setText("Disponible");
                } else if (lower.contains("peu")) {
                    setBackground(new Color(255, 255, 200));
                    setForeground(new Color(180, 120, 0));
                    setText("Peu de places");
                } else if (lower.contains("derni")) {
                    setBackground(new Color(255, 235, 200));
                    setForeground(new Color(200, 90, 0));
                    setText("Dernieres places");
                } else {
                    setBackground(new Color(255, 220, 220));
                    setForeground(new Color(180, 0, 0));
                    setText("Complet");
                }

                setFont(new Font("Dialog", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                setOpaque(true);
                return this;
            }
        });
        
        // Renderer pour les boutons d'action
        columnModel.getColumn(6).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            RoundedButton btn = createRoundedIconButton("\uf3ff", "Réserver", SUCCESS_COLOR);
            btn.setBackgroundColor(SUCCESS_COLOR);
            btn.setHoverColor(new Color(33, 136, 56));
            btn.setTextColor(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setBorder(new EmptyBorder(8, 20, 8, 20));
            
            final int voyageId = (int) voyagesModel.getValueAt(row, 0);
            btn.addActionListener(e -> {
                reserverVoyageSelectionne(voyageId);
            });
            
            return btn;
        });
        
        // Cell Editor pour la colonne Actions
        columnModel.getColumn(6).setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                RoundedButton button = createRoundedIconButton("\uf3ff", "Réserver", SUCCESS_COLOR);
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setBorder(new EmptyBorder(5, 15, 5, 15));
                
                final int voyageId = (int) voyagesModel.getValueAt(row, 0);
                button.addActionListener(e -> {
                    stopCellEditing();
                    reserverVoyageSelectionne(voyageId);
                });
                
                return button;
            }
        });
    }
    
    
    
    
    
    //Reservation PAGE
    
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf3ff", TAB_COLORS[2], 20f);
        title.setText("\uf3ff  Mes réservations");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Tableau des réservations
        String[] columns = {"N°", "Trajet", "Date départ", "Places", "Montant (DH)", "Statut", "Date réservation"};
        reservationsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsModel);
        reservationsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reservationsTable.setRowHeight(40);
        reservationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Renderer pour le statut
        reservationsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else if (status.contains("CONFIRM")) {
                    setBackground(new Color(220, 255, 220));
                    setForeground(new Color(0, 120, 0));
                } else if (status.contains("ATTENTE")) {
                    setBackground(new Color(255, 255, 200));
                    setForeground(new Color(180, 120, 0));
                } else if (status.contains("ANNUL")) {
                    setBackground(new Color(255, 220, 220));
                    setForeground(new Color(180, 0, 0));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.DARK_GRAY);
                }
                setFont(new Font("Dialog", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setOpaque(true);
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        
        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        
        RoundedButton detailsBtn = createRoundedIconButton("\uf06e", "Détails", INFO_COLOR);
        detailsBtn.addActionListener(e -> voirDetailsReservation());
        
        RoundedButton annulerBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        annulerBtn.addActionListener(e -> annulerReservation());
        
        RoundedButton refreshBtn = createRoundedIconButton("\uf021", "Actualiser", GRAY_COLOR);
        refreshBtn.addActionListener(e -> refreshReservationsTable());
        
        RoundedButton recuBtn = createRoundedIconButton("\uf019", "Télécharger reçu", new Color(111, 66, 193));
        recuBtn.addActionListener(e -> telechargerRecuReservation());
        
        actionPanel.add(detailsBtn);
        actionPanel.add(annulerBtn);
        actionPanel.add(recuBtn);
        actionPanel.add(refreshBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    
    
    
    
   // Profil Page 
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JLabel title = createIconLabel("\uf007", TAB_COLORS[4], 20f);
        title.setText("\uf007  Mon profil");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Formulaire
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        JTextField nomField = new JTextField(currentUser.getNom() != null ? currentUser.getNom() : "");
        JTextField prenomField = new JTextField(currentUser.getPrenom() != null ? currentUser.getPrenom() : "");
        JTextField emailField = new JTextField(currentUser.getEmail());
        JTextField telField = new JTextField(currentUser.getTelephone() != null ? currentUser.getTelephone() : "");
        JTextField cinField = new JTextField(currentUser.getCin() != null ? currentUser.getCin() : "");
        JPasswordField passwordField = new JPasswordField();
        
        formPanel.add(new JLabel("Nom:"));
        formPanel.add(nomField);
        formPanel.add(new JLabel("Prénom:"));
        formPanel.add(prenomField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Téléphone:"));
        formPanel.add(telField);
        formPanel.add(new JLabel("CIN:"));
        formPanel.add(cinField);
        formPanel.add(new JLabel("Nouveau mot de passe:"));
        formPanel.add(passwordField);
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Sauvegarder", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> saveProfile(nomField, prenomField, emailField, telField, cinField, passwordField));
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf0e2", "Réinitialiser", GRAY_COLOR);
        cancelBtn.addActionListener(e -> {
            nomField.setText(currentUser.getNom() != null ? currentUser.getNom() : "");
            prenomField.setText(currentUser.getPrenom() != null ? currentUser.getPrenom() : "");
            emailField.setText(currentUser.getEmail());
            telField.setText(currentUser.getTelephone() != null ? currentUser.getTelephone() : "");
            cinField.setText(currentUser.getCin() != null ? currentUser.getCin() : "");
            passwordField.setText("");
        });
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Création du panneau des offres spéciales
    private JPanel createOffresPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf02b", TAB_COLORS[3], 20f);
        title.setText("\uf02b  Offres Spéciales");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Panel pour les offres (grid layout)
        JPanel offresGridPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        offresGridPanel.setBackground(Color.WHITE);
        offresGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Charger les offres dans la grille
        loadOffresIntoGrid(offresGridPanel);
        
        JScrollPane scrollPane = new JScrollPane(offresGridPanel);
        scrollPane.setBorder(null);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void loadOffresIntoGrid(JPanel offresGridPanel) {
        offresGridPanel.removeAll();
        
        SwingWorker<List<OffreSpecial>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OffreSpecial> doInBackground() throws Exception {
                return offreService.getActiveOffres();
            }
            
            @Override
            protected void done() {
                try {
                    List<OffreSpecial> offres = get();
                    
                    if (offres.isEmpty()) {
                        JPanel emptyPanel = new JPanel(new BorderLayout());
                        emptyPanel.setBackground(Color.WHITE);
                        emptyPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
                        
                        JLabel emptyLabel = new JLabel("<html><div style='text-align: center;'>"
                            + "<h3 style='color: #666;'>Aucune offre disponible pour le moment</h3>"
                            + "<p>Revenez bientôt pour profiter de nos offres spéciales !</p>"
                            + "</div></html>");
                        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        
                        emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                        offresGridPanel.add(emptyPanel);
                    } else {
                        for (OffreSpecial offre : offres) {
                            offresGridPanel.add(createOffreCard(offre));
                        }
                    }
                    
                    offresGridPanel.revalidate();
                    offresGridPanel.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // Création d'une carte d'offre spéciale
    private JPanel createOffreCard(OffreSpecial offre) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 193, 7), 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Couleur en fonction du type de réduction
        Color headerColor;
        if (offre.getReductionPourcentage() > 0) {
            headerColor = ERROR_COLOR; // Rouge pour pourcentage
        } else {
            headerColor = SUCCESS_COLOR; // Vert pour réduction fixe
        }
        
        // En-tête de la carte
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titreLabel = new JLabel(offre.getTitre());
        titreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titreLabel.setForeground(Color.WHITE);
        
        JLabel codeLabel = new JLabel(offre.getCodePromo());
        codeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        codeLabel.setForeground(Color.YELLOW);
        codeLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        headerPanel.add(titreLabel, BorderLayout.WEST);
        headerPanel.add(codeLabel, BorderLayout.EAST);
        
        // Corps de la carte
        JPanel bodyPanel = new JPanel(new BorderLayout(10, 10));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
        
        // Description
        JTextArea descArea = new JTextArea(offre.getDescription());
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setBackground(Color.WHITE);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        
        // Détails
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        detailsPanel.setBackground(Color.WHITE);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        // Type de réduction
        JLabel reductionLabel = new JLabel(" " + offre.getReductionText());
        reductionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reductionLabel.setForeground(headerColor);
        
        // Dates
        JLabel datesLabel = new JLabel(" Du " + dateFormat.format(offre.getDateDebut()) + 
                                      " au " + dateFormat.format(offre.getDateFin()));
        datesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        datesLabel.setForeground(Color.DARK_GRAY);
        
        // Conditions
        if (offre.getConditions() != null && !offre.getConditions().trim().isEmpty()) {
            JLabel conditionsLabel = new JLabel(" " + offre.getConditions());
            conditionsLabel.setFont(new Font("Arial", Font.ITALIC, 11));
            conditionsLabel.setForeground(Color.GRAY);
            detailsPanel.add(conditionsLabel);
        }
        
        // Statut
        String statutText = offre.isActive() ? "✅ ACTIVE" : "⏰ EXPIRE BIENTÔT";
        JLabel statutLabel = new JLabel(statutText);
        statutLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statutLabel.setForeground(offre.isActive() ? SUCCESS_COLOR : WARNING_COLOR);
        
        detailsPanel.add(reductionLabel);
        detailsPanel.add(datesLabel);
        detailsPanel.add(statutLabel);
        
        bodyPanel.add(descArea, BorderLayout.CENTER);
        bodyPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        // Bouton "Utiliser cette offre"
        RoundedButton utiliserBtn = createRoundedIconButton("\uf0a4", "Utiliser cette offre", headerColor);
        utiliserBtn.setFont(new Font("Arial", Font.BOLD, 12));
        utiliserBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(ClientFrame.this,
                " Offre copiée !\n\n" +
                "Code: " + offre.getCodePromo() + "\n" +
                "Utilisez ce code lors de votre prochaine réservation pour bénéficier de l'offre.\n\n" +
                " Rendez-vous dans l'onglet 'Rechercher' pour réserver votre voyage !",
                "Code promo copié",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Copier le code dans le presse-papier
            StringSelection stringSelection = new StringSelection(offre.getCodePromo());
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(bodyPanel, BorderLayout.CENTER);
        card.add(utiliserBtn, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        String nomComplet = (currentUser.getPrenom() != null ? currentUser.getPrenom() : "") + " " +
                          (currentUser.getNom() != null ? currentUser.getNom() : "");
        
        JLabel statusLabel = createIconLabel("\uf058", SUCCESS_COLOR, 12f);
        statusLabel.setText("\uf058  Connecté en tant que " + 
            (nomComplet.trim().isEmpty() ? "Client" : nomComplet.trim()));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel helpLabel = createIconLabel("\uf059", INFO_COLOR, 12f);
        helpLabel.setText("\uf059  Besoin d'aide ? Contactez le support: 0522-123456 ");
        helpLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(helpLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    // Méthodes de gestion des données
    private void refreshAllData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                refreshVoyages();
                refreshReservations();
                refreshOffres();
                return null;
            }
            
            @Override
            protected void done() {
                refreshVoyagesTable();
                refreshReservationsTable();
                updatePromoPanel();
                JOptionPane.showMessageDialog(ClientFrame.this,
                    " Données actualisées avec succès !",
                    "Actualisation",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }
    
    private void refreshVoyages() {
        // Chargement en arrière-plan
    }
    
    private void refreshReservations() {
        // Chargement en arrière-plan
    }
    
    private void refreshOffres() {
        // Chargement en arrière-plan
    }
    
    private void refreshVoyagesTable() {
        final String filtreDepart  = (searchDepartCombo  != null) ? (String) searchDepartCombo.getSelectedItem()  : null;
        final String filtreArrivee = (searchArriveeCombo != null) ? (String) searchArriveeCombo.getSelectedItem() : null;
        final boolean hasFilter =
            filtreDepart  != null && !filtreDepart.equals("Sélectionnez votre ville de départ") &&
            filtreArrivee != null
            && !filtreArrivee.equals("Sélectionnez votre ville d'arrivée")
            && !filtreArrivee.equals("-- Choisissez d'abord la ville de départ --")
            && !filtreArrivee.equals("Aucune destination disponible");

        SwingWorker<List<Voyage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Voyage> doInBackground() throws Exception {
                List<Voyage> tous = voyageService.getAllVoyages();
                if (!hasFilter) return tous;
                List<Voyage> filtres = new ArrayList<>();
                for (Voyage v : tous) {
                    if (v.getVilleDepart()  != null && v.getVilleDepart().equalsIgnoreCase(filtreDepart) &&
                        v.getVilleArrivee() != null && v.getVilleArrivee().equalsIgnoreCase(filtreArrivee)) {
                        filtres.add(v);
                    }
                }
                return filtres;
            }
            @Override
            protected void done() {
                try {
                    List<Voyage> voyages = get();
                    voyagesModel.setRowCount(0);
                    for (Voyage voyage : voyages) {
                        int places = voyage.getPlacesDisponibles();
                        String dispo;
                        if (places >= 10)     dispo = "Disponible";
                        else if (places >= 3) dispo = "Peu de places";
                        else if (places > 0)  dispo = "Dernieres places";
                        else                  dispo = "Complet";
                        voyagesModel.addRow(new Object[]{
                            voyage.getId(),
                            voyage.getVilleDepart() + " \u2192 " + voyage.getVilleArrivee(),
                            datetimeFormat.format(voyage.getDateDepart()),
                            voyage.getTypeVehicule() != null ? voyage.getTypeVehicule() : "Non spécifié",
                            String.format("%.2f DH", voyage.getPrix()),
                            dispo,
                            "Réserver"
                        });
                    }
                    voyagesTable.revalidate();
                    voyagesTable.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    private void refreshReservationsTable() {
        SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return reservationService.getClientReservations(currentUser.getId());
            }
            
            @Override
            protected void done() {
                try {
                    List<Reservation> reservations = get();
                    reservationsModel.setRowCount(0);
                    
                    for (Reservation res : reservations) {
                        Object[] row = {
                            res.getId(),
                            res.getTrajet(),
                            datetimeFormat.format(res.getDateDepart()),
                            res.getNombrePlaces(),
                            String.format("%.2f", res.getMontantTotal()),
                            res.getStatutIcon(),
                            datetimeFormat.format(res.getDateReservation())
                        };
                        reservationsModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        " Erreur lors du chargement des réservations",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updatePromoPanel() {
        SwingWorker<List<OffreSpecial>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OffreSpecial> doInBackground() throws Exception {
                return offreService.getActiveOffres();
            }
            
            @Override
            protected void done() {
                try {
                    List<OffreSpecial> offres = get();
                    
                    // Trouver le promoPanel dans l'accueil
                    JPanel homePanel = (JPanel) tabbedPane.getComponentAt(0);
                    JPanel promoPanel = null;
                    
                    // Chercher le panel nommé "promoPanel"
                    for (Component comp : homePanel.getComponents()) {
                        if (comp instanceof JPanel && "promoPanel".equals(comp.getName())) {
                            promoPanel = (JPanel) comp;
                            break;
                        }
                    }
                    
                    if (promoPanel != null) {
                        promoPanel.removeAll();
                        promoPanel.setLayout(new BorderLayout(10, 10));
                        
                        if (offres.isEmpty()) {
                            JLabel noOffreLabel = new JLabel("<html><div style='text-align: center; color: #666;'>"
                                + " <b>Offres spéciales</b><br>"
                                + "Aucune offre disponible pour le moment"
                                + "</div></html>");
                            noOffreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                            promoPanel.add(noOffreLabel, BorderLayout.CENTER);
                        } else {
                            // Afficher la première offre en vedette
                            OffreSpecial featuredOffre = offres.get(0);
                            
                            JLabel offreLabel = new JLabel("<html><div style='padding: 10px;'>"
                                + " <b>OFFRE DU MOMENT</b><br>"
                                + "<span style='font-size: 16px;'><b>" + featuredOffre.getTitre() + "</b></span><br>"
                                + featuredOffre.getDescription() + "<br>"
                                + "<span style='color: #dc3545; font-weight: bold; font-size: 18px;'>"
                                + " CODE: " + featuredOffre.getCodePromo() + "</span>"
                                + "</div></html>");
                            offreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                            promoPanel.add(offreLabel, BorderLayout.CENTER);
                            
                            // Indiquer s'il y a plus d'offres
                            if (offres.size() > 1) {
                                JLabel moreOffresLabel = new JLabel("<html><div style='text-align: right; color: #0066cc;'>"
                                    + " +" + (offres.size() - 1) + " autres offres disponibles"
                                    + "</div></html>");
                                moreOffresLabel.setFont(new Font("Arial", Font.ITALIC, 11));
                                promoPanel.add(moreOffresLabel, BorderLayout.EAST);
                            }
                        }
                        
                        RoundedButton voirOffresBtn = createRoundedIconButton("\uf06e", "Voir toutes les offres", new Color(255, 193, 7));
                        voirOffresBtn.setFont(new Font("Arial", Font.BOLD, 12));
                        voirOffresBtn.addActionListener(e -> showAllOffres());
                        promoPanel.add(voirOffresBtn, BorderLayout.SOUTH);
                        
                        promoPanel.revalidate();
                        promoPanel.repaint();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    // Méthodes d'actions
    private void searchVoyages(String depart, String arrivee, Date date, int passagers) {
        SwingWorker<List<Voyage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Voyage> doInBackground() throws Exception {
                return voyageService.rechercherVoyagesDisponibles(depart, arrivee, date, passagers);
            }
            
            @Override
            protected void done() {
                try {
                    List<Voyage> voyages = get();
                    voyagesModel.setRowCount(0);
                    
                    for (Voyage voyage : voyages) {
                        String trajet = voyage.getVilleDepart() + " → " + voyage.getVilleArrivee();
                        String dateHeure = datetimeFormat.format(voyage.getDateDepart());
                        String vehicule = voyage.getTypeVehicule() != null ? voyage.getTypeVehicule() : "Non spécifié";
                        int places = voyage.getPlacesDisponibles();
                        String disponibilite;
                        if (places >= 10) disponibilite = "Disponible";
                        else if (places >= 3) disponibilite = "Peu de places";
                        else disponibilite = "Dernieres places";

                        voyagesModel.addRow(new Object[]{
                            voyage.getId(),
                            trajet,
                            dateHeure,
                            vehicule,
                            String.format("%.2f DH", voyage.getPrix()),
                            disponibilite,
                            "Réserver"
                        });
                    }
                    
                    if (voyages.isEmpty()) {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            "Aucun voyage trouvé pour ces critères",
                            "Aucun résultat",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        "Erreur lors de la recherche",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void reserverVoyageSelectionne() {
        int selectedRow = voyagesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                " Veuillez sélectionner un voyage à réserver",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int voyageId = (int) voyagesModel.getValueAt(selectedRow, 0);
        double prix = Double.parseDouble(voyagesModel.getValueAt(selectedRow, 5).toString());
        
        // Demander le nombre de places
        String placesStr = JOptionPane.showInputDialog(this,
            " Nombre de places à réserver (Prix unitaire: " + prix + " DH):",
            "Réservation",
            JOptionPane.QUESTION_MESSAGE);
        
        if (placesStr == null || placesStr.trim().isEmpty()) {
            return;
        }
        
        try {
            int places = Integer.parseInt(placesStr);
            if (places <= 0) {
                throw new NumberFormatException();
            }
            
            // Créer la réservation
            Reservation reservation = new Reservation();
            reservation.setClientId(currentUser.getId());
            reservation.setVoyageId(voyageId);
            reservation.setNombrePlaces(places);
            reservation.setMontantTotal(prix * places);
            reservation.setStatut("EN_ATTENTE");
            reservation.setSiegeNumbers("À attribuer");
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return reservationService.createReservation(reservation);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ClientFrame.this,
                                " Réservation créée avec succès !\n" +
                                "Numéro: #" + reservation.getId() + "\n" +
                                "Montant: " + reservation.getMontantTotal() + " DH\n" +
                                "Statut: En attente de paiement",
                                "Réservation réussie",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Rafraîchir les données
                            refreshVoyagesTable();
                            refreshReservationsTable();
                        } else {
                            JOptionPane.showMessageDialog(ClientFrame.this,
                                " Erreur lors de la création\n" +
                                "Vérifiez la disponibilité des places",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            " Erreur lors de la création",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                " Veuillez entrer un nombre valide de places",
                "Nombre invalide",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void reserverVoyageSelectionne(int voyageId) {
        System.out.println("=== DÉBUT RÉSERVATION ===");
        System.out.println("ID Voyage à réserver: " + voyageId);
        
        SwingWorker<Voyage, Void> getVoyageWorker = new SwingWorker<>() {
            @Override
            protected Voyage doInBackground() throws Exception {
                // Utiliser le service pour récupérer le voyage
                return getVoyageByIdFromService(voyageId);
            }
            
            @Override
            protected void done() {
                try {
                    Voyage voyage = get();
                    if (voyage == null) {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            "<html><div style='text-align: center;'>" +
                            "<h3 style='color: #dc3545;'>Voyage introuvable</h3>" +
                            "<p>Le voyage sélectionné n'existe plus ou a été supprimé.</p>" +
                            "</div></html>",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Afficher la boîte de dialogue de réservation
                    showReservationDialog(voyage);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        "Erreur lors de la récupération du voyage: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        getVoyageWorker.execute();
    }
    
    private void processReservation(Voyage voyage, int nombrePlaces) {
        if (voyage == null) {
            JOptionPane.showMessageDialog(this,
                "Informations du voyage manquantes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Vérifier la disponibilité
        if (voyage.getPlacesDisponibles() < nombrePlaces) {
            JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center;'>" +
                "<h3 style='color: #ffc107;'>Places insuffisantes</h3>" +
                "<p>Il ne reste que " + voyage.getPlacesDisponibles() + " place(s) disponible(s).</p>" +
                "</div></html>",
                "Disponibilité insuffisante",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Calculer le montant total
        double montantTotal = voyage.getPrix() * nombrePlaces;
        
        // Confirmation
        int confirmation = JOptionPane.showConfirmDialog(this,
            "<html><div style='padding: 10px;'>" +
            "<h3>" + " Confirmer la réservation</h3>" +
            "<table style='width: 100%; margin: 10px 0;'>" +
            "<tr><td><b>Trajet:</b></td><td>" + voyage.getVilleDepart() + " → " + voyage.getVilleArrivee() + "</td></tr>" +
            "<tr><td><b>Date:</b></td><td>" + datetimeFormat.format(voyage.getDateDepart()) + "</td></tr>" +
            "<tr><td><b>Véhicule:</b></td><td>" + voyage.getTypeVehicule() + "</td></tr>" +
            "<tr><td><b>Nombre de places:</b></td><td>" + nombrePlaces + "</td></tr>" +
            "<tr><td><b>Prix unitaire:</b></td><td>" + String.format("%.2f", voyage.getPrix()) + " DH</td></tr>" +
            "<tr><td><b>Montant total:</b></td><td><span style='color: #28a745; font-weight: bold;'>" + 
            String.format("%.2f", montantTotal) + " DH</span></td></tr>" +
            "</table>" +
            "</div></html>",
            "Confirmation de réservation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmation == JOptionPane.YES_OPTION) {
            // Créer la réservation
            createReservationInDatabase(voyage.getId(), nombrePlaces, montantTotal);
        }
    }

    private void createReservationInDatabase(int voyageId, int nombrePlaces, double montantTotal) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Créer l'objet réservation
                    Reservation reservation = new Reservation();
                    reservation.setClientId(currentUser.getId());
                    reservation.setVoyageId(voyageId);
                    reservation.setNombrePlaces(nombrePlaces);
                    reservation.setMontantTotal(montantTotal);
                    reservation.setStatut("EN_ATTENTE");
                    reservation.setSiegeNumbers("À attribuer");
                    reservation.setDateReservation(new Date());
                    
                    // Sauvegarder
                    return reservationService.createReservation(reservation);
                    
                } catch (Exception e) {
                    System.err.println("Erreur création réservation: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        // Succès
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            "<html><div style='text-align: center; padding: 20px;'>" +
                            "<h2 style='color: #28a745;'>Réservation confirmée !</h2>" +
                            "<p>Votre réservation a été enregistrée avec succès.</p>" +
                            "<p><b>Montant:</b> " + String.format("%.2f", montantTotal) + " DH</b></p>" +
                            "<p style='font-size: 12px; color: #666;'>" +
                            "Vous pouvez consulter votre réservation dans l'onglet 'Mes réservations'</p>" +
                            "</div></html>",
                            "Réservation réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir les données
                        refreshVoyagesTable();
                        refreshReservationsTable();
                        
                    } else {
                        // Échec
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            "<html><div style='text-align: center;'>" +
                            "<h3 style='color: #dc3545;'>Échec de la réservation</h3>" +
                            "<p>Impossible de créer la réservation. Veuillez réessayer.</p>" +
                            "</div></html>",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        "Erreur: " + e.getMessage(),
                        "Erreur système",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }

    // Méthode pour récupérer un voyage par ID via le service
    private Voyage getVoyageByIdFromService(int voyageId) {
        try {
            // Si vous n'avez pas de méthode getVoyageById dans le service,
            // vous pouvez récupérer tous les voyages et filtrer
            List<Voyage> allVoyages = voyageService.getAllVoyages();
            for (Voyage voyage : allVoyages) {
                if (voyage.getId() == voyageId) {
                    System.out.println("Voyage trouvé: " + voyage.getVilleDepart() + 
                                     " -> " + voyage.getVilleArrivee());
                    return voyage;
                }
            }
            
            // Si non trouvé, essayer une autre approche
            System.out.println("Voyage non trouvé dans la liste, vérification directe...");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du voyage: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    // Méthode utilitaire pour récupérer un voyage par ID
    private Voyage getVoyageById(int voyageId) {
        // Solution temporaire : Récupérer depuis la liste des voyages existants
        // Vous devez d'abord avoir chargé les voyages
        List<Voyage> allVoyages = voyageService.getAllVoyages();
        for (Voyage voyage : allVoyages) {
            if (voyage.getId() == voyageId) {
                return voyage;
            }
        }
        return null;
    }

    private void showReservationDialog(Voyage voyage) {
        JDialog dialog = new JDialog(this, "Réservation de voyage", true);
        dialog.setSize(520, 430);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        // ── En-tête coloré ──────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("  Réserver ce voyage");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);

        JLabel routeLabel = new JLabel(voyage.getVilleDepart() + "  →  " + voyage.getVilleArrivee() + "  ");
        routeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        routeLabel.setForeground(new Color(200, 230, 255));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(routeLabel, BorderLayout.EAST);

        // ── Détails du voyage dans une grille ────────────────────────────────────
        JPanel detailsCard = new JPanel(new GridBagLayout());
        detailsCard.setBackground(new Color(248, 249, 250));
        detailsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(6, 6, 6, 20);

        String[][] details = {
            {"Date de départ :", datetimeFormat.format(voyage.getDateDepart())},
            {"Véhicule :", voyage.getTypeVehicule() != null ? voyage.getTypeVehicule() : "Non spécifié"},
            {"Prix unitaire :", String.format("%.2f DH", voyage.getPrix())},
            {"Places disponibles :", voyage.getPlacesDisponibles() + " / " + voyage.getPlacesTotales()}
        };

        for (int i = 0; i < details.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(details[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(100, 100, 100));
            detailsCard.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            JLabel val = new JLabel(details[i][1]);
            val.setFont(new Font("Segoe UI", Font.BOLD, 13));
            val.setForeground(new Color(30, 30, 30));
            detailsCard.add(val, gc);
        }

        // ── Ligne : sélection nombre de places + total ───────────────────────────
        JPanel placesRow = new JPanel(new BorderLayout(15, 0));
        placesRow.setBackground(Color.WHITE);
        placesRow.setBorder(new EmptyBorder(15, 0, 5, 0));

        JLabel placesLabel = new JLabel("Nombre de places à réserver :");
        placesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        placesLabel.setForeground(new Color(51, 51, 51));

        int maxPlaces = Math.max(1, voyage.getPlacesDisponibles());
        JSpinner placesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, maxPlaces, 1));
        placesSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placesSpinner.setPreferredSize(new Dimension(80, 36));
        placesSpinner.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel totalLabel = new JLabel(String.format("Total : %.2f DH", voyage.getPrix()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(SUCCESS_COLOR);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        placesSpinner.addChangeListener(ev -> {
            int nb = (Integer) placesSpinner.getValue();
            totalLabel.setText(String.format("Total : %.2f DH", voyage.getPrix() * nb));
        });

        placesRow.add(placesLabel, BorderLayout.WEST);
        placesRow.add(placesSpinner, BorderLayout.CENTER);
        placesRow.add(totalLabel, BorderLayout.EAST);

        // ── Corps : détails + places ─────────────────────────────────────────────
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 10));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(20, 25, 10, 25));
        bodyPanel.add(detailsCard, BorderLayout.CENTER);
        bodyPanel.add(placesRow, BorderLayout.SOUTH);

        // ── Barre de boutons ────────────────────────────────────────────────────
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        buttonBar.setBackground(new Color(248, 249, 250));
        buttonBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", GRAY_COLOR);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBtn.setPreferredSize(new Dimension(130, 42));
        cancelBtn.addActionListener(e -> dialog.dispose());

        RoundedButton confirmBtn = createRoundedIconButton("\uf00c", "Confirmer la réservation", SUCCESS_COLOR);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmBtn.setPreferredSize(new Dimension(220, 42));
        confirmBtn.addActionListener(e -> {
            int places = (Integer) placesSpinner.getValue();
            createReservation(voyage, places, dialog);
        });

        buttonBar.add(cancelBtn);
        buttonBar.add(confirmBtn);

        // ── Assemblage final ────────────────────────────────────────────────────
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(bodyPanel, BorderLayout.CENTER);
        dialog.add(buttonBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(102, 102, 102));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JLabel createDetailValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(51, 51, 51));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        return label;
    }

    private void createReservation(Voyage voyage, int places, JDialog dialog) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Créer la réservation
                Reservation reservation = new Reservation();
                reservation.setClientId(currentUser.getId());
                reservation.setVoyageId(voyage.getId());
                reservation.setNombrePlaces(places);
                reservation.setMontantTotal(voyage.getPrix() * places);
                reservation.setStatut("EN_ATTENTE");
                reservation.setSiegeNumbers("À attribuer");
                
                return reservationService.createReservation(reservation);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    dialog.dispose();
                    
                    if (success) {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            "<html><div style='text-align: center; padding: 15px;'>" +
                            "<h3 style='color: #28a745;'>Réservation confirmée !</h3>" +
                            "<p style='font-size: 14px;'>Votre réservation a été enregistrée avec succès.</p>" +
                            "<p style='font-size: 12px; color: #666;'>Vous pouvez la consulter dans l'onglet 'Mes réservations'.</p>" +
                            "</div></html>",
                            "Réservation réussie",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Rafraîchir les données
                        refreshVoyagesTable();
                        refreshReservationsTable();
                    } else {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            " Erreur lors de la création de la réservation.\n" +
                            "Veuillez vérifier la disponibilité des places.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        " Une erreur s'est produite lors de la réservation",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void voirDetailsReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une réservation",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Reservation, Void> worker = new SwingWorker<>() {
            @Override
            protected Reservation doInBackground() throws Exception {
                List<Reservation> reservations = reservationService.getClientReservations(currentUser.getId());
                for (Reservation res : reservations) {
                    if (res.getId() == reservationId) {
                        return res;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    Reservation reservation = get();
                    if (reservation == null) return;
                    
                    // Créer un vrai dialog de détails
                    JDialog dialog = new JDialog(ClientFrame.this, "Détails de la réservation #" + reservation.getId(), true);
                    dialog.setSize(520, 420);
                    dialog.setLocationRelativeTo(ClientFrame.this);
                    dialog.setLayout(new BorderLayout());
                    
                    // En-tête coloré
                    JPanel headerDlg = new JPanel(new BorderLayout());
                    headerDlg.setBackground(INFO_COLOR);
                    headerDlg.setBorder(new EmptyBorder(15, 20, 15, 20));
                    JLabel dlgTitle = new JLabel("  Réservation #" + reservation.getId());
                    dlgTitle.setFont(new Font("Arial", Font.BOLD, 18));
                    dlgTitle.setForeground(Color.WHITE);
                    headerDlg.add(dlgTitle, BorderLayout.CENTER);
                    
                    // Contenu avec grille de champs
                    JPanel content = new JPanel(new GridBagLayout());
                    content.setBackground(Color.WHITE);
                    content.setBorder(new EmptyBorder(20, 30, 20, 30));
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.insets = new Insets(6, 6, 6, 12);
                    
                    String[][] fields = {
                        {"Trajet :", reservation.getTrajet()},
                        {"Date de départ :", datetimeFormat.format(reservation.getDateDepart())},
                        {"Nombre de places :", String.valueOf(reservation.getNombrePlaces())},
                        {"Numéros de sièges :", reservation.getSiegeNumbers()},
                        {"Montant total :", String.format("%.2f DH", reservation.getMontantTotal())},
                        {"Statut :", reservation.getStatutIcon()},
                        {"Date de réservation :", datetimeFormat.format(reservation.getDateReservation())}
                    };
                    
                    for (int i = 0; i < fields.length; i++) {
                        gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
                        JLabel lbl = new JLabel(fields[i][0]);
                        lbl.setFont(new Font("Arial", Font.BOLD, 13));
                        lbl.setForeground(GRAY_COLOR);
                        content.add(lbl, gbc);
                        
                        gbc.gridx = 1; gbc.weightx = 1;
                        JLabel val = new JLabel(fields[i][1]);
                        val.setFont(new Font("Arial", Font.PLAIN, 13));
                        // Colorier le statut
                        if (i == 5) {
                            String s = fields[i][1];
                            if (s.contains("CONFIRMÉ")) val.setForeground(SUCCESS_COLOR);
                            else if (s.contains("EN ATTENTE")) val.setForeground(WARNING_COLOR);
                            else val.setForeground(ERROR_COLOR);
                            val.setFont(new Font("Arial", Font.BOLD, 13));
                        }
                        content.add(val, gbc);
                        
                        // Ligne séparatrice légère
                        if (i < fields.length - 1) {
                            gbc.gridx = 0; gbc.gridwidth = 2; gbc.gridy = i;
                            gbc.insets = new Insets(0, 0, 0, 0);
                            JSeparator sep = new JSeparator();
                            sep.setForeground(LIGHT_GRAY);
                            // On n'ajoute pas le séparateur pour garder propre, l'insets suffit
                            gbc.gridwidth = 1;
                            gbc.insets = new Insets(6, 6, 6, 12);
                        }
                    }
                    
                    // Boutons
                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                    btnPanel.setBackground(new Color(248, 249, 250));
                    btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_GRAY));
                    
                    RoundedButton recuBtn = createRoundedIconButton("\uf019", "Télécharger reçu", new Color(111, 66, 193));
                    recuBtn.addActionListener(ev -> {
                        dialog.dispose();
                        telechargerRecuPourReservation(reservation);
                    });
                    
                    RoundedButton closeBtn = createRoundedIconButton("\uf00d", "Fermer", GRAY_COLOR);
                    closeBtn.addActionListener(ev -> dialog.dispose());
                    
                    btnPanel.add(recuBtn);
                    btnPanel.add(closeBtn);
                    
                    dialog.add(headerDlg, BorderLayout.NORTH);
                    dialog.add(new JScrollPane(content), BorderLayout.CENTER);
                    dialog.add(btnPanel, BorderLayout.SOUTH);
                    dialog.setVisible(true);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    // ─── Téléchargement du reçu de réservation ───────────────────────────────
    
    private void telechargerRecuReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une réservation pour télécharger le reçu",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Reservation, Void> worker = new SwingWorker<>() {
            @Override
            protected Reservation doInBackground() throws Exception {
                List<Reservation> reservations = reservationService.getClientReservations(currentUser.getId());
                for (Reservation res : reservations) {
                    if (res.getId() == reservationId) return res;
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    Reservation reservation = get();
                    if (reservation != null) {
                        telechargerRecuPourReservation(reservation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void telechargerRecuPourReservation(Reservation reservation) {
        // Choisir l'emplacement de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le reçu");
        fileChooser.setSelectedFile(new File("Recu_Reservation_" + reservation.getId() + ".pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier PDF (*.pdf)", "pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;
        
        File fichier = fileChooser.getSelectedFile();
        if (!fichier.getName().toLowerCase().endsWith(".pdf")) {
            fichier = new File(fichier.getAbsolutePath() + ".pdf");
        }
        
        final File fichierFinal = fichier;
        
        SwingWorker<Boolean, Void> saveWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                genererFichierRecu(reservation, fichierFinal);
                return true;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                    int open = JOptionPane.showConfirmDialog(ClientFrame.this,
                        "Reçu enregistré avec succès !\n" + fichierFinal.getAbsolutePath() +
                        "\n\nVoulez-vous l'ouvrir maintenant ?",
                        "Reçu téléchargé",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                    if (open == JOptionPane.YES_OPTION) {
                        try {
                            Desktop.getDesktop().open(fichierFinal);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ClientFrame.this,
                                "Impossible d'ouvrir le fichier automatiquement.\nVeuillez l'ouvrir manuellement.",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        "Erreur lors de l'enregistrement du reçu : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        saveWorker.execute();
    }
    
    private void genererFichierRecu(Reservation reservation, File fichier) throws IOException {
        String nomComplet = ((currentUser.getPrenom() != null ? currentUser.getPrenom() : "") + " " +
                            (currentUser.getNom() != null ? currentUser.getNom() : "")).trim();
        if (nomComplet.isEmpty()) nomComplet = "Client";

        String dateEmission = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        String numeroRecu = "REC-" + String.format("%06d", reservation.getId());

        // ---------------------------------------------------------------
        // 1. BufferedImage A4 haute résolution (3× = 2382 × 3369 px)
        // ---------------------------------------------------------------
        final int SCALE = 3;
        final int imgW  = 794 * SCALE;   // ~2382 px
        final int imgH  = 1123 * SCALE;  // ~3369 px

        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,       RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,  RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,          RenderingHints.VALUE_RENDER_QUALITY);

        // -- Constantes de mise en page (tout en px @ SCALE) --
        final int M    = 72 * SCALE;   // marge gauche/droite
        final int CW   = imgW - M * 2; // largeur du contenu
        final int COL  = 380 * SCALE;  // position X de la colonne "valeur"

        // Couleurs
        final Color BLUE       = new Color(0,   102, 204);
        final Color BLUE_LIGHT = new Color(0,    82, 180);
        final Color GRAY_BG    = new Color(245, 245, 245);
        final Color GRAY_LINE  = new Color(220, 220, 220);
        final Color GREEN_BOX  = new Color(40,  167,  69);
        final Color TEXT_DARK  = new Color(33,   37,  41);
        final Color TEXT_GRAY  = new Color(108, 117, 125);

        // ---------------------------------------------------------------
        // FOND BLANC
        // ---------------------------------------------------------------
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgW, imgH);

        // ---------------------------------------------------------------
        // EN-TÊTE BLEU
        // ---------------------------------------------------------------
        final int HEADER_H = 185 * SCALE;
        g.setColor(BLUE);
        g.fillRect(0, 0, imgW, HEADER_H);

        // Titre gauche : "TRANSPORT PLUS" + sous-titre
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 62 * SCALE / 3));
        g.drawString("TRANSPORT PLUS", M, 88 * SCALE / 3);

        g.setFont(new Font("Arial", Font.PLAIN, 26 * SCALE / 3));
        g.drawString("Recu de Reservation", M, 120 * SCALE / 3);

        // Bloc droit : numéro de reçu + date
        g.setFont(new Font("Arial", Font.BOLD, 28 * SCALE / 3));
        FontMetrics fmH = g.getFontMetrics();
        String recuLabel = numeroRecu;
        int rx = imgW - M - fmH.stringWidth(recuLabel);
        g.drawString(recuLabel, rx, 75 * SCALE / 3);

        g.setFont(new Font("Arial", Font.PLAIN, 20 * SCALE / 3));
        FontMetrics fmD = g.getFontMetrics();
        String dateLabel = "Emis le : " + dateEmission;
        int dx = imgW - M - fmD.stringWidth(dateLabel);
        g.drawString(dateLabel, dx, 105 * SCALE / 3);

        // Liseré cyan sous l'en-tête
        g.setColor(new Color(0, 180, 255));
        g.fillRect(0, HEADER_H - 6, imgW, 6);

        // ---------------------------------------------------------------
        // Curseur Y courant
        // ---------------------------------------------------------------
        int y = HEADER_H + 55;

        // Taille de police standard pour les lignes de données
        final int FONT_LABEL = 20 * SCALE / 3;
        final int FONT_TITLE = 23 * SCALE / 3;
        final int LINE_H     = 62;  // hauteur d'une ligne de données

        // ---------------------------------------------------------------
        // SECTION : INFORMATIONS CLIENT
        // ---------------------------------------------------------------
        // Fond gris de la section
        int secClientY = y;
        // On calcule d'abord la hauteur nécessaire (4 lignes fixes)
        int clientLines = 4; // Nom, Email, Telephone, CIN
        int secClientH = 52 + clientLines * LINE_H + 20;
        g.setColor(GRAY_BG);
        g.fillRect(M, secClientY, CW, secClientH);

        // Titre de section en bleu gras
        g.setColor(BLUE);
        g.setFont(new Font("Arial", Font.BOLD, FONT_TITLE));
        g.drawString("INFORMATIONS CLIENT", M + 20, y + 40); y += 58;

        // Lignes : label (gras) | valeur
        g.setFont(new Font("Arial", Font.BOLD, FONT_LABEL));
        g.setColor(TEXT_DARK);

        // Nom complet
        g.drawString("Nom complet :", M + 20, y + 30);
        g.setFont(new Font("Arial", Font.PLAIN, FONT_LABEL));
        g.drawString(nomComplet, COL, y + 30); y += LINE_H;

        g.setFont(new Font("Arial", Font.BOLD, FONT_LABEL));
        g.drawString("Email :", M + 20, y + 30);
        g.setFont(new Font("Arial", Font.PLAIN, FONT_LABEL));
        g.drawString(currentUser.getEmail(), COL, y + 30); y += LINE_H;

        g.setFont(new Font("Arial", Font.BOLD, FONT_LABEL));
        g.drawString("Telephone :", M + 20, y + 30);
        g.setFont(new Font("Arial", Font.PLAIN, FONT_LABEL));
        String tel = (currentUser.getTelephone() != null && !currentUser.getTelephone().isEmpty())
                     ? currentUser.getTelephone() : "-";
        g.drawString(tel, COL, y + 30); y += LINE_H;

        g.setFont(new Font("Arial", Font.BOLD, FONT_LABEL));
        g.drawString("CIN :", M + 20, y + 30);
        g.setFont(new Font("Arial", Font.PLAIN, FONT_LABEL));
        String cin = (currentUser.getCin() != null && !currentUser.getCin().isEmpty())
                     ? currentUser.getCin() : "-";
        g.drawString(cin, COL, y + 30); y += LINE_H;

        y += 50; // espace entre sections

        // ---------------------------------------------------------------
        // SECTION : DETAILS DE LA RESERVATION
        // ---------------------------------------------------------------
        int secResaY = y;
        int resaLines = 7; // Reservation N, Trajet, Date départ, Places, Sièges, Statut, Date résa
        int secResaH = 52 + resaLines * LINE_H + 20;
        g.setColor(GRAY_BG);
        g.fillRect(M, secResaY, CW, secResaH);

        g.setColor(BLUE);
        g.setFont(new Font("Arial", Font.BOLD, FONT_TITLE));
        g.drawString("DETAILS DE LA RESERVATION", M + 20, y + 40); y += 58;

        // Liseré bleu sous le titre de section
        g.setColor(BLUE);
        g.fillRect(M, y - 10, CW, 3); y += 5;

        // Lignes de données
        String[][] resaRows = {
            {"Reservation N :", "#" + reservation.getId()},
            {"Trajet :",        reservation.getTrajet()},
            {"Date de depart :", datetimeFormat.format(reservation.getDateDepart())},
            {"Nombre de places :", String.valueOf(reservation.getNombrePlaces())},
            {"Numeros sieges :", reservation.getSiegeNumbers()},
            {"Statut :",        reservation.getStatutIcon()},
            {"Date reservation :", datetimeFormat.format(reservation.getDateReservation())}
        };

        for (String[] row : resaRows) {
            g.setColor(TEXT_DARK);
            g.setFont(new Font("Arial", Font.BOLD, FONT_LABEL));
            g.drawString(row[0], M + 20, y + 30);
            g.setFont(new Font("Arial", Font.PLAIN, FONT_LABEL));
            g.drawString(row[1], COL, y + 30);
            // Ligne séparatrice fine
            g.setColor(GRAY_LINE);
            g.fillRect(M + 20, y + LINE_H - 4, CW - 40, 2);
            y += LINE_H;
        }
        y += 50;

        // ---------------------------------------------------------------
        // ENCADRÉ VERT : MONTANT TOTAL
        // ---------------------------------------------------------------
        final int BOX_H = 130;
        g.setColor(GREEN_BOX);
        g.setStroke(new BasicStroke(4));
        g.drawRoundRect(M, y, CW, BOX_H, 18, 18);

        // Label gauche "MONTANT TOTAL :" en vert foncé
        g.setColor(new Color(30, 130, 50));
        g.setFont(new Font("Arial", Font.BOLD, 22 * SCALE / 3));
        g.drawString("MONTANT TOTAL :", M + 30, y + BOX_H / 2 + 14);

        // Montant en grand à droite
        g.setColor(new Color(30, 130, 50));
        g.setFont(new Font("Arial", Font.BOLD, 38 * SCALE / 3));
        String montantStr = String.format("%.2f DH", reservation.getMontantTotal());
        FontMetrics fmM = g.getFontMetrics();
        int mxRight = M + CW - 30 - fmM.stringWidth(montantStr);
        g.drawString(montantStr, mxRight, y + BOX_H / 2 + 16);

        g.setStroke(new BasicStroke(1));
        y += BOX_H + 40;

        // ---------------------------------------------------------------
        // PIED DE PAGE
        // ---------------------------------------------------------------
        // Ligne séparatrice grise en bas
        g.setColor(GRAY_LINE);
        g.fillRect(M, imgH - 160, CW, 2);

        g.setColor(TEXT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 17 * SCALE / 3));
        FontMetrics fmF = g.getFontMetrics();
        String f1 = "Merci de votre confiance ! Transport Plus vous souhaite un bon voyage.";
        String f2 = "Support : support@transportplus.ma  |  Tel : +212 5 XX XX XX XX";
        g.drawString(f1, M, imgH - 110);
        g.drawString(f2, M, imgH - 65);

        g.dispose();

        // --- 2. Encoder l'image en JPEG dans un ByteArrayOutputStream ---
        ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
        ImageIO.write(img, "JPEG", jpegOut);
        byte[] jpegBytes = jpegOut.toByteArray();

        // --- 3. Construire le PDF 1.4 manuellement ---
        // Dimensions PDF en points (1 pt = 1/72 pouce, A4 = 595 x 842 pts)
        int pdfW = 595;
        int pdfH = 842;

        ByteArrayOutputStream pdfBuf = new ByteArrayOutputStream();

        // On garde les offsets de chaque objet pour la table xref
        int[] offsets = new int[6]; // objets 1..5
        int objCount = 0;

        // Helper : écrire une chaîne ASCII dans pdfBuf
        java.util.function.Consumer<String> write = s -> {
            try { pdfBuf.write(s.getBytes("ISO-8859-1")); } catch (IOException ex) { throw new RuntimeException(ex); }
        };

        // Header PDF
        write.accept("%PDF-1.4\n");
        write.accept("%\u00e2\u00e3\u00cf\u00d3\n"); // commentaire binaire (marqueur)

        // Objet 1 : Catalogue
        offsets[1] = pdfBuf.size();
        write.accept("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        // Objet 2 : Pages
        offsets[2] = pdfBuf.size();
        write.accept("2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");

        // Objet 3 : Page
        offsets[3] = pdfBuf.size();
        write.accept("3 0 obj\n");
        write.accept("<< /Type /Page /Parent 2 0 R\n");
        write.accept("   /MediaBox [0 0 " + pdfW + " " + pdfH + "]\n");
        write.accept("   /Contents 4 0 R\n");
        write.accept("   /Resources << /XObject << /Im1 5 0 R >> >>\n");
        write.accept(">>\nendobj\n");

        // Objet 4 : Flux de contenu (place l'image sur toute la page)
        String contentStream = "q\n" + pdfW + " 0 0 " + pdfH + " 0 0 cm\n/Im1 Do\nQ\n";
        byte[] csBytes = contentStream.getBytes("ISO-8859-1");
        offsets[4] = pdfBuf.size();
        write.accept("4 0 obj\n");
        write.accept("<< /Length " + csBytes.length + " >>\n");
        write.accept("stream\n");
        pdfBuf.write(csBytes);
        write.accept("\nendstream\nendobj\n");

        // Objet 5 : XObject Image JPEG (filtre DCTDecode)
        offsets[5] = pdfBuf.size();
        write.accept("5 0 obj\n");
        write.accept("<< /Type /XObject /Subtype /Image\n");
        write.accept("   /Width " + imgW + " /Height " + imgH + "\n");
        write.accept("   /ColorSpace /DeviceRGB /BitsPerComponent 8\n");
        write.accept("   /Filter /DCTDecode\n");
        write.accept("   /Length " + jpegBytes.length + "\n");
        write.accept(">>\n");
        write.accept("stream\n");
        pdfBuf.write(jpegBytes);
        write.accept("\nendstream\nendobj\n");

        // Table xref
        int xrefOffset = pdfBuf.size();
        write.accept("xref\n");
        write.accept("0 6\n");
        write.accept("0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            write.accept(String.format("%010d 00000 n \n", offsets[i]));
        }

        // Trailer
        write.accept("trailer\n");
        write.accept("<< /Size 6 /Root 1 0 R >>\n");
        write.accept("startxref\n");
        write.accept(xrefOffset + "\n");
        write.accept("%%EOF\n");

        // Écrire le PDF sur disque
        try (FileOutputStream fos = new FileOutputStream(fichier)) {
            pdfBuf.writeTo(fos);
        }
    }
    
    private void annulerReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                " Veuillez sélectionner une réservation à annuler",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        String statut = reservationsModel.getValueAt(selectedRow, 5).toString();
        
        if (statut.contains("ANNULÉ") || statut.contains("CONFIRMÉ")) {
            JOptionPane.showMessageDialog(this,
                " Cette réservation ne peut pas être annulée",
                "Annulation impossible",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            " Voulez-vous vraiment annuler cette réservation ?\n" +
            "Numéro: #" + reservationId,
            "Confirmation d'annulation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return reservationService.annulerReservation(reservationId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ClientFrame.this,
                                " Réservation annulée avec succès !",
                                "Annulation réussie",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshReservationsTable();
                        } else {
                            JOptionPane.showMessageDialog(ClientFrame.this,
                                " Erreur lors de l'annulation",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            " Erreur lors de l'annulation",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void saveProfile(JTextField nomField, JTextField prenomField, 
                           JTextField emailField, JTextField telField, 
                           JTextField cinField, JPasswordField passwordField) {
        String nouveauNom = nomField.getText().trim();
        String nouveauPrenom = prenomField.getText().trim();
        String nouvelEmail = emailField.getText().trim();
        String nouveauTel = telField.getText().trim();
        String nouveauCin = cinField.getText().trim();
        String nouveauPassword = new String(passwordField.getPassword());
        
        // Validation
        if (nouvelEmail.isEmpty() || !nouvelEmail.contains("@")) {
            JOptionPane.showMessageDialog(this,
                " Veuillez saisir un email valide",
                "Email invalide",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!nouveauPassword.isEmpty() && nouveauPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                " Le mot de passe doit contenir au moins 6 caractères",
                "Mot de passe trop court",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Mettre à jour l'utilisateur
                currentUser.setNom(nouveauNom.isEmpty() ? null : nouveauNom);
                currentUser.setPrenom(nouveauPrenom.isEmpty() ? null : nouveauPrenom);
                currentUser.setEmail(nouvelEmail);
                currentUser.setTelephone(nouveauTel.isEmpty() ? null : nouveauTel);
                currentUser.setCin(nouveauCin.isEmpty() ? null : nouveauCin);
                
                if (!nouveauPassword.isEmpty()) {
                    currentUser.setPassword(nouveauPassword);
                }
                
                return clientService.updateClient(currentUser);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            " Profil mis à jour avec succès !",
                            "Profil sauvegardé",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ClientFrame.this,
                            " Erreur lors de la mise à jour",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ClientFrame.this,
                        " Erreur lors de la mise à jour",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void showNotifications() {
        JOptionPane.showMessageDialog(this,
            " Vous avez 2 notifications:\n\n" +
            "1. Nouvelle offre spéciale disponible\n" +
            "2. Votre réservation #101 est confirmée",
            "Notifications",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAllOffres() {
        // Basculer vers l'onglet des offres
        tabbedPane.setSelectedIndex(3);
        refreshOffresTab();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            " Voulez-vous vraiment vous déconnecter ?",
            "Déconnexion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new com.transport.ui.frames.LoginFrame();
            });
        }
    }
}