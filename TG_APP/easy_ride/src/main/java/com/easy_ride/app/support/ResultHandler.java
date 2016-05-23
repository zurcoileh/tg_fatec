package com.easy_ride.app.support;

import com.easy_ride.app.model.User;

import java.util.HashMap;

/**
 * Created by Helio on 4/25/2016.
 */
public class ResultHandler {

    public static HashMap<String,User> getMapResult(String result, User userResult){
        HashMap<String,User> mapResult = new HashMap<>();
        mapResult.put(result,userResult);
        return mapResult;
    }
}
