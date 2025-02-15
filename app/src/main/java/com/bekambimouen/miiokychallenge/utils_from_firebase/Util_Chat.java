package com.bekambimouen.miiokychallenge.utils_from_firebase;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.messages.model.Chat;

import java.util.Map;
import java.util.Objects;

public class Util_Chat {

    public static Chat getChat(AppCompatActivity context, Map<Object, Object> objectMap) {
        Chat chat = new Chat();

        chat.setSuppressed(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_suppressed))).toString());
        chat.setChat_key(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_chat_key))).toString());
        chat.setVideo(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_video))).toString());
        chat.setSender(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_sender))).toString());
        chat.setReceiver(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_receiver))).toString());
        chat.setMessage(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_message))).toString());
        chat.setMessagePhoto(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_messagePhoto))).toString());
        chat.setVoiceMail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_voiceMail))).toString());
        chat.setIsseen(Boolean.parseBoolean(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_isseen))).toString()));
        chat.setImageUrl(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_imageUrl))).toString());
        chat.setThumbnail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_thumbnail))).toString());
        chat.setPhotoSimple(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photoSimple))).toString());
        chat.setTimeStamp(Long.parseLong(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_timeStamp))).toString()));
        chat.setStore_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_store_id))).toString());
        chat.setVoiceMail(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_voiceMail))).toString());
        chat.setPhotoi_id(Objects.requireNonNull(objectMap.get(context.getString(R.string.field_photo_id))).toString());

        return chat;
    }
}

