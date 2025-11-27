package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.AdminMenuOptions;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {

            // Example switch — you will customize this later
            switch (newVal) {
                case CREATE_CLIENT:
                    AnchorPane view = Model.getInstance().getViewFactory().getCreateClientView();
                    admin_parent.setCenter(view);
                    break;

                // Add your other menu options here…

                default:
                    break;
            }

        });
    }
}
