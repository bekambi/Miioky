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
import com.bekambimouen.miiokychallenge.model.SchoolAttended;

import java.util.ArrayList;

public class ProfileCollegeAdapter extends RecyclerView.Adapter<ProfileCollegeAdapter.MyViewHolder> {

    private final AppCompatActivity context;
    private final ArrayList<SchoolAttended> list;

    public ProfileCollegeAdapter(AppCompatActivity context, ArrayList<SchoolAttended> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_college_profile_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SchoolAttended school = list.get(position);
        holder.bind(school);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView school_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            school_name = itemView.findViewById(R.id.school_name);
        }

        public void bind(SchoolAttended school) {
            school_name.setText(Html.fromHtml(school.getUser_school_attended_header()+" <b>"+school.getUser_school_attended()+"</b>"));
        }
    }
}

