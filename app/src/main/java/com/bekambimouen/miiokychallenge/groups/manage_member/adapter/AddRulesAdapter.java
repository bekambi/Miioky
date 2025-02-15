package com.bekambimouen.miiokychallenge.groups.manage_member.adapter;

import android.text.Html;
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

public class AddRulesAdapter extends RecyclerView.Adapter<AddRulesAdapter.ViewHolder> {

    // vars
    private final AppCompatActivity context;
    private final List<Rule> list;
    private final TextView you_can_add_rule, button_create_another_rule;
    private final RelativeLayout verify;

    public AddRulesAdapter(AppCompatActivity context, List<Rule> list, TextView you_can_add_rule,
                           TextView button_create_another_rule, RelativeLayout verify) {
        this.context = context;
        this.list = list;
        this.you_can_add_rule = you_can_add_rule;
        this.button_create_another_rule = button_create_another_rule;
        this.verify = verify;
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

        // remove item on list
        holder.delete.setOnClickListener(view -> {
            removeAt(holder.getLayoutPosition());

            you_can_add_rule.setText(Html.fromHtml(context.getString(R.string.you_can_create)
                    +" "+(10 - (list.size()))+" "+context.getString(R.string.more_rules)));

            if (list.isEmpty()) {
                verify.setEnabled(false);

            } else if (list.size() == 10) {
                button_create_another_rule.setVisibility(View.GONE);
                you_can_add_rule.setVisibility(View.GONE);

            } else {
                button_create_another_rule.setVisibility(View.VISIBLE);
                you_can_add_rule.setVisibility(View.VISIBLE);
            }
        });
    }

    // remove item on list
    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @Override
    public int getItemCount() {
        return (list!= null && !list.isEmpty()) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout delete;
        private final TextView number;
        private final TextView rule_title;
        private final TextView rule_details;

        public ViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            number = itemView.findViewById(R.id.number);
            rule_title = itemView.findViewById(R.id.rule_title);
            rule_details = itemView.findViewById(R.id.rule_details);
        }
    }
}

