package com.bekambimouen.miiokychallenge.groups.manage_member.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;
import com.csguys.viewmoretextview.ViewMoreTextView;

import java.util.List;

public class ShowRulesAdapter extends RecyclerView.Adapter<ShowRulesAdapter.ViewHolder> {

    public interface OnItemCheckListener {
        void onItemCheck(Rule rule);
        void onItemUncheck(Rule rule);
    }

    // vars
    private final AppCompatActivity context;
    private final List<Rule> list;
    private final OnItemCheckListener onItemClick;

    public ShowRulesAdapter(AppCompatActivity context, List<Rule> list, OnItemCheckListener onItemClick) {
        this.context = context;
        this.list = list;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_non_rescpected_rules,parent,false);

        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rule rule = list.get(position);

        holder.rule_title.setText(rule.getTitle());
        holder.rule_details.setCharText(rule.getDetails());
        holder.number.setText(String.valueOf(position + 1));

        holder.itemView.setOnClickListener(view -> {
            holder.checkBox.setChecked(
                    !holder.checkBox.isChecked());
            if (holder.checkBox.isChecked()) {
                onItemClick.onItemCheck(rule);
            } else {
                onItemClick.onItemUncheck(rule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list!= null && !list.isEmpty()) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView number;
        private final TextView rule_title;
        private final ViewMoreTextView rule_details;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setClickable(false);
            number = itemView.findViewById(R.id.number);
            rule_title = itemView.findViewById(R.id.rule_title);
            rule_details = itemView.findViewById(R.id.rule_details);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}

