package com.example.tech.society.models;

/**
 * Created by tech on 9/30/17.
 */

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Date;
@Entity
public class ProblemModel {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String cityName;
    private String areaName;
    private String problem;
    private Double lat;
    private Double lon;
    @TypeConverters(DateConverter.class)
    private Date borrowDate;

    public ProblemModel(String cityName, String areaName,
                        String problem, Double lat, Double lon,
                        Date borrowDate) {
        this.cityName = cityName;
        this.areaName = areaName;
        this.problem = problem;
        this.lat = lat;
        this.lon = lon;
        this.borrowDate = borrowDate;
    }

    public String getCityName() {
        return cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getId() {
        return id;
    }

    public String getProblem() {
        return problem;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public LatLng getMapLatlng(){
       return  new LatLng(lat, lon);
    }
}