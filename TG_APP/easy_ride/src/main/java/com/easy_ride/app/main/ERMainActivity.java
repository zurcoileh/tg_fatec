package com.easy_ride.app.main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.easy_ride.app.model.User;
import com.easy_ride.app.model.UserDAO;
import com.firebase.client.Firebase;
import com.firebase.easy_ride.R;
import com.firebase.geofire.GeoFire;

public class ERMainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ermain);

        //-------------------------------------------------------------//

        Firebase.setAndroidContext(this);
        UserDAO userDao = new UserDAO();
        userDao.setRefConn(new Firebase("https://demolocal.firebaseio.com/users"));

        Intent intent = new Intent(this, ERLocationActivity.class);
        startActivity(intent);



         // setup GeoFire
      //  this.geoFire = new GeoFire(new Firebase("https://demolocal.firebaseio.com/geo"));
        // radius in km
      //  this.geoQuery = this.geoFire.queryAtLocation(INITIAL_CENTER, 1);

       // initializeData(userDao, geoFire);
    }

    public void initializeData(UserDAO userDao, GeoFire geo){

        userDao.save("heliocruz", new User("12345", "Helio", "Ribeiro da Cruz", "uteste1@teste.com"));
        userDao.save("ronancarmo", new User("12346", "Ronan", "Carmo Cruz", "uteste2@teste.com"));
        userDao.save("marcosribeiro", new User("12347", "Marcos", "Mauricio Ribeiro", "uteste3@teste.com"));

        //  userDao.saveLocation(geo,"heliocruz", new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"ronancarmo",new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"marcosribeiro",new GeoLocation(-23.1572774, -45.7953402));

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
