package com.example.myjavafxapp.Controllers.Admin;

import com.example.myjavafxapp.Models.Model;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateUserController  implements Initializable {
    public TextField fName_fld;
    public TextField lName_fld;
    public TextField password_fld;
    public CheckBox pAddress_box;
    public Label pAddress_lbl;
    public CheckBox ch_acc_box;
    public TextField ch_amount_fld;
    public CheckBox sv_acc_box;
    public TextField sv_amount_fld;
    public Button create_client_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupForm();
        addListeners();
    }

    private void setupForm() {
        // Initially disable amount fields
        ch_amount_fld.setDisable(true);
        sv_amount_fld.setDisable(true);

        // Clear error label
        error_lbl.setText("");
        error_lbl.setTextFill(Color.RED);

        // Set up PayeeAddress label
        pAddress_lbl.setText("");
    }

    private void addListeners() {
        // Create client button action
        create_client_btn.setOnAction(event -> onCreateClient());

        // Checking account checkbox listener
        ch_acc_box.setOnAction(event -> {
            if (ch_acc_box.isSelected()) {
                ch_amount_fld.setDisable(false);
                ch_amount_fld.setText("0.0");
            } else {
                ch_amount_fld.setDisable(true);
                ch_amount_fld.clear();
            }
        });

        // Savings account checkbox listener
        sv_acc_box.setOnAction(event -> {
            if (sv_acc_box.isSelected()) {
                sv_amount_fld.setDisable(false);
                sv_amount_fld.setText("0.0");
            } else {
                sv_amount_fld.setDisable(true);
                sv_amount_fld.clear();
            }
        });

        // PayeeAddress checkbox listener - auto-generate address
        pAddress_box.setOnAction(event -> {
            if (pAddress_box.isSelected()) {
                generatePayeeAddress();
            } else {
                pAddress_lbl.setText("");
            }
        });

        // Auto-generate PayeeAddress when first/last name changes
        fName_fld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (pAddress_box.isSelected()) {
                generatePayeeAddress();
            }
        });

        lName_fld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (pAddress_box.isSelected()) {
                generatePayeeAddress();
            }
        });
    }

    private void generatePayeeAddress() {
        String firstName = fName_fld.getText().trim();
        String lastName = lName_fld.getText().trim();

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            // Generate PayeeAddress: FirstName.LastName@bank.com
            int random = (int)(Math.random() * 900) + 100;
            String payeeAddress = "@"+lastName + random;
            pAddress_lbl.setText(payeeAddress);
        } else {
            pAddress_lbl.setText("");
        }
    }

    private boolean validateForm() {
        error_lbl.setText("");
        error_lbl.setTextFill(Color.RED);

        // Validate First Name
        if (fName_fld.getText() == null || fName_fld.getText().trim().isEmpty()) {
            error_lbl.setText("First name is required");
            return false;
        }

        // Validate Last Name
        if (lName_fld.getText() == null || lName_fld.getText().trim().isEmpty()) {
            error_lbl.setText("Last name is required");
            return false;
        }

        // Validate Password
        if (password_fld.getText() == null || password_fld.getText().trim().isEmpty()) {
            error_lbl.setText("Password is required");
            return false;
        }

        // Validate PayeeAddress
        String payeeAddress = pAddress_lbl.getText().trim();
        if (payeeAddress.isEmpty()) {
            error_lbl.setText("Please check the Payee Address checkbox to generate an address");
            return false;
        }

        // Validate Checking Account balance if checkbox is checked
        if (ch_acc_box.isSelected()) {
            String checkingBalanceStr = ch_amount_fld.getText().trim();
            if (checkingBalanceStr.isEmpty()) {
                error_lbl.setText("Please enter checking account balance");
                return false;
            }
            double checkingBalance = Model.getInstance().validateAccountBalance(checkingBalanceStr);
            if (checkingBalance < 0) {
                error_lbl.setText("Invalid checking account balance. Please enter a valid positive number.");
                return false;
            }
        }

        // Validate Savings Account balance if checkbox is checked
        if (sv_acc_box.isSelected()) {
            String savingsBalanceStr = sv_amount_fld.getText().trim();
            if (savingsBalanceStr.isEmpty()) {
                error_lbl.setText("Please enter savings account balance");
                return false;
            }
            double savingsBalance = Model.getInstance().validateAccountBalance(savingsBalanceStr);
            if (savingsBalance < 0) {
                error_lbl.setText("Invalid savings account balance. Please enter a valid positive number.");
                return false;
            }
        }

        // At least one account must be created
        if (!ch_acc_box.isSelected() && !sv_acc_box.isSelected()) {
            error_lbl.setText("Please select at least one account type (Checking or Savings)");
            return false;
        }

        return true;
    }

    private void onCreateClient() {
        // Validate form
        if (!validateForm()) {
            return;
        }

        // Extract form values
        String firstName = fName_fld.getText().trim();
        String lastName = lName_fld.getText().trim();
        String password = password_fld.getText().trim();
        String payeeAddress = pAddress_lbl.getText().trim();

        // Get account balances
        Double checkingBalance = null;
        if (ch_acc_box.isSelected()) {
            checkingBalance = Model.getInstance().validateAccountBalance(ch_amount_fld.getText().trim());
        }

        Double savingsBalance = null;
        if (sv_acc_box.isSelected()) {
            savingsBalance = Model.getInstance().validateAccountBalance(sv_amount_fld.getText().trim());
        }

        // Create client through Model
        boolean success = Model.getInstance().createClient(
                firstName,
                lastName,
                payeeAddress,
                password,
                checkingBalance,
                savingsBalance
        );

        if (success) {
            // Success - show success message and reset form
            error_lbl.setTextFill(Color.GREEN);
            error_lbl.setText("✓ Client created successfully!");
            resetForm();

            // Clear success message after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        error_lbl.setText("");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            // Failure - show error message
            String errorMessage = Model.getInstance().getLastErrorMessage();
            if (errorMessage.isEmpty()) {
                errorMessage = "Failed to create client. Please try again.";
            }
            error_lbl.setTextFill(Color.RED);
            error_lbl.setText(errorMessage);
        }
    }

    private void resetForm() {
        // Clear all text fields
        fName_fld.clear();
        lName_fld.clear();
        password_fld.clear();

        // Uncheck checkboxes
        pAddress_box.setSelected(false);
        ch_acc_box.setSelected(false);
        sv_acc_box.setSelected(false);

        // Clear and disable amount fields
        ch_amount_fld.clear();
        ch_amount_fld.setDisable(true);
        sv_amount_fld.clear();
        sv_amount_fld.setDisable(true);

        // Clear PayeeAddress label
        pAddress_lbl.setText("");

        // Clear error label
        error_lbl.setText("");
    }
}
