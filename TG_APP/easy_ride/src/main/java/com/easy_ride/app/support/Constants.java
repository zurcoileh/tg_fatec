package com.easy_ride.app.support;

import android.location.Location;

import com.firebase.geofire.GeoLocation;

import java.util.Map;

/**
 * Created by Helio on 5/1/2016.
 */
public class Constants {

    public static final String FIREBASE_URL = "https://demolocal.firebaseio.com/";
    public static final String FIREBASE_USER_REF = "https://demolocal.firebaseio.com/user_data";
    public static final String FIREBASE_GEO_DRIVERS = "https://demolocal.firebaseio.com/geo/drivers";
    public static final String FIREBASE_GEO_PASSENGERS = "https://demolocal.firebaseio.com/geo/passengers";

    public static final GeoLocation INITIAL_CENTER = new GeoLocation(-23.1572774, -45.7953402);
    public static final int INITIAL_ZOOM_LEVEL = 14;

    public static final int DATA_INITIALIZE = 0;
    public static final int DATA_CHECK_CHANGED = 1;
    public static final int DATA_SUBMIT = 2;

    public static final int OPEN_MAP_VIEW = 1;
    public static final int OPEN_SEARCH_LIST = 2;
    public static final int OPEN_SETTINGS = 3;

    public static final int  OPEN_DEFAULT = 0;
    public static final String USER_DO_NOT_EXIST = "Usuário não existe";
    public static final String WRONG_PASSWORD = "Senha incorreta";
    public static final String CONNECT_FAIL = "Erro na conexão";
    public static final String UNKOWN = "Tente novamente mais tarde";


    private static final String ON_SAVE_SUCESS = "added with success!";
    private static final String ON_SAVE_FAIL = "fail to add!";
    private static final String ON_UPDATE_SUCESS = "updated with success!";
    private static final String ON_UPDATE_FAIL = "failed to update!";
    private static final String ON_DELETE_SUCESS = "added with success!";
    private static final String ON_DELETE_FAIL = "failed to delete!";
    private static final String ON_SEARCH_SUCESS = "search success";
    private static final String ON_SEARCH_FAIL = "fail to search";

    public static String GEO_REFF(int mode){
        return mode == 1 ? FIREBASE_GEO_DRIVERS : FIREBASE_GEO_PASSENGERS;
    }


    public static double getDistance(GeoLocation LatLng1, GeoLocation LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }
}
