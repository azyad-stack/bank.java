package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.ClientCellFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepositController implements Initializable {

    public TextField pAddress_fld;
    public ListView Paddress_lisview;
    public TextField Amount_fld;
    public Button Deposit_btn;
    public Label error_lbl;
    public Button PaddressSearch_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        loadSearchedClient();
    }

    private void loadSearchedClient() {

    }

    private void addListeners() {
        PaddressSearch_btn.setOnAction(event -> onPaddressSearch());
        Deposit_btn.setOnAction(event -> onDeposit());
    }

    private void onDeposit() {
        String address = pAddress_fld.getText().trim();
        String amountText = Amount_fld.getText().trim();

        try {
            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                error_lbl.setText("Amount must be positive!");
                error_lbl.setTextFill(Color.RED);
                return;
            }

            boolean success = Model.getInstance().getDatabaseDriver()
                    .depositToCheckingAccount(address, amount);

            if (success) {
                error_lbl.setText("Deposit successful!");
                error_lbl.setTextFill(Color.GREEN);
            } else {
                error_lbl.setText("Deposit failed!");
                error_lbl.setTextFill(Color.RED);
            }
        } catch (NumberFormatException e) {
            error_lbl.setText("Invalid amount!");
            error_lbl.setTextFill(Color.RED);
        }
    }

    private Object onPaddressSearch() {
        String address = pAddress_fld.getText().trim();
        boolean success = Model.getInstance().SearchClient(
                address
        );
        if (success) {
            error_lbl.setText("CLIENT was Found!");
            error_lbl.setTextFill(Color.GREEN);
            // Clear success message after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        error_lbl.setText("");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            List<Client> client = Model.getInstance().getserachedClient(address);
            ObservableList<Client> clientsObservableList = FXCollections.observableArrayList(client);
            Paddress_lisview.setCellFactory(listView -> new ClientCellFactory());
            Paddress_lisview.setItems(clientsObservableList);

        }
        else {
        // Failure - show error message
        String errorMessage = Model.getInstance().getLastErrorMessageSearch();
        if (errorMessage.isEmpty()) {
            errorMessage = "Failed to find client. Please try other Payee Address.";
        }
        error_lbl.setTextFill(Color.RED);
        error_lbl.setText(errorMessage);
        }
    return null;
    }
}
