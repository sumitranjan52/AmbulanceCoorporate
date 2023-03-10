package com.ambulance.corporate;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambulance.corporate.Backend.RideRequest;
import com.ambulance.corporate.Common.AlertDialogBox;
import com.ambulance.corporate.Common.Common;
import com.ambulance.corporate.Common.DirectionJSONDecode;
import com.ambulance.corporate.Common.NetworkErrorMessages;
import com.ambulance.corporate.Interfaces.AmbulanceDialogInterface;
import com.ambulance.corporate.Interfaces.VolleyJSONResponses;
import com.ambulance.corporate.Singleton.VolleySingleton;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmRequestFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private Context mContext;
    private Activity activity;
    private MapView mMapView;
    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver;

    private TextView userName, userAddress;
    private ImageView callUser;
    private Button startTrip, stopTrip;
    private LinearLayout userSummary;

    public ConfirmRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        activity.setTitle("Emergency Request");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_request, container, false);

        userName = view.findViewById(R.id.userName);
        userAddress = view.findViewById(R.id.userAdress);
        callUser = view.findViewById(R.id.callUser);
        startTrip = view.findViewById(R.id.btnStartRide);
        stopTrip = view.findViewById(R.id.btnStopRide);
        userSummary = view.findViewById(R.id.userSummary);

        userName.setText(Common.userName);
        userAddress.setText(Common.userAddress);
        callUser.setOnClickListener(this);
        startTrip.setOnClickListener(this);
        stopTrip.setOnClickListener(this);

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setPadding(0, 0, 0, 100);

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
        Log.d("USER_LAT_LNG", "onReceive: " + Common.userLat + "," +Common.userLng);
        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.userLat, Common.userLng))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_blue)).snippet(Common.userAddress));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude, Common.longitude), 15.0f));
        getDirection(Common.userLat, Common.userLng);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MAP_FRAG", "onResume");
        mMapView.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        Common.latitude = intent.getExtras().getDouble("lat");
                        Common.longitude = intent.getExtras().getDouble("lng");
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));
                        Log.d("USER_LAT_LNG", "onReceive: " + Common.userLat + "," +Common.userLng);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.userLat, Common.userLng))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_blue)).snippet(Common.userAddress));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Common.latitude, Common.longitude), 15.0f));
                        getDirection(Common.userLat, Common.userLng);

                        RideRequest rideRequest = new RideRequest();
                        rideRequest.setDriver_lat(Common.latitude);
                        rideRequest.setDriver_lng(Common.longitude);
                        rideRequest.setBookingId(Common.emergencyRequestBookingID);
                        rideRequest.updateDriverLocationToServer(new VolleyJSONResponses() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                if (response.has("response")) {
                                    Log.d("U_L", "onSuccess: " + response.toString());
                                } else {
                                    Log.d("U_L", "onSuccess: " + response.toString());
                                }
                            }

                            @Override
                            public void onError(VolleyError error) {
                                Log.d("U_L", "onSuccess: " + error.toString());
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
                        });

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        mContext.registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    private void getDirection(double lat, double lng) {
        LatLng currentPosition = new LatLng(Common.latitude, Common.longitude);
        String requestAPI = null;
        try {
            requestAPI = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + currentPosition.latitude + "," + currentPosition.longitude + "&" +
                    "destination=" + lat + "," + lng + "&" +
                    "key=" + mContext.getResources().getString(R.string.google_maps_key);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, requestAPI, null
                    , new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        new ParserTask().execute(response.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            );
            VolleySingleton.getInstance(mContext).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("MAP_FRAG", "onPause");
        mMapView.onPause();
        if (broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MAP_FRAG", "onDestroy");
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("MAP_FRAG", "onLowMemory");
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.callUser:
                callUser();
                break;

            case R.id.btnStartRide:
                startRide();
                break;

            case R.id.btnStopRide:
                stopRide();
                break;

        }

    }

    private void stopRide() {

        RideRequest rideRequest = new RideRequest();
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.setStopLat(Common.latitude);
        rideRequest.setStopLng(Common.longitude);
        rideRequest.updateRideDetailToServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.has("response")) {
                    Log.d("STOP_TRIP", "onSuccess: " + response.toString());
                    EmergencyRequestActivity.fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    , android.R.anim.slide_out_right
                                    , android.R.anim.slide_in_left
                                    , android.R.anim.slide_out_right)
                            .replace(R.id.emergencyFragmentContainer, new FareFragment())
                            .commit();
                } else {
                    Log.d("STOP_TRIP", "onSuccess: " + response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("STOP_TRIP", "onSuccess: " + error.toString());
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
        });

    }

    private void startRide() {

        RideRequest rideRequest = new RideRequest();
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.setStartLat(Common.latitude);
        rideRequest.setStartLng(Common.longitude);
        rideRequest.updateRideDetailToServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("START_TRIP", "onSuccess: " + response.toString());
                if (response.has("response")) {
                    userSummary.setVisibility(View.GONE);
                    startTrip.setVisibility(View.GONE);
                    startTrip.setEnabled(false);
                    stopTrip.setVisibility(View.VISIBLE);
                } else {
                    Log.d("START_TRIP", "onSuccess: " + response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("START_TRIP", "onSuccess: " + error.toString());
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
        });

    }

    private void callUser() {
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Common.userPhone));
        startActivity(call);
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {

                jsonObject = new JSONObject(strings[0]);
                DirectionJSONDecode directionJSONDecode = new DirectionJSONDecode();
                routes = directionJSONDecode.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);

            ArrayList<LatLng> points;
            PolylineOptions polyOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList<LatLng>();
                polyOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int j = 0; j < path.size(); j++) {

                    HashMap<String, String> point = path.get(j);

                    double dLat = Double.parseDouble(point.get("lat"));
                    double dLng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(dLat, dLng);

                    points.add(position);

                }

                polyOptions.addAll(points);
                polyOptions.width(10);
                polyOptions.color(Color.RED);
                polyOptions.geodesic(true);

            }

            if(polyOptions != null){
                mMap.addPolyline(polyOptions);
            }
        }

    }
}
