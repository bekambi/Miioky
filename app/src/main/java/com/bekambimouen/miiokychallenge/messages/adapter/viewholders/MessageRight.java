package com.bekambimouen.miiokychallenge.messages.adapter.viewholders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Utilities;
import com.bekambimouen.miiokychallenge.messages.model.Chat;
import com.bekambimouen.miiokychallenge.Utils.TimeUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRight extends RecyclerView.ViewHolder implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "MessageRight";
    // widgets
    public final LinearLayout linLayout;
    public final LinearLayout linLayout_simple_message;
    public final LinearLayout linLayout2;
    public final LinearLayout lin_hourPhoto;
    public final LinearLayout ll_listitem;
    public final RelativeLayout deleted_comment;
    public final RelativeLayout relLayout_photo_simple;
    public final ImageView image;
    public final ImageView imageSimple;
    public final ImageView done_img_one;
    public final ImageView done_photo_one;
    public final ImageView done_msg_one;
    public final ImageView done_voice_one;
    public final ImageView img_play;
    private final TextView mDate;
    public final TextView show_message;
    public final TextView showMessagePhoto;
    public final TextView hour;
    public final TextView hourPhotoSimple;
    public final TextView hourPhoto;
    public final TextView voice_hour;
    public final ProgressBar pbProgressAction;
    private Chat mChat;

    private final AppCompatActivity context;
    private int currentPlayingPosition;

    // voice message
    public final CircleImageView profil_photo;
    @SuppressLint("StaticFieldLeak")
    private MediaPlayer mediaPlayer;
    private final SeekBar seekBar;
    private final ImageView imgPlay;
    private final TextView total_txtTime;
    private final TextView currentTime;
    // ____________________________________
    private SeekBarUpdater seekBarUpdater;

    public MessageRight(@NonNull View itemView, AppCompatActivity context, int currentPlayingPosition) {
        super(itemView);
        this.context = context;
        this.currentPlayingPosition = currentPlayingPosition;
        linLayout = itemView.findViewById(R.id.linLayout);
        linLayout_simple_message = itemView.findViewById(R.id.linLayout_simple_message);
        linLayout2 = itemView.findViewById(R.id.linLayout2);
        relLayout_photo_simple = itemView.findViewById(R.id.relLayout_photo_simple);
        pbProgressAction = itemView.findViewById(R.id.pbProgressAction);
        showMessagePhoto = itemView.findViewById(R.id.showMessagePhoto);
        show_message = itemView.findViewById(R.id.showMessage);
        mDate = itemView.findViewById(R.id.date);
        hourPhoto = itemView.findViewById(R.id.hourPhoto);
        lin_hourPhoto = itemView.findViewById(R.id.lin_hourPhoto);
        ll_listitem= itemView.findViewById(R.id.ll_listitem);
        deleted_comment = itemView.findViewById(R.id.deleted_comment);
        hourPhotoSimple = itemView.findViewById(R.id.hourPhotoSimple);
        hour = itemView.findViewById(R.id.hour);
        image = itemView.findViewById(R.id.image);
        imageSimple = itemView.findViewById(R.id.imageSimple);
        done_img_one = itemView.findViewById(R.id.done_img_one);
        done_photo_one = itemView.findViewById(R.id.done_photo_one);
        done_msg_one = itemView.findViewById(R.id.done_msg_one);
        done_voice_one = itemView.findViewById(R.id.done_voice_one);
        img_play = itemView.findViewById(R.id.img_play);

        // voice message
        profil_photo = itemView.findViewById(R.id.profil_photo);
        voice_hour = itemView.findViewById(R.id.voice_hour);
        total_txtTime = itemView.findViewById(R.id.total_txtTime);
        currentTime = itemView.findViewById(R.id.txtTime);
        seekBar = itemView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        imgPlay = itemView.findViewById(R.id.imgPlay);
        imgPlay.setOnClickListener(this);
    }

    public void bindDate(final Chat previousMessage, final Chat message, int postion) {
        setDate(previousMessage, message, postion);
    }

    public void bind(Chat chat, int position) {
        mChat = chat;

        new Thread(() -> {
            seekBarUpdater = new SeekBarUpdater();

            // initializing media player
            if (mediaPlayer == null)
                mediaPlayer = new MediaPlayer();

            if (position == currentPlayingPosition) {
                updatePlayingView();
            } else {
                updateNonPlayingView();
            }

            mediaPlayer = MediaPlayer.create(context, Uri.parse(chat.getVoiceMail()));

            try {
                long maxTime = mediaPlayer.getDuration();
                context.runOnUiThread(() -> total_txtTime.setText(Utilities.milliSecondsToTimer(maxTime)));
            } catch (NullPointerException e) {
                Log.d(TAG, "bind: "+e.getMessage());
            }

        }).start();
    }

    public void updateNonPlayingView() {
        seekBar.removeCallbacks(seekBarUpdater);
        seekBar.setProgress(0);
        imgPlay.setImageResource(R.drawable.play);
    }

    public void updatePlayingView() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        final Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    int duration=mediaPlayer.getCurrentPosition();
                    @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d ",
                            TimeUnit.MILLISECONDS.toMinutes(duration),
                            TimeUnit.MILLISECONDS.toSeconds(duration) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                    currentTime.setText(time);
                } catch (Exception e) {
                    Log.d(TAG, "run: "+e.getMessage());
                }
                //updateseekbar();
                handler.postDelayed(this,100);
            }
        });

        if (mediaPlayer.isPlaying()) {
            seekBar.postDelayed(seekBarUpdater, 100);
            imgPlay.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            seekBar.removeCallbacks(seekBarUpdater);
            imgPlay.setImageResource(R.drawable.play);
        }
    }

    @Override
    public void onClick(View view) {
        // internet control
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if (getLayoutPosition() == currentPlayingPosition) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                    mediaPlayer.start();
                }
            } else {
                currentPlayingPosition = getLayoutPosition();
                if (mediaPlayer != null) {
                    updateNonPlayingView();

                    mediaPlayer.release();
                }

                startMediaPlayer(mChat.getVoiceMail());
            }
            updatePlayingView();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        // internet control
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            if (b) {
                mediaPlayer.seekTo(i);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void startMediaPlayer(String voiceMail) {
        mediaPlayer = MediaPlayer.create(context, Uri.parse(voiceMail));
        mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        updateNonPlayingView();

        mediaPlayer.release();
        mediaPlayer = null;
        currentPlayingPosition = -1;
    }

    private class SeekBarUpdater implements Runnable {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekBar.postDelayed(this, 100);
        }
    }

    // date of the day
    private void setDate(Chat previousMessage, Chat message, int position) {
        Date previousMessageDate = null;
        if (previousMessage != null) {
            previousMessageDate = new Date(previousMessage.getTimeStamp());
        }

        Date messageDate = new Date(message.getTimeStamp());
        // it's today. show the label "today"
        if (TimeUtils.isDateToday(message.getTimeStamp())) {
            mDate.setText(itemView.getContext().getString(R.string.today));
        } else {
            // it's not today. shows the week of day label
            mDate.setText(TimeUtils.getFormattedTimestamp(itemView.getContext(), message.getTimeStamp()));
        }

        // hides or shows the date label
        if (previousMessageDate != null && position > 0) {
            if (TimeUtils.getDayOfWeek(messageDate)
                    .equals(TimeUtils.getDayOfWeek(previousMessageDate))) {
                mDate.setVisibility(View.GONE);
            } else {
                mDate.setVisibility(View.VISIBLE);
            }
        } else {
            mDate.setVisibility(View.VISIBLE);
        }
    }
}

