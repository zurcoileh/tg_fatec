package com.easy_ride.app.model;

import android.location.Location;
import android.util.Log;

import com.app.easy_ride.R;
import com.easy_ride.app.support.Constants;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERDBModel extends MainModel{

    private Firebase refConn;
    private ArrayList<User> users;
    private String LOGIN_STATUS;

    public ERDBModel(){
        this.refConn = new Firebase(Constants.FIREBASE_USER_REF);
        users = new ArrayList<>();

        //DUMMY DATA INITIALIZATION WITH RA
    /*    this.refConn.child("1234567").setValue(new User("1234567", "Helio", "Ribeiro da Cruz", "helio_r_cruz@hotmail.com","12988083199","Rua Jordao M Ferreira,236","Jd Sao Dimas"));
        this.refConn.child("1234565").setValue(new User("1234565", "Flavio", "Ribeiro da Cruz", "uteste1@teste.com", "1296885444","Av Rui Barbosa,500","Cidade Salvador - Jacarei"));
        this.refConn.child("1234568").setValue(new User("1234568", "Ronan", "Carmo Cruz", "uteste2@teste.com","129886712169","Rua Jordao M Ferreira,236","Jd Sao Dimas"));
        this.refConn.child("1234569").setValue(new User("1234569", "Marcos", "Mauricio Ribeiro", "uteste3@teste.com","12988653222","Rua Paraibuna 500","Centro"));

   /*     GeoFire refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_DRIVERS));
        refGeo.setLocation("1234565", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("1234568", new GeoLocation(-23.203144, -45.907928));//COLINAS
        refGeo.setLocation("1234569", new GeoLocation(-23.163525, -45.794087));//Unifesp

        refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_PASSENGERS));
        refGeo.setLocation("1234565", new GeoLocation(-23.217282, -45.893996)); //VALE SUL
        refGeo.setLocation("1234568", new GeoLocation(-23.1621341,-45.7974797));//PQ TECNOLOGICO
        refGeo.setLocation("1234569", new GeoLocation(-23.145248, -45.795992));//EUG MELLO  */


    }

    @Override
    public void update(){
        this.setChanged();
        this.notifyObservers(users);
    }

    public void loginStatus(){
        this.setChanged();
        this.notifyObservers(LOGIN_STATUS);
    }

    @Override
    public  void getAllResults(final UserSessionManager session) {

        refConn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    User user = personSnapshot.getValue(User.class);
                    List mykeys = Arrays.asList(session.getKeyLocations().split(";"));
                    for (Object k : mykeys) if (((String)k).equals(user.getRa())) users.add(user);
                }
                update();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void setMarkerInfoByKey(String key, final Marker marker){
        refConn.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    marker.setTitle(user.getName());
                    marker.setSnippet(user.getEmail());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void login(final String email, String password, final UserSessionManager session){

        final Firebase loginReff = new Firebase(Constants.FIREBASE_URL);

        loginReff.authWithPassword(email, password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Authentication just completed successfully :)
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("provider", authData.getProvider());
                        if (authData.getProviderData().containsKey("email")) {
                            map.put("email", authData.getProviderData().get("email").toString());
                        }
                        //SESSION SETTINGS
                        session.createUserSession(authData.getProviderData().get("email").toString());
                        storeRASession(authData.getProviderData().get("email").toString(), session);
                        session.configDriverMode(0);
                        session.configInvisMode(0);
                        loginReff.child("users").child(authData.getUid()).setValue(map);
                        LOGIN_STATUS = "OK";
                        loginStatus();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
                        // Something went wrong :(
                        switch (error.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                LOGIN_STATUS = Constants.USER_DO_NOT_EXIST;
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                LOGIN_STATUS = Constants.WRONG_PASSWORD;
                                break;
                            case FirebaseError.NETWORK_ERROR:
                                LOGIN_STATUS = Constants.CONNECT_FAIL;
                                break;
                            default:
                                LOGIN_STATUS = Constants.UNKOWN;
                                break;
                        }
                        loginStatus();
                    }
                });
    }

    public void saveLocation(UserSessionManager session, LatLng location) {
        GeoFire refGeo = new GeoFire(new Firebase(Constants.GEO_REFF(session.getDriverMode())));

        refGeo.setLocation(session.getUserRA(), new GeoLocation(location.latitude, location.longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");
                }
            }
        });
    }

    public void removeLocation(String ra, int mode){
        GeoFire refGeo = new GeoFire(new Firebase(Constants.GEO_REFF(mode)));
        refGeo.removeLocation(ra);
    }

    public void storeRASession(String email, final UserSessionManager session){

        refConn.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = (User) child.getValue(User.class);
                    if (user != null) {
                        session.setSessionRa(user.getRa());
                        session.setSessionName(user.getName());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void fillResult(String email, final Marker marker){

        users = new ArrayList<>();

        refConn.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = (User) child.getValue(User.class);
                    if (user != null){
                        user.setLoc(new GeoLocation(marker.getPosition().latitude,marker.getPosition().longitude));
                        users.add(user);
                        break;
                    }
                }
                update();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}
