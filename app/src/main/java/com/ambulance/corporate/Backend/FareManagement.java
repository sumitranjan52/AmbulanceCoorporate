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

/**
 * Created by sumit on 29-Mar-18.
 */

public class FareManagement {

    private String bookingId;
    private Context context;

    public FareManagement(Context context, String bookingId) {
        this.context = context;
        this.bookingId = bookingId;
    }

    public void getFareFromServer(final VolleyJSONResponses callback){
        JSONObject loginIdJSON = new JSONObject();
        try {
            loginIdJSON.put("bookingId", this.bookingId);
            loginIdJSON.put("key", Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginIdHttpRequestForJSON = new JsonObjectRequest(Request.Method.PATCH
                , (Common.BASE_URL + Common.FARE_URL), loginIdJSON
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
        loginIdHttpRequestForJSON.setRetryPolicy(new DefaultRetryPolicy(15*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(loginIdHttpRequestForJSON);
    }

    public static String errorMsg(String errCode){

        switch (errCode) {

            case "API_KEY_REQUIRED":
                return "Contact developer with error: API_KEY_REQUIRED on sumitranjan52@gmail.com";

            case "API_KEY_INVALID":
                return "Contact developer with error: API_KEY_INVALID on sumitranjan52@gmail.com";

            case "INVALID_INPUT":
                return "Contact developer with error: INVALID_INPUT on sumitranjan52@gmail.com";

            case "EMPTY_BOOKING_ID":
                return "Booking Id is not submitted.";

            case "FARE_GENERATION_FAILED":
                return "Fare amount generation failed due to some unknown reason.";

            case "NO_BOOKING_FOUND":
                return "No such booking is made by any user.";

            case "UPDATED":
                return "Record is updated into remote database.";

            case "NOTHING_CHANGED":
                return "Nothing changed in remote database.";

            default:
                return "Something went wrong";

        }

    }
}
