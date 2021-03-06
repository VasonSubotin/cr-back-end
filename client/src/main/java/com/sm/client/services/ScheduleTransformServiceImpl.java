package com.sm.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.client.model.eco.GridDataAggregated;
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
        smSchedules.setEndSoc(schedulerData.getEndSoc());
        smSchedules.setCapacity(schedulerData.getCapacity());
        smSchedules.setData(objectMapper.writeValueAsBytes(new BBData(schedulerData.getIntervals(), schedulerData.getMoers())));
        return smSchedules;
    }


    @Override
    public SchedulerData smSchedulesToScheduleWeb(SmSchedules smSchedules) throws IOException {
        if (smSchedules == null) {
            return null;
        }
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
        schedulerData.setEndSoc(smSchedules.getEndSoc());
        schedulerData.setCapacity(smSchedules.getCapacity());
        schedulerData.setTotalEnergy(schedulerData.getInitialEnergy() == null || schedulerData.getCapacity() == null ? null : schedulerData.getCapacity() - schedulerData.getInitialEnergy());
        if (smSchedules.getData() != null) {
            try {
                BBData bbData = objectMapper.readValue(smSchedules.getData(), BBData.class);
                if (bbData != null) {
                    schedulerData.setIntervals(bbData.getIntervals());
                    schedulerData.setMoers(bbData.getMoers());
                }

            } catch (Exception ex) {
                // failed trying to deserialize in old way
                schedulerData.setIntervals(objectMapper.readValue(smSchedules.getData(), new TypeReference<List<SchedulerInterval>>() {
                }));
            }
            double totalCost = 0;
            long totalEnergy = 0;
            for (SchedulerInterval schedulerInterval : schedulerData.getIntervals()) {
                totalCost += schedulerInterval.getCostOfCharging();
                totalEnergy += schedulerInterval.getEnergy();
            }
            schedulerData.setTotalCost(totalCost);
            schedulerData.setTotalEnergy(totalEnergy);
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

    private static class BBData {
        private List<SchedulerInterval> intervals;
        private GridDataAggregated moers;

        public BBData() {
        }

        public BBData(List<SchedulerInterval> intervals, GridDataAggregated moers) {
            this.intervals = intervals;
            this.moers = moers;
        }

        public List<SchedulerInterval> getIntervals() {
            return intervals;
        }

        public void setIntervals(List<SchedulerInterval> intervals) {
            this.intervals = intervals;
        }

        public GridDataAggregated getMoers() {
            return moers;
        }

        public void setMoers(GridDataAggregated moers) {
            this.moers = moers;
        }
    }
}
