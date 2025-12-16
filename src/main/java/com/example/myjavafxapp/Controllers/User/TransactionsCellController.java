package com.example.myjavafxapp.Controllers.User;

import com.example.myjavafxapp.Models.Model;
import com.example.myjavafxapp.Models.Transaction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TransactionsCellController implements Initializable {
    public FontAwesomeIconView left_arrow_trans_icon;
    public FontAwesomeIconView right_arrow_trans_icon;
    public Label date_trans_lbl;
    public Label sender_trans_lbl;
    public Label reciever_trans_lbl;
    public Label amount_trans_lbl;

    private final Transaction transaction;

    public TransactionsCellController(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (transaction != null) {
            String currentUserPayeeAddress = Model.getInstance().getClient().PayeeAddressProperty().get();
            boolean isSender = currentUserPayeeAddress != null &&
                    currentUserPayeeAddress.equals(transaction.senderProperty().get());

            // Display date
            if (transaction.dateProperty().get() != null) {
                date_trans_lbl.setText(transaction.dateProperty().get().format(DateTimeFormatter.ISO_DATE));
            } else {
                date_trans_lbl.setText("N/A");
            }

            // Display sender
            sender_trans_lbl.setText(transaction.senderProperty().get() != null ? transaction.senderProperty().get() : "");

            // Display receiver
            reciever_trans_lbl.setText(transaction.receiverProperty().get() != null ? transaction.receiverProperty().get() : "");

            // Display amount (no null check needed - double is always initialized)
            amount_trans_lbl.setText(String.format("%.2f MAD", transaction.amountProperty().get()));

            // Show appropriate arrow based on transaction direction
            if (left_arrow_trans_icon != null && right_arrow_trans_icon != null) {
                if (isSender) {
                    // User sent money - show right arrow (outgoing)
                    left_arrow_trans_icon.setVisible(false);
                    right_arrow_trans_icon.setVisible(true);
                } else {
                    // User received money - show left arrow (incoming)
                    left_arrow_trans_icon.setVisible(true);
                    right_arrow_trans_icon.setVisible(false);
                }
            }
        }
    }
}