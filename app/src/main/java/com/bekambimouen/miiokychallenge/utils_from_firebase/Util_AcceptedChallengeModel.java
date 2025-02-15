package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.model.AcceptedChallengeModel;

import java.util.Map;
import java.util.Objects;

public class Util_AcceptedChallengeModel {
    public static AcceptedChallengeModel getAcceptedChallengeModel(AppCompatActivity context, Map<String, Object> objectMap) {
        AcceptedChallengeModel acceptedChallengeModel = new AcceptedChallengeModel();

        acceptedChallengeModel.setId(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_id))).toString());
        acceptedChallengeModel.setInvite_photoID(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_invite_photoID))).toString());

        return acceptedChallengeModel;
    }
}

