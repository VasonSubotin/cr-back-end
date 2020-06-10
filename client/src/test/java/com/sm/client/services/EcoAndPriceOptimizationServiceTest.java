package com.sm.client.services;

import com.sm.client.model.eco.GridData;
import com.sm.client.model.to.EventInterval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EcoAndPriceOptimizationServiceTest {


    @Test
    public void testApplyEvent() {
        List<GridData> gridDataList = new ArrayList<>();

        long base =  10000000;
        long current = base;
        for (int i = 0; i < 20; i++) {
            GridData gridData = new GridData();
            gridData.setStart(current);
            gridData.setStop(current + 300000);
            gridData.setVersion(i+":" + new Date(gridData.getStart()) + " - " + new Date(gridData.getStop()));
            gridDataList.add(gridData);
            current += 600000;
        }

        List<EventInterval> intervalList = new ArrayList<>();
        EventInterval interval = new EventInterval();
        interval.setStart(base + 400_000);
        interval.setStop(base + 400_000 + 3000_000);

        interval.setDuration(3000_000);
        intervalList.add(interval);

        interval = new EventInterval();
        interval.setStart(base + 4500_000);
        interval.setStop(base + 4560_000);
        interval.setDuration(60000);
        intervalList.add(interval);


        interval = new EventInterval();
        interval.setStart(base + 5500_000);
        interval.setStop(base + 5500_000 + 300_000);
        interval.setDuration(300_000);
        intervalList.add(interval);

        for(EventInterval interval1 : intervalList){
            System.out.println(new Date(interval1.getStart()) + " - " + new Date(interval1.getStop()));
        }

//        List<GridData> grdResult = new CO2OptimizationService().applyEvents(intervalList, gridDataList);
//        System.out.println(grdResult);
    }
}
