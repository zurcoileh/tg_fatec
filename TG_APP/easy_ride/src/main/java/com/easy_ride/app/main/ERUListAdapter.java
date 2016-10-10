package com.easy_ride.app.main;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.easy_ride.R;
import com.easy_ride.app.model.User;
import com.easy_ride.app.support.Constants;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Helio on 5/8/2016.
 */
public class ERUListAdapter  extends BaseAdapter {


    protected Activity activity;
    protected ArrayList<User> users;
    protected boolean msg_option;
    protected LocationListener listener;

    public ERUListAdapter (Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
        this.msg_option = false;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
    }

    public boolean getOption(){
        return msg_option;
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public void setOption(boolean opt){
        this.msg_option = opt;
    }

    public void setLocListener(LocationListener listener){
        this.listener = listener;
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

        TextView title = (TextView) v.findViewById(R.id.name_list);
        title.setText(dir.getName());

        TextView description = (TextView) v.findViewById(R.id.email_list);
        description.setText(dir.getEmail());

        TextView neigh = (TextView) v.findViewById(R.id.neigh_list);
        neigh.setText(dir.getNeigh());

        TextView dist = (TextView) v.findViewById(R.id.dist_list);
        LatLng curr_location =  Constants.getLocation(activity,listener);
        String result = String.format("%.2f", Constants.getDistance(new GeoLocation(curr_location.latitude, curr_location.longitude), dir.getLoc())/1000);
        dist.setText(result+" Km");

        ImageView imagen = (ImageView) v.findViewById(R.id.profileImage);
       // imagen.setImageDrawable(dir.getImage());

        return v;
    }

}
