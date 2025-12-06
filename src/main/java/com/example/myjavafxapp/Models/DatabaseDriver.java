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
        Statement statement;
        ResultSet resultSet = null;
        try{
            statement = this.connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients WHERE PayeeAdress = '" + pAddress + "' AND Password = '" + password + "';");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }
}
