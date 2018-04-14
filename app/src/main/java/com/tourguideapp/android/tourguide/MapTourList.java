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

        /* Declaring the Buttons from the  Map Tour List layout */
        BtnFirstTour=findViewById(R.id.First_Tour);
       BtnSecondTour=findViewById(R.id.Second_Tour);
       BtnThirdTour=findViewById(R.id.Thid_Tour);
    }

    /* Clicking the Image-Button shows the route map  */
    public void Click_Map_Tour(View view)
    {
        Intent TourChoice=new Intent(this,MapTour.class);
        startActivity(TourChoice);
    }

}


