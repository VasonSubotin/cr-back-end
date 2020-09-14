package com.sm.client.services.optimization;

import com.sm.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.utils.StringDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("PriceOptimizationService")
public class PriceOptimizationService extends SimpleOptimizationService {

    @Override
    protected List<GridData> getData(Date start,
                                     Date stop,
                                     String locationId,
                                     Long resourceId) throws Exception {
        List<GridData> co2DataList = super.getData(start, stop, locationId, resourceId);


        co2DataList = applyEvents(ecoService.getEventInterval(resourceId), co2DataList);
        return co2DataList;
    }

    @Override
    public PolicyType getPolicy() {
        return PolicyType.PRICE;
    }
}
