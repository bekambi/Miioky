package com.bekambimouen.miiokychallenge.challenge_home.view_my_challenges.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.Utils.Util;
import com.bekambimouen.miiokychallenge.challenge_home.bottomsheet.BottomSheetAnswerChallenge;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.GroupResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseImageDoubleDisplay;
import com.bekambimouen.miiokychallenge.display_insta.adapter.viewholders.ResponseVideoDoubleDisplay;
import com.bekambimouen.miiokychallenge.model.BattleModel;
import com.bekambimouen.miiokychallenge.preload_image.PreloadMainFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.softrunapps.paginatedrecyclerview.PaginatedAdapter;

import java.util.Objects;

public class ChallengesIRespondedAdapter extends PaginatedAdapter<BattleModel, RecyclerView.ViewHolder> {
    private static final String TAG = "ChallengesIRespondedAdapter";

    public static final int REPONSE_IMG_DOUBLE = 1;
    public static final int REPONSE_VID_DOUBLE = 2;

    // group
    public static final int GROUP_RESPONSE_IMAGE_DOUBLE = 3;
    public static final int GROUP_RESPONSE_VIDEO_DOUBLE = 4;

    // vars
    private final AppCompatActivity context;
    private final ProgressBar progressBar;
    private final RelativeLayout relLayout_view_overlay;

    // firebase
    private  final String user_id;

    public ChallengesIRespondedAdapter(AppCompatActivity context, ProgressBar progressBar, RelativeLayout relLayout_view_overlay) {
        this.context = context;
        this.progressBar = progressBar;
        this.relLayout_view_overlay = relLayout_view_overlay;

        user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == REPONSE_IMG_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_imagedouble, parent, false);
            return new ResponseImageDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        } else if (viewType == REPONSE_VID_DOUBLE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_main_reponse_videodouble, parent, false);
            return new ResponseVideoDoubleDisplay(context, null, null, null, relLayout_view_overlay, view);

        }

        else if (viewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
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
        final BattleModel model = getItem(position);

        try {
            PreloadMainFragment.getPreloadImages(context, getItem(position+1));
            PreloadMainFragment.getPreloadImages(context, getItem(position+2));
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

        final int itemViewType = getItemViewType(position);

        if (itemViewType == REPONSE_IMG_DOUBLE) {
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

        }

         else if (itemViewType == GROUP_RESPONSE_IMAGE_DOUBLE) {
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

        }

        // hide home text
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isReponseImageDouble())
            return REPONSE_IMG_DOUBLE;
        else if (getItem(position).isReponseVideoDouble())
            return REPONSE_VID_DOUBLE;

        else if (getItem(position).isGroup_response_imageDouble())
            return GROUP_RESPONSE_IMAGE_DOUBLE;
        else  if (getItem(position).isGroup_response_videoDouble())
            return GROUP_RESPONSE_VIDEO_DOUBLE;
        else
            return -1;
    }
}
