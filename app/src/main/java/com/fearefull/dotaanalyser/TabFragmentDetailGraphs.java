package com.fearefull.dotaanalyser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by A.Hosseini on 2016-09-06.
 */
public class TabFragmentDetailGraphs extends Fragment {
    private View my_view;
    LayoutInflater inflater;
    boolean isCreate = true;
    boolean isCreateChart = false;
    DetailMatchActivity myDetailActivity;
    boolean isSetData = false;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    LinearLayout no_connection_layout;
    Button no_connection_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        my_view =  inflater.inflate(R.layout.detail_match_tab_graphs, container, false);
        this.inflater = inflater;
        myDetailActivity = (DetailMatchActivity) getActivity();

        no_connection_layout = (LinearLayout) my_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_view;
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("mipmap/" + imageName, null, context.getPackageName());
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isCreate && isVisibleToUser) {
            setHandlerData();
            isCreate = false;
        }
        else if (!isCreate && isVisibleToUser && isCreateChart) {
            LineChart lineChartTeam = (LineChart) my_view.findViewById(R.id.lineChartTeam);
            LineChart lineChartPlayer = (LineChart) my_view.findViewById(R.id.lineChartPlayer);
            lineChartTeam.animateXY(3000, 2000);
            lineChartPlayer.animateXY(3000, 2000);
        }
    }

    public void setRefresh() {
        if (!isCreate) {
            scrollView.setVisibility(View.GONE);
            no_connection_layout.setVisibility(View.GONE);
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
    }

    public void setData() throws JSONException {
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        isSetData = true;

        ArrayList<Integer> original_xp = new ArrayList<>();
        original_xp.add(0, 0);
        original_xp.add(1, 200);
        original_xp.add(2, 500);
        original_xp.add(3, 900);
        original_xp.add(4, 1400);
        original_xp.add(5, 2000);
        original_xp.add(6, 2600);
        original_xp.add(7, 3400);
        original_xp.add(8, 4400);
        original_xp.add(9, 5400);
        original_xp.add(10, 6000);
        original_xp.add(11, 8200);
        original_xp.add(12, 9000);
        original_xp.add(13, 10400);
        original_xp.add(14, 11900);
        original_xp.add(15, 13500);
        original_xp.add(16, 15200);
        original_xp.add(17, 17000);
        original_xp.add(18, 18900);
        original_xp.add(19, 20900);
        original_xp.add(20, 23000);
        original_xp.add(21, 25200);
        original_xp.add(22, 27500);
        original_xp.add(23, 29900);
        original_xp.add(24, 32400);

        ArrayList<String> color_hero = new ArrayList<>();
        color_hero.add("#001ADE");
        color_hero.add("#00F556");
        color_hero.add("#AA00BD");
        color_hero.add("#F6FF00");
        color_hero.add("#FF6600");
        color_hero.add("#FF0095");
        color_hero.add("#BBFF00");
        color_hero.add("#00E3C8");
        color_hero.add("#218500");
        color_hero.add("#854D00");

        try {
            ImageView graph_player_0 = (ImageView) my_view.findViewById(R.id.graph_player_0);
            ImageView graph_player_1 = (ImageView) my_view.findViewById(R.id.graph_player_1);
            ImageView graph_player_2 = (ImageView) my_view.findViewById(R.id.graph_player_2);
            ImageView graph_player_3 = (ImageView) my_view.findViewById(R.id.graph_player_3);
            ImageView graph_player_4 = (ImageView) my_view.findViewById(R.id.graph_player_4);
            ImageView graph_player_5 = (ImageView) my_view.findViewById(R.id.graph_player_5);
            ImageView graph_player_6 = (ImageView) my_view.findViewById(R.id.graph_player_6);
            ImageView graph_player_7 = (ImageView) my_view.findViewById(R.id.graph_player_7);
            ImageView graph_player_8 = (ImageView) my_view.findViewById(R.id.graph_player_8);
            ImageView graph_player_9 = (ImageView) my_view.findViewById(R.id.graph_player_9);

            graph_player_0.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(0).getString("hero_name")));
            graph_player_1.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(1).getString("hero_name")));
            graph_player_2.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(2).getString("hero_name")));
            graph_player_3.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(3).getString("hero_name")));
            graph_player_4.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(4).getString("hero_name")));
            graph_player_5.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(5).getString("hero_name")));
            graph_player_6.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(6).getString("hero_name")));
            graph_player_7.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(7).getString("hero_name")));
            graph_player_8.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(8).getString("hero_name")));
            graph_player_9.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(9).getString("hero_name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<Entry>> total_xp_players = new ArrayList<>();
        String index_time;
        int end_with;
        int max_xp_player = 0;
        int last_xp_player = 0;
        int duration_game;
        try {
            duration_game = ((int)myDetailActivity.resultJson.get("duration_right") / 60) + 1;
        } catch (JSONException e) {
            e.printStackTrace();
            duration_game = 30;
        }
        for (int i = 0; i < 10; i++) {
            try {
                ArrayList<Entry> xp_player = new ArrayList<>();
                int start_with = 1;
                xp_player.add(new Entry(0, 0));
                for (int j = 0; j < 25; j++) {
                    index_time = "time_" + String.valueOf(j+1);
                    if ((int)myDetailActivity.players.getJSONObject(i).get(index_time) != 0) {
                        last_xp_player = original_xp.get(j);
                        end_with = ((int)myDetailActivity.players.getJSONObject(i).get(index_time) / 60) + 1 ;
                        for (int k = start_with; k < end_with; k++ ) {
                            if (k < duration_game+1) {
                                xp_player.add(new Entry(original_xp.get(j) / k, k));
                                if ((original_xp.get(j) / k) > max_xp_player) {
                                    max_xp_player = (original_xp.get(j) / k);
                                }
                            }
                            else {
                                break;
                            }
                        }
                        start_with = end_with;
                    }
                    else {
                        for (int k = start_with; k < duration_game+1; k++) {
                            xp_player.add(new Entry(last_xp_player / k, k));
                        }
                        break;
                    }
                }
                total_xp_players.add(xp_player);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.w("max Value", String.valueOf(duration_game));
        LineChart lineChartTeam = (LineChart) my_view.findViewById(R.id.lineChartTeam);

        assert lineChartTeam != null;
        YAxis leftAxis = lineChartTeam.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines

        leftAxis.setDrawZeroLine(false);
        lineChartTeam.getAxisRight().setEnabled(false);
        lineChartTeam.setPinchZoom(true);
        lineChartTeam.setDescription("");

        ArrayList<Entry> direData = new ArrayList<>();
        ArrayList<Entry> radientData = new ArrayList<>();

        String[] timeData = new String[duration_game+1];
        for (int i = 0; i < duration_game+1; i++) {
            timeData[i] = String.valueOf(i);
        }

        int max_xp_team = 0;
        int xp_team;
        for (int j = 0; j < duration_game+1; j++) {
            xp_team = 0;
            for (int i = 0; i < 5; i++) {
                if (total_xp_players.get(i).size() - 1 > j ) {
                    xp_team += total_xp_players.get(i).get(j).getVal();
                }
            }
            xp_team = xp_team / 5;
            if (xp_team > 0) {
                radientData.add(new Entry(xp_team, j));
            }
            if (xp_team > max_xp_team) {
                max_xp_team = xp_team;
            }
        }

        for (int j = 0; j < duration_game+1; j++) {
            xp_team = 0;
            for (int i = 5; i < 10; i++) {
                if (total_xp_players.get(i).size() - 1 > j ) {
                    xp_team += total_xp_players.get(i).get(j).getVal();
                }
            }
            xp_team = xp_team / 5;
            if (xp_team > 0) {
                direData.add(new Entry(xp_team, j));
            }
            if (xp_team > max_xp_team) {
                max_xp_team = xp_team;
            }
        }
        leftAxis.setAxisMaxValue(max_xp_team + 100);
        leftAxis.setAxisMinValue(0);

        LineDataSet set1 = new LineDataSet(radientData,"Radiant");
        set1.setDrawCircles(false);
        set1.setColor(Color.GREEN);
        set1.setLineWidth(3f);
        set1.setDrawFilled(true);
        set1.setDrawValues(false);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_green);
            set1.setFillDrawable(drawable);
        }
        else {
            set1.setFillColor(Color.parseColor("#00ff55"));
        }

        LineDataSet set2 = new LineDataSet(direData,"Dire");
        set2.setDrawCircles(false);
        set2.setColor(Color.RED);
        set2.setLineWidth(3f);
        set2.setDrawFilled(true);
        set2.setDrawValues(false);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
            set2.setFillDrawable(drawable);
        }
        else {
            set2.setFillColor(Color.parseColor("#ff5050"));
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(set1);
        lineDataSets.add(set2);

        lineChartTeam.setData(new LineData(timeData, lineDataSets));

        lineChartTeam.animateXY(3000, 2000);

        LineChart lineChartPlayer = (LineChart) my_view.findViewById(R.id.lineChartPlayer);

        assert lineChartPlayer != null;
        leftAxis = lineChartPlayer.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(max_xp_player + 100);
        leftAxis.setAxisMinValue(0);

        leftAxis.setDrawZeroLine(false);
        lineChartPlayer.getAxisRight().setEnabled(false);
        lineChartPlayer.setPinchZoom(true);
        lineChartPlayer.setDescription("");

        ArrayList<ILineDataSet> linePlayersSets = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LineDataSet setPlayer = new LineDataSet(total_xp_players.get(i),String.valueOf(i));
            setPlayer.setDrawCircles(false);
            setPlayer.setDrawValues(false);
            setPlayer.setColor(Color.parseColor(color_hero.get(i)));
            setPlayer.setLineWidth(2f);
            linePlayersSets.add(setPlayer);
        }

        lineChartPlayer.getLegend().setEnabled(false);
        lineChartPlayer.setData(new LineData(timeData, linePlayersSets));

        lineChartTeam.animateXY(3000, 2000);
        lineChartPlayer.animateXY(3000, 2000);
        isCreateChart = true;
    }

    public void setNetworkError() {
        scrollView.setVisibility(View.GONE);
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
        isSetData = false;
    }

    public void setHandlerData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (myDetailActivity.isReceivingData) {
                    if (myDetailActivity.isNetworkError) { setNetworkError(); }
                    else  {
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
}
