package bg.tuvarna.sit.wms.controllers;

import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.entities.base.BaseUser;
import bg.tuvarna.sit.wms.util.JpaUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.persistence.EntityManager;

public class RegistrationController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleBox;

    @FXML
    protected void handleRegistration() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String role = roleBox.getValue();

        // TODO: Add Validations

        EntityManager em = JpaUtil.getEntityManager();
        em.getTransaction().begin();
        
        BaseUser user = null;
        
        switch (role) {
            case "OWNER":
                user = new Owner();
                break;
            case "AGENT":
                user = new Agent();
                break;
            case "TENANT":
                user = new Tenant();
                break;
            default:
                break;
        }
        
        if (user != null) {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(password); // TODO: Hash the password
            user.setPhone(phone);
            em.persist(user);
        }
        
        em.getTransaction().commit();
        em.close();
        
        // TODO: LOG
    }
}
