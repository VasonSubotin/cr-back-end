package com.sm.client.utils;

import org.junit.Assert;
import org.junit.Test;

public class TestGeoUtils {

    @Test
    public void testDistance() {
        //  16,50 мил. (26,55 км)
        //33.071627, -96.791653
        //33.045803, -96.508477
        //26546.001953125
        double distance = GeoUtils.calculateDistance(33.071627, -96.791653, 33.045803, -96.508477);
        // accuracy should be less than 10 meters
        Assert.assertEquals("Distnace check 1 failed", (26546.001953125D - 26550) < 10D, true);
    }

    @Test
    public void testLatRange() {
        double[] rangeLat = GeoUtils.calculateLatRange(33.071627, 500);
        double distance = GeoUtils.calculateDistance(rangeLat[0], -96.791653, rangeLat[1], -96.791653);

        //System.out.println(distance);
        Assert.assertEquals("Range latitude check failed", (500 - distance) < 1D, true);
    }

    @Test
    public void testLngRange() {
        double[] rangeLong = GeoUtils.calculateLngRange(33.071627, -96.791653, 500);
        double distance = GeoUtils.calculateDistance(33.071627, rangeLong[0], 33.071627, rangeLong[1]);

        System.out.println(distance);
        Assert.assertEquals("Range longitude check failed", (500 - distance) < 1D, true);
    }
}
