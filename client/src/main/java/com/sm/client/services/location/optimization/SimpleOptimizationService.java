package com.sm.client.services.location.optimization;

import java.util.List;

public class SimpleOptimizationService {

    /**
     * Method will do simplest optimization
     * 1. we are looking for the minimum price location
     * 2. till that location we are charging only if it is necessary
     * 3. on location with minimum price we are charging to max capacity
     * 4. after max charge we are repeating 1. for all location after this one (with minimum price)
     * @param intervals  - intervals objects
     * @param energy  - start energy
     * @param capacity - capacity of resource
     */
    public static void clculate(List<IntervalOfLocation> intervals, long energy, long capacity) {
        int curPos = -1;
        int from = -1;
        while (curPos < intervals.size()) {
            from = curPos + 1;
            curPos = getIndexOfMin(intervals, from);
            if (curPos < 0) {
                break;
            }
            energy = doCalculation(capacity, energy, intervals, from, curPos);
        }
    }

    private static int getIndexOfMin(List<IntervalOfLocation> intervals, int from) {
        int ret = -1;
        double minPrice = Double.MAX_VALUE;
        for (int i = from; i < intervals.size(); i++) {
            IntervalOfLocation intervalOfLocation = intervals.get(i);
            if (minPrice > intervalOfLocation.getPrice()) {
                minPrice = intervalOfLocation.getPrice();
                ret = i;
            }
        }
        return ret;
    }

    //will do calculations up to given index, the given index is the index with min price
    // will return energy which will left after we reach the next after min price point
    private static long doCalculation(long capacity, long energy, List<IntervalOfLocation> intervals, int from, int index) {
        for (int i = from; i < intervals.size() && i < index; i++) {
            IntervalOfLocation intervalOfLocation = intervals.get(i);
            energy = energy - intervalOfLocation.getNeedEnergy();
            if (energy > intervalOfLocation.getMinEnergyLeft()) {
                continue;
            } else {
                intervalOfLocation.setCharge((intervalOfLocation.getMinEnergyLeft() - energy));
                intervalOfLocation.setSummaryPrice(intervalOfLocation.getPrice() * intervalOfLocation.getCharge());
                energy = intervalOfLocation.getMinEnergyLeft();
            }
        }
        IntervalOfLocation intervalOfLocation = intervals.get(index);
        intervalOfLocation.setCharge(capacity - energy);
        return capacity - intervalOfLocation.getNeedEnergy();
    }


}


