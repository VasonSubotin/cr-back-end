package com.sm.ocpp;

import eu.chargetime.ocpp.*;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.SessionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OCPPServer {

    private static final Logger logger = LoggerFactory.getLogger(OCPPServer.class);

    @Value("${sm.ocpp.server.type:JSON}")
    private ServerType type;

    @Value("${sm.ocpp.server.host:localhost}")
    private String host;

    @Value("${sm.ocpp.server.port:80}")
    private int port;

    @Value("${sm.ocpp.server.auth.enable:false}")
    private boolean authEnable;

    //@Value("#{${sm.ocpp.server.auth.creds : {dGVzdDoxMjM0NTY3OA==: '1', key2: '2'}}}")
    @Value("#{'${sm.ocpp.server.auth.creds:dGVzdDoxMjM0NTY3OA==}'.split(',')}")
    private List<String> lCreds;

    private Map<String, Object> credentials;

    @Autowired
    private SmServerCoreEventHandler smServerCoreEventHandler;

    private IServerAPI server;

    @Autowired
    private ScheduleQueue scheduleQueue;

    @PostConstruct
    public void init() {
        credentials = lCreds == null || lCreds.isEmpty() ? null : lCreds.stream().collect(Collectors.toMap(a -> a.split(":")[0], a -> a));
        ServerCoreProfile core = new ServerCoreProfile(smServerCoreEventHandler);
        scheduleQueue.setCore(core);
        JSONConfiguration configuration = JSONConfiguration.get();
        configuration.setParameter(JSONConfiguration.REUSE_ADDR_PARAMETER, true);
        //configuration.setParameter(JSONConfiguration.)
        switch (type) {
            case JSON:
                this.server = new SMOCPServer(core, configuration, authEnable ? credentials : null);
                break;
            case SOAP:
                this.server = new SOAPServer(core);
                break;
            default:
                logger.error("***** Unknown OCPP server type[{}] - can't start OCPP server! *****", type);
        }

        server.open(host, port, new ServerEvents() {

            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {
                scheduleQueue.addSession(sessionIndex, information);
                // sessionIndex is used to send messages.
                System.out.println("New session " + sessionIndex + ": " + information.getIdentifier());
            }

            @Override
            public void lostSession(UUID sessionIndex) {
                scheduleQueue.removeSession(sessionIndex);
                System.out.println("Session " + sessionIndex + " lost connection");
            }
        });
    }

    enum ServerType {
        JSON, SOAP
    }

    public IServerAPI getServer() {
        return server;
    }
}


