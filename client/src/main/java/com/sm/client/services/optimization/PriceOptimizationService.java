package com.sm.client.services.optimization;

import com.sm.model.Pair;
import com.sm.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.utils.StringDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service("PriceOptimizationService")
public class PriceOptimizationService extends SimpleOptimizationService {

    @Override
    protected Pair<List<GridData>, List<GridData>> getData(Date start,
                                                           Date stop,
                                                           String locationId,
                                                           Long resourceId,
                                                           TimeZone timeZone) throws Exception {
        Pair<List<GridData>, List<GridData>> p = super.getData(start, stop, locationId, resourceId, timeZone);

        List<GridData> co2DataList = p.getValue();
        co2DataList = applyEvents(ecoService.getEventInterval(resourceId, timeZone), co2DataList);
        return new Pair(p.getKey(), co2DataList);
    }

    @Override
    public PolicyType getPolicy() {
        return PolicyType.PRICE;
    }
}
