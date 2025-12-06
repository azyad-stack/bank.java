package com.example.myjavafxapp.Models;

import java.sql.*;

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
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            // ✅ Use PreparedStatement instead of concatenating strings
            String query = "SELECT * FROM Clients WHERE PayeeAddress = ? AND Password = ?";
            preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, pAddress);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    /*
    * Admin Data Section
     */

    /**
     * Get admin data from database
     */
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
