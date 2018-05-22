package com.ambulance.corporate;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambulance.corporate.Backend.LocationToServer;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment implements View.OnClickListener {

    private ImageView logo;
    private Button btnFindBooking;
    private TextView status;

    private BroadcastReceiver broadcastReceiver;

    private boolean alertShown = false;

    private Context mContext;
    private Activity activity;

    public SplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d("SPLASH", "onCreate: Fragment Splash");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        logo = view.findViewById(R.id.logo);
        btnFindBooking = view.findViewById(R.id.btnFindBooking);
        status = view.findViewById(R.id.status);

        if (Common.isLoggedIn) {
            logo.setPadding(0, 0, 0, 100);
            status.setText("Getting your location");
            status.setTextSize(20.0f);
            btnFindBooking.setVisibility(View.GONE);
        } else {
            logo.setPadding(0, 0, 0, 0);
            status.setText("Ambulance Coporate");
            btnFindBooking.setVisibility(View.VISIBLE);
            btnFindBooking.setOnClickListener(this);
        }

        return view;
    }

    /* implement click on views */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnFindBooking:
                DefaultActivity.fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right
                                , android.R.anim.slide_in_left
                                , android.R.anim.slide_out_right)
                        .addToBackStack("ambulance")
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
                break;

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.isLoggedIn) {
            if (broadcastReceiver == null) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.d("BROADCAST", "fired");
                        try {
                            Common.latitude = intent.getExtras().getDouble("lat");
                            Common.longitude = intent.getExtras().getDouble("lng");

                            String dId = mContext.getSharedPreferences("account", Context.MODE_PRIVATE).getString("userId", "");
                            new LocationToServer(dId, Common.latitude, Common.longitude, mContext).setLocationToServer(new VolleyJSONResponses() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.d("LOCATION_RESPONSE_S", response.toString());
                                    if (response.has("response")){
                                        startActivity(new Intent(mContext, MainActivity.class));
                                        activity.finish();
                                    }
                                    else if (response.has("error")) {
                                        if (!alertShown){
                                            try {
                                                JSONArray errors = response.getJSONArray("error");
                                                for (int i = 0; i < errors.length(); i++) {
                                                    if (errors.getString(i).equals("EMPTY_DRIVER_ID")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!"
                                                                , LocationToServer.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        activity.finish();
                                                                    }
                                                                });
                                                    } else if (errors.getString(i).equals("EMPTY_LATITUDE")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!"
                                                                , LocationToServer.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        activity.finish();
                                                                    }
                                                                });
                                                    } else if (errors.getString(i).equals("EMPTY_LONGITUDE")) {
                                                        (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Error!", LocationToServer.errorResponseMsg(errors.getString(i))
                                                                , true, "Ok"
                                                                , new AmbulanceDialogInterface() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                        activity.finish();
                                                                    }
                                                                });
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            alertShown = true;
                                        }
                                    }
                                    else if (response.has("invalid")) {
                                        startActivity(new Intent(mContext, MainActivity.class));
                                        activity.finish();
                                    }
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    Log.d("LOCATION_RESPONSE_S", error.toString());
                                    try{
                                        if (!alertShown){
                                            (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
                                                    , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                                                    , true, "Ok"
                                                    , new AmbulanceDialogInterface() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            activity.finish();
                                                        }
                                                    });
                                            alertShown = true;
                                        }
                                    }catch (Exception e){
                                        error.printStackTrace();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
            mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }
}
