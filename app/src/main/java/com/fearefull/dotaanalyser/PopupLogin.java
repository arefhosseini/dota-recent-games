package com.fearefull.dotaanalyser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by A.Hosseini on 2016-08-05.
 */
public class PopupLogin extends Activity {

    DBHandler dbHandler;
    public static Activity popupLogin;
    Bundle bundle;
    String profileString;
    JSONObject profileJson;
    String myUrl;
    User newUser;
    boolean isReceiving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        popupLogin = this;
        setContentView(R.layout.popup_login_window);

        bundle = getIntent().getExtras();
        dbHandler = new DBHandler(getApplicationContext());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout( dpToPx(400),dpToPx(300));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    profileString = extras.getString("data");
                }

                try {
                    profileJson = new JSONObject(profileString);

                    TextView personName = (TextView) findViewById(R.id.personNamePopupLogin);
                    TextView realName = (TextView) findViewById(R.id.realNamePopupLogin);

                    personName.setText(profileJson.getString("person_name"));
                    realName.setText(profileJson.getString("real_name"));

                    Picasso.with(getApplicationContext()).load(profileJson.getString("avatar")).into((ImageView) findViewById(R.id.avatarPopupLogin));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 10);
    }

    public void yesPopupClick(View V) throws JSONException {
        if (!isReceiving) {
            myUrl = "http://dotaanalyser.000webhostapp.com/api/signup/?steam_id=" + profileJson.getString("steam_id") + "&is_valid=1";
            executeAsyncTask(new RequestTask(), myUrl);
            isReceiving = true;
            Log.w("request", profileJson.getString("steam_id"));
        }
    }

    public void noPopupClick (View v) {
        PopupLogin.this.finish();
    }

    public void saveUser(String result) throws JSONException {

        JSONObject newProfileJson = new JSONObject(result);
        Log.w("saveDB", "starting");
        newUser = new User(1, newProfileJson.getString("steam_id"), newProfileJson.getString("person_name"), newProfileJson.getString("real_name"), newProfileJson.getString("avatar"), "0");
        Log.w("saveDB", "starting2");
        if (dbHandler.getCountUser() == 0) {
            dbHandler.createUser(newUser);
            Log.w("saveDB", "create new user");
        }
        else {
            Log.w("saveDB", "starting3");
            Log.w("saveDB", "update new user " + dbHandler.updateUser(newUser));
        }

        Intent intent = new Intent(PopupLogin.this, HomeActivity.class);
        startActivity(intent);
    }

    private class RequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //do your request in here so that you don't interrupt the UI thread
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            //Here you are done with the task
            try {
                saveUser(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isReceiving = false;
        }
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 1000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.w("response", String.valueOf(response));
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return convertInputStreamToString(is, length);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {

        BufferedReader r = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString().trim();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }
}


