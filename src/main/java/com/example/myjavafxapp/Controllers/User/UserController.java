package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    public BorderPane user_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set default view to Dashboard on load
        Model.getInstance().getViewFactory().getUserSelectedMenuItem().set(com.example.myjavafxapp.Views.UserMenuOptions.DASHBOARD);
        user_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());

        Model.getInstance().getViewFactory().getUserSelectedMenuItem().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case TRANSACTIONS -> user_parent.setCenter(Model.getInstance().getViewFactory().getTransactionsView());
                case ACCOUNTS -> user_parent.setCenter(Model.getInstance().getViewFactory().getAccountsView());
                case LOGOUT -> {
                    // Close current admin window
                    Stage stage = (Stage) user_parent.getScene().getWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                    Model.getInstance().getViewFactory().showLoginWindow();
                }
                default -> user_parent.setCenter(Model.getInstance().getViewFactory().getDashboardView());
            }
        });
    }
}
