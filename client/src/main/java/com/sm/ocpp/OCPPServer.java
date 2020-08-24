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
import java.util.UUID;

@Service
public class OCPPServer {

    private static final Logger logger = LoggerFactory.getLogger(OCPPServer.class);

    @Value("${sm.ocpp.server.type:JSON}")
    private ServerType type;

    @Value("${sm.ocpp.server.host:localhost}")
    private String host;

    @Value("${sm.ocpp.server.port:80}")
    private int port;

    @Autowired
    private SmServerCoreEventHandler smServerCoreEventHandler;

    private IServerAPI server;
    private ServerCoreProfile core;

    @PostConstruct
    public void init() {
        JSONConfiguration configuration = JSONConfiguration.get();
        configuration.setParameter(JSONConfiguration.REUSE_ADDR_PARAMETER, true);
        //configuration.setParameter(JSONConfiguration.)
        switch (type) {
            case JSON:
                this.server = new JSONServer(new ServerCoreProfile(smServerCoreEventHandler), configuration);
                break;
            case SOAP:
                this.server = new SOAPServer(new ServerCoreProfile(smServerCoreEventHandler));
                break;
            default:
                logger.error("***** Unknown OCPP server type[{}] - can't start OCPP server! *****", type);
        }

        server.open(host, port, new ServerEvents() {

            @Override
            public void newSession(UUID sessionIndex, SessionInformation information) {

                // sessionIndex is used to send messages.
                System.out.println("New session " + sessionIndex + ": " + information.getIdentifier());
            }

            @Override
            public void lostSession(UUID sessionIndex) {

                System.out.println("Session " + sessionIndex + " lost connection");
            }
        });
    }

    enum ServerType {
        JSON, SOAP
    }
}


