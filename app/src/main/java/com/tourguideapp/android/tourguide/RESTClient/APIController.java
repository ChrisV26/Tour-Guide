package com.tourguideapp.android.tourguide.RESTClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class APIController
{
    //My IP for the sample
    private static final String Base_URL ="http://192.168.1.82:3000";
    private static APIController self;
    private Retrofit retrofit;

    private APIController()
    {
        /* For Error-Logs*/
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient=new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit.Builder builder=new Retrofit.Builder()
                .baseUrl(Base_URL)
                .addConverterFactory(JacksonConverterFactory.create());
    }

    public static APIController getInstance()
    {
        if(self==null)
        {
            synchronized (APIController.class)
            {
                if(self==null)
                {
                    self=new APIController();
                }
            }
        }
        return self;
    }

    public Retrofit getRetrofit()
    {
        return retrofit;
    }
}
