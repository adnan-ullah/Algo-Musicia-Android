package com.example.algomusicia;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ModelAudio implements Parcelable  {

    String audioTitle;
    String audioDuration;
    String audioArtist;
    String audioAlbum;
    Uri audioUri;

    public  ModelAudio()
    {

    }
    protected ModelAudio(Parcel in) {
        audioTitle = in.readString();
        audioDuration = in.readString();
        audioArtist = in.readString();
        audioAlbum = in.readString();
        audioUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<ModelAudio> CREATOR = new Creator<ModelAudio>() {
        @Override
        public ModelAudio createFromParcel(Parcel in) {
            return new ModelAudio(in);
        }

        @Override
        public ModelAudio[] newArray(int size) {
            return new ModelAudio[size];
        }
    };




    public String getaudioTitle() {
        return audioTitle;
    }

    public void setaudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }
    public String getaudioAlbum() {
        return audioAlbum;
    }

    public void setaudioAlbum(String audioAlbum) {
        this.audioAlbum = audioAlbum;
    }

    public String getaudioDuration() {
        return audioDuration;
    }

    public void setaudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public String getaudioArtist() {
        return audioArtist;
    }

    public void setaudioArtist(String audioArtist) {
        this.audioArtist = audioArtist;
    }

    public Uri getaudioUri() {
        return audioUri;
    }

    public void setaudioUri(Uri audioUri) {
        this.audioUri = audioUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(audioTitle);
        dest.writeString(audioDuration);
        dest.writeString(audioArtist);
        dest.writeString(audioAlbum);
        dest.writeParcelable(audioUri, flags);
    }
}
