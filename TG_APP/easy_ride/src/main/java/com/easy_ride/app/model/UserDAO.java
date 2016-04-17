package com.easy_ride.app.model;

import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;

/**
 * Created by Helio on 4/16/2016.
 */
public class UserDAO {
    private Firebase refConn;

    public Firebase getRefConn() {
        return refConn;
    }

    public void setRefConn(Firebase refConn) {
        this.refConn = refConn;
    }

    public void save(String id, User user){
        Firebase saveData = refConn.child(id);
        saveData.setValue(user);
    }

    public void saveLocation(GeoFire geoFire, String email, GeoLocation loc){

        geoFire.setLocation(email,loc);

    }

    public void getResultById(String id, final TextView result) {
        refConn.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

            private StringBuilder finalResult = new StringBuilder();

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                finalResult.append(snapshot.getValue());
                result.setText(finalResult);
            }
            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    public  ArrayList<String> getAllResults(final ArrayList<String> info) {

        final ArrayList<String> result = new ArrayList<>();

        refConn.addValueEventListener(new ValueEventListener() {
            private StringBuilder finalResult = new StringBuilder();

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot personSnapshot: snapshot.getChildren()) {
                    User p = personSnapshot.getValue(User.class);
                    //  finalResult.append(p + "\n");
                    info.add(p.getName());
                    result.add(p.getName());
                }

                //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, info);

                // result.setText(finalResult);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        return result;
    }
}
