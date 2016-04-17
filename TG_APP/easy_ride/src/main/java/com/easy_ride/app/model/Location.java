package com.easy_ride.app.model;

/**
 * Created by Helio on 4/16/2016.
 */
public class Location {

    private double latitude;
    private double longitude;
    private boolean tag;

    public Location(long latitude, long longitude, boolean tag) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.tag = tag;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}