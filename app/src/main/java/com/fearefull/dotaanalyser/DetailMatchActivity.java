package com.fearefull.dotaanalyser;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class DetailMatchActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    String match_id, lobby_type, game_mode, start_time, region, duration, my_url;
    int radiant_win;
    JSONArray players;
    JSONObject resultJson;
    Bundle bundle;
    boolean isReceiving = false;
    boolean isReceivingData = false;
    boolean isNetworkError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_match);

        bundle = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
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

        TextView mTitle = (TextView) toolbar.findViewById(R.id.matchIDDetail);
        Typeface DancingScript = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.otf");
        mTitle.setTypeface(DancingScript);

        match_id = bundle.getString("match_id");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        TextView matchIDDetail = (TextView) findViewById(R.id.matchIDDetail);
        matchIDDetail.setText("Match " + match_id);
        createTable();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.action_favorite) {

        }

        return super.onOptionsItemSelected(item);
    }

    public void createTable() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Farm"));
        tabLayout.addTab(tabLayout.newTab().setText("Damage"));
        tabLayout.addTab(tabLayout.newTab().setText("Items"));
        tabLayout.addTab(tabLayout.newTab().setText("Build"));
        tabLayout.addTab(tabLayout.newTab().setText("Graphs"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapterDetail adapter = new PagerAdapterDetail
                (getSupportFragmentManager(), tabLayout.getTabCount());
        assert viewPager != null;
        viewPager.setOffscreenPageLimit(7);
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

    public void getData() {
        if (!isReceiving) {
            my_url = "http://dotaanalyser.000webhostapp.com/api/detail/?match_id=" + match_id;
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(my_url);
            if(entry != null){
                try {
                    String data = new String(entry.data, "UTF-8");
                    createLayout(new JSONObject(data));
                } catch (JSONException | UnsupportedEncodingException e) {
                    sendRequest(my_url);
                    e.printStackTrace();
                }
            }
            else{
                sendRequest(my_url);
            }
            isReceiving = true;
        }
    }

    public void createLayout(JSONObject response) {
        try {
            resultJson = response;
            radiant_win = resultJson.getInt("radiant_win");
            start_time = resultJson.getString("start_time");
            duration = resultJson.getString("duration");
            region = resultJson.getString("region");
            game_mode = resultJson.getString("game_mode");
            lobby_type = resultJson.getString("lobby_type");
            players = resultJson.getJSONArray("players");
        } catch (JSONException e) {
            e.printStackTrace();
            isNetworkError = true;
        }
        isReceivingData = true;
    }

    public void sendRequest(String req_url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                req_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        createLayout(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                isReceivingData = true;
                isNetworkError = true;
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void refreshFunction() {
        my_url = "http://dotaanalyser.000webhostapp.com/api/detail/?match_id=" + match_id;
        isReceiving = false;
        isReceivingData = false;
        isNetworkError = false;
        sendRequest(my_url);

        TabFragmentDetailOverview tabFragmentDetailOverview = (TabFragmentDetailOverview)getSupportFragmentManager().getFragments().get(0);
        TabFragmentDetailFarm tabFragmentDetailFarm = (TabFragmentDetailFarm)getSupportFragmentManager().getFragments().get(1);
        TabFragmentDetailDamage tabFragmentDetailDamage = (TabFragmentDetailDamage)getSupportFragmentManager().getFragments().get(2);
        TabFragmentDetailItems tabFragmentDetailItems = (TabFragmentDetailItems)getSupportFragmentManager().getFragments().get(3);
        TabFragmentDetailBuild tabFragmentDetailBuild = (TabFragmentDetailBuild)getSupportFragmentManager().getFragments().get(4);
        TabFragmentDetailGraphs tabFragmentDetailGraphs = (TabFragmentDetailGraphs)getSupportFragmentManager().getFragments().get(5);

        tabFragmentDetailOverview.setRefresh();
        tabFragmentDetailBuild.setRefresh();
        tabFragmentDetailDamage.setRefresh();
        tabFragmentDetailFarm.setRefresh();
        tabFragmentDetailGraphs.setRefresh();
        tabFragmentDetailItems.setRefresh();
    }
}
