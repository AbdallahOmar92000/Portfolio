package com.sarrawi.mymaps.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

//public class DirectionsResponse {
//    @SerializedName("routes")
//    public List<Route> routes;
//
//    public static class Route {
//        @SerializedName("overview_polyline")
//        public OverviewPolyline overviewPolyline;
//    }
//
//    public static class OverviewPolyline {
//        @SerializedName("points")
//        public String points;
//    }
//}

public class DirectionsResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("legs")
        public List<Leg> legs;

        @SerializedName("overview_polyline")
        public Polyline overviewPolyline;
    }

    public static class Leg {
        @SerializedName("steps")
        public List<Step> steps; // هذا ما كان ينقصك
    }

    public static class Step {
        @SerializedName("html_instructions")
        public String htmlInstructions; // تعليمات مثل "اتجه يميناً"

        @SerializedName("start_location")
        public LocationPoint startLocation;

        @SerializedName("distance")
        public Distance distance;
    }

    public static class Polyline {
        @SerializedName("points")
        public String points;
    }

    public static class LocationPoint {
        @SerializedName("lat")
        public double lat;
        @SerializedName("lng")
        public double lng;
    }

    public static class Distance {
        @SerializedName("text")
        public String text;
    }
}
