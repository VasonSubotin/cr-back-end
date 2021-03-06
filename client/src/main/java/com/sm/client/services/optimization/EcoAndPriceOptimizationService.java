package com.sm.client.services.optimization;

import com.sm.model.Pair;
import com.sm.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.services.EcoService;
import com.sm.client.utils.StringDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service("EcoAndPriceOptimizationService")
public class EcoAndPriceOptimizationService extends EcoOptimizationService {
    private static final Logger logger = LoggerFactory.getLogger(EcoAndPriceOptimizationService.class);


    @Override
    protected Pair<List<GridData>, List<GridData>> getData(Date start,
                                                           Date stop,
                                                           String locationId,
                                                           Long resourceId) throws Exception {
        Pair<List<GridData>, List<GridData>> p = super.getData(start, stop, locationId, resourceId);

        List<GridData> co2DataList = p.getValue();
        co2DataList = applyEvents(ecoService.getEventInterval(resourceId), co2DataList);
        return new Pair<>(p.getKey(), co2DataList);
    }


    @Override
    public PolicyType getPolicy() {
        return PolicyType.ECO_PRICE;
    }

}
