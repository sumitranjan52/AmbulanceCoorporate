/*
 * Copyright (c) 2018. Sumit Ranjan
 */

package com.ambulance.corporate.Backend;

import android.content.Context;

import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Singleton.VolleySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RideRequest {

    private Context context;
    private String driverId;
    private String bookingId;
    private double startLat, startLng, stopLat, stopLng, driver_lat, driver_lng;

    public RideRequest() {
    }

    public RideRequest(Context context, String bookingId, double startLat, double startLng) {
        this.context = context;
        this.bookingId = bookingId;
        this.startLat = startLat;
        this.startLng = startLng;
    }

    public RideRequest(double stopLat, double stopLng, String bookingId, Context context) {
        this.context = context;
        this.bookingId = bookingId;
        this.stopLat = stopLat;
        this.stopLng = stopLng;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLng() {
        return startLng;
    }

    public void setStartLng(double startLng) {
        this.startLng = startLng;
    }

    public double getStopLat() {
        return stopLat;
    }

    public void setStopLat(double stopLat) {
        this.stopLat = stopLat;
    }

    public double getStopLng() {
        return stopLng;
    }

    public void setStopLng(double stopLng) {
        this.stopLng = stopLng;
    }

    public double getDriver_lat() {
        return driver_lat;
    }

    public void setDriver_lat(double driver_lat) {
        this.driver_lat = driver_lat;
    }

    public double getDriver_lng() {
        return driver_lng;
    }

    public void setDriver_lng(double driver_lng) {
        this.driver_lng = driver_lng;
    }

    /* Operation/Communication to/from server goes below */

    public void getRideDetailFromServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            if (this.bookingId != null) {
                jsonToken.put("bookingId", this.bookingId);
            }
            if (this.driverId != null) {
                jsonToken.put("driverId", this.driverId);
            }
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        tokenJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(tokenJsonObjectRequest);

    }

    public void updateRideDetailToServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("bookingId", this.bookingId);
            if (this.startLat != 0 && this.startLng != 0) {
                jsonToken.put("startLat", this.startLat);
                jsonToken.put("startLng", this.startLng);
                jsonToken.put("status", "started");
            }
            if (this.stopLat != 0 && this.stopLng != 0) {
                jsonToken.put("stopLat", this.stopLat);
                jsonToken.put("stopLng", this.stopLng);
                jsonToken.put("status", "completed");
            }

            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        tokenJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(tokenJsonObjectRequest);

    }

    public void cancelRideAndUpdateServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("bookingId", this.bookingId);
            jsonToken.put("status", "cancelledByDriver");
            jsonToken.put("driverId", this.driverId);
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        tokenJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(tokenJsonObjectRequest);

    }

    public void updateDriverLocationToServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("bookingId", this.bookingId);
            jsonToken.put("driver_lat", this.driver_lat);
            jsonToken.put("driver_lng", this.driver_lng);
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        tokenJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(tokenJsonObjectRequest);

    }

    public void acceptRideAndUpdateServer(final VolleyJSONResponses callback) {

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("bookingId", this.bookingId);
            jsonToken.put("status", "accepted");
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.REQUEST_URL)
                , jsonToken
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }
        );
        tokenJsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(tokenJsonObjectRequest);

    }

    public static String errorResponseMsg(String msgCode) {

        switch (msgCode) {

            case "API_KEY_REQUIRED":
                return "Contact developer with error: API_KEY_REQUIRED on sumitranjan52@gmail.com";

            case "API_KEY_INVALID":
                return "Contact developer with error: API_KEY_INVALID on sumitranjan52@gmail.com";

            case "INVALID_INPUT":
                return "Contact developer with error: INVALID_INPUT on sumitranjan52@gmail.com";

            case "UPDATED":
                return "Record is updated into remote database.";

            case "NOTHING_CHANGED":
                return "Nothing changed in remote database.";

            case "EMPTY_BOOKING_ID":
                return "Booking id is not sent to server.";

            case "NO_BOOKING_FOUND":
                return "No such booking is made by any user.";

            case "EMPTY_USER_ID":
                return "User Id is not set. Try logging in again";

            case "EMPTY_DRIVER_ID":
                return "Driver Id is not set. Try logging in again";

            case "EMPTY_LATITUDE":
                return "You can not book ambulance without providing your location.";

            case "EMPTY_LONGITUDE":
                return "You can not book ambulance without providing your location.";

            case "NO_DRIVER_FOUND":
                return "No nearby ambulance is found";

            case "RIDE_BOOKED":
                return "Congratulations! Ambulance is booked for you.";

            case "RIDE_BOOKING_FAILED":
                return "We are sorry. We are unable to book ambulance for you.";

            case "NO_LOCATION_SPECIFIED":
                return "Please allow app to send your location to server, when needed";

            case "EMPTY_STATUS":
                return "Please provide status of the booking.";

            case "NO_USER_SPECIFIED":
                return "Some one should take responsibility of this booking";

            default:
                return "Something went wrong";

        }

    }

}
