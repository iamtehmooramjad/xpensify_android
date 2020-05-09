package com.dev175.xpensify.Common;

import com.dev175.xpensify.Model.Contact;
import com.dev175.xpensify.Model.User;
import java.util.ArrayList;

public class Common {



    public static ArrayList<Contact> contactsArrayList;

    //These are static keys for map
    public static final String fullName = "fullName";
    public static final String phoneNumber = "phoneNumber";
    public static final String password = "password";
    public static final String email = "email";
    public static final String uri = "uri";

    public static final String registered = "registered";

    public static final String groupName = "groupName";
    public static final String groupId = "groupId";

    //FirebaseCollections
    public static final String users = "users";
    public static final String groups = "groups";
    public static final String members = "members";
    public static final String expenses = "expenses";
    public static final String notifications = "notifications";

    //View Expense
    public static final String title = "title";
    public static final String amount = "amount";
    public static final String timestamp = "timestamp";
    public static final String expenseUserList = "expenseUserList";



    //To get Current User on signup,as state of user will remain same during session
    public static User currentUser;

    //Save the contacts on creating group to access them in the next activity
    public static ArrayList<Contact> groupContactsList = new ArrayList<Contact>();

    public static final String groupStatus = "groupStatus";
    public static final String joined = "joined";
    public static final String left = "left";
    public static final String phoneNumbers = "phonenumbers";
}

