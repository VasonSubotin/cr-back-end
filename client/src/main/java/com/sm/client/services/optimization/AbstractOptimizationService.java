package com.sm.client.services.optimization;

import com.sm.client.model.eco.GridData;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.client.model.to.EventInterval;
import com.sm.client.services.EcoService;
import com.sm.client.services.TimeOfUsageService;
import com.sm.client.utils.intervals.Interval;
import com.sm.client.utils.intervals.IntervalTransformerUtils;
import com.sm.client.utils.intervals.IntervalUtils;
import com.sm.model.SmTimeOfUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class AbstractOptimizationService implements OptimizationService {

    private static Logger logger = LoggerFactory.getLogger(AbstractOptimizationService.class);

    // default style , all possible styles can be checked on smartcar site
    // https://www.watttime.org/get-the-data/api-documentation/#/reference/grid-information/determine-region/index
    protected static final String STYLE = "all";

    @Autowired
    protected EcoService ecoService;

    @Autowired
    protected TimeOfUsageService timeOfUsageService;

    /**
     * Method will get grid of CO2, we need it to calculate CO2 impact
     *
     * @param start      - start date in string format like ISO yyyy-MM-ddThh:mm:ssZ
     * @param stop-      stop date in string format like ISO yyyy-MM-ddThh:mm:ssZ
     * @param locationId - locationId of location where we need to calculate CO2
     * @return list of elements which represents  CO2 info
     * @throws Exception
     */
    protected List<GridData> getData(Date start,
                                     Date stop,
                                     String locationId,
                                     Long resourceId) throws Exception {
        List<GridData> co2DataList = ecoService.getEcoData(locationId, null, null, start, stop, null, STYLE);

        if (co2DataList == null || co2DataList.isEmpty()) {
            logger.error("No grid data found for the specified time range {} - {} and location {}", start, stop, locationId);
            throw new Exception("No grid data found for the specified time range " + start + " - " + stop + " and location " + locationId);
        }
        SmTimeOfUsage timeOfUsage = timeOfUsageService.getTimeOfUsageByResourceId(resourceId);
        if (timeOfUsage != null) {
            List<Interval> tousInterval = IntervalTransformerUtils.touToIntervals(timeOfUsage, start);
            if (tousInterval != null && !tousInterval.isEmpty()) {
                List<Interval> gridsInterval = IntervalTransformerUtils.gridDataListToIntervals(co2DataList);
                // applying tous
                List<Interval> gridIntervalAfterTou = IntervalUtils.intersection(gridsInterval, tousInterval);
                //restoring grids according new intervals
                co2DataList = new ArrayList<>();
                for (Interval<GridData> interval : gridIntervalAfterTou) {
                    GridData cloned = IntervalTransformerUtils.cloneGridData(interval.getData());
                    cloned.setStart(interval.getStart());
                    cloned.setStop(interval.getStop());
                    co2DataList.add(cloned);
                }
            }
        }
        return co2DataList;
    }

    /**
     * Method will modify incoming gridDataList by excluding intervals from events
     *
     * @param events       - intervals that should be excluded
     * @param gridDataList - list of grid elements which will be modified
     * @return will return modified list
     */
    protected List<GridData> applyEvents(List<EventInterval> events, List<GridData> gridDataList) {
        if (events == null || events.isEmpty() || gridDataList == null || gridDataList.isEmpty()) {
            return gridDataList;
        }

        List<GridData> gridDataListOut = new ArrayList<>();
        int currentEvent = 0;

        EventInterval eventInterval = events.get(currentEvent);

        //building tree for comparation;
        for (GridData grd : gridDataList) {
            if (events.size() > currentEvent) {
                if (eventInterval.getStart() < grd.getStart() && grd.getStop() < eventInterval.getStop()) {
                    //ignoring elements inside of interval
                    continue;
                }

                if (grd.getStart() < eventInterval.getStart() && eventInterval.getStart() < grd.getStop()) {
                    //cross beginning , need to split
                    grd.setStop(eventInterval.getStart());
                }

                if (grd.getStart() < eventInterval.getStop() && eventInterval.getStop() < grd.getStop()) {
                    //cross ending , need to split
                    grd.setStart(eventInterval.getStart());
                }

                if (eventInterval.getStop() < grd.getStop()) {
                    currentEvent++;
                    if (events.size() > currentEvent) {
                        eventInterval = events.get(currentEvent);
                    }
                }

                gridDataListOut.add(grd);
            } else {
                gridDataListOut.add(grd);
            }
        }
        return gridDataListOut;
    }


    /**
     * Method calculates CO2 impact based on array of incoming data and time range timeInMinsNeed without any optimization
     *
     * @param co2DataList    - sorted list of elements with value of co2
     * @param timeInMinsNeed - time range in minutes for changring
     * @return Summary CO2 impact
     */
    protected double calcSimpleCO2Impact(List<GridData> co2DataList, long timeInMinsNeed) {
        double coImpactSummary = 0;
        long timeInMins = timeInMinsNeed;
        for (GridData gridData : co2DataList) {
            timeInMins -= (gridData.getStop() - gridData.getStart()) / 60000;
            if (timeInMins < 0) {
                double curImpact = (gridData.getStop() - gridData.getStart() + timeInMins * 60000) / 3600000D * gridData.getValue();
                coImpactSummary += curImpact;
                break;
            } else {
                double curImpact = (gridData.getStop() - gridData.getStart()) / 3600000D * gridData.getValue();
                coImpactSummary += curImpact;
            }
        }

        return coImpactSummary;
    }


    protected SchedulerData calculateSchedulerData(List<GridData> originalDataList, Collection<GridData> optimizesDataList, long rateInWt, long chargeTimeInMins) {
        SchedulerData result = new SchedulerData();
        List<SchedulerInterval> intervals = new ArrayList<>();
        result.setIntervals(intervals);

        GridData lastGridData = null;
        SchedulerInterval current = new SchedulerInterval();

        double coImpactSummary = 0;
        // generating final result
        for (GridData gridData : optimizesDataList) {
            if (lastGridData == null || lastGridData.getStop() != gridData.getStart()) {
                current = new SchedulerInterval();
                current.setStartTime(gridData.getPointTime());
                current.setDuration(0);
                current.setDuration(0);
                current.setChargeRate(rateInWt);
                intervals.add(current);
            }

            current.setDuration(current.getDuration() + gridData.getStop() - gridData.getStart());
            double curImpact = (gridData.getStop() - gridData.getStart()) / 3600000D * gridData.getValue();
            current.setCo2Impact(current.getCo2Impact() + curImpact);
            current.setIntervalType(SchedulerInterval.IntervalType.CHR);
            coImpactSummary += curImpact;
            lastGridData = gridData;
        }
        result.setCo2Impact(coImpactSummary);

        //calculating simple usage
        result.setCo2_savings(calcSimpleCO2Impact(originalDataList, chargeTimeInMins) - coImpactSummary);
        return result;
    }

    protected static final Comparator<GridData> gridDataValueComparator = (a, b) -> {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        if (a.getValue() == b.getValue()) {
            return 0;
        }

        return a.getValue().compareTo(b.getValue());
    };

    public void setEcoService(EcoService ecoService) {
        this.ecoService = ecoService;
    }

    public void setTimeOfUsageService(TimeOfUsageService timeOfUsageService) {
        this.timeOfUsageService = timeOfUsageService;
    }
}
