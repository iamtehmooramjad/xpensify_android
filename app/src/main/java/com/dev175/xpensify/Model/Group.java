package com.dev175.xpensify.Model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group{

    private ArrayList<String> groupIdList;  //Contains group ids of groups
    private Map<String,String> groupNameList;//Give group id and get GroupName
    private Map<String,String> groupNameIdList;//Give group Name and get GroupId (for Manage Group fragment)
    // when click on group Name, get its id and the get all group member by giving this id to groupUserMap
    private Map<String,ArrayList<GroupUser>> groupUsersMap;

    //Give group id and get all group expenses
    private Map<String,ArrayList<Expense>> groupExpenseMap;


    public Group()
    {
        this.groupIdList = new ArrayList<String>();
        this.groupUsersMap= new HashMap<>();
        this.groupExpenseMap= new HashMap<>();
        this.groupNameList=new HashMap<>();
        this.groupNameIdList=new HashMap<>();
    }

    public ArrayList<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(ArrayList<String> groupIdList) {
        this.groupIdList = groupIdList;
    }

    public Map<String, ArrayList<GroupUser>> getGroupUsersMap() {
        return groupUsersMap;
    }


    public Map<String, ArrayList<Expense>> getGroupExpenseMap() {
        return groupExpenseMap;
    }

    public void setGroupUsersMap(String key,ArrayList<GroupUser> groupUsers) {
        this.groupUsersMap.put(key,groupUsers);
    }

    public void setGroupExpenseMap(String key,ArrayList<Expense> groupExpense) {
        this.groupExpenseMap.put(key,groupExpense);
    }

    public Map<String, String> getGroupNameList() {
        return groupNameList;
    }

    public void setGroupNameList(String groupId,String groupName) {
        this.groupNameList.put(groupId,groupName);
    }

    public Map<String, String> getGroupNameIdList() {
        return groupNameIdList;
    }

    public void setGroupNameIdList(String groupName,String groupId) {
        this.groupNameIdList.put(groupName,groupId);
    }

}