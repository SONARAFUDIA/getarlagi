package com.getarlagi.model;

import java.time.LocalDateTime;

public class Mainshock extends Earthquake {
    private String tectonicPlate;

    public Mainshock(double latitude, double longitude, double magnitude, LocalDateTime timestamp, String location, String tectonicPlate) {
        super(latitude, longitude, magnitude, timestamp, location); // Call parent constructor
        this.tectonicPlate = tectonicPlate;
    }

    public String getTectonicPlate() {
        return tectonicPlate;
    }

    public void setTectonicPlate(String tectonicPlate) {
        this.tectonicPlate = tectonicPlate;
    }
}
