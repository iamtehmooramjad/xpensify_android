package com.dev175.xpensify.Model;

public class ExpenseUser {

    private String name;
    private String phoneNumber;
    private String shareAmount; //40 in 120
    private Boolean userRole;  //Creditor or Debitor ->if true then creditor else debitor

    public ExpenseUser(String name, String phoneNumber, String shareAmount, Boolean userRole) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shareAmount = shareAmount;
        this.userRole = userRole;
    }

    public ExpenseUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(String shareAmount) {
        this.shareAmount = shareAmount;
    }

    public Boolean getUserRole() {
        return userRole;
    }

    public void setUserRole(Boolean userRole) {
        this.userRole = userRole;
    }
}
