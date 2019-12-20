package com.albertinisodringa.pocketmonsters;

public interface MapElement {
    int id = 0;
    String name = "";
    double lat = 0;
    double lon = 0;
    MapElementSize size = MapElementSize.SMALL;

    int getId();

    void setId(int id);

    String getName();

    void setName(String name);

    double getLat();

    void setLat(double lat);

    double getLon();

    void setLon(double lon);

    MapElementSize getSize();

    void setSize(MapElementSize size);
}
