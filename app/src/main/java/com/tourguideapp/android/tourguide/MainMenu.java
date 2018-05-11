package com.tourguideapp.android.tourguide;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/* Main Menu of the App  */
public  class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
//        isOnline();
    }

    /* Implementing the method Click_Map
      When the user clicks on the map icon
      The Map Layout is showed
     */
    public void Click_Map(View view) {
        Intent MapChoice=new Intent(this,MapTourList.class);
        startActivity(MapChoice);
    }

    private boolean isOnline()
    {
        boolean isConnectedWifi=false;
        boolean isConnectedMobileData=false;

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo[]  = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    isConnectedWifi  = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    isConnectedMobileData = true;
        }
        return isConnectedWifi || isConnectedMobileData;
    }
}
