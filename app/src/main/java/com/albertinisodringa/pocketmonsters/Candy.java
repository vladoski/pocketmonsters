package com.albertinisodringa.pocketmonsters;

public class Candy implements MapElement {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private MapElementSize size;

    public Candy() {
        this.id = 0;
        this.name = "";
        this.lat = 0.0f;
        this.lon = 0.0f;
        this.size = MapElementSize.SMALL;
    }

    public Candy(int id, String name, double lat, double lon, MapElementSize size) {
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
    public String toString() {
        return "Candy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", size=" + size +
                '}';
    }
}
