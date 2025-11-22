package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.AdminMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {

    public Button create_client_btn;
    public Button clients_btn;
    public Button deposit_client;
    public Button logout_btn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addlisteners();
    }
    private void addlisteners(){}
    private void onCreateClient(){
        Model.getInstance.getViewFactory().getClientSelectedMenuItem().set(AdminMenuOptions.CREATE_CLIENTS);
    }
}
