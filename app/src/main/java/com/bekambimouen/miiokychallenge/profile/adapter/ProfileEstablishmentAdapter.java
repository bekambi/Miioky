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
import com.bekambimouen.miiokychallenge.model.FrequentedEstablishment;

import java.util.ArrayList;

public class ProfileEstablishmentAdapter extends RecyclerView.Adapter<ProfileEstablishmentAdapter.MyViewHolder> {

    private final AppCompatActivity context;
    private final ArrayList<FrequentedEstablishment> list;

    public ProfileEstablishmentAdapter(AppCompatActivity context, ArrayList<FrequentedEstablishment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_establishment_profile_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FrequentedEstablishment establishment = list.get(position);
        holder.bind(establishment);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView establishment_name;

        public MyViewHolder(View itemView) {
            super(itemView);

            establishment_name = itemView.findViewById(R.id.establishment_name);
        }

        public void bind(FrequentedEstablishment establishment) {
            establishment_name.setText(Html.fromHtml(establishment.getUser_establishment_header()+" <b>"+establishment.getUser_establishment()+"</b>"));
        }
    }
}

