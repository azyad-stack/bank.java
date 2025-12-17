package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Models.Transaction;
import com.example.myjavafxapp.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {

    public ListView<Transaction> Transactions_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTransactions();
    }

    /**
     * Load transactions from database and display them in the ListView
     */
    public void loadTransactions() {
        // Get transactions from database
        List<Transaction> transactions = Model.getInstance().getClientTransactions();

        // Convert to ObservableList for JavaFX
        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(transactions);

        // Set cell factory to use TransactionCellFactory for custom cell rendering
        Transactions_listview.setCellFactory(listView -> new TransactionCellFactory());

        // Set items in ListView
        Transactions_listview.setItems(transactionsObservableList);
    }

    /**
     * Refresh transactions (call this when returning to this view)
     */
    public void refreshTransactions() {
        loadTransactions();
    }
}
