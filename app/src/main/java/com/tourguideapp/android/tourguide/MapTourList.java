package com.tourguideapp.android.tourguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



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

        //Creating a bundle to send the longitude and latitude of the Begin/End place points to MapTour
        Bundle lat_long=new Bundle();

        switch(view.getId())
        {
            case R.id.First_Tour:
                lat_long.putDouble("Begin_lat",37.970681);
                lat_long.putDouble("Begin_long",23.729414);
                lat_long.putDouble("End_lat",37.97682);
                lat_long.putDouble("End_long",23.724538);
                TourChoice.putExtras(lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Second_Tour:
                lat_long.putDouble("lat",17.03);
                TourChoice.putExtras(lat_long);
                startActivity(TourChoice);
                break;

            case R.id.Thid_Tour:
                lat_long.putDouble("lat",15.03);
                TourChoice.putExtras(lat_long);
                startActivity(TourChoice);
                break;
        }

    }

}//end of class


