package com.dev175.xpensify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dev175.xpensify.Interface.IRecyclerViewClickListener;
import com.dev175.xpensify.R;
import java.util.ArrayList;

public class ManageGroupsFragmentAdapter extends RecyclerView.Adapter<ManageGroupsFragmentAdapter.MyViewHolder> {

    ArrayList<String> groupUserArrayList;
    Context context;
    IRecyclerViewClickListener iRecyclerViewClickListener;


    public ManageGroupsFragmentAdapter(ArrayList<String> groupUserArrayList, Context context,IRecyclerViewClickListener iRecyclerViewClickListener) {
        this.groupUserArrayList = groupUserArrayList;
        this.context = context;
        this.iRecyclerViewClickListener = iRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_group,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final TextView groupName = holder.groupName;
        TextView moreInfoTV = holder.moreInfoTV;
        ImageView moreInfo = holder.moreInfo;


        groupName.setText(groupUserArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return groupUserArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {

        TextView groupName;
        TextView moreInfoTV;
        ImageView moreInfo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupName = itemView.findViewById(R.id.groupName);
            this.moreInfoTV = itemView.findViewById(R.id.moreInfoTV);
            this.moreInfo = itemView.findViewById(R.id.moreImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iRecyclerViewClickListener.recyclerViewItemClicked(getAdapterPosition());
                }
            });

        }

    }
}
