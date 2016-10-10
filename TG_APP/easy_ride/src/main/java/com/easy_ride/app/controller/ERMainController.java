package com.easy_ride.app.controller;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.LocationListener;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.app.easy_ride.R;
import com.easy_ride.app.main.ERLocationFragment;
import com.easy_ride.app.main.ERSettingsFragment;
import com.easy_ride.app.main.ERUListAdapter;
import com.easy_ride.app.main.ERUListFragment;
import com.easy_ride.app.main.ERUserActivity;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.MainModel;
import com.easy_ride.app.model.User;
import com.easy_ride.app.model.UserSessionManager;

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
            UserSessionManager session = new UserSessionManager(sender.getActivity().getApplicationContext());
            model.getAllResults(session);
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
            if ((int)data[0] == 0) {
                Fragment user = new ERUserActivity();
                FragmentManager fragmentManager = frag.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, user).commit();
            }else if ((int)data[0] == 1) {
                Fragment map_view = new ERLocationFragment();
                FragmentManager fragmentManager = frag.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, map_view).commit();
            }else if ((int)data[0] == 2)  {
                Fragment map_view = new ERUListFragment();
                FragmentManager fragmentManager = frag.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, map_view).commit();
            }else{
                Fragment map_view = new ERSettingsFragment();
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

    public void populateListView(Object data, LocationListener listener){
        final ListView listView = (ListView) this.getActivity().findViewById(R.id.listView);
        final UserSessionManager session = new UserSessionManager(getActivity().getApplicationContext());

        if (data!=null){
            ArrayList<User> uList = (ArrayList<User>) data;
            final ERUListAdapter adapter = new ERUListAdapter(this.getActivity(), uList);
            adapter.setLocListener(listener);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView)view.findViewById(R.id.name_list)).getText();

                    // custom dialog
                    final Dialog dialog = new Dialog(view.getContext());
                    dialog.setContentView(R.layout.msg_dialog);
                    dialog.setTitle("Enviar Mensagem");

                    final EditText text_msg = (EditText) dialog.findViewById(R.id.edit_msg);

                    if (session.getDriverMode() == 0)
                        text_msg.setText("Olá, me chamo " + session.getUserName() + " e preciso de uma carona. (Via EasyRide APP)");

                    ToggleButton msg_opt = (ToggleButton) dialog.findViewById(R.id.msg_opt);

                    msg_opt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                            adapter.setOption(isChecked ? true : false);
                        }
                    });

                    final User uSendTo = adapter.getUsers().get(position);

                    Button btn_send = (Button) dialog.findViewById(R.id.btnSendMsg);
                    Button btn_cancel = (Button) dialog.findViewById(R.id.btnCancel);
                    btn_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (adapter.getOption())
                                sendWhats(uSendTo.getPhone(), String.valueOf(text_msg.getText()));
                            else sendSMS(uSendTo.getPhone(), String.valueOf(text_msg.getText()));
                        }
                    });
                    // if button is clicked, close the custom dialog
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                }
            });
        }
    }

    public void sendWhats(String subject,String body) {

        Uri uri = Uri.parse("smsto:" + subject);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra("sms_body", "smsText");
        i.setPackage("com.whatsapp");
        try {
            getActivity().startActivity(i);

        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendSMS(String phoneNo, String msg){
        try {
            Uri uri = Uri.parse("smsto:"+phoneNo);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", msg);
            getActivity().startActivity(it);

        } catch (Exception ex) {
            Toast.makeText(getActivity().getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}