package com.tourguideapp.android.tourguide.CustomInfoWindow;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.tourguideapp.android.tourguide.R;

public class CustomInfoWindowMap implements GoogleMap.InfoWindowAdapter
{

    private Activity context;

    public CustomInfoWindowMap(Activity context){
       this.context=context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        View view = context.getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        TextView tourNameHeaderText=view.findViewById(R.id.TourName);
        TextView tourDescriptionText=view.findViewById(R.id.TourDescription);

        tourNameHeaderText.setText(marker.getTitle());
        tourDescriptionText.setText(marker.getSnippet());

        return view;
    }
}
