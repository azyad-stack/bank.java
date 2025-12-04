package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
                case CLIENTS -> admin_parent.setCenter(Model.getInstance().getViewFactory().getClientsView());
                case DEPOSIT ->  admin_parent.setCenter(Model.getInstance().getViewFactory().getDepositsView());
                case LOGOUT -> {
                    // Close current admin window
                    Stage stage = (Stage) admin_parent.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);

                    // Show login window
                    Model.getInstance().getViewFactory().showLoginWindow();
                }
                default -> admin_parent.setCenter(Model.getInstance().getViewFactory().getCreateUserView());

            }

        });
    }
}
