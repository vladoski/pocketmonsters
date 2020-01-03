package com.albertinisodringa.pocketmonsters;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;

/**
 * Class that handles everything that concern the Player
 */
public class Player {
    private String username;
    private byte[] image; // Base64
    private int lifePoints;
    private int experiencePoints;

    /**
     * Instantiates a new Player.
     */
    public Player() {
        // TODO: extract image from drawables not base64 image string
        String imageBase64 = "";

        this.username = "";
        this.image = imageBase64.getBytes(StandardCharsets.UTF_8);
        this.lifePoints = 0;
        this.experiencePoints = 0;
    }

    /**
     * Instantiates a new Player.
     *
     * @param username         the username
     * @param image            the image
     * @param lifePoints       the life points
     * @param experiencePoints the experience points
     */
    public Player(String username, byte[] image, int lifePoints, int experiencePoints) {
        this.username = username;
        this.image = image;
        this.lifePoints = lifePoints;
        this.experiencePoints = experiencePoints;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     * @return the username
     */
    public Player setUsername(String username) {
        this.username = username;
        return this;
    }

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
    public Player setImage(byte[] image) throws Exception {
        if (BitmapFactory.decodeByteArray(image, 0, image.length) != null) {
            this.image = image;
        } else {
            throw new Exception("Image is not valid");
        }
        return this;
    }

    /**
     * Gets life points.
     *
     * @return the life points
     */
    public int getLifePoints() {
        return lifePoints;
    }

    /**
     * Sets life points.
     *
     * @param lifePoints the life points
     * @return the life points
     */
    public Player setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
        return this;
    }

    /**
     * Gets experience points.
     *
     * @return the experience points
     */
    public int getExperiencePoints() {
        return experiencePoints;
    }

    /**
     * Sets experience points.
     *
     * @param experiencePoints the experience points
     * @return the experience points
     */
    public Player setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
        return this;
    }

    /**
     * Add life points to player.
     *
     * @param lifePoints the life points
     * @return the player
     */
    public Player addLifePoints(int lifePoints) {
        this.lifePoints += lifePoints;

        return this;
    }

    /**
     * Add experience points to player.
     *
     * @param experiencePoints the experience points
     * @return the player
     */
    public Player addExperiencePoints(int experiencePoints) {
        this.experiencePoints += experiencePoints;

        return this;
    }

    @Override
    @NonNull
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", lifePoints=" + lifePoints +
                ", experiencePoints=" + experiencePoints +
                '}';
    }
}
