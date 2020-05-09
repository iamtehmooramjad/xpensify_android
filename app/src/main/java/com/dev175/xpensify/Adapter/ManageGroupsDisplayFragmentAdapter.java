package com.dev175.xpensify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Model.GroupUser;
import com.dev175.xpensify.R;

import java.util.ArrayList;

public class ManageGroupsDisplayFragmentAdapter extends RecyclerView.Adapter<ManageGroupsDisplayFragmentAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<GroupUser> groupUserArrayList;

    public ManageGroupsDisplayFragmentAdapter(Context context, ArrayList<GroupUser> groupUserArrayList) {
        this.context = context;
        this.groupUserArrayList = groupUserArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_member,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView memberName = holder.memberName;
        TextView memberPhone = holder.memberPhone;
        TextView memberGroup = holder.memberGroup;
        ImageView memberNameImg = holder.memberNameImg;
        ImageView memberPhoneImg = holder.memberPhoneImg;
        ImageView memberGroupImg = holder.memberGroupImg;

        memberName.setText(groupUserArrayList.get(position).getFullName());
        memberPhone.setText(groupUserArrayList.get(position).getPhoneNumber());
        memberGroup.setText(groupUserArrayList.get(position).getGroupName());

    }

    @Override
    public int getItemCount() {
        return groupUserArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        TextView memberPhone;
        TextView memberGroup;
        ImageView memberNameImg;
        ImageView memberPhoneImg;
        ImageView memberGroupImg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.memberName = itemView.findViewById(R.id.memberName);
            this.memberPhone = itemView.findViewById(R.id.memberPhone);
            this.memberGroup = itemView.findViewById(R.id.memberGroup);
            this.memberNameImg = itemView.findViewById(R.id.memberNameImg);
            this.memberPhoneImg = itemView.findViewById(R.id.memberPhoneImg);
            this.memberGroupImg = itemView.findViewById(R.id.memberGroupImg);

        }
    }
}
