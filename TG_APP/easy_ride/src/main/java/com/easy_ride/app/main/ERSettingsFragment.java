package com.easy_ride.app.main;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.UserSessionManager;
import com.firebase.client.Firebase;

import java.util.Observable;


public class ERSettingsFragment extends Fragment implements ERView {

    private View view;
    private ERDBModel model;
    private ERMainController controller;
    private UserSessionManager session;

    private int seekR, seekG, seekB;
    SeekBar dist_settings;
    LinearLayout mScreen;
    ToggleButton driver_mode;
    ToggleButton invis_mode;
    TextView  dist_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_ersettings, container, false);

        //get user session data
        this.session = new UserSessionManager(this.view.getContext().getApplicationContext());

        driver_mode = (ToggleButton) this.view.findViewById(R.id.toggleButton1);
        invis_mode = (ToggleButton) this.view.findViewById(R.id.toggleButton2);

        mScreen = (LinearLayout) this.view.findViewById(R.id.settings1);
        dist_settings = (SeekBar) this.view.findViewById(R.id.seek1);
        dist_settings.setProgressDrawable(new ColorDrawable(Color.rgb( 128,128,128)));
        dist_title = (TextView) this.view.findViewById(R.id.dist_title);

        dist_settings.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                dist_title.setText(progressChanged + "Km");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                session.configDistPreferences(progressChanged);
              //  Toast.makeText(view.getContext().getApplicationContext(),"Configurações atualizadas!",Toast.LENGTH_SHORT).show();
            }
        });


        driver_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if(isChecked)
                    session.configDriverMode(1);
                else
                    session.configDriverMode(0);

             //   Toast.makeText(view.getContext().getApplicationContext(),"Configurações atualizadas!",Toast.LENGTH_SHORT).show();
            }
        });

        invis_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if(isChecked)
                    session.configInvisMode(1);
                else
                    session.configInvisMode(0);

               // Toast.makeText(view.getContext().getApplicationContext(),"Configurações atualizadas!",Toast.LENGTH_SHORT).show();
            }
        });


        Firebase.setAndroidContext(getActivity());

        // Set up ListView
        model = new ERDBModel();
        model.addObserver(this);
        controller = new ERMainController(model, this.getActivity());
        controller.handle(ERMainController.Messages.Initialize);

        //initial settings
        if (session.getDriverMode() == 0) driver_mode.setChecked(false);
        else driver_mode.setChecked(true);
        if (session.getInvisMode() == 0) invis_mode.setChecked(false);
        else invis_mode.setChecked(true);
        dist_settings.setProgress(session.getDistPreferences());


        return view;
    }

    @Override
    public void update(Observable observable, Object data) {

        //controller.populateListView(data);
    }
}