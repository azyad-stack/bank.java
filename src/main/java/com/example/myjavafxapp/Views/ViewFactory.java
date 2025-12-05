package com.example.myjavafxapp.Views;

import com.example.myjavafxapp.Controllers.Admin.AdminController;
import com.example.myjavafxapp.Controllers.User.UserController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
    private AccountType loginAccountType;
    //Client Views
    private final ObjectProperty<UserMenuOptions> userSelectedMenuItem;
    private AnchorPane dashboardView;
    private AnchorPane transactionView;
    private AnchorPane accountsView;

    // Admin Views
    private final ObjectProperty<AdminMenuOptions> adminSelectedMenuItem;
    private AnchorPane createUserView;
    private AnchorPane clientsView;
    private AnchorPane depositsView;

    public ViewFactory() {
        this.loginAccountType = AccountType.CLIENT;
        this.userSelectedMenuItem = new SimpleObjectProperty<>();
        //Admin Views
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
    }
    public AccountType getLoginAccountType() {
        return loginAccountType;
    }
    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }


    /**
     * User View Section
     *
     * **/
    public ObjectProperty<UserMenuOptions> getUserSelectedMenuItem() {
        return userSelectedMenuItem;
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
    public AnchorPane getTransactionsView() {
        if(transactionView == null) {
            try {
                transactionView = new FXMLLoader(getClass().getResource("/Fxml/User/Transactions.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return transactionView;
    }
    public AnchorPane getAccountsView() {
        if(accountsView == null) {
            try {
                accountsView = new FXMLLoader(getClass().getResource("/Fxml/User/Accounts.fxml")).load();

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return accountsView;
    }

    public void showClientWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/User/User.fxml"));
        UserController userController = new UserController();
        loader.setController(userController);
        createStage(loader,"USER");
    }
    /*
     * Admin Section
     * */

    public ObjectProperty<AdminMenuOptions> getAdminSelectedMenuItem () {
        return adminSelectedMenuItem;
    }
   public AnchorPane getDepositsView() {
        if(depositsView == null) {
            try {
                depositsView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Deposit.fxml")).load();
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
        return depositsView;
   }

    public AnchorPane getClientsView() {
        if(clientsView == null) {
            try {
                clientsView = new FXMLLoader(getClass().getResource("/Fxml/Admin/Clients.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return clientsView;
    }
    public AnchorPane getCreateUserView() {
        if(createUserView == null) {
            try {
                createUserView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CreateUser.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return createUserView;
    }
    public void showAdminWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/Admin.fxml"));
        AdminController adminController = new AdminController();
        loader.setController(adminController);
        createStage(loader,"ADMIN");
    }
    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        createStage(loader,"GADZ'Art Bank");
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
        stage.setTitle("Gadz'Art Bank");
        stage.show();
    }

    public void closeStage(Stage currentStage) {
        currentStage.close();
    }

}