package com.transport.ui.frames;

import com.transport.config.ThemeConfig;
import com.transport.models.User;
import com.transport.services.AuthService;
import com.transport.ui.components.RoundedButton;
import com.transport.ui.panels.LoginFormPanel;
import com.transport.ui.panels.LoginSidePanel;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final String APP_TITLE = "Transport+ - Connexion";
    private static final Dimension FRAME_SIZE = new Dimension(900, 500);
    
    private final AuthService authService;
    private LoginSidePanel sidePanel;
    private LoginFormPanel formPanel;
    private RoundedButton loginButton;
    private RoundedButton registerButton;
    
    public LoginFrame() {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(0, 0)); // Pas d'espacement entre les composants
    }
    
    private void createComponents() {
        sidePanel = new LoginSidePanel();
        formPanel = new LoginFormPanel();
        
        loginButton = createLoginButton();
        registerButton = createRegisterButton();
    }
    
    private RoundedButton createLoginButton() {
        RoundedButton button = new RoundedButton("Se connecter");
        button.setToolTipText("Cliquez pour vous connecter à votre compte");
        return button;
    }
    
    private RoundedButton createRegisterButton() {
        RoundedButton button = new RoundedButton("Créer un compte", 
            Color.WHITE, 
            Color.LIGHT_GRAY,
            ThemeConfig.PRIMARY_COLOR);
        button.setToolTipText("Cliquez pour créer un nouveau compte");
        return button;
    }
    
    private void arrangeComponents() {
        // Créer un conteneur principal avec deux panneaux côte à côte
        JPanel mainContainer = new JPanel(new BorderLayout());
        
        // Panel gauche : branding (largeur fixe)
        sidePanel.setPreferredSize(new Dimension(400, 500));
        mainContainer.add(sidePanel, BorderLayout.WEST);
        
        // Panel droite : formulaire (prend tout l'espace restant)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(createActionButtonsPanel(), BorderLayout.SOUTH);
        
        mainContainer.add(rightPanel, BorderLayout.CENTER);
        
        // Ajouter le conteneur principal à la fenêtre
        add(mainContainer, BorderLayout.CENTER);
        
        // Définir le bouton par défaut (Entrée déclenche la connexion)
        getRootPane().setDefaultButton(loginButton);
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, 
            new Color(230, 230, 230)));
        
        panel.add(registerButton);
        panel.add(loginButton);
        
        return panel;
    }
    
    private void wireUpEventHandlers() {
        // Connexion avec Entrée ou clic
        loginButton.addActionListener(e -> handleLogin());
        
                // Inscription
        registerButton.addActionListener(e -> handleRegistration());
        
        // Mot de passe oublié
        formPanel.onForgotPasswordClick(e -> handlePasswordRecovery());
    }
    
    private void applyVisualTheme() {
        ThemeConfig.applyTheme();
    }
    
    // ===== GESTION DE LA CONNEXION =====
    private void handleLogin() {
        // Validation des champs
        if (!formPanel.validateForm()) {
            return;
        }
        
        // Récupération des données
        String email = formPanel.getEmail();
        String password = formPanel.getPassword();
        String userType = formPanel.getUserType();
        
        // Conversion du type d'utilisateur
        User.UserRole role = convertUserTypeToRole(userType);
        
        // Indication visuelle de chargement
        showLoadingState(true);
        
        // Tentative de connexion (avec délai simulé)
        attemptLogin(email, password, role);
    }
    
    private User.UserRole convertUserTypeToRole(String userType) {
        return "Client".equals(userType) ? 
            User.UserRole.CLIENT : User.UserRole.ADMIN;
    }
    
    private void showLoadingState(boolean isLoading) {
        loginButton.setEnabled(!isLoading);
        loginButton.setText(isLoading ? "Connexion en cours..." : "Se connecter");
    }
    
    private void attemptLogin(String email, String password, User.UserRole role) {
        // Simulation d'un délai réseau (à remplacer par un appel réel asynchrone)
        Timer networkDelay = new Timer(800, e -> {
            processLoginResult(email, password, role);
        });
        networkDelay.setRepeats(false);
        networkDelay.start();
    }
    
    private void processLoginResult(String email, String password, User.UserRole role) {
        var userOptional = authService.login(email, password, role);
        
        if (userOptional.isPresent()) {
            handleLoginSuccess(userOptional.get(), role);
        } else {
            handleLoginFailure();
        }
    }
    
    private void handleLoginSuccess(User user, User.UserRole role) {
        // Fermer la fenêtre de connexion
        dispose();
        
        // Ouvrir l'interface appropriée
        openUserInterface(user, role);
        
        // Message de bienvenue
        showWelcomeMessage(user);
    }
    
    private void handleLoginFailure() {
        JOptionPane.showMessageDialog(this,
            "Identifiants incorrects ou type d'utilisateur invalide.\n" +
            "Veuillez vérifier vos informations.",
            "Échec de la connexion",
            JOptionPane.ERROR_MESSAGE);
        
        // Réactiver le bouton
        showLoadingState(false);
        
        // Focus sur le champ email pour correction
        formPanel.focusEmailField();
    }
    
    // ===== GESTION DE L'INSCRIPTION =====
    private void handleRegistration() {
        // Fermer la fenêtre de connexion
        dispose();
        
        // Ouvrir la fenêtre d'inscription
        SwingUtilities.invokeLater(() -> new RegistrationFrame());
    }
    
    // ===== RÉCUPÉRATION DE MOT DE PASSE =====
    private void handlePasswordRecovery() {
        String email = JOptionPane.showInputDialog(this,
            "Entrez votre adresse email pour réinitialiser votre mot de passe :",
            "Mot de passe oublié",
            JOptionPane.QUESTION_MESSAGE);
        
        if (email != null && !email.trim().isEmpty()) {
            // Simulation d'envoi d'email
            JOptionPane.showMessageDialog(this,
                "Un email de réinitialisation a été envoyé à :\n" +
                email + "\n\n" +
                "Veuillez vérifier votre boîte de réception.",
                "Email envoyé",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // ===== NAVIGATION =====
    private void openUserInterface(User user, User.UserRole role) {
        SwingUtilities.invokeLater(() -> {
            switch (role) {
                case CLIENT -> openClientDashboard(user);
                case ADMIN -> openAdminDashboard(user);
            }
        });
    }
    
    private void openClientDashboard(User user) {
        new ClientFrame(user).setVisible(true);
    }
    
    private void openAdminDashboard(User user) {
        new AdminFrame(user).setVisible(true);
    }
    
    private void showWelcomeMessage(User user) {
        JOptionPane.showMessageDialog(null,
            String.format("Bienvenue %s !\n\nConnexion réussie.", user.getFullName()),
            "Connexion réussie",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ===== UTILITAIRES =====
    public void resetForm() {
        formPanel.resetForm();
        showLoadingState(false);
    }
    
    public void focusOnForm() {
        formPanel.focusEmailField();
    }
    
    // ===== MAIN (pour test) =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame();
        });
    }
}