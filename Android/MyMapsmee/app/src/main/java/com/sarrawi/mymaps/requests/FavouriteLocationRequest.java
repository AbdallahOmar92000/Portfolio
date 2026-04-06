package com.sarrawi.mymaps.requests;

import com.google.gson.annotations.SerializedName;
import com.sarrawi.mymaps.entities.FavouriteLocation;

public class FavouriteLocationRequest {
    @SerializedName("data")
    FavouriteLocation data;

    public FavouriteLocationRequest(FavouriteLocation data) {
        this.data = data;
    }

    public FavouriteLocation getData() {
        return data;
    }

    public void setData(FavouriteLocation data) {
        this.data = data;
    }
}
