package com.sm.client.utils.intervals;

import com.sm.client.model.eco.GridData;
import com.sm.client.utils.StringDateUtil;
import com.sm.model.SmDREvent;
import com.sm.model.SmTimeOfUsage;

import java.util.*;

public class IntervalTransformerUtils {

    public static List<Interval> drEventsToIntervals(List<SmDREvent> drEvents, Date relativeDate) {
        List<Interval> ret = new ArrayList<>();
        for (SmDREvent drEvent : drEvents) {
            ret.addAll(drEventToIntervals(drEvent, relativeDate));
        }
        return ret;
    }

    public static List<Interval> drEventToIntervals(SmDREvent drEvent, Date relativeDate) {
        List<Interval> ret = new ArrayList<>();
        long start = StringDateUtil.setTimeFromMinutesOfDay(relativeDate, drEvent.getStart()).getTime();
        long stop = 0;
        if (drEvent.getStart() > drEvent.getStop()) {
            stop = start + (24 * 60 + drEvent.getStop() - drEvent.getStart()) * 60_000;
        } else {
            stop = start + (drEvent.getStop() - drEvent.getStart()) * 60_000;
        }

        ret.add(new Interval(start, stop, drEvent));
        // just in case adding extra day
        ret.add(new Interval(start + 24 * 3600_000, stop + 24 * 3600_000, drEvent));
        return ret;
    }

    public static List<Interval> tousToIntervals(List<SmTimeOfUsage> tous, Date relativeDate) {
        List<Interval> ret = new ArrayList<>();
        for (SmTimeOfUsage tou : tous) {
            ret.addAll(touToIntervals(tou, relativeDate));
        }
        return ret;
    }

    public static List<Interval> touToIntervals(SmTimeOfUsage smTimeOfUsage, Date relativeDate) {
        if (smTimeOfUsage == null) {
            return Arrays.asList();
        }
        List<Interval> ret = new ArrayList<>();
        long start = StringDateUtil.setTimeFromMinutesOfDay(relativeDate, smTimeOfUsage.getStart()).getTime();
        long stop = 0;
        if (smTimeOfUsage.getStart() > smTimeOfUsage.getStop()) {
            stop = start + (24 * 60 + smTimeOfUsage.getStop() - smTimeOfUsage.getStart()) * 60_000;
        } else {
            stop = start + (smTimeOfUsage.getStop() - smTimeOfUsage.getStart()) * 60_000;
        }

        ret.add(new Interval(start, stop, smTimeOfUsage));
        // just in case adding extra day
        ret.add(new Interval(start + 24 * 3600_000, stop + 24 * 3600_000, smTimeOfUsage));
        return ret;
    }

    public static List<Interval> gridDataListToIntervals(List<GridData> gridDatas) {
        List<Interval> gridDataIntervals = new ArrayList<>();
        for (GridData gridData : gridDatas) {
            Interval<GridData> gridDataInterval = gridDataToIntervals(gridData);
            if (gridDataInterval != null) {
                gridDataIntervals.add(gridDataInterval);
            }
        }
        return gridDataIntervals;
    }

    public static Interval gridDataToIntervals(GridData gridData) {
        if (gridData == null) {
            return null;
        }

        return new Interval(gridData.getStart(), gridData.getStop(), gridData);
    }


    public static GridData cloneGridData(GridData original) {
        GridData copy = new GridData();
        copy.setValue(original.getValue());
        copy.setFrequence(original.getFrequence());
        copy.setPointTime(original.getPointTime());
        copy.setMarket(original.getMarket());
        copy.setVersion(original.getVersion());
        copy.setLocationId(original.getLocationId());
        copy.setStart(original.getStart());
        copy.setStop(original.getStop());
        copy.setDatatype(original.getDatatype());
        return copy;
    }
}
