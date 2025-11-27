package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.AdminMenuOptions;
import javafx.fxml.FXML; // Don't forget this
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {

    @FXML
    public Button create_client_btn;
    @FXML
    public Button clients_btn;
    @FXML
    public Button deposit_client;
    @FXML
    public Button logout_btn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
    }

    private void addListeners(){
        // You must link the button click to the method here
        create_client_btn.setOnAction(event -> onCreateClient());
    }

    private void onCreateClient(){
        // FIX 1: Added () to getInstance
        // FIX 2: Changed getClient... to getAdmin...
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.CREATE_CLIENTS);
    }
}