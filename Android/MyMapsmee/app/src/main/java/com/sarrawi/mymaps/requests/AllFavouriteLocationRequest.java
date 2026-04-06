package com.sarrawi.mymaps.requests;

import com.sarrawi.mymaps.entities.FavouriteLocation;
import com.sarrawi.mymaps.responses.FavouriteLocationResponse;

import java.util.ArrayList;

public class AllFavouriteLocationRequest {

    ArrayList<FavouriteLocationResponse> data;

    public AllFavouriteLocationRequest(ArrayList<FavouriteLocationResponse> data) {
        this.data = data;
    }

    public ArrayList<FavouriteLocationResponse> getData() {
        return data;
    }

    public ArrayList<FavouriteLocation> getFavouriteLocations() {
        ArrayList<FavouriteLocation> res = null;
        if(data.size() > 0){
            for(FavouriteLocationResponse attr : data) {
                res.add(attr.getAttributes());
            }
        }
        return res;
    }

    public void setData(ArrayList<FavouriteLocationResponse> data) {
        this.data = data;
    }
}