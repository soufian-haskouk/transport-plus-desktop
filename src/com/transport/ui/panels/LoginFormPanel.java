package com.transport.ui.panels;

import com.transport.config.ThemeConfig;
import com.transport.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class LoginFormPanel extends JPanel {
    // Components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox showPasswordCheckbox;
    private JButton forgotPasswordButton;
    
    // Design constants
    private static final int FORM_PADDING = 40;
    private static final int COMPONENT_SPACING = 10;
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color TITLE_COLOR = ThemeConfig.PRIMARY_COLOR;
    private static final Color SUBTITLE_COLOR = Color.GRAY;
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    
    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font LINK_FONT = new Font("Arial", Font.PLAIN, 11);
    
    public LoginFormPanel() {
        setupAppearance();
        buildForm();
        positionComponents();
    }
    
    // ===== APPEARANCE SETUP =====
    private void setupAppearance() {
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(FORM_PADDING, FORM_PADDING, FORM_PADDING, FORM_PADDING));
        setLayout(new GridBagLayout());
    }
    
    // ===== FORM BUILDING =====
    private void buildForm() {
        createRoleSelector();
        createEmailInput();
        createPasswordInput();
        createForgotPasswordLink();
    }
    
    private void createRoleSelector() {
        String[] userTypes = {"Client", "Administrateur"};
        roleComboBox = new JComboBox<>(userTypes);
        styleInputComponent(roleComboBox);
        roleComboBox.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }
    
    private void createEmailInput() {
        emailField = new JTextField(20);
        styleInputComponent(emailField);
        emailField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }
    
    private void createPasswordInput() {
        passwordField = new JPasswordField(20);
        styleInputComponent(passwordField);
        passwordField.setEchoChar('•');
        passwordField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        showPasswordCheckbox = new JCheckBox("Afficher");
        styleCheckboxComponent(showPasswordCheckbox);
        showPasswordCheckbox.addActionListener(e -> togglePasswordDisplay());
    }
    
    private void createForgotPasswordLink() {
        forgotPasswordButton = new JButton("Mot de passe oublié ?");
        styleLinkButton(forgotPasswordButton);
    }
    
    // ===== COMPONENT STYLING =====
    private void styleInputComponent(JComponent component) {
        component.setFont(INPUT_FONT);
        component.setBackground(BACKGROUND);
        component.setPreferredSize(new Dimension(250, 35));
    }
    
    private void styleCheckboxComponent(JCheckBox checkbox) {
        checkbox.setFont(new Font("Arial", Font.PLAIN, 12));
        checkbox.setBackground(BACKGROUND);
        checkbox.setFocusPainted(false);
    }
    
    private void styleLinkButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(ThemeConfig.SECONDARY_COLOR);
        button.setFont(LINK_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    }
    
    private void togglePasswordDisplay() {
        char echoChar = showPasswordCheckbox.isSelected() ? (char) 0 : '•';
        passwordField.setEchoChar(echoChar);
    }
    
    // ===== LAYOUT MANAGEMENT =====
    private void positionComponents() {
        GridBagConstraints gbc = createLayoutConstraints();
        
        addFormHeader(gbc);
        addUserTypeSelection(gbc);
        addEmailField(gbc);
        addPasswordField(gbc);
        addPasswordHelper(gbc);
    }
    
    private GridBagConstraints createLayoutConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(COMPONENT_SPACING, COMPONENT_SPACING, COMPONENT_SPACING, COMPONENT_SPACING);
        return gbc;
    }
    
    private void addFormHeader(GridBagConstraints gbc) {
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(createFormTitle(), gbc);
        
        // Subtitle
        gbc.gridy = 1;
        gbc.insets = new Insets(0, COMPONENT_SPACING, 30, COMPONENT_SPACING);
        add(createFormSubtitle(), gbc);
        
        // Reset for other components
        gbc.insets = new Insets(COMPONENT_SPACING, COMPONENT_SPACING, COMPONENT_SPACING, COMPONENT_SPACING);
        gbc.gridwidth = 1;
    }
    
    private JLabel createFormTitle() {
        JLabel label = new JLabel("Connexion", SwingConstants.CENTER);
        label.setFont(TITLE_FONT);
        label.setForeground(TITLE_COLOR);
        return label;
    }
    
    private JLabel createFormSubtitle() {
        JLabel label = new JLabel("Accédez à votre espace", SwingConstants.CENTER);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(SUBTITLE_COLOR);
        return label;
    }
    
    private void addUserTypeSelection(GridBagConstraints gbc) {
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(createFieldLabel("Je suis :"), gbc);
        
        gbc.gridx = 1;
        add(roleComboBox, gbc);
    }
    
    private void addEmailField(GridBagConstraints gbc) {
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(createFieldLabel("Email :"), gbc);
        
        gbc.gridx = 1;
        add(emailField, gbc);
    }
    
    private void addPasswordField(GridBagConstraints gbc) {
        gbc.gridy = 4;
        gbc.gridx = 0;
        add(createFieldLabel("Mot de passe :"), gbc);
        
        gbc.gridx = 1;
        add(createPasswordFieldWithToggle(), gbc);
    }
    
    private void addPasswordHelper(GridBagConstraints gbc) {
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(forgotPasswordButton, gbc);
    }
    
    // ===== HELPER METHODS =====
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }
    
    private JPanel createPasswordFieldWithToggle() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(BACKGROUND);
        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(showPasswordCheckbox, BorderLayout.EAST);
        return panel;
    }
    
    // ===== DATA ACCESS =====
    public String getEmail() {
        return emailField.getText().trim();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public String getUserType() {
        return (String) roleComboBox.getSelectedItem();
    }
    
    // Compatibilité avec l'ancien code
    public String getSelectedRole() {
        return getUserType();
    }
    
    public FormData getFormData() {
        return new FormData(getEmail(), getPassword(), getUserType());
    }
    
    // ===== VALIDATION =====
    public boolean validateForm() {
        return checkForEmptyFields() && validateEmailFormat();
    }
    
    // Compatibilité avec l'ancien code
    public boolean isInputValid() {
        return validateForm();
    }
    
    public boolean validateInput() {
        return validateForm();
    }
    
    private boolean checkForEmptyFields() {
        if (getEmail().isEmpty() || getPassword().isEmpty()) {
            showAlert("Veuillez remplir tous les champs", "Champs manquants");
            return false;
        }
        return true;
    }
    
    private boolean validateEmailFormat() {
        if (!ValidationUtils.isValidEmail(getEmail())) {
            showAlert("Format d'email invalide", "Email invalide");
            return false;
        }
        return true;
    }
    
    private void showAlert(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    // ===== FORM CONTROL =====
    public void resetForm() {
        emailField.setText("");
        passwordField.setText("");
        showPasswordCheckbox.setSelected(false);
        passwordField.setEchoChar('•');
    }
    
    // Compatibilité avec l'ancien code
    public void clearFields() {
        resetForm();
    }
    
    public void clearForm() {
        resetForm();
    }
    
    public void focusEmailField() {
        emailField.requestFocusInWindow();
    }
    
    // Compatibilité avec l'ancien code
    public void setFocusOnEmailField() {
        focusEmailField();
    }
    
    // ===== EVENT HANDLING =====
    public void onUserTypeChange(ItemListener listener) {
        roleComboBox.addItemListener(listener);
    }
    
    // Compatibilité avec l'ancien code
    public void addRoleChangeListener(ItemListener listener) {
        onUserTypeChange(listener);
    }
    
    public void onForgotPasswordClick(ActionListener listener) {
        forgotPasswordButton.addActionListener(listener);
    }
    
    // Compatibilité avec l'ancien code
    public void addForgotPasswordListener(ActionListener listener) {
        onForgotPasswordClick(listener);
    }
    
    // ===== DATA TRANSFER OBJECT =====
    public record FormData(String email, String password, String userType) {
        public boolean isComplete() {
            return email != null && !email.trim().isEmpty() &&
                   password != null && !password.trim().isEmpty();
        }
    }
    
    // Pour compatibilité avec LoginFrame existant
    public Credentials getCredentials() {
        return new Credentials(getEmail(), getPassword(), getUserType());
    }
    
    public record Credentials(String email, String password, String role) {
        public boolean hasEmptyFields() {
            return email == null || email.trim().isEmpty() || 
                   password == null || password.trim().isEmpty();
        }
    }
}