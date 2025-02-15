package com.bekambimouen.miiokychallenge.challenge_home.adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.VideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.followersfollowings.utils.Utils_Map_FollowerFollowingModel;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.model.User;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_RemoveSuggestionModel;
import com.bekambimouen.miiokychallenge.utils_from_firebase.Util_User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeChallengesAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "HomeChallengesAdapter";

    public static final int IMAGE_DOUBLE = 2;
    public static final int VIDEO_DOUBLE = 3;
    public static final int REPONSE_IMG_DOUBLE = 4;
    public static final int REPONSE_VID_DOUBLE = 5;
    // group
    public static final int GROUP_IMAGE_DOUBLE = 6;
    public static final int GROUP_VIDEO_DOUBLE = 7;
    public static final int GROUP_RESPONSE_IMAGE_DOUBLE = 8;
    public static final int GROUP_RESPONSE_VIDEO_DOUBLE = 9;

    // vars
    private final AppCompatActivity context;
    private final RelativeLayout relLayout_welcome;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private final DatabaseReference myRef;
    private final String user_id;

    public HomeChallengesAdapter(AppCompatActivity context, RelativeLayout relLayout_welcome, ProgressBar progressBar,
                                 RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.relLayout_welcome = relLayout_welcome;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        myRef = FirebaseDatabase.getInstance().getReference();
        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_imagedouble_item, parent, false);
            return new ImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_videodouble_item, parent, false);
            return new VideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_IMG_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_imagedouble, parent, false);
            return new ResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_videodouble, parent, false);
            return new ResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_imagedouble_item, parent, false);
            return new GroupImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_VIDEO_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_videodouble_item, parent, false);
            return new GroupVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_reponse_imagedouble_item, parent, false);
            return new GroupResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == GROUP_RESPONSE_VIDEO_DOUBLE){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_group_reponse_videodouble_item, parent, false);
            return new GroupResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else
            return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BattleModel model = getItem(position);

        try {
            PreloadMainFragment.getPreloadImages(context, getItem(position+1));
            PreloadMainFragment.getPreloadImages(context, getItem(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);

        if (itemViewType == IMAGE_DOUBLE){
            ImageDoubleDisplay imageDouble = (ImageDoubleDisplay) holder;
            imageDouble.init(getItem(position));

            if (!model.getUser_id().equals(user_id)) {
                imageDouble.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), imageDouble.relLayout_follow, imageDouble.menu_item, imageDouble.delete_suggestion);

            } else
                imageDouble.relLayout_follow.setVisibility(View.GONE);

            // delete suggestion
            imageDouble.delete_suggestion.setOnClickListener(view -> deleteSuggestion(imageDouble, model.getUser_id()));

        } else if (itemViewType == VIDEO_DOUBLE) {
            VideoDoubleDisplay videoDouble = (VideoDoubleDisplay) holder;
            videoDouble.init(model);

            if (!model.getUser_id().equals(user_id)) {
                videoDouble.relLayout_follow.setVisibility(View.VISIBLE);

                // follow
                getFollow(model.getUser_id(), videoDouble.relLayout_follow, videoDouble.menu_item, videoDouble.delete_suggestion);

            } else
                videoDouble.relLayout_follow.setVisibility(View.GONE);

            // delete suggestion
            videoDouble.delete_suggestion.setOnClickListener(view -> deleteSuggestion(videoDouble, model.getUser_id()));

        } else if (itemViewType == REPONSE_IMG_DOUBLE) {
            ResponseImageDoubleDisplay responseImageDouble = (ResponseImageDoubleDisplay) holder;
            responseImageDouble.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                responseImageDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, null, model.getInvite_url(),
                    true, false, false, false);
            responseImageDouble.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(responseImageDouble.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

        } else if (itemViewType == REPONSE_VID_DOUBLE) {
            ResponseVideoDoubleDisplay responseVideoDouble = (ResponseVideoDoubleDisplay) holder;
            responseVideoDouble.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                responseVideoDouble.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, "video", model.getThumbnail_invite(),
                    false, true, false, false);
            responseVideoDouble.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(responseVideoDouble.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

        } else if (itemViewType == GROUP_IMAGE_DOUBLE){
            GroupImageDoubleDisplay groupImageDoubleDisplay = (GroupImageDoubleDisplay) holder;
            groupImageDoubleDisplay.init(model);

            // delete suggestion
            groupImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_VIDEO_DOUBLE) {
            GroupVideoDoubleDisplay groupVideoDoubleDisplay = (GroupVideoDoubleDisplay) holder;
            groupVideoDoubleDisplay.init(model);

            // delete suggestion
            groupVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupVideoDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
            GroupResponseImageDoubleDisplay groupResponseImageDoubleDisplay = (GroupResponseImageDoubleDisplay) holder;
            groupResponseImageDoubleDisplay.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                groupResponseImageDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, null, model.getInvite_url(),
                    false, false, true, false);
            groupResponseImageDoubleDisplay.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(groupResponseImageDoubleDisplay.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            // delete suggestion
            groupResponseImageDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupResponseImageDoubleDisplay, model.getGroup_id()));

        } else if (itemViewType == GROUP_RESPONSE_VIDEO_DOUBLE) {
            GroupResponseVideoDoubleDisplay groupResponseVideoDoubleDisplay = (GroupResponseVideoDoubleDisplay) holder;
            groupResponseVideoDoubleDisplay.init(model);

            if (model.isEveryone_can_answer_this_challenge())
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);
            else
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);

            if (model.getReponse_user_id().equals(user_id) || model.getInvite_userID().equals(user_id))
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.GONE);
            else
                groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setVisibility(View.VISIBLE);

            BottomSheetAnswerChallenge bottomSheet = new BottomSheetAnswerChallenge(context, model, "video", model.getThumbnail_invite(),
                    false, false, false, true);
            groupResponseVideoDoubleDisplay.relLayout_answer_challenge.setOnClickListener(view -> {
                Util.preventTwoClick(groupResponseVideoDoubleDisplay.relLayout_answer_challenge);
                bottomSheet.show(context.getSupportFragmentManager(), "TAG");
            });

            // delete suggestion
            groupResponseVideoDoubleDisplay.delete_suggestion.setOnClickListener(view -> deleteSuggestion(groupResponseVideoDoubleDisplay, model.getGroup_id()));

        }

        // hide home text
        if (relLayout_welcome.getVisibility() == View.VISIBLE) {
            relLayout_welcome.setVisibility(View.GONE);
        }

        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.GONE);
    }

    // follow ______________________________________________________________________________________
    private void getFollow(String userID, RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion) {
        Query myQuery = myRef.child(context.getString(R.string.dbname_friend_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(userID);

        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: "+ds.getKey());

                    relLayout_follow.setVisibility(View.GONE);
                }

                if (!snapshot.exists()) {
                    Query query = myRef
                            .child(context.getString(R.string.dbname_users))
                            .orderByChild(context.getString(R.string.field_user_id))
                            .equalTo(userID);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds: snapshot.getChildren()) {

                                Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                                assert objectMap != null;
                                User user = Util_User.getUser(context, objectMap, ds);

                                isFollowing(user, relLayout_follow, menu_item, delete_suggestion);

                                HashMap<Object, Object> map_current_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user_id);
                                HashMap<Object, Object> map_other_user = Utils_Map_FollowerFollowingModel.setFollowerFollowingModel(user.getUser_id());

                                relLayout_follow.setOnClickListener(view -> {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_following))
                                            .child(user_id)
                                            .child(user.getUser_id())
                                            .setValue(map_other_user);

                                    FirebaseDatabase.getInstance().getReference()
                                            .child(context.getString(R.string.dbname_followers))
                                            .child(user.getUser_id())
                                            .child(user_id)
                                            .setValue(map_current_user);
                                    setFollowing(relLayout_follow, menu_item, delete_suggestion);
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setFollowing(RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_follow.setVisibility(View.GONE);
        menu_item.setVisibility(View.VISIBLE);
        delete_suggestion.setVisibility(View.GONE);
    }

    private void setUnFollowing(RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "setFollowing: updating UI for following this user");
        relLayout_follow.setVisibility(View.VISIBLE);
        menu_item.setVisibility(View.GONE);
        delete_suggestion.setVisibility(View.VISIBLE);
    }

    private void isFollowing(User user, RelativeLayout relLayout_follow, ImageView menu_item, ImageView delete_suggestion){
        Log.d(TAG, "isFollowing: checking if following this users.");

        Query query = myRef.child(context.getString(R.string.dbname_following))
                .child(user_id)
                .orderByChild(context.getString(R.string.field_user_id))
                .equalTo(user.getUser_id());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: found user:" + ds.getValue());

                    setFollowing(relLayout_follow, menu_item, delete_suggestion);
                }

                if (!snapshot.exists()) {
                    setUnFollowing(relLayout_follow, menu_item, delete_suggestion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // delete suggestion
    private void deleteSuggestion(RecyclerView.ViewHolder holder, String id) {
        // show dialog box
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.layout_dialog_group_rules, null);

        TextView dialog_title = v.findViewById(R.id.dialog_title);
        TextView dialog_text = v.findViewById(R.id.dialog_text);
        TextView negativeButton = v.findViewById(R.id.tv_no);
        TextView positiveButton = v.findViewById(R.id.tv_yes);
        builder.setView(v);

        Dialog d = builder.create();
        d.show();

        negativeButton.setText(context.getString(R.string.cancel));
        positiveButton.setText(context.getString(R.string.hide));

        dialog_title.setText(Html.fromHtml(context.getString(R.string.hide_suggestion)));
        dialog_text.setText(Html.fromHtml(context.getString(R.string.miioky_will_definitely_remove_this_suggestion_for_you)));

        negativeButton.setOnClickListener(view2 -> d.dismiss());

        positiveButton.setOnClickListener(view3 -> {
            // internet control
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(context, context.getResources().getString(R.string.no_connexion), Toast.LENGTH_LONG).show();
                d.dismiss();

            } else {
                Query query = myRef.child(context.getString(R.string.dbname_remove_Suggestion))
                        .child(user_id)
                        .orderByChild(context.getString(R.string.field_user_id))
                        .equalTo(id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            Map<Object, Object> objectMap = (HashMap<Object, Object>) ds.getValue();
                            assert objectMap != null;
                            RemoveSuggestionModel suggestionModel = Util_RemoveSuggestionModel.getRemoveSuggestionModel(context, objectMap);

                            if (!suggestionModel.getUser_id().equals(id)) {
                                holder.itemView.setVisibility(View.GONE);
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height = 0;
                                params.width = 0;
                                holder.itemView.setLayoutParams(params);

                                String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                                HashMap<String, Object> map_account = new HashMap<>();
                                map_account.put("user_id", id);
                                assert newKey != null;
                                FirebaseDatabase.getInstance().getReference()
                                        .child(context.getString(R.string.dbname_remove_Suggestion))
                                        .child(user_id)
                                        .child(newKey)
                                        .setValue(map_account);
                                d.dismiss();
                            } else {
                                d.dismiss();
                                Toast.makeText(context, context.getResources().getString(R.string.your_request_is_in_progress), Toast.LENGTH_LONG).show();
                            }
                        }

                        if (!snapshot.exists()) {
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            params.width = 0;
                            holder.itemView.setLayoutParams(params);

                            String newKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                            HashMap<String, Object> map_account = new HashMap<>();
                            map_account.put("user_id", id);
                            assert newKey != null;
                            FirebaseDatabase.getInstance().getReference()
                                    .child(context.getString(R.string.dbname_remove_Suggestion))
                                    .child(user_id)
                                    .child(newKey)
                                    .setValue(map_account);
                            d.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isImageDouble())
            return IMAGE_DOUBLE;
        else if (getItem(position).isVideoDouble())
            return VIDEO_DOUBLE;
        else if (getItem(position).isReponseImageDouble())
            return REPONSE_IMG_DOUBLE;
        else if (getItem(position).isReponseVideoDouble())
            return REPONSE_VID_DOUBLE;
        else if (getItem(position).isGroup_imageDouble())
            return GROUP_IMAGE_DOUBLE;
        else if (getItem(position).isGroup_videoDouble())
            return GROUP_VIDEO_DOUBLE;
        else if (getItem(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_response_videoDouble())
            return GROUP_RESPONSE_VIDEO_DOUBLE;
        else
            return  -1;
    }
}
