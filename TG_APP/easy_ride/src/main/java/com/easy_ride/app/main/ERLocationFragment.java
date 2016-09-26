package com.easy_ride.app.main;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.UserSessionManager;
import com.easy_ride.app.support.Constants;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

public class ERLocationFragment extends Fragment implements GeoQueryEventListener, GoogleMap.OnCameraChangeListener,LocationListener,ERView {

    private ERMainController controller;
    private ERDBModel model;
    private GoogleMap map;
    private Circle searchCircle;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private View view;
    private UserSessionManager session;

    private Map<String,Marker> markers;

    public ERLocationFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_erlocation,container,false);
        //------ Setting MVP -------//
        model = new ERDBModel();
        model.addObserver(this);
        controller = new ERMainController(model,this.getActivity());
        //get user session data
        this.session = new UserSessionManager(this.view.getContext().getApplicationContext());

        // setup map and camera position
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            this.map = mapFragment.getMap();
           // LatLng latLngCenter = new LatLng(Constants.INITIAL_CENTER.latitude, Constants.INITIAL_CENTER.longitude);
            LatLng latLngCenter = getLocation();
            this.searchCircle = this.map.addCircle(new CircleOptions().center(latLngCenter).radius(session.getDistPreferences() * 1000));
            this.searchCircle.setFillColor(Color.argb(66, 205, 210, 255));
            this.searchCircle.setStrokeColor(Color.argb(33, 0, 0, 0));
            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, Constants.INITIAL_ZOOM_LEVEL));
            this.map.setOnCameraChangeListener(this);
        }

        Firebase.setAndroidContext(getActivity());

        // setup GeoFire
        this.geoFire = new GeoFire(new Firebase(Constants.FIREBASE_GEO_REF));
        // radius in km
        this.geoQuery = this.geoFire.queryAtLocation(new GeoLocation(getLocation().latitude, getLocation().longitude),session.getDistPreferences() > 0 ? session.getDistPreferences() : 1);

        // setup markers
        this.markers = new HashMap<String, Marker>();


        Marker user_marker=null;
        //Set USER LOCATION MARKER
        if(session.getDriverMode() == 0){
            user_marker = this.map.addMarker(new MarkerOptions().position(getLocation()).title("Você está aqui!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon)));
        }else{
            user_marker = this.map.addMarker(new MarkerOptions().position(getLocation()).title("Você está aqui!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
        }

        this.markers.put("current_user", user_marker);

        return view;
    }

    @Override
    public void update(Observable observable, Object data) {
        //controller.populateListView(data);
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        this.geoQuery.removeAllListeners();
        /*for (Marker marker: this.markers.values()) {
            marker.remove();
        }
        this.markers.clear(); */
    }

    @Override
    public void onStart() {
        super.onStart();
        // add an event listener to start updating locations again
        this.geoQuery.addGeoQueryEventListener(this);
    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        // Add a new marker to the map
        Marker marker =null;
        if(session.getDriverMode() == 0){
            marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
        }else{
            marker = this.map.addMarker(new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon)));
        }
        this.markers.put(key, marker);
        model.setMarkerInfoByKey(key, marker);
    }

    @Override
    public void onKeyExited(String key) {
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);
        }
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        // Move the marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            this.animateMarkerTo(marker, location.latitude, location.longitude);
        }
    }

    @Override
    public void onGeoQueryReady() {
    }

    @Override
    public void onGeoQueryError(FirebaseError error) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Animation handler for old APIs without animation support
    private void animateMarkerTo(final Marker marker, final double lat, final double lng) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long DURATION_MS = 3000;
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final LatLng startPosition = marker.getPosition();
        handler.post(new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);

                double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                marker.setPosition(new LatLng(currentLat, currentLng));

                // if animation is not finished yet, repeat
                if (t < 1) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000/Math.pow(2, zoomLevel);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        // Update the search criteria for this geoQuery and the circle on the map
        LatLng center = cameraPosition.target;
        double radius = zoomLevelToRadius(cameraPosition.zoom);
      //  this.searchCircle.setCenter(center);
      //  this.searchCircle.setRadius(radius);
        this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
        // radius in km
    //    this.geoQuery.setRadius(radius/1000);
    }

    public LatLng getLocation()
    {
        // Get the location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude ();
            lon = location.getLongitude ();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // Move the marker
        Marker marker = this.markers.get("current_user");
        if (marker != null) {
            this.animateMarkerTo(marker, getLocation().latitude, getLocation().longitude);
        }else{
            Marker user_marker = this.map.addMarker(new MarkerOptions().position(getLocation()).title("You are here!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon)));

            this.markers.put("current_user", user_marker);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
