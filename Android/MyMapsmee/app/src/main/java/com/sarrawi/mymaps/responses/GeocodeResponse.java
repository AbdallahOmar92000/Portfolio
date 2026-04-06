package com.sarrawi.mymaps.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeocodeResponse {

    @SerializedName("results")
    public List<Result> results;

    @SerializedName("status")
    public String status;

    public static class Result {

        @SerializedName("formatted_address")
        public String formattedAddress;

        @SerializedName("geometry")
        public Geometry geometry;
    }

    public static class Geometry {

        @SerializedName("location")
        public Location location;
    }

    public static class Location {

        @SerializedName("lat")
        public double lat;

        @SerializedName("lng")
        public double lng;
    }
}
