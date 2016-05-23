package com.easy_ride.app.controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.easy_ride.R;
import com.easy_ride.app.main.ERLocationFragment;
import com.easy_ride.app.main.ERUListAdapter;
import com.easy_ride.app.main.ERUListFragment;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.MainModel;
import com.easy_ride.app.model.User;

import java.util.ArrayList;

/**
 * Created by Helio on 5/1/2016.
 */
public class ERMainController extends ERController<MainModel>{

    // constructor
    public ERMainController(MainModel model, Activity activity)   {
        super(model, activity);
    }

    // Event implementations
    private static final MessageTask<ERMainController> initializeTask = new MessageTask<ERMainController>() {
        public void run(ERMainController sender, Object[] data) {
            ERDBModel model = (ERDBModel)sender.getModel();
            model.getAllResults();
            //model.notifyObservers();
        }
    };

    private static final MessageTask<ERMainController> checkChangedTask = new MessageTask<ERMainController>() {
        public void run(ERMainController sender, Object[] data) {
            MainModel model = sender.getModel();
            boolean checked = (Boolean) data[0];
            model.notifyObservers();
        }
    };

    private static final MessageTask<ERMainController> submitTask = new MessageTask<ERMainController>() {
        public void run(ERMainController sender, Object[] data) {
            FragmentActivity frag = (FragmentActivity)sender.getActivity();
            if ((int)data[0] == 1) {
                Fragment map_view = new ERLocationFragment();
                FragmentManager fragmentManager = frag.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, map_view).commit();
            }else {
                Fragment map_view = new ERUListFragment();
                FragmentManager fragmentManager = frag.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, map_view).commit();
            }
        }
    };


    // Messages enum
    public enum Messages implements Message<ERMainController> {
        Initialize(initializeTask),
        //CheckChanged(checkChangedTask),
        Submit(submitTask);
        // Message<T> implementation
        private MessageTask<ERMainController> task;
        private Messages(MessageTask<ERMainController> task) {
            this.task = task;
        }
        @Override
        public MessageTask<ERMainController> getTask() {
            return task;
        }
    }

    public void populateListView(Object data){
        final ListView listView = (ListView) this.getActivity().findViewById(R.id.listView);
        if (data!=null){
            ArrayList<User> uList = (ArrayList<User>) data;
            ERUListAdapter adapter = new ERUListAdapter(this.getActivity(), uList);
            listView.setAdapter(adapter);
        }
    }
}