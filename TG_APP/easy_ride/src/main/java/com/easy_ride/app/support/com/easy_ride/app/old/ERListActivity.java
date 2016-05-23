package com.easy_ride.app.support.com.easy_ride.app.old;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.main.ERView;
import com.easy_ride.app.model.ERDBModel;
import com.firebase.client.Firebase;
import java.util.Observable;


public class ERListActivity extends Activity implements ERView {

    private ERDBModel model;
    //private ERUserListController controller;

    private ERMainController controller;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erlist);

        Firebase.setAndroidContext(this);

        // Set up ListView
        listView = (ListView) findViewById(R.id.listView);
        model = new ERDBModel();
        model.addObserver(this);
        //controller = new ERUserListController(model, this);
       // controller.handle(Constants.DATA_INITIALIZE);

        controller = new ERMainController(model, this);
        controller.handle(ERMainController.Messages.Initialize);
    }

    public void checkBoc_onClick(ERView view) {
       // controller.handle(ERMainController.MESSAGE_CHECK_CHANGED, checkBox.isChecked());
    }

    public void button_onClick(ERView view) {
       // controller.handle(ERMainController.MESSAGE_SUBMIT, nameEditText.getText().toString(), optionalEditText.getText().toString(), checkBox.isChecked());
    }

    //event called when observers are notified
    @Override
    public void update(Observable observable, Object data) {
        //controller.populateListView(listView,data);
       // controller.handle(ERMainController.Messages.PopulateList,data);
    }


        /*
        new Thread() {
            @Override
            public void run() {
                synchronized (this){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            }
        }.start(); */
}
