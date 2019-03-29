package com.tourguideapp.android.tourguide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.tourguideapp.android.tourguide.CustomInfoWindow.CustomInfoWindowMap;
import com.tourguideapp.android.tourguide.RESTClient.GetDataService;
import com.tourguideapp.android.tourguide.RESTClient.POI;
import com.tourguideapp.android.tourguide.RESTClient.RetrofitInstance;
import com.tourguideapp.android.tourguide.RESTClient.TourName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapTour extends AppCompatActivity implements OnMapReadyCallback {

    // Google Map Variables
    protected static GoogleMap mMap;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;
    protected FusedLocationProviderClient mFusedLocationProviderClient;
    protected ArrayList<LatLng> MarkerPoints;
    private LocationCallback mLocationCallback;
    private LocationManager locationManager;
    private Context mContext;

    protected LatLng point;
    protected LatLng CurrentLocation;
    protected LatLng Start_position;
    protected LatLng Dest_position;
    private double start_point_lat;
    private double start_point_lng;
    private double dest_point_lat;
    private double dest_point_lng;
    private String correspond_waypoints;
    private Marker marker;

    private static final String TAG = MapTour.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_CODE = 34;

    public GoogleMap getmMap() {
        return mMap;
    }

    //Network Request-Variables
    private double LatWaypoints;
    private double LngWaypoints;
    private TourName tourName;
    private String tour_name;
    private String tourDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tour);

        // Receive the Lat Long coordinates from MapTourList
        Bundle get_long_lang = getIntent().getParcelableExtra("Chosen_Tour");
        if (get_long_lang != null) {
            Start_position = get_long_lang.getParcelable("Start_Location");
            Dest_position = get_long_lang.getParcelable("Dest_Location");
            correspond_waypoints = get_long_lang.getString("Tour_Name");
            Log.i("Coordinates", "Coordinates OK!");
            start_point_lat = Start_position.latitude;
            start_point_lng = Start_position.longitude;
            dest_point_lat = Dest_position.latitude;
            dest_point_lng = Dest_position.longitude;

        } else {
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show();
            Log.d("Lat_Long_Bundle", "NULL Lat Long");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

         // Register Location Updates
        registerLocationUpdates();

        // Initializing the ArrayList and adding the corresponding Waypoints to each of the Chosen Tour
        MarkerPoints = new ArrayList<>();

        switch (correspond_waypoints) {
            case "First_Tour":
                //FetchTourData(1);
                MarkerPoints.add(new LatLng(37.990398, 23.713123)); //Korinthou
                MarkerPoints.add(new LatLng(37.990056,23.716405)); //Home
                MarkerPoints.add(new LatLng(37.988826,23.716695)); //SuperMarket
                MarkerPoints.add(new LatLng(37.969300, 23.7331));  // Naos Olympiou Dios
                MarkerPoints.add(new LatLng(37.968450, 23.728523)); // Akropolis Museum
                MarkerPoints.add(new LatLng(37.970795, 23.724583)); // Odio Irodiou attikou
                MarkerPoints.add(new LatLng(37.974651, 23.721972)); // Arxaia Agora
                MarkerPoints.add(new LatLng(37.975818, 23.719245)); // Thisio
                break;
            case "Second_Tour":
                FetchTourData(2);
                break;
            case "Third_Tour":
                FetchTourData(3);
                break;
        }

    }

    /** Check Location Permissions on Startup */
    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            checkLocationPermission();
        }
    }

    /** Stop location updates when Activity is no longer active */
    @Override
    public void onPause() {
        super.onPause();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    /** Resume Location Updates when Activity is active again */
    @Override
    public void onResume()
    {
        super.onResume();
        if(mLastLocation!=null)
        {
            calculateDistance();
        }
        else
        {
            startLocationUpdates();
        }
    }

    /** Network Request to fetch data from API */
    public void FetchTourData(final int id)
    {
        /* Create handle for the Retrofit-Instance */
        GetDataService service= RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        /* Call the method with parameter in the interface to get the POI data */
        Call<List<POI>> call=service.getPOIByTourID(id);

        /* Log the URL called */
        Log.wtf("URL Called",call.request().url() + "");

        /* make an async call */
        call.enqueue(new Callback<List<POI>>() {
            @Override
            public void onResponse(Call<List<POI>> call, Response<List<POI>> response)
            {

                if(response.isSuccessful())
                {
                    List<POI> poi = response.body();
                    Log.i("POI_RESPONSE_SIZE", String.valueOf(poi.size()));
                    for(int j=1; j<=5; ++j) //iterate the poi List to fetch Lat/Lng,TourName and Tour Description
                    {
                        LatWaypoints=poi.get(j).getLat();
                        LngWaypoints=poi.get(j).getLng();
                        tourName=poi.get(j).getTourName();
                        tour_name=tourName.getTourName();
                        tourDescription=poi.get(j).getTourDescription();
                        MarkerPoints.add(new LatLng(LatWaypoints,LngWaypoints));
                        addMarkerPoints();
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
                Toast.makeText(getApplicationContext(),"Unable to Fetch Data from Server",Toast.LENGTH_SHORT).show();
                Log.d("RESPONSE_FAILURE",t.getMessage());
            }
        });

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

        // updating every 1 minute the current location of the user
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000); // one minute interval
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

        // Adding and Styling the predefined Markers of the User's Tour choice
        Marker Start_Pos=mMap.addMarker(new MarkerOptions()
                .position(new LatLng(start_point_lat,start_point_lng))
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );
        Start_Pos.showInfoWindow();

        Marker End_Pos= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(dest_point_lat,dest_point_lng))
                .title("End"));
        // End_Pos.showInfoWindow();

        // Adding the Waypoint Markers to MarkerPoints List
        addMarkerPoints();

        // Custom Info window in order to show the whole text of snippet
        CustomInfoWindowMap adapter=new CustomInfoWindowMap(MapTour.this);
        mMap.setInfoWindowAdapter(adapter);

        // Send LatLng and fetch directions for the Markers
      /*  String url = getUrl(Start_position,Dest_position);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);*/

        // zoom Camera Map to markers
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    // Adding the Waypoint Markers to MarkerPoints List and connect them with polyline
    protected void addMarkerPoints()
    {
        for(int i=0; i<MarkerPoints.size(); ++i)
        {
            LatLng position=MarkerPoints.get(i);
            addMarkerToMap(position);
            mMap.addPolyline(new PolylineOptions().color(Color.RED)
                    .addAll(
                            MarkerPoints
                    ));
        }
    }

    // Add Waypoint Markers on Google Maps
    protected void addMarkerToMap(LatLng latlng)
    {
        marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(tour_name)
                .snippet(tourDescription)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }

    // calculating Distance between Last Known Location and POIs
    private void calculateDistance()
    {
        float[] results = new float[1];
        float distance=0;
        for(int i=0; i<MarkerPoints.size(); i++)
        {
            mLastLocation.distanceBetween(this.mLastLocation.getLatitude(), this.mLastLocation.getLongitude(),MarkerPoints.get(i).latitude,MarkerPoints.get(i).longitude, results);
            distance = results[0];
            Log.i("DistanceBetweenLoc_POI",String.valueOf(distance));
            if(distance<=15)
            {
                Toast.makeText(this,"You Arrived!",Toast.LENGTH_LONG).show();
            }
        }

    }

    private void registerLocationUpdates()
    {
        mContext = this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (!checkPermissions()) {
            checkLocationPermission();
        }
        if(mLastLocation!=null)
        {
            calculateDistance();
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    120000,
                    15, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    120000,
                    15, locationListener);
        }
    }

   LocationListener locationListener=new LocationListener() {
       @Override
       public void onLocationChanged(Location location) {
           calculateDistance();
           Log.i("Location Changed",String.valueOf(location));
       }

       @Override
       public void onStatusChanged(String s, int i, Bundle bundle) {

       }

       @Override
       public void onProviderEnabled(String s) {

       }

       @Override
       public void onProviderDisabled(String s) {

       }
   };

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

        // Sensor disabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor+"&"+waypoints+"&"+mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private void startLocationUpdates() {
        /* Implementing onLocationResult which handles current place of the user */
            mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    mLastLocation = location;

                    //Current Location
                    CurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    calculateDistance();

                    // Send LatLong of current position and draw the route between current and start location
                     /* String url_2=getUrl(CurrentLocation,Start_position);
                         FetchUrl FetchUrl = new FetchUrl();
                             FetchUrl.execute(url_2);*/

                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLocation, 11));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
            }

        };
    }

    /** Return the current state of the permissions needed */
    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /** Implementing checkLocationPermission where user has to confirm for permission to use his Location  */
    private void checkLocationPermission()
    {
            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

            // Should we show an explanation?
            if (shouldProvideRationale)
            {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location Permission, please accept to use Location Functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapTour.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_CODE);
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
                        PERMISSIONS_REQUEST_CODE);
            }

    }

    /** Implementing onRequestPermissionsResult where we handle user's choice for permission  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_CODE:
            {
                // permission was granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                }
                else if(grantResults.length<= 0) // If request is cancelled, the grant result arrays are empty
                {
                    Log.i("PERMISSSIONS REQUEST","User Interaction was cancelled");
                }
                else
                {
                    // Permission was denied,
                    // Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    Log.i("PERMISSIONS REQUEST","User denied the functionality");
                }
                //return;
            }

        }

    }

} //end of class
