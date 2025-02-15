package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.fun.model.BattleModel_fun;
import com.bekambimouen.miiokychallenge.model.Comment;
import com.bekambimouen.miiokychallenge.model.Crush;
import com.bekambimouen.miiokychallenge.model.Like;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Util_BattleModel_fun {

    public static BattleModel_fun getBattleModel_fun(AppCompatActivity context, Map<Object, Object> objectMap, DataSnapshot ds) {
        BattleModel_fun model = new BattleModel_fun();

        model.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));
        model.setCaption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());
        model.setTags(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_tags))).toString());
        model.setPhoto_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());
        model.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        model.setDate_created(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_date_created))).toString()));
        model.setUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_url))).toString());
        model.setThumbnail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail))).toString());

        List<Comment> commentsList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds.child(context.getString(R.string.field_comments)).getChildren()){
            Comment comment = new Comment();
            Map<Object, Object> objectHashMap = (HashMap<Object, Object>) dSnapshot.getValue();

            assert objectHashMap != null;
            comment.setUser_id(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_user_id))).toString());
            comment.setComment(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_comment))).toString());
            comment.setCommentKey(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_commentKey))).toString());
            comment.setDate_created(Long.parseLong(Objects.requireNonNull(objectHashMap.get(context.getString(R.string.field_date_created))).toString()));

            List<Like> likeList = new ArrayList<>();
            for (DataSnapshot data : dSnapshot
                    .child(context.getString(R.string.field_likes)).getChildren()) {
                Like like = new Like();
                like.setUser_id(Objects.requireNonNull(data.getValue(Like.class)).getUser_id());
                likeList.add(like);
            }
            comment.setLikes(likeList);
            commentsList.add(comment);
        }
        model.setComments(commentsList);

        List<Like> likesList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_likes)).getChildren()) {
            Like like = new Like();
            like.setUser_id(Objects.requireNonNull(dSnapshot.getValue(Like.class)).getUser_id());
            likesList.add(like);
        }
        model.setLikes(likesList);

        List<Crush> crushList = new ArrayList<>();
        for (DataSnapshot dSnapshot : ds
                .child(context.getString(R.string.field_crush)).getChildren()) {
            Crush crush = new Crush();
            crush.setUser_id(Objects.requireNonNull(dSnapshot.getValue(Crush.class)).getUser_id());
            crushList.add(crush);
        }
        model.setCrush(crushList);

        return model;
    }
}

