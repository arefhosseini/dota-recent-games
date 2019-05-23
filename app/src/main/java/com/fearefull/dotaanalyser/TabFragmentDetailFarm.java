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
public class TabFragmentDetailFarm extends Fragment {
    private View my_farm_view;
    LayoutInflater inflater;
    DetailMatchActivity myDetailActivity;
    boolean isSetData = false;
    boolean isCreate = true;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    LinearLayout no_connection_layout;
    Button no_connection_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        my_farm_view =  inflater.inflate(R.layout.detail_match_tab_farm, container, false);
        this.inflater = inflater;
        myDetailActivity = (DetailMatchActivity) getActivity();

        no_connection_layout = (LinearLayout) my_farm_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_farm_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_farm_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_farm_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_farm_view;
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

        LinearLayout tableFarmRadiantLayout = (LinearLayout) my_farm_view.findViewById(R.id.tableFarmRadiantLayout);
        LinearLayout tableFarmDireLayout = (LinearLayout) my_farm_view.findViewById(R.id.tableFarmDireLayout);
        String hero_image_url;
        for (int i = 0; i < 5; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_farm_row, tableFarmRadiantLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView total_gold_player = (TextView) player_row.findViewById(R.id.total_gold_player);
                TextView last_hit_player = (TextView) player_row.findViewById(R.id.last_hit_player);
                TextView deny_player = (TextView) player_row.findViewById(R.id.deny_player);
                TextView gold_per_minute_player = (TextView) player_row.findViewById(R.id.gold_per_minute_player);
                TextView xp_per_minute_player = (TextView) player_row.findViewById(R.id.xp_per_minute_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                total_gold_player.setText(myDetailActivity.players.getJSONObject(i).getString("total_gold"));
                last_hit_player.setText(myDetailActivity.players.getJSONObject(i).getString("last_hit"));
                deny_player.setText(myDetailActivity.players.getJSONObject(i).getString("deny"));
                gold_per_minute_player.setText(myDetailActivity.players.getJSONObject(i).getString("gold_per_minute"));
                xp_per_minute_player.setText(myDetailActivity.players.getJSONObject(i).getString("xp_per_minute"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableFarmRadiantLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_farm_total_row, tableFarmRadiantLayout, false);
            TextView total_gold_team = (TextView) team_row.findViewById(R.id.total_gold_team);
            TextView last_hit_team = (TextView) team_row.findViewById(R.id.last_hit_team);
            TextView deny_team = (TextView) team_row.findViewById(R.id.deny_team);
            TextView gold_per_minute_team = (TextView) team_row.findViewById(R.id.gold_per_minute_team);
            TextView xp_per_minute_team = (TextView) team_row.findViewById(R.id.xp_per_minute_team);

            total_gold_team.setText(myDetailActivity.resultJson.getString("radiant_total_gold"));
            last_hit_team.setText(myDetailActivity.resultJson.getString("radiant_last_hit"));
            deny_team.setText(myDetailActivity.resultJson.getString("radiant_deny"));
            gold_per_minute_team.setText(myDetailActivity.resultJson.getString("radiant_gold_per_minute"));
            xp_per_minute_team.setText(myDetailActivity.resultJson.getString("radiant_xp_per_minute"));

            tableFarmRadiantLayout.addView(team_row);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 5; i < 10; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_farm_row, tableFarmDireLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView total_gold_player = (TextView) player_row.findViewById(R.id.total_gold_player);
                TextView last_hit_player = (TextView) player_row.findViewById(R.id.last_hit_player);
                TextView deny_player = (TextView) player_row.findViewById(R.id.deny_player);
                TextView gold_per_minute_player = (TextView) player_row.findViewById(R.id.gold_per_minute_player);
                TextView xp_per_minute_player = (TextView) player_row.findViewById(R.id.xp_per_minute_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                total_gold_player.setText(myDetailActivity.players.getJSONObject(i).getString("total_gold"));
                last_hit_player.setText(myDetailActivity.players.getJSONObject(i).getString("last_hit"));
                deny_player.setText(myDetailActivity.players.getJSONObject(i).getString("deny"));
                gold_per_minute_player.setText(myDetailActivity.players.getJSONObject(i).getString("gold_per_minute"));
                xp_per_minute_player.setText(myDetailActivity.players.getJSONObject(i).getString("xp_per_minute"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableFarmDireLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_farm_total_row, tableFarmRadiantLayout, false);
            TextView total_gold_team = (TextView) team_row.findViewById(R.id.total_gold_team);
            TextView last_hit_team = (TextView) team_row.findViewById(R.id.last_hit_team);
            TextView deny_team = (TextView) team_row.findViewById(R.id.deny_team);
            TextView gold_per_minute_team = (TextView) team_row.findViewById(R.id.gold_per_minute_team);
            TextView xp_per_minute_team = (TextView) team_row.findViewById(R.id.xp_per_minute_team);

            total_gold_team.setText(myDetailActivity.resultJson.getString("dire_total_gold"));
            last_hit_team.setText(myDetailActivity.resultJson.getString("dire_last_hit"));
            deny_team.setText(myDetailActivity.resultJson.getString("dire_deny"));
            gold_per_minute_team.setText(myDetailActivity.resultJson.getString("dire_gold_per_minute"));
            xp_per_minute_team.setText(myDetailActivity.resultJson.getString("dire_xp_per_minute"));

            tableFarmDireLayout.addView(team_row);
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
