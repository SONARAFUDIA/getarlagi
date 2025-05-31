package com.getarlagi.util;

public class OmoriLaw {
    public static int predictAftershockCount(double mainshockMagnitude, double durationHours) {
        double baseCount = 10;
        double magnitudeFactor = Math.pow(10, (mainshockMagnitude - 5.0) * 0.8); 
        double timeFactor = Math.log1p(durationHours); 

        int count = (int) (baseCount * magnitudeFactor * timeFactor);

        return Math.max(count, 1);
    }
}
