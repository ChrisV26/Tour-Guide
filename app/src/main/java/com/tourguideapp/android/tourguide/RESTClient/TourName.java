package com.tourguideapp.android.tourguide.RESTClient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Model Class to map JSON to POJO */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TourName
{

    @JsonProperty("id")
    private int id;

    @JsonProperty("tourName")
    private String tourName;

    public int getId() {
        return id;
    }

    public TourName(){}

    public TourName(int id,String tourName)
    {
        this.setId(id);
        this.setTourName(tourName);
    }

    /** Setters */
    public void setId(int id) {
        this.id = id;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    /** Getters */
    public int getID()
    {
        return id;
    }
    public String getTourName() {
        return tourName;
    }


}
