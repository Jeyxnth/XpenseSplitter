package com.example.xpensesplitter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.R;
import com.example.xpensesplitter.models.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    // Interface to handle delete clicks
    public interface OnExpenseDeleteListener {
        void onDeleteClicked(String expenseId, String addedBy);
    }

    private List<Expense> expenses;
    private OnExpenseDeleteListener listener;

    // ✅ Correct constructor (takes both list & listener)
    public ExpenseAdapter(List<Expense> expenses, OnExpenseDeleteListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder holder, int position) {
        Expense exp = expenses.get(position);

        holder.textTitle.setText(exp.getTitle());
        holder.textAmount.setText("₹" + exp.getAmount());
        holder.textAddedBy.setText("Added by: " + exp.getAddedBy());

        // 🗑️ Delete icon functionality
        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(exp.getId(), exp.getAddedBy());
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    // ViewHolder
    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textAmount, textAddedBy;
        ImageButton buttonDelete;

        ExpenseViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textExpenseTitle);
            textAmount = itemView.findViewById(R.id.textExpenseAmount);
            textAddedBy = itemView.findViewById(R.id.textExpenseAddedBy);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteExpense);
        }
    }
}
