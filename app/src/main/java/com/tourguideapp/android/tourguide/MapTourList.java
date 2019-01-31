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
    GetDataService service;

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

        /* Create handle for the Retrofit-Instance Interface */
        service=RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        /* Call the method with parameter in the interface to get the POI data*/
        Call<List<POI>> call=service.getPOIByTourID(1);

        /* Log the URL called */
        Log.wtf("URL Called",call.request().url() + "");

        call.enqueue(new Callback<List<POI>>() {
            @Override
            public void onResponse(Call<List<POI>> call, Response<List<POI>> response)
            {

                if(response.isSuccessful())
                {
                    List<POI> poi= response.body();
                }
                else
                {
                    Log.d("RESPONSE_CODE",response.code()+"");
                }

            }

            @Override
            public void onFailure(Call<List<POI>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Unable to Retrieve data",Toast.LENGTH_SHORT).show();
                Log.d("RESPONSE_FAILURE",t.getMessage());
            }
        });


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

    /** Clicking the Image-Button shows the Google Route Map */
    public void Click_Map_Tour(View view)
    {
        //Creating an Intent to go from MapTourList to MapTour
        Intent TourChoice = new Intent(this, MapTour.class);

        //Creating a bundle to send the Longitude and Latitude of the Start/End Marker Points to MapTour
        Bundle lat_long=new Bundle();

        switch(view.getId())
        {
            case R.id.First_Tour:
                // send Plaka-Monastiraki coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.970681,23.729414));
                lat_long.putParcelable("Dest_Location",new LatLng(37.97682,23.724538));
                lat_long.putString("Tour_Name","First_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Second_Tour:
                // send Syntagma-Thisio coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.9757,23.7339));
                lat_long.putParcelable("Dest_Location",new LatLng(37.9758,23.7192));
                lat_long.putString("Tour_Name","Second_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Thid_Tour:
                // send Akropolis-Zappeion coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.971532, 23.725749));
                lat_long.putParcelable("Dest_Location",new LatLng(37.971341,23.7365537));
                lat_long.putString("Tour_Name","Third_Tour");
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;
        }

    }

}//end of class


