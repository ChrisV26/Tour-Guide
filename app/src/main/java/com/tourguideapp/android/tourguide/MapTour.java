package com.tourguideapp.android.tourguide;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Map;

public class MapTour extends AppCompatActivity implements OnMapReadyCallback,OnCompleteListener<Void>
{
    // Google Map Variables

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

    // Geofence Variables

    private static final String TAG = MapTour.class.getSimpleName();

    public GoogleMap getmMap() {
        return mMap;
    }

    //Tracks whether the user requested to add or remove geofences, or to do neither.
    private enum PendingGeofenceTask
    {
        ADD, REMOVE, NONE
    }

    //Provides access to the Geofencing API.
    private GeofencingClient mGeofencingClient;


    //The list of geofences used in this sample.
    private ArrayList<Geofence> mGeofenceList;


    //Used when requesting to add or remove geofences.
    private PendingIntent mGeofencePendingIntent;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

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
        else{
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_LONG).show();
            Log.i("Lat_Long_Bundle","NULL Lat Long");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initializing the ArrayList and adding the corresponding Waypoints to each of the Chosen Tour
        MarkerPoints = new ArrayList<>();
        switch (correspond_waypoints)
        {
            case "First_Tour":
                MarkerPoints.add(new LatLng(37.969300, 23.7331));  // Naos Olympiou Dios
                MarkerPoints.add(new LatLng(37.968450, 23.728523)); // Akropolis Museum
                MarkerPoints.add(new LatLng(37.970795, 23.724583)); // Odio Irodiou attikou
                MarkerPoints.add(new LatLng(37.974651, 23.721972)); // Arxaia Agora
                MarkerPoints.add(new LatLng(37.975818, 23.719245)); // Thisio
                break;
            case "Second_Tour":
                MarkerPoints.add(new LatLng(37.976960, 23.740877)); //Platia kolonakiou
                MarkerPoints.add(new LatLng(37.981786, 23.743056)); //lykavitos
                MarkerPoints.add(new LatLng(37.982584, 23.734656)); //akadimia athinon
                MarkerPoints.add(new LatLng(37.980395, 23.727566)); //Dimotiki agora athinon
                MarkerPoints.add(new LatLng(37.977955, 23.716889)); // Arxaiologikos xoros Keramikou
                break;
            case "Third_Tour":
                MarkerPoints.add(new LatLng(37.969766, 23.725299)); //Anafiotika
                MarkerPoints.add(new LatLng(37.968334, 23.741112)); //Kallimarmaro
                MarkerPoints.add(new LatLng(37.975382, 23.74534)); // War Museum
                MarkerPoints.add(new LatLng(37.975952, 23.740446)); // Benaki Museum
                MarkerPoints.add(new LatLng(37.974090, 23.73893)); // Votaniko Museum of National Garden
                break;
        }

        /* Geofence Initialization */

        // Get the UI widgets
        mAddGeofencesButton = findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton =  findViewById(R.id.remove_geofences_button);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
    }

    /* Check Location Permissions on Startup */
    @Override
    public void onStart()
    {
        super.onStart();
        if(!checkPermissions())
        {
            checkLocationPermission();
        }
        else{
            performPendingGeofenceTask();
        }
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

    /* ActionBar Menu to Add or Remove Geofences */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_geofence, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.add_geof:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.rm_geof:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

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

        // Adding and Styling the predefined Markers of the User's Tour choice
        Marker Start_Pos=mMap.addMarker(new MarkerOptions()
                .position(new LatLng(start_point_lat,start_point_lng))
                .title("Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );
        // Start_Pos.showInfoWindow();

        Marker End_Pos= mMap.addMarker(new MarkerOptions()
                .position(new LatLng(dest_point_lat,dest_point_lng))
                .title("End"));
        // End_Pos.showInfoWindow();

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

        // move Camera Map
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
        //marker.showInfoWindow();

    }

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

    /* Geofence methods */

    /*
      Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
      Also specifies how the geofence notifications are initially triggered.
    */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /*
      Adds geofences, which sets alerts to be notified when the device enters or exits one of the
      specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view)
    {
        if(!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            checkLocationPermission();
            return;
        }
        addGeofences();
    }

    /* Adds geofences. This method should be called after the user has granted the location permission. */
    @SuppressWarnings("MissingPermission")
    private void addGeofences()
    {

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /*
      Removes geofences, which stops further notifications when the device enters or exits
      previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view)
    {

        if(!checkPermissions())
        {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            checkLocationPermission();
            return;
        }
        removeGeofences();
    }

    /* Removes geofences. This method should be called after the user has granted the location permission. */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences()
    {
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /*
      Runs when the result of calling addGeofences() and/or  removeGeofences()
      is available.
      @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task)
    {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            setButtonsEnabledState();

            int messageId = getGeofencesAdded() ? R.string.geofences_added :
                    R.string.geofences_removed;
            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    /*
      Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
      issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
      current list of geofences.

      @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent()
    {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

      /*
         This sample hard codes geofence data.
         A real app might dynamically create geofences based on the user's location.
       */
    private void populateGeofenceList()
    {
        for (Map.Entry<String, LatLng> entry : Constants.ATHENS_AREA_LANDMARKS.entrySet())
        {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)

                    // Create the geofence
                    .build());
        }
    }

    /*
      Ensures that only one button is enabled at any time.
      The Add Geofences button is enabled if the user hasn't yet added geofences.
      The Remove Geofences button is enabled if the user has added geofences.
    */
    private void setButtonsEnabledState()
    {
        if (getGeofencesAdded()) {
            mAddGeofencesButton.setEnabled(false);
            mRemoveGeofencesButton.setEnabled(true);
        } else {
            mAddGeofencesButton.setEnabled(true);
            mRemoveGeofencesButton.setEnabled(false);
        }
    }

    /* Returns true if geofences were added, otherwise false. */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

   /*
      Stores whether geofences were added ore removed in {SharedPreferences};
      @param added Whether geofences were added or removed.
   */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /* Performs the geofencing task that was pending until location permission was granted. */
    private void performPendingGeofenceTask()
    {
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
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
               /* MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(Current_Location);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                mCurrLocationMarker.showInfoWindow();*/


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

    /* Return the current state of the permissions needed */
    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /* Implementing checkLocationPermission where user has to confirm for permission to use his Location  */
    private static final int PERMISSIONS_REQUEST_CODE = 99;
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
    }

    /* Implementing onRequestPermissionsResult where we handle user's choice for permission  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_CODE:
            {
                // If request is cancelled, the grant result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    performPendingGeofenceTask();
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
                    // Permission was denied,
                    // Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    mPendingGeofenceTask = PendingGeofenceTask.NONE;
                }
                return;
            }

        }

    }







} //end of class
