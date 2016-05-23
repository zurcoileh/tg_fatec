package com.easy_ride.app.model;

import com.easy_ride.app.support.Constants;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERDBModel extends MainModel{

    private Firebase refConn;

    private ArrayList<User> users;

    public ERDBModel(){
        this.refConn = new Firebase(Constants.FIREBASE_USER_REF);
        users = new ArrayList<>();
    }

    @Override
    public void update(){
        this.setChanged();
        this.notifyObservers(users);
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
}
