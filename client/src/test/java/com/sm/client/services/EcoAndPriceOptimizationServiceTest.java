package com.sm.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.client.model.eco.GridData;
import com.sm.model.SmScheduleType;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.client.model.to.EventInterval;
import com.sm.model.SmSchedules;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EcoAndPriceOptimizationServiceTest {


    @Test
    public void testApplyEvent() {
        List<GridData> gridDataList = new ArrayList<>();

        long base = 10000000;
        long current = base;
        for (int i = 0; i < 20; i++) {
            GridData gridData = new GridData();
            gridData.setStart(current);
            gridData.setStop(current + 300000);
            gridData.setVersion(i + ":" + new Date(gridData.getStart()) + " - " + new Date(gridData.getStop()));
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

        for (EventInterval interval1 : intervalList) {
            System.out.println(new Date(interval1.getStart()) + " - " + new Date(interval1.getStop()));
        }

//        List<GridData> grdResult = new CO2OptimizationService().applyEvents(intervalList, gridDataList);
//        System.out.println(grdResult);
    }

    @Test
    public void test2() throws ParseException, JsonProcessingException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SchedulerData smSchedules =new SchedulerData();

        smSchedules.setIntervals(new ArrayList<>(Arrays.asList(
                generateInterval(sdf.parse("2020-07-15T07:12:35"),3600_000L ),
                generateInterval(sdf.parse("2020-07-15T08:40:00"),1800_000L ),
                generateInterval(sdf.parse("2020-07-15T10:32:15"),1200_000L ),
                generateInterval(sdf.parse("2020-07-15T11:50:35"),3600_000L ),
                generateInterval(sdf.parse("2020-07-15T13:12:35"),3600_000L )
        )));

        ObjectMapper objectMapper = new ObjectMapper();
        ScheduleTransformServiceImpl scheduleTransformService = new ScheduleTransformServiceImpl();

        SmSchedules s= scheduleTransformService.scheduleWebToSmSchedules(smSchedules);
        System.out.println(new String(s.getData()));
    }

    private SchedulerInterval generateInterval(Date start, long length) {

        SchedulerInterval interval1 = new SchedulerInterval();
        interval1.setChargeRate(1000);
        interval1.setStartTime(start);
        interval1.setDuration(length);
        interval1.setCo2Impact(0.9);
        interval1.setSmScheduleType(SmScheduleType.DRV);
        interval1.setPrimaryTrigger("test-trigger");
        return interval1;
    }



}
