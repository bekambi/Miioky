package com.bekambimouen.miiokychallenge.util_map;

import com.bekambimouen.miiokychallenge.market_place.model.MarketModel;
import com.bekambimouen.miiokychallenge.messages.model.Chat;

import java.util.HashMap;

public class Utils_Map_Chat {
    public static HashMap<String, Object> getChat(String chat_key, String video, String sender, String receiver, String message,
                               String messagePhoto, String voiceMail, boolean isseen, String imageUrl, String thumbnail,
                               String photoSimple, long timeStamp, String store_id, String photoi_id, String suppressed) {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("chat_key", chat_key);
        hashMap.put("video", video);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("messagePhoto", messagePhoto);
        hashMap.put("voiceMail", voiceMail);
        hashMap.put("isseen", isseen);
        hashMap.put("imageUrl", imageUrl);
        hashMap.put("thumbnail", thumbnail);
        hashMap.put("photoSimple", photoSimple);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("store_id", store_id);
        hashMap.put("photoi_id", photoi_id);
        // suppress the chat
        hashMap.put("suppressed", suppressed);

        return hashMap;
    }
}
