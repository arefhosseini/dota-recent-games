package com.fearefull.dotaanalyser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    DBHandler dbHandler;
    User loggedInUser;
    public static Activity splashActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        final ImageView iv = (ImageView) findViewById(R.id.splashImageView);
        final TextView tv = (TextView) findViewById(R.id.splashTextView);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move_down);
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);
        splashActivity = this;
        assert iv != null;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
                iv.setVisibility(View.VISIBLE);
                iv.startAnimation(an);
            }
        }, 2000);
        mediaPlayer = MediaPlayer.create(this, R.raw.splash_song);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv.setVisibility(View.VISIBLE);
                tv.startAnimation(in);
                new Handler().postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        mediaPlayer.stop();
                        dbHandler = new DBHandler(getApplicationContext());
                        if (dbHandler.getCountUser() > 0) {
                            Log.w("saveDB", "db is not empty");
                            loggedInUser = dbHandler.getUser(1);
                            if (Objects.equals(loggedInUser.getIsLogout(), "0")) {
                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                LinearLayout login_splash_screen = (LinearLayout) findViewById(R.id.login_splash_screen);
                                login_splash_screen.setVisibility(View.VISIBLE);
                                login_splash_screen.startAnimation(in);
                            }
                        }
                        else {
                            LinearLayout login_splash_screen = (LinearLayout) findViewById(R.id.login_splash_screen);
                            login_splash_screen.setVisibility(View.VISIBLE);
                            login_splash_screen.startAnimation(in);
                        }
                    }
                }, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void signInClick(View v){

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Intent mIntent = new Intent(SplashActivity.this, OauthActivity.class);
        startActivity(mIntent);
    }
}
