package com.example.myjavafxapp.Views;

import com.example.myjavafxapp.Controllers.Admin.AdminController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
    private AccountType loginAccountType;
    private final ObjectProperty<AdminMenuOptions> adminSelectedMenuItem;
    private AnchorPane createClientView;

    public ViewFactory() {
        this.loginAccountType = AccountType.CLIENT;
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    // --- LOGIN VIEW ---
    private FXMLLoader loadLoginView() {
        return loadFXML("/Fxml/Login.fxml");
    }

    public void showLoginWindow() {
        createStage(loadLoginView(), "Login");
    }

    // --- CLIENT DASHBOARD ---
    private FXMLLoader loadClientDashboardView() {
        return loadFXML("/Fxml/User/Dashboard.fxml");
    }

    public void showClientWindow() {
        createStage(loadClientDashboardView(), "Client Dashboard");
    }

    // --- ADMIN VIEWS SECTION ---
    public ObjectProperty<AdminMenuOptions> getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }

    public AnchorPane getCreateClientView() {
        if (createClientView == null) {
            try {
                createClientView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CreateClient.fxml")).load();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return createClientView;
    }

    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController controller = new AdminController();
        loader.setController(controller);
        createStage(loader, "Admin");
    }

    private FXMLLoader loadFXML(String resourcePath) {
        return new FXMLLoader(getClass().getResource(resourcePath));
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

    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}