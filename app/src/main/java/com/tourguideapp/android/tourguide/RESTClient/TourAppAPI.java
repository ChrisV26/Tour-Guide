package com.tourguideapp.android.tourguide.RESTClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TourAppAPI
{
    @POST("poi/")
    Call<List<POI>> loadListOfPOIs(@Path("id") int id);
}
