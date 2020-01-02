package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;


// TODO: add comments and possible refactoring
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, Style.OnStyleLoaded, PermissionsListener {

    private ApiModel api;

    private MapView mapView;
    private SymbolManager symbolManager;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private Style style;

    private Handler mapHandler = new Handler();
    private Runnable mapUpdater;

    private LocationEngine locationEngine;
    private LocationListeningCallback locationListeningCallback;
    private Location location;

    private final Gson gson = new Gson();

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //map
        Mapbox.getInstance(this, getString(R.string.mapboxapi_access_token));
        setContentView(R.layout.main_activity_map);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        permissionsManager = new PermissionsManager(this);

        locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        locationListeningCallback = new LocationListeningCallback(this);

        // SharedPreferences used to store persistently the sessionId
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE);

        // Game API handler
        api = new ApiModel(getString(R.string.api_url), getApplicationContext());

        // Write on sharedPreferences the new sessionId from signing up on the API, if there's no one inside sharedPreferences.
        // If it's present just read sessionId from sharedPreferences and set it to the ApiModel
        if (!sharedPreferences.contains("sessionId")) {
            api.registerAsync(new VolleyEventListener() {
                @Override
                public void onSuccess(Object returnFromCallback) {
                    String sessionId = getString(R.string.api_default_session_id); // Default session_id
                    if (returnFromCallback instanceof String) {
                        sessionId = (String) returnFromCallback;
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("sessionId", sessionId);
                    editor.apply();

                    // Set sessionId because it's null
                    api.setSessionId(sessionId);
                }

                @Override
                public void onFailure(Exception error) {
                    ApiModelErrorHandler.handle(error, getApplicationContext());
                }
            });
        } else {
            // Set sessionId from sharedPreferences because it's null
            api.setSessionId(getApplicationContext().getSharedPreferences(getString(R.string.sharedpreferences_key), Context.MODE_PRIVATE).getString("sessionId", null));
        }

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
    public void onRestart() {
        super.onRestart();

        getMap(api, symbolManager);
        getProfile(api);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if (mapHandler != null) {
            mapHandler.removeCallbacks(mapUpdater);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(locationListeningCallback);
        }

        mapView.onStop();

        if (mapHandler != null) {
            mapHandler.removeCallbacks(mapUpdater);
        }
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

        if (mapHandler != null) {
            mapHandler.removeCallbacks(mapUpdater);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, this);
    }

    @Override
    public void onStyleLoaded(@NonNull Style style) {
        this.style = style;
        mapboxMap.getUiSettings().setAttributionEnabled(false);
        mapboxMap.getUiSettings().setLogoEnabled(false);
        mapboxMap.getUiSettings().setCompassFadeFacingNorth(false);

        // Adding monster images to the MapBox style
        mapboxMap.getStyle().addImage("monster_s", BitmapFactory.decodeResource(getResources(), R.drawable.monster_s));
        mapboxMap.getStyle().addImage("monster_m", BitmapFactory.decodeResource(getResources(), R.drawable.monster_m));
        mapboxMap.getStyle().addImage("monster_l", BitmapFactory.decodeResource(getResources(), R.drawable.monster_l));
        mapboxMap.getStyle().addImage("candy_s", BitmapFactory.decodeResource(getResources(), R.drawable.candy_s));
        mapboxMap.getStyle().addImage("candy_m", BitmapFactory.decodeResource(getResources(), R.drawable.candy_m));
        mapboxMap.getStyle().addImage("candy_l", BitmapFactory.decodeResource(getResources(), R.drawable.candy_l));

        // Handles symbols, annotations and markers for the MapBox API
        symbolManager = new SymbolManager(mapView, mapboxMap, mapboxMap.getStyle());
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconTranslate(new Float[]{-4f, 5f});
        symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);

        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                FightFragment fightFragment = new FightFragment();

                Bundle args = new Bundle();

                // Pass the MapElement data (annotation) to the fragment
                args.putString("mapElementData", symbol.getData().getAsJsonObject().toString());
                fightFragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_container, fightFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Set camera position on Milan
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(45.464200, 9.189829)) // Milan
                .zoom(9)
                .build();
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


        // Location permission handler, if the user previously granted  permission to use his position, display it on the map; if not, ask for location permission.
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            showUserLastLocation();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

        // Listenes if the FightFragment has been closed by the user, so the map can refresh
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                getMap(api, symbolManager);
                getProfile(api);
            }
        });

        getMap(api, symbolManager);
        getProfile(api);

        // Refreshes map every 20 seconds in the main thread (because Mapbox doesn't allow to access the map from another thread)
        final long MAP_REFRESH_TIME_IN_MILLISECONDS = DEFAULT_INTERVAL_IN_MILLISECONDS * 30L;

        mapUpdater = new Runnable() {
            @Override
            public void run() {
                try {
                    getMap(api, symbolManager);
                    getProfile(api);
                } catch (Exception e) {
                    Log.d("ExceptionMainActivity", e.getMessage());
                }
                Log.d("MainActivity", "Refreshed map and profile");

                // Using recursion to simulate infinite polling
                mapHandler.postDelayed(this, MAP_REFRESH_TIME_IN_MILLISECONDS);
            }
        };

        mapHandler.postDelayed(mapUpdater, MAP_REFRESH_TIME_IN_MILLISECONDS);
    }

    public void showUserLastLocation() {
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        locationEngine.requestLocationUpdates(request, locationListeningCallback, getMainLooper());
        locationEngine.getLastLocation(locationListeningCallback);

        LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                .elevation(5)
                .accuracyAlpha(.4f)
                .accuracyColor(Color.rgb(113, 197, 247))
                .build();

        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        LocationComponentActivationOptions locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, style)
                .locationComponentOptions(customLocationComponentOptions)
                .build();

        locationComponent.activateLocationComponent(locationComponentActivationOptions);
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    public void onCenterButtonPressed(View view) {
        if (location != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .build();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
        }
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            showUserLastLocation();
        } else {
            Toast.makeText(this, "Permission to use user location not granted", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static class LocationListeningCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;
        private MainActivity mainActivity;

        LocationListeningCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
            mainActivity = activity;
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            mainActivity.location = result.getLastLocation();
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            // TODO: handle this exception
        }
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
                    mapElementList = (List<MapElement>) returnFromCallback; // Can't check type becuase it's a generic, TODO: should refactor
                }

                // Process all the MapElements
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

                        // Create symbol per every Monster
                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(mapElement.getLat(), mapElement.getLon()))
                                .withIconImage(monsterImageId)
                                .withIconSize(0.08f)
                                .withIconOffset(new Float[]{0f, 0.2f})
                                .withData(gson.fromJson(mapElement.getJson().toString(), JsonElement.class)) // Pass MapElement as JSON using Gson in a bad way, it should be refactored to use all the power of the gson package
                        );

                    } else if (mapElement instanceof Candy) {
                        String candyImageId = "candy_";

                        if (mapElement.getSize() == MapElementSize.SMALL) {
                            candyImageId += "s";
                        } else if (mapElement.getSize() == MapElementSize.MEDIUM) {
                            candyImageId += "m";
                        } else {
                            candyImageId += "l";
                        }

                        // Create symbol per every Candy
                        symbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(mapElement.getLat(), mapElement.getLon()))
                                .withIconImage(candyImageId)
                                .withIconSize(0.08f)
                                .withIconOffset(new Float[]{0f, 0.2f})
                                .withData(gson.fromJson(mapElement.getJson().toString(), JsonElement.class)) // Pass MapElement as JSON using Gson in a bad way, it should be refactored to use all the power of the gson package
                        );
                    }
                }
            }

            @Override
            public void onFailure(Exception error) {
                ApiModelErrorHandler.handle(error, getApplicationContext());
            }
        });
    }

    /**
     * Helper function
     * Gets the player profile from the API, then edits the two TextViews (life points and experience points)
     *
     * @param api
     */
    private void getProfile(ApiModel api) {
        api.getProfileAsync(new VolleyEventListener() {
            @Override
            public void onSuccess(Object returnFromCallback) {
                Player player = new Player();
                if (returnFromCallback instanceof Player) {
                    player = (Player) returnFromCallback;
                }

                // Set life points on TextView
                TextView lifePointsTextView = findViewById(R.id.player_life_point);
                lifePointsTextView.setText(player.getLifePoints() + " LP");

                // Set experience points on TextView
                TextView experiencePointsTextView = findViewById(R.id.player_experience_point);
                experiencePointsTextView.setText(player.getExperiencePoints() + " XP");
            }

            @Override
            public void onFailure(Exception error) {
                ApiModelErrorHandler.handle(error, getApplicationContext());
            }
        });
    }
}