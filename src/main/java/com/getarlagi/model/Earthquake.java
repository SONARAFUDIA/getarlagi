package com.getarlagi.model;

import java.time.LocalDateTime;

public class Earthquake {
    private double latitude;
    private double longitude;
    private double magnitude;
    private LocalDateTime timestamp;
    private String location;

    public Earthquake(double latitude, double longitude, double magnitude, LocalDateTime timestamp, String location) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.magnitude = magnitude;
        this.timestamp = timestamp;
        this.location = location;
    }

    // Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    // Setters - Optional, depending on whether attributes can change
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
