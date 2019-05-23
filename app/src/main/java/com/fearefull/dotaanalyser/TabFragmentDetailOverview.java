package com.fearefull.dotaanalyser;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Created by A.Hosseini on 2016-09-06.
 */
public class TabFragmentDetailOverview extends Fragment {
    DetailMatchActivity myDetailActivity;
    boolean isSetData = false;
    boolean isCreate = true;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    View my_overview_view;
    LayoutInflater inflater;
    LinearLayout no_connection_layout;
    Button no_connection_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myDetailActivity = (DetailMatchActivity) getActivity();
        my_overview_view =  inflater.inflate(R.layout.detail_match_tab_overview, container, false);
        this.inflater = inflater;

        no_connection_layout = (LinearLayout) my_overview_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_overview_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_overview_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_overview_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_overview_view;
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isCreate && isVisibleToUser) {
            setHandlerData();
            isCreate = false;
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

        TextView result_match_game = (TextView) my_overview_view.findViewById(R.id.resultMatchDetail);
        TextView game_mode_game = (TextView) my_overview_view.findViewById(R.id.gameModeDetail);
        TextView lobby_type_game = (TextView) my_overview_view.findViewById(R.id.lobbyTypeDetail);
        TextView region_game = (TextView) my_overview_view.findViewById(R.id.regionDetail);
        TextView start_time_game = (TextView) my_overview_view.findViewById(R.id.startTimeDetail);
        TextView duration_game = (TextView) my_overview_view.findViewById(R.id.durationDetail);
        game_mode_game.setText(myDetailActivity.game_mode);
        lobby_type_game.setText(myDetailActivity.lobby_type);
        region_game.setText(myDetailActivity.region);
        start_time_game.setText(myDetailActivity.start_time);
        duration_game.setText(myDetailActivity.duration);

        if (myDetailActivity.radiant_win == 1) {
            result_match_game.setText("RADIANT VICTORY");
            result_match_game.setTextColor(getResources().getColor(R.color.colorWin));
        }
        else {
            result_match_game.setText("DIRE VICTORY");
            result_match_game.setTextColor(getResources().getColor(R.color.colorLose));
        }

        LinearLayout tableOverviewRadiantLayout = (LinearLayout) my_overview_view.findViewById(R.id.tableOverviewRadiantLayout);
        LinearLayout tableOverviewDireLayout = (LinearLayout) my_overview_view.findViewById(R.id.tableOverviewDireLayout);
        String hero_image_url;
        for (int i = 0; i < 5; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_overview_row, tableOverviewRadiantLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView level_player = (TextView) player_row.findViewById(R.id.level_player);
                TextView kills_player = (TextView) player_row.findViewById(R.id.kills_player);
                TextView deaths_player = (TextView) player_row.findViewById(R.id.deaths_player);
                TextView assists_player = (TextView) player_row.findViewById(R.id.assists_player);
                TextView kda_player = (TextView) player_row.findViewById(R.id.kda_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                level_player.setText(myDetailActivity.players.getJSONObject(i).getString("level"));
                kills_player.setText(myDetailActivity.players.getJSONObject(i).getString("kills"));
                deaths_player.setText(myDetailActivity.players.getJSONObject(i).getString("deaths"));
                assists_player.setText(myDetailActivity.players.getJSONObject(i).getString("assists"));
                kda_player.setText(myDetailActivity.players.getJSONObject(i).getString("kda"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableOverviewRadiantLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_overview_total_row, tableOverviewRadiantLayout, false);
            TextView level_team = (TextView) team_row.findViewById(R.id.level_team);
            TextView kills_team = (TextView) team_row.findViewById(R.id.kills_team);
            TextView deaths_team = (TextView) team_row.findViewById(R.id.deaths_team);
            TextView assists_team = (TextView) team_row.findViewById(R.id.assists_team);
            TextView kda_team= (TextView) team_row.findViewById(R.id.kda_team);

            level_team.setText(myDetailActivity.resultJson.getString("radiant_level"));
            kills_team.setText(myDetailActivity.resultJson.getString("radiant_kills"));
            deaths_team.setText(myDetailActivity.resultJson.getString("radiant_deaths"));
            assists_team.setText(myDetailActivity.resultJson.getString("radiant_assists"));
            kda_team.setText(myDetailActivity.resultJson.getString("radiant_kda"));

            tableOverviewRadiantLayout.addView(team_row);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 5; i < 10; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_overview_row, tableOverviewDireLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView level_player = (TextView) player_row.findViewById(R.id.level_player);
                TextView kills_player = (TextView) player_row.findViewById(R.id.kills_player);
                TextView deaths_player = (TextView) player_row.findViewById(R.id.deaths_player);
                TextView assists_player = (TextView) player_row.findViewById(R.id.assists_player);
                TextView kda_player = (TextView) player_row.findViewById(R.id.kda_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                level_player.setText(myDetailActivity.players.getJSONObject(i).getString("level"));
                kills_player.setText(myDetailActivity.players.getJSONObject(i).getString("kills"));
                deaths_player.setText(myDetailActivity.players.getJSONObject(i).getString("deaths"));
                assists_player.setText(myDetailActivity.players.getJSONObject(i).getString("assists"));
                kda_player.setText(myDetailActivity.players.getJSONObject(i).getString("kda"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableOverviewDireLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_overview_total_row, tableOverviewDireLayout, false);
            TextView level_team = (TextView) team_row.findViewById(R.id.level_team);
            TextView kills_team = (TextView) team_row.findViewById(R.id.kills_team);
            TextView deaths_team = (TextView) team_row.findViewById(R.id.deaths_team);
            TextView assists_team = (TextView) team_row.findViewById(R.id.assists_team);
            TextView kda_team= (TextView) team_row.findViewById(R.id.kda_team);

            level_team.setText(myDetailActivity.resultJson.getString("dire_level"));
            kills_team.setText(myDetailActivity.resultJson.getString("dire_kills"));
            deaths_team.setText(myDetailActivity.resultJson.getString("dire_deaths"));
            assists_team.setText(myDetailActivity.resultJson.getString("dire_assists"));
            kda_team.setText(myDetailActivity.resultJson.getString("dire_kda"));

            tableOverviewDireLayout.addView(team_row);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
