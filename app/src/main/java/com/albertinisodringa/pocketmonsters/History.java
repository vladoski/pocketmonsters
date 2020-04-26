package com.albertinisodringa.pocketmonsters;

/**
 * History class
 *
 * Handles everything that has to do with the History element of a player
 * It's used by the ApiHandler when gethistory is called
 */
public class History {
    private int mapElementId;
    private int counter;

    /**
     * Get image byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Sets image.
     *
     * @param image the image
     * @return the image
     */
    public History setImage(byte[] image) {
        this.image = image;
        return this;
    }

    private byte[] image;

    /**
     * History constructior
     *
     * @param mapElementId the map element id
     * @param counter      the counter
     */
    public History(int mapElementId, int counter) {
        this.mapElementId = mapElementId;
        this.counter = counter;
        this.image = null;
    }

    /**
     * Gets map element id.
     *
     * @return the map element id
     */
    public int getMapElementId() {
        return mapElementId;
    }

    /**
     * Sets map element id.
     *
     * @param mapElementId the map element id
     * @return the map element id
     */
    public History setMapElementId(int mapElementId) {
        this.mapElementId = mapElementId;
        return this;
    }

    /**
     * Gets counter.
     *
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Sets counter.
     *
     * @param counter the counter
     * @return the counter
     */
    public History setCounter(int counter) {
        this.counter = counter;
        return this;
    }

    @Override
    public String toString() {
        return "History{" +
                "mapElementId=" + mapElementId +
                ", counter=" + counter +
                '}';
    }
}
