package com.sm.client.services.optimization;

import com.sm.client.model.PolicyType;
import com.sm.client.model.eco.GridData;
import com.sm.client.services.EcoService;
import com.sm.client.utils.StringDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("EcoAndPriceOptimizationService")
public class EcoAndPriceOptimizationService extends EcoOptimizationService {
    private static final Logger logger = LoggerFactory.getLogger(EcoAndPriceOptimizationService.class);

    @Autowired
    private EcoService ecoService;


    @Override
    protected List<GridData> getData(String start,
                                     String stop,
                                     String locationId,
                                     boolean mock) throws Exception {
        List<GridData> co2DataList = super.getData(start, stop, locationId, mock);

        if (mock) {
            co2DataList = applyEvents(ecoService.getEventIntervalMock(StringDateUtil.parseDate(start), StringDateUtil.parseDate(stop)), co2DataList);
        }
        return co2DataList;
    }


    @Override
    public PolicyType getPolicy() {
        return PolicyType.ECO_PRICE;
    }

}
