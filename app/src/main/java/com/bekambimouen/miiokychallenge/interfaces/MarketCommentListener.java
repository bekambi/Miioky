package com.bekambimouen.miiokychallenge.interfaces;

import com.bekambimouen.miiokychallenge.model.Comment;

public interface MarketCommentListener {
    void onClick(String commentKey, String comment, String user_id, String url, String thumbnail,
                 Comment commentModel, long time);
}

