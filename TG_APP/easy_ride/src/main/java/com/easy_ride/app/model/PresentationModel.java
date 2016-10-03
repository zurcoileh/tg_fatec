package com.easy_ride.app.model;

import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by Helio on 5/1/2016.
 */

public interface PresentationModel {
    void getAllResults(UserSessionManager session);
    void update();
    void login();
    void loginStatus();
}
