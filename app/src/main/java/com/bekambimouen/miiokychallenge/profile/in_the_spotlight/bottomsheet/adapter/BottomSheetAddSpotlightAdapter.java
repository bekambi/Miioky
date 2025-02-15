package com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.CustomShareProgressView;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.bottomsheet.BottomSheetAddSpotlight;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.SpolightCover;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class BottomSheetAddSpotlightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // constants
    private static final int ICON_ADD = 1;
    private static final int ITEM = 2;

    private final AppCompatActivity context;
    private final List<SpolightCover> mSheetList;
    private final BottomSheetAddSpotlight bottomSheet;
    private final StoriesProgressView storiesProgressView;
    private final String user_id;
    private final String storyid;
    private final String mediaUrl;
    private final String caption;
    private final RecyclerView recyclerview;
    private final RelativeLayout relLayout_one_item;
    private final CircleImageView cover_photo;
    private final TextView button_add_spotlight;
    private final EditText add_title;
    private final RelativeLayout relLayout_done;

    // vars
    private CustomShareProgressView mProgresDialog;

    private void showLoading(){
        if (mProgresDialog == null)
            mProgresDialog = new CustomShareProgressView(context);
        mProgresDialog.show();
    }

    // firebase
    private final DatabaseReference myRef;

    public BottomSheetAddSpotlightAdapter(AppCompatActivity context, List<SpolightCover> sheetList,
                                          BottomSheetAddSpotlight bottomSheet, StoriesProgressView storiesProgressView,
                                          String user_id, String storyid, String mediaUrl, String caption, RecyclerView recyclerview,
                                          RelativeLayout relLayout_one_item, CircleImageView cover_photo,
                                          TextView button_add_spotlight, EditText add_title, RelativeLayout relLayout_done) {
        this.mSheetList = sheetList;
        this.context = context;
        this.bottomSheet = bottomSheet;
        this.storiesProgressView = storiesProgressView;
        this.user_id = user_id;
        this.storyid = storyid;
        this.mediaUrl = mediaUrl;
        this.caption = caption;
        this.recyclerview = recyclerview;
        this.relLayout_one_item = relLayout_one_item;
        this.cover_photo = cover_photo;
        this.button_add_spotlight = button_add_spotlight;
        this.add_title = add_title;
        this.relLayout_done = relLayout_done;

        myRef = FirebaseDatabase.getInstance().getReference();

        this.mSheetList.remove(null);
        this.mSheetList.add(0, null);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ICON_ADD) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_story_add_spotlight_icon,parent,false);
            return new AddIcon(view);

        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.layout_story_spotlight_item,parent,false);
            return new Item(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int itemViewType = getItemViewType(position);

        SpolightCover spotlight_cover = mSheetList.get(position);

        Query query = FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.dbname_story_spotlight_cover))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    String storyID = ds.getKey();
                    assert storyID != null;
                    if (storyID.equals(storyid)) {
                        relLayout_done.setVisibility(View.VISIBLE);
                        button_add_spotlight.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (itemViewType == ICON_ADD) {
            AddIcon addIcon = (AddIcon) holder;

            addIcon.iv_add.setOnClickListener(view -> {
                recyclerview.setVisibility(View.GONE);
                relLayout_one_item.setVisibility(View.VISIBLE);
                button_add_spotlight.setVisibility(View.VISIBLE);

                if (!context.isFinishing()) {
                    Glide.with(context)
                            .load(mediaUrl)
                            .into(cover_photo);
                }

                button_add_spotlight.setEnabled(true);
                button_add_spotlight.setOnClickListener(view1 -> {
                    String title = add_title.getText().toString();
                    if (!TextUtils.isEmpty(title)) {
                        showLoading();
                        addPhotosToDatabase(title, mediaUrl);

                    } else {
                        Toast.makeText(context, context.getString(R.string.add_a_title), Toast.LENGTH_LONG).show();
                    }
                });
            });

        } else if (itemViewType == ITEM) {
            Item item = (Item) holder;
            if (!context.isFinishing()) {
                Glide.with(context)
                        .load(spotlight_cover.getMediaUrl())
                        .into(item.cover);
            }

            item.title.setText(spotlight_cover.getTitle());

            Query query1 = FirebaseDatabase.getInstance()
                    .getReference(context.getString(R.string.dbname_story_spotlight))
                    .child(user_id)
                    .child(spotlight_cover.getStoryid());

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        String storyID = ds.getKey();
                        assert storyID != null;
                        if (storyID.equals(storyid)) {
                            item.relLayout_filter_done.setVisibility(View.VISIBLE);
                            item.itemView.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            item.itemView.setOnClickListener(view -> {
                // download to firebase
                showLoading();

                Story_spotlight spotlight = new Story_spotlight();
                spotlight.setMediaUrl(mediaUrl);
                spotlight.setStoryid(storyid);
                spotlight.setUser_id(user_id);
                spotlight.setCaption(caption);
                // suppress
                spotlight.setSuppressed(false);

                myRef.child(context.getString(R.string.dbname_story_spotlight))
                        .child(user_id)
                        .child(spotlight_cover.getStoryid())
                        .child(storyid)
                        .setValue(spotlight)
                        .addOnCompleteListener(task -> {
                            mProgresDialog.dismiss();
                            Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                            bottomSheet.dismiss();
                            storiesProgressView.resume();
                        });
            });
        }
    }

    private void addPhotosToDatabase(String title, String url) {
        String newPhotoKey = myRef.child(context.getString(R.string.dbname_story_spotlight)).push().getKey();

        // cover ____________________________________________________________________
        SpolightCover spolightCover = new SpolightCover();
        spolightCover.setUser_id(user_id);
        spolightCover.setMediaUrl(url);
        spolightCover.setStoryid(newPhotoKey);
        spolightCover.setTitle(title);

        myRef.child(context.getString(R.string.dbname_story_spotlight_cover))
                .child(user_id)
                .child(newPhotoKey)
                .setValue(spolightCover);
        // __________________________________________________________________________

        Story_spotlight spotlight = new Story_spotlight();
        spotlight.setMediaUrl(url);
        spotlight.setStoryid(storyid);
        spotlight.setUser_id(user_id);
        spotlight.setCaption(caption);
        // suppress
        spotlight.setSuppressed(false);

        myRef.child(context.getString(R.string.dbname_story_spotlight))
                .child(user_id)
                .child(newPhotoKey)
                .child(storyid)
                .setValue(spotlight)
                .addOnCompleteListener(task -> {
                    mProgresDialog.dismiss();
                    Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_SHORT).show();
                    bottomSheet.dismiss();
                    storiesProgressView.resume();
                    button_add_spotlight.setEnabled(false);
                });
    }

    @Override
    public int getItemCount() {
        if(mSheetList==null) return 0;
        return mSheetList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return ICON_ADD;
        else
            return ITEM;
    }

    public static class AddIcon extends RecyclerView.ViewHolder {
        ImageView iv_add;
        public AddIcon(@NonNull View itemView) {
            super(itemView);
            iv_add = itemView.findViewById(R.id.iv_add);
        }
    }

    public static class Item extends RecyclerView.ViewHolder {
        private final CircleImageView cover;
        private final TextView title;
        private final RelativeLayout relLayout_filter_done;

        public Item(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            relLayout_filter_done = itemView.findViewById(R.id.relLayout_filter_done);
        }
    }
}

