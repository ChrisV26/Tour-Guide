package com.tourguideapp.android.tourguide;

import android.app.Application;

/*  Initialize Global Data */
public class IntroApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Globals.init(this);
    }
}
