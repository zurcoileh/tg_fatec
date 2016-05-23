package com.easy_ride.app.controller;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.MainModel;
import com.easy_ride.app.model.User;

import java.util.ArrayList;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERLoginController extends ERController<MainModel>{

    // constructor
    public ERLoginController(MainModel model, Activity activity)   {
        super(model, activity);
    }

    // Event implementations
    private static final MessageTask<ERLoginController> initializeTask = new MessageTask<ERLoginController>() {
        public void run(ERLoginController sender, Object[] data) {
            ERDBModel model = (ERDBModel)sender.getModel();
            model.getAllResults();
            //model.notifyObservers();
        }
    };

    // Messages enum
    public enum Messages implements Message<ERLoginController> {
        Initialize(initializeTask);
        //CheckChanged(checkChangedTask),
       // Submit(submitTask);

        // Message<T> implementation
        private MessageTask<ERLoginController> task;
        private Messages(MessageTask<ERLoginController> task) {
            this.task = task;
        }

        @Override
        public MessageTask<ERLoginController> getTask() {
            return task;
        }
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