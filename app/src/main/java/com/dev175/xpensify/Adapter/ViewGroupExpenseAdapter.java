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
import com.dev175.xpensify.Model.Expense;
import com.dev175.xpensify.R;

import java.util.ArrayList;

public class ViewGroupExpenseAdapter extends RecyclerView.Adapter<ViewGroupExpenseAdapter.MyViewHolder> {

    private ArrayList<Expense> expenseArrayList;
    private Context context;
    private IRecyclerViewClickListener iRecyclerViewClickListener;


    public ViewGroupExpenseAdapter(ArrayList<Expense> expenseArrayList, Context context, IRecyclerViewClickListener iRecyclerViewClickListener) {
        this.expenseArrayList = expenseArrayList;
        this.context = context;
        this.iRecyclerViewClickListener = iRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_expense,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TextView expense_title = holder.expense_title;
          //  TextView expense_amount = holder.expense_amount;
            TextView expense_timestamp = holder.expense_timestamp;
            ImageView expense_more = holder.expense_more;


            expense_title.setText(expenseArrayList.get(position).getTitle());
            //expense_amount.setText(expenseArrayList.get(position).getAmount());
            expense_timestamp.setText(expenseArrayList.get(position).getTimestamp());

    }

    @Override
    public int getItemCount() {
        return expenseArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView expense_title;
  //      TextView expense_amount;
        TextView expense_timestamp;
        ImageView expense_more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.expense_title = itemView.findViewById(R.id.expense_title);
      //      this.expense_amount = itemView.findViewById(R.id.expense_amount);
            this.expense_timestamp = itemView.findViewById(R.id.expense_timestamp);
            this.expense_more = itemView.findViewById(R.id.expense_more);
            this.expense_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iRecyclerViewClickListener.recyclerViewItemClicked(getAdapterPosition());
                }
            });
        }
    }
}
