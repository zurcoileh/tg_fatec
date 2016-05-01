package com.easy_ride.app.model;

import java.util.HashMap;

/**
 * Created by Helio on 4/25/2016.
 */
public class ResultHandler {

    private static final String ON_SAVE_SUCESS = "added with success!";
    private static final String ON_SAVE_FAIL = "fail to add!";
    private static final String ON_UPDATE_SUCESS = "updated with success!";
    private static final String ON_UPDATE_FAIL = "failed to update!";
    private static final String ON_DELETE_SUCESS = "added with success!";
    private static final String ON_DELETE_FAIL = "failed to delete!";
    private static final String ON_SEARCH_SUCESS = "search success";
    private static final String ON_SEARCH_FAIL = "fail to search";

    public static HashMap<String,User> getMapResult(String result, User userResult){
        HashMap<String,User> mapResult = new HashMap<>();
        mapResult.put(result,userResult);
        return mapResult;
    }
}
