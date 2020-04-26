package com.albertinisodringa.pocketmonsters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * HistoryDetailAcitivity implements the activity that shows to the user more data about the MapElement that he has clicked on the previous Activity (HistoryActivity)
 */
public class HistoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);
        ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

        final int mapElementId = getIntent().getExtras().getInt("mapElementId");
        final int actionCounter = getIntent().getExtras().getInt("counter");

        api.getMapAsync(new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                List<MapElement> mapElementList = null;
                if (returnFromCallback instanceof List) {
                    mapElementList = (List<MapElement>) returnFromCallback;
                }

                MapElement mapElement = null;
                for (MapElement element : mapElementList) {
                    if (element.getId() == mapElementId) {
                        mapElement = element;
                        break;
                    }
                }

                getMapElementImageCallback(mapElement, actionCounter);

            }

            @Override
            public void onFailure(Exception error) {
                ApiErrorHandler.handle(error, getApplicationContext());
            }
        });

    }

    private void getMapElementImageCallback(final MapElement mapElement, final int counter) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);
        ApiHandler api = new ApiHandler(sharedPreferences.getString("sessionId", null), getString(R.string.api_url), getApplicationContext());

        api.getMapElementImageAsync(mapElement, new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                byte[] image = null;

                if (returnFromCallback instanceof byte[]) {
                    image = (byte[]) returnFromCallback;
                }

                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                TextView mapElementNameTextView = findViewById(R.id.map_element_name_text_view);
                TextView counterTextView = findViewById(R.id.counter_text_view);
                TextView sizeTextView = findViewById(R.id.size_text_view);

                mapElementNameTextView.setText(mapElement.getName());
                counterTextView.setText("Times: " + counter);
                sizeTextView.setText(mapElement.getSize().toString());

                ImageView mapElementImageView = findViewById(R.id.map_element_image_view);
                mapElementImageView.setImageBitmap(imageBitmap);
            }

            @Override
            public void onFailure(Exception error) {
                ApiErrorHandler.handle(error, getApplicationContext());
            }
        });
    }

    /**
     * On back click.
     *
     * @param v the view
     */
// Goes back to ProfileActivity if back button is clicked
    public void onBackClick(View v) {
        Log.d("ProfileActivity", "Back tap to MainActivity");
        super.onBackPressed();
    }
}
