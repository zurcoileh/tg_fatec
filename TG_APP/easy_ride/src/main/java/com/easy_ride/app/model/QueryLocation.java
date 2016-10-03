package com.easy_ride.app.model;

import com.easy_ride.app.support.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helio on 5/16/2016.
 */
public class QueryLocation {

    private Map<String, Marker> markers;
    private Map<String, GeoLocation> locations;
    private GeoFire geoFire;
    private GeoQuery geoQuery;


    public QueryLocation() {

        // setup GeoFire
        this.geoFire = new GeoFire(new Firebase(Constants.FIREBASE_GEO_PASSENGERS));
        // radius in km
        this.geoQuery = this.geoFire.queryAtLocation(Constants.INITIAL_CENTER, 1);
        // setup markers
        this.markers = new HashMap<String, Marker>();

    }


    public GeoFire getGeoFire() {
        return geoFire;
    }

    public void setGeoFire(GeoFire geoFire) {
        this.geoFire = geoFire;
    }

    public GeoQuery getGeoQuery() {
        return geoQuery;
    }

    public void setGeoQuery(GeoQuery geoQuery) {
        this.geoQuery = geoQuery;
    }

    public Map<String, Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(Map<String, Marker> markers) {
        this.markers = markers;
    }

    public Map<String, GeoLocation> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, GeoLocation> locations) {
        this.locations = locations;
    }
}