package com.getarlagi.util;

import java.util.Random;

public class GutenbergRichterLaw {
    private static final Random random = new Random();

    
    public static double estimateAftershockMagnitude(double mainshockMagnitude) {
        double bValue = 1.0;

        double maxAftershockMagnitude = mainshockMagnitude - 0.5;
        double minAftershockMagnitude = 1.5;

        if (maxAftershockMagnitude < minAftershockMagnitude) {
            maxAftershockMagnitude = minAftershockMagnitude;
        }

        double u = random.nextDouble();
        double estimatedMagnitude = maxAftershockMagnitude - (1/bValue) * Math.log10(u * (Math.pow(10, bValue * maxAftershockMagnitude) - Math.pow(10, bValue * minAftershockMagnitude)) + Math.pow(10, bValue * minAftershockMagnitude));

        return Math.max(minAftershockMagnitude, estimatedMagnitude);
    }
}
