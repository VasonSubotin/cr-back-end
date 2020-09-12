package com.sm.client.utils;

import com.smartcar.sdk.data.SmartcarResponse;
import com.smartcar.sdk.data.VehicleLocation;

public class TestLocations {

    static private VehicleLocation[] vehicleLocations = new VehicleLocation[]{
            new VehicleLocation(37.3404659, -121.8876484), // horacemann
            new VehicleLocation(37.7984792, -122.4118597),// sanFrancisco1
            new VehicleLocation(33.9527744, -118.2673587),// LA1
            new VehicleLocation(33.9162154, -118.3806147),//LA2
            new VehicleLocation(34.0236824, -118.4847487),// Santa monica
            new VehicleLocation(34.0835584, -118.3165957),//LA3
            new VehicleLocation(33.6552224, -117.7518387),
            new VehicleLocation(34.0504554, -118.2541667),
            new VehicleLocation(32.7178375, -117.1602657),
            new VehicleLocation(32.7761055, -117.1563677),
            new VehicleLocation(34.0596424, -118.4333097)
    };

    public static SmartcarResponse<VehicleLocation> getTestCarLocationByVin(String vin) {
        int i = (vin.hashCode() % vehicleLocations.length + vehicleLocations.length) % vehicleLocations.length;
        return new SmartcarResponse(vehicleLocations[i], null, null);
    }
}
