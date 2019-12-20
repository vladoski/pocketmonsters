package com.albertinisodringa.pocketmonsters;

import android.content.Context;
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

public class ApiModel {
    private String sessionId;
    private String apiUrl;
    static private RequestQueue requestQueue;

    public ApiModel(String sessionId, String apiUrl, Context context) {
        this.sessionId = sessionId;
        this.apiUrl = apiUrl;
        ApiModel.requestQueue = Volley.newRequestQueue(context);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void getProfileAsync(final VolleyEventListener callback) {
        String apiUrlRequest = "/getprofile.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            Log.d("Exception", e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getApiUrl() + apiUrlRequest, requestJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ApiModel", response.toString());

                Player player = new Player();
                try {
                    player = new Player(
                            response.getString("username"),
                            response.getString("img").getBytes(StandardCharsets.UTF_8),
                            response.getInt("lp"),
                            response.getInt("xp")
                    );
                } catch (JSONException e) {
                    Log.d("ApiModel", e.getMessage());
                }

                callback.onSuccess(player);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ApiModel", new String(error.networkResponse.data, StandardCharsets.UTF_8)); // TODO: alternative solution
            }
        });

        ApiModel.requestQueue.add(request);
    }

    public void getMapAsync(final VolleyEventListener callback) {
        String apiUrlRequest = "/getmap.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            Log.d("Exception", e.getMessage());
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
                    Log.d("ApiModel", e.getMessage());
                }

                callback.onSuccess(mapElementList);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ApiModel", new String(error.networkResponse.data, StandardCharsets.UTF_8)); // TODO: alternative solution
            }
        });

        ApiModel.requestQueue.add(request);
    }

    public void getRankingAsync(final VolleyEventListener callback) {
        final String apiUrlRequest = "/ranking.php";
        JSONObject requestJson = new JSONObject();

        try {
            requestJson = new JSONObject("{ \"session_id\": \"" + getSessionId() + "\"} ");
            Log.d("ApiModel", requestJson.toString());
        } catch (JSONException e) {
            Log.d("Exception", e.getMessage());
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
                                playerRankingJson.getString("img").getBytes(StandardCharsets.UTF_8),
                                playerRankingJson.getInt("lp"),
                                playerRankingJson.getInt("xp")
                        ));
                    }

                } catch (Exception e) {
                    Log.d("ApiModel", e.getMessage());
                }

                callback.onSuccess(playerList);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ApiModel", new String(error.networkResponse.data, StandardCharsets.UTF_8)); // TODO: alternative solution
            }
        });

        ApiModel.requestQueue.add(request);
    }
}
