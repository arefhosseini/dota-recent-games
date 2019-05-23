package com.fearefull.dotaanalyser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    String tag_json_obj = "json_obj_req";
    private static final String TAG = MainActivity.class.getSimpleName();
    DBHandler dbHandler;
    User loggedInUser;
    String steamID;
    String myUrl;
    public static Activity friendsActivity;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout no_connection_layout;
    Button no_connection_button;
    JSONObject recreateJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsActivity = this;
        setContentView(R.layout.activity_friends);
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

        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Typeface DancingScript = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.otf");
        mTitle.setTypeface(DancingScript);

        dbHandler = new DBHandler(getApplicationContext());

        no_connection_layout = (LinearLayout) findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFunction();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        assert swipeRefreshLayout != null;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorLose);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFunction();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        swipeRefreshLayout.setRefreshing(true);
        loggedInUser = dbHandler.getUser(1);
        steamID = loggedInUser.getSteamID();

        myUrl = "http://dotaanalyser.000webhostapp.com/api/friend/?steam_id=" + steamID;
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(myUrl);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                JSONObject myJsonObject = new JSONObject(data);
                Long tsLong = System.currentTimeMillis()/1000;
                Long tsPastLong = myJsonObject.getLong("update_time");
                if (tsLong - tsPastLong > 1800) {
                    sendRequest(myUrl);
                }
                else {
                    recreateJson = myJsonObject;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            createFriendLayout(recreateJson);
                        }
                    }, 1500);
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                sendRequest(myUrl);
                e.printStackTrace();
            }
        }
        else{
            sendRequest(myUrl);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendRequest(String req_url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                req_url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        createFriendLayout(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                setNetworkError();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void openFriendActivity(View v) {
        TextView clickedLayoutMatch = (TextView) v.findViewById(R.id.friendSteamIDInflate);
        TextView personNameFriend = (TextView) v.findViewById(R.id.friendUsernameInflate);
        TextView stateFriend = (TextView) v.findViewById(R.id.friendStateInflate);
        TextView imagePathFriend = (TextView) v.findViewById(R.id.friendImagePathInflate);
        Intent intent = new Intent(FriendsActivity.this, FriendHomeActivity.class);
        intent.putExtra("steam_id", clickedLayoutMatch.getText());
        intent.putExtra("person_name", personNameFriend.getText());
        intent.putExtra("state", stateFriend.getText());
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

    public void notAllowedToast(View v) {
        Toast.makeText(FriendsActivity.this, "This user is private", Toast.LENGTH_SHORT).show();
    }

    public void createFriendLayout(JSONObject result) {
        try {

            JSONArray inGameFriendArray = result.getJSONArray("in_game");
            JSONArray onlineFriendArray = result.getJSONArray("online");
            JSONArray awayFriendArray = result.getJSONArray("away");
            JSONArray offlineFriendArray = result.getJSONArray("offline");

            LinearLayout friendInGameLayout = (LinearLayout) findViewById(R.id.friendInGameLayout);
            LinearLayout friendOnlineLayout = (LinearLayout) findViewById(R.id.friendOnlineLayout);
            LinearLayout friendAwayLayout = (LinearLayout) findViewById(R.id.friendAwayLayout);
            LinearLayout friendOfflineLayout = (LinearLayout) findViewById(R.id.friendOfflineLayout);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            if (!(inGameFriendArray.length() > 0)) {
                assert friendInGameLayout != null;
                friendInGameLayout.setVisibility(View.GONE);
            }
            else {
                assert friendInGameLayout != null;
                friendInGameLayout.setVisibility(View.VISIBLE);
            }

            if (!(onlineFriendArray.length() > 0)) {
                assert friendOnlineLayout != null;
                friendOnlineLayout.setVisibility(View.GONE);
            }
            else {
                assert friendOnlineLayout  != null;
                friendOnlineLayout .setVisibility(View.VISIBLE);
            }

            if (!(awayFriendArray.length() > 0)) {
                assert friendAwayLayout != null;
                friendAwayLayout.setVisibility(View.GONE);
            }
            else {
                assert friendAwayLayout != null;
                friendAwayLayout.setVisibility(View.VISIBLE);
            }

            if (!(offlineFriendArray.length() > 0)) {
                assert friendOfflineLayout != null;
                friendOfflineLayout.setVisibility(View.GONE);
            }
            else {
                assert friendOfflineLayout != null;
                friendOfflineLayout.setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < inGameFriendArray.length(); i++) {

                View myFriendLayout = inflater.inflate(R.layout.layout_friend, friendInGameLayout, false);
                TextView friendSteamID = (TextView) myFriendLayout.findViewById(R.id.friendSteamIDInflate);
                TextView friendUsername = (TextView) myFriendLayout.findViewById(R.id.friendUsernameInflate);
                TextView friendState = (TextView) myFriendLayout.findViewById(R.id.friendStateInflate);
                TextView friendImagePath = (TextView) myFriendLayout.findViewById(R.id.friendImagePathInflate);
                CircleImageView friendImage = (CircleImageView) myFriendLayout.findViewById(R.id.friendImageInflate);
                ImageView lock_image = (ImageView) myFriendLayout.findViewById(R.id.lock_image);


                friendSteamID.setText(inGameFriendArray.getJSONObject(i).getString("steam_id"));
                friendUsername.setText(inGameFriendArray.getJSONObject(i).getString("person_name"));
                friendState.setText(inGameFriendArray.getJSONObject(i).getString("player_state"));
                friendImagePath.setText(inGameFriendArray.getJSONObject(i).getString("avatar"));
                friendImage.setBorderColor(getResources().getColor(R.color.inGameFriend));
                friendState.setTextColor(getResources().getColor(R.color.inGameFriend));
                friendUsername.setTextColor(getResources().getColor(R.color.inGameFriend));

                Picasso.with(getApplicationContext()).load(inGameFriendArray.getJSONObject(i).getString("avatar")).into(friendImage);

                if (inGameFriendArray.getJSONObject(i).getInt("is_allowed") == 1) {
                    lock_image.setVisibility(View.GONE);

                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFriendActivity(v);
                        }
                    });
                }
                else {
                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notAllowedToast(v);
                        }
                    });
                }

                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);

                friendInGameLayout.addView(myFriendLayout);
                myFriendLayout.startAnimation(slide_up);
            }

            for (int i = 0; i < onlineFriendArray.length(); i++) {

                View myFriendLayout = inflater.inflate(R.layout.layout_friend, friendOnlineLayout, false);
                TextView friendSteamID = (TextView) myFriendLayout.findViewById(R.id.friendSteamIDInflate);
                TextView friendUsername = (TextView) myFriendLayout.findViewById(R.id.friendUsernameInflate);
                TextView friendState = (TextView) myFriendLayout.findViewById(R.id.friendStateInflate);
                TextView friendImagePath = (TextView) myFriendLayout.findViewById(R.id.friendImagePathInflate);
                CircleImageView friendImage = (CircleImageView) myFriendLayout.findViewById(R.id.friendImageInflate);
                ImageView lock_image = (ImageView) myFriendLayout.findViewById(R.id.lock_image);

                friendSteamID.setText(onlineFriendArray.getJSONObject(i).getString("steam_id"));
                friendUsername.setText(onlineFriendArray.getJSONObject(i).getString("person_name"));
                friendState.setText(onlineFriendArray.getJSONObject(i).getString("player_state"));
                friendImagePath.setText(onlineFriendArray.getJSONObject(i).getString("avatar"));
                friendImage.setBorderColor(getResources().getColor(R.color.onlineFriend));
                friendState.setTextColor(getResources().getColor(R.color.onlineFriend));
                friendUsername.setTextColor(getResources().getColor(R.color.onlineFriend));

                if (onlineFriendArray.getJSONObject(i).getInt("is_allowed") == 1) {
                    lock_image.setVisibility(View.GONE);

                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFriendActivity(v);
                        }
                    });
                }
                else {
                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notAllowedToast(v);
                        }
                    });
                }

                Picasso.with(getApplicationContext()).load(onlineFriendArray.getJSONObject(i).getString("avatar")).into(friendImage);

                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);

                friendOnlineLayout.addView(myFriendLayout);
                myFriendLayout.startAnimation(slide_up);
            }

            for (int i = 0; i < awayFriendArray.length(); i++) {
                View myFriendLayout = inflater.inflate(R.layout.layout_friend, friendAwayLayout, false);
                TextView friendSteamID = (TextView) myFriendLayout.findViewById(R.id.friendSteamIDInflate);
                TextView friendUsername = (TextView) myFriendLayout.findViewById(R.id.friendUsernameInflate);
                TextView friendState = (TextView) myFriendLayout.findViewById(R.id.friendStateInflate);
                TextView friendImagePath = (TextView) myFriendLayout.findViewById(R.id.friendImagePathInflate);
                CircleImageView friendImage = (CircleImageView) myFriendLayout.findViewById(R.id.friendImageInflate);
                ImageView lock_image = (ImageView) myFriendLayout.findViewById(R.id.lock_image);

                friendSteamID.setText(awayFriendArray.getJSONObject(i).getString("steam_id"));
                friendUsername.setText(awayFriendArray.getJSONObject(i).getString("person_name"));
                friendState.setText(awayFriendArray.getJSONObject(i).getString("player_state"));
                friendImagePath.setText(awayFriendArray.getJSONObject(i).getString("avatar"));
                friendImage.setBorderColor(getResources().getColor(R.color.awayFriend));
                friendState.setTextColor(getResources().getColor(R.color.awayFriend));
                friendUsername.setTextColor(getResources().getColor(R.color.awayFriend));

                Picasso.with(getApplicationContext()).load(awayFriendArray.getJSONObject(i).getString("avatar")).into(friendImage);

                if (awayFriendArray.getJSONObject(i).getInt("is_allowed") == 1) {
                    lock_image.setVisibility(View.GONE);

                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFriendActivity(v);
                        }
                    });
                }
                else {
                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notAllowedToast(v);
                        }
                    });
                }

                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);

                friendAwayLayout.addView(myFriendLayout);
                myFriendLayout.startAnimation(slide_up);
            }

            for (int i = 0; i < offlineFriendArray.length(); i++) {

                View myFriendLayout = inflater.inflate(R.layout.layout_friend, friendOfflineLayout, false);
                TextView friendSteamID = (TextView) myFriendLayout.findViewById(R.id.friendSteamIDInflate);
                TextView friendUsername = (TextView) myFriendLayout.findViewById(R.id.friendUsernameInflate);
                TextView friendState = (TextView) myFriendLayout.findViewById(R.id.friendStateInflate);
                TextView friendImagePath = (TextView) myFriendLayout.findViewById(R.id.friendImagePathInflate);
                CircleImageView friendImage = (CircleImageView) myFriendLayout.findViewById(R.id.friendImageInflate);
                ImageView lock_image = (ImageView) myFriendLayout.findViewById(R.id.lock_image);


                friendSteamID.setText(offlineFriendArray.getJSONObject(i).getString("steam_id"));
                friendUsername.setText(offlineFriendArray.getJSONObject(i).getString("person_name"));
                friendState.setText(offlineFriendArray.getJSONObject(i).getString("player_state"));
                friendImagePath.setText(offlineFriendArray.getJSONObject(i).getString("avatar"));
                friendImage.setBorderColor(getResources().getColor(R.color.offlineFriend));
                friendUsername.setTextColor(getResources().getColor(R.color.offlineFriend));
                friendState.setTextColor(getResources().getColor(R.color.offlineFriend));

                Picasso.with(getApplicationContext()).load(offlineFriendArray.getJSONObject(i).getString("avatar")).into(friendImage);

                if (offlineFriendArray.getJSONObject(i).getInt("is_allowed") == 1) {
                    lock_image.setVisibility(View.GONE);

                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFriendActivity(v);
                        }
                    });
                }
                else {
                    myFriendLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notAllowedToast(v);
                        }
                    });
                }

                Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_up);

                friendOfflineLayout.addView(myFriendLayout);
                myFriendLayout.startAnimation(slide_up);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            setNetworkError();
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    public void setNetworkError() {
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    public void refreshFunction() {
        no_connection_layout.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        loggedInUser = dbHandler.getUser(1);
        steamID = loggedInUser.getSteamID();
        myUrl = "http://dotaanalyser.000webhostapp.com/api/friend/?steam_id=" + steamID;
        swipeRefreshLayout.setRefreshing(true);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                myUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        createFriendLayout(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                setNetworkError();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        LinearLayout friendInGameLayout = (LinearLayout) findViewById(R.id.friendInGameLayout);
        LinearLayout friendOnlineLayout = (LinearLayout) findViewById(R.id.friendOnlineLayout);
        LinearLayout friendAwayLayout = (LinearLayout) findViewById(R.id.friendAwayLayout);
        LinearLayout friendOfflineLayout = (LinearLayout) findViewById(R.id.friendOfflineLayout);

        assert friendInGameLayout != null;
        friendInGameLayout.removeViews(1, friendInGameLayout.getChildCount() -1);
        friendInGameLayout.setVisibility(View.GONE);

        assert friendOnlineLayout != null;
        friendOnlineLayout.removeViews(1, friendOnlineLayout.getChildCount() -1);
        friendOnlineLayout.setVisibility(View.GONE);

        assert friendAwayLayout != null;
        friendAwayLayout.removeViews(1, friendAwayLayout.getChildCount() -1);
        friendAwayLayout.setVisibility(View.GONE);

        assert friendOfflineLayout != null;
        friendOfflineLayout.removeViews(1, friendOfflineLayout.getChildCount() -1);
        friendOfflineLayout.setVisibility(View.GONE);
    }
}
