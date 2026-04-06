package com.sarrawi.mymaps.utils;
import com.sarrawi.mymaps.responses.DirectionsResponse;
import com.sarrawi.mymaps.responses.DistanceBetweenLocations;
import com.sarrawi.mymaps.responses.GeocodeResponse;
import com.sarrawi.mymaps.responses.SearchesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LdgoGoogleMapsApi {
    @GET("place/textsearch/json?key=AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc")
    Call<SearchesResponse> searchForPlace(@Query("query") String query);

    @GET("distancematrix/json?key=AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc")
    Call<DistanceBetweenLocations> getDistanceBetweenLocations(@Query("units") String units,
                                                               @Query("destinations") String destinations,
                                                               @Query("origins") String origins);

    @GET("maps/api/directions/json")
    Call<DirectionsResponse> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key
    );

    @GET("geocode/json?key=AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc")
    Call<GeocodeResponse> geocode(@Query("address") String address);

    @GET("maps/api/directions/json")
    Call<DirectionsResponse> getDirectionsgi(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode, // أضفنا هذا السطر
            @Query("key") String key
    );

    // تأكد أيضاً من وجود دالة جلب المسافات
    @GET("maps/api/distancematrix/json")
    Call<DistanceBetweenLocations> getDistanceBetweenLocationsgi(
            @Query("units") String units,
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("key") String key
    );


}