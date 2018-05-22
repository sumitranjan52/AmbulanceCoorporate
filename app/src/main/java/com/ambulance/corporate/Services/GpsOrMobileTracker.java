package com.ambulance.corporate.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by sumit on 19-Feb-18.
 */

public class GpsOrMobileTracker extends Service implements LocationListener {

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;
    private boolean locationByGPS = false;

    private Location location;
    private double latitude;
    private double longitude;

    private static final long MIN_DISTANCE = 10;
    private static final long MIN_TIME = 1000;
    protected LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CHECKING","reached");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getLocation() {

        try{

            if (locationManager == null){
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            }
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled){}
            else{

                canGetLocation = true;

                /* Get Location from Network */
                locationByNetworkProvider();

                /* Get Location from GPS */
                locationByGPSProvider();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void locationByGPSProvider() {
        Log.d("GPSPROVIDER","valid");
        if (isGPSEnabled){

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE,this);

            if (locationManager != null){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null){
                    Intent i = new Intent("location_update");
                    i.putExtra("lat",getLatitude());
                    i.putExtra("lng",getLongitude());
                    sendBroadcast(i);
                }
            }
        }
    }

    private void locationByNetworkProvider() {
        Log.d("NETPROVIDER","valid");
        if (isNetworkEnabled && !locationByGPS){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE,this);

            if (locationManager != null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    public void stopUsingLocation(){
        Log.d("STOPLOCUP","valid");
        if (locationManager != null){
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.removeUpdates(this);
        }
    }

    public double getLatitude() {
        if (location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean isCanGetLocation() {
        return canGetLocation;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location.getProvider().equals(locationManager.GPS_PROVIDER)) {
            if (!locationByGPS) {
                locationByGPS = true;
                stopUsingLocation();
                locationByGPSProvider();
            }
        }
        this.location = location;
        Log.d("LOCATION",this.location.toString() + " " +locationByGPS);
        Intent i = new Intent("location_update");
        i.putExtra("lat",getLatitude());
        i.putExtra("lng",getLongitude());
        sendBroadcast(i);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        stopUsingLocation();
        super.onDestroy();
    }

}
