package com.easy_ride.app.support;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.Map;

/**
 * Created by Helio on 5/1/2016.
 */
public class Constants {

    public static final String FIREBASE_URL = "https://demolocal.firebaseio.com/";
    public static final String FIREBASE_USER_REF = "https://demolocal.firebaseio.com/user_data";
    public static final String FIREBASE_GEO_DRIVERS = "https://demolocal.firebaseio.com/geo/drivers";
    public static final String FIREBASE_GEO_PASSENGERS = "https://demolocal.firebaseio.com/geo/passengers";


    public static final String[] DIRECTIONS = {"NORTH","SOUTH","EAST","WEST"};

    public static final GeoLocation INITIAL_CENTER = new GeoLocation(-23.1572774, -45.7953402);
    public static final int INITIAL_ZOOM_LEVEL = 16;

    public static final int DATA_INITIALIZE = 0;
    public static final int DATA_CHECK_CHANGED = 1;
    public static final int DATA_SUBMIT = 2;

    public static final int OPEN_USER_PROFILE = 0;
    public static final int OPEN_MAP_VIEW = 1;
    public static final int OPEN_SEARCH_LIST = 2;
    public static final int OPEN_SETTINGS = 3;
    public static final int ABOUT_PAGE = 4;

    public static final int  OPEN_DEFAULT = -1;
    public static final String USER_DO_NOT_EXIST = "Usuário não existe";
    public static final String WRONG_PASSWORD = "Senha incorreta";
    public static final String CONNECT_FAIL = "Erro na conexão";
    public static final String UNKOWN = "Tente novamente mais tarde";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;


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

    public static LatLng getLocation(Activity activity, LocationListener listenner) {
        LatLng result = null;
        try {
            LocationManager locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
            //the default criteria
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            result  =  new LatLng(location.getLatitude(), location.getLongitude());

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                // locationManager.removeUpdates(this);

            } else {
                // this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, listenner);
                    // Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            result  =  new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, listenner);
                        // Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                result  =  new LatLng(location.getLatitude(), location.getLongitude());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static double getNewLocation(double xx_lat,double xx_long,double xx_distance,String Direction)
    {

        int equator_circumference=6371000;
        int polar_circumference=6356800;

        double m_per_deg_long =  360 / polar_circumference;
        double rad_lat=(xx_lat* (Math.PI) / 180);
        double m_per_deg_lat = 360 / ( Math.cos(rad_lat) * equator_circumference);

        double deg_diff_long = xx_distance * m_per_deg_long;
        double deg_diff_lat  = xx_distance * m_per_deg_lat;


        double xx_north_lat = xx_lat + deg_diff_long;
        //double xx_north_long= xx_long;
        double xx_south_lat = xx_lat - deg_diff_long;
        //double xx_south_long= xx_long;

        //double xx_east_lat = xx_lat;
        double xx_east_long= xx_long + deg_diff_lat;
        //double xx_west_lat = xx_lat;
        double xx_west_long= xx_long - deg_diff_lat;

        if (Direction.toUpperCase().contains("NORTH")) {
            return xx_north_lat;
        } else if (Direction.toUpperCase().contains("SOUTH"))
        {
            return xx_south_lat;
        } else if (Direction.toUpperCase().contains("EAST"))
        {
            return xx_east_long;
        } else if (Direction.toUpperCase().contains("WEST"))
        {
            return xx_west_long;
        }
        else
            return 0;

    }
}
