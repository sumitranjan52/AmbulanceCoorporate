package com.ambulance.corporate.Services;

import android.content.SharedPreferences;
import android.util.Log;

import com.ambulance.corporate.Backend.FCMToken;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

/**
 * Created by sumit on 18-Mar-18.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN",token);
        setInSharedPref(token);
        updateTokenToServer(token);

    }

    private void setInSharedPref(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("firebase",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcmToken",token);
        editor.apply();
    }

    private void updateTokenToServer(String token) {

        String userId = getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
        if (!userId.equals("")){

            new FCMToken(getApplicationContext(),userId,token).updateTokenToServer(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("FCM_RESPONSE",response.toString());
                }

                @Override
                public void onError(VolleyError error) {

                }
            });

        }

    }
}
