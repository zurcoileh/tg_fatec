package com.easy_ride.app.support;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.UserSessionManager;
import com.firebase.client.Firebase;


/**
 * Created by Helio on 10/12/2016.
 */
public class MyService extends Service {

    private ERDBModel model;
    private UserSessionManager session;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Makes the service sticky, so that it gets restarted automatically.
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
        //------- CONF MVP ---------//
        model = new ERDBModel();
        this.session = new UserSessionManager(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onTaskRemoved(Intent rootIntent) {
        //Code here
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isNetworkEnabled){
            model.removeLocation(session.getUserRA(),session.getDriverMode());
        }
        stopSelf();
    }
}
