package com.easy_ride.app.main;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Helio on 5/1/2016.
 */

public interface ERView extends Observer {
    void update(Observable observable, Object data);
}