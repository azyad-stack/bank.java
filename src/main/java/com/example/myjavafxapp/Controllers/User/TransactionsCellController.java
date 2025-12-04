package com.example.myjavafxapp.Controllers.User;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionsCellController implements Initializable {
    public FontAwesomeIconView left_arrow_trans_icon;
    public FontAwesomeIconView right_arrow_trans_icon;
    public Label date_trans_lbl;
    public Label sender_trans_lbl;
    public Label reciever_trans_lbl;
    public Label amount_trans_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
