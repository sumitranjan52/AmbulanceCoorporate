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
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ambulance.corporate.Backend.FCMToken;
import com.ambulance.corporate.Backend.LocationToServer;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks {

    private static final int PERMISSION_REQUEST_CODE = 1997;

    private static final int LOCATION_ENABLE_RESOLUTION = 1996;

    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 1000 * 60;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    SharedPreferences permissionPref;
    SharedPreferences.Editor permissionPrefEditor;

    public static FragmentManager fragmentManager;

    private ActionBarDrawerToggle toggle;

    /*private TextView navName, navEmail;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionPref = getSharedPreferences("permission_pref", MODE_PRIVATE);
        permissionPrefEditor = permissionPref.edit();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();

        /*SharedPreferences sharedPreferences = getSharedPreferences("account",MODE_PRIVATE);

        navName = findViewById(R.id.txtNavName);
        navEmail = findViewById(R.id.txtNavEmail);
        navName.setText(sharedPreferences.getString("name",""));
        navEmail.setText(sharedPreferences.getString("email",""));*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        runtimePermissionCheck();

        if (findViewById(R.id.mainFragmentContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
            fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left
                            ,android.R.anim.slide_out_right
                            ,android.R.anim.slide_in_left
                            ,android.R.anim.slide_out_right)
                    .add(R.id.mainFragmentContainer, new HomeMapFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            if (!item.isChecked()){
                if (findViewById(R.id.mainFragmentContainer) != null) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.mainFragmentContainer, new HomeMapFragment())
                            .commit();
                }
            }

        } else if (id == R.id.nav_account) {

            if (!item.isChecked()){
                if (findViewById(R.id.mainFragmentContainer) != null) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.mainFragmentContainer, new DriverAccountFragment())
                            .commit();
                }
            }

        } else if (id == R.id.nav_booking_history) {

            if (!item.isChecked()){
                if (findViewById(R.id.mainFragmentContainer) != null) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.mainFragmentContainer, new BookingHistoryFragment())
                            .commit();
                }
            }

        } /*else if (id == R.id.nav_earning) {

            if (!item.isChecked()){
                if (findViewById(R.id.mainFragmentContainer) != null) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.mainFragmentContainer, new PaymentFragment())
                            .commit();
                }
            }

        } else if (id == R.id.nav_setting) {

            if (!item.isChecked()){
                if (findViewById(R.id.mainFragmentContainer) != null) {
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.mainFragmentContainer, new SettingFragment())
                            .commit();
                }
            }

        }*/ else if (id == R.id.nav_signOut) {

            if (!item.isChecked()){
                String userId = getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
                SharedPreferences sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId","");
                editor.putBoolean("auto-login", false);
                editor.apply();

                if (!userId.equals("")){

                    FCMToken fcmToken = new FCMToken();
                    fcmToken.setContext(this);
                    fcmToken.setUserId(userId);
                    fcmToken.deleteTokenFromServer(new VolleyJSONResponses() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("FCM_RESPONSE",response.toString());
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.d("FCM_RESPONSE",error.toString());
                            try{
                                (new AlertDialogBox(MainActivity.this)).dialogBuilderWithSingleAction("Something not right!"
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

                    new LocationToServer(userId,this).deleteLocationFromServer(new VolleyJSONResponses() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("LOCATION_RESPONSE",response.toString());
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.d("LOCATION_RESPONSE",error.toString());
                            try{
                                (new AlertDialogBox(MainActivity.this)).dialogBuilderWithSingleAction("Something not right!"
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

                Common.isLoggedIn = false;
                startActivity(new Intent(this,DefaultActivity.class));
                finish();
            }

        } else if (id == R.id.nav_share) {

            if (!item.isChecked()){
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_SUBJECT,"Ambulance Corporate");
                send.putExtra(Intent.EXTRA_TEXT,"Ambulance driver app is intended for ambulance drivers only." +
                        "Download from here: https://goo.gl/eXqusc and choose the latest build version.");
                startActivity(Intent.createChooser(send,"Choose"));
            }

        } else if (id == R.id.nav_send) {

            if (!item.isChecked()){
                Intent email = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:sumitranjan52@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT,"Ambulance Corporate - Support");
                email.putExtra(Intent.EXTRA_TEXT,"Remove this line and write your message.");
                startActivity(Intent.createChooser(email,"Choose"));
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                            status.startResolutionForResult(MainActivity.this, LOCATION_ENABLE_RESOLUTION);
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
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
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

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationSetting();
        startService(new Intent(this, GpsOrMobileTracker.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        stopService(new Intent(this, GpsOrMobileTracker.class));

        String userId = getSharedPreferences("account",MODE_PRIVATE).getString("userId","");
        if (!userId.equals("")) {
            new LocationToServer(userId, this).deleteLocationFromServer(new VolleyJSONResponses() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("LOCATION_RESPONSE", response.toString());
                }

                @Override
                public void onError(VolleyError error) {
                    Log.d("LOCATION_RESPONSE", error.toString());
                    try{
                        (new AlertDialogBox(MainActivity.this)).dialogBuilderWithSingleAction("Something not right!"
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
