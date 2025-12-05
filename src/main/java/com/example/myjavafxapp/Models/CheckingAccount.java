package com.example.myjavafxapp.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CheckingAccount extends Account {
    //number of transactions a client is allowed to de per day.
    private final IntegerProperty TransactionLimit;

    public CheckingAccount(String Owner, String AccountNumber, double Balance, int TransactionLimit) {
        super(Owner, AccountNumber, Balance);
        this.TransactionLimit = new SimpleIntegerProperty(this, "TransactionLimit", TransactionLimit);
    }

    public IntegerProperty TransactionLimitProperty() {
        return TransactionLimit;
    }

}
