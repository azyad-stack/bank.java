package com.example.myjavafxapp.Views;

import com.example.myjavafxapp.Controllers.Admin.AdminController;
import com.example.myjavafxapp.Controllers.User.UserController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
    //Client Views
    private final ObjectProperty<UserMenuOptions> userSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane transactionView;
    private AnchorPane accountsView;

    //Admin Views
    private final StringProperty adminSelectedMenuItem;
    private AnchorPane createUserView;


    public ViewFactory() {
        this.userSelectedMenuItem = new SimpleObjectProperty<>();
        this.adminSelectedMenuItem = new SimpleStringProperty("");
    }


    /**
     * User View Section
     *
     * **/
    public ObjectProperty<UserMenuOptions> getUserSelectedMenuItemProperty() {
        return this.userSelectedMenuItem;
    }
    public AnchorPane getDashboardView() {
        if(dashboardView == null) {
            try {
                dashboardView = new FXMLLoader(getClass().getResource("/Fxml/User/Dashboard.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return dashboardView;
    }
    public Node getTransactionsView() {
    }
    public Node getAccountsView() {
    }
    public void showLoginWindow() {
       FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader,"GADZ'Art Bank");
    }
    public void showUserWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/User/User.fxml"));
        UserController userController = new UserController();
        loader.setController(userController);
        createStage(loader,"USER");
    }
    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader,"ADMIN");
    }
    private void createStage(FXMLLoader loader, String title) {
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }



}