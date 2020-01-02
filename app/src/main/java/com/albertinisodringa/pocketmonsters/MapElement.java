package com.albertinisodringa.pocketmonsters;

import org.json.JSONObject;

/**
 * Interface that handles the MapElements present on the map (candies and monsters)
 */
public interface MapElement {
    /**
     * The id.
     */
    int id = 0;

    /**
     * The name.
     */
    String name = "";

    /**
     * The latitute where's the MapElement on the map.
     */
    double lat = 0;

    /**
     * The longitude where's the MapElement on the map.
     */
    double lon = 0;

    /**
     * The size of the MapElement.
     */
    MapElementSize size = MapElementSize.SMALL;

    /**
     * Gets id.
     *
     * @return the id
     */
    int getId();

    /**
     * Sets id.
     *
     * @param id the id
     */
    void setId(int id);

    /**
     * Gets name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets name.
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Gets lat.
     *
     * @return the lat
     */
    double getLat();

    /**
     * Sets latitude.
     *
     * @param lat the latitute
     */
    void setLat(double lat);

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    double getLon();

    /**
     * Sets lon.
     *
     * @param lon the lon
     */
    void setLon(double lon);

    /**
     * Gets size.
     *
     * @return the size
     */
    MapElementSize getSize();

    /**
     * Sets size of the MapElement.
     *
     * @param size the size of the MapElement
     */
    void setSize(MapElementSize size);

    /**
     * Gets a JSONObject that represent the MapElement
     *
     * @return
     */
    JSONObject getJson();

}
