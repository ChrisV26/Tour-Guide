package com.tourguideapp.android.tourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.model.LatLng;


public class MapTourList extends AppCompatActivity
{
    Button BtnFirstTour,BtnSecondTour,BtnThirdTour;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tour_list);

        /* Declaring the Buttons for the MapTourList layout */
        BtnFirstTour=findViewById(R.id.First_Tour);
        BtnSecondTour=findViewById(R.id.Second_Tour);
        BtnThirdTour=findViewById(R.id.Thid_Tour);
    }

    /* Clicking the Image-Button shows the Route Map */
    public void Click_Map_Tour(View view)
    {
        //Creating an Intent to go from MapTourList to MapTour
        Intent TourChoice = new Intent(this, MapTour.class);

        //Creating a bundle to send the longitude and latitude of the Start/End place points to MapTour
        Bundle lat_long=new Bundle();

        switch(view.getId())
        {
            case R.id.First_Tour:
                // send Plaka-Monastiraki coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.970681,23.729414));
                lat_long.putParcelable("Dest_Location",new LatLng(37.97682,23.724538));
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Second_Tour:
                // send Syntagma-Thisio coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.9757,23.7339));
                lat_long.putParcelable("Dest_Location",new LatLng(37.9758,23.7192));
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Thid_Tour:
                // send Akropolis coordinates
                lat_long.putParcelable("Start_Location",new LatLng(37.983810,23.727539));
                lat_long.putParcelable("Dest_Location",new LatLng(37.971341,23.7365537));
                TourChoice.putExtra("Chosen_Tour",lat_long);
                startActivity(TourChoice);
                break;
        }

    }

}//end of class


