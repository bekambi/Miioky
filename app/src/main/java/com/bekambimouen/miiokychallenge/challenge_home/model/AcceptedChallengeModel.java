package com.bekambimouen.miiokychallenge.challenge_home.model;

public class AcceptedChallengeModel {
    private String invite_photoID;
    private String id;

    public AcceptedChallengeModel() {
    }

    public AcceptedChallengeModel(String invite_photoID, String id) {
        this.invite_photoID = invite_photoID;
        this.id = id;
    }

    public String getInvite_photoID() {
        return invite_photoID;
    }

    public void setInvite_photoID(String invite_photoID) {
        this.invite_photoID = invite_photoID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

