package com.ambulance.corporate.Interfaces;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by sumit on 13-Mar-18.
 */

public interface VolleyJSONResponses {
    void onSuccess(JSONObject response);
    void onError(VolleyError error);
}
