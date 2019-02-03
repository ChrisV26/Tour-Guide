package com.tourguideapp.android.tourguide;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/** Main Menu of the App  */
public  class MainMenu extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

   // Check on Startup of the App if Wifi or Mobile Data is enabled
    @Override
    public void onStart()
    {
        super.onStart();
        // check for Network Connection
        if(!isNetworkConnected())
        {
            // Create an Alert Dialog Message
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Internet Connection or GPS is Required," +
                    "Please enable your WiFi/Mobile Data or GPS")
                    .setCancelable(false)
                    // kill the app
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            finish();

                        }
                    });
            AlertDialog alert=builder.create();
            alert.show();
        }


    }

    /* Implementing the method Click_Map
      When the user clicks on the map icon
      The Map Layout is showed
     */
    public void Click_Map(View view)
    {
        Intent MapChoice=new Intent(this,MapTourList.class);
        startActivity(MapChoice);
    }

    /* Check if the device is connected either to WiFi or Mobile Data  */
    private boolean isNetworkConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)  // connected to the internet
        {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                // connected to WiFi
                return true;

            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                // connected to the mobile provider's data plan
                return true;
            }
        } else {
            // not connected to the internet
            return false;
        }
        return false;
    }
/*
    private boolean isGPSEnabled()
    {
        LocationManager location_manager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return false;
    }*/
}
