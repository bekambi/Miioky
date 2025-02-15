package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.display_insta.model.RemoveSuggestionModel;

import java.util.Map;
import java.util.Objects;

public class Util_RemoveSuggestionModel {
    public static RemoveSuggestionModel getRemoveSuggestionModel(AppCompatActivity context, Map<Object, Object> objectMap) {
        RemoveSuggestionModel suggestionModel = new RemoveSuggestionModel();

        suggestionModel.setUser_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_user_id))).toString());

        return suggestionModel;
    }
}
