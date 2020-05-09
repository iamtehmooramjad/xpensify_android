package com.dev175.xpensify.Interface;

import com.dev175.xpensify.Model.Group;

public interface IGetGroupExpensesListener {
    void onGetGroupExpenseSuccess(Group groupObj);
    void onGetGroupExpenseFailed(String message);
}
