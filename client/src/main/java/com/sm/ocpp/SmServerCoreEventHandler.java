package com.sm.ocpp;

import com.sm.ocpp.to.OCPPScheduleData;
import com.sm.ocpp.to.OCPPScheduleEvent;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.model.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@Component
public class SmServerCoreEventHandler implements ServerCoreEventHandler {

    @Autowired
    private ScheduleQueue scheduleQueue;

    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID uuid, AuthorizeRequest authorizeRequest) {
        return new AuthorizeConfirmation();
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID uuid, BootNotificationRequest bootNotificationRequest) {
        Map<String, OCPPScheduleData> mp = scheduleQueue.getSchedulerDataMap();

        OCPPScheduleData ocppScheduleData = new OCPPScheduleData(uuid, 1L, scheduleQueue.getIdentifierBySessionId(uuid), 7);
        LinkedList<OCPPScheduleEvent> events = ocppScheduleData.getEvents();
        int i = 1;
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));

        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));

        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));

        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));

        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));
        events.add(new OCPPScheduleEvent(new Date(System.currentTimeMillis() + 300_000 * (i++)), i % 2 == 0 ? true : false));

        mp.put(ocppScheduleData.getExtrenalId(), ocppScheduleData);
        return new BootNotificationConfirmation();
    }

    @Override
    public DataTransferConfirmation handleDataTransferRequest(UUID uuid, DataTransferRequest dataTransferRequest) {
        return new DataTransferConfirmation();
    }

    @Override
    public HeartbeatConfirmation handleHeartbeatRequest(UUID uuid, HeartbeatRequest heartbeatRequest) {
        return new HeartbeatConfirmation();
    }

    @Override
    public MeterValuesConfirmation handleMeterValuesRequest(UUID uuid, MeterValuesRequest meterValuesRequest) {
        return new MeterValuesConfirmation();
    }

    @Override
    public StartTransactionConfirmation handleStartTransactionRequest(UUID uuid, StartTransactionRequest startTransactionRequest) {
        return new StartTransactionConfirmation();
    }

    @Override
    public StatusNotificationConfirmation handleStatusNotificationRequest(UUID uuid, StatusNotificationRequest statusNotificationRequest) {
        return new StatusNotificationConfirmation();
    }

    @Override
    public StopTransactionConfirmation handleStopTransactionRequest(UUID uuid, StopTransactionRequest stopTransactionRequest) {
        return new StopTransactionConfirmation();
    }

}
