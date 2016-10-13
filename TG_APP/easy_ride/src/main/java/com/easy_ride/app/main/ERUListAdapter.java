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
    LayoutInflater inflater;

    public ERUListAdapter (Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
        this.msg_option = false;
        inflater = LayoutInflater.from(activity);
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
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_erulist_fragment, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        User user = users.get(position);

        mViewHolder.title.setText(user.getName());
        mViewHolder.description.setText(user.getEmail());
        mViewHolder.neigh.setText(user.getNeigh() + " " + user.getCity());
        mViewHolder.course.setText(user.getCourse() + " " + user.getPeriod());
        LatLng curr_location =  Constants.getLocation(activity,listener);

        Double dist =  Constants.getDistance(new GeoLocation(curr_location.latitude, curr_location.longitude), user.getLoc())/1000;
        String result = dist >=  1.0 ? String.format("%.2f Km",dist) : String.format("%.2f metros",dist * 1000);
        mViewHolder.dist.setText(result);

        return convertView;
    }

    private class MyViewHolder {
        TextView title, description,neigh,course,dist;
        ImageView imagen;

        public MyViewHolder(View v) {
            title = (TextView) v.findViewById(R.id.name_list);
            description = (TextView) v.findViewById(R.id.email_list);
            neigh = (TextView) v.findViewById(R.id.neigh_list);
            course = (TextView) v.findViewById(R.id.course_list);
            dist = (TextView) v.findViewById(R.id.dist_list);
            imagen = (ImageView) v.findViewById(R.id.profileImage);

        }
    }
}
