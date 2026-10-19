package com.transport.ui.frames;

import com.transport.config.ThemeConfig;
import com.transport.models.User;
import com.transport.dao.UserDAO;
import com.transport.services.AuthService;
import com.transport.ui.components.RoundedButton;
import com.transport.ui.panels.LoginSidePanel;
import com.transport.ui.panels.RegistrationFormPanel;

import javax.swing.*;
import java.awt.*;

public class RegistrationFrame extends JFrame {
    private static final String APP_TITLE = "Transport+ - Inscription";
    private static final Dimension FRAME_SIZE = new Dimension(1000, 600);
    
    private final AuthService authService;
    private LoginSidePanel sidePanel;
    private RegistrationFormPanel formPanel;
    private RoundedButton registerButton;
    private RoundedButton backButton;
    
    public RegistrationFrame() {
        super(APP_TITLE);
        this.authService = new AuthService();
        
        initializeFrame();
        createComponents();
        arrangeComponents();
        wireUpEventHandlers();
        applyVisualTheme();
        
        setVisible(true);
    }
    
    private void initializeFrame() {
        setSize(FRAME_SIZE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 247, 250));
    }
    
    private void createComponents() {
        sidePanel = new LoginSidePanel();
        formPanel = new RegistrationFormPanel();
        
        registerButton = createRegisterButton();
        backButton = createBackButton();
    }
    
    private RoundedButton createRegisterButton() {
        RoundedButton button = new RoundedButton("S'inscrire");
        button.setBackgroundColor(ThemeConfig.PRIMARY_COLOR);
        button.setHoverColor(new Color(0, 82, 184));
        button.setPreferredSize(new Dimension(120, 40));
        button.setToolTipText("Cliquez pour créer votre compte");
        return button;
    }
    
    private RoundedButton createBackButton() {
        RoundedButton button = new RoundedButton("Retour");
        button.setBackgroundColor(new Color(245, 245, 245));
        button.setHoverColor(new Color(230, 230, 230));
        button.setTextColor(new Color(80, 80, 80));
        button.setPreferredSize(new Dimension(100, 40));
        button.setToolTipText("Retour à la page de connexion");
        return button;
    }
    
    private void arrangeComponents() {
        // Créer un conteneur principal
        JPanel mainContainer = createMainContainer();
        
        // Panel gauche : branding
        JPanel leftPanel = createLeftPanel();
        mainContainer.add(leftPanel, BorderLayout.WEST);
        
        // Séparateur
        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setForeground(new Color(220, 220, 220));
        separator.setPreferredSize(new Dimension(1, 500));
        mainContainer.add(separator, BorderLayout.CENTER);
        
        // Panel droite : formulaire
        JPanel rightPanel = createRightPanel();
        mainContainer.add(rightPanel, BorderLayout.CENTER);
        
        // Centrer le conteneur
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(245, 247, 250));
        centerWrapper.add(mainContainer);
        
        add(centerWrapper, BorderLayout.CENTER);
        
        // Définir le bouton par défaut
        getRootPane().setDefaultButton(registerButton);
    }
    
    private JPanel createMainContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(950, 550));
        panel.setBackground(Color.WHITE);
        
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        return panel;
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(420, 550));
        
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setBackground(Color.WHITE);
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddingPanel.add(sidePanel, BorderLayout.CENTER);
        
        panel.add(paddingPanel, BorderLayout.CENTER);
        
        // Footer
        panel.add(createCopyrightFooter(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(createActionButtonsPanel(), BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCopyrightFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(new Color(250, 250, 250));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(240, 240, 240)));
        
        JLabel copyright = new JLabel("© 2024 Transport+. Tous droits réservés.");
        copyright.setFont(new Font("Arial", Font.PLAIN, 11));
        copyright.setForeground(new Color(150, 150, 150));
        footer.add(copyright);
        
        return footer;
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        panel.setBackground(Color.WHITE);
        
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));
        
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonContainer.setBackground(Color.WHITE);
        
        buttonContainer.add(backButton);
        buttonContainer.add(registerButton);
        
        panel.add(buttonContainer);
        
        return panel;
    }
    
    private void wireUpEventHandlers() {
        // Inscription
        registerButton.addActionListener(e -> handleRegistration());
        
        // Retour
        backButton.addActionListener(e -> handleBack());
    }
    
    private void applyVisualTheme() {
        ThemeConfig.applyTheme();
    }
    
    // ===== GESTION DE L'INSCRIPTION =====
    private void handleRegistration() {
        // Validation des champs
        if (!formPanel.validateForm()) {
            return;
        }
        
        // Récupération des données
        var formData = formPanel.getFormData();
        
        // Indication visuelle de chargement
        showLoadingState(true);
        
        // Tentative d'inscription
        attemptRegistration(formData);
    }
    
    private void showLoadingState(boolean isLoading) {
        registerButton.setEnabled(!isLoading);
        registerButton.setText(isLoading ? "Inscription en cours..." : "S'inscrire");
        registerButton.setBackgroundColor(isLoading ? 
            new Color(180, 180, 180) : ThemeConfig.PRIMARY_COLOR);
    }
    
    private void attemptRegistration(RegistrationFormPanel.FormData formData) {
        Timer networkDelay = new Timer(800, e -> {
            processRegistrationResult(formData);
        });
        networkDelay.setRepeats(false);
        networkDelay.start();
    }
    
    private void processRegistrationResult(RegistrationFormPanel.FormData formData) {
        // Vérifier que les mots de passe correspondent
        if (!formData.password().equals(formData.confirmPassword())) {
            JOptionPane.showMessageDialog(this,
                "Les mots de passe ne correspondent pas.",
                "Erreur d'inscription",
                JOptionPane.ERROR_MESSAGE);
            showLoadingState(false);
            return;
        }
        
        // Vérifier si l'email existe déjà
        if (authService.userExists(formData.email())) {
            JOptionPane.showMessageDialog(this,
                "Cet email est déjà utilisé. Veuillez en choisir un autre.",
                "Email existant",
                JOptionPane.WARNING_MESSAGE);
            showLoadingState(false);
            return;
        }
        
        // Créer l'utilisateur
        User.UserRole role = "Client".equals(formData.userType()) ? 
            User.UserRole.CLIENT : User.UserRole.ADMIN;
        
        boolean success = authService.registerUser(
            formData.firstName(),
            formData.lastName(),
            formData.email(),
            formData.phone(),
            formData.password(),
            role
        );
        
        if (success) {
            handleRegistrationSuccess(formData);
        } else {
            handleRegistrationFailure();
        }
    }
    
    private void handleRegistrationSuccess(RegistrationFormPanel.FormData formData) {
        // Message de succès
        JOptionPane.showMessageDialog(this,
            String.format("Félicitations %s %s !\n\nVotre compte a été créé avec succès.\n" +
                         "Vous pouvez maintenant vous connecter avec vos identifiants.",
                         formData.firstName(), formData.lastName()),
            "Inscription réussie",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Fermer cette fenêtre
        dispose();
        
        // Ouvrir la fenêtre de connexion
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
    
    private void handleRegistrationFailure() {
        JOptionPane.showMessageDialog(this,
            "Une erreur est survenue lors de l'inscription.\n" +
            "Veuillez réessayer plus tard.",
            "Échec de l'inscription",
            JOptionPane.ERROR_MESSAGE);
        
        showLoadingState(false);
    }
    
    // ===== NAVIGATION =====
    private void handleBack() {
        // Fermer cette fenêtre
        dispose();
        
        // Ouvrir la fenêtre de connexion
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
    
    // ===== MAIN (pour test) =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new RegistrationFrame();
        });
    }
}