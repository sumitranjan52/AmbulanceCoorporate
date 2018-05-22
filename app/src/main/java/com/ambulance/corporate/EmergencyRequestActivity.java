package com.ambulance.corporate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;

import com.ambulance.corporate.Backend.RideRequest;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Services.GpsOrMobileTracker;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONObject;

public class EmergencyRequestActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {

    public static FragmentManager fragmentManager;

    private static final int PERMISSION_REQUEST_CODE = 1997;

    private static final int LOCATION_ENABLE_RESOLUTION = 1996;

    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 1000 * 60;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    SharedPreferences permissionPref;
    SharedPreferences.Editor permissionPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_request);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getIntent().getExtras() != null){
            if (getIntent().getExtras().getString("bookingId") != null){
                Log.d("REQUEST_NOTIFICATION",getIntent().getExtras().getString("bookingId"));
                Common.emergencyRequestBookingID = getIntent().getExtras().getString("bookingId");
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.emergencyFragmentContainer) != null){
            if (savedInstanceState != null){
                return;
            }
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left
                            ,android.R.anim.slide_out_right
                            ,android.R.anim.slide_in_left
                            ,android.R.anim.slide_out_right)
                    .add(R.id.emergencyFragmentContainer, new RequestFragment())
                    .commit();
        }

        permissionPref = getSharedPreferences("permission_pref", MODE_PRIVATE);
        permissionPrefEditor = permissionPref.edit();

        runtimePermissionCheck();
    }

    /* check for location enable and set to high accuracy */
    public void enableLocationSetting() {

        /* Setup Location request */
        mLocationRequest = new LocationRequest();
        mLocationRequest.setSmallestDisplacement(MIN_DISTANCE);
        mLocationRequest.setFastestInterval(MIN_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /* setup googleApiClient */
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        LocationSettingsRequest.Builder locationSettingRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        locationSettingRequest.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> locationSettingsResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingRequest.build());

        locationSettingsResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(EmergencyRequestActivity.this, LOCATION_ENABLE_RESOLUTION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });

    }

    /* Check for runtime permission */
    private void runtimePermissionCheck() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setMessage("We need your location for displaying it to the clients and giving you rides.")
                        .setTitle("Location permission needed")
                        .setCancelable(false)
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                permissionPrefEditor.putBoolean("asked", true);
                                ActivityCompat.requestPermissions(EmergencyRequestActivity.this, new String[]{
                                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                                }, PERMISSION_REQUEST_CODE);

                            }
                        })
                        .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                finish();

                            }
                        })
                        .show();

            } else if (permissionPref.getBoolean("asked", false)) {

                new AlertDialog.Builder(this)
                        .setMessage("We need your location for displaying it to the clients and giving you rides.")
                        .setTitle("Location permission needed")
                        .setCancelable(false)
                        .setPositiveButton("Goto setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                Intent appSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                startActivity(appSetting);

                            }
                        })
                        .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                finish();

                            }
                        })
                        .show();

            } else {

                permissionPrefEditor.putBoolean("asked", true);
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);

            }

            permissionPrefEditor.apply();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        (new AlertDialogBox(this)).dialogBuilderWithTwoAction(
                "Cancel booking?"
                , "Are you sure want to cancel this booking?"
                , false
                , "Yes"
                , new AmbulanceDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        RideRequest rideRequest = new RideRequest();
                        rideRequest.setBookingId(Common.emergencyRequestBookingID);
                        String driverId = getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
                        rideRequest.setDriverId(driverId);

                        rideRequest.cancelRideAndUpdateServer(new VolleyJSONResponses() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                if (response.has("response")){
                                    Common.emergencyRequestBookingID = "";
                                    startActivity(new Intent(EmergencyRequestActivity.this,MainActivity.class));
                                    finish();
                                }else{
                                    Log.d("BACK_CANCEL", "onSuccess: "+response.toString());
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Log.d("BACK_CANCEL", "onSuccess: "+error.toString());
                                try{
                                    (new AlertDialogBox(EmergencyRequestActivity.this)).dialogBuilderWithSingleAction("Something not right!"
                                            , NetworkErrorMessages.networkErrorMsg(error.networkResponse.statusCode)
                                            , true, "Ok"
                                            , new AmbulanceDialogInterface() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }, "No"
                , new AmbulanceDialogInterface() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, GpsOrMobileTracker.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationSetting();
        startService(new Intent(this, GpsOrMobileTracker.class));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.reconnect();
    }

    /* handle the permission request grant and deny */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {

            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                runtimePermissionCheck();
            }

        } else {
            runtimePermissionCheck();
        }
    }

    /* handle location enable dialog result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_ENABLE_RESOLUTION) {
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    finish();
                    break;
                case Activity.RESULT_OK:
                    startService(new Intent(this, GpsOrMobileTracker.class));
                    break;
            }
        }
    }
}
