package com.easy_ride.app.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.model.UserSessionManager;
import com.easy_ride.app.support.Constants;
import com.easy_ride.app.support.MyService;
import com.firebase.client.Firebase;

import java.util.Observable;

public class ERVMainActivity extends FragmentActivity implements ERView {

    private ERDBModel model;
    private ERMainController controller;
    private UserSessionManager session;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mItemTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ermain);

        startService(new Intent(getBaseContext(), MyService.class));

        //-------CONF MENU LATERAL E MENU BARRA TITULO-----------//
        mTitle = mDrawerTitle = getTitle();
        mItemTitles = getResources().getStringArray(R.array.menu_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mItemTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    mDrawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description for accessibility */
                    R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
                public void onDrawerClosed(View view) {
                    getActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
                public void onDrawerOpened(View drawerView) {
                    getActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
            mDrawerLayout.setDrawerListener(mDrawerToggle);


        Firebase.setAndroidContext(this);

        //------- CONF MVP ---------//
        model = new ERDBModel();
        model.addObserver(this);
        controller = new ERMainController(model, this);

        this.session = new UserSessionManager(getApplicationContext());

        //open the map as default screen
        if (savedInstanceState == null) {
            selectItem(1);
        }

    }

    //event called when observers are notified
    @Override
    public void update(Observable observable, Object data) {
       // controller.populateListView(listView,data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_logout:
                this.model.removeLocation(session.getUserRA(),session.getDriverMode());
                this.session.logoutUser();
             return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        switch(position){
            case Constants.OPEN_DEFAULT:
              break;
            case Constants.OPEN_MAP_VIEW:
                controller.handle(ERMainController.Messages.Submit,Constants.OPEN_MAP_VIEW);break;
            case Constants.OPEN_SEARCH_LIST:
                controller.handle(ERMainController.Messages.Submit,Constants.OPEN_SEARCH_LIST);break;
            case Constants.OPEN_SETTINGS:
                controller.handle(ERMainController.Messages.Submit, Constants.OPEN_SETTINGS);break;
            case Constants.OPEN_USER_PROFILE:
                controller.handle(ERMainController.Messages.Submit, Constants.OPEN_USER_PROFILE);break;
            case Constants.ABOUT_PAGE:
                Fragment about = new ResultViewFragment();
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, about).commit();
                break;
            default:break;
        }
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mItemTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    //    controller.handle(ERMainController.Messages.Submit, Constants.OPEN_MAP_VIEW);
    //    setTitle(mItemTitles[1]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isNetworkEnabled){
            model.removeLocation(session.getUserRA(),session.getDriverMode());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a planet
     */


    public static class ResultViewFragment extends Fragment {
        public static final String ARG_MENU_ITEM_NUMBER = "menu_item_number";
        private ImageView link_face;

        public ResultViewFragment() {
            // Empty constructor required for fragment subclasses
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.about, container, false);

            link_face = (ImageView) rootView.findViewById(R.id.link_face);

            link_face.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Finish the registration screen and return to the Login activity
                    try {
                        getActivity().getApplicationContext().getPackageManager()
                                .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                        Intent facebookIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/1194391301")); //Trys to make intent with FB's URI
                        startActivity(facebookIntent);
                    } catch (Exception e) {
                        Intent facebookIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/zurcoileh")); //catches and opens a url to the desired page
                        startActivity(facebookIntent);
                    }
                }
            });
            return rootView;
        }
    }
}
