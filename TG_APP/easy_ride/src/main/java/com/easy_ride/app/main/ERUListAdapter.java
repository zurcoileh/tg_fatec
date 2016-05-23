package com.easy_ride.app.main;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.easy_ride.R;
import com.easy_ride.app.model.User;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Helio on 5/8/2016.
 */
public class ERUListAdapter  extends BaseAdapter {

    private static final GeoLocation INITIAL_CENTER = new GeoLocation(-23.1572774, -45.7953402);
    protected Activity activity;
    protected ArrayList<User> users;

    public ERUListAdapter (Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
    }

    public void addAll(ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            users.add(users.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return users.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.activity_erulist_fragment, null);
        }

        User dir = users.get(position);

        TextView title = (TextView) v.findViewById(R.id.category);
        title.setText(dir.getName());

        TextView description = (TextView) v.findViewById(R.id.texto);
        description.setText(dir.getEmail());

        TextView dist = (TextView) v.findViewById(R.id.dist);
        dist.setText(String.valueOf(getDistance(INITIAL_CENTER, new GeoLocation(-23.1572774, -45.7953402))) + " Km");

        ImageView imagen = (ImageView) v.findViewById(R.id.profileImage);
       // imagen.setImageDrawable(dir.getImage());

        return v;
    }

    public double getDistance(GeoLocation LatLng1, GeoLocation LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;

    }
}
