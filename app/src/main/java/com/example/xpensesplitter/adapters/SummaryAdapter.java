package com.example.xpensesplitter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.R;

import java.util.List;
import java.util.Map;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {

    private final List<Map.Entry<String, Double>> summaryList;

    public SummaryAdapter(List<Map.Entry<String, Double>> summaryList) {
        this.summaryList = summaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_summary, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Double> entry = summaryList.get(position);
        String name = entry.getKey();
        double balance = entry.getValue();

        holder.textName.setText(name);

        if (balance > 0) {
            holder.textBalance.setText(String.format("Gets ₹%.2f", balance));
            holder.textBalance.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_light));
        } else if (balance < 0) {
            holder.textBalance.setText(String.format("Owes ₹%.2f", -balance));
            holder.textBalance.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_light));
        } else {
            holder.textBalance.setText("Settled up");
            holder.textBalance.setTextColor(holder.itemView.getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textBalance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textBalance = itemView.findViewById(R.id.textBalance);
        }
    }
}
