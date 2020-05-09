package com.dev175.xpensify.Model;

import java.util.ArrayList;

public class Expense {

    private String title;
    private String amount;
    private String timestamp;
    private ArrayList<ExpenseUser> expenseUserList;


    public Expense(String title, String amount, ArrayList<ExpenseUser> expenseUserList, String timestamp) {
        this.title = title;
        this.amount = amount;
        this.expenseUserList = expenseUserList;
        this.timestamp = timestamp;
    }

    public Expense() {
    }

    public void addExpenseUserInList(ExpenseUser user)
    {
        this.expenseUserList.add(user);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public ArrayList<ExpenseUser> getExpenseUserList() {
        return expenseUserList;
    }

    public void setExpenseUserList(ArrayList<ExpenseUser> expenseUserList) {
        this.expenseUserList = expenseUserList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
