package com.fearefull.dotaanalyser;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FriendHomeActivity extends AppCompatActivity {

    String tag_json_obj = "json_obj_req";
    private static final String TAG = MainActivity.class.getSimpleName();
    boolean isReceiving = false;
    String myUrl;
    String steamID;
    Bundle bundle;

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
        setContentView(R.layout.activity_friend_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        bundle = getIntent().getExtras();

        toolbar.setNavigationContentDescription("back");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final ArrayList<View> outViews = new ArrayList();
            toolbar.findViewsWithText(outViews, "back", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL)
                outViews.get(0).setRotation(180f);
        }

        displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        heightScreen = displaymetrics.heightPixels;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        this.menu = menu;

        ImageView friendHistoryProfileImage = (ImageView) findViewById(R.id.friendHistoryImageProfile);
        TextView friendHistoryProfileUsername = (TextView) findViewById(R.id.friendHistoryUsernameProfile);
        TextView friendHistoryProfileState = (TextView) findViewById(R.id.friendHistoryStateProfile);

        Picasso.with(getApplicationContext()).load(bundle.getString("image")).into(friendHistoryProfileImage);
        assert friendHistoryProfileUsername != null;
        friendHistoryProfileUsername.setText(bundle.getString("person_name"));
        assert friendHistoryProfileState != null;
        friendHistoryProfileState.setText(bundle.getString("state"));

        steamID = bundle.getString("steam_id");
        isReceivingSteamID = true;

        createTable();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshFunction();
        }
        else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return false;
    }

    public void createTable() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Records"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapterFriend adapter = new PagerAdapterFriend
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
                AlertDialog alertDialog = new AlertDialog.Builder(FriendHomeActivity.this).create();
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
                AlertDialog alertDialog = new AlertDialog.Builder(FriendHomeActivity.this).create();
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

            TabFragmentFriendOverview tabFragmentHomeOverview = (TabFragmentFriendOverview) getSupportFragmentManager().getFragments().get(0);
            TabFragmentFriendRecords tabFragmentHomeRecords = (TabFragmentFriendRecords) getSupportFragmentManager().getFragments().get(2);

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

        TabFragmentFriendOverview tabFragmentFriendOverview = (TabFragmentFriendOverview)getSupportFragmentManager().getFragments().get(0);
        TabFragmentFriendRecords tabFragmentFriendRecords = (TabFragmentFriendRecords)getSupportFragmentManager().getFragments().get(2);

        tabFragmentFriendOverview.setRefresh();
        tabFragmentFriendRecords.setRefresh();
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
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
