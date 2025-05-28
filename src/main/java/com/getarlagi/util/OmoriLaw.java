package com.getarlagi.util;

public class OmoriLaw {
    /**
     * Predicts the number of aftershocks based on Omori's Law.
     * This is a dummy/simplified implementation. In a real application,
     * you would use the exact parameters (K, c, p) of Omori's Law and the precise formula.
     *
     * Basic Omori's Law formula: N(t) = K / (t + c)^p
     * Where:
     * N(t) = Rate of aftershock occurrence at time t
     * K, c, p = Constants dependent on the region and mainshock
     * t = Time since the mainshock
     *
     * For demonstration purposes, we will use a very simplified model.
     *
     * @param mainshockMagnitude Mainshock magnitude.
     * @param durationHours Time duration in hours for prediction.
     * @return Estimated number of aftershocks within the durationHours period.
     */
    public static int predictAftershockCount(double mainshockMagnitude, double durationHours) {
        // This is a very simplified model and not based on actual Omori calculations.
        // You need to replace this with a more accurate implementation.
        double baseCount = 10; // Base number of aftershocks
        double magnitudeFactor = Math.pow(10, (mainshockMagnitude - 5.0) * 0.8); // Increases exponentially with magnitude
        double timeFactor = Math.log1p(durationHours); // Logarithmic with time

        // The number of aftershocks tends to be higher for larger mainshocks
        // and over longer durations.
        int count = (int) (baseCount * magnitudeFactor * timeFactor);

        // Limit the minimum count to avoid being too low
        return Math.max(count, 1); // At least 1 if there's a prediction
    }
}
