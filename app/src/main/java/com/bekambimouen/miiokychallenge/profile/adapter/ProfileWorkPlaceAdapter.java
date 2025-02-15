package com.bekambimouen.miiokychallenge.profile.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.WorkAt;

import java.util.ArrayList;

public class ProfileWorkPlaceAdapter extends RecyclerView.Adapter<ProfileWorkPlaceAdapter.MyViewHolder> {

    private final AppCompatActivity context;
    private final ArrayList<WorkAt> list;

    public ProfileWorkPlaceAdapter(AppCompatActivity context, ArrayList<WorkAt> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_workplace_profile_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WorkAt workAt = list.get(position);
        holder.bind(workAt);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView workplace_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            workplace_name = itemView.findViewById(R.id.workplace_name);
        }

        public void bind(WorkAt workAt) {
            workplace_name.setText(Html.fromHtml(workAt.getUser_work_at_header()+" <b>"+workAt.getUser_work_at()+"</b>"));
        }
    }
}

