package com.albertinisodringa.pocketmonsters;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapboxapi_access_token));

        setContentView(R.layout.main_activity_map);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        mapboxMap.getUiSettings().setAttributionEnabled(false);
                        mapboxMap.getUiSettings().setLogoEnabled(false);
                        mapboxMap.getUiSettings().setCompassFadeFacingNorth(false);

                        // Adding monster images to the MapBox style
                        mapboxMap.getStyle().addImage("monster_s", BitmapFactory.decodeResource(getResources(), R.drawable.monster_s));
                        mapboxMap.getStyle().addImage("monster_m", BitmapFactory.decodeResource(getResources(), R.drawable.monster_m));
                        mapboxMap.getStyle().addImage("monster_l", BitmapFactory.decodeResource(getResources(), R.drawable.monster_l));

                        // Handles symbols, annotations and markers for the MapBox API
                        final SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);

                        // Set camera position on Milan
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(45.464200, 9.189829)) // Milan
                                .zoom(9)
                                .build();
                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


                        // Game API handler
                        ApiModel api = new ApiModel("v6LxCAWaIJGHoLxK", getString(R.string.api_url), getApplicationContext());

                        // Get the MapElements from the API
                        getMap(api, symbolManager);
                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void onProfileImageClick(View v) {
        Log.d("MainActivity", "profile tap");
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }

    public void onRankingClick(View v) {
        Log.d("MainActivity", "ranking tap");
        Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
        startActivity(intent);
    }

    /**
     * Helper function
     * Gets or refreshes the MapElements on the MapBox Map
     *
     * @param api
     * @param symbolManager
     */
    private void getMap(ApiModel api, final SymbolManager symbolManager) {
        // Delets all annotations/markers on the map if present
        symbolManager.deleteAll();

        // Call to the api to get all the MapElements and displays them on the MapBox map
        api.getMapAsync(new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {

                // Displaying the MapElements on the MapBox map
                List<MapElement> mapElementList = new ArrayList<>();
                if (returnFromCallback instanceof List<?>) {
                    mapElementList = (List<MapElement>) returnFromCallback; // Can't check type becuase it's a generic
                }

                for (int i = 0; i < mapElementList.size(); i++) {
                    MapElement mapElement = mapElementList.get(i);

                    if (mapElement instanceof Monster) {
                        String monsterImageId = "monster_";

                        if (mapElement.getSize() == MapElementSize.SMALL) {
                            monsterImageId += "s";
                        } else if (mapElement.getSize() == MapElementSize.MEDIUM) {
                            monsterImageId += "m";
                        } else {
                            monsterImageId += "l";
                        }

                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(mapElement.getLat(), mapElement.getLon()))
                                .withIconImage(monsterImageId)
                                .withIconSize(0.08f)
                                .withIconOffset(new Float[]{0f, 0.2f})
                        );

                    } else if (mapElement instanceof Candy) {
                        String candyImageId = "monster_"; // TODO: after adding candy images, edit id to candy_

                        if (mapElement.getSize() == MapElementSize.SMALL) {
                            candyImageId += "s";
                        } else if (mapElement.getSize() == MapElementSize.MEDIUM) {
                            candyImageId += "m";
                        } else {
                            candyImageId += "l";
                        }

                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(mapElement.getLat(), mapElement.getLon()))
                                .withIconImage(candyImageId)
                                .withIconSize(0.08f)
                                .withIconOffset(new Float[]{0f, 0.2f})
                        );
                    }
                }
            }

            @Override
            public void onFailure(Exception error) {
                Log.d("MainActivity", "ErrorVlado");
            }
        });
    }

}