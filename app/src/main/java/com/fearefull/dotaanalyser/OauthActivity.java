package com.fearefull.dotaanalyser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;

public class OauthActivity extends AppCompatActivity {

    final String REALM_PARAM = "Dota Analyser";
    String tag_json_obj = "json_obj_req";
    public static Activity oauthActivity;
    private static final String TAG = OauthActivity.class.getSimpleName();
    WebView webView;
    DBHandler dbHandler;
    String myUrl;
    String steam_id;
    FrameLayout frame_layout_webView;
    ImageView loading_image;
    Animation loading_animation;
    LinearLayout no_connection_layout;
    Button no_connection_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        oauthActivity = this;
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
        frame_layout_webView = (FrameLayout) findViewById(R.id.frame_layout_webView);
        loading_image = (ImageView) findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate_loading);
        no_connection_layout = (LinearLayout) findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshFunction();
            }
        });

        webView = (WebView) findViewById(R.id.webView);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
                setNetworkError();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {

                //checks the url being loaded
                setTitle(url);
                Uri Url = Uri.parse(url);

                if(Url.getAuthority().equals(REALM_PARAM.toLowerCase())){
                    // That means that authentication is finished and the url contains user's id.
                    webView.stopLoading();

                    // Extracts user id.
                    frame_layout_webView.setVisibility(View.GONE);
                    no_connection_layout.setVisibility(View.GONE);
                    loading_image.startAnimation(loading_animation);
                    loading_image.setVisibility(View.VISIBLE);
                    Uri userAccountUrl = Uri.parse(Url.getQueryParameter("openid.identity"));
                    String userId = userAccountUrl.getLastPathSegment();
                    BigInteger int1 = new BigInteger(userId);
                    BigInteger int2 = new BigInteger("76561197960265728");
                    steam_id = int1.subtract(int2).toString();
                    myUrl = "http://dotaanalyser.000webhostapp.com/api/signup/?steam_id=" + steam_id + "&is_valid=1";
                    sendRequest(myUrl);
                }
            }
        });

        WebChromeClient webChromeClient = new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);

            }
        };

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.requestFocus(View.FOCUS_DOWN);

        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.setWebChromeClient(webChromeClient);
        String my_url = "https://steamcommunity.com/openid/login?" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.mode=checkid_setup&" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.realm=https://" + REALM_PARAM + "&" +
                "openid.return_to=https://" + REALM_PARAM + "/signin/";

        webView.loadUrl(my_url);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                        User newUser;
                        try {
                            newUser = new User(1, response.getString("steam_id"), response.getString("person_name"), response.getString("real_name"), response.getString("avatar"), "0");
                            if (dbHandler.getCountUser() == 0) {
                                dbHandler.createUser(newUser);
                            }
                            else {
                                dbHandler.updateUser(newUser);
                            }
                            Toast.makeText(OauthActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OauthActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setNetworkError();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                setNetworkError();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void setNetworkError() {
        frame_layout_webView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    public void refreshFunction() {
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        frame_layout_webView.setVisibility(View.VISIBLE);
        String my_url = "https://steamcommunity.com/openid/login?" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.mode=checkid_setup&" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.realm=https://" + REALM_PARAM + "&" +
                "openid.return_to=https://" + REALM_PARAM + "/signin/";

        webView.loadUrl(my_url);
    }
}
