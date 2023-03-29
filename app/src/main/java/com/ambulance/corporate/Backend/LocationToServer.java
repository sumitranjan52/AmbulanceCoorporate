package com.ambulance.corporate.Backend;

/*
 * Copyright (c) 2018. Sumit Ranjan
 */

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

public class LocationToServer {

    private String driverId;
    private double lat, lng;
    private Context context;

    public LocationToServer() {
    }

    public LocationToServer(String driverId, Context context) {
        this.driverId = driverId;
        this.context = context;
    }

    public LocationToServer(double lat, double lng, Context context) {
        this.lat = lat;
        this.lng = lng;
        this.context = context;
    }

    public LocationToServer(String driverId, double lat, double lng, Context context) {
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
        this.context = context;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void getLocationFromServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, (Common.BASE_URL + Common.LOCATION_URL)
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

    public void setLocationToServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("driverId",driverId);
            jsonToken.put("latitude", lat);
            jsonToken.put("longitude", lng);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, (Common.BASE_URL + Common.LOCATION_URL), jsonToken
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

    public void updateLocationToServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("driverId",driverId);
            jsonToken.put("latitude", lat);
            jsonToken.put("longitude", lng);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.LOCATION_URL)
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

    public void deleteLocationFromServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("driverId",driverId);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, (Common.BASE_URL + Common.LOCATION_URL)
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

    public static String errorResponseMsg(String msgCode){

        switch (msgCode) {

            case "API_KEY_REQUIRED":
                return "Contact developer with error: API_KEY_REQUIRED on sumitranjan52@gmail.com";

            case "API_KEY_INVALID":
                return "Contact developer with error: API_KEY_INVALID on sumitranjan52@gmail.com";

            case "INVALID_INPUT":
                return "Contact developer with error: INVALID_INPUT on sumitranjan52@gmail.com";

            case "EMPTY_DRIVER_ID":
                return "Shared_pref is altered. Try logging in again.";

            case "EMPTY_LATITUDE":
                return "You will not receive booking. Since, your location is not updated to server";

            case "EMPTY_LONGITUDE":
                return "You will not receive booking. Since, your location is not updated to server";

            case "NO_DRIVER_FOUND":
                return "No driver(s) is/are found.";

            case "LOCATION_ADDED":
                return "Location data is added to server";

            case "DRIVER_LOCATION_ALREADY_PRESENT":
                return "Location data is already added to server";

            case "DELETED":
                return "Location record is cleared from remote database.";

            case "DELETION_FAILED":
                return "Location record is not cleared from remote database.";

            case "UPDATED":
                return "Record is updated into remote database.";

            case "NOTHING_CHANGED":
                return "Nothing changed in remote database.";

            case "ACCOUNT_INACTIVE":
                return "Your account is under review. Please try again later.";

            default:
                return "Something went wrong";

        }

    }

}
