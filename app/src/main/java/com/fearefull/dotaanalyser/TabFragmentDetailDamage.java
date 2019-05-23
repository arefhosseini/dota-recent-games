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
public class TabFragmentDetailDamage extends Fragment {
    private View my_damage_view;
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

        this.inflater = inflater;
        my_damage_view = inflater.inflate(R.layout.detail_match_tab_damage, container, false);
        myDetailActivity = (DetailMatchActivity) getActivity();

        no_connection_layout = (LinearLayout) my_damage_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_damage_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_damage_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_damage_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_damage_view;
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

        LinearLayout tableDamageRadiantLayout = (LinearLayout) my_damage_view.findViewById(R.id.tableDamageRadiantLayout);
        LinearLayout tableDamageDireLayout = (LinearLayout) my_damage_view.findViewById(R.id.tableDamageDireLayout);
        String hero_image_url;
        for (int i = 0; i < 5; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_damage_row, tableDamageRadiantLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView hero_damage_player = (TextView) player_row.findViewById(R.id.hero_damage_player);
                TextView hero_heal_player = (TextView) player_row.findViewById(R.id.hero_heal_player);
                TextView tower_damage_player = (TextView) player_row.findViewById(R.id.tower_damage_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                hero_damage_player.setText(myDetailActivity.players.getJSONObject(i).getString("hero_damage"));
                hero_heal_player.setText(myDetailActivity.players.getJSONObject(i).getString("hero_heal"));
                tower_damage_player.setText(myDetailActivity.players.getJSONObject(i).getString("tower_damage"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableDamageRadiantLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_damage_total_row, tableDamageRadiantLayout, false);
            TextView hero_damage_team = (TextView) team_row.findViewById(R.id.hero_damage_team);
            TextView hero_heal_team = (TextView) team_row.findViewById(R.id.hero_heal_team);
            TextView tower_damage_team = (TextView) team_row.findViewById(R.id.tower_damage_team);

            hero_damage_team.setText(myDetailActivity.resultJson.getString("radiant_hero_damage"));
            hero_heal_team.setText(myDetailActivity.resultJson.getString("radiant_hero_heal"));
            tower_damage_team.setText(myDetailActivity.resultJson.getString("radiant_tower_damage"));

            tableDamageRadiantLayout.addView(team_row);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 5; i < 10; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_damage_row, tableDamageDireLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                TextView hero_damage_player = (TextView) player_row.findViewById(R.id.hero_damage_player);
                TextView hero_heal_player = (TextView) player_row.findViewById(R.id.hero_heal_player);
                TextView tower_damage_player = (TextView) player_row.findViewById(R.id.tower_damage_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                hero_damage_player.setText(myDetailActivity.players.getJSONObject(i).getString("hero_damage"));
                hero_heal_player.setText(myDetailActivity.players.getJSONObject(i).getString("hero_heal"));
                tower_damage_player.setText(myDetailActivity.players.getJSONObject(i).getString("tower_damage"));

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableDamageDireLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            View team_row = inflater.inflate(R.layout.layout_damage_total_row, tableDamageDireLayout, false);
            TextView hero_damage_team = (TextView) team_row.findViewById(R.id.hero_damage_team);
            TextView hero_heal_team = (TextView) team_row.findViewById(R.id.hero_heal_team);
            TextView tower_damage_team = (TextView) team_row.findViewById(R.id.tower_damage_team);

            hero_damage_team.setText(myDetailActivity.resultJson.getString("dire_hero_damage"));
            hero_heal_team.setText(myDetailActivity.resultJson.getString("dire_hero_heal"));
            tower_damage_team.setText(myDetailActivity.resultJson.getString("dire_tower_damage"));

            tableDamageDireLayout.addView(team_row);
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