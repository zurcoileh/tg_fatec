package com.easy_ride.app.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.easy_ride.R;
import com.easy_ride.app.controller.ERMainController;
import com.easy_ride.app.model.ERDBModel;
import com.easy_ride.app.support.Constants;
import com.firebase.client.Firebase;

import java.util.Observable;

public class ERVMainActivity extends FragmentActivity implements ERView {

    private ERDBModel model;
    private ERMainController controller;

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

        //-------CONF MENU LATERAL E MENU BARRA TITULO-----------//
        mTitle = mDrawerTitle = getTitle();
        mItemTitles = getResources().getStringArray(R.array.menu_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mItemTitles));
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

        if (savedInstanceState == null) {
            selectItem(0);
        }


        Firebase.setAndroidContext(this);

        //------- CONF MVP ---------//
        model = new ERDBModel();
        model.addObserver(this);
        controller = new ERMainController(model, this);
    }

    //event called when observers are notified
    @Override
    public void update(Observable observable, Object data) {
       // controller.populateListView(listView,data);
    }

    /*
    public void initializeData(UserDAO userDao, GeoFire geo){

        userDao.save("heliocruz", new User("12345", "Helio", "Ribeiro da Cruz", "uteste1@teste.com"));
        userDao.save("ronancarmo", new User("12346", "Ronan", "Carmo Cruz", "uteste2@teste.com"));
        userDao.save("marcosribeiro", new User("12347", "Marcos", "Mauricio Ribeiro", "uteste3@teste.com"));

        //  userDao.saveLocation(geo,"heliocruz", new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"ronancarmo",new GeoLocation(-23.1572774, -45.7953402));
        //  userDao.saveLocation(geo,"marcosribeiro",new GeoLocation(-23.1572774, -45.7953402));

    }*/

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
              /*  Fragment fragment = new ResultViewFragment();
                Bundle args = new Bundle();
                args.putInt(ResultViewFragment.ARG_MENU_ITEM_NUMBER, position);
                fragment.setArguments(args);*/ break;

            case Constants.OPEN_MAP_VIEW:
                controller.handle(ERMainController.Messages.Submit,Constants.OPEN_MAP_VIEW);break;

            case Constants.OPEN_SEARCH_LIST:
                controller.handle(ERMainController.Messages.Submit,Constants.OPEN_SEARCH_LIST);break;

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

    /*
    public static class ResultViewFragment extends Fragment {
        public static final String ARG_MENU_ITEM_NUMBER = "menu_item_number";

        public ResultViewFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_result_view, container, false);
            int i = getArguments().getInt(ARG_MENU_ITEM_NUMBER);
            String menu_item = getResources().getStringArray(R.array.menu_items_array)[i];

        //    int imageId = getResources().getIdentifier(menu_item.toLowerCase(Locale.getDefault()),
                //    "drawable", getActivity().getPackageName());
         //   ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(menu_item);
            return rootView;
        }
    } */
}
