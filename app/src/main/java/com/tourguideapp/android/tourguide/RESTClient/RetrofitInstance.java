package com.tourguideapp.android.tourguide.RESTClient;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Create an instance of Retrofit Object
 * This sample uses JACKSON converter
 */

public class RetrofitInstance
{
    //My Machine's IP:Port of Tomcat for the sample
    private static final String Base_URL ="http://192.168.1.82:8080";
    private static Retrofit retrofit;


    public static Retrofit getRetrofitInstance()
    {
        /* For HTTP Error-Logs*/
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient=new OkHttpClient.Builder().addInterceptor(interceptor).build();

        if(retrofit==null)
        {
            retrofit=new retrofit2.Retrofit.Builder()
                    .baseUrl(Base_URL)
                    .client(httpClient)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
