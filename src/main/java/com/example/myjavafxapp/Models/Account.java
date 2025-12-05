package com.example.myjavafxapp.Models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Account {
    private final StringProperty Owner;
    private final StringProperty AccountNumber;
    private final DoubleProperty Balance;

    public Account(String Owner, String AccountNumber, double Balance) {
        this.Owner = new SimpleStringProperty(this, "Owner", Owner);
        this.AccountNumber = new SimpleStringProperty(this, "AccountNumber", AccountNumber);
        this.Balance = new SimpleDoubleProperty(this, "Balance", Balance);
    }

    public StringProperty OwnerProperty() {
        return Owner;
    }
    public StringProperty AccountNumberProperty() {
        return AccountNumber;
    }
    public DoubleProperty BalanceProperty() {
        return Balance;
    }
}
