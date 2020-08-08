package com.sm.client.services.location.optimization;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SimpleOptimizationServiceTest {

    @Test
    public void testMainFlowSimple() {
        List<IntervalOfLocation> intervals = Arrays.asList(
                new IntervalOfLocation(1000, 4000, 10D, new CheckValue(26000)),
                new IntervalOfLocation(1000, 7000, 11D, new CheckValue(0)),
                new IntervalOfLocation(1000, 8000, 12D, new CheckValue(0)),
                new IntervalOfLocation(1000, 3000, 13D, new CheckValue(0)),
                new IntervalOfLocation(1000, 20000, 14D, new CheckValue(13000)),
                new IntervalOfLocation(1000, 2000, 10D, new CheckValue(29000)),
                new IntervalOfLocation(1000, 5000, 11D, new CheckValue(2000)),
                new IntervalOfLocation(1000, 6000, 12D, new CheckValue(5000)),
                new IntervalOfLocation(1000, 0, 13D, new CheckValue(6000))
        );
        SimpleOptimizationService.clculate(intervals, 4000, 30000);
        long energy = 4000;
        int i = 0;
        for (IntervalOfLocation intervalOfLocation : intervals) {
            energy = energy + intervalOfLocation.getCharge();
            System.out.println((energy) + ": " + intervalOfLocation.getCharge() + ": " + (intervalOfLocation.getCharge() * intervalOfLocation.getPrice()));
            energy = energy - intervalOfLocation.getNeedEnergy();
            ((CheckValue) intervalOfLocation.getRefObject()).check(intervalOfLocation.getCharge(), i++);
        }
    }

    private static class CheckValue {
        private long expected;

        public CheckValue(long expected) {
            this.expected = expected;
        }

        public void check(long actual, int index) {
            Assert.assertEquals("failed on checker " + index, expected, actual);
        }
    }
}
