package com.example.myjavafxapp.Models;

import com.example.myjavafxapp.Views.AccountType;
import com.example.myjavafxapp.Views.ViewFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    private AccountType loginAccountType = AccountType.CLIENT;

    //Client Data Section
    private final Client client;
    private boolean clientLoginSuccessFlag;
    private String lastErrorMessage;
    private boolean adminLoginSuccessFlag;


    private Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        //Client Data section
        this.clientLoginSuccessFlag = false;
        this.client = new Client("","","",null,null,null);
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

    public DatabaseDriver getDatabaseDriver() {return databaseDriver;}

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    /*
     *Client Method Section
     */

    public boolean getClientLoginSuccessFlag() {return this.clientLoginSuccessFlag;}

    public void setClientLoginSuccessFlag(boolean flag) {this.clientLoginSuccessFlag = flag;}

    public Client getClient() {return client;}

    /**
     * Refresh the currently logged-in client's accounts from the database.
     * Useful when opening dashboard to ensure we show the latest balances.
     */
    public void refreshClientAccounts() {
        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return;
        }

        // Reload checking account
        CheckingAccount checkingAccount = null;
        try {
            ResultSet checkingResultSet = databaseDriver.getCheckingAccount(payeeAddress);
            if (checkingResultSet != null && checkingResultSet.next()) {
                String accountNumber = checkingResultSet.getString("AccountNumber");
                double balance = checkingResultSet.getDouble("Balance");
                int transactionLimit = checkingResultSet.getInt("TransactionLimit");
                checkingAccount = new CheckingAccount(payeeAddress, accountNumber, balance, transactionLimit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.client.CheckingAccountProperty().set(checkingAccount);

        // Reload savings account
        SavingsAccount savingsAccount = null;
        try {
            ResultSet savingsResultSet = databaseDriver.getSavingsAccount(payeeAddress);
            if (savingsResultSet != null && savingsResultSet.next()) {
                String accountNumber = savingsResultSet.getString("AccountNumber");
                double balance = savingsResultSet.getDouble("Balance");
                double withdrawalLimit;
                try {
                    withdrawalLimit = savingsResultSet.getDouble("WithdrawalLimit");
                } catch (Exception e) {
                    try {
                        withdrawalLimit = savingsResultSet.getDouble("WithdrawLimit");
                    } catch (Exception e2) {
                        withdrawalLimit = 1000.0; // Default fallback
                    }
                }
                savingsAccount = new SavingsAccount(payeeAddress, accountNumber, balance, withdrawalLimit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.client.SavingsAccountProperty().set(savingsAccount);
    }

    public void evaluateClientCred(String pAddress, String password){
        System.out.println("========== DEBUG: evaluateClientCred ==========");
        System.out.println("Attempting login for: " + pAddress);

        ResultSet resultSet = databaseDriver.getClientData(pAddress, password);

        try{
            if (resultSet != null && resultSet.isBeforeFirst()) {
                resultSet.next();
                System.out.println("✓ Client data found in database");

                this.client.FirstNameProperty().set(resultSet.getString("FirstName"));
                this.client.LastNameProperty().set(resultSet.getString("LastName"));
                this.client.PayeeAddressProperty().set(resultSet.getString("PayeeAddress"));

                System.out.println("Client Name: " + resultSet.getString("FirstName") + " " + resultSet.getString("LastName"));

                String[] dateParts = resultSet.getString("Date").split("-");
                LocalDate date = LocalDate.of(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]));
                this.client.DateCreatedProperty().set(date);

                // Load checking account
                System.out.println("\n--- Loading Checking Account ---");
                CheckingAccount checkingAccount = null;
                ResultSet checkingResultSet = databaseDriver.getCheckingAccount(pAddress);

                if (checkingResultSet != null) {
                    System.out.println("✓ Checking ResultSet is not null");

                    if (checkingResultSet.next()) {
                        System.out.println("✓ Checking account found");

                        String accountNumber = checkingResultSet.getString("AccountNumber");
                        double balance = checkingResultSet.getDouble("Balance");
                        int transactionLimit = checkingResultSet.getInt("TransactionLimit");

                        System.out.println("  Account Number: " + accountNumber);
                        System.out.println("  Balance: " + balance);
                        System.out.println("  Transaction Limit: " + transactionLimit);

                        checkingAccount = new CheckingAccount(pAddress, accountNumber, balance, transactionLimit);
                        System.out.println("✓ CheckingAccount object created");
                        System.out.println("  Object Balance Property: " + checkingAccount.BalanceProperty().get());
                    } else {
                        System.out.println("✗ No checking account found for this user");
                    }
                } else {
                    System.out.println("✗ Checking ResultSet is null");
                }

                this.client.CheckingAccountProperty().set(checkingAccount);
                System.out.println("✓ Checking account set to client");
                if (checkingAccount != null) {
                    System.out.println("  Client's checking balance: " + this.client.CheckingAccountProperty().get().BalanceProperty().get());
                }

                // Load savings account
                System.out.println("\n--- Loading Savings Account ---");
                SavingsAccount savingsAccount = null;
                ResultSet savingsResultSet = databaseDriver.getSavingsAccount(pAddress);

                if (savingsResultSet != null) {
                    System.out.println("✓ Savings ResultSet is not null");

                    if (savingsResultSet.next()) {
                        System.out.println("✓ Savings account found");

                        String accountNumber = savingsResultSet.getString("AccountNumber");
                        double balance = savingsResultSet.getDouble("Balance");
                        double withdrawalLimit = 0.0;

                        try {
                            withdrawalLimit = savingsResultSet.getDouble("WithdrawalLimit");
                        } catch (Exception e) {
                            try {
                                withdrawalLimit = savingsResultSet.getDouble("WithdrawLimit");
                            } catch (Exception e2) {
                                withdrawalLimit = 1000.0; // Default
                            }
                        }

                        System.out.println("  Account Number: " + accountNumber);
                        System.out.println("  Balance: " + balance);
                        System.out.println("  Withdrawal Limit: " + withdrawalLimit);

                        savingsAccount = new SavingsAccount(pAddress, accountNumber, balance, withdrawalLimit);
                        System.out.println("✓ SavingsAccount object created");
                        System.out.println("  Object Balance Property: " + savingsAccount.BalanceProperty().get());
                    } else {
                        System.out.println("✗ No savings account found for this user");
                    }
                } else {
                    System.out.println("✗ Savings ResultSet is null");
                }

                this.client.SavingsAccountProperty().set(savingsAccount);
                System.out.println("✓ Savings account set to client");
                if (savingsAccount != null) {
                    System.out.println("  Client's savings balance: " + this.client.SavingsAccountProperty().get().BalanceProperty().get());
                }

                this.clientLoginSuccessFlag = true;
                System.out.println("\n✅ Login successful!");
                System.out.println("===============================================\n");
            } else {
                this.clientLoginSuccessFlag = false;
                System.out.println("❌ Login failed - no matching credentials");
                System.out.println("===============================================\n");
            }
        }catch(Exception e){
            System.out.println("❌ EXCEPTION occurred during login:");
            e.printStackTrace();
            this.clientLoginSuccessFlag = false;
            System.out.println("===============================================\n");
        }
    }

    /**
     * Get the last error message from client creation
     * @return Error message string
     */
    public String getLastErrorMessage() {
        return lastErrorMessage != null ? lastErrorMessage : "";
    }

    /**
     * Validate client data before creation
     * @param firstName First name
     * @param lastName Last name
     * @param payeeAddress Payee address
     * @param password Password
     * @return true if valid, false otherwise
     */
    public boolean validateClientData(String firstName, String lastName, String payeeAddress, String password) {
        if (firstName == null || firstName.trim().isEmpty()) {
            lastErrorMessage = "First name cannot be empty";
            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            lastErrorMessage = "Last name cannot be empty";
            return false;
        }
        if (payeeAddress == null || payeeAddress.trim().isEmpty()) {
            lastErrorMessage = "Payee address cannot be empty";
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            lastErrorMessage = "Password cannot be empty";
            return false;
        }
        if (password.length() < 4) {
            lastErrorMessage = "Password must be at least 4 characters long";
            return false;
        }
        return true;
    }

    /**
     * Validate account balance
     * @param balanceString Balance as string
     * @return Validated balance as double, or -1 if invalid
     */
    public double validateAccountBalance(String balanceString) {
        if (balanceString == null || balanceString.trim().isEmpty()) {
            return -1;
        }
        try {
            double balance = Double.parseDouble(balanceString.trim());
            if (balance < 0) {
                return -1;
            }
            return balance;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Create a new client with optional accounts
     * @param firstName First name
     * @param lastName Last name
     * @param payeeAddress Payee address
     * @param password Password
     * @param checkingBalance Checking account balance (null if not creating)
     * @param savingsBalance Savings account balance (null if not creating)
     * @return true if successful, false otherwise
     */
    public boolean createClient(String firstName, String lastName, String payeeAddress, String password,
                                Double checkingBalance, Double savingsBalance) {
        lastErrorMessage = "";

        // Validate input data
        if (!validateClientData(firstName, lastName, payeeAddress, password)) {
            return false;
        }

        // Check if PayeeAddress already exists
        if (databaseDriver.checkPayeeAddressExists(payeeAddress)) {
            lastErrorMessage = "A client with this Payee Address already exists";
            return false;
        }

        // Validate account balances if provided
        if (checkingBalance != null && checkingBalance < 0) {
            lastErrorMessage = "Checking account balance cannot be negative";
            return false;
        }
        if (savingsBalance != null && savingsBalance < 0) {
            lastErrorMessage = "Savings account balance cannot be negative";
            return false;
        }

        LocalDate currentDate = LocalDate.now();

        // Create client
        if (!databaseDriver.createClient(firstName, lastName, payeeAddress, password, currentDate)) {
            lastErrorMessage = "Failed to create client. Database error occurred.";
            return false;
        }

        // Create checking account if requested
        if (checkingBalance != null) {
            String checkingAccountNumber = databaseDriver.generateAccountNumber("CHK");
            int defaultTransactionLimit = 10; // Default transaction limit
            if (!databaseDriver.createCheckingAccount(payeeAddress, checkingAccountNumber,
                    checkingBalance, defaultTransactionLimit, currentDate)) {
                lastErrorMessage = "Client created but failed to create checking account.";
                return false;
            }
        }

        // Create savings account if requested
        if (savingsBalance != null) {
            String savingsAccountNumber = databaseDriver.generateAccountNumber("SAV");
            double defaultWithdrawalLimit = 1000.0; // Default withdrawal limit
            if (!databaseDriver.createSavingsAccount(payeeAddress, savingsAccountNumber,
                    savingsBalance, defaultWithdrawalLimit, currentDate)) {
                lastErrorMessage = "Client created but failed to create savings account.";
                return false;
            }
        }

        return true;
    }

    /**
     * Get all clients from the database with their accounts
     * @return List of Client objects
     */
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        ResultSet clientsResultSet = databaseDriver.getAllClients();

        try {
            if (clientsResultSet != null) {
                while (clientsResultSet.next()) {
                    String firstName = clientsResultSet.getString("FirstName");
                    String lastName = clientsResultSet.getString("LastName");
                    String payeeAddress = clientsResultSet.getString("PayeeAddress");

                    // Parse date
                    String dateString = clientsResultSet.getString("Date");
                    LocalDate dateCreated = null;
                    if (dateString != null && !dateString.isEmpty()) {
                        try {
                            String[] dateParts = dateString.split("-");
                            if (dateParts.length == 3) {
                                dateCreated = LocalDate.of(
                                        Integer.parseInt(dateParts[0]),
                                        Integer.parseInt(dateParts[1]),
                                        Integer.parseInt(dateParts[2])
                                );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            dateCreated = LocalDate.now();
                        }
                    } else {
                        dateCreated = LocalDate.now();
                    }

                    // Get checking account
                    CheckingAccount checkingAccount = null;
                    ResultSet checkingResultSet = databaseDriver.getCheckingAccount(payeeAddress);
                    if (checkingResultSet != null && checkingResultSet.next()) {
                        String accountNumber = checkingResultSet.getString("AccountNumber");
                        double balance = checkingResultSet.getDouble("Balance");
                        int transactionLimit = checkingResultSet.getInt("TransactionLimit");
                        checkingAccount = new CheckingAccount(payeeAddress, accountNumber, balance, transactionLimit);
                    }

                    // Get savings account
                    SavingsAccount savingsAccount = null;
                    ResultSet savingsResultSet = databaseDriver.getSavingsAccount(payeeAddress);
                    if (savingsResultSet != null && savingsResultSet.next()) {
                        String accountNumber = savingsResultSet.getString("AccountNumber");
                        double balance = savingsResultSet.getDouble("Balance");
                        // Try both possible column names
                        double withdrawalLimit = 0.0;
                        try {
                            withdrawalLimit = savingsResultSet.getDouble("WithdrawalLimit");
                        } catch (Exception e) {
                            try {
                                withdrawalLimit = savingsResultSet.getDouble("WithdrawLimit");
                            } catch (Exception e2) {
                                withdrawalLimit = 1000.0; // Default
                            }
                        }
                        savingsAccount = new SavingsAccount(payeeAddress, accountNumber, balance, withdrawalLimit);
                    }

                    // Create client object
                    Client client = new Client(firstName, lastName, payeeAddress, checkingAccount, savingsAccount, dateCreated);
                    clients.add(client);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clients;
    }

    /**
     * Delete a client and all associated accounts
     * @param payeeAddress The PayeeAddress of the client to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteClient(String payeeAddress) {
        lastErrorMessage = "";

        if (payeeAddress == null || payeeAddress.trim().isEmpty()) {
            lastErrorMessage = "PayeeAddress cannot be empty";
            return false;
        }

        // Delete checking account first (if exists)
        if (!databaseDriver.deleteCheckingAccount(payeeAddress)) {
            // Log but don't fail - account might not exist
            System.out.println("Warning: Could not delete checking account for " + payeeAddress);
        }

        // Delete savings account (if exists)
        if (!databaseDriver.deleteSavingsAccount(payeeAddress)) {
            // Log but don't fail - account might not exist
            System.out.println("Warning: Could not delete savings account for " + payeeAddress);
        }

        // Delete client
        if (!databaseDriver.deleteClient(payeeAddress)) {
            lastErrorMessage = "Failed to delete client. Client may not exist.";
            return false;
        }

        return true;
    }

    /**
     * Get all transactions for the logged-in client
     * @return List of Transaction objects
     */
    public List<Transaction> getClientTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String payeeAddress = client.PayeeAddressProperty().get();

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return transactions;
        }

        ResultSet resultSet = databaseDriver.getClientTransactions(payeeAddress);

        try {
            if (resultSet != null) {
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String receiver = resultSet.getString("Receiver");
                    double amount = resultSet.getDouble("Amount");
                    String message = resultSet.getString("Message");
                    if (message == null) {
                        message = "";
                    }

                    // Parse date
                    String dateString = resultSet.getString("Date");
                    LocalDate date = null;
                    if (dateString != null && !dateString.isEmpty()) {
                        try {
                            String[] dateParts = dateString.split("-");
                            if (dateParts.length == 3) {
                                date = LocalDate.of(
                                        Integer.parseInt(dateParts[0]),
                                        Integer.parseInt(dateParts[1]),
                                        Integer.parseInt(dateParts[2])
                                );
                            }
                        } catch (Exception e) {
                            date = LocalDate.now();
                        }
                    } else {
                        date = LocalDate.now();
                    }

                    Transaction transaction = new Transaction(sender, receiver, amount, date, message);
                    transactions.add(transaction);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * Calculate total income (received transactions) for the logged-in client
     * @return Total income amount
     */
    public double getTotalIncome() {
        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return 0.0;
        }

        List<Transaction> transactions = getClientTransactions();
        double totalIncome = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.receiverProperty().get().equals(payeeAddress)) {
                totalIncome += transaction.amountProperty().get();
            }
        }

        return totalIncome;
    }

    /**
     * Calculate total expenses (sent transactions) for the logged-in client
     * @return Total expenses amount
     */
    public double getTotalExpenses() {
        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return 0.0;
        }

        List<Transaction> transactions = getClientTransactions();
        double totalExpenses = 0.0;

        for (Transaction transaction : transactions) {
            if (transaction.senderProperty().get().equals(payeeAddress)) {
                totalExpenses += transaction.amountProperty().get();
            }
        }

        return totalExpenses;
    }
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