package com.dev175.xpensify.Interface;

import com.dev175.xpensify.Model.Group;

public interface IGetGroupMembersListener {
    void onGetGroupMembersSuccess(Group groupObj);
    void onGetGroupMembersFailed(String message);
}
