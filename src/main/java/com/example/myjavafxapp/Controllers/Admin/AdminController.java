package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.example.myjavafxapp.Views.AdminMenuOptions.CREATE_CLIENT;

public class AdminController implements Initializable {

    @FXML
    private BorderPane admin_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().addListener((observableValue, oldVal, newVal) -> {

            // Example switch — you will customize this later
            switch (newVal) {
                case CREATE_CLIENT -> admin_parent.setCenter(Model.getInstance().getViewFactory().getCreateUserView());

                // Add your other menu options here…
                default -> {}
            }

        });
    }
}
