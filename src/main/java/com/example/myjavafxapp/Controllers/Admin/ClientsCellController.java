package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Client;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientsCellController implements Initializable {

    public Label name_lbl;
    public Label lastname_lbl;
    public Label ch_account_lbl;
    public Label sv_account_lbl;
    public Label date_lbl;
    public Label payeeAddress_lbl;

    private final Client client;

    public ClientsCellController(Client client) {
        this.client = client;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
