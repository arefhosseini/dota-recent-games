package com.fearefull.dotaanalyser;

import android.content.Context;
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

import org.json.JSONException;

/**
 * Created by A.Hosseini on 2016-09-20.
 */
public class TabFragmentHomeRecords extends Fragment {

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
    LinearLayout expose_public_match_layout;
    Button expose_public_match_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        my_view =  inflater.inflate(R.layout.home_tab_records, container, false);

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
        LinearLayout recordsLayout = (LinearLayout) my_view.findViewById(R.id.recordsLayout);
        recordsLayout.removeViews(0, recordsLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        loading_image.startAnimation(loading_animation);
        loading_image.setVisibility(View.VISIBLE);
        isSetData = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setHandlerData();
            }
        }, 500);
    }

    public void setData() throws JSONException {
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        isSetData = true;

        LinearLayout recordsLayout = (LinearLayout) my_view.findViewById(R.id.recordsLayout);
        for (int i = 0; i < myHomeActivity.jsonArrayRecords.length(); i++) {
            View records_row = inflater.inflate(R.layout.layout_records, recordsLayout, false);

            ImageView records_hero_image = (ImageView) records_row.findViewById(R.id.records_hero_image);
            TextView records_hero_name = (TextView) records_row.findViewById(R.id.records_hero_name);
            TextView records_date = (TextView) records_row.findViewById(R.id.records_date);
            TextView records_record_name = (TextView) records_row.findViewById(R.id.records_record_name);
            TextView records_record_value = (TextView) records_row.findViewById(R.id.records_record_value);
            TextView records_result = (TextView) records_row.findViewById(R.id.records_result);
            TextView records_match_id = (TextView) records_row.findViewById(R.id.records_match_id);

            String heroNameString = myHomeActivity.jsonArrayRecords.getJSONObject(i).getString("hero_name");

            records_hero_image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),heroNameString));

            heroNameString = heroNameString.replace("npc_dota_hero_", "");
            heroNameString = heroNameString.replace("_", " ");
            heroNameString = heroNameString.substring(0,1).toUpperCase() + heroNameString.substring(1).toLowerCase();

            records_hero_name.setText(heroNameString);
            records_date.setText(myHomeActivity.jsonArrayRecords.getJSONObject(i).getString("date"));
            records_record_name.setText(myHomeActivity.jsonArrayRecords.getJSONObject(i).getString("name"));
            records_record_value.setText(myHomeActivity.jsonArrayRecords.getJSONObject(i).getString("value"));
            records_match_id.setText(myHomeActivity.jsonArrayRecords.getJSONObject(i).getString("match_id"));

            if (myHomeActivity.jsonArrayRecords.getJSONObject(i).getInt("is_win") == 1) {
                records_result.setText("Win");
                records_result.setTextColor(getResources().getColor(R.color.colorWin));
            }
            else {
                records_result.setText("Lose");
                records_result.setTextColor(getResources().getColor(R.color.colorLose));
            }

            records_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetailMatch(v);
                }
            });

            recordsLayout.addView(records_row);
        }
    }

    public void setNetworkError() {
        LinearLayout recordsLayout = (LinearLayout) my_view.findViewById(R.id.recordsLayout);
        recordsLayout.removeViews(0, recordsLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setExposePublicMatchError() {
        LinearLayout recordsLayout = (LinearLayout) my_view.findViewById(R.id.recordsLayout);
        recordsLayout.removeViews(0, recordsLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setInProgressAnalysing() {
        LinearLayout recordsLayout = (LinearLayout) my_view.findViewById(R.id.recordsLayout);
        recordsLayout.removeViews(0, recordsLayout.getChildCount());
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

    public void setHandlerData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myHomeActivity.isReceivingData) {
                    if (myHomeActivity.isNetworkError) { setNetworkError(); }
                    else if (myHomeActivity.isExposePublicMatchError) { setExposePublicMatchError(); }
                    else if (myHomeActivity.isInProgressAnalysing) { setInProgressAnalysing(); }
                    else {
                        if (! isSetData) {
                            try {
                                setData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setNetworkError();
                            }
                        }
                    }
                }
                else {
                    setHandlerData();
                }
            }
        }, 200);
    }

    public void openDetailMatch(View v) {
        TextView clickedLayoutMatch = (TextView) v.findViewById(R.id.records_match_id);
        Intent intent = new Intent(myHomeActivity, DetailMatchActivity.class);
        intent.putExtra("match_id", clickedLayoutMatch.getText());
        startActivity(intent);
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("mipmap/" + imageName, null, context.getPackageName());
    }
}
