package com.transport.ui.panels;

import com.transport.config.ThemeConfig;
import com.transport.utils.FontAwesomeLoader;
import com.transport.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegistrationFormPanel extends JPanel {
    // Components
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JCheckBox showPasswordCheckbox;
    private JCheckBox showConfirmPasswordCheckbox;
    private JCheckBox termsCheckbox;
    private JLabel termsLinkLabel;
    
    // Design constants
    private static final int FORM_PADDING = 20;
    private static final int COMPONENT_SPACING = 8;
    private static final Color BACKGROUND = Color.WHITE;
    private static final Color TITLE_COLOR = ThemeConfig.PRIMARY_COLOR;
    private static final Color SUBTITLE_COLOR = new Color(100, 100, 100);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color FOCUS_COLOR = ThemeConfig.PRIMARY_COLOR;
    private static final Color LINK_COLOR = ThemeConfig.SECONDARY_COLOR;
    private static final Color ERROR_COLOR = new Color(220, 0, 0);
    
    // Font constants
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 11);
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 13);
    private static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final Font LINK_FONT = new Font("Arial", Font.PLAIN, 10);
    
    public RegistrationFormPanel() {
        setupAppearance();
        buildForm();
        positionComponents();
    }
    
    // ===== APPEARANCE SETUP =====
    private void setupAppearance() {
        setBackground(BACKGROUND);
        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(FORM_PADDING, FORM_PADDING, FORM_PADDING, FORM_PADDING));
    }
    
    // ===== FORM BUILDING =====
    private void buildForm() {
        createPersonalInfoFields();
        createContactFields();
        createSecurityFields();
        createTermsSection();
    }
    
    private void createPersonalInfoFields() {
        firstNameField = createTextField();
        lastNameField = createTextField();
        
        String[] userTypes = {"Client", "Administrateur"};
        roleComboBox = new JComboBox<>(userTypes);
        styleInputComponent(roleComboBox);
        roleComboBox.setBorder(createInputBorder());
    }
    
    private void createContactFields() {
        emailField = createTextField();
        phoneField = createTextField();
    }
    
    private void createSecurityFields() {
        passwordField = new JPasswordField(20);
        styleInputComponent(passwordField);
        passwordField.setEchoChar('•');
        passwordField.setBorder(createInputBorder());
        
        confirmPasswordField = new JPasswordField(20);
        styleInputComponent(confirmPasswordField);
        confirmPasswordField.setEchoChar('•');
        confirmPasswordField.setBorder(createInputBorder());
        
        showPasswordCheckbox = createCheckbox("Afficher", e -> togglePasswordDisplay(passwordField, showPasswordCheckbox));
        showConfirmPasswordCheckbox = createCheckbox("Afficher", e -> togglePasswordDisplay(confirmPasswordField, showConfirmPasswordCheckbox));
    }
    
    private void createTermsSection() {
        termsCheckbox = new JCheckBox();
        termsCheckbox.setBackground(BACKGROUND);
        termsCheckbox.setFocusPainted(false);
        termsCheckbox.setPreferredSize(new Dimension(15, 15));
        
        termsLinkLabel = new JLabel("conditions générales d'utilisation");
        termsLinkLabel.setFont(LINK_FONT);
        termsLinkLabel.setForeground(LINK_COLOR);
        termsLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        termsLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
           
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                termsLinkLabel.setForeground(LINK_COLOR);
                termsLinkLabel.setText("conditions générales d'utilisation");
            }
        });
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        styleInputComponent(field);
        field.setBorder(createInputBorder());
        return field;
    }
    
    private JCheckBox createCheckbox(String text, ActionListener listener) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(new Font("Arial", Font.PLAIN, 11));
        checkbox.setBackground(BACKGROUND);
        checkbox.setFocusPainted(false);
        checkbox.addActionListener(listener);
        return checkbox;
    }
    
    private javax.swing.border.Border createInputBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }
    
    // ===== COMPONENT STYLING =====
    private void styleInputComponent(JComponent component) {
        component.setFont(INPUT_FONT);
        component.setBackground(BACKGROUND);
        component.setPreferredSize(new Dimension(200, 32));
    }
    
    private void togglePasswordDisplay(JPasswordField passwordField, JCheckBox checkbox) {
        char echoChar = checkbox.isSelected() ? (char) 0 : '•';
        passwordField.setEchoChar(echoChar);
    }
    
    // ===== LAYOUT MANAGEMENT =====
    private void positionComponents() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND);
        add(formPanel, BorderLayout.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 8, 4, 8);
        
        // Row 0-1: Title already added in header
        
        // Row 2: First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Nom :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(firstNameField, gbc);
        
        // Row 3: Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Prénom :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(lastNameField, gbc);
        
        // Row 4: User Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Je suis :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(roleComboBox, gbc);
        
        // Row 5: Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Email :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(emailField, gbc);
        
        // Row 6: Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Téléphone :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(phoneField, gbc);
        
        // Row 7: Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Mot de passe :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(createPasswordFieldWithToggle(passwordField, showPasswordCheckbox), gbc);
        
        // Row 8: Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel("Confirmer :"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(createPasswordFieldWithToggle(confirmPasswordField, showConfirmPasswordCheckbox), gbc);
        
        // Row 9: Terms and Conditions
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(createFieldLabel(""), gbc); // Empty label for alignment
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(createTermsPanel(), gbc);
        
        // Footer spacer
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Inscription");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Rejoignez notre communauté de voyageurs");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(SUBTITLE_COLOR);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        return headerPanel;
    }
    
    // ===== HELPER METHODS =====
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(60, 60, 60));
        return label;
    }
    
    private JPanel createPasswordFieldWithToggle(JPasswordField passwordField, JCheckBox checkbox) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(BACKGROUND);
        panel.add(passwordField, BorderLayout.CENTER);
        
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(BACKGROUND);
        checkboxPanel.add(checkbox);
        panel.add(checkboxPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTermsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        panel.setBackground(BACKGROUND);
        
        JLabel staticText = new JLabel("J'accepte les ");
        staticText.setFont(SMALL_FONT);
        staticText.setForeground(new Color(80, 80, 80));
        
        panel.add(termsCheckbox);
        panel.add(staticText);
        panel.add(termsLinkLabel);
        
        return panel;
    }
        
    // ===== DATA ACCESS =====
    public FormData getFormData() {
        return new FormData(
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            new String(passwordField.getPassword()),
            new String(confirmPasswordField.getPassword()),
            (String) roleComboBox.getSelectedItem()
        );
    }
    
    // ===== VALIDATION =====
    public boolean validateForm() {
        return checkForEmptyFields() && 
               validateEmailFormat() && 
               validatePhoneFormat() &&
               validatePasswordStrength() &&
               validateTermsAccepted();
    }
    
    private boolean checkForEmptyFields() {
        FormData data = getFormData();
        
        if (data.firstName().isEmpty() || data.lastName().isEmpty() || 
            data.email().isEmpty() || data.phone().isEmpty() ||
            data.password().isEmpty() || data.confirmPassword().isEmpty()) {
            
            showAlert("Veuillez remplir tous les champs obligatoires", "Champs manquants");
            return false;
        }
        return true;
    }
    
    private boolean validateEmailFormat() {
        if (!ValidationUtils.isValidEmail(getFormData().email())) {
            showAlert("Format d'email invalide (ex: utilisateur@domaine.com)", "Email invalide");
            emailField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
            return false;
        }
        emailField.setBorder(createInputBorder());
        return true;
    }
    
    private boolean validatePhoneFormat() {
        if (!ValidationUtils.isValidPhone(getFormData().phone())) {
            showAlert("Format de téléphone invalide (10 chiffres requis)", "Téléphone invalide");
            phoneField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
            return false;
        }
        phoneField.setBorder(createInputBorder());
        return true;
    }
    
    private boolean validatePasswordStrength() {
        String password = getFormData().password();
        if (password.length() < 8) {
            showAlert("Le mot de passe doit contenir au moins 8 caractères", "Mot de passe faible");
            passwordField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
            return false;
        }
        passwordField.setBorder(createInputBorder());
        return true;
    }
    
    private boolean validateTermsAccepted() {
        if (!termsCheckbox.isSelected()) {
            showAlert("Vous devez accepter les conditions générales d'utilisation.", 
                     "Conditions non acceptées");
            return false;
        }
        return true;
    }
    
    private void showAlert(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    // ===== FORM CONTROL =====
    public void resetForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        showPasswordCheckbox.setSelected(false);
        showConfirmPasswordCheckbox.setSelected(false);
        termsCheckbox.setSelected(false);
        passwordField.setEchoChar('•');
        confirmPasswordField.setEchoChar('•');
        
        // Reset borders
        firstNameField.setBorder(createInputBorder());
        lastNameField.setBorder(createInputBorder());
        emailField.setBorder(createInputBorder());
        phoneField.setBorder(createInputBorder());
        passwordField.setBorder(createInputBorder());
        confirmPasswordField.setBorder(createInputBorder());
        
        firstNameField.requestFocusInWindow();
    }
    
    // ===== DATA TRANSFER OBJECT =====
    public record FormData(
        String firstName,
        String lastName,
        String email,
        String phone,
        String password,
        String confirmPassword,
        String userType
    ) {}
}