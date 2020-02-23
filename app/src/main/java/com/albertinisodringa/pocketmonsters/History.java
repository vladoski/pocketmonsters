package com.albertinisodringa.pocketmonsters;

public class History {
    private int mapElementId;
    private int counter;

    public byte[] getImage() {
        return image;
    }

    public History setImage(byte[] image) {
        this.image = image;
        return this;
    }

    private byte[] image;

    public History(int mapElementId, int counter) {
        this.mapElementId = mapElementId;
        this.counter = counter;
        this.image = null;
    }

    public int getMapElementId() {
        return mapElementId;
    }

    public History setMapElementId(int mapElementId) {
        this.mapElementId = mapElementId;
        return this;
    }

    public int getCounter() {
        return counter;
    }

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
