package com.sm.ocpp;

import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.model.core.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SmServerCoreEventHandler implements ServerCoreEventHandler {
    @Override
    public AuthorizeConfirmation handleAuthorizeRequest(UUID uuid, AuthorizeRequest authorizeRequest) {
        return new AuthorizeConfirmation();
    }

    @Override
    public BootNotificationConfirmation handleBootNotificationRequest(UUID uuid, BootNotificationRequest bootNotificationRequest) {
        return  new BootNotificationConfirmation();
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
