package com.fearefull.dotaanalyser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by A.Hosseini on 2016-10-13.
 */

public class CustomAboutUs extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    LinearLayout about_us_email_layout;

    public CustomAboutUs(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_us_dialog);

        about_us_email_layout = (LinearLayout) findViewById(R.id.about_us_email_layout);
        about_us_email_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.about_us_email_layout) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"arefhosseini@yahoo.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Dota Analyser Feedback");
            i.putExtra(Intent.EXTRA_TEXT   , "");
            try {
                c.startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(c, "There are no email application installed.", Toast.LENGTH_SHORT).show();
            }
        }
        dismiss();
    }
}
