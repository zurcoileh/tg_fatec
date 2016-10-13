package com.easy_ride.app.model;

import android.location.Location;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Helio on 4/16/2016.
 */
public class User {

    private String ra;
    private String name;
    private String email;
    private GeoLocation location;
    private GeoLocation home;
    private int type_user;
    private String phone;
    private String end;
    private String neigh;
    private String city;
    private String course;
    private String period;


    public User(){}

    public User(String ra, String name,String email,String phone, String end, String neigh,String city, String course, String period) {
        this.ra = ra;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.end = end;
        this.neigh = neigh;
        this.course = course;
        this.period = period;
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType_user() {
        return type_user;
    }

    public void setType_user(int type_user) {
        this.type_user = type_user;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoLocation getLoc() {
        return location;
    }

    public void setLoc(GeoLocation loc) {
        this.location = loc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getNeigh() {
        return neigh;
    }

    public void setNeigh(String neigh) {
        this.neigh = neigh;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
