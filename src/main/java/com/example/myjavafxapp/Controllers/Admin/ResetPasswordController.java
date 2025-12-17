package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ResetPasswordController implements Initializable {

    public TextField search_field;
    public Button search_btn;
    public Label search_result_lbl;

    public VBox user_info_section;
    public Label first_name_lbl;
    public Label last_name_lbl;
    public Label payee_address_lbl;
    public Label date_created_lbl;

    public VBox reset_section;
    public PasswordField new_password_field;
    public PasswordField confirm_password_field;
    public Button reset_password_btn;
    public Button cancel_btn;
    public Label reset_result_lbl;

    private String currentPayeeAddress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupHandlers();
        hideUserSections();
    }

    private void setupHandlers() {
        search_btn.setOnAction(event -> onSearch());
        reset_password_btn.setOnAction(event -> onResetPassword());
        cancel_btn.setOnAction(event -> onCancel());

        // Clear results when typing
        search_field.textProperty().addListener((obs, oldVal, newVal) -> {
            hideUserSections();
            search_result_lbl.setText("");
        });
    }

    private void onSearch() {
        String payeeAddress = search_field.getText().trim();

        if (payeeAddress.isEmpty()) {
            showSearchError("Please enter a Payee Address");
            return;
        }

        // Search for client
        boolean exists = Model.getInstance().SearchClient(payeeAddress);

        if (exists) {
            List<Client> clients = Model.getInstance().getserachedClient(payeeAddress);

            if (!clients.isEmpty()) {
                Client client = clients.get(0);
                displayUserInfo(client);
                showSearchSuccess("User found!");
                currentPayeeAddress = payeeAddress;
            } else {
                showSearchError("User not found");
                hideUserSections();
            }
        } else {
            String errorMsg = Model.getInstance().getLastErrorMessageSearch();
            showSearchError(errorMsg.isEmpty() ? "User not found" : errorMsg);
            hideUserSections();
        }
    }

    private void displayUserInfo(Client client) {
        // Show sections
        user_info_section.setVisible(true);
        user_info_section.setManaged(true);
        reset_section.setVisible(true);
        reset_section.setManaged(true);

        // Display user data
        first_name_lbl.setText(client.FirstNameProperty().get());
        last_name_lbl.setText(client.LastNameProperty().get());
        payee_address_lbl.setText(client.PayeeAddressProperty().get());

        if (client.DateCreatedProperty().get() != null) {
            date_created_lbl.setText(client.DateCreatedProperty().get()
                    .format(DateTimeFormatter.ISO_DATE));
        } else {
            date_created_lbl.setText("N/A");
        }

        // Clear password fields
        new_password_field.clear();
        confirm_password_field.clear();
        reset_result_lbl.setText("");
    }

    private void onResetPassword() {
        String newPassword = new_password_field.getText();
        String confirmPassword = confirm_password_field.getText();

        // Validate inputs
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showResetError("Please fill in all password fields");
            return;
        }

        if (newPassword.length() < 4) {
            showResetError("Password must be at least 4 characters long");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showResetError("Passwords do not match");
            return;
        }

        // Reset password
        boolean success = Model.getInstance().resetUserPassword(currentPayeeAddress, newPassword);

        if (success) {
            showResetSuccess("✓ Password reset successfully!");

            // Clear form after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> {
                        onCancel();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            String errorMsg = Model.getInstance().getLastErrorMessage();
            showResetError(errorMsg.isEmpty() ? "Failed to reset password" : errorMsg);
        }
    }

    private void onCancel() {
        search_field.clear();
        new_password_field.clear();
        confirm_password_field.clear();
        hideUserSections();
        search_result_lbl.setText("");
        reset_result_lbl.setText("");
        currentPayeeAddress = null;
    }

    private void hideUserSections() {
        user_info_section.setVisible(false);
        user_info_section.setManaged(false);
        reset_section.setVisible(false);
        reset_section.setManaged(false);
    }

    private void showSearchError(String message) {
        search_result_lbl.setText(message);
        search_result_lbl.setTextFill(Color.web("#dc2626"));
    }

    private void showSearchSuccess(String message) {
        search_result_lbl.setText(message);
        search_result_lbl.setTextFill(Color.web("#10b981"));
    }

    private void showResetError(String message) {
        reset_result_lbl.setText(message);
        reset_result_lbl.setTextFill(Color.web("#dc2626"));
    }

    private void showResetSuccess(String message) {
        reset_result_lbl.setText(message);
        reset_result_lbl.setTextFill(Color.web("#10b981"));
    }
}