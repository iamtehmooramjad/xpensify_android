package com.dev175.xpensify.Model;

import java.util.Comparator;

public class Contact  {

    private Boolean checked;
    private String name;
    private String phoneNumber;
    private String status;

    public Contact() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    //Sort Contacts By Name
    public static Comparator<Contact> contactListSortByName = new Comparator<Contact>() {

        public int compare(Contact s1, Contact s2) {
            String contact1 = s1.getName().toUpperCase();
            String contact2 = s2.getName().toUpperCase();

            //ascending order
            return contact1.compareTo(contact2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};


    //Sort Contacts By Status
    public static Comparator<Contact> contactListSortByStatus = new Comparator<Contact>() {

        public int compare(Contact s1, Contact s2) {
            String contact1 = s1.getStatus().toUpperCase();
            String contact2 = s2.getStatus().toUpperCase();

            //ascending order
            return contact1.compareTo(contact2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};

}

