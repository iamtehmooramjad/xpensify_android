package com.dev175.xpensify.Interface;

import com.dev175.xpensify.Model.Group;

public interface IGetGroupIdsListener {
     void onGetGroupsSuccess(Group groupObj);
     void onGetGroupsLoadFailed(String message);
}
