package com.sm.client.utils;

public class GeoUtils {

    public static double calculateDistance(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {
        double earthRad = 6371000D; //meters
        double deltaLatitude = Math.toRadians(latitudeB - latitudeA);
        double deltaLongitude = Math.toRadians(longitudeB - longitudeA);
        double tk = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(Math.toRadians(latitudeA)) * Math.cos(Math.toRadians(latitudeB))
                * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);
        double kof = 2 * Math.atan2(Math.sqrt(tk), Math.sqrt(1 - tk));
        float destination = (float) (earthRad * kof);

        return destination;
    }
}
