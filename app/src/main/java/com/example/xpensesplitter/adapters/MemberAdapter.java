package com.example.xpensesplitter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.R;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private List<String> members;

    public MemberAdapter(List<String> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String member = members.get(position);
        holder.textMember.setText(member);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView textMember;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            textMember = itemView.findViewById(R.id.textMember);
        }
    }
}
