package com.bekambimouen.miiokychallenge.groups.manage_member.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.groups.manage_member.model.Rule;

import java.util.List;

public class VerifyRulesAdapter extends RecyclerView.Adapter<VerifyRulesAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<Rule> list;

    public VerifyRulesAdapter(AppCompatActivity context, List<Rule> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_group_add_rules,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rule rule = list.get(position);

        holder.rule_title.setText(rule.getTitle());
        holder.rule_details.setText(rule.getDetails());
        holder.number.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return (list!= null && !list.isEmpty()) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView number;
        private final TextView rule_title;
        private final TextView rule_details;

        public ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            rule_title = itemView.findViewById(R.id.rule_title);
            rule_details = itemView.findViewById(R.id.rule_details);
            RelativeLayout delete = itemView.findViewById(R.id.delete);
            delete.setVisibility(View.GONE);
        }
    }
}

