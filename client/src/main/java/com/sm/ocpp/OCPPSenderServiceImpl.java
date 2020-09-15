package com.sm.ocpp;

import com.sm.ocpp.to.OCPPScheduleData;
import com.sm.ocpp.to.OCPPScheduleEvent;
import eu.chargetime.ocpp.IServerAPI;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Scope("singleton")
public class OCPPSenderServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(OCPPSenderServiceImpl.class);

    @Autowired
    private OCPPServer ocppServer;

    @Autowired
    private ScheduleQueue scheduleQueue;

    private IServerAPI server;

    @PostConstruct
    public void init() {
        this.server = ocppServer.getServer();
    }


    @Scheduled(fixedDelayString = "${interval.check.delay.seconds:300}000")
    private void processSingleTick() {

        List<OCPPScheduleData> schedules = new ArrayList<>();
        schedules.addAll(scheduleQueue.getSchedulerDataMap().values());

        for (OCPPScheduleData schedulerData : schedules) {
            //for each object checking intervals and if it is time then sending apropreate commands
            if (schedulerData.getEvents() == null || schedulerData.getEvents().isEmpty()) {
                logger.info("no interval was found in schedule for resource={}", schedulerData.getResourceId());
                // removing from map
                scheduleQueue.getSchedulerDataMap().remove(schedulerData.getExtrenalId());
                continue;
            }
            Iterator<OCPPScheduleEvent> eventIterator = schedulerData.getEvents().iterator();
            while (eventIterator.hasNext()) {
                OCPPScheduleEvent event = eventIterator.next();

                if (event.getDate() == null) {
                    logger.info("no date was found in interval for schedule for resource={} - interval will be removed ", schedulerData.getResourceId());
                    eventIterator.remove();
                    continue;
                }

                if (event.getDate().after(new Date())) {
                    // sending request
                    try {
                        sendRequest(event, schedulerData.getUuid(), schedulerData.getConnectors());
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                    eventIterator.remove();
                    break;
                }
            }
        }
    }

    private void sendRequest(OCPPScheduleEvent event, UUID uuid, int connectors) throws NotConnectedException, OccurenceConstraintException, UnsupportedFeatureException {
        for (int i = 1; i <= connectors; i++) {
            server.send(uuid, scheduleQueue.getCore().createChangeAvailabilityRequest(event.isEnable() ? AvailabilityType.Operative : AvailabilityType.Inoperative, i));
        }
    }
}
