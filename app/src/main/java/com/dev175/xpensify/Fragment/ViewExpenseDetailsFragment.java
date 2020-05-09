package com.dev175.xpensify.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Adapter.DebitorListAdapter;
import com.dev175.xpensify.Model.Expense;
import com.dev175.xpensify.Model.ExpenseUser;
import com.dev175.xpensify.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ViewExpenseDetailsFragment extends Fragment {

    private static final String TAG = ViewGroupExpensesFragment.class.getSimpleName();
    private Expense  expenseObj;
    private int totalExpenses;
    private RecyclerView totalExpenseRecyclerView;


    //Views in xml
    private TextView expense_title_details;
    private TextView expense_amount_details;
    private TextView expense_timestamp_details;
    private TextView expense_creditorName_details;
    private TextView expense_creditorPhone_details;
    private TextView expense_debitor_details;
    private TextView expense_creditorExpense_details;
    private TextView total_Expenses;
    private RecyclerView expense_debitor_recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private DebitorListAdapter debitorListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_expense_details,container,false);



        //Fragment views
        expense_title_details = view.findViewById(R.id.expense_title_details);
        expense_amount_details = view.findViewById(R.id.expense_amount_details);
        expense_timestamp_details = view.findViewById(R.id.expense_timestamp_details);
        expense_creditorName_details = view.findViewById(R.id.expense_creditorName_details);
        expense_creditorExpense_details = view.findViewById(R.id.expense_creditorExpense_details);
        expense_creditorPhone_details = view.findViewById(R.id.expense_creditorPhone_details);
        expense_debitor_recyclerView = view.findViewById(R.id.expense_debitor_recyclerView);
        expense_debitor_details = view.findViewById(R.id.expense_debitor_details);
        total_Expenses = view.findViewById(R.id.total_Expenses);

        layoutManager = new LinearLayoutManager(getContext());
        expense_debitor_recyclerView.setLayoutManager(layoutManager);

        //setting views
        expense_title_details.setText(expenseObj.getTitle());
        expense_amount_details.setText(expenseObj.getAmount()+" Rs");
        expense_timestamp_details.setText(expenseObj.getTimestamp());
        total_Expenses.setText(totalExpenses+" Rs");

//        List<ExpenseUser> myList = new CopyOnWriteArrayList<ExpenseUser>();

        List<ExpenseUser> expenseUsers = expenseObj.getExpenseUserList();
        List<ExpenseUser> expenseDebitorUsers =  new CopyOnWriteArrayList<ExpenseUser>();

        try {
            for (ExpenseUser user:expenseUsers ) {
                if (user.getUserRole())
                {
                    expense_creditorName_details.setText(user.getName());
                    expense_creditorPhone_details.setText(user.getPhoneNumber());
                    expense_creditorExpense_details.setText(user.getShareAmount()+" Rs");
                }
                else {
                    expenseDebitorUsers.add(user);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Exception "+e, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onCreateView: "+e );
        }


//        Toast.makeText(getContext(), expenseUsers.size(), Toast.LENGTH_SHORT).show();

        debitorListAdapter = new DebitorListAdapter(expenseDebitorUsers,getContext());
        expense_debitor_recyclerView.setAdapter(debitorListAdapter);

        if(expenseDebitorUsers.size()!=0)
        {
            expense_debitor_details.setVisibility(View.VISIBLE);
        }


        return view;
    }

    public void setExpenseObj(Expense expenseObj,int totalExpenses)
    {
        this.expenseObj = expenseObj;
        this.totalExpenses = totalExpenses;
    }
}
