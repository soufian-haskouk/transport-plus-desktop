package com.transport.ui.frames;

import com.transport.config.ThemeConfig;
import com.transport.models.*;
import com.transport.services.*;
import com.transport.ui.components.RoundedButton;
import com.transport.ui.components.GradientPanel;
import com.transport.utils.FontAwesomeLoader;
import com.transport.services.StatistiqueService;
import com.transport.models.Statistiques;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminFrame extends JFrame {
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
    private DefaultTableModel clientsModel;
    private DefaultTableModel offresModel;
    private JTable voyagesTable;
    private JTable reservationsTable;
    private JTable clientsTable;
    private JTable offresTable;
    
    // Formats
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    // Constantes de couleurs
    private static final Color[] TAB_COLORS = {
        new Color(0, 123, 255),    // Bleu pour Tableau de bord
        new Color(40, 167, 69),    // Vert pour Voyages
        new Color(255, 193, 7),    // Jaune/Or pour Réservations
        new Color(220, 53, 69),    // Rouge pour Clients
        new Color(111, 66, 193),   // Violet pour Offres
        new Color(253, 126, 20)    // Orange pour Paramètres
    };
    
    private static final Color[] STAT_COLORS = {
        new Color(40, 167, 69),    // vert
        new Color(0, 123, 255),    // bleu
        new Color(255, 193, 7),    // jaune
        new Color(220, 53, 69)     // rouge
    };
    
    private static final Color[] DESTINATION_COLORS = {
        new Color(0, 123, 255),    // bleu
        new Color(40, 167, 69),    // vert
        new Color(255, 193, 7),    // jaune
        new Color(220, 53, 69),    // rouge
        new Color(111, 66, 193),   // violet
        new Color(253, 126, 20),   // orange
        new Color(32, 201, 151)    // turquoise
    };
    
    // Couleurs supplémentaires
    private static final Color GRAY_COLOR = new Color(108, 117, 125);
    private static final Color LIGHT_GRAY = new Color(230, 230, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color INFO_COLOR = new Color(0, 123, 255);
    
    public AdminFrame(User user) {
        super("Tableau de bord Admin - " + user.getFullName());
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
        clientsModel = new DefaultTableModel();
        offresModel = new DefaultTableModel();
        
        // Initialiser les composants
        JPanel sidebar = createSidebar();
        JPanel header = createHeader();
        
        // Configurer le TabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Ajouter un listener pour mettre à jour l'apparence des onglets quand on change
        tabbedPane.addChangeListener(e -> updateTabComponents());
        
        // Créer les onglets
        tabbedPane.addTab("", createDashboardPanel());
        tabbedPane.addTab("", createVoyagesPanel());
        tabbedPane.addTab("", createReservationsPanel());
        tabbedPane.addTab("", createClientsPanel());
        tabbedPane.addTab("", createOffresPanel());
        tabbedPane.addTab("", createSettingsPanel());
        
        // Définir les composants d'onglet avec icônes FontAwesome
        setTabComponent(0, "\uf015", "Tableau de bord");
        setTabComponent(1, "\uf1b9", "Voyages");
        setTabComponent(2, "\uf3ff", "Réservations");
        setTabComponent(3, "\uf0c0", "Clients");
        setTabComponent(4, "\uf02b", "Offres");
        setTabComponent(5, "\uf013", "Paramètres");
        
        // Mettre à jour l'apparence initiale
        updateTabComponents();
        
        // Ajouter les composants à la fenêtre
        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

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
        
        // Rendre le panel cliquable pour sélectionner l'onglet
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tabbedPane.setSelectedIndex(index);
                updateTabComponents();
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
        });
        
        tabbedPane.setTabComponentAt(index, panel);
    }
    
    // Méthode utilitaire pour créer des icônes colorées
    private JLabel createIconLabel(String iconCode, Color color, float size) {
        JLabel iconLabel = new JLabel(iconCode);
        
        try {
            Font iconFont = ThemeConfig.getIconFont(size);
            iconLabel.setFont(iconFont);
        } catch (Exception e) {
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int)size));
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
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    // Méthode pour créer des boutons arrondis avec icônes
    private RoundedButton createRoundedIconButton(String icon, String text, Color color) {
        RoundedButton button = new RoundedButton(icon + " " + text);
        button.setFont(ThemeConfig.getIconFont(14f));
        button.setBackgroundColor(color);
        button.setHoverColor(color.darker());
        button.setTextColor(Color.WHITE);
        return button;
    }
    
    // Méthode pour mettre à jour tous les onglets quand on change de sélection
    private void updateTabComponents() {
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
                refreshDashboard();
                refreshVoyagesTable();
                refreshReservationsTable();
                refreshClientsTable();
                refreshOffresTable();
                return null;
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
        menuPanel.setLayout(new GridLayout(8, 1, 5, 10)); // Réduit l'espacement vertical
        menuPanel.setBorder(new EmptyBorder(20, 10, 20, 10));
        
     // Icônes et textes pour le menu
        Object[][] menuData = {
            {"\uf015", "Tableau de bord", TAB_COLORS[0]},
            {"\uf1b9", "Voyages", TAB_COLORS[1]},
            {"\uf3ff", "Réservations", TAB_COLORS[2]},
            {"\uf0c0", "Clients", TAB_COLORS[3]},
            {"\uf02b", "Offres", TAB_COLORS[4]},
            {"\uf059", "Aide", GRAY_COLOR},
            {"\uf013", "Paramètres", TAB_COLORS[5]},
            {"\uf08b", "Déconnexion", ERROR_COLOR}
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
        if (menuItem.contains("Tableau")) {
            tabbedPane.setSelectedIndex(0);
            refreshDashboard();
        } else if (menuItem.contains("Voyages")) {
            tabbedPane.setSelectedIndex(1);
            refreshVoyagesTable();
        } else if (menuItem.contains("Réservations")) {
            tabbedPane.setSelectedIndex(2);
            refreshReservationsTable();
        } else if (menuItem.contains("Clients")) {
            tabbedPane.setSelectedIndex(3);
            refreshClientsTable();
        } else if (menuItem.contains("Offres")) {
            tabbedPane.setSelectedIndex(4);
            refreshOffresTable();
        } else if (menuItem.contains("Aide")) {
            showHelp();
        } else if (menuItem.contains("Paramètres")) {
            tabbedPane.setSelectedIndex(5);
        } else if (menuItem.contains("Déconnexion")) {
            logout();
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
        
        JButton closeBtn = createRoundedIconButton("\uf00d", "Fermer", ERROR_COLOR);
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
        
        JLabel titleLabel = createIconLabel("\uf015", TAB_COLORS[0], 20f);
        titleLabel.setText("\uf015  Tableau de bord Administrateur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel dateTimeLabel = createIconLabel("\uf017", GRAY_COLOR, 14f);
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        javax.swing.Timer dateTimer = new javax.swing.Timer(1000, e -> {
            dateTimeLabel.setText("\uf017  " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        dateTimer.start();
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        JButton refreshBtn = createIconButton("\uf021", INFO_COLOR, "Actualiser les données");
        refreshBtn.addActionListener(e -> refreshAllData());
        
        actionPanel.add(refreshBtn);
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(dateTimeLabel, BorderLayout.EAST);
        header.add(actionPanel, BorderLayout.CENTER);
        
        return header;
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout(10, 10));
        dashboard.setBackground(Color.WHITE);
        dashboard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel statsPanel = createStatsPanel();
        dashboard.add(statsPanel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createRecentReservationsPanel());
        contentPanel.add(createPopularDestinationsPanel());
        
        dashboard.add(contentPanel, BorderLayout.CENTER);
        
        return dashboard;
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        StatistiqueService statsService = new StatistiqueService();
        Statistiques stats = statsService.getStatistiques();
        
        String[][] statData = {
            {"\uf155", "Revenus", String.format("%,.2f DH", stats.getRevenusTotal())},
            {"\uf3ff", "Réservations", String.valueOf(stats.getTotalReservations())},
            {"\uf1b9", "Voyages", String.valueOf(stats.getTotalVoyages())},
            {"\uf0c0", "Clients", String.valueOf(stats.getTotalClients())}
        };
        
        for (int i = 0; i < statData.length; i++) {
            JPanel statCard = new JPanel(new BorderLayout(10, 10));
            statCard.setBackground(Color.WHITE);
            statCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(STAT_COLORS[i], 2, true),
                new EmptyBorder(15, 20, 15, 20)
            ));
            
            JLabel iconLabel = createIconLabel(statData[i][0], STAT_COLORS[i], 24f);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel titleLabel = new JLabel(statData[i][1]);
            titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            titleLabel.setForeground(Color.GRAY);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JLabel valueLabel = new JLabel(statData[i][2]);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
            valueLabel.setForeground(STAT_COLORS[i]);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.add(iconLabel, BorderLayout.NORTH);
            centerPanel.add(titleLabel, BorderLayout.CENTER);
            centerPanel.add(valueLabel, BorderLayout.SOUTH);
            
            statCard.add(centerPanel, BorderLayout.CENTER);
            statsPanel.add(statCard);
        }
        
        return statsPanel;
    }
    
    private JPanel createRecentReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = createIconLabel("\uf46c", TAB_COLORS[2], 18f);
        title.setText("\uf46c  Réservations récentes");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] columns = {"ID", "Client", "Trajet", "Date", "Montant", "Statut"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return reservationService.getAllReservations();
            }
            
            @Override
            protected void done() {
                try {
                    List<Reservation> reservations = get();
                    model.setRowCount(0);
                    
                    int limit = Math.min(reservations.size(), 5);
                    for (int i = 0; i < limit; i++) {
                        Reservation res = reservations.get(i);
                        Object[] row = {
                            res.getId(),
                            res.getClientNom(),
                            res.getTrajet(),
                            datetimeFormat.format(res.getDateDepart()),
                            String.format("%.2f DH", res.getMontantTotal()),
                            res.getStatutIcon()
                        };
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPopularDestinationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(LIGHT_GRAY),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = createIconLabel("\uf041", SUCCESS_COLOR, 18f);
        title.setText("\uf041  Destinations populaires");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        final StatistiqueService statsService = new StatistiqueService();
        final int totalReservations = statsService.getTotalReservationsToutes();
        Map<String, Integer> destinationsMap = getDestinationsData(statsService);
        
        final java.util.List<String> destinationNames = new ArrayList<>();
        final java.util.List<Integer> destinationCounts = new ArrayList<>();
        
        int totalFromDestinations = 0;
        for (Map.Entry<String, Integer> entry : destinationsMap.entrySet()) {
            destinationNames.add(entry.getKey());
            destinationCounts.add(entry.getValue());
            totalFromDestinations += entry.getValue();
        }
        
        final int displayTotal = totalReservations > 0 ? totalReservations : totalFromDestinations;
        final int[] pourcentages = calculatePercentagesBasedOnTotal(destinationCounts, displayTotal);
        adjustPercentages(pourcentages);
        
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                
                if (destinationNames.isEmpty()) {
                    drawNoDataMessage(g2d, width, height);
                    return;
                }
                
                int chartSize = Math.min(width - 220, height - 40);
                if (chartSize < 120) chartSize = 120;
                
                int chartX = 20;
                int chartY = (height - chartSize) / 2;
                if (chartY < 20) chartY = 20;
                
                draw3DPieChart(g2d, destinationNames, pourcentages, DESTINATION_COLORS, chartX, chartY, chartSize);
                drawLegendWithDetails(g2d, destinationNames, destinationCounts, pourcentages, 
                                     DESTINATION_COLORS, chartX + chartSize + 20, chartY, height - chartY - 20);
                drawTotalInCenter(g2d, displayTotal, chartX, chartY, chartSize);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(450, 280);
            }
        };
        
        chartPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                chartPanel.repaint();
            }
        });
        
        JButton refreshButton = createIconButton("\uf021", INFO_COLOR, "Rafraîchir les données");
        refreshButton.addActionListener(e -> {
            refreshButton.setText("\uf021  Chargement...");
            refreshButton.setEnabled(false);
            
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Map<String, Integer> newData = getDestinationsData(statsService);
                    
                    SwingUtilities.invokeLater(() -> {
                        if (!newData.isEmpty()) {
                            destinationNames.clear();
                            destinationCounts.clear();
                            
                            int newTotal = 0;
                            for (Map.Entry<String, Integer> entry : newData.entrySet()) {
                                destinationNames.add(entry.getKey());
                                destinationCounts.add(entry.getValue());
                                newTotal += entry.getValue();
                            }
                            
                            int[] newPercentages = calculatePercentagesBasedOnTotal(destinationCounts, newTotal);
                            System.arraycopy(newPercentages, 0, pourcentages, 0, 
                                           Math.min(pourcentages.length, newPercentages.length));
                            
                            adjustPercentages(pourcentages);
                            chartPanel.repaint();
                        }
                        
                        refreshButton.setText("\uf021  Rafraîchir");
                        refreshButton.setEnabled(true);
                    });
                    
                    return null;
                }
            };
            worker.execute();
        });
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(title, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createVoyagesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel title = createIconLabel("\uf1b9", TAB_COLORS[1], 20f);
        title.setText("\uf1b9  Gestion des Voyages");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton addBtn = createRoundedIconButton("\uf067", "Ajouter", SUCCESS_COLOR);
        addBtn.addActionListener(e -> addVoyage());
        
        RoundedButton editBtn = createRoundedIconButton("\uf044", "Modifier", INFO_COLOR);
        editBtn.addActionListener(e -> editVoyage());
        
        RoundedButton deleteBtn = createRoundedIconButton("\uf1f8", "Supprimer", ERROR_COLOR);
        deleteBtn.addActionListener(e -> deleteVoyage());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        String[] voyageColumns = {"ID", "Départ", "Arrivée", "Date/Heure", "Véhicule", "Prix (DH)", "Places", "Disponibles", "Statut"};
        voyagesModel = new DefaultTableModel(voyageColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        voyagesTable = new JTable(voyagesModel);
        styleTable(voyagesTable);
        
        JScrollPane scrollPane = new JScrollPane(voyagesTable);
        scrollPane.setBorder(new LineBorder(LIGHT_GRAY));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf3ff", TAB_COLORS[2], 20f);
        title.setText("\uf3ff  Gestion des Réservations");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        String[] reservationColumns = {"ID", "Client", "Trajet", "Date départ", "Places", "Montant (DH)", "Statut", "Date réservation"};
        reservationsModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reservationsTable = new JTable(reservationsModel);
        styleTable(reservationsTable);
        
        reservationsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = table.getValueAt(row, 6).toString();
                    if (status.contains("CONFIRMÉ")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (status.contains("EN ATTENTE")) {
                        c.setBackground(new Color(255, 255, 200));
                    } else if (status.contains("ANNULÉ")) {
                        c.setBackground(new Color(255, 220, 220));
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        
        RoundedButton confirmBtn = createRoundedIconButton("\uf00c", "Confirmer", SUCCESS_COLOR);
        confirmBtn.addActionListener(e -> confirmReservation());
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        cancelBtn.addActionListener(e -> cancelReservationAdmin());
        
        RoundedButton detailsBtn = createRoundedIconButton("\uf002", "Détails", INFO_COLOR);
        detailsBtn.addActionListener(e -> viewReservationDetailsAdmin());
        
        actionPanel.add(confirmBtn);
        actionPanel.add(cancelBtn);
        actionPanel.add(detailsBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf0c0", TAB_COLORS[3], 20f);
        title.setText("\uf0c0  Gestion des Clients");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        String[] clientColumns = {"ID", "Nom", "Prénom", "Email", "Téléphone", "CIN", "Date inscription", "Nb Réservations"};
        clientsModel = new DefaultTableModel(clientColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        clientsTable = new JTable(clientsModel);
        styleTable(clientsTable);
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        
        RoundedButton viewBtn = createRoundedIconButton("\uf06e", "Voir détails", INFO_COLOR);
        viewBtn.addActionListener(e -> viewClientDetails());
        
        RoundedButton historyBtn = createRoundedIconButton("\uf1da", "Historique", TAB_COLORS[4]);
        historyBtn.addActionListener(e -> viewClientHistory());
        
        actionPanel.add(viewBtn);
        actionPanel.add(historyBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createOffresPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel title = createIconLabel("\uf02b", TAB_COLORS[4], 20f);
        title.setText("\uf02b  Gestion des Offres Spéciales");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        RoundedButton addBtn = createRoundedIconButton("\uf067", "Ajouter", SUCCESS_COLOR);
        addBtn.addActionListener(e -> addOffre());
        
        RoundedButton editBtn = createRoundedIconButton("\uf044", "Modifier", INFO_COLOR);
        editBtn.addActionListener(e -> editOffre());
        
        RoundedButton deleteBtn = createRoundedIconButton("\uf1f8", "Supprimer", ERROR_COLOR);
        deleteBtn.addActionListener(e -> deleteOffre());
        
        RoundedButton refreshBtn = createRoundedIconButton("\uf021", "Rafraîchir", TAB_COLORS[4]);
        refreshBtn.addActionListener(e -> refreshOffresTable());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        String[] offresColumns = {"ID", "Titre", "Code Promo", "Réduction", "Date Début", "Date Fin", "Statut"};
        offresModel = new DefaultTableModel(offresColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        offresTable = new JTable(offresModel);
        styleTable(offresTable);
        
        offresTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = table.getValueAt(row, 6).toString();
                    if (status.contains("ACTIVE")) {
                        c.setBackground(new Color(220, 255, 220));
                    } else if (status.contains("EXPIRED")) {
                        c.setBackground(new Color(255, 220, 220));
                    } else if (status.contains("INACTIVE")) {
                        c.setBackground(new Color(240, 240, 240));
                    } else {
                        c.setBackground(table.getBackground());
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(offresTable);
        scrollPane.setBorder(new LineBorder(LIGHT_GRAY));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createIconLabel("\uf013", TAB_COLORS[5], 20f);
        title.setText("\uf013  Paramètres du système");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTabbedPane settingsTabs = new JTabbedPane();
        
        JPanel generalPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        generalPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        String[] settings = {
            "Nom de la société:", "Transport+",
            "Devise:", "DH (Dirham marocain)",
            "Fuseau horaire:", "GMT+1",
            "TVA (%):", "20",
            "Frais d'annulation (%):", "10"
        };
        
        for (int i = 0; i < settings.length; i += 2) {
            generalPanel.add(new JLabel(settings[i]));
            generalPanel.add(new JTextField(settings[i + 1]));
        }
        
        settingsTabs.addTab("Général", generalPanel);
        
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.setBackground(Color.WHITE);
        
        RoundedButton resetBtn = createRoundedIconButton("\uf0e2", "Réinitialiser", GRAY_COLOR);
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Sauvegarder", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> saveSettings());
        
        savePanel.add(resetBtn);
        savePanel.add(saveBtn);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(settingsTabs, BorderLayout.CENTER);
        panel.add(savePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
        
        String nomComplet = (currentUser.getPrenom() != null ? currentUser.getPrenom() : "") + " " +
                          (currentUser.getNom() != null ? currentUser.getNom() : "");
        
        JLabel statusLabel = createIconLabel("\uf058", SUCCESS_COLOR, 12f);
        statusLabel.setText("\uf058  Connecté en tant que Administrateur - " + 
            (nomComplet.trim().isEmpty() ? "Admin System" : nomComplet.trim()));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel memoryLabel = createIconLabel("\uf233", GRAY_COLOR, 12f);
        memoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        javax.swing.Timer memoryTimer = new javax.swing.Timer(5000, e -> {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            memoryLabel.setText("\uf233  Mémoire: " + usedMemory + "MB / " + maxMemory + "MB ");
        });
        memoryTimer.start();
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(memoryLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    // Méthodes de rafraîchissement des données
    private void refreshAllData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                refreshDashboard();
                refreshVoyagesTable();
                refreshReservationsTable();
                refreshClientsTable();
                refreshOffresTable();
                return null;
            }
            
            @Override
            protected void done() {
                JOptionPane.showMessageDialog(AdminFrame.this,
                    createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                    " Données actualisées avec succès !",
                    "Actualisation",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }
    
    private void refreshDashboard() {
        // Le dashboard se rafraîchit automatiquement via les appels aux services
    }
    
    private void refreshVoyagesTable() {
        SwingWorker<List<Voyage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Voyage> doInBackground() throws Exception {
                return voyageService.getAllVoyages();
            }
            
            @Override
            protected void done() {
                try {
                    List<Voyage> voyages = get();
                    voyagesModel.setRowCount(0);
                    
                    for (Voyage voyage : voyages) {
                        Object[] row = {
                            voyage.getId(),
                            voyage.getVilleDepart(),
                            voyage.getVilleArrivee(),
                            datetimeFormat.format(voyage.getDateDepart()),
                            voyage.getTypeVehicule(),
                            voyage.getPrix(),
                            voyage.getPlacesTotales(),
                            voyage.getPlacesDisponibles(),
                            voyage.getStatut()
                        };
                        voyagesModel.addRow(row);
                    }
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
                return reservationService.getAllReservations();
            }
            
            @Override
            protected void done() {
                try {
                    List<Reservation> reservations = get();
                    reservationsModel.setRowCount(0);
                    
                    for (Reservation res : reservations) {
                        Object[] row = {
                            res.getId(),
                            res.getClientNom(),
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
                }
            }
        };
        worker.execute();
    }
    
    private void refreshClientsTable() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return clientService.getAllClients();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> clients = get();
                    clientsModel.setRowCount(0);
                    
                    for (User client : clients) {
                        int nbReservations = reservationService.getClientReservations(client.getId()).size();
                        
                        Object[] row = {
                            client.getId(),
                            client.getNom(),
                            client.getPrenom(),
                            client.getEmail(),
                            client.getTelephone(),
                            client.getCin(),
                            dateFormat.format(client.getDateInscription()),
                            nbReservations
                        };
                        clientsModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void refreshOffresTable() {
        SwingWorker<List<OffreSpecial>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OffreSpecial> doInBackground() throws Exception {
                return offreService.getAllOffres();
            }
            
            @Override
            protected void done() {
                try {
                    List<OffreSpecial> offres = get();
                    offresModel.setRowCount(0);
                    
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    
                    for (OffreSpecial offre : offres) {
                        String reductionText;
                        if (offre.getReductionPourcentage() > 0) {
                            reductionText = String.format("%.0f%%", offre.getReductionPourcentage());
                        } else if (offre.getReductionFixe() > 0) {
                            reductionText = String.format("%.0f DH", offre.getReductionFixe());
                        } else {
                            reductionText = "Aucune";
                        }
                        
                        String statut;
                        Date now = new Date();
                        if ("ACTIVE".equals(offre.getStatut()) && 
                            offre.getDateDebut().before(now) && 
                            offre.getDateFin().after(now)) {
                            statut = "ACTIVE";
                        } else if (offre.getDateFin().before(now)) {
                            statut = "EXPIRED";
                        } else {
                            statut = offre.getStatut();
                        }
                        
                        Object[] row = {
                            offre.getId(),
                            offre.getTitre(),
                            offre.getCodePromo(),
                            reductionText,
                            dateFormat.format(offre.getDateDebut()),
                            dateFormat.format(offre.getDateFin()),
                            statut
                        };
                        offresModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    // Les méthodes restantes (addVoyage, editVoyage, deleteVoyage, etc.) restent identiques
    // mais avec l'utilisation des constantes de couleurs définies plus haut
    
    private void addVoyage() {
        JDialog dialog = new JDialog(this, "Ajouter un voyage", true);
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField departField = new JTextField();
        JTextField arriveeField = new JTextField();
        JTextField dateField = new JTextField("AAAA-MM-JJ HH:MM");
        
        List<String> vehiculesList = new ArrayList<>();
        Map<String, Integer> vehiculeMap = new HashMap<>();
        
        try {
            List<String> tempList = voyageService.getVehiculesDisponibles();
            vehiculesList.addAll(tempList);
            
            for (String vehicule : tempList) {
                try {
                    String idPart = vehicule.split("ID: ")[1].split(" -")[0];
                    int id = Integer.parseInt(idPart);
                    vehiculeMap.put(vehicule, id);
                } catch (Exception e) {
                    System.err.println("Erreur parsing ID véhicule: " + vehicule);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        final List<String> finalVehiculesList = new ArrayList<>(vehiculesList);
        final Map<String, Integer> finalVehiculeMap = new HashMap<>(vehiculeMap);
        
        JComboBox<String> vehiculeCombo = new JComboBox<>();
        
        if (finalVehiculesList.isEmpty()) {
            vehiculeCombo.addItem("Aucun véhicule disponible - Ajoutez-en d'abord");
        } else {
            for (String vehicule : finalVehiculesList) {
                vehiculeCombo.addItem(vehicule);
            }
        }
        
        JTextField prixField = new JTextField();
        JTextField placesField = new JTextField();
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"ACTIF", "INACTIF"});
        
        panel.add(new JLabel("Ville départ:"));
        panel.add(departField);
        panel.add(new JLabel("Ville arrivée:"));
        panel.add(arriveeField);
        panel.add(new JLabel("Date départ (AAAA-MM-JJ HH:MM):"));
        panel.add(dateField);
        panel.add(new JLabel("Véhicule:"));
        panel.add(vehiculeCombo);
        panel.add(new JLabel("Prix (DH):"));
        panel.add(prixField);
        panel.add(new JLabel("Places totales:"));
        panel.add(placesField);
        panel.add(new JLabel("Statut:"));
        panel.add(statutCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Enregistrer", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            if (departField.getText().trim().isEmpty() || 
                arriveeField.getText().trim().isEmpty() || 
                dateField.getText().trim().isEmpty() ||
                prixField.getText().trim().isEmpty() ||
                placesField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Veuillez remplir tous les champs");
                return;
            }
            
            if (finalVehiculesList.isEmpty() || 
                vehiculeCombo.getSelectedItem().toString().contains("Aucun véhicule")) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Aucun véhicule disponible. Ajoutez d'abord des véhicules dans la BD.");
                return;
            }
            
            double prix = 0;
            try {
                prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                        " Le prix doit être supérieur à 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Prix invalide. Entrez un nombre valide.");
                return;
            }
            
            int places = 0;
            try {
                places = Integer.parseInt(placesField.getText());
                if (places <= 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                        " Le nombre de places doit être supérieur à 0");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Nombre de places invalide. Entrez un nombre entier valide.");
                return;
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false);
            
            try {
                Date dateDepart = sdf.parse(dateField.getText());
                
                if (dateDepart.before(new Date())) {
                    JOptionPane.showMessageDialog(dialog, 
                        createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                        " La date de départ ne peut pas être dans le passé");
                    return;
                }
                
                Voyage voyage = new Voyage();
                voyage.setVilleDepart(departField.getText().trim());
                voyage.setVilleArrivee(arriveeField.getText().trim());
                voyage.setDateDepart(dateDepart);
                
                String selectedVehicule = (String) vehiculeCombo.getSelectedItem();
                if (finalVehiculeMap.containsKey(selectedVehicule)) {
                    int vehiculeId = finalVehiculeMap.get(selectedVehicule);
                    voyage.setVehiculeId(vehiculeId);
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                        " Erreur: Véhicule non valide");
                    return;
                }
                
                voyage.setPrix(prix);
                voyage.setPlacesTotales(places);
                voyage.setPlacesDisponibles(places);
                voyage.setStatut(statutCombo.getSelectedItem().toString());
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return voyageService.createVoyage(voyage);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                    " Voyage ajouté avec succès!");
                                refreshVoyagesTable();
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                    " Erreur lors de l'ajout du voyage. Vérifiez les données.");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, 
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
                
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Format de date invalide. Utilisez AAAA-MM-JJ HH:MM (ex: 2024-01-16 14:30)");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                    " Erreur inattendue: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editVoyage() {
        int selectedRow = voyagesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner un voyage à modifier",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int voyageId = (int) voyagesModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Voyage, Void> worker = new SwingWorker<>() {
            @Override
            protected Voyage doInBackground() throws Exception {
                List<Voyage> voyages = voyageService.getAllVoyages();
                for (Voyage voyage : voyages) {
                    if (voyage.getId() == voyageId) {
                        return voyage;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    Voyage voyage = get();
                    if (voyage != null) {
                        showEditVoyageDialog(voyage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void showEditVoyageDialog(Voyage voyage) {
        JDialog editDialog = new JDialog(this, "Modifier le voyage", true);
        editDialog.setSize(500, 450);
        editDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextField departField = new JTextField(voyage.getVilleDepart());
        JTextField arriveeField = new JTextField(voyage.getVilleArrivee());
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(voyage.getDateDepart()));
        
        List<String> vehiculesList = voyageService.getVehiculesDisponibles();
        JComboBox<String> vehiculeCombo = new JComboBox<>();
        
        if (vehiculesList.isEmpty()) {
            vehiculeCombo.addItem("Aucun véhicule disponible");
        } else {
            for (String vehicule : vehiculesList) {
                vehiculeCombo.addItem(vehicule);
            }
        }
        
        JTextField prixField = new JTextField(String.valueOf(voyage.getPrix()));
        JTextField placesField = new JTextField(String.valueOf(voyage.getPlacesTotales()));
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"ACTIF", "INACTIF"});
        statutCombo.setSelectedItem(voyage.getStatut());
        
        panel.add(new JLabel("Ville départ:"));
        panel.add(departField);
        panel.add(new JLabel("Ville arrivée:"));
        panel.add(arriveeField);
        panel.add(new JLabel("Date départ (AAAA-MM-JJ HH:MM):"));
        panel.add(dateField);
        panel.add(new JLabel("Véhicule:"));
        panel.add(vehiculeCombo);
        panel.add(new JLabel("Prix (DH):"));
        panel.add(prixField);
        panel.add(new JLabel("Places totales:"));
        panel.add(placesField);
        panel.add(new JLabel("Statut:"));
        panel.add(statutCombo);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        cancelBtn.addActionListener(e -> editDialog.dispose());
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Enregistrer", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            voyage.setVilleDepart(departField.getText().trim());
            voyage.setVilleArrivee(arriveeField.getText().trim());
            
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                voyage.setDateDepart(sdf.parse(dateField.getText()));
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Format de date invalide");
                return;
            }
            
            try {
                voyage.setPrix(Double.parseDouble(prixField.getText()));
                voyage.setPlacesTotales(Integer.parseInt(placesField.getText()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editDialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Prix ou nombre de places invalide");
                return;
            }
            
            voyage.setStatut((String) statutCombo.getSelectedItem());
            
            SwingWorker<Boolean, Void> updateWorker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return voyageService.updateVoyage(voyage);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(editDialog, 
                                createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                " Voyage modifié avec succès!");
                            refreshVoyagesTable();
                            editDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(editDialog, 
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur lors de la modification");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            updateWorker.execute();
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        editDialog.add(panel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setVisible(true);
    }

    private void deleteVoyage() {
        int selectedRow = voyagesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner un voyage à supprimer",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int voyageId = (int) voyagesModel.getValueAt(selectedRow, 0);
        String trajet = voyagesModel.getValueAt(selectedRow, 1) + " → " + 
                       voyagesModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            createIconLabel("\uf1f8", ERROR_COLOR, 16f).getText() + 
            " Voulez-vous vraiment supprimer ce voyage?\n\n" +
            "ID: " + voyageId + "\n" +
            "Trajet: " + trajet,
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return voyageService.deleteVoyage(voyageId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AdminFrame.this, 
                                createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                " Voyage supprimé avec succès!");
                            refreshVoyagesTable();
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur lors de la suppression du voyage\n" +
                                "Vérifiez qu'il n'y a pas de réservations associées",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(AdminFrame.this,
                            createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                            " Erreur lors de la suppression",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void confirmReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner une réservation à confirmer",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return reservationService.confirmReservation(reservationId);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                            createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                            " Réservation confirmée avec succès!");
                        refreshReservationsTable();
                    } else {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                            createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                            " Erreur lors de la confirmation",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void cancelReservationAdmin() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner une réservation à annuler",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
            " Voulez-vous vraiment annuler cette réservation?\n\n" +
            "ID: #" + reservationId,
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
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                " Réservation annulée avec succès!");
                            refreshReservationsTable();
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur lors de l'annulation",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private void viewReservationDetailsAdmin() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner une réservation",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int reservationId = (int) reservationsModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Reservation, Void> worker = new SwingWorker<>() {
            @Override
            protected Reservation doInBackground() throws Exception {
                List<Reservation> reservations = reservationService.getAllReservations();
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
                    if (reservation != null) {
                        String details = String.format(
                            createIconLabel("\uf46c", INFO_COLOR, 16f).getText() + 
                            " Détails de la réservation #%s\n\n" +
                            createIconLabel("\uf007", INFO_COLOR, 14f).getText() + " Client: %s\n" +
                            createIconLabel("\uf041", INFO_COLOR, 14f).getText() + " Trajet: %s\n" +
                            createIconLabel("\uf017", INFO_COLOR, 14f).getText() + " Date départ: %s\n" +
                            createIconLabel("\uf0c0", INFO_COLOR, 14f).getText() + " Nombre de places: %s\n" +
                            createIconLabel("\uf155", INFO_COLOR, 14f).getText() + " Montant total: %.2f DH\n" +
                            createIconLabel("\uf06e", INFO_COLOR, 14f).getText() + " Statut: %s\n" +
                            createIconLabel("\uf133", INFO_COLOR, 14f).getText() + " Date réservation: %s\n" +
                            createIconLabel("\uf236", INFO_COLOR, 14f).getText() + " Numéro sièges: %s",
                            reservation.getId(),
                            reservation.getClientNom(),
                            reservation.getTrajet(),
                            datetimeFormat.format(reservation.getDateDepart()),
                            reservation.getNombrePlaces(),
                            reservation.getMontantTotal(),
                            reservation.getStatutIcon(),
                            datetimeFormat.format(reservation.getDateReservation()),
                            reservation.getSiegeNumbers()
                        );
                        
                        JOptionPane.showMessageDialog(AdminFrame.this, 
                            details, 
                            createIconLabel("\uf46c", INFO_COLOR, 16f).getText() + 
                            " Détails réservation", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void viewClientDetails() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner un client",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int clientId = (int) clientsModel.getValueAt(selectedRow, 0);
        
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                List<User> clients = clientService.getAllClients();
                for (User client : clients) {
                    if (client.getId() == clientId) {
                        return client;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    User client = get();
                    if (client != null) {
                        String details = String.format(
                            createIconLabel("\uf007", INFO_COLOR, 16f).getText() + 
                            " Détails du client\n\n" +
                            createIconLabel("\uf022", INFO_COLOR, 14f).getText() + " Nom: %s\n" +
                            createIconLabel("\uf2c2", INFO_COLOR, 14f).getText() + " Prénom: %s\n" +
                            createIconLabel("\uf0e0", INFO_COLOR, 14f).getText() + " Email: %s\n" +
                            createIconLabel("\uf095", INFO_COLOR, 14f).getText() + " Téléphone: %s\n" +
                            createIconLabel("\uf2c3", INFO_COLOR, 14f).getText() + " CIN: %s\n" +
                            createIconLabel("\uf133", INFO_COLOR, 14f).getText() + " Date inscription: %s",
                            client.getNom(),
                            client.getPrenom(),
                            client.getEmail(),
                            client.getTelephone(),
                            client.getCin(),
                            dateFormat.format(client.getDateInscription())
                        );
                        
                        JOptionPane.showMessageDialog(AdminFrame.this, 
                            details, 
                            createIconLabel("\uf007", INFO_COLOR, 16f).getText() + 
                            " Détails client", 
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void viewClientHistory() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner un client",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int clientId = (int) clientsModel.getValueAt(selectedRow, 0);
        String clientNom = clientsModel.getValueAt(selectedRow, 1) + " " + 
                          clientsModel.getValueAt(selectedRow, 2);
        
        SwingWorker<List<Reservation>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Reservation> doInBackground() throws Exception {
                return reservationService.getClientReservations(clientId);
            }
            
            @Override
            protected void done() {
                try {
                    List<Reservation> reservations = get();
                    
                    if (reservations.isEmpty()) {
                        JOptionPane.showMessageDialog(AdminFrame.this,
                            createIconLabel("\uf2b6", INFO_COLOR, 16f).getText() + 
                            " Aucune réservation pour ce client",
                            "Historique vide",
                            JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><div style='font-family: Arial; padding: 10px;'>");
                    sb.append("<h3 style='color: #2c3e50;'>")
                      .append(createIconLabel("\uf1da", INFO_COLOR, 16f).getText())
                      .append(" Historique des réservations</h3>");
                    sb.append("<p><b>")
                      .append(createIconLabel("\uf007", INFO_COLOR, 14f).getText())
                      .append(" Client:</b> ").append(clientNom).append("</p>");
                    sb.append("<table border='1' cellpadding='5' style='border-collapse: collapse; width: 100%;'>");
                    sb.append("<tr style='background-color: #f8f9fa;'>")
                      .append("<th>ID</th><th>Trajet</th><th>Date</th><th>Places</th><th>Montant</th><th>Statut</th>")
                      .append("</tr>");
                    
                    double total = 0;
                    for (Reservation res : reservations) {
                        String statusColor = "";
                        if (res.getStatutIcon().contains("CONFIRMÉ")) {
                            statusColor = "color: #28a745;";
                        } else if (res.getStatutIcon().contains("ANNULÉ")) {
                            statusColor = "color: #dc3545;";
                        } else {
                            statusColor = "color: #ffc107;";
                        }
                        
                        sb.append("<tr>");
                        sb.append("<td>").append(res.getId()).append("</td>");
                        sb.append("<td>").append(res.getTrajet()).append("</td>");
                        sb.append("<td>").append(datetimeFormat.format(res.getDateDepart())).append("</td>");
                        sb.append("<td align='center'>").append(res.getNombrePlaces()).append("</td>");
                        sb.append("<td align='right'>").append(String.format("%.2f", res.getMontantTotal())).append(" DH</td>");
                        sb.append("<td style='").append(statusColor).append("'>").append(res.getStatutIcon()).append("</td>");
                        sb.append("</tr>");
                        total += res.getMontantTotal();
                    }
                    
                    sb.append("</table>");
                    sb.append("<p style='margin-top: 15px; padding: 10px; background-color: #f8f9fa; border-radius: 5px;'>")
                      .append("<b>")
                      .append(createIconLabel("\uf155", SUCCESS_COLOR, 14f).getText())
                      .append(" Total dépensé:</b> <span style='color: #28a745; font-weight: bold;'>")
                      .append(String.format("%.2f", total)).append(" DH</span></p>");
                    sb.append("</div></html>");
                    
                    JOptionPane.showMessageDialog(AdminFrame.this,
                        sb.toString(),
                        createIconLabel("\uf1da", INFO_COLOR, 16f).getText() + 
                        " Historique client - " + clientNom,
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void saveSettings() {
        JOptionPane.showMessageDialog(this,
            createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
            " Paramètres sauvegardés avec succès!",
            "Sauvegarde",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addOffre() {
        JDialog dialog = new JDialog(this, "Ajouter une offre spéciale", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Titre:"), gbc);
        
        JTextField titreField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(titreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(descriptionScroll, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Code Promo:"), gbc);
        
        JTextField codeField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(codeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Type de réduction:"), gbc);
        
        JPanel reductionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton pourcentageBtn = new JRadioButton("Pourcentage");
        JRadioButton fixeBtn = new JRadioButton("Montant fixe");
        ButtonGroup reductionGroup = new ButtonGroup();
        reductionGroup.add(pourcentageBtn);
        reductionGroup.add(fixeBtn);
        pourcentageBtn.setSelected(true);
        
        reductionPanel.add(pourcentageBtn);
        reductionPanel.add(fixeBtn);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(reductionPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Valeur:"), gbc);
        
        JTextField reductionField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(reductionField, gbc);
        
        JLabel reductionUnit = new JLabel("%");
        gbc.gridx = 2; gbc.gridy = 4;
        panel.add(reductionUnit, gbc);
        
        pourcentageBtn.addActionListener(e -> reductionUnit.setText("%"));
        fixeBtn.addActionListener(e -> reductionUnit.setText("DH"));
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Date début (AAAA-MM-JJ):"), gbc);
        
        JTextField dateDebutField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(dateDebutField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Date fin (AAAA-MM-JJ):"), gbc);
        
        JTextField dateFinField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(dateFinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Conditions:"), gbc);
        
        JTextField conditionsField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 7;
        panel.add(conditionsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Statut:"), gbc);
        
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        gbc.gridx = 1; gbc.gridy = 8;
        panel.add(statutCombo, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Enregistrer", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            if (titreField.getText().trim().isEmpty() ||
                codeField.getText().trim().isEmpty() ||
                reductionField.getText().trim().isEmpty() ||
                dateDebutField.getText().trim().isEmpty() ||
                dateFinField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Veuillez remplir tous les champs obligatoires");
                return;
            }
            
            try {
                OffreSpecial offre = new OffreSpecial();
                offre.setTitre(titreField.getText().trim());
                offre.setDescription(descriptionArea.getText().trim());
                offre.setCodePromo(codeField.getText().trim().toUpperCase());
                
                double reductionValue = Double.parseDouble(reductionField.getText());
                if (pourcentageBtn.isSelected()) {
                    offre.setReductionPourcentage(reductionValue);
                    offre.setReductionFixe(0);
                } else {
                    offre.setReductionPourcentage(0);
                    offre.setReductionFixe(reductionValue);
                }
                
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                offre.setDateDebut(sdf.parse(dateDebutField.getText()));
                offre.setDateFin(sdf.parse(dateFinField.getText()));
                
                offre.setConditions(conditionsField.getText().trim());
                offre.setStatut((String) statutCombo.getSelectedItem());
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return offreService.createOffre(offre);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                    " Offre ajoutée avec succès!");
                                refreshOffresTable();
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                    " Erreur lors de l'ajout de l'offre");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, 
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Valeur de réduction invalide");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Format de date invalide. Utilisez AAAA-MM-JJ");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                    " Erreur: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);
        
        gbc.gridy = 10;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editOffre() {
        int selectedRow = offresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner une offre à modifier",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int offreId = (int) offresModel.getValueAt(selectedRow, 0);
        
        SwingWorker<OffreSpecial, Void> worker = new SwingWorker<>() {
            @Override
            protected OffreSpecial doInBackground() throws Exception {
                List<OffreSpecial> offres = offreService.getAllOffres();
                for (OffreSpecial offre : offres) {
                    if (offre.getId() == offreId) {
                        return offre;
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    OffreSpecial offre = get();
                    if (offre != null) {
                        showEditOffreDialog(offre);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void showEditOffreDialog(OffreSpecial offre) {
        JDialog dialog = new JDialog(this, "Modifier l'offre spéciale", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Titre:"), gbc);
        
        JTextField titreField = new JTextField(offre.getTitre(), 20);
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(titreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        
        JTextArea descriptionArea = new JTextArea(offre.getDescription(), 3, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(descriptionScroll, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Code Promo:"), gbc);
        
        JTextField codeField = new JTextField(offre.getCodePromo(), 10);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(codeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Type de réduction:"), gbc);
        
        JPanel reductionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton pourcentageBtn = new JRadioButton("Pourcentage");
        JRadioButton fixeBtn = new JRadioButton("Montant fixe");
        ButtonGroup reductionGroup = new ButtonGroup();
        reductionGroup.add(pourcentageBtn);
        reductionGroup.add(fixeBtn);
        
        if (offre.getReductionPourcentage() > 0) {
            pourcentageBtn.setSelected(true);
        } else if (offre.getReductionFixe() > 0) {
            fixeBtn.setSelected(true);
        } else {
            pourcentageBtn.setSelected(true);
        }
        
        reductionPanel.add(pourcentageBtn);
        reductionPanel.add(fixeBtn);
        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(reductionPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Valeur:"), gbc);
        
        double reductionValue = offre.getReductionPourcentage() > 0 ? 
                               offre.getReductionPourcentage() : offre.getReductionFixe();
        JTextField reductionField = new JTextField(String.valueOf(reductionValue), 10);
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(reductionField, gbc);
        
        JLabel reductionUnit = new JLabel(offre.getReductionPourcentage() > 0 ? "%" : "DH");
        gbc.gridx = 2; gbc.gridy = 4;
        panel.add(reductionUnit, gbc);
        
        pourcentageBtn.addActionListener(e -> reductionUnit.setText("%"));
        fixeBtn.addActionListener(e -> reductionUnit.setText("DH"));
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Date début (AAAA-MM-JJ):"), gbc);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JTextField dateDebutField = new JTextField(sdf.format(offre.getDateDebut()), 10);
        gbc.gridx = 1; gbc.gridy = 5;
        panel.add(dateDebutField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Date fin (AAAA-MM-JJ):"), gbc);
        
        JTextField dateFinField = new JTextField(sdf.format(offre.getDateFin()), 10);
        gbc.gridx = 1; gbc.gridy = 6;
        panel.add(dateFinField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Conditions:"), gbc);
        
        JTextField conditionsField = new JTextField(offre.getConditions(), 20);
        gbc.gridx = 1; gbc.gridy = 7;
        panel.add(conditionsField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Statut:"), gbc);
        
        JComboBox<String> statutCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        statutCombo.setSelectedItem(offre.getStatut());
        gbc.gridx = 1; gbc.gridy = 8;
        panel.add(statutCombo, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        RoundedButton cancelBtn = createRoundedIconButton("\uf00d", "Annuler", ERROR_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        RoundedButton saveBtn = createRoundedIconButton("\uf0c7", "Enregistrer", SUCCESS_COLOR);
        saveBtn.addActionListener(e -> {
            if (titreField.getText().trim().isEmpty() ||
                codeField.getText().trim().isEmpty() ||
                reductionField.getText().trim().isEmpty() ||
                dateDebutField.getText().trim().isEmpty() ||
                dateFinField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Veuillez remplir tous les champs obligatoires");
                return;
            }
            
            try {
                offre.setTitre(titreField.getText().trim());
                offre.setDescription(descriptionArea.getText().trim());
                offre.setCodePromo(codeField.getText().trim().toUpperCase());
                
                double newReductionValue = Double.parseDouble(reductionField.getText());
                if (pourcentageBtn.isSelected()) {
                    offre.setReductionPourcentage(newReductionValue);
                    offre.setReductionFixe(0);
                } else {
                    offre.setReductionPourcentage(0);
                    offre.setReductionFixe(newReductionValue);
                }
                
                offre.setDateDebut(sdf.parse(dateDebutField.getText()));
                offre.setDateFin(sdf.parse(dateFinField.getText()));
                
                offre.setConditions(conditionsField.getText().trim());
                offre.setStatut((String) statutCombo.getSelectedItem());
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return offreService.updateOffre(offre);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                    " Offre modifiée avec succès!");
                                refreshOffresTable();
                                dialog.dispose();
                            } else {
                                JOptionPane.showMessageDialog(dialog, 
                                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                    " Erreur lors de la modification de l'offre");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, 
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Valeur de réduction invalide");
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                    " Format de date invalide. Utilisez AAAA-MM-JJ");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                    " Erreur: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);
        
        gbc.gridy = 10;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteOffre() {
        int selectedRow = offresTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                createIconLabel("\uf071", WARNING_COLOR, 16f).getText() + 
                " Veuillez sélectionner une offre à supprimer",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int offreId = (int) offresModel.getValueAt(selectedRow, 0);
        String titre = (String) offresModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            createIconLabel("\uf1f8", ERROR_COLOR, 16f).getText() + 
            " Voulez-vous vraiment supprimer cette offre?\n\n" +
            "ID: " + offreId + "\n" +
            "Titre: " + titre,
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return offreService.deleteOffre(offreId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                createIconLabel("\uf00c", SUCCESS_COLOR, 16f).getText() + 
                                " Offre supprimée avec succès!");
                            refreshOffresTable();
                        } else {
                            JOptionPane.showMessageDialog(AdminFrame.this,
                                createIconLabel("\uf00d", ERROR_COLOR, 16f).getText() + 
                                " Erreur lors de la suppression",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
    
    private Map<String, Integer> getDestinationsData(StatistiqueService service) {
        Map<String, Integer> destinations = new LinkedHashMap<>();
        
        try (java.sql.Connection conn = com.transport.dao.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            String query = "SELECT v.ville_arrivee, COUNT(r.id) as nombre_reservations " +
                          "FROM voyages v " +
                          "LEFT JOIN reservations r ON v.id = r.voyage_id " +
                          "GROUP BY v.ville_arrivee " +
                          "ORDER BY nombre_reservations DESC " +
                          "LIMIT 6";
            
            java.sql.ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                String destination = rs.getString("ville_arrivee");
                int count = rs.getInt("nombre_reservations");
                destinations.put(destination, count);
            }
            
            rs.close();
            
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return getDefaultDestinations();
        }
        
        return destinations;
    }

    private int[] calculatePercentagesBasedOnTotal(java.util.List<Integer> counts, int total) {
        int[] percentages = new int[counts.size()];
        
        if (total > 0) {
            for (int i = 0; i < counts.size(); i++) {
                percentages[i] = (int) Math.round((counts.get(i) * 100.0) / total);
            }
        }
        
        return percentages;
    }

    private void draw3DPieChart(Graphics2D g2d, java.util.List<String> names, int[] percentages, 
                               Color[] colors, int x, int y, int size) {
        
        int startAngle = 0;
        int shadowOffset = 3;
        
        g2d.setColor(new Color(200, 200, 200, 100));
        for (int i = 0; i < percentages.length; i++) {
            int arcAngle = (int) (percentages[i] * 3.6);
            Arc2D arc = new Arc2D.Double(x + shadowOffset, y + shadowOffset, 
                                        size, size, startAngle, arcAngle, Arc2D.PIE);
            g2d.fill(arc);
            startAngle += arcAngle;
        }
        
        startAngle = 0;
        for (int i = 0; i < percentages.length; i++) {
            int arcAngle = (int) (percentages[i] * 3.6);
            
            GradientPaint gradient = new GradientPaint(
                x, y, colors[i % colors.length],
                x + size, y + size, colors[i % colors.length].darker()
            );
            g2d.setPaint(gradient);
            
            Arc2D arc = new Arc2D.Double(x, y, size, size, startAngle, arcAngle, Arc2D.PIE);
            g2d.fill(arc);
            
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(arc);
            
            startAngle += arcAngle;
        }
        
        if (size > 100) {
            g2d.setColor(Color.WHITE);
            int donutSize = size / 3;
            int donutX = x + (size - donutSize) / 2;
            int donutY = y + (size - donutSize) / 2;
            g2d.fillOval(donutX, donutY, donutSize, donutSize);
            
            g2d.setColor(new Color(240, 240, 240));
            g2d.setStroke(new BasicStroke(1f));
            g2d.drawOval(donutX, donutY, donutSize, donutSize);
        }
    }

    private void drawLegendWithDetails(Graphics2D g2d, java.util.List<String> names, 
                                      java.util.List<Integer> counts, int[] percentages,
                                      Color[] colors, int x, int y, int maxHeight) {
        
        int itemHeight = 30;
        int availableHeight = maxHeight;
        
        if (names.size() * itemHeight > availableHeight) {
            itemHeight = availableHeight / names.size();
            if (itemHeight < 20) itemHeight = 20;
        }
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        for (int i = 0; i < names.size(); i++) {
            g2d.setColor(new Color(200, 200, 200, 100));
            g2d.fillRect(x + 1, y + 1, 16, 16);
            
            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(x, y, 15, 15);
            
            g2d.setColor(Color.WHITE);
            g2d.drawRect(x, y, 15, 15);
            
            g2d.setColor(Color.BLACK);
            String name = names.get(i);
            if (name.length() > 12) name = name.substring(0, 10) + "..";
            
            String mainText = String.format("%s (%d%%)", name, percentages[i]);
            g2d.drawString(mainText, x + 20, y + 12);
            
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String details = counts.get(i) + " réservation" + (counts.get(i) > 1 ? "s" : "");
            g2d.drawString(details, x + 20, y + 25);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            y += itemHeight;
        }
    }

    private void drawTotalInCenter(Graphics2D g2d, int total, int chartX, int chartY, int chartSize) {
        if (chartSize > 100) {
            g2d.setColor(new Color(255, 255, 255, 200));
            int circleSize = chartSize / 4;
            int circleX = chartX + (chartSize - circleSize) / 2;
            int circleY = chartY + (chartSize - circleSize) / 2;
            g2d.fillOval(circleX, circleY, circleSize, circleSize);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            String totalText = total + " réservations";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(totalText);
            int textX = chartX + (chartSize - textWidth) / 2;
            int textY = chartY + chartSize / 2 + fm.getHeight() / 4;
            g2d.drawString(totalText, textX, textY);
            
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String subText = "Total";
            int subTextWidth = g2d.getFontMetrics().stringWidth(subText);
            int subTextX = chartX + (chartSize - subTextWidth) / 2;
            int subTextY = textY - 15;
            g2d.drawString(subText, subTextX, subTextY);
        }
    }

    private void drawNoDataMessage(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(200, 200, 200));
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        
        String message = "Aucune donnée de destination disponible";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int textX = (width - textWidth) / 2;
        int textY = height / 2;
        
        g2d.drawString(message, textX, textY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 48));
        String icon = "\uf041";
        int iconWidth = g2d.getFontMetrics().stringWidth(icon);
        int iconX = (width - iconWidth) / 2;
        int iconY = textY - 40;
        g2d.drawString(icon, iconX, iconY);
    }

    private void adjustPercentages(int[] percentages) {
        if (percentages.length == 0) return;
        
        int sum = 0;
        for (int p : percentages) {
            sum += p;
        }
        
        if (sum != 100) {
            int diff = 100 - sum;
            int maxIndex = 0;
            for (int i = 1; i < percentages.length; i++) {
                if (percentages[i] > percentages[maxIndex]) {
                    maxIndex = i;
                }
            }
            percentages[maxIndex] += diff;
            
            if (percentages[maxIndex] < 0) {
                percentages[maxIndex] = 0;
            }
        }
    }

    private Map<String, Integer> getDefaultDestinations() {
        Map<String, Integer> defaults = new LinkedHashMap<>();
        defaults.put("Casablanca", 4);
        defaults.put("Marrakech", 3);
        defaults.put("Rabat", 2);
        defaults.put("Tanger", 2);
        defaults.put("Fès", 1);
        return defaults;
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            createIconLabel("\uf08b", ERROR_COLOR, 16f).getText() + 
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