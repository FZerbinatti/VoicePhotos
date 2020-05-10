package com.francesco.voicephotos.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Photo implements Serializable {

    private String photo_name;
    private String photo_path;
    private String photo_description;
    private String orientation;

    public Photo(String photo_name, String photo_path, String photo_description, String orientation) {
        this.photo_name = photo_name;
        this.photo_path = photo_path;
        this.photo_description = photo_description;
        this.orientation = orientation;
    }

    public String getPhoto_name() {
        return photo_name;
    }

    public void setPhoto_name(String photo_name) {
        this.photo_name = photo_name;
    }

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public String getPhoto_description() {
        return photo_description;
    }

    public void setPhoto_description(String photo_description) {
        this.photo_description = photo_description;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


}
