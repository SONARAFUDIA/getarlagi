package com.getarlagi.util;

import java.util.Random;

public class DistanceCalculator {
    private static final Random random = new Random();

    /**
     * Estimates the distance of an aftershock from the mainshock in kilometers.
     * This is a dummy/simplified implementation. In a real application,
     * you would use a more sophisticated model that considers
     * the size of the rupture area and the dispersion of aftershocks.
     *
     * @param mainshockMagnitude Mainshock magnitude.
     * @return Estimated distance in kilometers.
     */
    public static double estimateDistanceKm(double mainshockMagnitude) {
        // Simple example: the aftershock zone expands with the mainshock magnitude.
        // A rough rule of thumb: aftershocks occur within a radius of ~1-2 times the rupture length.
        // Rupture length (L) can be estimated from magnitude (M) using empirical formulas, e.g.:
        // log10(L) = 0.5 * M - 1.8 (for L in km)

        // Estimate rupture length (in km)
        double ruptureLengthKm = Math.pow(10, (0.5 * mainshockMagnitude - 1.8));

        // Aftershock distance can be 1 to 3 times the rupture length, or wider.
        // We will use a random factor for variation.
        double maxRadiusKm = ruptureLengthKm * 2.0; // Maximum possible radius

        // Ensure a reasonable minimum distance
        if (maxRadiusKm < 10) maxRadiusKm = 10; // At least 10 km for small earthquakes

        // Generate a random distance within that radius
        return random.nextDouble() * maxRadiusKm;
    }
}
