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
    //admin data section
    private boolean adminLoginSuccessFlag;
    private String LastSearchErrorMessage;


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
                savingsAccount = new SavingsAccount(payeeAddress, accountNumber, balance);
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
                        savingsAccount = new SavingsAccount(pAddress, accountNumber, balance);
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
        try {
            // Begin transaction
            databaseDriver.beginTransaction();


            // Create client
            if (!databaseDriver.createClient(firstName, lastName, payeeAddress, password, currentDate)) {
                databaseDriver.rollbackTransaction();
                lastErrorMessage = "Failed to create client. Database error occurred.";
                return false;
            }

            // Create checking account if requested
            if (checkingBalance != null) {
                String checkingAccountNumber = databaseDriver.generateAccountNumber("CHK");
                int defaultTransactionLimit = 10;
                if (!databaseDriver.createCheckingAccount(payeeAddress, checkingAccountNumber,
                        checkingBalance, defaultTransactionLimit)) {
                    databaseDriver.rollbackTransaction();
                    lastErrorMessage = "Failed to create checking account.";
                    return false;
                }
            }

            // Create savings account if requested
            if (savingsBalance != null) {
                String savingsAccountNumber = databaseDriver.generateAccountNumber("SAV");
                if (!databaseDriver.createSavingsAccount(payeeAddress, savingsAccountNumber,
                        savingsBalance)) {
                    databaseDriver.rollbackTransaction();
                    lastErrorMessage = "Failed to create savings account.";
                    return false;
                }
            }

            // All successful, commit transaction
            databaseDriver.commitTransaction();
            return true;

        } catch (SQLException e) {
            databaseDriver.rollbackTransaction();
            lastErrorMessage = "Database error occurred during client creation.";
            e.printStackTrace();
            return false;
        }
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
                        savingsAccount = new SavingsAccount(payeeAddress, accountNumber, balance);
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

        ResultSet resultSet = null;
        try {
            resultSet = databaseDriver.getClientTransactions(payeeAddress);
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close ResultSet to free resources
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

   //Model Search for client
    public boolean SearchClient(String address) {
        LastSearchErrorMessage = "";
        if (address == null || address.isEmpty()) {
            LastSearchErrorMessage = "Payee Address is Empty :(";
            return false;
        }
        if (databaseDriver.checkPayeeAddressExists(address)) {
            return true;
        }
        return false;
    }

    public String getLastErrorMessageSearch() {

        return LastSearchErrorMessage != null ? LastSearchErrorMessage : "";

    }
    /**
     * Search for a client and return full details with accounts
     */
    public List<Client> getserachedClient(String address) {
        List<Client> clients = new ArrayList<>();

        if(!databaseDriver.checkPayeeAddressExists(address)) {
            return clients;
        }

        ResultSet clientResultSet = databaseDriver.getSearchedClient(address);
        try {
            while(clientResultSet != null && clientResultSet.next()) {
                String fName = clientResultSet.getString("FirstName");
                String lName = clientResultSet.getString("LastName");
                String payeeAddress = clientResultSet.getString("PayeeAddress");

                // Parse date
                String dateString = clientResultSet.getString("Date");
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
                    savingsAccount = new SavingsAccount(payeeAddress, accountNumber, balance);
                }

                Client client = new Client(fName, lName, payeeAddress, checkingAccount, savingsAccount, dateCreated);
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    /**
     * Deposit amount to checking account
     * Fixed to properly validate input and return correct status
     */
    public boolean DepositAmount(Double Amount, String payeeAddress) {
        // Validate inputs
        if (Amount == null || Amount <= 0) {
            lastErrorMessage = "Invalid deposit amount";
            return false;
        }

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            lastErrorMessage = "Payee address is empty";
            return false;
        }

        // Check if account exists
        if (!databaseDriver.checkPayeeAddressExists(payeeAddress)) {
            lastErrorMessage = "Client not found";
            return false;
        }

        // Perform deposit
        if (!databaseDriver.depositToCheckingAccount(payeeAddress, Amount)) {
            lastErrorMessage = "Failed to deposit amount";
            return false;
        }

        return true;
    }
    /**
     * Send money from logged-in client to another client
     * @param receiverPayeeAddress PayeeAddress of the receiver
     * @param amount Amount to send
     * @param message Optional message
     * @return true if successful, false otherwise
     */
    public boolean sendMoney(String receiverPayeeAddress, double amount, String message) {
        lastErrorMessage = "";

        String senderPayeeAddress = client.PayeeAddressProperty().get();
        if (senderPayeeAddress == null || senderPayeeAddress.isEmpty()) {
            lastErrorMessage = "You must be logged in to send money";
            return false;
        }

        // Validate receiver
        if (receiverPayeeAddress == null || receiverPayeeAddress.trim().isEmpty()) {
            lastErrorMessage = "Receiver Payee Address cannot be empty";
            return false;
        }

        // Can't send to yourself
        if (receiverPayeeAddress.equals(senderPayeeAddress)) {
            lastErrorMessage = "You cannot send money to yourself";
            return false;
        }

        // Validate receiver exists
        if (!databaseDriver.checkPayeeAddressExists(receiverPayeeAddress)) {
            lastErrorMessage = "Receiver Payee Address does not exist";
            return false;
        }

        // Validate amount
        if (amount <= 0) {
            lastErrorMessage = "Amount must be greater than 0";
            return false;
        }

        // Check sender has checking account
        Account checkingAccount = client.CheckingAccountProperty().get();
        if (checkingAccount == null) {
            lastErrorMessage = "You do not have a checking account";
            return false;
        }

        // Check sufficient balance
        double currentBalance = checkingAccount.BalanceProperty().get();
        if (currentBalance < amount) {
            lastErrorMessage = "Insufficient funds. Current balance: " + String.format("%.2f", currentBalance) + " MAD";
            return false;
        }

        // Create transaction
        LocalDate transactionDate = LocalDate.now();
        if (databaseDriver.createTransaction(senderPayeeAddress, receiverPayeeAddress, amount, message, transactionDate)) {
            // Refresh client accounts to show updated balance
            refreshClientAccounts();
            return true;
        } else {
            lastErrorMessage = "Failed to process transaction. Please try again.";
            return false;
        }
    }

    /**
     * Get paginated transactions for the logged-in client
     * @param page Page number (1-based)
     * @param pageSize Number of transactions per page
     * @return List of transactions
     */
    public List<Transaction> getClientTransactionsPaginated(int page, int pageSize) {
        List<Transaction> transactions = new ArrayList<>();
        String payeeAddress = client.PayeeAddressProperty().get();

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return transactions;
        }

        int offset = (page - 1) * pageSize;
        ResultSet resultSet = null;
        try {
            resultSet = databaseDriver.getClientTransactionsPaginated(payeeAddress, offset, pageSize);
            if (resultSet != null) {
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String receiver = resultSet.getString("Receiver");
                    double amount = resultSet.getDouble("Amount");
                    String message = resultSet.getString("Message");
                    if (message == null) message = "";

                    String dateString = resultSet.getString("Date");
                    LocalDate date = LocalDate.now();
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
                    }

                    Transaction transaction = new Transaction(sender, receiver, amount, date, message);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return transactions;
    }

    /**
     * Get total number of pages for transactions
     * @param pageSize Number of transactions per page
     * @return Total number of pages
     */
    public int getTransactionPageCount(int pageSize) {
        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return 0;
        }

        int totalTransactions = databaseDriver.getClientTransactionCount(payeeAddress);
        return (int) Math.ceil((double) totalTransactions / pageSize);
    }

    /**
     * Search transactions by date range
     * @param startDate Start date
     * @param endDate End date
     * @param page Page number (1-based)
     * @param pageSize Items per page
     * @return List of filtered transactions
     */
    public List<Transaction> searchTransactionsByDate(LocalDate startDate, LocalDate endDate,
                                                      int page, int pageSize) {
        List<Transaction> transactions = new ArrayList<>();
        String payeeAddress = client.PayeeAddressProperty().get();

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            return transactions;
        }

        int offset = (page - 1) * pageSize;
        ResultSet resultSet = null;
        try {
            resultSet = databaseDriver.searchTransactionsByDateRange(
                    payeeAddress, startDate, endDate, offset, pageSize
            );

            if (resultSet != null) {
                while (resultSet.next()) {
                    String sender = resultSet.getString("Sender");
                    String receiver = resultSet.getString("Receiver");
                    double amount = resultSet.getDouble("Amount");
                    String message = resultSet.getString("Message");
                    if (message == null) message = "";

                    String dateString = resultSet.getString("Date");
                    LocalDate date = LocalDate.now();
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
                    }

                    Transaction transaction = new Transaction(sender, receiver, amount, date, message);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return transactions;
    }
    /**
     * Transfer money from checking to savings account
     * @param amount Amount to transfer
     * @return true if successful
     */
    public boolean transferToSavings(double amount) {
        lastErrorMessage = "";

        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            lastErrorMessage = "User not logged in";
            return false;
        }

        // Validate amount
        if (amount <= 0) {
            lastErrorMessage = "Amount must be greater than 0";
            return false;
        }

        // Check if user has checking account
        Account checkingAccount = client.CheckingAccountProperty().get();
        if (checkingAccount == null) {
            lastErrorMessage = "You do not have a checking account";
            return false;
        }

        // Check if user has savings account
        Account savingsAccount = client.SavingsAccountProperty().get();
        if (savingsAccount == null) {
            lastErrorMessage = "You do not have a savings account";
            return false;
        }

        // Check sufficient balance
        double currentBalance = checkingAccount.BalanceProperty().get();
        if (currentBalance < amount) {
            lastErrorMessage = "Insufficient funds in checking account. Current balance: " +
                    String.format("%.2f", currentBalance) + " MAD";
            return false;
        }

        // Perform transfer
        if (databaseDriver.transferCheckingToSavings(payeeAddress, amount)) {
            // Refresh accounts
            refreshClientAccounts();
            return true;
        } else {
            lastErrorMessage = "Failed to process transfer. Please try again.";
            return false;
        }
    }

    /**
     * Transfer money from savings to checking account
     * @param amount Amount to transfer
     * @return true if successful
     */
    public boolean transferToChecking(double amount) {
        lastErrorMessage = "";

        String payeeAddress = client.PayeeAddressProperty().get();
        if (payeeAddress == null || payeeAddress.isEmpty()) {
            lastErrorMessage = "User not logged in";
            return false;
        }

        // Validate amount
        if (amount <= 0) {
            lastErrorMessage = "Amount must be greater than 0";
            return false;
        }

        // Check if user has savings account
        Account savingsAccount = client.SavingsAccountProperty().get();
        if (savingsAccount == null) {
            lastErrorMessage = "You do not have a savings account";
            return false;
        }

        // Check if user has checking account
        Account checkingAccount = client.CheckingAccountProperty().get();
        if (checkingAccount == null) {
            lastErrorMessage = "You do not have a checking account";
            return false;
        }

        // Check sufficient balance
        double currentBalance = savingsAccount.BalanceProperty().get();
        if (currentBalance < amount) {
            lastErrorMessage = "Insufficient funds in savings account. Current balance: " +
                    String.format("%.2f", currentBalance) + " MAD";
            return false;
        }

        // Perform transfer
        if (databaseDriver.transferSavingsToChecking(payeeAddress, amount)) {
            // Refresh accounts
            refreshClientAccounts();
            return true;
        } else {
            lastErrorMessage = "Failed to process transfer. Please try again.";
            return false;
        }
    }
    /**
     * Reset user password (Admin function)
     * @param payeeAddress User's PayeeAddress
     * @param newPassword New password
     * @return true if successful
     */
    public boolean resetUserPassword(String payeeAddress, String newPassword) {
        lastErrorMessage = "";

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            lastErrorMessage = "Payee Address is required";
            return false;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            lastErrorMessage = "Password is required";
            return false;
        }

        if (newPassword.length() < 4) {
            lastErrorMessage = "Password must be at least 4 characters long";
            return false;
        }

        // Check if user exists
        if (!databaseDriver.checkPayeeAddressExists(payeeAddress)) {
            lastErrorMessage = "User not found";
            return false;
        }

        // Reset password
        if (databaseDriver.resetUserPassword(payeeAddress, newPassword)) {
            return true;
        } else {
            lastErrorMessage = "Failed to reset password. Database error.";
            return false;
        }
    }
}