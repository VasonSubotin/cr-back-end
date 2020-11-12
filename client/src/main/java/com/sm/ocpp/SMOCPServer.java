package com.sm.ocpp;

import eu.chargetime.ocpp.*;
import eu.chargetime.ocpp.feature.profile.Profile;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.wss.BaseWssFactoryBuilder;
import eu.chargetime.ocpp.wss.WssFactoryBuilder;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.protocols.IProtocol;
import org.java_websocket.protocols.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class SMOCPServer implements IServerAPI {
    private static final Logger logger = LoggerFactory.getLogger(eu.chargetime.ocpp.JSONServer.class);
    public final Draft draftOcppOnly;
    private final SmWebSocketListener listener;
    private final Server server;
    private final FeatureRepository featureRepository;
    private JSONConfiguration jsonConfiguration;


    public SMOCPServer(ServerCoreProfile coreProfile, JSONConfiguration configuration, Map<String, Object> credentials) {
        this.featureRepository = new FeatureRepository();
        SessionFactory sessionFactory = new SessionFactory(this.featureRepository);
        ArrayList<IProtocol> protocols = new ArrayList();
        protocols.add(new Protocol("ocpp1.6"));
        // protocols.add(new Protocol(""));
        //this.draftOcppOnly = new Draft_6455(Collections.emptyList(), protocols);
        Draft_6455a draft = new Draft_6455a(Collections.emptyList(), protocols);
        draft.setCredentials(credentials);
        this.draftOcppOnly = draft;

        this.listener = new SmWebSocketListener(sessionFactory, configuration, credentials, new Draft[]{this.draftOcppOnly});
        this.server = new Server(this.listener, this.featureRepository, new PromiseRepository());
        this.featureRepository.addFeatureProfile(coreProfile);
    }

//    public SMOCPServer(ServerCoreProfile coreProfile) {
//        this(coreProfile, JSONConfiguration.get());
//    }
//
//    public SMOCPServer(ServerCoreProfile coreProfile, WssFactoryBuilder wssFactoryBuilder, JSONConfiguration configuration) {
//        this(coreProfile, configuration);
//        this.enableWSS(wssFactoryBuilder);
//    }
//
//    public SMOCPServer(ServerCoreProfile coreProfile, WssFactoryBuilder wssFactoryBuilder) {
//        this(coreProfile, wssFactoryBuilder, JSONConfiguration.get());
//    }

    public void enableWSS(SSLContext sslContext) throws IOException {
        WssFactoryBuilder builder = BaseWssFactoryBuilder.builder().sslContext(sslContext);
        this.enableWSS((WssFactoryBuilder) builder);
    }

    public SMOCPServer enableWSS(WssFactoryBuilder wssFactoryBuilder) {
        wssFactoryBuilder.verify();
        this.listener.enableWSS(wssFactoryBuilder);
        return this;
    }

    public void addFeatureProfile(Profile profile) {
        this.featureRepository.addFeatureProfile(profile);
    }

    public void closeSession(UUID session) {
        this.server.closeSession(session);
    }

    public void open(String host, int port, ServerEvents serverEvents) {
        logger.info("Feature repository: {}", this.featureRepository);
        this.server.open(host, port, serverEvents);
    }

    public void close() {
        this.server.close();
    }

    public boolean isClosed() {
        return this.listener.isClosed();
    }

    public CompletionStage<Confirmation> send(UUID session, Request request) throws OccurenceConstraintException, UnsupportedFeatureException, NotConnectedException {
        return this.server.send(session, request);
    }
}

