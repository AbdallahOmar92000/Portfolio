package com.sarrawi.mymaps.utils;

import com.sarrawi.mymaps.entities.LoginResponse;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.entities.UserLogin;
import com.sarrawi.mymaps.requests.AllFavouriteLocationRequest;
import com.sarrawi.mymaps.requests.FavouriteLocationRequest;
import com.sarrawi.mymaps.responses.DirectionsResponse;
import com.sarrawi.mymaps.responses.FavouriteLocationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LdgoApi {
    @FormUrlEncoded
    @POST("auth/local")
    Call<LoginResponse> login(@Field("email") String identifier,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("auth/local/register")
    Call<UserLogin> register(@Field("name") String name,
                     @Field("email") String email,
//                     @Field("username") String username,
                     @Field("password") String password);

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("favourites")
    Call<FavouriteLocationResponse> addFavouritesLocations(@Header("Authorization") String token,
                                                           @Body FavouriteLocationRequest favourite);

    @GET("favourites")
    Call<AllFavouriteLocationRequest> getFavouritesLocations(@Header("Authorization") String token);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") String id, @Body User user);

    @GET("users/me")
    Call<User> getMe(@Header("Authorization") String token);

    @FormUrlEncoded
    @PUT("users/{id}")
    Call<User> updateUserField(@Path("id") String id, @Field("name") String name,
                        @Field("email") String email,
                        @Field("username") String username,
                        @Field("useMetric") Boolean useMetric);



}
