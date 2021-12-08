package com.sm.client.services.optimization;

import com.sm.model.Pair;
import com.sm.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.model.smartcar.SchedulerData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service("SimpleOptimizationService")
public class SimpleOptimizationService extends AbstractOptimizationService {

    @Override
    public SchedulerData optimize(Date start,
                                  Date stop,
                                  long capacityInWt,
                                  long chargeLevelInWt,
                                  long rateInWt,
                                  String locationId,
                                  TimeZone timeZone,
                                  Long resourceId) throws Exception {
        Pair<List<GridData>, List<GridData>> p = getData(start, stop, locationId, resourceId, timeZone);
        List<GridData> co2DataList = p.getValue();
        //calculating time
        long timeInMinsNeed = (60 * (capacityInWt - chargeLevelInWt)) / rateInWt;
        long timeInMins = timeInMinsNeed;

        List<GridData> optimizedDataList = new ArrayList<>();
        for (GridData gridData : co2DataList) {
            optimizedDataList.add(gridData);
            timeInMins -= (gridData.getStop() - gridData.getStart()) / 60000;
            if (timeInMins < 0) {
                //fixing the last element stop date <-- this is in case if the last element should be less then it is
                gridData.setStop(gridData.getStop() + timeInMins * 60000);
                break;
            }
        }

        SchedulerData schedulerData = calculateSchedulerData(co2DataList, optimizedDataList, rateInWt, timeInMinsNeed);
        schedulerData.setInitialEnergy(chargeLevelInWt);
        schedulerData.setCapacity(capacityInWt);
        schedulerData.setMoers(aggregateGridData(schedulerData.getIntervals(), p.getKey()));
        return schedulerData;
    }

    @Override
    public PolicyType getPolicy() {
        return PolicyType.SIMPLE;
    }
}
