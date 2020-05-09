package com.dev175.xpensify.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev175.xpensify.Adapter.ViewGroupExpenseAdapter;
import com.dev175.xpensify.Interface.IRecyclerViewClickListener;
import com.dev175.xpensify.Model.Expense;
import com.dev175.xpensify.R;

import java.util.ArrayList;

public class ViewGroupExpensesFragment extends Fragment implements IRecyclerViewClickListener {

    public static final String TAG = ViewGroupExpensesFragment.class.getSimpleName();
    private ArrayList<Expense> expenseArrayList;
    private RecyclerView view_group_expense_RecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ViewGroupExpenseAdapter viewGroupExpenseAdapter;
    private int totalExpenses;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_group_expenses,container,false);


        view_group_expense_RecyclerView = (RecyclerView)view.findViewById(R.id.view_group_expense_RecyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        view_group_expense_RecyclerView.setLayoutManager(layoutManager);

        viewGroupExpenseAdapter = new ViewGroupExpenseAdapter(expenseArrayList,getContext(),this);
        view_group_expense_RecyclerView.setAdapter(viewGroupExpenseAdapter);

        return view;
    }


    public void setExpenseList(ArrayList<Expense> expenseList,int totalExpenses)
    {
        this.expenseArrayList = expenseList;
        this.totalExpenses = totalExpenses;
    }

    @Override
    public void recyclerViewItemClicked(int position) {
        Expense selectedExpenseObj = expenseArrayList.get(position);

//        Toast.makeText(getContext(), selectedExpenseObj.getTitle()+" "+selectedExpenseObj.getAmount()+"\n"+
//                selectedExpenseObj.getTimestamp(), Toast.LENGTH_SHORT).show();
        hideViewGroupExpenseF();
        addViewGroupExpenseDetailsFragment(selectedExpenseObj);

    }



    private void addViewGroupExpenseDetailsFragment(Expense expense)
    {
        ViewExpenseDetailsFragment fragment = new ViewExpenseDetailsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.expenseContainer,fragment,"viewGroupExpenseDetailsF");
        fragment.setExpenseObj(expense,totalExpenses);
        transaction.addToBackStack("addViewGroupExpenseDetailsF");
        transaction.commit();

    }




    private void hideViewGroupExpenseF() {
        ViewGroupExpensesFragment fragment = (ViewGroupExpensesFragment) getFragmentManager().findFragmentByTag("viewGroupExpenseF");
        if (fragment!=null)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(fragment);
            transaction.commit();
        }
        else
        {
//            Toast.makeText(getContext(), "viewGroupExpenseF not found", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "hideViewGroupExpenseF: not found!!");
        }
    }




    @Override
    public void recyclerViewItemUnClicked(int position) {

    }
}
