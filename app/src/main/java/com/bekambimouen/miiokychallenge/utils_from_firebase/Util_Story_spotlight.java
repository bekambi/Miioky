package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.profile.in_the_spotlight.model.Story_spotlight;

import java.util.Map;
import java.util.Objects;

public class Util_Story_spotlight {
    public static Story_spotlight getStory_spotlight(AppCompatActivity context, Map<Object, Object> objectMap) {
        Story_spotlight spotlight = new Story_spotlight();

        spotlight.setMediaUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_mediaUrl))).toString());
        spotlight.setStoryid(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_storyid))).toString());
        spotlight.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());
        spotlight.setCaption(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_caption))).toString());

        return spotlight;
    }
}

