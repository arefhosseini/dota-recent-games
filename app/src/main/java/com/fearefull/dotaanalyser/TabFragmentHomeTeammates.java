package com.fearefull.dotaanalyser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

/**
 * Created by A.Hosseini on 2016-09-20.
 */
public class TabFragmentHomeTeammates extends Fragment {
    public static final String TAG = AppController.class.getSimpleName();
    String tag_json_obj = "json_obj_req";
    HomeActivity myHomeActivity;
    View my_view;
    LayoutInflater inflater;
    boolean isSetData = false;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    MenuItem refresh_item;
    LinearLayout no_connection_layout;
    Button no_connection_button;
    String myUrl;
    LinearLayout expose_public_match_layout;
    Button expose_public_match_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        my_view =  inflater.inflate(R.layout.home_tab_teammates, container, false);

        this.inflater = inflater;
        myHomeActivity = (HomeActivity) getActivity();
        refresh_item = myHomeActivity.menu.findItem(R.id.action_refresh);

        no_connection_layout = (LinearLayout) my_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHomeActivity.refreshFunction();
            }
        });

        expose_public_match_layout = (LinearLayout) my_view.findViewById(R.id.expose_public_match_layout);
        expose_public_match_button = (Button) my_view.findViewById(R.id.expose_public_match_button);
        expose_public_match_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent=new Intent(myHomeActivity, ExposePublicMatchActivity.class);
                startActivity(mIntent);
            }
        });

        scrollView = (ScrollView) my_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myHomeActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        setHandlerData();

        return my_view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && refresh_item != null) {
            if(! refresh_item.isVisible()) { refresh_item.setVisible(true); }
        }
    }

    public void setRefresh() {
        myUrl = "http://dotaanalyser.000webhostapp.com/api/teammates/?steam_id=" + myHomeActivity.steamID;
        LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
        teammatesLayout.removeViews(0, teammatesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        loading_image.startAnimation(loading_animation);
        loading_image.setVisibility(View.VISIBLE);
        isSetData = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendRequest(myUrl, true);
            }
        }, 500);
    }

    public void setRebuild() {
        myUrl = "http://dotaanalyser.000webhostapp.com/api/teammates/?steam_id=" + myHomeActivity.steamID;
        isSetData = false;
        sendRequest(myUrl, false);
    }

    public void setHandlerData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myHomeActivity.isReceivingSteamID) {
                    if (! isSetData) {
                        myUrl = "http://dotaanalyser.000webhostapp.com/api/teammates/?steam_id=" + myHomeActivity.steamID;
                        Cache cache = AppController.getInstance().getRequestQueue().getCache();
                        Cache.Entry entry = cache.get(myUrl);
                        if(entry != null){
                            try {
                                String data = new String(entry.data, "UTF-8");
                                JSONObject myJsonObject = new JSONObject(data);
                                createLayout(myJsonObject, true);

                                Long tsLong = System.currentTimeMillis()/1000;
                                Long tsPastLong = myJsonObject.getLong("update_time");
                                if (tsLong - tsPastLong > 18000) {
                                    setRebuild();
                                }

                            } catch (JSONException | UnsupportedEncodingException e) {
                                sendRequest(myUrl, true);
                            }
                        }
                        else{
                            sendRequest(myUrl, true);
                        }
                        isSetData = true;
                    }
                }
                else {
                    setHandlerData();
                }
            }
        }, 200);
    }

    public void setNetworkError() {
        LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
        teammatesLayout.removeViews(0, teammatesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setExposePublicMatchError() {
        LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
        teammatesLayout.removeViews(0, teammatesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setInProgressAnalysing() {
        LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
        teammatesLayout.removeViews(0, teammatesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_button.setVisibility(View.GONE);
        TextView a = (TextView) expose_public_match_layout.findViewById(R.id.expose_public_match_text);
        a.setText("Analysing in progress, please be patient.");
        expose_public_match_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void createLayout(JSONObject response, boolean is_create) {
        try {
            if (!is_create) {
                LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
                teammatesLayout.removeViews(0, teammatesLayout.getChildCount());
                scrollView.setVisibility(View.GONE);
                no_connection_layout.setVisibility(View.GONE);
                expose_public_match_layout.setVisibility(View.GONE);
                loading_image.startAnimation(loading_animation);
                loading_image.setVisibility(View.VISIBLE);
            }
            if (response.getInt("status") == 15) {
                setExposePublicMatchError();
            }
            else if(response.getInt("status") == 3) {
                setInProgressAnalysing();
            }
            else {
                loading_image.clearAnimation();
                loading_image.setVisibility(View.GONE);
                expose_public_match_layout.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                JSONArray jsonArray;

                jsonArray = response.getJSONArray("teammates");
                LinearLayout teammatesLayout = (LinearLayout) my_view.findViewById(R.id.teammatesLayout);
                for (int i = 0; i < jsonArray.length(); i++) {
                    View teammates_row = inflater.inflate(R.layout.layout_teammates, teammatesLayout, false);

                    ImageView teammates_user_image = (ImageView) teammates_row.findViewById(R.id.teammates_user_image);
                    TextView teammates_person_name = (TextView) teammates_row.findViewById(R.id.teammates_person_name);
                    TextView teammates_matches = (TextView) teammates_row.findViewById(R.id.teammates_matches);
                    TextView teammates_win_rate = (TextView) teammates_row.findViewById(R.id.teammates_win_rate);
                    TextView teammates_last_match = (TextView) teammates_row.findViewById(R.id.teammates_last_match);
                    TextView teammates_steam_id = (TextView) teammates_row.findViewById(R.id.teammates_steam_id);
                    TextView teammates_image_path = (TextView) teammates_row.findViewById(R.id.teammates_image_path);

                    Picasso.with(myHomeActivity.getApplicationContext()).load(jsonArray.getJSONObject(i).getString("avatar")).into(teammates_user_image);

                    teammates_person_name.setText(jsonArray.getJSONObject(i).getString("person_name"));
                    teammates_matches.setText(jsonArray.getJSONObject(i).getString("matches"));
                    teammates_win_rate.setText(jsonArray.getJSONObject(i).getString("win_rate"));
                    teammates_last_match.setText(jsonArray.getJSONObject(i).getString("last_match_date"));
                    teammates_steam_id.setText(jsonArray.getJSONObject(i).getString("steam_id"));
                    teammates_image_path.setText(jsonArray.getJSONObject(i).getString("avatar"));

                    teammates_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFriendActivity(v);
                        }
                    });

                    teammatesLayout.addView(teammates_row);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (is_create) {
                setNetworkError();
            }
        }
    }

    public void sendRequest(String req_url, final boolean is_create) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                req_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        createLayout(response, is_create);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (is_create) {
                    setNetworkError();
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void openFriendActivity(View v) {

        TextView clickedLayoutMatch = (TextView) v.findViewById(R.id.teammates_steam_id);
        TextView personNameFriend = (TextView) v.findViewById(R.id.teammates_person_name);
        TextView imagePathFriend = (TextView) v.findViewById(R.id.teammates_image_path);
        Intent intent = new Intent(myHomeActivity, FriendHomeActivity.class);
        intent.putExtra("steam_id", clickedLayoutMatch.getText());
        intent.putExtra("person_name", personNameFriend.getText());
        intent.putExtra("state", "Offline");
        intent.putExtra("image", imagePathFriend.getText());

        startActivity(intent);
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            View sharedView = v.findViewById(R.id.friendImageInflate);
            String transitionName = getString(R.string.friend_image);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(FriendsActivity.this, sharedView, transitionName);
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }*/
    }
}
