package com.example.myjavafxapp.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SavingsAccount extends Account{
    //withdrawal limit from the Savings
    private final DoubleProperty WithDrawalLimit;

    public SavingsAccount(String Owner, String AccountNumber, double Balance, double WithDrawalLimit) {
        super(Owner, AccountNumber, Balance);
        this.WithDrawalLimit = new SimpleDoubleProperty(this, "WithDrawalLimit", WithDrawalLimit);
    }

    public DoubleProperty WithDrawalLimitProperty() {
        return WithDrawalLimit;
    }
}
