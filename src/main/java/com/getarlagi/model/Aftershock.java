package com.getarlagi.model;

import java.time.LocalDateTime;

public class Aftershock extends Earthquake {
    private double mainshockDistanceKm; // Distance from the mainshock
    private int predictedCount; // For calculations based on Omori's Law (predicted count)

    public Aftershock(double latitude, double longitude, double magnitude, LocalDateTime timestamp, String location, double mainshockDistanceKm, int predictedCount) {
        super(latitude, longitude, magnitude, timestamp, location);
        this.mainshockDistanceKm = mainshockDistanceKm;
        this.predictedCount = predictedCount;
    }

    public double getMainshockDistanceKm() {
        return mainshockDistanceKm;
    }

    public void setMainshockDistanceKm(double mainshockDistanceKm) {
        this.mainshockDistanceKm = mainshockDistanceKm;
    }

    public int getPredictedCount() {
        return predictedCount;
    }

    public void setPredictedCount(int predictedCount) {
        this.predictedCount = predictedCount;
    }
}
