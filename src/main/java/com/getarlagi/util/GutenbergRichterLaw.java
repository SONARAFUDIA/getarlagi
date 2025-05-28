package com.getarlagi.util;

import java.util.Random;

public class GutenbergRichterLaw {
    private static final Random random = new Random();

    /**
     * Estimates the magnitude of an aftershock based on the Gutenberg-Richter Law.
     * This is a dummy/simplified implementation. In a real application,
     * you would use the exact parameters (a and b) and generate magnitudes
     * from the appropriate probability distribution.
     *
     * Gutenberg-Richter Law: log10(N) = a - bM
     * Where:
     * N = Number of earthquakes with magnitude M or greater
     * a, b = Constants (b-value is generally around 1.0)
     *
     * @param mainshockMagnitude Mainshock magnitude.
     * @return Estimated aftershock magnitude.
     */
    public static double estimateAftershockMagnitude(double mainshockMagnitude) {
        // Aftershock magnitudes are generally smaller than the mainshock.
        // The magnitude distribution after a mainshock tends to follow Gutenberg-Richter.

        // Assume a standard b-value (around 1.0)
        double bValue = 1.0;

        // Maximum possible magnitude for an aftershock (slightly below mainshock)
        double maxAftershockMagnitude = mainshockMagnitude - 0.5;
        // Realistic minimum magnitude for relevant aftershocks
        double minAftershockMagnitude = 1.5; // Limit to avoid being too small

        if (maxAftershockMagnitude < minAftershockMagnitude) {
            maxAftershockMagnitude = minAftershockMagnitude; // Ensure a valid range
        }

        // Generate a random magnitude, with a bias towards smaller magnitudes
        // This is a very simple way to simulate the G-R distribution,
        // where smaller earthquakes are much more frequent.
        // More advanced logic would involve the inverse CDF of an exponential distribution.
        double u = random.nextDouble(); // Random number between 0.0 and 1.0
        double estimatedMagnitude = maxAftershockMagnitude - (1/bValue) * Math.log10(u * (Math.pow(10, bValue * maxAftershockMagnitude) - Math.pow(10, bValue * minAftershockMagnitude)) + Math.pow(10, bValue * minAftershockMagnitude));

        return Math.max(minAftershockMagnitude, estimatedMagnitude); // Ensure not lower than realistic minimum
    }
}
