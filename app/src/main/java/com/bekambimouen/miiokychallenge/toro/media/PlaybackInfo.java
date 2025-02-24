package com.bekambimouen.miiokychallenge.toro.media;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * @author eneim | 6/6/17.
 */

public class PlaybackInfo implements Parcelable {

    public static final long TIME_UNSET = Long.MIN_VALUE + 1;

    public static final int INDEX_UNSET = -1;

    private int resumeWindow;
    private long resumePosition;
    @NonNull private VolumeInfo volumeInfo;

    public PlaybackInfo(int resumeWindow, long resumePosition) {
        this.resumeWindow = resumeWindow;
        this.resumePosition = resumePosition;
        this.volumeInfo = new VolumeInfo(false, 1.f);
    }

    public PlaybackInfo(int resumeWindow, long resumePosition, @NonNull VolumeInfo volumeInfo) {
        this.resumeWindow = resumeWindow;
        this.resumePosition = resumePosition;
        this.volumeInfo = volumeInfo;
    }

    public PlaybackInfo() {
        this(INDEX_UNSET, TIME_UNSET);
    }

    public PlaybackInfo(PlaybackInfo other) {
        this(other.getResumeWindow(), other.getResumePosition(), other.getVolumeInfo());
    }

    public int getResumeWindow() {
        return resumeWindow;
    }

    public void setResumeWindow(int resumeWindow) {
        this.resumeWindow = resumeWindow;
    }

    public long getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }

    @NonNull public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public void setVolumeInfo(@NonNull VolumeInfo volumeInfo) {
        this.volumeInfo = volumeInfo;
    }

    public void reset() {
        resumeWindow = INDEX_UNSET;
        resumePosition = TIME_UNSET;
        volumeInfo = new VolumeInfo(false, 1.f);
    }

    @NonNull
    @Override public String toString() {
        return this == SCRAP ? "Info:SCRAP" : //
                "Info{"
                        + "window="
                        + resumeWindow
                        + ", position="
                        + resumePosition
                        + ", volume="
                        + volumeInfo
                        + '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaybackInfo)) return false;

        PlaybackInfo that = (PlaybackInfo) o;

        if (resumeWindow != that.resumeWindow) return false;
        return resumePosition == that.resumePosition;
    }

    @Override public int hashCode() {
        int result = resumeWindow;
        result = 31 * result + (int) (resumePosition ^ (resumePosition >>> 32));
        return result;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resumeWindow);
        dest.writeLong(this.resumePosition);
        dest.writeParcelable(this.volumeInfo, flags);
    }

    protected PlaybackInfo(Parcel in) {
        this.resumeWindow = in.readInt();
        this.resumePosition = in.readLong();
        this.volumeInfo = Objects.requireNonNull(in.readParcelable(VolumeInfo.class.getClassLoader()));
    }

    public static final Creator<PlaybackInfo> CREATOR = new Creator<PlaybackInfo>() {
        @Override public PlaybackInfo createFromParcel(Parcel source) {
            return new PlaybackInfo(source);
        }

        @Override public PlaybackInfo[] newArray(int size) {
            return new PlaybackInfo[size];
        }
    };

    // A default PlaybackInfo instance, only use this to mark un-initialized players.
    public static final PlaybackInfo SCRAP = new PlaybackInfo();
}
