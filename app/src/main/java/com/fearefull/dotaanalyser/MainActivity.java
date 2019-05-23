package com.fearefull.dotaanalyser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static Activity mainActivity;
    DBHandler dbHandler;
    User loggedInUser;
    boolean isReceiving = false;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new DBHandler(getApplicationContext());
        if (dbHandler.getCountUser() > 0) {
            Log.w("saveDB", "db is not empty");

            loggedInUser = dbHandler.getUser(1);
            if (Objects.equals(loggedInUser.getIsLogout(), "0")) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else {
            Log.w("saveDB", "db is empty");
        }

        setContentView(R.layout.activity_main);
        mainActivity = this;
    }

    public void signInClick(View v){

        if (! isReceiving) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            Intent mIntent = new Intent(MainActivity.this, OauthActivity.class);
            startActivity(mIntent);
        }
    }

}
