package com.getarlagi.model;

import com.getarlagi.util.OmoriLaw;
import com.getarlagi.util.GutenbergRichterLaw;
import com.getarlagi.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class AftershockCalculator {

    public List<Aftershock> calculateAftershocks(Mainshock mainshock, double durationHours, double minMagnitude) {
        List<Aftershock> predictedAftershocks = new ArrayList<>();

        int numPredictedAftershocks = OmoriLaw.predictAftershockCount(mainshock.getMagnitude(), durationHours);

        for (int i = 0; i < numPredictedAftershocks; i++) {
            double estimatedMagnitude = GutenbergRichterLaw.estimateAftershockMagnitude(mainshock.getMagnitude());

            if (estimatedMagnitude < minMagnitude) {
                continue;
            }

            double estimatedDistance = DistanceCalculator.estimateDistanceKm(mainshock.getMagnitude());

            long minutesToAdd = (long) (Math.random() * durationHours * 60);
            LocalDateTime estimatedTime = mainshock.getTimestamp().plusMinutes(minutesToAdd);

            Aftershock aftershock = new Aftershock(
                mainshock.getLatitude() + (Math.random() - 0.5) * (estimatedDistance / 111.0),
                mainshock.getLongitude() + (Math.random() - 0.5) * (estimatedDistance / (111.0 * Math.cos(Math.toRadians(mainshock.getLatitude())))),
                estimatedMagnitude, estimatedTime,"Predicted Location", estimatedDistance, 1 
            );
            predictedAftershocks.add(aftershock);
        }

        return predictedAftershocks;
    }
}
