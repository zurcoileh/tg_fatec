package com.easy_ride.app.model;

/**
 * Created by Helio on 4/16/2016.
 */
public class User {

    private String ra;
    private String name;
    private String lastname;
    private String email;
    private Location location;
    private Location home;
    private int type_user;

    public User(){}

    public User(String ra, String name, String lastname, String email) {
        this.ra = ra;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    @Override
    public String toString(){
        return name+"  "+lastname;
    }

    public Location getLoc() {
        return location;
    }

    public void setLoc(Location loc) {
        this.location = loc;
    }
}
