package com.bekambimouen.miiokychallenge.Utils;

import android.os.Environment;

public class FilePaths {
    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE = "battle/users/";
    public String FIREBASE_IMAGE_STORAGE_GROUP = "group_create/users/";
    public String FIREBASE_IMAGE_STORAGE_GROUP_BATTLE = "group_battle/image/users/";
    public String FIREBASE_VIDEO_STORAGE_GROUP_BATTLE = "group_battle/video/users/";
    public String FIREBASE_STORAGE_GROUP_COMMENT = "group_comment/users/";
    public String FIREBASE_STORAGE_MARKET_COMMENT = "market_comment/users/";
    public String FIREBASE_CHALLENGE_GROUP_IMAGE = "group_challenge/image/users/";
    public String FIREBASE_CHALLENGE_GROUP_VIDEO = "group_challenge/video/users/";
    public String FIREBASE_FUN_VIDEO_STORAGE = "videos/fun/users/";
    public String FIREBASE_BATTLE_VIDEO_STORAGE = "videos/battle/users/";
    public String FIREBASE_CHALLENGE_STORAGE = "challenge/users/";
    public String FIREBASE_CHAT_IMAGE_STORAGE = "chats/users/";
    public String FIREBASE_COMMENT_STORAGE = "comments/users/";
    public String FIREBASE_STORIES_STORAGE = "stories/users/";
    public String FIREBASE_IMAGE_STORAGE_STORE = "store_create/users/";
    public String FIREBASE_IMAGE_STORAGE_MARKET = "market/image/users/";
}

