package com.bekambimouen.miiokychallenge.util_map;

import com.bekambimouen.miiokychallenge.model.Comment;

public class Utils_Map_Comment {
    public static Comment getCommentResponse(String suppressed, String thumbnailUrl, String filePath,
                                             String newComment, String commentKey, long date_created, String user_id) {

        Comment comment = new Comment();
        comment.setSuppressed(suppressed);
        comment.setThumbnail(thumbnailUrl);
        comment.setUrl(filePath);
        comment.setComment(newComment);
        comment.setCommentKey(commentKey);
        comment.setDate_created(date_created);
        comment.setUser_id(user_id);

        return comment;
    }
}
