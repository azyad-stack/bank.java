package com.example.myjavafxapp.Views;

import com.example.myjavafxapp.Controllers.Admin.ClientsCellController;
import com.example.myjavafxapp.Models.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class ClientCellFactory extends ListCell<Client> {
    @Override
    protected void updateItem(Client client, boolean empty) {
        super.updateItem(client, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientCell.fxml"));
            ClientsCellController controller = new ClientsCellController(client);
            loader.setController(controller);
            setText(null);
            try {
                setGraphic(loader.load());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
