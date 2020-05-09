package com.dev175.xpensify.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Activity.HomeActivity;
import com.dev175.xpensify.Adapter.ManageGroupsDisplayFragmentAdapter;
import com.dev175.xpensify.Common.Common;
import com.dev175.xpensify.Model.Group;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageGroupsDisplayFragment extends Fragment {

    private Group groupObj;
    private ArrayList<GroupUser> groupUsers;
    private String groupId;
    private String groupName;


    private AppCompatButton btnLeaveGroup;

    private static final String TAG = ManageGroupsDisplayFragment.class.getSimpleName();
    RecyclerView.LayoutManager rLayoutManager;
    RecyclerView recycler_manageGroupDisplay;
    ManageGroupsDisplayFragmentAdapter manageGroupsDisplayFragmentAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_groups_display,container,false);


        btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
        recycler_manageGroupDisplay = (RecyclerView)view.findViewById(R.id.recycler_manageGroupDisplay);
        rLayoutManager = new LinearLayoutManager(getContext());
        recycler_manageGroupDisplay.setLayoutManager(rLayoutManager);

        manageGroupsDisplayFragmentAdapter=new ManageGroupsDisplayFragmentAdapter(getContext(),groupUsers);
        recycler_manageGroupDisplay.setAdapter(manageGroupsDisplayFragmentAdapter);

        btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });

        return view;
    }

    private void leaveGroup() {

   //     Toast.makeText(getContext(), "groupId = "+groupId +" groupName = "+groupName, Toast.LENGTH_SHORT).show();

    final WriteBatch  batch= FirebaseFirestore.getInstance().batch();



       String uid = FirebaseAuth.getInstance().getUid();
        //Remove "group" data from user collection and then later remove user data from other users collections
        final DocumentReference groupDoc = FirebaseFirestore.getInstance()
                .collection(Common.users)
                .document(uid)
                .collection(Common.groups)
                .document(groupId);



        groupDoc.collection(Common.members)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot querySnapshot: task.getResult())
                            {
                                DocumentReference docRef = groupDoc.collection(Common.members).document(querySnapshot.getId());
                                batch.delete(docRef);
                                Log.d(TAG, "onComplete: "+querySnapshot.getId());
                            }

                            Map<String ,Object> group = new HashMap<>();
                            group.put(Common.groupStatus,Common.left);
                            group.put(Common.groupName,groupName);

                            batch.update(groupDoc,group);

                            batch.commit()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onFailure: Batch "+e.getMessage() );
                                }
                           });



                        }
                        else {
                            Log.e(TAG, "onFailure: "+task.getException() );
                        }
                    }
                });

        //All members deleted from user group.
        //But user is not completely removed from other users group collection.

        //Now i will get those users that were present in this group. except me.
        //groupUsers contains all groups.ill remove myself from this collection first

        ArrayList<GroupUser> members = new ArrayList<>();
        for (GroupUser groupUser : groupUsers)
        {
            if (!PhoneNumberUtils.compare(Common.currentUser.getPhoneNumber(),groupUser.getPhoneNumber()))
            {
                members.add(groupUser);
            }
        }

        getRegisteredPhonenumbersFromDb(members);


        //Now in members, there are members of group in which i have to go remove this user


        Toast.makeText(getContext(), "Group Left Successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    //Get all docs in "phonenumbers" Collection and then add these doc ids in arraylist
    private void getRegisteredPhonenumbersFromDb(final ArrayList<GroupUser> members)
    {

        CollectionReference phoneNumbersCol = FirebaseFirestore.getInstance().collection(Common.phoneNumbers);
        phoneNumbersCol.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for ( QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                           String memberId = documentSnapshot.getId();
                           String phoneNumber =  (String) documentSnapshot.get(Common.phoneNumber);
                           for (GroupUser member : members)
                           {
                               if (PhoneNumberUtils.compare(phoneNumber,member.getPhoneNumber()))
                               {
                                   Log.d(TAG, "onSuccess: memberId = "+memberId);
                                   Log.d(TAG, "onSuccess: memberPhoneNumber = "+phoneNumber);

                                   member.setGroupUserId(memberId);
                               }
                           }


                        }
                        deleteUserFromMembers(members);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "onFailure: "+e.getMessage());
                    }
                });


    }

    private void deleteUserFromMembers(final ArrayList<GroupUser> members) {

        //All the members with their doc id has received

        for (GroupUser groupUser :members)
        {

            final CollectionReference groupsCol = FirebaseFirestore.getInstance()
                    .collection(Common.users)
                    .document(groupUser.getGroupUserId())
                    .collection(Common.groups);

            groupsCol
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                            {
                                String memberGroupName = (String) queryDocumentSnapshot.get(Common.groupName);
                                String groupStatus = (String) queryDocumentSnapshot.get(Common.groupStatus);

                                if (memberGroupName.equals(groupName))
                                {
                                    Log.d(TAG, "onSuccess: Group Exist");

                                    if (groupStatus.equals(Common.joined))
                                    {
                                        Log.d(TAG, "onSuccess: "+Common.joined);
                                        final CollectionReference membersCollection = groupsCol
                                                .document(queryDocumentSnapshot.getId())
                                                .collection(Common.members);

                                        deleteMember(membersCollection);


                                    }
                                }

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: "+e.getMessage() );
                        }
                    });




        }



    }

    private void deleteMember(final CollectionReference membersCollection) {
        membersCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot queryDocument :queryDocumentSnapshots)
                        {
                            Object groupUserObj =  queryDocument.get(Common.members);
                            Map<String,String> map = (HashMap)groupUserObj;

                            String phoneNumber = map.get(Common.phoneNumber);
                            if (Common.currentUser.getPhoneNumber().equals(phoneNumber))
                            {
                                Log.d(TAG, "onSuccess: matched");
                                DocumentReference userDoc = membersCollection.document(queryDocument.getId());
                                userDoc.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user deleted from other users");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: "+e.getMessage());
                                            }
                                        });

                            }

                        }
                    }
                });
    }


    public void setData(Group groupObj, ArrayList<GroupUser> groupUsers,String groupId,String groupName) {
        this.groupObj = groupObj;
        this.groupUsers=groupUsers;
        this.groupId = groupId;
        this.groupName = groupName;
    }
}
