package com.sm.client.mvc;

import com.google.gson.Gson;
import com.sm.client.model.smartcar.UserData;
import com.sm.client.model.smartcar.VehicleData;
import com.smartcar.sdk.AuthClient;
import com.smartcar.sdk.SmartcarException;
import com.smartcar.sdk.Vehicle;
import com.smartcar.sdk.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
public class SmartCarController {
    private static final Logger logger = LoggerFactory.getLogger(SmartCarController.class);

    private AuthClient client;
    private String code;
    private String access;

    @Value("${smartcar.clientId:70789610-4ebe-41b8-b913-c870ef6228ce}")
    private String clientId;

    @Value("${smartcar.clientSecret:66448ff0-666c-491d-b755-3ab906f42f19}")
    private String clientSecret;

    @Value("${smartcar.redirectUrl:http://localhost:8080/authrized}")
    private String urlRedirect;

    @Value("#{'${smartcar.permissions:required:read_vehicle_info,read_odometer,read_engine_oil,read_battery,read_charge,read_fuel,read_location,control_security,read_tires,read_vin}'.split(',')}")
    private String permissions[];

    @Value("${smartcar.testMode:false}")
    private boolean testMode = false;

    @PostConstruct
    public void init() {
//        String clientId = System.getenv("CLIENT_ID");
//        clientId = clientId == null ? "70789610-4ebe-41b8-b913-c870ef6228ce" : clientId;
//
//        String clientSecret = System.getenv("CLIENT_SECRET");
//        clientSecret = clientSecret == null ? "66448ff0-666c-491d-b755-3ab906f42f19" : clientSecret;

//        String redirectUri = System.getenv("REDIRECT_URI");
//        redirectUri = redirectUri == null ? "http://localhost:8080/authrized" : redirectUri;

        //  String[] scope = {"required:read_vehicle_info", "read_odometer"};


        this.client = new AuthClient(
                clientId,
                clientSecret,
                urlRedirect,
                permissions,
                testMode
        );


    }


    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(HttpServletRequest request,
                        HttpServletResponse response) {

        logger.info("----------------------call /login -------------------");
        String link = client.authUrlBuilder().setApprovalPrompt(true).build();
        //String link = client.getAuthUrl();
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", link);
        //String code = request.queryMap("code").value();
        //  System.out.println(code);
        return "ok";
    }

//
//    @RequestMapping("/login")
//    public String login(HttpServletRequest request, HttpServletResponse response) {
//
//        System.out.println("----------------------------------------------------------------");
//        String link = client.getAuthUrl();
//        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
//        response.setHeader("Location", link);
//        //String code = request.queryMap("code").value();
//        //  System.out.println(code);
//        return "sasas";
//    }


    @RequestMapping("/authrized")
    public String authrized(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response) throws SmartcarException {
        this.code = code;
        Auth auth = client.exchangeCode(code);
        return auth.getAccessToken();
    }

    @RequestMapping(value = "/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserData getData(HttpServletRequest request, @RequestParam("access") String access, HttpServletResponse responser) throws SmartcarException {
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

    @RequestMapping("/getInfo")
    public String getInfo(HttpServletRequest request, HttpServletResponse responseR) throws SmartcarException {
        StringBuilder sb = new StringBuilder();

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
        // the list of vehicle ids
        String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();
        sb.append("===vehicleIds=").append(vehicleIdResponse.getData()).append(":").append(vehicleIdResponse.getData().getVehicleIds().length);
        // instantiate the first vehicle in the vehicle id list
        Vehicle vehicle = new Vehicle(vehicleIds[0], access);
        sb.append("===v=").append(vehicle.info());

        // Make a request to Smartcar API
        VehicleInfo info = vehicle.info();


// Fetch the vehicle's odometer
        SmartcarResponse<VehicleOdometer> odometer = vehicle.odometer();
        System.out.println(odometer.getData().getDistance());

        sb.append("[").append(odometer.getData().getDistance()).append(odometer.getUnitSystem()).append("]").append(new Gson().toJson(vehicle));
        return sb.toString();
    }


    @RequestMapping("/getOdometr2")
    public String getOdometr2(HttpServletRequest request, HttpServletResponse responseR) throws SmartcarException {
        StringBuilder sb = new StringBuilder();

        SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
        // the list of vehicle ids
        String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();
        sb.append("===vehicleIds=").append(vehicleIdResponse.getData()).append(":").append(vehicleIdResponse.getData().getVehicleIds().length);
        // instantiate the first vehicle in the vehicle id list
        Vehicle vehicle = new Vehicle(vehicleIds[0], access);
        sb.append("===v=").append(vehicle.info());

        // Make a request to Smartcar API
        VehicleInfo info = vehicle.info();


// Fetch the vehicle's odometer
        SmartcarResponse<VehicleOdometer> odometer = vehicle.odometer();
        System.out.println(odometer.getData().getDistance());

        sb.append("[").append(new Gson().toJson(odometer)).append("]").append(new Gson().toJson(vehicle));
        return sb.toString();
    }


}
