package com.fearefull.dotaanalyser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by A.Hosseini on 2016-09-06.
 */
public class TabFragmentHomeOverview extends Fragment {
    HomeActivity myHomeActivity;
    View my_view;
    LayoutInflater inflater;
    boolean isSetData = false;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    MenuItem refresh_item;
    RadarChart playerStyleChart;
    LinearLayout no_connection_layout;
    Button no_connection_button;
    LinearLayout expose_public_match_layout;
    Button expose_public_match_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        my_view =  inflater.inflate(R.layout.home_tab_overview, container, false);

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

        playerStyleChart = (RadarChart) my_view.findViewById(R.id.playerStyleChart);

        playerStyleChart.setDescription("");

        playerStyleChart.setWebLineWidth(1f);
        playerStyleChart.setWebColor(Color.parseColor("#9f9f9f"));
        playerStyleChart.setWebLineWidthInner(1f);
        playerStyleChart.setWebColorInner(Color.parseColor("#9f9f9f"));
        playerStyleChart.setWebAlpha(100);

        XAxis xAxis = playerStyleChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);

        YAxis yAxis = playerStyleChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMaxValue(100);
        yAxis.setAxisMinValue(0);
        yAxis.setDrawLabels(false);

        playerStyleChart.getLegend().setEnabled(false);

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
        LinearLayout mostPlayedHeroesLayout = (LinearLayout) my_view.findViewById(R.id.mostPlayedHeroesLayout);
        mostPlayedHeroesLayout.removeViews(0, mostPlayedHeroesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.GONE);
        loading_image.startAnimation(loading_animation);
        loading_image.setVisibility(View.VISIBLE);
        playerStyleChart.clear();
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

        TextView total_last_match_date = (TextView) my_view.findViewById(R.id.total_last_match_date);
        TextView total_matches = (TextView) my_view.findViewById(R.id.total_matches);
        TextView total_win_rate = (TextView) my_view.findViewById(R.id.total_win_rate);
        TextView total_kda = (TextView) my_view.findViewById(R.id.total_kda);

        total_matches.setText(myHomeActivity.jsonResult.getString("total_matches"));
        total_last_match_date.setText(myHomeActivity.jsonResult.getJSONObject("last_match").getString("date"));
        total_win_rate.setText(myHomeActivity.jsonResult.getString("win_rate"));
        total_kda.setText(myHomeActivity.jsonResult.getString("total_kda"));

        LinearLayout mostPlayedHeroesLayout = (LinearLayout) my_view.findViewById(R.id.mostPlayedHeroesLayout);
        for (int i = 0; i < myHomeActivity.jsonArray.length(); i++) {
            View most_played_row = inflater.inflate(R.layout.layout_most_played_hero, mostPlayedHeroesLayout, false);

            ImageView most_played_hero_image = (ImageView) most_played_row.findViewById(R.id.most_played_hero_image);
            TextView most_played_hero_name = (TextView) most_played_row.findViewById(R.id.most_played_hero_name);
            TextView most_played_last_match = (TextView) most_played_row.findViewById(R.id.most_played_last_match);
            TextView most_played_total_matches = (TextView) most_played_row.findViewById(R.id.most_played_total_matches);
            TextView most_played_win_rate = (TextView) most_played_row.findViewById(R.id.most_played_win_rate);
            TextView most_played_kda = (TextView) most_played_row.findViewById(R.id.most_played_kda);
            TextView most_played_gpm = (TextView) most_played_row.findViewById(R.id.most_played_gpm);
            TextView most_played_xpm = (TextView) most_played_row.findViewById(R.id.most_played_xpm);
            TextView most_played_hero_id = (TextView) most_played_row.findViewById(R.id.most_played_hero_id);

            String heroNameString = myHomeActivity.jsonArray.getJSONObject(i).getString("hero_name");

            most_played_hero_image.setImageResource(getImageId(myHomeActivity.getApplicationContext(),heroNameString));

            heroNameString = heroNameString.replace("npc_dota_hero_", "");
            heroNameString = heroNameString.replace("_", " ");
            heroNameString = heroNameString.substring(0,1).toUpperCase() + heroNameString.substring(1).toLowerCase();

            most_played_hero_name.setText(heroNameString);
            most_played_last_match.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("last_match_date"));
            most_played_total_matches.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("total_matches"));
            most_played_win_rate.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("win_rate"));
            most_played_kda.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("kda"));
            most_played_gpm.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("gold_per_minute"));
            most_played_xpm.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("xp_per_minute"));
            most_played_hero_id.setText(myHomeActivity.jsonArray.getJSONObject(i).getString("hero_id"));

            mostPlayedHeroesLayout.addView(most_played_row);
        }

        TextView average_gpm = (TextView) my_view.findViewById(R.id.average_gpm);
        TextView average_xpm = (TextView) my_view.findViewById(R.id.average_xpm);
        TextView average_last_hit = (TextView) my_view.findViewById(R.id.average_last_hit);
        TextView average_kills = (TextView) my_view.findViewById(R.id.average_kills);
        TextView average_deaths = (TextView) my_view.findViewById(R.id.average_deaths);
        TextView average_assists = (TextView) my_view.findViewById(R.id.average_assists);
        TextView average_hero_damage = (TextView) my_view.findViewById(R.id.average_hero_damage);
        TextView average_tower_damage = (TextView) my_view.findViewById(R.id.average_tower_damage);
        TextView average_hero_heal = (TextView) my_view.findViewById(R.id.average_hero_heal);

        average_gpm.setText(myHomeActivity.jsonResult.getString("average_gold_per_minute"));
        average_xpm.setText(myHomeActivity.jsonResult.getString("average_xp_per_minute"));
        average_last_hit.setText(myHomeActivity.jsonResult.getString("average_last_hit"));
        average_kills.setText(myHomeActivity.jsonResult.getString("average_kills"));
        average_deaths.setText(myHomeActivity.jsonResult.getString("average_deaths"));
        average_assists.setText(myHomeActivity.jsonResult.getString("average_assists"));
        average_hero_damage.setText(myHomeActivity.jsonResult.getString("average_hero_damage"));
        average_tower_damage.setText(myHomeActivity.jsonResult.getString("average_tower_damage"));
        average_hero_heal.setText(myHomeActivity.jsonResult.getString("average_hero_heal"));

        RadarChart playerStyleChart = (RadarChart) my_view.findViewById(R.id.playerStyleChart);

        ArrayList<Entry> entries1 = new ArrayList<>();

        entries1.add(new Entry(myHomeActivity.jsonResult.getInt("versatility"), 0));
        entries1.add(new Entry(myHomeActivity.jsonResult.getInt("average_kill_participation"), 1));
        entries1.add(new Entry(myHomeActivity.jsonResult.getInt("farming"), 2));
        entries1.add(new Entry(myHomeActivity.jsonResult.getInt("pushing"), 3));
        entries1.add(new Entry(myHomeActivity.jsonResult.getInt("experience"), 4));

        RadarDataSet set1 = new RadarDataSet(entries1, "Last 20 Games");
        set1.setColor(Color.parseColor("#ffa616"));
        set1.setFillColor(Color.parseColor("#DDFFA616"));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawValues(true);
        set1.setDrawHighlightCircleEnabled(false);
        set1.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);

        final String[] xValues = new String[]{"Versatility", "Fighting", "Farming", "Pushing", "Experience"};

        RadarData data = new RadarData(xValues, sets);
        data.setValueTextSize(8f);

        playerStyleChart.setData(data);

        playerStyleChart.invalidate();
        playerStyleChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);
    }

    public void setNetworkError() {
        LinearLayout mostPlayedHeroesLayout = (LinearLayout) my_view.findViewById(R.id.mostPlayedHeroesLayout);
        mostPlayedHeroesLayout.removeViews(0, mostPlayedHeroesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        playerStyleChart.clear();
        expose_public_match_layout.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setExposePublicMatchError() {
        LinearLayout mostPlayedHeroesLayout = (LinearLayout) my_view.findViewById(R.id.mostPlayedHeroesLayout);
        mostPlayedHeroesLayout.removeViews(0, mostPlayedHeroesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        playerStyleChart.clear();
        no_connection_layout.setVisibility(View.GONE);
        expose_public_match_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setInProgressAnalysing() {
        LinearLayout mostPlayedHeroesLayout = (LinearLayout) my_view.findViewById(R.id.mostPlayedHeroesLayout);
        mostPlayedHeroesLayout.removeViews(0, mostPlayedHeroesLayout.getChildCount());
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        playerStyleChart.clear();
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
                                setNetworkError();
                                e.printStackTrace();
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

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("mipmap/" + imageName, null, context.getPackageName());
    }
}

