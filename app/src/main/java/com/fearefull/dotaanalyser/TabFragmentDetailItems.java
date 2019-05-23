package com.fearefull.dotaanalyser;

import android.content.Context;
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
public class TabFragmentDetailItems extends Fragment {
    private View my_item_view;
    LayoutInflater inflater;
    boolean isCreate = true;
    DetailMatchActivity myDetailActivity;
    boolean isSetData = false;
    ImageView loading_image;
    ScrollView scrollView;
    Animation loading_animation;
    LinearLayout no_connection_layout;
    Button no_connection_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        my_item_view =  inflater.inflate(R.layout.detail_match_tab_item, container, false);
        this.inflater = inflater;
        myDetailActivity = (DetailMatchActivity) getActivity();

        no_connection_layout = (LinearLayout) my_item_view.findViewById(R.id.no_connection_layout);
        no_connection_button = (Button) my_item_view.findViewById(R.id.no_connection_button);
        no_connection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDetailActivity.refreshFunction();
            }
        });

        scrollView = (ScrollView) my_item_view.findViewById(R.id.scrollView);
        loading_image = (ImageView) my_item_view.findViewById(R.id.loading_image);
        loading_animation = AnimationUtils.loadAnimation(myDetailActivity.getBaseContext(), R.anim.rotate_loading);
        loading_image.startAnimation(loading_animation);

        return my_item_view;
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

        LinearLayout tableItemRadiantLayout = (LinearLayout) my_item_view.findViewById(R.id.tableItemRadiantLayout);
        LinearLayout tableItemDireLayout = (LinearLayout) my_item_view.findViewById(R.id.tableItemDireLayout);
        String hero_image_url;
        for (int i = 0; i < 5; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_item_row, tableItemRadiantLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                ImageView item_0_player = (ImageView) player_row.findViewById(R.id.item_0_player);
                ImageView item_1_player = (ImageView) player_row.findViewById(R.id.item_1_player);
                ImageView item_2_player = (ImageView) player_row.findViewById(R.id.item_2_player);
                ImageView item_3_player = (ImageView) player_row.findViewById(R.id.item_3_player);
                ImageView item_4_player = (ImageView) player_row.findViewById(R.id.item_4_player);
                ImageView item_5_player = (ImageView) player_row.findViewById(R.id.item_5_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                if (myDetailActivity.players.getJSONObject(i).isNull("item_0")) {
                    item_0_player.setVisibility(View.GONE);

                    View item_0_space = player_row.findViewById(R.id.space_0_item);
                    item_0_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_0_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_0")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_1")) {
                    item_1_player.setVisibility(View.GONE);

                    View item_1_space = player_row.findViewById(R.id.space_1_item);
                    item_1_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_1_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_1")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_2")) {
                    item_2_player.setVisibility(View.GONE);

                    View item_2_space = player_row.findViewById(R.id.space_2_item);
                    item_2_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_2_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_2")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_3")) {
                    item_3_player.setVisibility(View.GONE);

                    View item_3_space = player_row.findViewById(R.id.space_3_item);
                    item_3_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_3_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_3")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_4")) {
                    item_4_player.setVisibility(View.GONE);

                    View item_4_space = player_row.findViewById(R.id.space_4_item);
                    item_4_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_4_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_4")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_5")) {
                    item_5_player.setVisibility(View.GONE);

                    View item_5_space = player_row.findViewById(R.id.space_5_item);
                    item_5_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_5_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_5")));
                }

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableItemRadiantLayout.addView(player_row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 5; i < 10; i++) {

            try {
                View player_row = inflater.inflate(R.layout.layout_item_row, tableItemDireLayout, false);

                ImageView hero_image_player = (ImageView) player_row.findViewById(R.id.hero_image_player);
                TextView person_name_player = (TextView) player_row.findViewById(R.id.person_name_player);
                ImageView item_0_player = (ImageView) player_row.findViewById(R.id.item_0_player);
                ImageView item_1_player = (ImageView) player_row.findViewById(R.id.item_1_player);
                ImageView item_2_player = (ImageView) player_row.findViewById(R.id.item_2_player);
                ImageView item_3_player = (ImageView) player_row.findViewById(R.id.item_3_player);
                ImageView item_4_player = (ImageView) player_row.findViewById(R.id.item_4_player);
                ImageView item_5_player = (ImageView) player_row.findViewById(R.id.item_5_player);

                person_name_player.setText(myDetailActivity.players.getJSONObject(i).getString("person_name"));
                if (myDetailActivity.players.getJSONObject(i).isNull("item_0")) {
                    item_0_player.setVisibility(View.GONE);

                    View item_0_space = player_row.findViewById(R.id.space_0_item);
                    item_0_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_0_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_0")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_1")) {
                    item_1_player.setVisibility(View.GONE);

                    View item_1_space = player_row.findViewById(R.id.space_1_item);
                    item_1_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_1_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_1")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_2")) {
                    item_2_player.setVisibility(View.GONE);

                    View item_2_space = player_row.findViewById(R.id.space_2_item);
                    item_2_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_2_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_2")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_3")) {
                    item_3_player.setVisibility(View.GONE);

                    View item_3_space = player_row.findViewById(R.id.space_3_item);
                    item_3_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_3_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_3")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_4")) {
                    item_4_player.setVisibility(View.GONE);

                    View item_4_space = player_row.findViewById(R.id.space_4_item);
                    item_4_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_4_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_4")));
                }
                if (myDetailActivity.players.getJSONObject(i).isNull("item_5")) {
                    item_5_player.setVisibility(View.GONE);

                    View item_5_space = player_row.findViewById(R.id.space_5_item);
                    item_5_space.setVisibility(View.VISIBLE);
                }
                else {
                    item_5_player.setImageResource(getImageId(myDetailActivity.getApplicationContext(),myDetailActivity.players.getJSONObject(i).getString("item_5")));
                }

                String hero_name = myDetailActivity.players.getJSONObject(i).getString("hero_name").replace("npc_dota_hero_", "");
                hero_image_url = "http://cdn.dota2.com/apps/dota2/images/heroes/" + hero_name + "_lg.png";
                Picasso.with(myDetailActivity.getApplicationContext()).load(hero_image_url).into(hero_image_player);

                tableItemDireLayout.addView(player_row);
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
