package com.sm.client.mvc.auth;


import com.sm.client.model.smartcar.UserData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.ResourceService;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.client.services.cache.SmartCarCacheService;
import com.sm.model.Constants;
import com.sm.model.ServiceResult;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.Auth;
import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleIds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
public class SmartCarAuthController {
    private static final Logger logger = LoggerFactory.getLogger(SmartCarAuthController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SmartCarService smartCarService;

    @Autowired
    private SmartCarCacheService smartCarCacheService;

    @RequestMapping(value = "/smartCarLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        logger.info("----------------------call /login -------------------");
        String link = smartCarService.getClient().authUrlBuilder().setApprovalPrompt(true).build();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", link);
        return "ok";
    }


    @RequestMapping(value = "/smartCarToken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String smartCarToken(HttpServletRequest request, HttpServletResponse response, String code) {
        logger.info("----------------------call smartCarToken -------------------");
        return code;
    }


    @RequestMapping(value = "/smartCarSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startSession(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws SmartcarException {

        Auth auth = smartCarService.getClient().exchangeCode(code);
        try {
            // SmUserSession smUserSession = securityService.saveCurrentSession(Constants.SMART_CAR_AUTH_TYPE, auth.getAccessToken(), auth.getRefreshToken(), 3600000);
            SmUserSession smUserSession = securityService.createSmUserSession(Constants.SMART_CAR_AUTH_TYPE, auth.getAccessToken(), auth.getRefreshToken(), 3600000, securityService.getAccount().getIdAccount());
            resourceService.refreshCarData(smUserSession);
            return new ResponseEntity(HttpStatus.OK);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/authrized"), status);
        }
    }

    @RequestMapping(value = "/needInitSmartCarSession", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> needInitSmartCarSession(@RequestParam(value = "resourceId", required = false) Long resourceId, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (resourceId != null) {
                Map<String, Object> res = new HashMap<>();
                res.put("needInit", smartCarService.needInitUserSession(resourceId));
                return new ResponseEntity(res, HttpStatus.OK);
            } else {
                return new ResponseEntity(smartCarService.needInitUserSessions(), HttpStatus.OK);
            }
        } catch (SmException ex) {
            return new ResponseEntity(ex.getMessage(), HttpStatus.valueOf(ex.getCode()));
        }
    }

    @RequestMapping(value = "/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserData getData(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "access", required = false) String access, HttpServletResponse responser) throws SmartcarException, SmException {
        if (access == null) {
            //looking into table
            List<SmUserSession> userSessions = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
            //if expired or closed
            if (userSessions == null || userSessions.isEmpty() || userSessions.get(0).getClosed()) {
                login(request, response);
                return null;
            }
            access = userSessions.get(0).getToken();
        }
        UserData userData = new UserData();
        List<VehicleData> vehiclesList = new ArrayList<>();
        userData.setVehiclesList(vehiclesList);
        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
        userData.setUserId(AuthClient.getUserId(access));

        for (String vehicleId : vehicleIdResponse.getData().getVehicleIds()) {
            VehicleData vehicleData = new VehicleData();
            Vehicle vehicle = new Vehicle(vehicleId, access);

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

            vehiclesList.add(vehicleData);
        }

        return userData;
    }


}
