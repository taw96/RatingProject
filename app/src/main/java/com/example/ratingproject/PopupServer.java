package com.example.ratingproject;

import com.google.gson.annotations.SerializedName;

public class PopupServer {

    private Boolean show_rating_popup;


    public PopupServer(Boolean show_rating) {
        this.show_rating_popup = show_rating;

    }

    public Boolean getShow_rating_popup() {
        return show_rating_popup;
    }

    public void setShow_rating_popup(Boolean show_rating_popup) {
        this.show_rating_popup = show_rating_popup;
    }
}
