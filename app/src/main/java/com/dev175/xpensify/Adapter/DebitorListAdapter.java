package com.dev175.xpensify.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Model.ExpenseUser;
import com.dev175.xpensify.R;

import java.util.ArrayList;
import java.util.List;

public class DebitorListAdapter extends RecyclerView.Adapter<DebitorListAdapter.MyViewHolder> {

    private List<ExpenseUser> expenseUsers;
    private Context context;

    public DebitorListAdapter(List<ExpenseUser> expenseUsers, Context context) {
        this.expenseUsers = expenseUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_debitor,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TextView expense_debitorName = holder.expense_debitorName;
        TextView expense_debitorName_details = holder.expense_debitorName_details;
        TextView expense_debitorExpense = holder.expense_debitorExpense;
        TextView expense_debitorExpense_details = holder.expense_debitorExpense_details;
        TextView expense_debitorPhone = holder.expense_debitorPhone;
        TextView expense_debitorPhone_details = holder.expense_debitorPhone_details;


        expense_debitorName_details.setText(expenseUsers.get(position).getName());
        expense_debitorExpense_details.setText(expenseUsers.get(position).getShareAmount()+" Rs");
        expense_debitorPhone_details.setText(expenseUsers.get(position).getPhoneNumber());

    }

    @Override
    public int getItemCount() {
        return expenseUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView expense_debitorName;
        TextView expense_debitorName_details;
        TextView expense_debitorExpense;
        TextView expense_debitorExpense_details;
        TextView expense_debitorPhone;
        TextView expense_debitorPhone_details;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.expense_debitorName = itemView.findViewById(R.id.expense_debitorName);
            this.expense_debitorName_details = itemView.findViewById(R.id.expense_debitorName_details);
            this.expense_debitorExpense = itemView.findViewById(R.id.expense_debitorExpense);
            this.expense_debitorExpense_details = itemView.findViewById(R.id.expense_debitorExpense_details);
            this.expense_debitorPhone = itemView.findViewById(R.id.expense_debitorPhone);
            this.expense_debitorPhone_details = itemView.findViewById(R.id.expense_debitorPhone_details);

        }
    }
}
