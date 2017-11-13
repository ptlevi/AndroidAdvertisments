package com.ptlevi.sapientia.ms.project;

/**
 * Created by ptlev on 2017. 11. 13..
 */

import com.google.android.gms.maps.GoogleMap;

public class Advertisment {

    private String title;
    private String description;
    private String image;
    private GoogleMap googleMap;
    private User creator;

    public Advertisment(String title, String description, String image, GoogleMap googleMap, User creator) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.googleMap = googleMap;
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
