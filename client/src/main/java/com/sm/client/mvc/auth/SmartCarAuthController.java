package com.sm.client.mvc.auth;

import com.sm.client.model.smartcar.UserData;
import com.sm.client.model.smartcar.VehicleData;
import com.sm.client.services.SecurityService;
import com.sm.client.services.SmartCarService;
import com.sm.dao.conf.Constants;
import com.sm.model.ServiceResult;
import com.sm.model.SmException;
import com.sm.model.SmUserSession;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SmartCarAuthController {
    private static final Logger logger = LoggerFactory.getLogger(SmartCarAuthController.class);

    private AuthClient client;
    // private String code;
    // private String access;

    @Value("${smartcar.clientId:70789610-4ebe-41b8-b913-c870ef6228ce}")
    private String clientId;

    @Value("${smartcar.clientSecret:66448ff0-666c-491d-b755-3ab906f42f19}")
    private String clientSecret;

    @Value("${smartcar.redirectUrl:http://localhost:8080/smartCarToken}")
    private String urlRedirect;

    @Value("#{'${smartcar.permissions:required:read_vehicle_info,read_odometer,read_engine_oil,read_battery,read_charge,read_fuel,read_location,control_security,read_tires,read_vin}'.split(',')}")
    private String permissions[];

    @Value("${smartcar.testMode:false}")
    private boolean testMode = false;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private SmartCarService smartCarService;

    @PostConstruct
    public void init() {
        this.client = new AuthClient(
                clientId,
                clientSecret,
                urlRedirect,
                permissions,
                testMode
        );
    }


    @RequestMapping(value = "/smartCarLogin", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        logger.info("----------------------call /login -------------------");
        String link = client.authUrlBuilder().setApprovalPrompt(true).build();
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

        Auth auth = client.exchangeCode(code);
        try {
            SmUserSession smUserSession = securityService.saveCurrentSession(Constants.SMART_CAR_AUTH_TYPE, auth.getAccessToken(), auth.getRefreshToken(), 3600000);
            smartCarService.refreshCarData(securityService.getAccount().getLogin());
            return new ResponseEntity(HttpStatus.OK);
        } catch (SmException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getCode());
            return new ResponseEntity(new ServiceResult(ex.getCode(), status.getReasonPhrase(), ex.getMessage(), "/authrized"), status);
        }
    }

    @RequestMapping(value = "/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserData getData(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "access", required = false) String access, HttpServletResponse responser) throws SmartcarException, SmException {
        if (access == null) {
            //looking into table
            SmUserSession userSession = securityService.getActiveSession(Constants.SMART_CAR_AUTH_TYPE);
            //if expired or closed
            if (userSession == null || userSession.getClosed()) {
                login(request, response);
                return null;
            }
            access = userSession.getToken();
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
