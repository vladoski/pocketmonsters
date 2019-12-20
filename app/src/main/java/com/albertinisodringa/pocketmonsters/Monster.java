package com.albertinisodringa.pocketmonsters;

public class Monster implements MapElement {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private MapElementSize size;

    public Monster() {
        this.id = 0;
        this.name = "";
        this.lat = 0.0f;
        this.lon = 0.0f;
        this.size = MapElementSize.SMALL;
    }

    public Monster(int id, String name, double lat, double lon, MapElementSize size) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.size = size;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void setId(int id) {

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getLat() {
        return 0;
    }

    @Override
    public void setLat(double lat) {

    }

    @Override
    public double getLon() {
        return 0;
    }

    @Override
    public void setLon(double lon) {

    }

    @Override
    public MapElementSize getSize() {
        return null;
    }

    @Override
    public void setSize(MapElementSize size) {

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
