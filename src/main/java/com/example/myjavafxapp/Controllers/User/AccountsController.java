package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Account;
import com.example.myjavafxapp.Models.CheckingAccount;
import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label ch_acc_num;
    public Label transaction_limit;
    public Label ch_acc_date;
    public Label ch_acc_bal;
    public Label sv_acc_num;
    public Label withdrawal_limit;
    public Label sv_acc_date;
    public Label sv_acc_bal;
    public TextField amount_to_sv;
    public Button trans_to_sv_btn;
    public TextField amount_to_ch;
    public Button trans_to_ch_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAccountData();
        setupTransferHandlers();
    }

    /**
     * Load account information from the database
     */
    private void loadAccountData() {
        // Refresh to get latest data
        Model.getInstance().refreshClientAccounts();

        Client client = Model.getInstance().getClient();

        // Load Checking Account Data
        Account checkingAccount = client.CheckingAccountProperty().get();
        if (checkingAccount != null) {
            ch_acc_num.setText(checkingAccount.AccountNumberProperty().get());
            ch_acc_bal.setText(String.format("%.2f MAD", checkingAccount.BalanceProperty().get()));

            if (checkingAccount instanceof CheckingAccount) {
                CheckingAccount chkAcc = (CheckingAccount) checkingAccount;
                transaction_limit.setText(String.valueOf(chkAcc.TransactionLimitProperty().get()));
            } else {
                transaction_limit.setText("N/A");
            }

            // Set date (you might want to store this in the database)
            if (client.DateCreatedProperty().get() != null) {
                ch_acc_date.setText(client.DateCreatedProperty().get()
                        .format(DateTimeFormatter.ISO_DATE));
            } else {
                ch_acc_date.setText("N/A");
            }
        } else {
            ch_acc_num.setText("No Checking Account");
            ch_acc_bal.setText("0.00 MAD");
            transaction_limit.setText("N/A");
            ch_acc_date.setText("N/A");
        }

        // Load Savings Account Data
        Account savingsAccount = client.SavingsAccountProperty().get();
        if (savingsAccount != null) {
            sv_acc_num.setText(savingsAccount.AccountNumberProperty().get());
            sv_acc_bal.setText(String.format("%.2f MAD", savingsAccount.BalanceProperty().get()));

            // Withdrawal limit (can be added to database if needed)
            withdrawal_limit.setText("2000.00 MAD");

            if (client.DateCreatedProperty().get() != null) {
                sv_acc_date.setText(client.DateCreatedProperty().get()
                        .format(DateTimeFormatter.ISO_DATE));
            } else {
                sv_acc_date.setText("N/A");
            }
        } else {
            sv_acc_num.setText("No Savings Account");
            sv_acc_bal.setText("0.00 MAD");
            withdrawal_limit.setText("N/A");
            sv_acc_date.setText("N/A");
        }
    }

    /**
     * Setup transfer button handlers
     */
    private void setupTransferHandlers() {
        trans_to_sv_btn.setOnAction(event -> onTransferToSavings());
        trans_to_ch_btn.setOnAction(event -> onTransferToChecking());
    }

    /**
     * Handle transfer from checking to savings
     */
    private void onTransferToSavings() {
        String amountText = amount_to_sv.getText().trim();

        if (amountText.isEmpty()) {
            showError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }

            // Perform transfer
            if (Model.getInstance().transferToSavings(amount)) {
                showSuccess("Successfully transferred " + String.format("%.2f", amount) +
                        " MAD from Checking to Savings");
                amount_to_sv.clear();
                loadAccountData(); // Refresh balances
            } else {
                String errorMsg = Model.getInstance().getLastErrorMessage();
                showError(errorMsg.isEmpty() ? "Transfer failed" : errorMsg);
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
        }
    }

    /**
     * Handle transfer from savings to checking
     */
    private void onTransferToChecking() {
        String amountText = amount_to_ch.getText().trim();

        if (amountText.isEmpty()) {
            showError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }

            // Perform transfer
            if (Model.getInstance().transferToChecking(amount)) {
                showSuccess("Successfully transferred " + String.format("%.2f", amount) +
                        " MAD from Savings to Checking");
                amount_to_ch.clear();
                loadAccountData(); // Refresh balances
            } else {
                String errorMsg = Model.getInstance().getLastErrorMessage();
                showError(errorMsg.isEmpty() ? "Transfer failed" : errorMsg);
            }

        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
        }
    }

    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Transfer Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success alert
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transfer Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refresh account data (called when returning to this view)
     */
    public void refreshAccountData() {
        loadAccountData();
    }
}