package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Model that handles the connection with the API of the game.
 * Handles automatically asynchronous request using Volley.
 */
public class ApiModel {
    private String sessionId;
    private String apiUrl;
    static private RequestQueue requestQueue;

    /**
     * Instantiates a new Api model.
     * Useful before signing up on the API (registerAsync)
     *
     * @param apiUrl  the api url
     * @param context the context
     */
    public ApiModel(String apiUrl, Context context) {
        this.sessionId = null;
        this.apiUrl = apiUrl;
        ApiModel.requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Instantiates a new Api model.
     *
     * @param sessionId the session id
     * @param apiUrl    the api url
     * @param context   the context
     */
    public ApiModel(String sessionId, String apiUrl, Context context) {
        this.sessionId = sessionId;
        this.apiUrl = apiUrl;
        ApiModel.requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Gets session id.
     *
     * @return the session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets session id.
     *
     * @param sessionId the session id
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets api url.
     *
     * @return the api url
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * Sets api url.
     *
     * @param apiUrl the api url
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * Gets the user profile asynchronously from the API.
     * Returns a Player object in the callback.
     *
     * @param callback the callback
     */
    public void getProfileAsync(final VolleyEventListener callback) {
        String apiUrlRequest = "/getprofile.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                Player player = new Player();
                try {
                    player = new Player(
                            response.getString("username"),
                            Base64.decode(response.getString("img"), Base64.DEFAULT),
                            response.getInt("lp"),
                            response.getInt("xp")
                    );
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(player);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Sets the user profile asynchronously from the API.
     * Returns the new modified Player object in the callback
     *
     * @param name     the name
     * @param image    the image
     * @param callback the callback
     */
// TODO: check if it's better to have a Player object passed as parameter
    public void setProfileAsync(String name, byte[] image, final VolleyEventListener callback) {
        String apiUrlRequest = "/setprofile.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{\n" +
                    "\t\"session_id\": \"" + getSessionId() + "\",\n" +
                    "\t\"username\": \"" + name + "\",\n" +
                    "\t\"img\": \"" + Base64.encodeToString(image, Base64.DEFAULT) + "\"\n" +
                    "}");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                Player player = new Player();
                try {
                    player = new Player(
                            response.getString("username"),
                            Base64.decode(response.getString("img"), Base64.DEFAULT),
                            response.getInt("lp"),
                            response.getInt("xp")
                    );
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(player);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Gets the game map (where the MapElements are on the map) asynchronously from the API.
     * Returns a List of MapElement object in the callback
     *
     * @param callback the callback
     */
    public void getMapAsync(final VolleyEventListener callback) {
        String apiUrlRequest = "/getmap.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                List<MapElement> mapElementList = new ArrayList<>();
                try {
                    JSONArray mapElementJsonArray = response.getJSONArray("mapobjects");

                    for (int i = 0; i < mapElementJsonArray.length(); i++) {
                        JSONObject mapElementJson = mapElementJsonArray.getJSONObject(i);

                        MapElementSize mapElementSize = MapElementSize.SMALL;

                        if (mapElementJson.getString("size").equals("M")) {
                            mapElementSize = MapElementSize.MEDIUM;
                        } else if (mapElementJson.getString("size").equals("L")) {
                            mapElementSize = MapElementSize.LARGE;
                        }

                        if (mapElementJson.getString("type").equals("MO")) {
                            mapElementList.add(new Monster(
                                    mapElementJson.getInt("id"),
                                    mapElementJson.getString("name"),
                                    mapElementJson.getDouble("lat"),
                                    mapElementJson.getDouble("lon"),
                                    mapElementSize
                            ));
                        } else {
                            mapElementList.add(new Candy(
                                    mapElementJson.getInt("id"),
                                    mapElementJson.getString("name"),
                                    mapElementJson.getDouble("lat"),
                                    mapElementJson.getDouble("lon"),
                                    mapElementSize
                            ));
                        }
                    }

                } catch (Exception e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(mapElementList);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Gets ranking (top 20 players ordered by experiencePoints) asynchronously from the API.
     * Returns a List of Player object in the callback
     *
     * @param callback the callback
     */
    public void getRankingAsync(final VolleyEventListener callback) {
        final String apiUrlRequest = "/ranking.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                List<Player> playerList = new ArrayList<>();
                try {
                    JSONArray rankingJsonArray = response.getJSONArray("ranking");

                    for (int i = 0; i < rankingJsonArray.length(); i++) {
                        JSONObject playerRankingJson = rankingJsonArray.getJSONObject(i);

                        playerList.add(new Player(
                                playerRankingJson.getString("username"),
                                Base64.decode(playerRankingJson.getString("img"), Base64.DEFAULT),
                                playerRankingJson.getInt("lp"),
                                playerRankingJson.getInt("xp")
                        ));
                    }

                } catch (Exception e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(playerList);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Gets the images of the MapElements present on the map, asynchronously from the API.
     * Returns a byte[] to the callback
     *
     * @param mapElement the map element
     * @param callback   the callback
     */
    public void getMapElementImageAsync(MapElement mapElement, final VolleyEventListener callback) {
        final String apiUrlRequest = "/getimage.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{\n" +
                    "\t\"session_id\": \"" + getSessionId() + "\",\n" +
                    "\t\"target_id\": " + mapElement.getId() + "\n" +
                    "}");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                String imageBase64 = "";

                try {
                    imageBase64 = response.getString("img");
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(Base64.decode(imageBase64, Base64.DEFAULT));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Gets the images of the MapElements present on the map, asynchronously from the API.
     * Returns a byte[] to the callback
     *
     * @param mapElementId the map element id
     * @param callback   the callback
     */
    public void getMapElementImageAsync(int mapElementId, final VolleyEventListener callback) {
        final String apiUrlRequest = "/getimage.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{\n" +
                    "\t\"session_id\": \"" + getSessionId() + "\",\n" +
                    "\t\"target_id\": " + mapElementId + "\n" +
                    "}");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                String imageBase64 = "";

                try {
                    imageBase64 = response.getString("img");
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(Base64.decode(imageBase64, Base64.DEFAULT));
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Registers a new session/player profile, asynchronously from the API.
     * Returns the sessionId as a String to the callback
     *
     * @param callback the callback
     */
    public void registerAsync(final VolleyEventListener callback) {
        final String apiUrlRequest = "/register.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                String sessionId = "";

                try {
                    sessionId = response.getString("session_id");
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(sessionId);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }

    /**
     * Implements the monster fight between the player and the monster or the candy eating functionality.
     * Asynchronous call to the API.
     * Returns a FightEatResponse to the callback.
     *
     * @param mapElement the map element
     * @param callback   the callback
     */
    public void fightEatAsync(MapElement mapElement, final VolleyEventListener callback) {
        final String apiUrlRequest = "/fighteat.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{\n" +
                    "\t\"session_id\": \"" + getSessionId() + "\",\n" +
                    "\t\"target_id\": \"" + mapElement.getId() + "\"\n" +
                    "}");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            callback.onFailure(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                String sessionId = "";

                try {
                    sessionId = response.getString("session_id");
                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                boolean isPlayerDead = false;

                FightEatResponse fightEatResponse = new FightEatResponse();
                try {
                    if (response.getString("died").equals("true")) {
                        isPlayerDead = true;
                    }

                    fightEatResponse = new FightEatResponse(
                            isPlayerDead,
                            response.getInt("lp"),
                            response.getInt("xp")
                    );

                } catch (JSONException e) {
                    callback.onFailure(e);
                }

                callback.onSuccess(fightEatResponse);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error);
            }
        });

        ApiModel.requestQueue.add(request);
    }
}
