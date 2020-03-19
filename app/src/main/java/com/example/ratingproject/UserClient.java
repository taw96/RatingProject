package com.example.ratingproject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserClient {

    String BASE_URL = "http://www.mocky.io/v2/";

    @GET("5e6541c034000064003389bf")
    Call<List<PopupServer>> getShowPopupValue();
}


