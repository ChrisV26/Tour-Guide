package com.tourguideapp.android.tourguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public  class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    /** Implementing the method Click_Map
     * When the user clicks on the map icon
     * The Map Layout is showed
     */
    public void Click_Map(View view) {
        Intent MapChoice=new Intent(this,MapTourList.class);
        startActivity(MapChoice);
    }
}
