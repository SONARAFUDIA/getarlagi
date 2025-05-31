package com.getarlagi.util;

import java.util.Random;

public class DistanceCalculator {
    private static final Random random = new Random();

    
    public static double estimateDistanceKm(double mainshockMagnitude) {
        double ruptureLengthKm = Math.pow(10, (0.5 * mainshockMagnitude - 1.8));

        double maxRadiusKm = ruptureLengthKm * 2.0; 

        if (maxRadiusKm < 10) maxRadiusKm = 10;

        return random.nextDouble() * maxRadiusKm;
    }
}
