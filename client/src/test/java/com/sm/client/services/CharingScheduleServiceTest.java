package com.sm.client.services;

import com.sm.client.model.eco.GridData;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.calcs.TimeScheduleService;
import com.sm.client.services.calcs.TimeScheduleServiceImpl;
import com.sm.client.services.optimization.EcoAndPriceOptimizationService;
import com.sm.client.services.optimization.EcoOptimizationService;
import com.sm.client.services.optimization.OptimizationServiceFactory;
import com.sm.client.services.optimization.SimpleOptimizationService;
import com.sm.dao.ScheduleDao;
import com.sm.dao.ScheduleDaoImpl;
import com.sm.model.*;
import com.sm.model.cache.Coordinates;
import com.smartcar.sdk.data.VehicleBattery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CharingScheduleServiceTest {

    private static long uniqId;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    TimeScheduleServiceImpl timeScheduleService;
    OptimizationServiceFactory optimizationServiceFactory;
    TimeOfUsageServiceImpl timeOfUsageService;

    @Before
    public void init() throws ParseException, SmException {
        timeScheduleService = new TimeScheduleServiceImpl();
        optimizationServiceFactory = Mockito.mock(OptimizationServiceFactory.class);
        timeScheduleService.setOptimizationServiceFactory(optimizationServiceFactory);


        EcoServiceImpl ecoService = new EcoServiceImpl();
        RestTemplate ecoTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);

        ScheduleDaoImpl scheduleDao = new ScheduleDaoImpl();
        SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
        scheduleDao.setSessionFactory(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(Mockito.mock(Session.class));
        ecoService.setEcoTemplate(ecoTemplate);

        //DREventServiceImpl drEventService = Mockito.mock(DREventServiceImpl.class);

        when(responseEntity.getBody()).thenReturn(generateListGrid());
        when(ecoTemplate.exchange(any(URI.class), Matchers.eq(HttpMethod.GET), any(HttpEntity.class), Matchers.eq(GridData[].class))).thenReturn(responseEntity);

        DREventService drEventService = mock(DREventService.class);
        when(drEventService.getDREventsByResourceId(1L)).thenReturn(Arrays.asList(createDREvent(1, 2), createDREvent(3, 4), createDREvent(6, 8), createDREvent(14, 17)));
        ecoService.setDrEventService(drEventService);
        ecoService.setUrlEcoData("http://test.com");

        timeScheduleService.setEcoService(ecoService);
        timeScheduleService.setScheduleDao(scheduleDao);
        //   ecoTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(buildBearerHttpHeader(currentToken)), GridData[].class);

        timeOfUsageService = Mockito.mock(TimeOfUsageServiceImpl.class);

        SimpleOptimizationService simpleOptimizationService = new SimpleOptimizationService();
        simpleOptimizationService.setEcoService(ecoService);
        simpleOptimizationService.setTimeOfUsageService(timeOfUsageService);
        when(optimizationServiceFactory.getService(simpleOptimizationService.getPolicy())).thenReturn(simpleOptimizationService);


        EcoOptimizationService ecoOptimizationService = new EcoOptimizationService();
        ecoOptimizationService.setEcoService(ecoService);
        ecoOptimizationService.setTimeOfUsageService(timeOfUsageService);
        when(optimizationServiceFactory.getService(ecoOptimizationService.getPolicy())).thenReturn(ecoOptimizationService);

        EcoAndPriceOptimizationService ecoPriceOptimizationService = new EcoAndPriceOptimizationService();
        ecoPriceOptimizationService.setEcoService(ecoService);
        ecoPriceOptimizationService.setTimeOfUsageService(timeOfUsageService);
        when(optimizationServiceFactory.getService(ecoPriceOptimizationService.getPolicy())).thenReturn(ecoPriceOptimizationService);
    }


    @Test
    public void testSimple() throws Exception {
        timeScheduleService.setScheduleTransformService(new ScheduleTransformServiceImpl());
        SmTimeOfUsage timeOfUsage = new SmTimeOfUsage();
        timeOfUsage.setStart(5 * 60L);
        timeOfUsage.setStop(10 * 60L);
        timeOfUsage.setIdTou(1L);
        timeOfUsage.setLocationId(1L);
        timeOfUsage.setResourceId(1L);
        when(timeOfUsageService.getTimeOfUsageByResourceId(1L)).thenReturn(timeOfUsage);

        SchedulerData schedulerData = timeScheduleService.calculateSchedule(generateVehicleData(), generateSmResource(PolicyType.SIMPLE), "2020-09-02T05:00", "2020-09-03T05:00");
        System.out.println(schedulerData);
    }


    @Test
    public void testEco() throws Exception {
        timeScheduleService.setScheduleTransformService(new ScheduleTransformServiceImpl());

        SchedulerData schedulerData = timeScheduleService.calculateSchedule(generateVehicleData(), generateSmResource(PolicyType.ECO), "2020-09-02", "2020-09-03");
        System.out.println(schedulerData);
        System.out.println(schedulerData.getIntervals().get(0).getStarttime());
    }

    @Test
    public void testEcoPrice() throws Exception {
        timeScheduleService.setScheduleTransformService(new ScheduleTransformServiceImpl());

        SchedulerData schedulerData = timeScheduleService.calculateSchedule(generateVehicleData(), generateSmResource(PolicyType.ECO_PRICE), "2020-09-02", "2020-09-03");
        System.out.println(schedulerData);
        System.out.println(schedulerData.getIntervals().get(0).getStarttime());
    }

    private GridData[] generateListGrid() throws ParseException {
        return new GridData[]{
                createGridData(sdf.parse("2020-09-02T00:00:00"), 3600, 10D),
                createGridData(sdf.parse("2020-09-02T01:00:00"), 3600, 11D),
                createGridData(sdf.parse("2020-09-02T02:00:00"), 3600, 12D),
                createGridData(sdf.parse("2020-09-02T03:00:00"), 3600, 13D),
                createGridData(sdf.parse("2020-09-02T04:00:00"), 3600, 14D),
                createGridData(sdf.parse("2020-09-02T05:00:00"), 3600, 15D),
                createGridData(sdf.parse("2020-09-02T06:00:00"), 3600, 16D),
                createGridData(sdf.parse("2020-09-02T07:00:00"), 3600, 17D),
                createGridData(sdf.parse("2020-09-02T08:00:00"), 3600, 10D),
                createGridData(sdf.parse("2020-09-02T09:00:00"), 3600, 1D),
                createGridData(sdf.parse("2020-09-02T10:00:00"), 3600, 2D),
                createGridData(sdf.parse("2020-09-02T11:00:00"), 3600, 20D)
        };
    }

    private SmDREvent createDREvent(long start, long stop) {
        SmDREvent drEvent = new SmDREvent();
        drEvent.setIdDrEvent(getId());
        drEvent.setResourceId(1L);
        drEvent.setStart(start * 60);
        drEvent.setStart(stop * 60);
        return drEvent;
    }

    private VehicleData generateVehicleData() {
        VehicleData smData = new VehicleData();
        smData.setBattery(new VehicleBattery(100, 50));
        smData.setVin("testVin1");
        return smData;
    }

    private SmResource generateSmResource(PolicyType policyType) {
        SmResource smResource = new SmResource();
        smResource.setPolicyId(policyType.getId());
        smResource.setCapacity(30000L);
        smResource.setAccountId(1L);
        smResource.setIdResource(1L);
        smResource.setResourceTypeId(1L);
        smResource.setModel("Tesla-test");
        smResource.setExternalResourceId("testVin1");
        return smResource;
    }

    private GridData createGridData(Date startPoint, long frequency, double value) {
        GridData gridData = new GridData();
        gridData.setLocationId("test-" + System.currentTimeMillis() % 1000);
        gridData.setVersion("test-v1");
        gridData.setMarket("test-market");
        gridData.setPointTime(startPoint);
        gridData.setValue(value);
        gridData.setFrequence(frequency);
        return gridData;
    }

    private static long getId() {
        return uniqId++;
    }
}
