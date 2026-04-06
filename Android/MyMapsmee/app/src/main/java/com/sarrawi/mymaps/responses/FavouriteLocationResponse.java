package com.sarrawi.mymaps.responses;


import com.sarrawi.mymaps.entities.FavouriteLocation;

public class FavouriteLocationResponse {
    int id;
    FavouriteLocation attributes;

    public FavouriteLocationResponse(int id, FavouriteLocation attributes) {
        this.id = id;
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FavouriteLocation getAttributes() {
        return attributes;
    }

    public void setAttributes(FavouriteLocation attributes) {
        this.attributes = attributes;
    }
}
