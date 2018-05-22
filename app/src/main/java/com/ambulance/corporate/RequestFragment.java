package com.ambulance.corporate;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
public class RequestFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private TextView rName, rMobile, rAddress, timeToR, distanceToR;
    private Button btnAccept, btnDecline;
    private ImageView callR;

    private MapView mMapView;
    private GoogleMap mMap;

    private Context mContext;
    private Activity activity;

    private MediaPlayer mediaPlayer;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mediaPlayer = MediaPlayer.create(mContext,R.raw.ambulance_siren);
        activity.setTitle("Emergency Request");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request, container, false);

        rName = view.findViewById(R.id.requesterName);
        rMobile = view.findViewById(R.id.requesterMobile);
        rAddress = view.findViewById(R.id.requesterAddress);
        timeToR = view.findViewById(R.id.timeToRequester);
        distanceToR = view.findViewById(R.id.distanceToRequester);
        callR = view.findViewById(R.id.callRequester);
        btnAccept = view.findViewById(R.id.btnAccept);
        btnDecline = view.findViewById(R.id.btnDecline);
        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
        callR.setOnClickListener(this);

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        gettingDetailsFromServer();

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("REQ_FRAG", "onAttach");
        mContext = context;
        activity = (Activity) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("REQ_FRAG", "onResume");
        mMapView.onResume();
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("REQ_FRAG", "onPause");
        mMapView.onPause();
        if (mediaPlayer.isLooping()){
            mediaPlayer.setLooping(false);
        }
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("REQ_FRAG", "onDestroy");
        mMapView.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.d("REQ_FRAG", "onLowMemory");
        mMapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.callRequester:
                if (mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                callRequester();
                break;

            case R.id.btnAccept:
                if (mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                acceptBooking();
                break;

            case R.id.btnDecline:
                if (mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }
                declineBooking();
                break;

        }

    }

    private void gettingDetailsFromServer() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.setContext(mContext);
        rideRequest.setDriverId(mContext.getSharedPreferences("account",Context.MODE_PRIVATE).getString("userId",""));
        rideRequest.getRideDetailFromServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {

                if (response.has("response")) {

                    try {
                        String name = response.getJSONObject("response").getString("name");
                        String phone = response.getJSONObject("response").getString("phone");
                        double lat = Double.parseDouble(response.getJSONObject("response").getString("lat"));
                        double lng = Double.parseDouble(response.getJSONObject("response").getString("lng"));

                        rName.setText(name);
                        rMobile.setText(phone);

                        Common.userName = name;
                        Common.userPhone = phone;
                        Common.userLat = lat;
                        Common.userLng = lng;

                        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + Common.latitude + "," + Common.longitude +
                                "&destinations=" + lat + "," + lng + "&mode=driving&language=en-US";

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null
                                , new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    rAddress.setText(response.getJSONArray("destination_addresses").getString(0));
                                    JSONObject jsonObject = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                                    distanceToR.setText(jsonObject.getJSONObject("distance").getString("text"));
                                    timeToR.setText(jsonObject.getJSONObject("duration").getString("text"));

                                    Common.userAddress = rAddress.getText().toString();

                                } catch (JSONException e) {
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
                        VolleySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_blue)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Common.latitude, Common.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mark_red)));

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(Common.latitude, Common.longitude)).include(new LatLng(lat, lng));

                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 100);
                        mMap.moveCamera(cameraUpdate);

                        getDirection(lat, lng);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (response.has("error")) {
                    Log.d("ERROR_RES", response.toString());
                } else if (response.has("invalid")) {
                    Log.d("INVALID_RES", response.toString());
                }

            }

            @Override
            public void onError(VolleyError error) {
                Log.d("ERROR_RE", error.toString());
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

    private void declineBooking() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setContext(mContext);
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.setDriverId(mContext.getSharedPreferences("account",Context.MODE_PRIVATE).getString("userId",""));
        rideRequest.cancelRideAndUpdateServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                Common.emergencyRequestBookingID = "";
                Log.d("ONCANCEL", "onSuccess: "+response.toString());
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("ONCANCEL", "onSuccess: "+error.toString());
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
        startActivity(new Intent(mContext,MainActivity.class));
        activity.finish();
    }

    private void acceptBooking() {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setBookingId(Common.emergencyRequestBookingID);
        rideRequest.acceptRideAndUpdateServer(new VolleyJSONResponses() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("Accept", "onSuccess: "+response.toString());
                if (response.has("response")){
                    EmergencyRequestActivity.fragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right
                                    ,android.R.anim.slide_in_left
                                    ,android.R.anim.slide_out_right)
                            .replace(R.id.emergencyFragmentContainer, new ConfirmRequestFragment())
                            .commit();
                }else{
                    Log.d("Accept", "onSuccess: "+response.toString());
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.d("Accept", "onSuccess: "+error.toString());
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

    private void callRequester() {
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + rMobile.getText().toString()));
        startActivity(call);
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
            }
            );
            VolleySingleton.getInstance(mContext).addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

            ArrayList points;
            PolylineOptions polyOptions = null;

            for (int i = 0; i < lists.size(); i++) {

                points = new ArrayList();
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

            mMap.addPolyline(polyOptions);

        }

    }
}
