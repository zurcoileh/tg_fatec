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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERDBModel extends MainModel{

    private Firebase refConn;
    private ArrayList<User> users;
    private String LOGIN_STATUS;
    private String SIGNUP_STATUS;

    public ERDBModel(){
        this.refConn = new Firebase(Constants.FIREBASE_USER_REF);
        users = new ArrayList<>();

        //DUMMY DATA INITIALIZATION WITH RA
     //   this.refConn.child("1234567").setValue(new User("1234567", "Helio Ribeiro da Cruz", "helio_r_cruz@hotmail.com","12988083199","Rua Jordao M Ferreira,236","Jd Sao Dimas"));
    /*    this.refConn.child("1234565").setValue(new User("1234565", "Flavio Ribeiro da Cruz", "uteste1@teste.com", "1296885444","Av Rui Barbosa,500","Cidade Salvador","Jacarei","Logistica","Noite"));
        this.refConn.child("1234569").setValue(new User("1234569", "Marcos Mauricio Ribeiro", "uteste3@teste.com","12988653222","Rua Paraibuna 500","Centro","Sao Joseo dos Campos","Banco de Dados","Noite"));

        GeoFire refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_DRIVERS));
        refGeo.setLocation("1234565", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("1234569", new GeoLocation(-23.163525, -45.794087));//Unifesp

        refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_PASSENGERS));
        refGeo.setLocation("1234565", new GeoLocation(-23.217282, -45.893996)); //VALE SUL
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

    public void signUpStatus(){
        this.setChanged();
        this.notifyObservers(SIGNUP_STATUS);
    }

    @Override
    public  void getAllResults(final UserSessionManager session) {

        users = new ArrayList<>();

        refConn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    User user = personSnapshot.getValue(User.class);
                    List<String> mykeys = new LinkedList<String>(Arrays.asList(session.getKeyLocations().split(";")));
                    for (String k : mykeys)
                        if (k.split(",")[0].equals(user.getRa())) {
                            GeoLocation location = new GeoLocation(Double.parseDouble(k.split(",")[1]), Double.parseDouble(k.split(",")[2]));
                            user.setLoc(location);
                            users.add(user);
                        }
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

    public void verifyEmail(String email){
        SIGNUP_STATUS = "VALID_EMAIL";
        refConn.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = (User) child.getValue(User.class);
                    if (user != null) {
                        SIGNUP_STATUS = "USER_EXIST";
                        break;
                    }
                }
                signUpStatus();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                SIGNUP_STATUS = "Error on SIGNUP: " + firebaseError;
                signUpStatus();
            }
        });
    }

    public void signup(String email, String password)
    {
        final Firebase loginReff = new Firebase(Constants.FIREBASE_URL);

        loginReff.createUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                SIGNUP_STATUS = "OK";
                signUpStatus();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                SIGNUP_STATUS = "Error on SIGNUP: " + firebaseError;
                signUpStatus();
            }
        });
    }


    public void getUserData(String email){
        users = new ArrayList<>();
        refConn.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) {
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

    public void storeUser(User user){
        this.refConn = new Firebase(Constants.FIREBASE_USER_REF);
        this.refConn.child(user.getRa()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    SIGNUP_STATUS = "Error on SIGNUP: " + firebaseError;
                    signUpStatus();
                } else {
                    SIGNUP_STATUS = "STORED";
                    signUpStatus();
                }
            }
        });
    }

    public void updateUser(final User user){
        users = new ArrayList<>();
        this.refConn = new Firebase(Constants.FIREBASE_USER_REF);
        this.refConn.child(user.getRa()).setValue(user, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    users = null;
                    update();
                } else {
                    users.add(user);
                    update();
                }
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

        if (session.validKey(session.getUserRA())) {
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
