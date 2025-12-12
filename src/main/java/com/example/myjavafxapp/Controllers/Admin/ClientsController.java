package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Client;
import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Views.ClientCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {

    public ListView<Client> clients_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadClients();
    }

    /**
     * Load all clients from the database and display them in the ListView
     */
    public void loadClients() {
        // Get all clients from Model
        List<Client> clients = Model.getInstance().getAllClients();

        // Convert to ObservableList for ListView
        ObservableList<Client> clientsObservableList = FXCollections.observableArrayList(clients);

        // Set cell factory for custom cell rendering with refresh callback
        clients_listview.setCellFactory(listView -> new ClientCellFactory(this::loadClients));

        // Set items in ListView
        clients_listview.setItems(clientsObservableList);
    }
}
