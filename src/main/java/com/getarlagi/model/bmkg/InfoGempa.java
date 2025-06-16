package com.getarlagi.model.bmkg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InfoGempa {
    @JsonProperty("gempa")
    private Gempa gempa;

    // Getter and Setter
    public Gempa getGempa() {
        return gempa;
    }

    public void setGempa(Gempa gempa) {
        this.gempa = gempa;
    }
}