package com.fearefull.dotaanalyser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

/**
 * Created by A.Hosseini on 2016-09-20.
 */
public class TabFragmentHomeHistory extends Fragment {

    String tag_json_obj = "json_obj_req";
    private static final String TAG = MainActivity.class.getSimpleName();
    DBHandler dbHandler;
    Bundle bundle;
    int scrollYScrollView;
    int maxScrollYLinearLayout;
    int fixHeightSizeScrollView;
    String myUrl;
    String limit = "0";
    String steamID;
    boolean isReceiving = true;
    boolean is_first_time = true;
    boolean is_mini_error_turn = false;
    boolean is_got_network_error = false;
    String heroNameString;
    private SwipeRefreshLayout swipeRefreshLayout;
    ScrollView targetScrollView;
    LinearLayout historyLinear;
    HomeActivity myHomeActivity;
    View my_history_view;
    ArrayList<String> match_id_array = new ArrayList<>();
    MenuItem refresh_item;
    LinearLayout mini_no_connection_layout;
    LinearLayout no_connection_layout;
    Button mini_no_connection_button;
    Button no_connection_button;
    LinearLayout expose_public_match_layout;
    Button expose_public_match_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myHomeActivity = (HomeActivity) getActivity();
        my_history_view =  inflater.inflate(R.layout.home_tab_history, container, false);
        refresh_item = myHomeActivity.menu.findItem(R.id.action_refresh);

        dbHandler = new DBHandler(myHomeActivity.getApplicationContext());

        mini_no_connection_layout = (LinearLayout) my_history_view.findViewById(R.id.mini_no_connection_layout);
        no_connection_layout = (LinearLayout) my_history_view.findViewById(R.id.no_connection_layout);
        mini_no_connection_button = (Button) my_history_view.findViewById(R.id.mini_no_connection_button);
        no_connection_button = (Button) my_history_view.findViewById(R.id.no_connection_button);
        expose_public_match_layout = (LinearLayout) my_history_view.findViewById(R.id.expose_public_match_layout);
        expose_public_match_button = (Button) my_history_view.findViewById(R.id.expose_public_match_button);

        mini_no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReceiving) {
                    isReceiving = false;
                }
                swipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReceiving) {
                    isReceiving = false;
                }
                expose_public_match_layout.setVisibility(View.GONE);
                no_connection_layout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(true);
                getData();
            }
        });

        expose_public_match_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(myHomeActivity, ExposePublicMatchActivity.class);
                startActivity(mIntent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) my_history_view.findViewById(R.id.swipe_refresh_layout);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorLose);

        targetScrollView = (ScrollView) my_history_view.findViewById(R.id.scrollView);
        historyLinear = (LinearLayout) my_history_view.findViewById(R.id.historyLinearLayout);

        targetScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onScrollChanged() {
                scrollYScrollView = targetScrollView.getScrollY();
                maxScrollYLinearLayout = historyLinear.getHeight();
                fixHeightSizeScrollView = targetScrollView.getHeight();
                if (maxScrollYLinearLayout - (scrollYScrollView + fixHeightSizeScrollView) < 500 && !isReceiving && !is_got_network_error) {
                    Log.w("scrollView", "now Show urself");
                    getData();
                    isReceiving = true;
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                historyLinear.removeViews(0, historyLinear.getChildCount());
                mini_no_connection_layout.setVisibility(View.GONE);
                limit = "0";
                is_mini_error_turn = false;
                if (isReceiving) {
                    isReceiving = false;
                }
                match_id_array.clear();
                getData();
            }
        });

        return my_history_view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (is_first_time) {
                steamID = myHomeActivity.steamID;
                isReceiving = false;
                is_first_time = false;

                myUrl = "http://dotaanalyser.000webhostapp.com/api/history/?steam_id=" + steamID + "&limit=" + limit;
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                        myUrl, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                createHistoryLayout(response);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());

                        Cache cache = AppController.getInstance().getRequestQueue().getCache();
                        Cache.Entry entry = cache.get(myUrl);
                        if(entry != null){
                            try {
                                String data = new String(entry.data, "UTF-8");
                                createHistoryLayout(new JSONObject(data));
                            } catch (JSONException | UnsupportedEncodingException e) {
                                setNetworkError();
                                e.printStackTrace();
                            }
                        }
                        else{
                            setNetworkError();
                        }
                    }
                });

                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
            }
            if(refresh_item != null && refresh_item.isVisible()) { refresh_item.setVisible(false); }
        }
    }

    private static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("mipmap/" + imageName, null, context.getPackageName());
    }

    private void openDetailMatch(View v) {
        TextView clickedLayoutMatch = (TextView) v.findViewById(R.id.matchIDHistory);
        Intent intent = new Intent(myHomeActivity, DetailMatchActivity.class);
        intent.putExtra("match_id", clickedLayoutMatch.getText());
        startActivity(intent);
    }

    private void getData() {
        if (!isReceiving) {
            myUrl = "http://dotaanalyser.000webhostapp.com/api/history/?steam_id=" + steamID + "&limit=" + limit;
            isReceiving = true;
            swipeRefreshLayout.setRefreshing(true);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    myUrl, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            mini_no_connection_layout.setVisibility(View.GONE);
                            isReceiving = false;
                            is_got_network_error = false;
                            createHistoryLayout(response);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    isReceiving = false;
                    is_got_network_error = true;
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    if (is_mini_error_turn) {
                        swipeRefreshLayout.setRefreshing(false);
                        mini_no_connection_layout.setVisibility(View.VISIBLE);
                        Toast.makeText(myHomeActivity, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    else {
                        setNetworkError();
                    }
                }
            });

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void createHistoryLayout(JSONObject result) {
        try {
            if (result.getInt("status") == 15) {
                setExposePublicMatchError();
            }
            else if(result.getInt("status") == 3) {
                setInProgressAnalysing();
            }
            else {
                JSONArray jsonArray = result.getJSONArray("result");

                LinearLayout historyLinearLayout = (LinearLayout)my_history_view.findViewById(R.id.historyLinearLayout);
                LayoutInflater inflater = LayoutInflater.from(myHomeActivity.getApplicationContext());

                for (int i = 0; i < jsonArray.length(); i++) {
                    if (! match_id_array.contains(jsonArray.getJSONObject(i).getString("match_id"))) {
                        View myHistoryLayout = inflater.inflate(R.layout.layout_history, historyLinearLayout, false);
                        heroNameString = jsonArray.getJSONObject(i).getString("hero_name");

                        ImageView heroImage = (ImageView) myHistoryLayout.findViewById(R.id.heroImageInflate);
                        TextView heroName = (TextView) myHistoryLayout.findViewById(R.id.heroNameInflate);
                        TextView durationMatch = (TextView) myHistoryLayout.findViewById(R.id.durationMatchInflate);
                        TextView typeMatch = (TextView) myHistoryLayout.findViewById(R.id.typeMatchInflate);
                        TextView dateMatch = (TextView) myHistoryLayout.findViewById(R.id.dateMatchInflate);
                        TextView resultMatch = (TextView) myHistoryLayout.findViewById(R.id.resultMatchInflate);
                        TextView KDAMatch = (TextView) myHistoryLayout.findViewById(R.id.KDAMatchInflate);
                        TextView matchIDMatch = (TextView) myHistoryLayout.findViewById(R.id.matchIDHistory);

                        ImageView item0Image = (ImageView) myHistoryLayout.findViewById(R.id.item0Inflate);
                        ImageView item1Image = (ImageView) myHistoryLayout.findViewById(R.id.item1Inflate);
                        ImageView item2Image = (ImageView) myHistoryLayout.findViewById(R.id.item2Inflate);
                        ImageView item3Image = (ImageView) myHistoryLayout.findViewById(R.id.item3Inflate);
                        ImageView item4Image = (ImageView) myHistoryLayout.findViewById(R.id.item4Inflate);
                        ImageView item5Image = (ImageView) myHistoryLayout.findViewById(R.id.item5Inflate);

                        heroImage.setImageResource(getImageId(myHomeActivity.getApplicationContext(),heroNameString));

                        heroNameString = heroNameString.replace("npc_dota_hero_", "");
                        heroNameString = heroNameString.replace("_", " ");
                        heroNameString = heroNameString.substring(0,1).toUpperCase() + heroNameString.substring(1).toLowerCase();

                        heroName.setText(heroNameString);
                        resultMatch.setText(jsonArray.getJSONObject(i).getString("result"));
                        if (Objects.equals(jsonArray.getJSONObject(i).getString("result"), "Win")) {
                            resultMatch.setTextColor(getResources().getColor(R.color.colorWin));
                        }
                        else if (Objects.equals(jsonArray.getJSONObject(i).getString("result"), "Lose")) {
                            resultMatch.setTextColor(getResources().getColor(R.color.colorLose));
                        }
                        else {
                            resultMatch.setTextColor(getResources().getColor(R.color.colorAbandoned));
                        }
                        durationMatch.setText(jsonArray.getJSONObject(i).getString("duration"));
                        dateMatch.setText(jsonArray.getJSONObject(i).getString("start_time"));
                        typeMatch.setText(jsonArray.getJSONObject(i).getString("game_mode"));
                        KDAMatch.setText(jsonArray.getJSONObject(i).getString("KDA"));
                        matchIDMatch.setText(jsonArray.getJSONObject(i).getString("match_id"));

                        if (jsonArray.getJSONObject(i).isNull("item_0")) {
                            item0Image.setVisibility(View.GONE);
                        }
                        else {
                            item0Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_0")));
                        }
                        if (jsonArray.getJSONObject(i).isNull("item_1")) {
                            item1Image.setVisibility(View.GONE);
                        }
                        else {
                            item1Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_1")));
                        }
                        if (jsonArray.getJSONObject(i).isNull("item_2")) {
                            item2Image.setVisibility(View.GONE);
                        }
                        else {
                            item2Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_2")));
                        }
                        if (jsonArray.getJSONObject(i).isNull("item_3")) {
                            item3Image.setVisibility(View.GONE);
                        }
                        else {
                            item3Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_3")));
                        }
                        if (jsonArray.getJSONObject(i).isNull("item_4")) {
                            item4Image.setVisibility(View.GONE);
                        }
                        else {
                            item4Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_4")));
                        }
                        if (jsonArray.getJSONObject(i).isNull("item_5")) {
                            item5Image.setVisibility(View.GONE);
                        }
                        else {
                            item5Image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),jsonArray.getJSONObject(i).getString("item_5")));
                        }

                        myHistoryLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openDetailMatch(v);
                            }
                        });
                        Animation slide_up = AnimationUtils.loadAnimation(myHomeActivity.getApplicationContext(),
                                R.anim.slide_up);

                        assert historyLinearLayout != null;
                        historyLinearLayout.addView(myHistoryLayout);
                        myHistoryLayout.startAnimation(slide_up);

                        match_id_array.add(jsonArray.getJSONObject(i).getString("match_id"));
                    }
                }

                limit = result.getString("next_limit");

                scrollYScrollView = targetScrollView.getScrollY();
                maxScrollYLinearLayout = historyLinear.getHeight();
                fixHeightSizeScrollView = targetScrollView.getHeight();
                isReceiving = false;
                if (maxScrollYLinearLayout < fixHeightSizeScrollView) {
                    getData();
                    isReceiving = true;
                }
                is_mini_error_turn = true;
            }
        } catch (JSONException e) {
            if (is_mini_error_turn) {
                mini_no_connection_layout.setVisibility(View.VISIBLE);
            }
            else {
                setNetworkError();
            }
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setNetworkError() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    public void setExposePublicMatchError() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.VISIBLE);
    }

    public void setInProgressAnalysing() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.VISIBLE);
        expose_public_match_button.setVisibility(View.GONE);
        TextView a = (TextView) expose_public_match_layout.findViewById(R.id.expose_public_match_text);
        a.setText("Analysing in progress, please be patient.");
        expose_public_match_layout.setVisibility(View.VISIBLE);
    }
}
