package com.easy_ride.app.model;

import com.app.easy_ride.R;
import com.easy_ride.app.support.Constants;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
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

        this.refConn.child("heliocruz").setValue(new User("12345", "Helio", "Ribeiro da Cruz", "uteste1@teste.com"));
        this.refConn.child("ronancarmo").setValue(new User("12346", "Ronan", "Carmo Cruz", "uteste2@teste.com"));
        this.refConn.child("marcosribeiro").setValue( new User("12347", "Marcos", "Mauricio Ribeiro", "uteste3@teste.com"));

        //  userDao.saveLocation(geo,"heliocruz", new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"ronancarmo",new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"marcosribeiro",new GeoLocation(-23.1572774, -45.7953402));


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
    public  void getAllResults() {

        refConn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                    User user = personSnapshot.getValue(User.class);
                    users.add(user);
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
                marker.setTitle(user.getName());
                marker.setSnippet(user.getEmail());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void login(final String email, String password, final UserSessionManager session){

        final Firebase loginReff = new Firebase(Constants.FIREBASE_URL);

        loginReff.authWithPassword(email,password,
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        // Authentication just completed successfully :)
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("provider", authData.getProvider());
                        if(authData.getProviderData().containsKey("email")) {
                            map.put("email", authData.getProviderData().get("email").toString());
                        }
                        session.createUserSession(authData.getProviderData().get("email").toString(),email);
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
}
