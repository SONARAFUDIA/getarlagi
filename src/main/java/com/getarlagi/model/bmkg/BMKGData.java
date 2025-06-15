package com.getarlagi.model.bmkg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BMKGData {
    @JsonProperty("Infogempa")
    private InfoGempa infoGempa;

    // Getter and Setter
    public InfoGempa getInfoGempa() {
        return infoGempa;
    }

    public void setInfoGempa(InfoGempa infoGempa) {
        this.infoGempa = infoGempa;
    }
}