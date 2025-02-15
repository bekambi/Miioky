package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.story.model.Story;

import java.util.Map;
import java.util.Objects;

public class Util_Story {
    public static Story getStory(AppCompatActivity context, Map<Object, Object> objectMap) {
        Story story = new Story();

        story.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        story.setMediaUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_mediaUrl))).toString());
        story.setStoryid(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_storyid))).toString());
        story.setCaption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());
        story.setTimestart(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_timestart))).toString()));
        story.setTimeend(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_timeend))).toString()));
        story.setSuppressed(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString()));

        return story;
    }
}

