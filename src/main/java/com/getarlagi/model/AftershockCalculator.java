package com.getarlagi.model;

import com.getarlagi.util.OmoriLaw;
import com.getarlagi.util.GutenbergRichterLaw;
import com.getarlagi.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class AftershockCalculator {

    /**
     * Calculates and predicts aftershocks based on the mainshock.
     * Uses Omori's Law, Gutenberg-Richter, and distance estimation.
     *
     * @param mainshock The Mainshock object as a reference.
     * @param durationHours The duration (in hours) to predict aftershocks.
     * @param minMagnitude The minimum magnitude to predict for aftershocks.
     * @return A list of predicted Aftershock objects.
     */
    public List<Aftershock> calculateAftershocks(Mainshock mainshock, double durationHours, double minMagnitude) {
        List<Aftershock> predictedAftershocks = new ArrayList<>();

        // 1. Estimate the number of aftershocks using Omori's Law
        int numPredictedAftershocks = OmoriLaw.predictAftershockCount(mainshock.getMagnitude(), durationHours);

        // 2. Iterate for each predicted aftershock
        for (int i = 0; i < numPredictedAftershocks; i++) {
            // Estimate aftershock magnitude
            double estimatedMagnitude = GutenbergRichterLaw.estimateAftershockMagnitude(mainshock.getMagnitude());

            // Skip if the estimated magnitude is below the minimum threshold
            if (estimatedMagnitude < minMagnitude) {
                continue;
            }

            // Estimate the distance of the aftershock from the mainshock
            double estimatedDistance = DistanceCalculator.estimateDistanceKm(mainshock.getMagnitude());

            // Estimate the occurrence time of the aftershock (within the specified duration)
            // This is a random estimation within the duration
            long minutesToAdd = (long) (Math.random() * durationHours * 60);
            LocalDateTime estimatedTime = mainshock.getTimestamp().plusMinutes(minutesToAdd);

            // Create a new Aftershock object
            // For latitude/longitude, we introduce a small variation from the mainshock
            // so that each aftershock has a slightly different position
            Aftershock aftershock = new Aftershock(
                mainshock.getLatitude() + (Math.random() - 0.5) * (estimatedDistance / 111.0), // ~1 deg lat = 111km
                mainshock.getLongitude() + (Math.random() - 0.5) * (estimatedDistance / (111.0 * Math.cos(Math.toRadians(mainshock.getLatitude())))),
                estimatedMagnitude,
                estimatedTime,
                "Predicted Location", // Generic location
                estimatedDistance,
                1 // This could be further refined if the Omori model provides a count per aftershock
            );
            predictedAftershocks.add(aftershock);
        }

        return predictedAftershocks;
    }
}
