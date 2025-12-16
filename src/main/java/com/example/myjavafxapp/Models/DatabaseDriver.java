package com.example.myjavafxapp.Models;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.sql.DriverManager.getConnection;

public class DatabaseDriver {
    private Connection connection;

    public DatabaseDriver(){
        try{
            this.connection = getConnection("jdbc:sqlite:bank.java.db");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    /*
     * Client Section
     */

    public ResultSet getClientData(String pAddress, String password){
        String query = "SELECT * FROM Clients WHERE PayeeAddress = ? AND Password = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, pAddress);
            statement.setString(2, password);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if a PayeeAddress already exists in the database
     * @param payeeAddress The PayeeAddress to check
     * @return true if exists, false otherwise
     */
    public boolean checkPayeeAddressExists(String payeeAddress) {
        String query = "SELECT COUNT(*) FROM Clients WHERE PayeeAddress = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, payeeAddress);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new client in the database
     * @param firstName First name of the client
     * @param lastName Last name of the client
     * @param payeeAddress Payee address (unique identifier)
     * @param password Password for the client
     * @param dateCreated Date when the client was created
     * @return true if successful, false otherwise
     */
    public boolean createClient(String firstName, String lastName, String payeeAddress, String password, LocalDate dateCreated) {
        String query = "INSERT INTO Clients (FirstName, LastName, PayeeAddress, Password, Date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, payeeAddress);
            statement.setString(4, password);
            statement.setString(5, dateCreated.format(DateTimeFormatter.ISO_DATE));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate a unique account number for checking or savings account
     * @param accountType "CHK" for checking, "SAV" for savings
     * @return A unique account number
     */
    public String generateAccountNumber(String accountType) {
        String prefix = accountType.equals("CHK") ? "CHK" : "SAV";
        String tableName = accountType.equals("CHK") ? "CheckingAccounts" : "SavingsAccounts";
        String accountNumber;
        int attempts = 0;
        int maxAttempts = 100;

        do {
            // Generate a 6-digit random number
            int randomNum = (int)(Math.random() * 900000) + 100000;
            accountNumber = prefix + "-" + randomNum;
            attempts++;

            if (attempts >= maxAttempts) {
                // Fallback: use timestamp-based number
                accountNumber = prefix + "-" + System.currentTimeMillis();
                break;
            }
        } while (accountNumberExists(accountNumber, tableName));

        return accountNumber;
    }

    /**
     * Check if an account number already exists
     * @param accountNumber The account number to check
     * @param tableName The table name (CheckingAccounts or SavingsAccounts)
     * @return true if exists, false otherwise
     */
    private boolean accountNumberExists(String accountNumber, String tableName) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE AccountNumber = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a checking account in the database
     * @param owner PayeeAddress of the account owner
     * @param accountNumber Unique account number
     * @param balance Initial balance
     * @param transactionLimit Transaction limit per day
     * @return true if successful, false otherwise
     */
    public boolean createCheckingAccount(String owner, String accountNumber, double balance, int transactionLimit) {
        String query = "INSERT INTO CheckingAccounts (Owner, AccountNumber, TransactionLimit, Balance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, owner);
            statement.setString(2, accountNumber);
            statement.setDouble(3, balance);
            statement.setInt(4, transactionLimit);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a savings account in the database
     * @param owner PayeeAddress of the account owner
     * @param accountNumber Unique account number
     * @param balance Initial balance
     * @return true if successful, false otherwise
     */
    public boolean createSavingsAccount(String owner, String accountNumber, double balance) {
        String query = "INSERT INTO SavingsAccounts (Owner, AccountNumber, Balance) VALUES (?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, owner);
            statement.setString(2, accountNumber);
            statement.setDouble(3, balance);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create client and accounts within a transaction
     * This ensures atomicity - if any step fails, everything is rolled back
     * @param firstName First name
     * @param lastName Last name
     * @param payeeAddress Payee address
     * @param password Password (hashed)
     * @param dateCreated Date created
     * @param checkingBalance Checking account balance (null if not creating)
     * @param savingsBalance Savings account balance (null if not creating)
     * @return true if successful, false otherwise
     */




    /**
     * Get all clients from the database
     * @return ResultSet containing all clients
     */
    public ResultSet getAllClients() {
        String query = "SELECT * FROM Clients ORDER BY Date DESC";
        try {
            Statement statement = this.connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get checking account for a client by PayeeAddress
     * @param payeeAddress The PayeeAddress of the client
     * @return ResultSet containing the checking account, or null if not found
     */
    public ResultSet getCheckingAccount(String payeeAddress) {
        String query = "SELECT * FROM CheckingAccounts WHERE Owner = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, payeeAddress);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get savings account for a client by PayeeAddress
     * @param payeeAddress The PayeeAddress of the client
     * @return ResultSet containing the savings account, or null if not found
     */
    public ResultSet getSavingsAccount(String payeeAddress) {
        // Try both possible column names (WithdrawalLimit vs WithdrawLimit)
        String query = "SELECT * FROM SavingsAccounts WHERE Owner = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, payeeAddress);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete a checking account by PayeeAddress (Owner)
     * @param payeeAddress The PayeeAddress of the client
     * @return true if successful, false otherwise
     */
    public boolean deleteCheckingAccount(String payeeAddress) {
        String query = "DELETE FROM CheckingAccounts WHERE Owner = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, payeeAddress);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected >= 0; // >= 0 because account might not exist
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a savings account by PayeeAddress (Owner)
     * @param payeeAddress The PayeeAddress of the client
     * @return true if successful, false otherwise
     */
    public boolean deleteSavingsAccount(String payeeAddress) {
        String query = "DELETE FROM SavingsAccounts WHERE Owner = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, payeeAddress);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected >= 0; // >= 0 because account might not exist
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a client by PayeeAddress
     * Note: Should delete accounts first before deleting client
     * @param payeeAddress The PayeeAddress of the client
     * @return true if successful, false otherwise
     */
    public boolean deleteClient(String payeeAddress) {
        String query = "DELETE FROM Clients WHERE PayeeAddress = ?";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, payeeAddress);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all transactions for a client (both sent and received)
     * @param payeeAddress The PayeeAddress of the client
     * @return ResultSet containing all transactions
     * Note: Statement stays open until ResultSet is closed by caller
     */
    public ResultSet getClientTransactions(String payeeAddress) {
        String query = "SELECT * FROM Transactions WHERE Sender = ? OR Receiver = ? ORDER BY Date DESC LIMIT 10";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, payeeAddress);
            statement.setString(2, payeeAddress);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getAdminData(String username, String password) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            // Check if you have an Admins table or if admins are in Clients table
            // Assuming you have an Admins table:
            String query = "SELECT * FROM Admins WHERE Username = ? AND Password = ?";
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
   //To search for client
   public ResultSet getSearchedClient(String PayeeAddress) {
       String query = "SELECT * FROM Clients WHERE PayeeAddress = ?";  // Changed to SELECT *
       try {
           PreparedStatement preparedStatement = this.connection.prepareStatement(query);  // Use PreparedStatement
           preparedStatement.setString(1, PayeeAddress);  // Bind parameter
           return preparedStatement.executeQuery();
       } catch (SQLException e) {
           e.printStackTrace();
           return null;
       }
   }
   //Deposit
   public boolean depositToCheckingAccount(String payeeAddress, double amount) {
       String query = "UPDATE CheckingAccounts SET Balance = Balance + ? WHERE Owner = ?";
       try {
           PreparedStatement preparedStatement = this.connection.prepareStatement(query);
           preparedStatement.setDouble(1, amount);
           preparedStatement.setString(2, payeeAddress);

           int rowsAffected = preparedStatement.executeUpdate();
           return rowsAffected > 0;
       } catch (SQLException e) {
           e.printStackTrace();
           return false;
       }
   }
    /**
     * Create a transaction and update account balances atomically
     * @param sender PayeeAddress of the sender
     * @param receiver PayeeAddress of the receiver
     * @param amount Amount to transfer
     * @param message Optional message for the transaction
     * @param date Date of the transaction
     * @return true if successful, false otherwise
     */
    public boolean createTransaction(String sender, String receiver, double amount, String message, LocalDate date) {
        // Use transaction to ensure atomicity
        try {
            connection.setAutoCommit(false);

            // 1. Verify sender has checking account with sufficient balance
            String checkBalanceQuery = "SELECT Balance FROM CheckingAccounts WHERE Owner = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkBalanceQuery)) {
                checkStmt.setString(1, sender);
                ResultSet balanceResult = checkStmt.executeQuery();

                if (!balanceResult.next()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false; // Sender has no checking account
                }

                double currentBalance = balanceResult.getDouble("Balance");
                if (currentBalance < amount) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false; // Insufficient funds
                }
            }

            // 2. Deduct amount from sender's checking account
            String deductQuery = "UPDATE CheckingAccounts SET Balance = Balance - ? WHERE Owner = ?";
            try (PreparedStatement deductStmt = connection.prepareStatement(deductQuery)) {
                deductStmt.setDouble(1, amount);
                deductStmt.setString(2, sender);
                int rowsAffected = deductStmt.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false; // Failed to deduct
                }
            }

            // 3. Add amount to receiver's checking account (if exists)
            String addQuery = "UPDATE CheckingAccounts SET Balance = Balance + ? WHERE Owner = ?";
            try (PreparedStatement addStmt = connection.prepareStatement(addQuery)) {
                addStmt.setDouble(1, amount);
                addStmt.setString(2, receiver);
                addStmt.executeUpdate(); // Don't fail if receiver has no account
            }

            // 4. Record transaction in Transactions table
            String transactionQuery = "INSERT INTO Transactions (Sender, Receiver, Amount, Message, Date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement transStmt = connection.prepareStatement(transactionQuery)) {
                transStmt.setString(1, sender);
                transStmt.setString(2, receiver);
                transStmt.setDouble(3, amount);
                transStmt.setString(4, message != null ? message : "");
                transStmt.setString(5, date.format(DateTimeFormatter.ISO_DATE));
                transStmt.executeUpdate();
            }

            // Commit transaction
            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                e.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Begin a database transaction (set autocommit to false)
     */
    public void beginTransaction() throws SQLException {
        this.connection.setAutoCommit(false);
    }
    

    /**
     * Commit the current transaction
     */
    public void commitTransaction() throws SQLException {
        this.connection.commit();
        this.connection.setAutoCommit(true);
    }

    /**
     * Rollback the current transaction
     */
    public void rollbackTransaction() {
        try {
            this.connection.rollback();
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
