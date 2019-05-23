package com.fearefull.dotaanalyser;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class LoadingActivity extends Activity {

    public static Activity loadingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingActivity = this;
        setContentView(R.layout.activity_loading);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout( dpToPx(150),dpToPx(150));

        final ImageView iv = (ImageView) findViewById(R.id.loadingImageView);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate_loading);

        assert iv != null;
        iv.startAnimation(an);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
