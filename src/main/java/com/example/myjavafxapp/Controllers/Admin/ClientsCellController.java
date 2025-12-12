package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Account;
import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientsCellController implements Initializable {

    public Label name_lbl;
    public Label lastname_lbl;
    public Label ch_account_lbl;
    public Label sv_account_lbl;
    public Label date_lbl;
    public Label payeeAddress_lbl;
    public Button dlete_btn;

    private final Client client;
    private final Runnable onDeleteCallback;

    public ClientsCellController(Client client) {
        this.client = client;
        this.onDeleteCallback = null;
    }

    public ClientsCellController(Client client, Runnable onDeleteCallback) {
        this.client = client;
        this.onDeleteCallback = onDeleteCallback;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (client != null) {
            // Set name labels
            name_lbl.setText(client.FirstNameProperty().get() != null ? client.FirstNameProperty().get() : "");
            lastname_lbl.setText(client.LastNameProperty().get() != null ? client.LastNameProperty().get() : "");

            // Set PayeeAddress
            payeeAddress_lbl.setText(client.PayeeAddressProperty().get() != null ? client.PayeeAddressProperty().get() : "");

            // Set checking account number
            Account checkingAccount = client.CheckingAccountProperty().get();
            if (checkingAccount != null && checkingAccount.AccountNumberProperty().get() != null) {
                ch_account_lbl.setText(checkingAccount.AccountNumberProperty().get());
            } else {
                ch_account_lbl.setText("N/A");
            }

            // Set savings account number
            Account savingsAccount = client.SavingsAccountProperty().get();
            if (savingsAccount != null && savingsAccount.AccountNumberProperty().get() != null) {
                sv_account_lbl.setText(savingsAccount.AccountNumberProperty().get());
            } else {
                sv_account_lbl.setText("N/A");
            }

            // Set date
            if (client.DateCreatedProperty().get() != null) {
                date_lbl.setText(client.DateCreatedProperty().get().format(DateTimeFormatter.ISO_DATE));
            } else {
                date_lbl.setText("N/A");
            }

            // Set up delete button action
            if (dlete_btn != null) {
                dlete_btn.setOnAction(event -> onDeleteClient());
            }
        }
    }

    /**
     * Handle delete button click
     */
    private void onDeleteClient() {
        if (client == null) {
            return;
        }

        String clientName = client.FirstNameProperty().get() + " " + client.LastNameProperty().get();
        String payeeAddress = client.PayeeAddressProperty().get();

        // Show confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Client");
        confirmationAlert.setHeaderText("Confirm Deletion");
        confirmationAlert.setContentText("Are you sure you want to delete client:\n" + clientName + "\n(" + payeeAddress + ")?\n\nThis will also delete all associated accounts. This action cannot be undone.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete client
            boolean success = Model.getInstance().deleteClient(payeeAddress);

            if (success) {
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText("Client Deleted");
                successAlert.setContentText("Client " + clientName + " has been successfully deleted.");
                successAlert.showAndWait();

                // Refresh the list
                if (onDeleteCallback != null) {
                    onDeleteCallback.run();
                }
            } else {
                // Show error message
                String errorMessage = Model.getInstance().getLastErrorMessage();
                if (errorMessage.isEmpty()) {
                    errorMessage = "Failed to delete client. Please try again.";
                }

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Delete Failed");
                errorAlert.setContentText(errorMessage);
                errorAlert.showAndWait();
            }
        }
    }
}
