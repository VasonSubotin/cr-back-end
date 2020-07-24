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
}
