package com.bankofapis.core.model.accounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class CredibilityAccount {

    @JsonProperty("AccountId")
    private String accountId;

    @JsonProperty("AccountType")
    private String accountType;

    @JsonProperty("AccountSubType")
    private String accountSubType;

    @JsonProperty("YearlyCredit")
    private double yearlyCredit;

    @JsonProperty("YearlyDebit")
    private double yearlyDebit;

    @JsonProperty("Balance")
    private double balance;

    @JsonProperty("Currency")
    private String currency;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getYearlyCredit() {
        return yearlyCredit;
    }

    public void setYearlyCredit(double yearlyCredit) {
        this.yearlyCredit = yearlyCredit;
    }

    public double getYearlyDebit() {
        return yearlyDebit;
    }

    public void setYearlyDebit(double yearlyDebit) {
        this.yearlyDebit = yearlyDebit;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountSubType() {
        return accountSubType;
    }

    public void setAccountSubType(String accountSubType) {
        this.accountSubType = accountSubType;
    }
}
