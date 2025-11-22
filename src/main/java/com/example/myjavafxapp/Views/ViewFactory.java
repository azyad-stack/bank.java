package com.example.myjavafxapp.Views;

import com.example.myjavafxapp.Controllers.Admin.AdminController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewFactory {
   // Admin Views
    private final ObjectProperty<AdminMenuOptions> adminSelectedMenuItem;
    private AnchorPane createClientView;
    public ViewFactory() {
        this.adminSelectedMenuItem = new SimpleObjectProperty<>();
    }
    /*
    * Admin views Section
    * */
    public ObjectProperty<AdminMenuOptions> getAdminSelectedMenuItem() {
        return adminSelectedMenuItem;
    }
    private AnchorPane getCreateClientView() {
        if (createClientView == null) {
            try {
                createClientView = new FXMLLoader(getClass().getResource("/Fxml/Admin/CreateClient.fxml")).load();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return createClientView;

    }
   public void showAdminWindow(){
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
           AdminController controller = new AdminController();
           loader.setController(controller);
           createStage(loader);
       }
   }

    private void createStage(FXMLLoader loader) {
       Scene scene = null;
       try {
           scene = new Scene(loader.load());
       }catch (Exception e){
           e.printStackTrace();
       }
       Stage stage = new Stage();
       stage.setScene(scene);
       stage.setTitle("JBANK");
       stage.show();
    }
    public void closeStage(Stage stage){
       stage.close();
    }


    public void showLoginWindow() {
        FXMLLoader Loader = new FXMLLoader(getClass().getResource("/Fxml/Login.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(Loader.load());
        }catch (Exception e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("JBANK");
        stage.show();
    }
}
