package com.sm.client.utils;

public class GeoUtils {

    private static final double earthRad = 6371000D; //meters

    public static double calculateDistance(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {

        double deltaLatitude = Math.toRadians(latitudeB - latitudeA);
        double deltaLongitude = Math.toRadians(longitudeB - longitudeA);
        double tk = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                + Math.cos(Math.toRadians(latitudeA)) * Math.cos(Math.toRadians(latitudeB))
                * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);
        double kof = 2 * Math.atan2(Math.sqrt(tk), Math.sqrt(1 - tk));
        float destination = (float) (earthRad * kof);

        return destination;
    }

    /**
     * Method calculates range for latitude
     *
     * @param latitude
     * @param range    - in meters
     * @return array with from  and to lattitude
     */
    public static double[] calculateLatRange(double latitude, double range) {
        double radDelta = Math.toDegrees(range / earthRad) / 2;
        return new double[]{latitude - radDelta, latitude + radDelta};
    }


    public static double[] calculateLngRange(double latitude, double longitude, double range) {
        double lRad = Math.cos(Math.toRadians(latitude)) * earthRad;
        double radDelta = Math.toDegrees(range / lRad) / 2;

        return new double[]{longitude - radDelta, longitude + radDelta};
    }
}
