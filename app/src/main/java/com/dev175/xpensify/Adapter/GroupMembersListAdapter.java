package com.dev175.xpensify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Interface.IRecyclerViewClickListener;
import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;

import java.util.ArrayList;

public class GroupMembersListAdapter extends RecyclerView.Adapter<GroupMembersListAdapter.MyViewHolder> {

    private ArrayList<GroupUser> groupUsers;
    private Context context;
    private IRecyclerViewClickListener iRecyclerViewClickListener;

    public GroupMembersListAdapter(ArrayList<GroupUser> groupUsers, Context context,IRecyclerViewClickListener listener) {
        this.groupUsers = groupUsers;
        this.context = context;
        this.iRecyclerViewClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_groupmember,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView memberName = holder.memberName;
        TextView memberNumber = holder.memberNumber;
        ImageView memberImg = holder.memberImg;
        CheckBox memberCb = holder.memberCb;

        memberName.setText(groupUsers.get(position).getFullName());
        memberNumber.setText(groupUsers.get(position).getPhoneNumber());


    }

    @Override
    public int getItemCount() {
        return groupUsers.size();
    }

     class MyViewHolder extends RecyclerView.ViewHolder{
            TextView memberName;
            TextView memberNumber;
            ImageView memberImg;
            CheckBox memberCb;



        public MyViewHolder(@NonNull final View itemView) {
             super(itemView);
             this.memberName = itemView.findViewById(R.id.gMemberName);
             this.memberNumber = itemView.findViewById(R.id.gMemberNumber);
             this.memberImg = itemView.findViewById(R.id.gMemberImg);
             this.memberCb = itemView.findViewById(R.id.gMemberCb);
             this.memberCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if (isChecked)
                     {
                         iRecyclerViewClickListener.recyclerViewItemClicked(getAdapterPosition());
                     }
                     else if (!isChecked){
                         iRecyclerViewClickListener.recyclerViewItemUnClicked(getAdapterPosition());
                     }
                 }
             });

        }
     }
}
