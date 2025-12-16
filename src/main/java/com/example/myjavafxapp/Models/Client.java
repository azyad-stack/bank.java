package com.example.myjavafxapp.Models;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Client {
    private final StringProperty FirstName;
    private final StringProperty LastName;
    private final StringProperty PayeeAddress;
    public final ObjectProperty<Account> CheckingAccount;
    public final ObjectProperty<Account> SavingsAccount;
    private final ObjectProperty<LocalDate> DateCreated;

    public Client(String FirstName, String LastName, String PayeeAddress, Account CheckingAccount, Account SavingsAccount, LocalDate DateCreated) {
        this.FirstName = new SimpleStringProperty(this , "FirstName", FirstName);
        this.LastName = new SimpleStringProperty(this, "LastName", LastName);
        this.PayeeAddress = new SimpleStringProperty(this , "PayeeAddress", PayeeAddress);
        this.CheckingAccount = new SimpleObjectProperty<>(this , "CheckingAccount", CheckingAccount);
        this.SavingsAccount = new SimpleObjectProperty<>(this , "SavingsAccount", SavingsAccount);
        this.DateCreated = new SimpleObjectProperty<>(this , "DateCreated", DateCreated);
    }

    public StringProperty FirstNameProperty() {
        return FirstName;
    }
    public StringProperty LastNameProperty() {
        return LastName;
    }
    public StringProperty PayeeAddressProperty() {
        return PayeeAddress;
    }
    public ObjectProperty<Account> CheckingAccountProperty() {
        return CheckingAccount;
    }
    public ObjectProperty<Account> SavingsAccountProperty() {
        return SavingsAccount;
    }
    public ObjectProperty<LocalDate> DateCreatedProperty() {
        return DateCreated;
    }

}
