package com.dev175.xpensify.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Activity.ManageGroupActivity;
import com.dev175.xpensify.Adapter.ManageGroupsFragmentAdapter;
import com.dev175.xpensify.Interface.IRecyclerViewClickListener;
import com.dev175.xpensify.Model.Group;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;
import java.util.ArrayList;
import java.util.Map;

public class ManageGroupsFragment extends Fragment  implements IRecyclerViewClickListener{

    private static final String TAG = ManageGroupsFragment.class.getSimpleName();
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recycler_manageGroup;
    Group groupObj;
    ManageGroupsFragmentAdapter manageGroupsFragmentAdapter;
    ArrayList<String>  groups;
    String groupName; //groupName from fragment ManageGroupsFragment.When item is clicked

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_groups,container,false);


        recycler_manageGroup = (RecyclerView)view.findViewById(R.id.recycler_manageGroup);
        layoutManager = new LinearLayoutManager(getContext());
        recycler_manageGroup.setLayoutManager(layoutManager);

        manageGroupsFragmentAdapter = new ManageGroupsFragmentAdapter(groups,getContext(),this);
        recycler_manageGroup.setAdapter(manageGroupsFragmentAdapter);

        return view;
    }

    //Get Obj from activity and set it in fragment
    public void setGroupObj(Group groupObj, ArrayList<String> groups)
    {
        this.groupObj = groupObj;
        this.groups = groups;
    }


    @Override
    public void recyclerViewItemClicked(int position) {
//        Toast.makeText(getContext(), groups.get(position), Toast.LENGTH_SHORT).show();
        groupName = groups.get(position);

        //Now send this groupName to ManageGroupsDisplayFragment to display contacts
        String group_Id = groupObj.getGroupNameIdList().get(groupName);

        //This array List contacins group members inside the selected group.
        ArrayList<GroupUser> groupUserArrayList =   groupObj.getGroupUsersMap().get(group_Id);

        //Now pass this groupserArrayList

        addManageGroupsDisplayFragment(groupObj,groupUserArrayList,group_Id,groupName);

    }

    @Override
    public void recyclerViewItemUnClicked(int position) {

    }

    //Add Second fragment : ManageGroupsDisplayFragment

    public void addManageGroupsDisplayFragment(Group groupObj,ArrayList<GroupUser> groupUsers,String groupId,String groupName)
    {
        ManageGroupsDisplayFragment fragment = new ManageGroupsDisplayFragment();

        if (fragment!=null)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.container,fragment,"manageGroupsDisplayAddF");
            Log.i(TAG, "addManageGroupsDisplayFragment: added");
            transaction.addToBackStack("addManageGroupDisplayF");
            fragment.setData(groupObj,groupUsers,groupId,groupName);
            transaction.commit();
        }
        else {
//            Toast.makeText(getContext(), "ManageGroupsDisplayFragment not found", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "addManageGroupsDisplayFragment: not Found!!");
        }
    }

}
