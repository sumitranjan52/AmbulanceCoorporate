package com.ambulance.corporate.Common;

/**
 * Created by sumit on 19-Feb-18.
 */

public class Common {

    public static double latitude;
    public static double longitude;

    public static boolean isLoggedIn = false;

    public static final String LOG_TAG = "CORPORATE";

    public static final String BASE_URL = "http://hiva26.com/ambulanceapi/";
    public static final String DRIVER_URL = "driver.php";
    public static final String TOKEN_URL = "fcm.php";
    public static final String LOCATION_URL = "driverLocation.php";
    public static final String REQUEST_URL = "rideRequest.php";
    public static final String FARE_URL = "fare.php";

    public static final String API_KEY = "b1816167ecba02ec2b99ffbf79fa59bc";

    /* Requester details summary */
    public static String emergencyRequestBookingID = null;
    public static String userName = "";
    public static String userPhone = "";
    public static double userLat;
    public static double userLng;
    public static String userAddress = "";

    public static boolean isConnected = false;

    public static double startLat;
    public static double startLng;

    public static double stopLat;
    public static double stopLng;
}
