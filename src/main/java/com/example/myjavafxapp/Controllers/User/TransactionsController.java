package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Models.Transaction;
import com.example.myjavafxapp.Views.TransactionCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {

    public ListView<Transaction> Transactions_listview;
    public DatePicker start_date_picker;
    public DatePicker end_date_picker;
    public Button search_btn;
    public Button reset_btn;
    public Button first_page_btn;
    public Button prev_page_btn;
    public Button next_page_btn;
    public Button last_page_btn;
    public Label page_info_label;
    public ComboBox<Integer> page_size_combo;

    private int currentPage = 1;
    private int pageSize = 10;
    private int totalPages = 1;
    private boolean isFiltering = false;
    private LocalDate filterStartDate = null;
    private LocalDate filterEndDate = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupPageSizeCombo();
        setupPaginationControls();
        setupSearchControls();
        loadTransactions();
    }

    private void setupPageSizeCombo() {
        page_size_combo.setItems(FXCollections.observableArrayList(5, 10, 20, 50, 100));
        page_size_combo.setValue(pageSize);
        page_size_combo.setOnAction(event -> {
            pageSize = page_size_combo.getValue();
            currentPage = 1;
            loadTransactions();
        });
    }

    private void setupPaginationControls() {
        first_page_btn.setOnAction(event -> {
            currentPage = 1;
            loadTransactions();
        });

        prev_page_btn.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                loadTransactions();
            }
        });

        next_page_btn.setOnAction(event -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadTransactions();
            }
        });

        last_page_btn.setOnAction(event -> {
            currentPage = totalPages;
            loadTransactions();
        });
    }

    private void setupSearchControls() {
        search_btn.setOnAction(event -> onSearch());
        reset_btn.setOnAction(event -> onReset());
    }

    private void onSearch() {
        LocalDate startDate = start_date_picker.getValue();
        LocalDate endDate = end_date_picker.getValue();

        if (startDate == null || endDate == null) {
            showAlert("Please select both start and end dates");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showAlert("Start date must be before end date");
            return;
        }

        isFiltering = true;
        filterStartDate = startDate;
        filterEndDate = endDate;
        currentPage = 1;
        loadTransactions();
    }

    private void onReset() {
        isFiltering = false;
        filterStartDate = null;
        filterEndDate = null;
        start_date_picker.setValue(null);
        end_date_picker.setValue(null);
        currentPage = 1;
        loadTransactions();
    }

    /**
     * Load transactions with current pagination settings
     */
    public void loadTransactions() {
        List<Transaction> transactions;

        if (isFiltering && filterStartDate != null && filterEndDate != null) {
            transactions = Model.getInstance().searchTransactionsByDate(
                    filterStartDate, filterEndDate, currentPage, pageSize
            );
        } else {
            transactions = Model.getInstance().getClientTransactionsPaginated(currentPage, pageSize);
        }

        // Calculate total pages
        totalPages = Model.getInstance().getTransactionPageCount(pageSize);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        // Update pagination controls
        updatePaginationUI();

        // Display transactions
        ObservableList<Transaction> transactionsObservableList = FXCollections.observableArrayList(transactions);
        Transactions_listview.setCellFactory(listView -> new TransactionCellFactory());
        Transactions_listview.setItems(transactionsObservableList);
    }

    private void updatePaginationUI() {
        page_info_label.setText("Page " + currentPage + " of " + totalPages);

        first_page_btn.setDisable(currentPage == 1);
        prev_page_btn.setDisable(currentPage == 1);
        next_page_btn.setDisable(currentPage == totalPages);
        last_page_btn.setDisable(currentPage == totalPages);
    }

    /**
     * Refresh transactions (call this when returning to this view)
     */
    public void refreshTransactions() {
        currentPage = 1;
        loadTransactions();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Search Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}