package com.tourguideapp.android.tourguide.RESTClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/** Model Class to map JSON to POJO */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class POI
{

    @JsonProperty("id")
    private int id;

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lng")
    private double lng;

   @JsonProperty("tourid")
   private int tourid;

   @JsonProperty("tourName")
   private TourName tourName;

   @JsonProperty("tourDescription")
   private String tourDescription;

    public POI(){}

   public POI(int id,double lat,double lng, int tourid, TourName tourName,String tourDescription)
    {
        this.setId(id);
        this.setLat(lat);
        this.setLng(lng);
        this.setTourID(tourid);
        this.setTourName(tourName);
        this.setTourDescription(tourDescription);

    }

    /** Setters */

    private void setTourID(int tourid)
    {
        this.tourid=tourid;
    }
    private void setLng(double lng) {
        this.lng=lng;
    }

    private void setLat(double lat) {
        this.lat =lat;
    }

    private void setId(int id) {
        this.id=id;
    }

    private void setTourName(TourName tourName)
    {
        this.tourName=tourName;
    }

    private void setTourDescription(String tourDescription)
    {
        this.tourDescription=tourDescription;
    }



    /** Getters */

    public int getId() {
        return id;
    }


    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }

    public int getTourID()
    {
        return tourid;
    }

    public TourName getTourName() {
        return tourName;
    }

    public String getTourDescription() {
        return tourDescription;
    }
}
