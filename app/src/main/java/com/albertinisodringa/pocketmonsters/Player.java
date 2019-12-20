package com.albertinisodringa.pocketmonsters;

import java.nio.charset.StandardCharsets;

public class Player {
    private String username;
    private byte[] image; // Base64
    private int lifePoints;
    private int experiencePoints;

    public Player() {
        // TODO: extract image from drawables not base64 image string
        String imageBase64 = "";

        this.username = "";
        this.image = imageBase64.getBytes(StandardCharsets.UTF_8);
        this.lifePoints = 0;
        this.experiencePoints = 0;
    }

    public Player(String username, byte[] image, int lifePoints, int experiencePoints) {
        this.username = username;
        this.image = image;
        this.lifePoints = lifePoints;
        this.experiencePoints = experiencePoints;
    }

    // TODO
    public boolean fight(Monster monster) {
        return true;
    }

    // TODO
    public void eat(Candy candy) {

    }


    public String getUsername() {
        return username;
    }

    public Player setUsername(String username) {
        this.username = username;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public Player setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public Player setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
        return this;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public Player setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
        return this;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", lifePoints=" + lifePoints +
                ", experiencePoints=" + experiencePoints +
                '}';
    }
}
