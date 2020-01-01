package com.albertinisodringa.pocketmonsters;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that handles the Monster MapElement
 */
public class Monster implements MapElement {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private MapElementSize size;

    /**
     * Instantiates a new Monster.
     */
    public Monster() {
        this.id = 0;
        this.name = "";
        this.lat = 0.0f;
        this.lon = 0.0f;
        this.size = MapElementSize.SMALL;
    }

    /**
     * Instantiates a new Monster.
     *
     * @param id   the id
     * @param name the name
     * @param lat  the lat
     * @param lon  the lon
     * @param size the size
     */
    public Monster(int id, String name, double lat, double lon, MapElementSize size) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.size = size;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getLat() {
        return this.lat;
    }

    @Override
    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public double getLon() {
        return this.lon;
    }

    @Override
    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public MapElementSize getSize() {
        return this.size;
    }

    @Override
    public void setSize(MapElementSize size) {
        this.size = size;
    }

    @Override
    public JSONObject getJson() {
        JSONObject json = new JSONObject();

        try {
            json = new JSONObject("{\n" +
                    "\t\"type\": \"" + "monster" + "\",\n" +
                    "\t\"id\": \"" + getId() + "\",\n" +
                    "\t\"name\": \"" + getName() + "\",\n" +
                    "\t\"lat\": \"" + getLat() + "\",\n" +
                    "\t\"lon\": \"" + getLon() + "\",\n" +
                    "\t\"size\": " + getSize() + "\n" +
                    "}");
        } catch (JSONException e) {
            Log.d("Monster", e.getMessage());
        }

        return json;
    }

    @Override
    public String toString() {
        return "Monster{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", size=" + size +
                '}';
    }
}
