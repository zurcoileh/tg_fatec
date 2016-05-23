package com.easy_ride.app.support.com.easy_ride.app.old;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.User;
import com.easy_ride.app.support.Constants;

import java.util.ArrayList;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERUserListController {

    private Activity activity;
    private ERDBModel model;

    public ERUserListController(ERDBModel model, Activity activity)   {
        this.activity = activity;
        this.model = model;
    }

    public Activity getActivity() {
        return activity;
    }

    public ERDBModel getModel() {
        return model;
    }

    // handle event
    public void handle(int message, final Object... data) {
        switch (message) {
            case Constants.DATA_INITIALIZE:
                doInitialize(data);
                break;
            case Constants.DATA_CHECK_CHANGED:
                doCheckChanged(data);
                break;
            case Constants.DATA_SUBMIT:
                doSubmit(data);
                break;
        }
    }

    private void doSubmit(Object[] data) {
        // Intent i = new Intent(activity, AnotherActivity.class);
        //  activity.startActivity(i);
    }

    private void doCheckChanged(Object[] data) {
        ArrayList<User> users = new ArrayList<>();
        for (Object obj: data){
            users.add((User) obj);
        }
        model.notifyObservers();
    }

    private void doInitialize(Object[] data) {
      // ArrayAdapter<String> adapter =  (ArrayAdapter<String>)data[0];
       model.getAllResults();
      // model.notifyObservers();
    }

    public void populateListView(ListView lv, Object data){

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        if (data!=null){
            ArrayList<User> uList = (ArrayList<User>) data;
            for (User u: uList) {
                adapter.add(u.getName());
            }
        }
        lv.setAdapter(adapter);
    }
}
