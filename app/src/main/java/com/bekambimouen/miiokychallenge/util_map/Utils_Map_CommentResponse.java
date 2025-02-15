package com.bekambimouen.miiokychallenge.util_map;

import com.bekambimouen.miiokychallenge.model.CommentResponse;

public class Utils_Map_CommentResponse {
    public static CommentResponse getCommentResponse(String suppressed, String thumbnailUrl, String filePath, boolean userAnswer,
                                                     String newComment, String commentKey, String commentParentKey, String answeringUsername,
                                                     long date_created, String user_id) {

        CommentResponse comment = new CommentResponse();
        comment.setSuppressed(suppressed);
        comment.setThumbnail(thumbnailUrl);
        comment.setUrl(filePath);
        comment.setUserAnswer(userAnswer);
        comment.setComment(newComment);
        comment.setCommentKey(commentKey);
        comment.setCommentParentKey(commentParentKey);
        comment.setAnsweringUsername(answeringUsername);
        comment.setDate_created(date_created);
        comment.setUser_id(user_id);

        return comment;
    }
}
