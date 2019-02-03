package com.tourguideapp.android.tourguide.RESTClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Interface which will be used to reach the endpoint */
public interface GetDataService
{
    /** Defining URL endpoints */

    /**
     * @return all POIs
     */
    @GET("poi/")
    Call<List<POI>> getAllPOIs();

    /**
     *@return POIs based on TourID
     **/
    @GET("poi/{tourid}")
    Call<List<POI>> getPOIByTourID(@Path("tourid") int tourid);
}
