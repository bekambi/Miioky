package com.bekambimouen.miiokychallenge.full_img_and_vid_simple.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.MyVideoView;

import java.util.ArrayList;

public class SimpleFullChallengeVideoAdapter extends RecyclerView.Adapter<SimpleFullChallengeVideoAdapter.MyViewHolder> {
    private static final String TAG = "FullChallengeVideoAdapter";

    private final AppCompatActivity context;
    private final ArrayList<String> list;

    public SimpleFullChallengeVideoAdapter(AppCompatActivity context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_local_video, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  MyViewHolder holder, int position) {
        String model = list.get(position);
        holder.bindVideo(model);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if(list==null) return 0;
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // widgets
        private final ImageView img_play;
        private final ImageView img_pause;
        private final MyVideoView videoView;

        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(@NonNull  View itemView) {
            super(itemView);
            img_play = itemView.findViewById(R.id.img_play);
            img_pause = itemView.findViewById(R.id.img_pause);
            videoView = itemView.findViewById(R.id.videoView);
        }

        void bindVideo(String video_url) {

            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(videoView);

            videoView.setVideoURI(Uri.parse(video_url));
            videoView.requestFocus();
            videoView.setZOrderOnTop(true);
            videoView.start();

            videoView.setOnPreparedListener(mp -> mp.setLooping(true));

            img_play.setOnClickListener(view -> {
                img_pause.setVisibility(View.VISIBLE);
                img_play.setVisibility(View.GONE);
                videoView.start();
            });

            img_pause.setOnClickListener(view -> {
                img_pause.setVisibility(View.GONE);
                img_play.setVisibility(View.VISIBLE);
                videoView.pause();
            });
        }
    }
}
