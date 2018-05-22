package com.ambulance.corporate;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import android.util.Log;

import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Services.GpsOrMobileTracker;
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

public class DefaultActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {

    SharedPreferences permissionPref;
    SharedPreferences.Editor permissionPrefEditor;

    private static final int PERMISSION_REQUEST_CODE = 1997;
    private static final int LOCATION_ENABLE_RESOLUTION = 1996;

    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 1000 * 60;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public static FragmentManager fragmentManager;

    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        SharedPreferences preferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        boolean autoLogin = preferences.getBoolean("auto-login", false);

        Log.d("DEFAULTACTIVITY", userId + "<-userId autoLogin->" + autoLogin + " LoggedIn->" + Common.isLoggedIn);

        if (autoLogin && !userId.equals("")) {
            Common.isLoggedIn = true;
            /*Log.d("DEFAULTACTIVITY","TOP");
            dialog = (new AlertDialogBox(this)).dialogBuilderWithoutAction("", "Please wait...", false);
            final Accounts accounts = new Accounts(this, null);
            accounts.checkUserId(userId, new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("DEFAULTACTIVITY","GOT RESPONSE");
                    dialog.dismiss();
                    try {

                        if (response.has("response")) {

                            Common.isLoggedIn = true;

                        } else if (response.has("invalid")) {

                            (new AlertDialogBox(DefaultActivity.this))
                                    .dialogBuilderWithSingleAction("Something not right!"
                                            , accounts.errorMessages(response.getString("invalid")), true, "Ok"
                                            , new AmbulanceDialogInterface() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(VolleyError error) {
                    error.printStackTrace();
                    try{
                    (new AlertDialogBox(mContext)).dialogBuilderWithSingleAction("Something not right!"
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
            });*/
        }

        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left
                            , android.R.anim.slide_out_right
                            , android.R.anim.slide_in_left
                            , android.R.anim.slide_out_right)
                    .add(R.id.fragment_container, new SplashFragment())
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
                            status.startResolutionForResult(DefaultActivity.this, LOCATION_ENABLE_RESOLUTION);
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setMessage("We need your location for displaying it to the clients and giving you rides.")
                        .setTitle("Location permission needed")
                        .setCancelable(false)
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                permissionPrefEditor.putBoolean("asked", true);
                                ActivityCompat.requestPermissions(DefaultActivity.this, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMISSION_REQUEST_CODE);

            }

            permissionPrefEditor.apply();

        }
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

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationSetting();
        startService(new Intent(this, GpsOrMobileTracker.class));
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        stopService(new Intent(this, GpsOrMobileTracker.class));
    }
}