package com.bekambimouen.miiokychallenge.messages.model;



import android.os.Parcel;
import android.os.Parcelable;

public class Chat implements Parcelable {
    private String chat_key;
    private String video;
    private String sender;
    private String receiver;
    private String message;
    private String messagePhoto;
    private String voiceMail;
    private boolean isseen;
    private String imageUrl;
    private String thumbnail;
    private String photoSimple;
    private long timeStamp;
    // about market
    private String store_id;
    private String photoi_id;
    // suppressed the comment
    private String suppressed;

    public Chat() {
    }

    public Chat(String chat_key, String video, String sender, String receiver, String message,
                String messagePhoto, String voiceMail, boolean isseen, String imageUrl, String thumbnail,
                String photoSimple, long timeStamp, String store_id, String photoi_id, String suppressed) {
        this.chat_key = chat_key;
        this.video = video;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messagePhoto = messagePhoto;
        this.voiceMail = voiceMail;
        this.isseen = isseen;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.photoSimple = photoSimple;
        this.timeStamp = timeStamp;
        this.store_id = store_id;
        this.photoi_id = photoi_id;
        this.suppressed = suppressed;
    }

    protected Chat(Parcel in) {
        chat_key = in.readString();
        suppressed = in.readString();
        video = in.readString();
        sender = in.readString();
        receiver = in.readString();
        message = in.readString();
        messagePhoto = in.readString();
        voiceMail = in.readString();
        isseen = in.readByte() != 0;
        imageUrl = in.readString();
        thumbnail = in.readString();
        photoSimple = in.readString();
        timeStamp = in.readLong();
        store_id = in.readString();
        photoi_id = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getChat_key() {
        return chat_key;
    }

    public void setChat_key(String chat_key) {
        this.chat_key = chat_key;
    }

    public String getSuppressed() {
        return suppressed;
    }

    public void setSuppressed(String suppressed) {
        this.suppressed = suppressed;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessagePhoto() {
        return messagePhoto;
    }

    public void setMessagePhoto(String messagePhoto) {
        this.messagePhoto = messagePhoto;
    }

    public String getVoiceMail() {
        return voiceMail;
    }

    public void setVoiceMail(String voiceMail) {
        this.voiceMail = voiceMail;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhotoSimple() {
        return photoSimple;
    }

    public void setPhotoSimple(String photoSimple) {
        this.photoSimple = photoSimple;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getPhotoi_id() {
        return photoi_id;
    }

    public void setPhotoi_id(String photoi_id) {
        this.photoi_id = photoi_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(chat_key);
        parcel.writeString(suppressed);
        parcel.writeString(video);
        parcel.writeString(sender);
        parcel.writeString(receiver);
        parcel.writeString(message);
        parcel.writeString(messagePhoto);
        parcel.writeString(voiceMail);
        parcel.writeByte((byte) (isseen ? 1 : 0));
        parcel.writeString(imageUrl);
        parcel.writeString(thumbnail);
        parcel.writeString(photoSimple);
        parcel.writeLong(timeStamp);
        parcel.writeString(store_id);
        parcel.writeString(photoi_id);
    }
}

