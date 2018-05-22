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
 * Created by sumit on 18-Mar-18.
 */

public class FCMToken {

    private Context context;
    private String userId;
    private String fcmToken;

    public FCMToken(Context context, String userId, String fcmToken) {
        this.context = context;
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    public FCMToken() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void getTokenFromServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("userId",userId);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, (Common.BASE_URL + Common.TOKEN_URL)
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

    public void sendTokenToServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("userId",userId);
            jsonToken.put("fcmToken", fcmToken);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, (Common.BASE_URL + Common.TOKEN_URL), jsonToken
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

    public void updateTokenToServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("userId",userId);
            jsonToken.put("fcmToken", fcmToken);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, (Common.BASE_URL + Common.TOKEN_URL)
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

    public void deleteTokenFromServer(final VolleyJSONResponses callback){

        JSONObject jsonToken = new JSONObject();
        try {
            jsonToken.put("userId",userId);
            jsonToken.put("key",Common.API_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tokenJsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, (Common.BASE_URL + Common.TOKEN_URL)
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

            case "TOKEN_REGISTERED":
                return "You are registered successfully!";

            case "TOKEN_REGISTRATION_FAILED":
                return "We are sorry. Registration failed!";

            case "EMPTY_USERID":
                return "UserId is cleared from memory. Try signing in again.";

            case "EMPTY_FCMTOKEN":
                return "Token is cleared from memory. Try signing in again.";

            case "DELETED":
                return "Record is cleared from remote database.";

            case "UPDATED":
                return "Record is updated into remote database.";

            case "NOTHING_CHANGED":
                return "Nothing changed in remote database.";

            case "USER_NOT_EXIST":
                return "Shared Preferences is altered. Try signing in again.";

            case "USER_OR_TOKEN_NOT_FOUND":
                return "Shared Preferences is altered. Try signing in again.";

            default:
                return "Something went wrong";

        }

    }
}
