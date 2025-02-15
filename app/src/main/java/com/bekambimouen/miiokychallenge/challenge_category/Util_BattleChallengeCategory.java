package com.bekambimouen.miiokychallenge.challenge_category;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.model.BattleModel;

public class Util_BattleChallengeCategory {
    public static String getBattleChallengeCategory(AppCompatActivity context, BattleModel model) {
        String category = model.getInvite_category_status();
        String set_category = null;

        if (category.equals(context.getString(R.string.love_choice)))
            set_category = context.getString(R.string.love);
        else if (category.equals(context.getString(R.string.beauty_choice)))
            set_category = context.getString(R.string.beauty);
        else if (category.equals(context.getString(R.string.acapella_choice)))
            set_category = context.getString(R.string.acapella);
        else if (category.equals(context.getString(R.string.dance_choice)))
            set_category = context.getString(R.string.dance);
        else if (category.equals(context.getString(R.string.comedy_choice)))
            set_category = context.getString(R.string.comedy);
        else if (category.equals(context.getString(R.string.swag_choice)))
            set_category = context.getString(R.string.swag);
        else if (category.equals(context.getString(R.string.decoration_choice)))
            set_category = context.getString(R.string.decoration);
        else if (category.equals(context.getString(R.string.couple_choice)))
            set_category = context.getString(R.string.couple);
        else if (category.equals(context.getString(R.string.cinema_choice)))
            set_category = context.getString(R.string.cinema);
        else if (category.equals(context.getString(R.string.emotions_choice)))
            set_category = context.getString(R.string.emotions);
        else if (category.equals(context.getString(R.string.art_choice)))
            set_category = context.getString(R.string.art);
        else if (category.equals(context.getString(R.string.sport_choice)))
            set_category = context.getString(R.string.sport);
        else if (category.equals(context.getString(R.string.games_choice)))
            set_category = context.getString(R.string.games);
        else if (category.equals(context.getString(R.string.vehicle_choice)))
            set_category = context.getString(R.string.vehicle);
        else if (category.equals(context.getString(R.string.accessories_choice)))
            set_category = context.getString(R.string.accessories);
        else if (category.equals(context.getString(R.string.kitchen_choice)))
            set_category = context.getString(R.string.kitchen);
        else if (category.equals(context.getString(R.string.interior_decoration_choice)))
            set_category = context.getString(R.string.interior_decoration);
        else if (category.equals(context.getString(R.string.accommodation_choice)))
            set_category = context.getString(R.string.accommodation);
        else if (category.equals(context.getString(R.string.restoration_choice)))
            set_category = context.getString(R.string.restoration);
        else if (category.equals(context.getString(R.string.journey_choice)))
            set_category = context.getString(R.string.journey);
        else if (category.equals(context.getString(R.string.animals_choice)))
            set_category = context.getString(R.string.animals);
        else if (category.equals(context.getString(R.string.entertainment_choice)))
            set_category = context.getString(R.string.entertainment);
        else if (category.equals(context.getString(R.string.event_choice)))
            set_category = context.getString(R.string.event);

        return set_category;

    }
}

