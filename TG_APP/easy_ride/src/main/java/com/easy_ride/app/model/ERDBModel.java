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
import java.util.Random;

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
    //    this.refConn.child("1234565").setValue(new User("1234565", "Flavio Ribeiro da Cruz", "uteste1@teste.com", "1296885444","Av Rui Barbosa,500","Cidade Salvador","Jacarei","Logistica","Noite"));
    //    this.refConn.child("1234569").setValue(new User("1234569", "Marcos Mauricio Ribeiro", "uteste3@teste.com","12988653222","Rua Paraibuna 500","Centro","Sao Joseo dos Campos","Banco de Dados","Noite"));

    /*    GeoFire refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_DRIVERS));
        refGeo.setLocation("1234569", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT

        refGeo.setLocation("123457", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123458", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123459", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123450", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123451", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123452", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT
        refGeo.setLocation("123453", new GeoLocation(-23.200423, -45.892367)); //PQ SANTOS DUMONT

        GeoFire refGeo = new GeoFire(new Firebase(Constants.FIREBASE_GEO_PASSENGERS));
        double lat = -23.1967997;
        double lon = -45.8873514;
        for (int i=0;i<100;i++) {

            if (i > 10){
             /*   String direction = (Constants.DIRECTIONS[new Random().nextInt(Constants.DIRECTIONS.length)]);
                double new_position = Constants.getNewLocation(lat,lon,2.0, direction);
                if (direction == Constants.DIRECTIONS[0] || direction == Constants.DIRECTIONS[1]) {
                    refGeo.setLocation("12345" + String.valueOf(i), new GeoLocation(new_position,lon));
                    lat = new_position;
                }
                if (direction == Constants.DIRECTIONS[2] || direction == Constants.DIRECTIONS[3]) {
                    refGeo.setLocation("12345" + String.valueOf(i), new GeoLocation(lon,new_position));
                    lon = new_position;
                }
                refGeo.removeLocation("12345" + String.valueOf(i));


            }else{
                //refGeo.setLocation("12345" + String.valueOf(i), new GeoLocation(-23.1967997, -45.8873514));
            }
        }    */


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
                        session.configDistPreferences(1);
                        loginReff.child("users").child(authData.getUid()).setValue(map);
                        LOGIN_STATUS = "OK";
                        loginStatus();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError error) {
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
                        user.setLoc(new GeoLocation(marker.getPosition().latitude, marker.getPosition().longitude));
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

    public void removeKeyOnDisconnect(final String ra){
        // remove their GeoFire entry
        Firebase refDrivers = new Firebase(Constants.FIREBASE_GEO_DRIVERS);
        refDrivers.child(ra).onDisconnect().removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError error, Firebase firebase) {
                if (error != null) {
                    Log.d("ERROR DISCONNECT", "could not establish onDisconnect event:" + error.getMessage());
                }else{
                    Log.d("NETWORK", "MONITORING_DRIVER KEY" + ra);
                }
            }
        });
        Firebase refPass = new Firebase(Constants.FIREBASE_GEO_PASSENGERS);
        refPass.child(ra).onDisconnect().removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError error, Firebase firebase) {
                if (error != null) {
                    Log.d("ERROR DISCONNECT", "could not establish onDisconnect event:" + error.getMessage());
                } else {
                    Log.d("NETWORK","MONITORING_PASSENGER KEY" + ra);
                }
            }
        });
    }
}
