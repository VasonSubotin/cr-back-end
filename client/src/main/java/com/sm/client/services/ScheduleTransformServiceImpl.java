package com.sm.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.client.model.smartcar.SchedulerData;
import com.sm.client.model.smartcar.SchedulerInterval;
import com.sm.model.SmSchedules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ScheduleTransformServiceImpl implements ScheduleTransformService {

    private Logger logger = LoggerFactory.getLogger(ScheduleTransformServiceImpl.class);


    private ObjectMapper objectMapper = new ObjectMapper();


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
        schedulerData.setIntervals(objectMapper.readValue(smSchedules.getData(), new TypeReference<List<SchedulerInterval>>(){}));

        return schedulerData;
    }
}
