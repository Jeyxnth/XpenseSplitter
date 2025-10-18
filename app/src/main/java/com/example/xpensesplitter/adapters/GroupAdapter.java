package com.example.xpensesplitter.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.xpensesplitter.R;
import com.example.xpensesplitter.models.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(Group group);
    }

    private final List<Group> groups;
    private final OnItemClickListener listener;

    public GroupAdapter(List<Group> groups, OnItemClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Group g = groups.get(position);
        holder.tvName.setText(g.getName() != null ? g.getName() : "Unnamed group");
        int count = g.getMembers() != null ? g.getMembers().size() : 0;
        holder.tvMembers.setText(count + " member" + (count == 1 ? "" : "s"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(g);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvMembers;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGroupName);
            tvMembers = itemView.findViewById(R.id.tvMemberCount);
        }
    }
}
