package com.example.myjavafxapp.Models;

import com.example.myjavafxapp.Views.AccountType;
import com.example.myjavafxapp.Views.ViewFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    private AccountType loginAccountType = AccountType.CLIENT;

    //Client Data Section
    private final Client client;
    private boolean clientLoginSuccessFlag;

    //Admin Data Section
    private boolean adminLoginSuccessFlag;

    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        //Client Data section
        this.clientLoginSuccessFlag = false;
        this.client = new Client("", "", "", null, null, null);
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    /*
     *Client Method Section
     */

    public boolean getClientLoginSuccessFlag() {
        return this.clientLoginSuccessFlag;
    }

    public void setClientLoginSuccessFlag(boolean flag) {
        this.clientLoginSuccessFlag = flag;
    }

    public Client getClient() {
        return client;
    }

    public void evaluateClientCred(String pAddress, String password) {
        CheckingAccount checkingAccount = null;
        SavingsAccount savingsAccount = null;
        ResultSet resultSet = databaseDriver.getClientData(pAddress, password);

        try {
            if (resultSet != null && resultSet.isBeforeFirst()) {
                resultSet.next();  // ✅ ADD THIS LINE - Move cursor to first row!

                this.client.FirstNameProperty().set(resultSet.getString("FirstName"));
                this.client.LastNameProperty().set(resultSet.getString("LastName"));
                this.client.PayeeAddressProperty().set(resultSet.getString("PayeeAddress"));

                String[] dateParts = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
                this.client.DateCreatedProperty().set(date);

                this.clientLoginSuccessFlag = true;
                System.out.println("✅ Login successful!");
            } else {
                this.clientLoginSuccessFlag = false;
                System.out.println("❌ Login failed - no matching credentials");
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.clientLoginSuccessFlag = false;
        }
    }

    /*
     *Admin Method Section
     */

    /**
     * Evaluate admin credentials from database
     */
    public boolean getAdminLoginSuccessFlag() {
        return this.adminLoginSuccessFlag;
    }

    public void setAdminLoginSuccessFlag(boolean flag) {this.adminLoginSuccessFlag = flag;}

    public void evaluateAdminCred(String Username, String Password) {
        ResultSet resultSet = databaseDriver.getAdminData(Username, Password);

        try {
            if (resultSet != null && resultSet.isBeforeFirst()) {
                resultSet.next();  // Move to first row
                this.adminLoginSuccessFlag = true;
                System.out.println("✅ Admin login successful: " + Username);
            } else {
                this.adminLoginSuccessFlag = false;
                System.out.println("❌ Admin login failed: Invalid credentials");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.adminLoginSuccessFlag = false;
        }
    }
}
