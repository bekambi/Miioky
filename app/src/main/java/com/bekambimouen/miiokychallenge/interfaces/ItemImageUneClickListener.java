package com.bekambimouen.miiokychallenge.interfaces;

import com.bekambimouen.miiokychallenge.model.Comment;

public interface ItemImageUneClickListener {
    void onClick(String commentKey, String comment, String user_id, String url, String thumbnail,
                 Comment commentModel, String recyclerview_photo_id, String recyclerview_fieldLike,
                 long time);
}
