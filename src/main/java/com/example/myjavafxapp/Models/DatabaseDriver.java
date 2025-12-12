package com.example.myjavafxapp.Models;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseDriver {
    private Connection connection;

    public DatabaseDriver(){
        try{
            this.connection = DriverManager.getConnection("jdbc:sqlite:bank.java.db");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    /*
     * Client Section
     */

    public ResultSet getClientData(String pAddress, String password){
        Statement statement;
        ResultSet resultSet = null;
        try{
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients WHERE PayeeAddress = '" + pAddress + "' AND Password = '" + password + "';");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
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
     * @param dateCreated Date when account was created
     * @return true if successful, false otherwise
     */
    public boolean createCheckingAccount(String owner, String accountNumber, double balance, int transactionLimit, LocalDate dateCreated) {
        String query = "INSERT INTO CheckingAccounts (Owner, AccountNumber, Balance, TransactionLimit, DateCreated) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, owner);
            statement.setString(2, accountNumber);
            statement.setDouble(3, balance);
            statement.setInt(4, transactionLimit);
            statement.setString(5, dateCreated.format(DateTimeFormatter.ISO_DATE));

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
     * @param withdrawalLimit Withdrawal limit
     * @param dateCreated Date when account was created
     * @return true if successful, false otherwise
     */
    public boolean createSavingsAccount(String owner, String accountNumber, double balance, double withdrawalLimit, LocalDate dateCreated) {
        String query = "INSERT INTO SavingsAccounts (Owner, AccountNumber, Balance, WithdrawalLimit, DateCreated) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setString(1, owner);
            statement.setString(2, accountNumber);
            statement.setDouble(3, balance);
            statement.setDouble(4, withdrawalLimit);
            statement.setString(5, dateCreated.format(DateTimeFormatter.ISO_DATE));

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
     */
    public ResultSet getClientTransactions(String payeeAddress) {
        String query = "SELECT * FROM Transactions WHERE Sender = ? OR Receiver = ? ORDER BY Date DESC LIMIT 10";
        try (PreparedStatement statement = this.connection.prepareStatement(query)) {
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
}
