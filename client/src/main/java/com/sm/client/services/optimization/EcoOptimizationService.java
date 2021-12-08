package com.sm.client.services.optimization;

import com.sm.model.Pair;
import com.sm.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.smartcar.SchedulerData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("EcoOptimizationService")
public class EcoOptimizationService extends AbstractOptimizationService {

    @Override
    public SchedulerData optimize(Date start, Date stop, long capacityInWt, long chargeLevelInWt, long rateInWt, String locationId, TimeZone timeZone, Long recourceId) throws Exception {

        Pair<List<GridData>, List<GridData>> p = getData(start, stop, locationId, recourceId, timeZone);
        List<GridData> co2DataList = p.getValue();

        List<GridData> co2DataListSorted = new ArrayList<>(co2DataList);
        co2DataListSorted.sort(gridDataValueComparator);

        //calculating time
        long timeInMinsNeed = (60 * (capacityInWt - chargeLevelInWt)) / rateInWt;

        // will be used for end date fix
        long timeInMins = timeInMinsNeed;

        //will store date sorted data
        TreeSet<GridData> timeSortedResult = new TreeSet<>(Comparator.comparing(GridData::getPointTime));

        // we don't expect that time period may be different but just in case calculating it
        for (GridData gridData : co2DataListSorted) {
            timeSortedResult.add(gridData);
            timeInMins -= (gridData.getStop() - gridData.getStart()) / 60000;
            if (timeInMins < 0) {
                //fixing the last element stop date <-- this is in case if the last element should be less then it is
                gridData.setStop(gridData.getStop() + timeInMins * 60000);
                break;
            }
        }
        SchedulerData schedulerData = calculateSchedulerData(co2DataList, timeSortedResult, rateInWt, timeInMinsNeed);
        schedulerData.setInitialEnergy(chargeLevelInWt);
        schedulerData.setCapacity(capacityInWt);
        schedulerData.setMoers(aggregateGridData(schedulerData.getIntervals(), p.getKey()));
        return schedulerData;
    }

    @Override
    public PolicyType getPolicy() {
        return PolicyType.ECO;
    }
}
