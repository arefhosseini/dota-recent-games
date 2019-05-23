package com.fearefull.dotaanalyser;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    DBHandler dbHandler;
    Bundle bundle;
    boolean doubleBackToExitPressedOnce = false;
    User loggedInUser;
    String myUrl;
    String steamID;
    boolean isReceiving = false;
    DisplayMetrics displaymetrics;
    int heightScreen;
    boolean isReceivingData = false;
    boolean isReceivingSteamID = false;
    boolean isNetworkError = false;
    boolean isExposePublicMatchError = false;
    boolean isInProgressAnalysing = false;
    JSONObject jsonResult;
    JSONArray jsonArray;
    JSONArray jsonArrayRecords;
    Menu menu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHandler = new DBHandler(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface DancingScript = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.otf");
        mTitle.setTypeface(DancingScript);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        heightScreen = displaymetrics.heightPixels;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                exitApp();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    public void createTable() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Records"));
        tabLayout.addTab(tabLayout.newTab().setText("Teammates"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapterHome adapter = new PagerAdapterHome
                (getSupportFragmentManager(), tabLayout.getTabCount());
        assert viewPager != null;
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        this.menu = menu;

        try {
            SplashActivity.splashActivity.finish();
            OauthActivity.oauthActivity.finish();
        } catch (Exception ignored) {
        }

        loggedInUser = dbHandler.getUser(1);
        steamID = loggedInUser.getSteamID();
        isReceivingSteamID = true;
        TextView personName = (TextView) findViewById(R.id.personNameTextView);
        assert personName != null;
        TextView realName = (TextView) findViewById(R.id.realNameTextView);
        assert realName != null;
        personName.setText(loggedInUser.getPersonName());
        realName.setText(loggedInUser.getRealName());
        Picasso.with(getApplicationContext()).load(loggedInUser.getImagePath()).into((ImageView) findViewById(R.id.profileImage));
        createTable();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshFunction();
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friends) {
            Intent mIntent=new Intent(HomeActivity.this, FriendsActivity.class);
                        startActivity(mIntent);
        } else if (id == R.id.nav_favorite) {
            Toast.makeText(HomeActivity.this, "Not Yet!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_info) {
            Toast.makeText(HomeActivity.this, "Not Yet!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_about_us) {
            CustomAboutUs cdd = new CustomAboutUs(HomeActivity.this);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(HomeActivity.this, "Not Yet!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_signOut) {
            User signedOutUSer = new User(1, loggedInUser.getSteamID(), loggedInUser.getPersonName(), loggedInUser.getRealName(), loggedInUser.getImagePath(), "1");
            dbHandler.updateUser(signedOutUSer);

            try {
                FriendsActivity.friendsActivity.finish();
            } catch (Exception ignored) {
            }

            Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_exit) {
            exitApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exitApp() {
        try {
            FriendsActivity.friendsActivity.finish();
        } catch (Exception ignored) {
        }
        finishAffinity();

    }

    private void getData() {
        if (!isReceiving) {
            myUrl = "http://dotaanalyser.000webhostapp.com/api/analyse/?steam_id=" + steamID + "&get_last=1";
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(myUrl);
            if(entry != null){
                try {
                    String data = new String(entry.data, "UTF-8");
                    JSONObject myJsonObject = new JSONObject(data);
                    createLayout(myJsonObject);
                    Long tsLong = System.currentTimeMillis()/1000;
                    Long tsPastLong = myJsonObject.getLong("update_time");
                    if (tsLong - tsPastLong > 3600) {
                        sendRequest(myUrl, false);
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    sendRequest(myUrl, true);
                    e.printStackTrace();
                }
            }
            else{
                sendRequest(myUrl, true);
            }
            isReceiving = true;
        }
    }

    public void createLayout(JSONObject response) {
        try {
            jsonResult = response;
            if (jsonResult.getInt("status") == 15) {
                isExposePublicMatchError = true;
            }
            else if(jsonResult.getInt("status") == 3) {
                isInProgressAnalysing = true;
            }
            else if(jsonResult.getInt("status") == 2) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Attention");
                alertDialog.setMessage("Analysing of this player is not finished.");
                alertDialog.setIcon(R.mipmap.icon_luncher);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                jsonArray = jsonResult.getJSONArray("most_played_heroes");
                jsonArrayRecords = jsonResult.getJSONArray("records");
            }
            else {
                jsonArray = jsonResult.getJSONArray("most_played_heroes");
                jsonArrayRecords = jsonResult.getJSONArray("records");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            isNetworkError = true;
        }
        isReceivingData = true;
    }

    public void recreateLayout(JSONObject response) {
        isReceiving = false;
        isReceivingData = false;
        isNetworkError = false;
        isInProgressAnalysing = false;
        isExposePublicMatchError = false;

        try {
            jsonResult = response;
            if (jsonResult.getInt("status") == 15) {
                isExposePublicMatchError = true;
            }
            else if(jsonResult.getInt("status") == 3) {
                isInProgressAnalysing = true;
            }
            else if(jsonResult.getInt("status") == 2) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Attention");
                alertDialog.setMessage("Analysing of this player is not finished.");
                alertDialog.setIcon(R.mipmap.icon_luncher);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                jsonArray = jsonResult.getJSONArray("most_played_heroes");
                jsonArrayRecords = jsonResult.getJSONArray("records");
            }
            else {
                jsonArray = jsonResult.getJSONArray("most_played_heroes");
                jsonArrayRecords = jsonResult.getJSONArray("records");
            }

            TabFragmentHomeOverview tabFragmentHomeOverview = (TabFragmentHomeOverview) getSupportFragmentManager().getFragments().get(0);
            TabFragmentHomeRecords tabFragmentHomeRecords = (TabFragmentHomeRecords)getSupportFragmentManager().getFragments().get(2);

            tabFragmentHomeOverview.setRefresh();
            tabFragmentHomeRecords.setRefresh();

        } catch (JSONException e) {
            e.printStackTrace();
            isNetworkError = true;
        }
        isReceivingData = true;
    }

    public void refreshFunction() {
        myUrl = "http://dotaanalyser.000webhostapp.com/api/analyse/?steam_id=" + steamID + "&get_last=1";
        isReceiving = false;
        isReceivingData = false;
        isNetworkError = false;
        isInProgressAnalysing = false;
        isExposePublicMatchError = false;
        sendRequest(myUrl, true);

        TabFragmentHomeOverview tabFragmentHomeOverview = (TabFragmentHomeOverview) getSupportFragmentManager().getFragments().get(0);
        TabFragmentHomeRecords tabFragmentHomeRecords = (TabFragmentHomeRecords)getSupportFragmentManager().getFragments().get(2);
        TabFragmentHomeTeammates tabFragmentHomeTeammates = (TabFragmentHomeTeammates)getSupportFragmentManager().getFragments().get(3);

        tabFragmentHomeOverview.setRefresh();
        tabFragmentHomeRecords.setRefresh();
        tabFragmentHomeTeammates.setRefresh();
    }

    public void sendRequest(String req_url, final boolean is_create) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                req_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (is_create) {
                            createLayout(response);
                        }
                        else {
                            recreateLayout(response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (is_create) {
                    isReceivingData = true;
                    isNetworkError = true;
                }
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
