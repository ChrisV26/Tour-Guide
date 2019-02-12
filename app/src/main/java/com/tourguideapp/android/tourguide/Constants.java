package com.tourguideapp.android.tourguide;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;

/* Declaring Geofence Places */

 final class Constants
 {

        private Constants()
        { }

        private static final String PACKAGE_NAME = "com.tourguideapp.android.tourguide";

        static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

        /*
          Used to set an expiration time for a geofence. After this amount of time Location Services
          stops tracking the geofence.
        */
        private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

        /*
           For this sample, geofences expire after twelve hours.
        */
        static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
                GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
        static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

        /*
             Map for storing information about sights in Athens.
         */
       /* static final HashMap<String, LatLng> ATHENS_AREA_LANDMARKS = new HashMap<>();

         static
         {
             // Naos Olympiou Dios
            ATHENS_AREA_LANDMARKS.put("Zeus Temple", new LatLng(37.969300, 23.7331));

            // Akropolis Museum
            ATHENS_AREA_LANDMARKS.put("Akropolis Museum", new LatLng(37.968450, 23.728523));

            //Odio Irodio
            ATHENS_AREA_LANDMARKS.put("Odio Irodiou Attikou",new LatLng(37.970795, 23.724583));
        }*/
 }

