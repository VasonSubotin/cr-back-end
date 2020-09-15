package com.sm.ocpp;


import com.sm.ocpp.to.OCPPScheduleData;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.SessionInformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope("singleton")
public class ScheduleQueue {

    private ServerCoreProfile core;

    private Map<UUID, SessionInformation> sessionInformationMap = new ConcurrentHashMap<>();

    private Map<String, OCPPScheduleData> schedulerDataMap = new ConcurrentHashMap<>();

    public Map<String, OCPPScheduleData> getSchedulerDataMap() {
        return schedulerDataMap;
    }

    public void addSession(UUID uuid, SessionInformation sessionInformation) {
        sessionInformationMap.put(uuid, sessionInformation);
    }

    public void removeSession(UUID uuid) {
        sessionInformationMap.remove(uuid);
    }

    public String getIdentifierBySessionId(UUID uuid) {
        // getting the last section of url
        String url = sessionInformationMap.get(uuid).getIdentifier();
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public ServerCoreProfile getCore() {
        return core;
    }

    public void setCore(ServerCoreProfile core) {
        this.core = core;
    }
}
