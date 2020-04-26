package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * FightFragment implements the fragment for fighting or eating the MapElement.
 * The fragment calls fighteat from the API.
 * Displays some MapElement attributes and gives the user the possibility to fight/eat the MapElement.
 */
public class FightFragment extends Fragment {

    /**
     * The Map element.
     */
    MapElement mapElement = null;

    /**
     * The Is player dead message.
     */
    final String IS_PLAYER_DEAD_MESSAGE = "You have been slain!";
    /**
     * The Candy eaten message.
     */
    final String CANDY_EATEN_MESSAGE = "Gnam. What a delicious candy!";
    /**
     * The Monster killed message.
     */
    final String MONSTER_KILLED_MESSAGE = "You have killed the monster!";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fight_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // This empty onTouchListener resolves the clickable map annotations from the fragment bug
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // View handlers
        final TextView actionTitleTextView = getView().findViewById(R.id.fight_fragment_title_text_view);
        final TextView actionNameTextView = getView().findViewById(R.id.action_name_text_view);
        final TextView mapElementNameTextView = getView().findViewById(R.id.map_element_name_text_view);

        final ImageView mapElementPictureImageView = getView().findViewById(R.id.map_element_image_view);
        final ImageView yesButtonImageView = getView().findViewById(R.id.accept_fight_button_image_view);
        final ImageView noButtonImageView = getView().findViewById(R.id.decline_fight_button_image_view);

        // Get data from the bundle passed from MainActivity
        final Bundle mapElementDataBundle = getArguments();

        try {
            // Create JSONObject from the JSON string passed in the bundle
            final JSONObject mapElementDataJson = new JSONObject(mapElementDataBundle.getString("mapElementData"));

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
            final ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

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
                    ApiErrorHandler.handle(error, getApplicationContext());
                }
            });

            // Accept fightEat action button
            yesButtonImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    api.fightEatAsync(mapElement, new VolleyEventListener() {
                        @Override
                        public void onSuccess(Object returnFromCallback) {
                            try {
                                final String mapElementType = new JSONObject(mapElementDataBundle.getString("mapElementData")).getString("type"); // Useful for the FightFragment Toast message, because it has to be accessed in anonymous class being final.  TODO: should refactor

                                FightEatResponse fightEatResponse = (FightEatResponse) returnFromCallback;

                                // String message after eating/slaying the monster
                                String fightEatResponseToastString;
                                if (fightEatResponse.isPlayerDead()) {
                                    fightEatResponseToastString = IS_PLAYER_DEAD_MESSAGE;
                                } else if (!fightEatResponse.isPlayerDead() && mapElementType.equals("candy")) {
                                    fightEatResponseToastString = CANDY_EATEN_MESSAGE;
                                } else {
                                    fightEatResponseToastString = MONSTER_KILLED_MESSAGE;
                                }

                                fightEatResponseToastString += "\nLP: " + fightEatResponse.getLifePoints() + " XP: " + fightEatResponse.getExperiencePoints();

                                Toast.makeText(getApplicationContext(), fightEatResponseToastString, Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack(); // closes the Fragment
                            } catch (JSONException e) {
                                ApiErrorHandler.handle(e, getApplicationContext());
                            }
                        }

                        @Override
                        public void onFailure(Exception error) {
                            ApiErrorHandler.handle(error, getApplicationContext());
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
            ApiErrorHandler.handle(e, getApplicationContext());
        }

    }
}
