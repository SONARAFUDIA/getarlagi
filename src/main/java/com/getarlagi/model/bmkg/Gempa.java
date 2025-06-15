package com.getarlagi.model.bmkg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Gempa {
    @JsonProperty("Magnitude")
    private String magnitude;

    @JsonProperty("Lintang")
    private String lintang;

    @JsonProperty("Bujur")
    private String bujur;

    @JsonProperty("Wilayah")
    private String wilayah;

    // Getters and Setters
    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getLintang() {
        return lintang;
    }

    public void setLintang(String lintang) {
        this.lintang = lintang;
    }

    public String getBujur() {
        return bujur;
    }

    public void setBujur(String bujur) {
        this.bujur = bujur;
    }

    public String getWilayah() {
        return wilayah;
    }

    public void setWilayah(String wilayah) {
        this.wilayah = wilayah;
    }
}