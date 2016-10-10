package com.easy_ride.app.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.User;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    private LocationManager locationManager;
    private static boolean msg_option = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; //
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1;
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
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            this.map = mapFragment.getMap();
           // LatLng latLngCenter = new LatLng(Constants.INITIAL_CENTER.latitude, Constants.INITIAL_CENTER.longitude);
            LatLng latLngCenter = getLocation();
            this.searchCircle = this.map.addCircle(new CircleOptions().center(latLngCenter).radius(session.getDistPreferences() * 1000));
            this.searchCircle.setFillColor(Color.argb(66, 205, 210, 255));
            this.searchCircle.setStrokeColor(Color.argb(33, 0, 0, 0));
            this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCenter, Constants.INITIAL_ZOOM_LEVEL));
            this.map.setOnCameraChangeListener(this);

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    String email = marker.getSnippet();

                    if (email != null) {

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mapFragment.getView().getLayoutParams();
                        params.weight = 85.0f;
                        mapFragment.getView().setLayoutParams(params);

                        RelativeLayout result = (RelativeLayout) view.findViewById(R.id.user_result);
                        LinearLayout.LayoutParams params_2 = (LinearLayout.LayoutParams) result.getLayoutParams();
                        params_2.weight = 15.0f;
                        result.setLayoutParams(params_2);

                        model.fillResult(email, marker);
                    }

                    return false;
                }
            });
        }

        Firebase.setAndroidContext(getActivity());
        //save user location to geofire if it is not invisile
        if(session.getInvisMode()==0){
            model.saveLocation(session, getLocation());
        }

        // setup markers
        this.markers = new HashMap<String, Marker>();


        Marker user_marker=null;
        //Set USER LOCATION MARKER  and Geofire query listenner
        if(session.getDriverMode() == 0){
            this.geoFire = new GeoFire(new Firebase(Constants.FIREBASE_GEO_DRIVERS));

            user_marker = this.map.addMarker(new MarkerOptions().position(getLocation()).title("Você está aqui!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon)));
        }else{
            this.geoFire = new GeoFire(new Firebase(Constants.FIREBASE_GEO_PASSENGERS));

            user_marker = this.map.addMarker(new MarkerOptions().position(getLocation()).title("Você está aqui!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
        }

        // radius in km
        this.geoQuery = this.geoFire.queryAtLocation(new GeoLocation(getLocation().latitude, getLocation().longitude),session.getDistPreferences() > 0 ? session.getDistPreferences() : 1);

        this.markers.put("current_user", user_marker);

        //set available user keys to be stored for list manager
        this.session.setLocationKeys("");

        return view;
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data!=null){
            ArrayList<User> uList = (ArrayList<User>) data;
           if (!uList.isEmpty()) {
               final User u = uList.get(0);
               TextView result_name = (TextView) view.findViewById(R.id.name_result);
               TextView result_email = (TextView) view.findViewById(R.id.email_result);
               TextView result_neihg = (TextView) view.findViewById(R.id.neigh_result);
               TextView result_dist = (TextView) view.findViewById(R.id.dist_result);
               Button btnSend = (Button) view.findViewById(R.id.btnSendMessage);

               result_name.setText(u.getName());
               result_email.setText(u.getEmail());
               result_neihg.setText(u.getNeigh());

               String result = String.format("%.2f", Constants.getDistance(new GeoLocation(getLocation().latitude,getLocation().longitude),u.getLoc())/1000);
               result_dist.setText(result+" Km");

               if (session.getInvisMode()==0) {
                   // add button listener
                   btnSend.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View arg0) {
                           // custom dialog
                           final Dialog dialog = new Dialog(view.getContext());
                           dialog.setContentView(R.layout.msg_dialog);
                           dialog.setTitle("Enviar Mensagem");

                           final EditText text_msg = (EditText) dialog.findViewById(R.id.edit_msg);

                           if (session.getDriverMode() == 0)
                               text_msg.setText("Olá, me chamo " + session.getUserName() + " e preciso de uma carona. (Via EasyRide APP)");

                           ToggleButton msg_opt = (ToggleButton) dialog.findViewById(R.id.msg_opt);

                           msg_opt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                                   msg_option = isChecked ? true : false;
                               }
                           });

                           Button btn_send = (Button) dialog.findViewById(R.id.btnSendMsg);
                           Button btn_cancel = (Button) dialog.findViewById(R.id.btnCancel);
                           btn_send.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   if (msg_option)
                                       sendWhats(u.getPhone(), String.valueOf(text_msg.getText()));
                                   else sendSMS(u.getPhone(), String.valueOf(text_msg.getText()));
                               }
                           });
                           // if button is clicked, close the custom dialog
                           btn_cancel.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   dialog.dismiss();
                               }
                           });

                           dialog.show();
                       }
                   });
               }
           }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove all event listeners to stop updating in the background
        this.geoQuery.removeAllListeners();
        this.locationManager.removeUpdates(this);
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

        //store key for list
        this.session.setLocationKeys(this.session.getKeyLocations() + ";" + key + "," + location.latitude + "," + location.longitude);
    }

    @Override
    public void onKeyExited(String key) {
        // Remove any old marker
        Marker marker = this.markers.get(key);
        if (marker != null) {
            marker.remove();
            this.markers.remove(key);

            List<String> mykeys = new LinkedList<String>(Arrays.asList(this.session.getKeyLocations().split(";")));
            String to_remove ="";
            for (String k : mykeys) {
                String key_of =  k.split(",")[0];
                if (key.equals(key_of))to_remove = k;
            }
            mykeys.remove(to_remove);
            this.session.setLocationKeys("");
            for (Object k : mykeys) this.session.setLocationKeys(this.session.getKeyLocations() + ";" + k);

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

    public LatLng getLocation() {
        LatLng result = null;
        try {
            locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
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
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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


    public void updateMarker(Location location) {

        LatLng updated = new LatLng(location.getLatitude(), location.getLongitude());
        // Move the marker
        Marker marker = this.markers.get("current_user");
        if (marker != null) {
            this.animateMarkerTo(marker, updated.latitude, updated.longitude);
        }else{
            Marker user_marker = this.map.addMarker(new MarkerOptions().position(updated).title("You are here!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon)));

            this.markers.put("current_user", user_marker);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateMarker(location);
        //update user location on geofire
        if(session.getInvisMode()==0) {
            model.saveLocation(session, new LatLng(location.getLatitude(), location.getLongitude()));
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

    public void sendWhats(String subject,String body) {

        Uri uri = Uri.parse("smsto:" + subject);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra("sms_body", "smsText");
        i.setPackage("com.whatsapp");
        try {
            getActivity().startActivity(i);

        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }

    /*    final Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO,uri);
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, body);
        whatsappIntent.setType("text/plain");
        startActivity(whatsappIntent);  */
    }

    public void sendSMS(String phoneNo, String msg){
        try {
        //    SmsManager smsManager = SmsManager.getDefault();
         //   smsManager.sendTextMessage(phoneNo, null, msg, null, null);

            Uri uri = Uri.parse("smsto:"+phoneNo);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", msg);
            getActivity().startActivity(it);

        } catch (Exception ex) {
            Toast.makeText(getActivity().getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

}
