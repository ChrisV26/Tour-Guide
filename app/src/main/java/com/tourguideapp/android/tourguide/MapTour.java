package com.tourguideapp.android.tourguide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapTour extends FragmentActivity implements OnMapReadyCallback
{
    protected static GoogleMap mMap;
    protected Marker mCurrLocationMarker;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    protected ArrayList<LatLng> MarkerPoints;

    protected LatLng point;
    protected LatLng Current_Location;
    protected LatLng Start_position;
    protected LatLng Dest_position;
    private double start_point_lat;
    private double start_point_lng;
    private double dest_point_lat;
    private double dest_point_lng;
    private String correspond_waypoints;
    // Declare a variable for the cluster manager.
    //private ClusterManager<MyItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tour);

        // Receive the Lat Long coordinates from MapTourList
        Bundle get_long_lang=getIntent().getParcelableExtra("Chosen_Tour");
        if(get_long_lang!=null)
        {
            Start_position = get_long_lang.getParcelable("Start_Location");
            Dest_position = get_long_lang.getParcelable("Dest_Location");
            correspond_waypoints=get_long_lang.getString("Tour_Name");
            Log.i("Coordinates", "Coordinates OK!");
            start_point_lat=Start_position.latitude;
            start_point_lng=Start_position.longitude;
            dest_point_lat=Dest_position.latitude;
            dest_point_lng=Dest_position.longitude;

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initializing the ArrayList and adding the corresponding Waypoints to each of the Chosen Tour
        MarkerPoints = new ArrayList<>();
        if(correspond_waypoints.equals("First_Tour"))
        {
            MarkerPoints.add(new LatLng(37.969300, 23.7331));  // Naos Olympiou Dios
            MarkerPoints.add(new LatLng(37.968450, 23.728523)); // Akropolis Museum
            MarkerPoints.add(new LatLng(37.970795, 23.724583)); // Odio Irodiou attikou
            MarkerPoints.add(new LatLng(37.974651, 23.721972)); // Arxaia Agora
            MarkerPoints.add(new LatLng(37.975818, 23.719245)); // Thisio
        }
        else if(correspond_waypoints.equals("Second_Tour"))
        {
            MarkerPoints.add(new LatLng(37.976960, 23.740877)); //Platia kolonakiou
            MarkerPoints.add(new LatLng(37.981786, 23.743056)); //lykavitos
            MarkerPoints.add(new LatLng(37.982584, 23.734656)); //akadimia athinon
            MarkerPoints.add(new LatLng(37.980395, 23.727566)); //Dimotiki agora athinon
            MarkerPoints.add(new LatLng(37.977955, 23.716889)); // Arxaiologikos xoros Keramikou
        }
        else
        {
            MarkerPoints.add(new LatLng(37.969766, 23.725299)); //Anafiotika
            MarkerPoints.add(new LatLng(37.968334, 23.741112)); //Kallimarmaro
            MarkerPoints.add(new LatLng(37.975382, 23.74534)); // War Museum
            MarkerPoints.add(new LatLng(37.975952, 23.740446)); // Benaki Museum
            MarkerPoints.add(new LatLng(37.974090, 23.73893)); // Votaniko Museum of National Garden
        }
    }

    /* Send the var mMap to ParserTask Class*/
    public static GoogleMap getmMap() {
        return mMap;
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //stop location updates when Activity is no longer active
        if ( mFusedLocationProviderClient != null)
        {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /**
     *  Manipulates the map once available.
     *  This callback is triggered when the map is ready to be used.
     *  This is where we can add markers or lines, add listeners or move the camera.
     *  This method will only be triggered once the user has
     *  installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // updating every 2 minutes the current location of the user
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // granting permission and enabling to find the current location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                //Location Permission already granted
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
            else
            {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        // Adding and Styling the predefined markers of the user's Tour Choice
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(start_point_lat,start_point_lng))
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(dest_point_lat,dest_point_lng))
                .title("End"));


        // Adding the Waypoint Markers
        for(int i=0; i<MarkerPoints.size(); ++i)
        {
            LatLng position=MarkerPoints.get(i);
            addMarkerToMap(position);
        }

        // Send LatLng and fetch directions for the Markers
        String url = getUrl(Start_position,Dest_position);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);

        // move Map Camera
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    // Add Waypoint Markers on Map
    protected void addMarkerToMap(LatLng latlng)
    {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("title")
                .snippet("snippet")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        //markers.add(marker);

    }


 /*   private void setUpClusterer() {
        // Position the map.
       mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(this, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
       *//* mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);*//*

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

       // Set some lat/lng coordinates to start with.
      *//*  double lat = 51.5145160;
        double lng = -0.1270060;*//*

      double lat=Start_position.latitude;
      double lng=Start_position.longitude;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 4; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }*/

    /* Implementing the getUrl method in order to fetch directions from Google Maps Directions API */
    protected String getUrl(LatLng Start_position, LatLng Dest_position)
    {

        // Origin of route
        String str_origin = "origin=" + Start_position.latitude + "," + Start_position.longitude;

        // Destination of route
        String str_dest = "destination=" + Dest_position.latitude + "," + Dest_position.longitude;

        // Adding Waypoints
      String waypoints="waypoints=";
        for(int i=0; i<MarkerPoints.size(); ++i)
        {
            point=MarkerPoints.get(i);
           if(i==0)
                waypoints = "waypoints=";
                waypoints += point.latitude + "," + point.longitude + "|";

        }

        // Travel Mode-Walking
        String mode="mode=walking";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor+"&"+waypoints+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /* Implementing onLocationResult which handles current place of the user */
    LocationCallback mLocationCallback = new LocationCallback()
    {
        @Override
        public void onLocationResult(LocationResult locationResult)
        {
            for (Location location : locationResult.getLocations())
            {
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null)
                {
                    mCurrLocationMarker.remove();
                }

                //Place current Location Marker
                Current_Location = new LatLng(location.getLatitude(),location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(Current_Location);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                mCurrLocationMarker.showInfoWindow();


                // Send LatLong of current position and draw the route between current and start location
                String url_2=getUrl(Current_Location,Start_position);
                FetchUrl FetchUrl = new FetchUrl();
                FetchUrl.execute(url_2);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Current_Location,11));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
        }

    };


    /* Implementing checkLocationPermission where user has to confirm for permission to use his Location  */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapTour.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            }
            else
            {
                // No explanation needed, we can request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    /* Implementing onRequestPermissionsResult where we handle user's choice for permission  */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else
                {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }

    }







} //end of class
