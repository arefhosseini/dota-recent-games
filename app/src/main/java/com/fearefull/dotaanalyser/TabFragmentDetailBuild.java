package com.fearefull.dotaanalyser;

import android.annotation.TargetApi;
import android.os.Build;
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

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Objects;

/**
 * Created by A.Hosseini on 2016-09-06.
 */
public class TabFragmentDetailBuild extends Fragment {
    private View my_build_view;
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

        my_build_view =  inflater.inflate(R.layout.detail_match_tab_build, container, false);
        this.inflater = inflater;
        myDetailActivity = (DetailMatchActivity) getActivity();

        no_connection_layout = (LinearLayout) my_build_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_build_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_build_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_build_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_build_view;
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setData() throws JSONException {
        loading_image.clearAnimation();
        loading_image.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        isSetData = true;

        View player_row;

        LinearLayout tableBuildRadiantLayout = (LinearLayout) my_build_view.findViewById(R.id.tableBuildRadiantLayout);
        LinearLayout tableBuildDireLayout = (LinearLayout) my_build_view.findViewById(R.id.tableBuildDireLayout);
        String hero_image_url;
        String ability_image_url;
        for (int i = 0; i < 10; i++) {

            try {
                if (i < 5){
                    player_row = inflater.inflate(R.layout.layout_build_row, tableBuildRadiantLayout, false);
                }
                else {
                    player_row = inflater.inflate(R.layout.layout_build_row, tableBuildDireLayout, false);
                }

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);

                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_1"), "0")) {
                    ImageView ability_1_player = (ImageView) player_row.findViewById(R.id.ability_1_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_1"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_1") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_1_player);
                    }
                    ability_1_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_2"), "0")) {
                    ImageView ability_2_player = (ImageView) player_row.findViewById(R.id.ability_2_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_2"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_2") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_2_player);
                    }
                    ability_2_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_3"), "0")) {
                    ImageView ability_3_player = (ImageView) player_row.findViewById(R.id.ability_3_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_3"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_3") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_3_player);
                    }
                    ability_3_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_4"), "0")) {
                    ImageView ability_4_player = (ImageView) player_row.findViewById(R.id.ability_4_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_4"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_4") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_4_player);
                    }
                    ability_4_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_5"), "0")) {
                    ImageView ability_5_player = (ImageView) player_row.findViewById(R.id.ability_5_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_5"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_5") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_5_player);
                    }
                    ability_5_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_6"), "0")) {
                    ImageView ability_6_player = (ImageView) player_row.findViewById(R.id.ability_6_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_6"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_6") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_6_player);
                    }
                    ability_6_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_7"), "0")) {
                    ImageView ability_7_player = (ImageView) player_row.findViewById(R.id.ability_7_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_7"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_7") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_7_player);
                    }
                    ability_7_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_8"), "0")) {
                    ImageView ability_8_player = (ImageView) player_row.findViewById(R.id.ability_8_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_8"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_8") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_8_player);
                    }
                    ability_8_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_9"), "0")) {
                    ImageView ability_9_player = (ImageView) player_row.findViewById(R.id.ability_9_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_9"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_9") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_9_player);
                    }
                    ability_9_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_10"), "0")) {
                    ImageView ability_10_player = (ImageView) player_row.findViewById(R.id.ability_10_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_10"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_10") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_10_player);
                    }
                    ability_10_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_11"), "0")) {
                    ImageView ability_11_player = (ImageView) player_row.findViewById(R.id.ability_11_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_11"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_11") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_11_player);
                    }
                    ability_11_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_12"), "0")) {
                    ImageView ability_12_player = (ImageView) player_row.findViewById(R.id.ability_12_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_12"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_12") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_12_player);
                    }
                    ability_12_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_13"), "0")) {
                    ImageView ability_13_player = (ImageView) player_row.findViewById(R.id.ability_13_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_13"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_13") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_13_player);
                    }
                    ability_13_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_14"), "0")) {
                    ImageView ability_14_player = (ImageView) player_row.findViewById(R.id.ability_14_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_14"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_14") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_14_player);
                    }
                    ability_14_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_15"), "0")) {
                    ImageView ability_15_player = (ImageView) player_row.findViewById(R.id.ability_15_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_15"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_15") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_15_player);
                    }
                    ability_15_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_16"), "0")) {
                    ImageView ability_16_player = (ImageView) player_row.findViewById(R.id.ability_16_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_16"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_16") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_16_player);
                    }
                    ability_16_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_17"), "0")) {
                    ImageView ability_17_player = (ImageView) player_row.findViewById(R.id.ability_17_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_17"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_17") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_17_player);
                    }
                    ability_17_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_18"), "0")) {
                    ImageView ability_18_player = (ImageView) player_row.findViewById(R.id.ability_18_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_18"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_18") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_18_player);
                    }
                    ability_18_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_19"), "0")) {
                    ImageView ability_19_player = (ImageView) player_row.findViewById(R.id.ability_19_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_19"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_19") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_19_player);
                    }
                    ability_19_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_20"), "0")) {
                    ImageView ability_20_player = (ImageView) player_row.findViewById(R.id.ability_20_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_20"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_20") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_20_player);
                    }
                    ability_20_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_21"), "0")) {
                    ImageView ability_21_player = (ImageView) player_row.findViewById(R.id.ability_21_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_21"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_21") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_21_player);
                    }
                    ability_21_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_22"), "0")) {
                    ImageView ability_22_player = (ImageView) player_row.findViewById(R.id.ability_22_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_22"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_22") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_22_player);
                    }
                    ability_22_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_23"), "0")) {
                    ImageView ability_23_player = (ImageView) player_row.findViewById(R.id.ability_23_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_23"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_23") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_23_player);
                    }
                    ability_23_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_24"), "0")) {
                    ImageView ability_24_player = (ImageView) player_row.findViewById(R.id.ability_24_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_24"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_24") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_24_player);
                    }
                    ability_24_player.setVisibility(View.VISIBLE);
                }
                if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_25"), "0")) {
                    ImageView ability_25_player = (ImageView) player_row.findViewById(R.id.ability_25_player);
                    if (! Objects.equals(myDetailActivity.players.getJSONObject(i).getString("ability_25"), "stats")) {
                        ability_image_url = "http://cdn.dota2.com/apps/dota2/images/abilities/" + myDetailActivity.players.getJSONObject(i).getString("ability_25") + "_hp1.png";
                        Picasso.with(myDetailActivity.getApplicationContext()).load(ability_image_url).into(ability_25_player);
                    }
                    ability_25_player.setVisibility(View.VISIBLE);
                }

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);


                if (i < 5) {
                    tableBuildRadiantLayout.addView(player_row);
                }
                else {
                    tableBuildDireLayout.addView(player_row);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
