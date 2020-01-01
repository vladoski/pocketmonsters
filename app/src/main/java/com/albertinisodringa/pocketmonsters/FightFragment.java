package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FightFragment extends Fragment {

    MapElement mapElement = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fight_fragment, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        final Gson gson = new Gson();

        // View handlers
        final TextView actionTitleTextView = getView().findViewById(R.id.fight_text);
        final TextView actionNameTextView = getView().findViewById(R.id.action_name_TextView);
        final TextView mapElementNameTextView = getView().findViewById(R.id.map_element_name_TextView);

        final ImageView mapElementPictureImageView = getView().findViewById(R.id.map_element_picture);
        final ImageView yesButtonImageView = getView().findViewById(R.id.yes_button);
        final ImageView noButtonImageView = getView().findViewById(R.id.no_button);

        // Get data from the bundle passed from MainActivity
        Bundle mapElementDataBundle = getArguments();

        try {
            JSONObject mapElementDataJson = new JSONObject(mapElementDataBundle.getString("mapElementData")); // Create JSONObject from the JSON string passed in the bundle

            // MapElementSize selector
            MapElementSize mapElementSize;
            if (mapElementDataJson.getString("size").equals("SMALL")) {
                mapElementSize = MapElementSize.SMALL;
            } else if (mapElementDataJson.getString("size").equals("MEDIUM")) {
                mapElementSize = MapElementSize.MEDIUM;
            } else {
                mapElementSize = MapElementSize.LARGE;
            }

            // Edits textviews based on the MapElement's type and creates MapElement object
            if (mapElementDataJson.getString("type").equals("candy")) {
                actionTitleTextView.setText(actionTitleTextView.getText() + " eat:");
                actionNameTextView.setText("Eat?");

                mapElement = new Candy(
                        mapElementDataJson.getInt("id"),
                        mapElementDataJson.getString("name"),
                        mapElementDataJson.getDouble("lat"),
                        mapElementDataJson.getDouble("lon"),
                        mapElementSize
                );

            } else {
                actionTitleTextView.setText(actionTitleTextView.getText() + " fight:");
                actionNameTextView.setText("Fight?");

                mapElement = new Monster(
                        mapElementDataJson.getInt("id"),
                        mapElementDataJson.getString("name"),
                        mapElementDataJson.getDouble("lat"),
                        mapElementDataJson.getDouble("lon"),
                        mapElementSize
                );
            }

            // SharedPreferences used for retreiving the sessionId
            final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

            // Get API model
            final ApiModel api = new ApiModel(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

            // Sets the MapElement's name on the view
            mapElementNameTextView.setText(mapElement.getName());

            // Gets the MapElement's image from the server
            api.getMapElementImageAsync(mapElement, new VolleyEventListener() {
                @Override
                public void onSuccess(Object returnFromCallback) {
                    byte[] monsterImage = null;

                    if (returnFromCallback instanceof byte[]) {
                        monsterImage = (byte[]) returnFromCallback;
                    }

                    // Sets the MapElement's image on the view
                    mapElementPictureImageView.setImageBitmap(BitmapFactory.decodeByteArray(monsterImage, 0, monsterImage.length));
                }

                @Override
                public void onFailure(Exception error) {
                    // TODO: refactor this code, useful only for debugging
                    // Used for debugging the VolleyError response, to refactor
                    if (error instanceof VolleyError) {
                        VolleyError volleyError = (VolleyError) error;
                        String body;
                        final String statusCode = String.valueOf(volleyError.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        try {
                            body = new String(volleyError.networkResponse.data, "UTF-8");


                        } catch (Exception e) {
                            Log.d("FightFragment", e.getMessage());
                        }
                    } else {
                        // TODO: find the bug, why is this exception being thrown so many times?
                        Log.d("ProfileEditActivityJSON", error.getMessage());
                    }
                }
            });

            // Accept fightEat action button
            yesButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    api.fightEatAsync(mapElement, new VolleyEventListener() {
                        @Override
                        public void onSuccess(Object returnFromCallback) {
                            // TODO: add message if the player won the fight or he's been killed by the monster

                            getFragmentManager().popBackStack(); // closes the Fragment
                        }

                        @Override
                        public void onFailure(Exception error) {
                            Log.d("FightFragment", error.getMessage());
                        }
                    });
                }
            });

            // Deny fightEat action button
            noButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStack(); // closes the Fragment
                }
            });

        } catch (JSONException e) {
            Log.d("FightFragment", e.getMessage());
        }

    }

}
