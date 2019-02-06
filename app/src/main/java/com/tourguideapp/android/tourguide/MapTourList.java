package com.tourguideapp.android.tourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.tourguideapp.android.tourguide.RESTClient.GetDataService;
import com.tourguideapp.android.tourguide.RESTClient.POI;
import com.tourguideapp.android.tourguide.RESTClient.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapTourList extends AppCompatActivity
{
    Button BtnFirstTour,BtnSecondTour,BtnThirdTour;
    double Starting_LocPoint_Lat;
    double Starting_LocPoint_Lng;
    double Destination_LocPoint_Lat;
    double Destination_LocPoint_Lng;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tour_list);

        // Declaring the Buttons for the MapTourList Layout
        BtnFirstTour=findViewById(R.id.First_Tour);
        BtnSecondTour=findViewById(R.id.Second_Tour);
        BtnThirdTour=findViewById(R.id.Thid_Tour);

        // Declaring the "Up Button"
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


    }

    /** Implementing the "Up Button" to go back in Parent Activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Clicking the Image-Button shows the Google Map
     *  and fetches async Lat/Lng Starting and Destination coordinates from the API
     * */
    public void Click_Map_Tour(View view)
    {
        //Creating an Intent to go from MapTourList to MapTour
        Intent TourChoice = new Intent(this, MapTour.class);

        //Creating a bundle to send the Longitude and Latitude of the Start/End Marker Points to MapTour
        Bundle lat_long=new Bundle();

        switch(view.getId())
        {
            // send Plaka-Monastiraki coordinates
            case R.id.First_Tour:
                FetchTourData(1);
                lat_long.putParcelable("Start_Location",new LatLng(Starting_LocPoint_Lat,Starting_LocPoint_Lng));
                lat_long.putParcelable("Dest_Location",new LatLng(Destination_LocPoint_Lat,Destination_LocPoint_Lng));
                lat_long.putString("Tour_Name","First_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            // send Syntagma-Thisio coordinates
            case R.id.Second_Tour:
                FetchTourData(2);
                lat_long.putParcelable("Start_Location",new LatLng(Starting_LocPoint_Lat,Starting_LocPoint_Lng));
                lat_long.putParcelable("Dest_Location",new LatLng(Destination_LocPoint_Lat,Destination_LocPoint_Lng));
                lat_long.putString("Tour_Name","Second_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            // send Akropolis-Zappeion coordinates
            case R.id.Thid_Tour:
                FetchTourData(3);
                lat_long.putParcelable("Start_Location",new LatLng(Starting_LocPoint_Lat,Starting_LocPoint_Lng));
                lat_long.putParcelable("Dest_Location",new LatLng(Destination_LocPoint_Lat,Destination_LocPoint_Lng));
                lat_long.putString("Tour_Name","Third_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;
        }

    }

    public void FetchTourData(int tourid)
    {
        /* Create handle for the Retrofit-Instance Interface */
        GetDataService service=RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        /* Call the method with parameter in the interface to get the POI data */
        Call<List<POI>> call=service.getPOIByTourID(tourid);

        /* Log the URL called */
        Log.wtf("URL Called",call.request().url() + "");

        call.enqueue(new Callback<List<POI>>() {
            @Override
            public void onResponse(Call<List<POI>> call, Response<List<POI>> response)
            {

                if(response.isSuccessful())
                {
                    List<POI> poi = response.body();
                    Log.i("POI_RESPONSE_SIZE", String.valueOf(poi.size()));
                   for(POI p: poi) //iterate the poi List to fetch Lat/Lng
                   {
                        Starting_LocPoint_Lat= poi.get(0).getLat();
                        Starting_LocPoint_Lng=poi.get(0).getLng();
                        Destination_LocPoint_Lat=poi.get(6).getLat();
                        Destination_LocPoint_Lng=poi.get(6).getLng();
                   }


                }
                else
                {
                    //Log the HTTP Response Code
                    Log.d("RESPONSE_CODE",response.code()+"");
                }

            }

            @Override
            public void onFailure(Call<List<POI>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Unable to Retrieve Data from Server",Toast.LENGTH_SHORT).show();
                Log.d("RESPONSE_FAILURE",t.getMessage());
            }
        });

    }

}//end of class


