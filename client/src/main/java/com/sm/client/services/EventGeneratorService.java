package com.sm.client.services;

import com.sm.client.model.smartcar.VehicleData;
import com.sm.dao.EventsDao;
import com.sm.dao.UserSessionDao;
import com.sm.dao.conf.Constants;
import com.sm.model.SmEvent;
import com.sm.model.SmUserSession;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(EventGeneratorService.class);

    @Autowired
    private EventsDao eventsDao;

    @Autowired
    private UserSessionDao userSessionDao;

    public SmEvent generateEvent(Long accountId) throws SmartcarException {


        SmUserSession smUserSession = userSessionDao.getLastSessionsByType(accountId, Constants.SMART_CAR_AUTH_TYPE);
        String token = smUserSession.getToken();
        SmEvent smEvent = new SmEvent();
        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(token);

        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
            VehicleData vehicleData = new VehicleData();
            Vehicle vehicle = new Vehicle(vehicleId, token);

            try {
                vehicleData.setBattery(vehicle.battery().getData());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVehicleInfo(vehicle.info());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setOdometer(vehicle.odometer());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setCharge(vehicle.charge());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setFuel(vehicle.fuel());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }


            try {
                vehicleData.setOil(vehicle.oil());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVin(vehicle.vin());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setVehicleId(vehicle.getVehicleId());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setLocation(vehicle.location());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            try {
                vehicleData.setTirePressure(vehicle.tirePressure());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }

            smEvent.setLatitude(vehicleData.getLocation().getData().getLatitude());
            smEvent.setLongitude(vehicleData.getLocation().getData().getLongitude());
            smEvent.setAccountId(accountId);
            smEvent.setEnergy(Double.valueOf(vehicleData.getBattery().getPercentRemaining()).longValue());
            //smEvent.setExtTemperature(vehicleData.getCharge().getData().);
            //vehiclesList.add(vehicleData);
        }

        return smEvent;
    }
}
