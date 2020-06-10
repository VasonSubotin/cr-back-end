package com.sm.client.services.optimization;

import com.sm.client.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.smartcar.SchedulerData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Service("EcoOptimizationService")
public class EcoOptimizationService extends AbstractOptimizationService {

    @Override
    public SchedulerData optimize(String start, String stop, long capacityInWt, long chargeLevelInWt, long rateInWt, String locationId, boolean mock) throws Exception {

        List<GridData> co2DataList = getData(start, stop, locationId, mock);

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

        return calculateSchedulerData(co2DataList, timeSortedResult, rateInWt, timeInMinsNeed);
    }

    @Override
    public PolicyType getPolicy() {
        return PolicyType.ECO;
    }
}
