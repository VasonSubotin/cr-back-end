package com.sm.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.dao.ResourcesDao;
import com.sm.model.SmResource;
import com.sm.model.SmSchedules;
import com.sm.ocpp.to.OCPPScheduleData;
import com.sm.ocpp.to.OCPPScheduleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ScheduleTransformServiceImpl implements ScheduleTransformService {

    private Logger logger = LoggerFactory.getLogger(ScheduleTransformServiceImpl.class);


    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ResourcesDao resourcesDao;

    @Override
    public SmSchedules scheduleWebToSmSchedules(SchedulerData schedulerData) throws JsonProcessingException {
        if (schedulerData == null) {
            return null;
        }

        SmSchedules smSchedules = new SmSchedules();

        smSchedules.setSessionId(schedulerData.getSessionId());
        smSchedules.setAccountId(schedulerData.getAccountId());
        smSchedules.setCarbonImpact(schedulerData.getCo2Impact());
        smSchedules.setCarbonSavings(schedulerData.getCo2_savings());
        smSchedules.setFinanceSavings(schedulerData.getFinanceSavings());
        smSchedules.setDtCreated(new Date());
        smSchedules.setDtStart(schedulerData.getTimeStart());
        smSchedules.setDtStop(schedulerData.getTimeStop());
        smSchedules.setPolicyId(schedulerData.getPolicyId());
        smSchedules.setDtCreated(schedulerData.getCreatedTime());
        smSchedules.setLocationId(schedulerData.getLocationId());
        smSchedules.setResourceId(schedulerData.getResourceId());
        smSchedules.setInitEnergy(schedulerData.getInitialEnergy());
        smSchedules.setScheduleType(schedulerData.getScheduleType());
        smSchedules.setData(objectMapper.writeValueAsBytes(schedulerData.getIntervals()));
        return smSchedules;
    }

    @Override
    public SchedulerData smSchedulesToScheduleWeb(SmSchedules smSchedules) throws IOException {
        SchedulerData schedulerData = new SchedulerData();
        schedulerData.setCo2_savings(smSchedules.getCarbonSavings());
        schedulerData.setCo2Impact(smSchedules.getCarbonImpact());
        schedulerData.setLocationId(smSchedules.getLocationId());
        schedulerData.setAccountId(smSchedules.getAccountId());
        schedulerData.setPolicyId(smSchedules.getPolicyId());
        schedulerData.setFinanceSavings(smSchedules.getFinanceSavings());
        schedulerData.setResourceId(smSchedules.getResourceId());
        schedulerData.setSessionId(smSchedules.getSessionId());
        schedulerData.setTimeStart(smSchedules.getDtStart());
        schedulerData.setTimeStop(smSchedules.getDtStop());
        schedulerData.setSchedulerId(smSchedules.getIdSchedule());
        schedulerData.setCreatedTime(smSchedules.getDtCreated());
        schedulerData.setInitialEnergy(smSchedules.getInitEnergy());
        schedulerData.setScheduleType(smSchedules.getScheduleType());
        if (smSchedules.getData() != null) {
            schedulerData.setIntervals(objectMapper.readValue(smSchedules.getData(), new TypeReference<List<SchedulerInterval>>() {
            }));
        }
        return schedulerData;
    }

    public OCPPScheduleData SchedulerDataToOCPPScheduleData(SchedulerData schedulerData, UUID uuid) {
        SmResource smResource = resourcesDao.getResourceByIdAndAccountId(schedulerData.getResourceId(), schedulerData.getAccountId());
        OCPPScheduleData ocppScheduleData = new OCPPScheduleData(uuid, schedulerData.getResourceId(), smResource.getExternalResourceId(), 7);

        if (schedulerData.getIntervals() == null) {
            return ocppScheduleData;
        }
        LinkedList<OCPPScheduleEvent> events = ocppScheduleData.getEvents();
        for (SchedulerInterval schedulerInterval : schedulerData.getIntervals()) {
            events.add(new OCPPScheduleEvent(schedulerInterval.getStartTime(), true));
            events.add(new OCPPScheduleEvent(new Date(schedulerInterval.getStartTime().getTime() + schedulerInterval.getDuration()), false));
        }
        return ocppScheduleData;
    }
}
