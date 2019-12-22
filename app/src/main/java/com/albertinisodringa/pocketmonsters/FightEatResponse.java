package com.albertinisodringa.pocketmonsters;

/**
 * Handles the Response from a fight or eat call to the API.
 */
public class FightEatResponse {
    private Boolean isPlayerDead;
    private int lifePoints;
    private int experiencePoints;

    /**
     * Instantiates a new FightEatResponse.
     */
    public FightEatResponse() {
        this.isPlayerDead = true;
        this.lifePoints = 0;
        this.experiencePoints = 0;
    }

    /**
     * Instantiates a new FightEatResponse.
     *
     * @param isPlayerDead     the is player dead
     * @param lifePoints       the life points
     * @param experiencePoints the experience points
     */
    public FightEatResponse(Boolean isPlayerDead, int lifePoints, int experiencePoints) {
        this.isPlayerDead = isPlayerDead;
        this.lifePoints = lifePoints;
        this.experiencePoints = experiencePoints;
    }

    /**
     * Checks if the player is dead.
     *
     * @return the boolean
     */
    public Boolean isPlayerDead() {
        return isPlayerDead;
    }

    /**
     * Sets is player dead.
     *
     * @param isPlayerDead the is player dead
     * @return the is player dead
     */
    public FightEatResponse setIsPlayerDead(Boolean isPlayerDead) {
        this.isPlayerDead = isPlayerDead;
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
    public FightEatResponse setLifePoints(int lifePoints) {
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
    public FightEatResponse setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
        return this;
    }

    @Override
    public String toString() {
        return "FightEatResponse{" +
                "isPlayerDead=" + isPlayerDead +
                ", lifePoints=" + lifePoints +
                ", experiencePoints=" + experiencePoints +
                '}';
    }
}
