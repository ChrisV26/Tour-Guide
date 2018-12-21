package com.tourguideapp.android.tourguide.RESTClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class POI
{
    @JsonProperty("id")
    private int id;
    @JsonProperty("lat")
    private BigDecimal lat;
    @JsonProperty("lng")
    private BigDecimal lng;
    @JsonProperty("Tour")
    private int Tour;
    @JsonProperty("name_id")
    private int name_id;

    //public POI(){}

   public POI(int id,BigDecimal lat,BigDecimal lng, int Tour,int name_id)
    {
        this.setId(id);
        this.setLat(lat);
        this.setLng(lng);
        this.setTour(Tour);
        this.setName_id(name_id);
    }

    // Setters
    @JsonProperty("Tour")
   private void setTour(int Tour) {
        this.Tour=Tour;
    }
    @JsonProperty("lng")
    private void setLng(BigDecimal lng) {
        this.lng=lng;
    }
    @JsonProperty("lat")
    private void setLat(BigDecimal lat) {
        this.lat =lat;
    }
    @JsonProperty("id")
    private void setId(int id) {
        this.id=id;
    }
    @JsonProperty("name_id")
    private void setName_id(int name_id){
        this.name_id =name_id;
    }

    // Getters
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("lat")
    public BigDecimal getLat() {
        return lat;
    }

    @JsonProperty("lng")
    public BigDecimal getLng() {
        return lng;
    }

    @JsonProperty("Tour")
    public int getTour() {
        return Tour;
    }

    @JsonProperty("name_id")
    public int getName_id() {
        return name_id;
    }
}
