package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Account;
import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Models.Transaction;
import com.example.myjavafxapp.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    public Text user_name;
    public Label date_label;
    public Label checking_balance;
    public Label checking_acc_num;
    public Label savings_balance;
    public Label savings_acc_num;
    public Label income_label;
    public Label expenses_label;
    public ListView<Transaction> transaction_listview;
    public TextField payee_field;
    public TextField amount_field;
    public TextArea message_field;
    public Button send_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDashboardData();
    }

    /**
     * Load and display all dashboard data from the database
     */
    public void loadDashboardData() {
        // Ensure we have the latest account data from the database
        Model.getInstance().refreshClientAccounts();

        Client client = Model.getInstance().getClient();

        // Display user name
        if (client != null && client.FirstNameProperty().get() != null) {
            String firstName = client.FirstNameProperty().get();
            user_name.setText("HI, " + firstName.toUpperCase());
        } else {
            user_name.setText("HI, USER");
        }

        // Display current date
        LocalDate today = LocalDate.now();
        date_label.setText("Today, " + today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Display checking account information
        Account checkingAccount = client != null ? client.CheckingAccountProperty().get() : null;
        if (checkingAccount != null) {
            double balance = checkingAccount.BalanceProperty().get();
            String accountNumber = checkingAccount.AccountNumberProperty().get();

            checking_balance.setText(String.format("%.2f MAD", balance));

            // Display last 4 digits of account number
            if (accountNumber != null && accountNumber.length() >= 4) {
                String lastFour = accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                checking_acc_num.setText(lastFour);
            } else {
                checking_acc_num.setText("N/A");
            }
        } else {
            checking_balance.setText("0.00 MAD");
            checking_acc_num.setText("N/A");
        }

        // Display savings account information
        Account savingsAccount = client != null ? client.SavingsAccountProperty().get() : null;
        if (savingsAccount != null) {
            double balance = savingsAccount.BalanceProperty().get();
            String accountNumber = savingsAccount.AccountNumberProperty().get();

            savings_balance.setText(String.format("%.2f MAD", balance));

            // Display last 4 digits of account number
            if (accountNumber != null && accountNumber.length() >= 4) {
                String lastFour = accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                savings_acc_num.setText(lastFour);
            } else {
                savings_acc_num.setText("N/A");
            }
        } else {
            savings_balance.setText("0.00 MAD");
            savings_acc_num.setText("N/A");
        }

        // Calculate and display income and expenses
        double totalIncome = Model.getInstance().getTotalIncome();
        double totalExpenses = Model.getInstance().getTotalExpenses();

        income_label.setText(String.format("+%.2f MAD", totalIncome));
        expenses_label.setText(String.format("-%.2f MAD", totalExpenses));

        // Load and display transactions
        loadTransactions();
    }

    /**
     * Load transactions and display them in the ListView
     */
    private void loadTransactions() {
        List<Transaction> transactions = Model.getInstance().getClientTransactions();

        // Convert to ObservableList
        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(transactions);

        // Set cell factory for custom cell rendering
        transaction_listview.setCellFactory(listView -> new TransactionCellFactory());

        // Set items in ListView
        transaction_listview.setItems(transactionsObservableList);
    }
}