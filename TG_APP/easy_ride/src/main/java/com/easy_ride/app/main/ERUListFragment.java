package com.easy_ride.app.main;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View;
import android.widget.ListView;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.firebase.client.Firebase;

import java.util.Observable;

public class ERUListFragment extends Fragment implements LocationListener, ERView {

    private View view;

    private ERDBModel model;
    private ERMainController controller;

    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.activity_erlist, container, false);

        Firebase.setAndroidContext(getActivity());

        // Set up ListView
        listView = (ListView) this.view.findViewById(R.id.listView);
        model = new ERDBModel();
        model.addObserver(this);
        controller = new ERMainController(model, this.getActivity());

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        controller.handle(ERMainController.Messages.Initialize);
    }

    @Override
    public void update(Observable observable, Object data) {
        controller.populateListView(data,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        controller.handle(ERMainController.Messages.Initialize);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}