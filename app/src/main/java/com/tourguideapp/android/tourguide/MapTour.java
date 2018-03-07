package com.tourguideapp.android.tourguide;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapTour extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tour);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     *  This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Turn on the My Location layer and the related control on the map.



        // Add a marker in Athens and move the camera
        LatLng athens = new LatLng( 37.983810, 23.727539);
        mMap.addMarker(new MarkerOptions().position(athens).title("Marker in Athens"));


        //Drawing a line between plaka and thisio
        Polyline polyline1=googleMap.addPolyline(new PolylineOptions()
        .clickable(true)
        .add(
                new LatLng(37.97166278, 23.7166638) ,
                new LatLng(37.96999612, 23.7249971)
        ));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(athens));
        mMap.setOnPolylineClickListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }



}
